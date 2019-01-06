package com.tom.defense.tileentity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.common.UsernameCache;

import com.tom.api.block.IItemTile;
import com.tom.api.item.IIdentityCard;
import com.tom.api.item.IPowerLinkCard;
import com.tom.api.item.ISecurityStationLinkCard;
import com.tom.api.item.ISwitch;
import com.tom.api.tileentity.AccessType;
import com.tom.api.tileentity.IForceDevice;
import com.tom.api.tileentity.IForcePowerStation;
import com.tom.api.tileentity.ISecurityStation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.core.CoreInit;
import com.tom.defense.DefenseInit;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.block.ForceCapacitor;
import com.tom.defense.item.IdentityCard;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.util.TomsModUtils;

public class TileEntitySecurityStation extends TileEntityTomsMod implements IForceDevice, ISecurityStation, ISidedInventory, IItemTile {
	private InventoryBasic inv = new InventoryBasic("", false, getSizeInventory());
	public ForceDeviceControlType rsMode = ForceDeviceControlType.LOW_REDSTONE;
	private EnergyStorage energy = new EnergyStorage(10000, 1000);
	public boolean active = false;
	private boolean firstStart = true;
	public int clientEnergy = 0, rightListClient = 0;
	private Map<String, List<AccessType>> accessMap = new HashMap<>();

	@Override
	public String getName() {
		return "securityStation";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	@Override
	public int getSizeInventory() {
		return 41;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUsable(pos, player, world, this);
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

	@Override
	public int getField(int id) {
		return id == 0 ? this.clientEnergy : id == 1 ? this.rightListClient : 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0)
			this.clientEnergy = value;
		if (id == 1)
			this.rightListClient = value;
	}

	@Override
	public int getFieldCount() {
		return 2;
	}

	@Override
	public boolean canPlayerAccess(AccessType type, EntityPlayer player) {
		if (this.active && this.energy.getEnergyStored() > 0.1D) {
			ItemStack is = this.getStackInSlot(0);
			if (!is.isEmpty() && is.getItem() instanceof IIdentityCard && ((IIdentityCard) is.getItem()).getUsername(is) != null) {
				if (this.accessMap.containsKey(player.getName())) {
					List<AccessType> rights = this.accessMap.get(player.getName());
					return rights != null ? rights.contains(type) : false;
				}
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canPlayerAccess(AccessType type, UUID ownerId) {
		if (this.active && this.energy.getEnergyStored() > 0.1D) {
			ItemStack is = this.getStackInSlot(0);
			if (!is.isEmpty() && is.getItem() instanceof IIdentityCard && ((IIdentityCard) is.getItem()).getUsername(is) != null) {
				String name = UsernameCache.getLastKnownUsername(ownerId);
				if (name != null && this.accessMap.containsKey(name)) {
					List<AccessType> rights = this.accessMap.get(name);
					return rights != null ? rights.contains(type) : false;
				}
				return false;
			}
		}
		return true;
	}

	@Override
	public double receiveEnergy(double maxReceive, boolean simulate) {
		return energy.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public void updateEntity(IBlockState currentState) {
		if (!world.isRemote) {
			if (this.firstStart) {
				BlockPos pos = this.getCapacitorPos();
				if (pos != null) {
					TileEntity tile = world.getTileEntity(pos);
					if (tile instanceof IForcePowerStation) {
						IForcePowerStation te = (IForcePowerStation) tile;
						te.registerDevice(this);
					}
				}
			}
			if (rsMode == ForceDeviceControlType.HIGH_REDSTONE) {
				this.active = world.isBlockIndirectlyGettingPowered(pos) > 0;
			} else if (rsMode == ForceDeviceControlType.LOW_REDSTONE) {
				this.active = world.isBlockIndirectlyGettingPowered(pos) == 0;
			} else if (rsMode == ForceDeviceControlType.IGNORE) {
				this.active = true;
			}
			this.clientEnergy = MathHelper.floor(this.energy.getEnergyStored());
			TomsModUtils.setBlockStateWithCondition(world, pos, currentState, ForceCapacitor.ACTIVE, this.active && this.energy.getEnergyStored() > 0.1D);
			if (this.active && this.energy.getEnergyStored() > 0.1D) {
				energy.extractEnergy(0.01, false);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound);
		TomsModUtils.loadAllItems(compound.getTagList("inventory", 10), inv);
		rsMode = ForceDeviceControlType.get(compound.getInteger("redstone_mode"));
		this.active = compound.getBoolean("active");
		this.updateAccessMap();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		energy.writeToNBT(compound);
		compound.setTag("inventory", TomsModUtils.saveAllItems(inv));
		compound.setInteger("redstone_mode", rsMode.ordinal());
		compound.setBoolean("active", active);
		return compound;
	}

	public void writeToStackNBT(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for (int i = 0;i < getSizeInventory();i++) {
			if (i != 2 && i != 3 && i != 4) {
				NBTTagCompound tag = new NBTTagCompound();
				inv.getStackInSlot(i).writeToNBT(tag);
				tag.setByte("Slot", (byte) i);
				list.appendTag(tag);
			}
		}
		compound.setTag("inventory", list);
		compound.setInteger("redstone_mode", rsMode.ordinal());
	}

	public boolean onBlockActivated(EntityPlayer player, ItemStack held) {
		if (!world.isRemote) {
			if (player.capabilities.isCreativeMode)
				player.openGui(CoreInit.modInstance, GuiIDs.securityStation.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());// Debug
			if (held != null && held.getItem() instanceof ISwitch && ((ISwitch) held.getItem()).isSwitch(held, player)) {
				if (rsMode == ForceDeviceControlType.SWITCH) {
					if (this.canPlayerAccess(AccessType.RIGHTS_MODIFICATION, player)) {
						this.active = !this.active;
						return true;
					} else {
						TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
						return false;
					}
				} else {
					TomsModUtils.sendNoSpamTranslate(player, new Style().setColor(TextFormatting.RED), "tomsMod.chat.mnotSwitchable", new TextComponentTranslation(held.getUnlocalizedName() + ".name"));
					return false;
				}
			} else if (held != null && held.getItem() instanceof ISecurityStationLinkCard && ((ISecurityStationLinkCard) held.getItem()).isEmpty(held)) {
				if (this.canPlayerAccess(AccessType.CONFIGURATION, player)) {
					((ISecurityStationLinkCard) held.getItem()).setStation(held, pos);
					return true;
				} else {
					TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
					return false;
				}
			} else {
				if (this.canPlayerAccess(AccessType.RIGHTS_MODIFICATION, player)) {
					player.openGui(CoreInit.modInstance, GuiIDs.securityStation.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
				} else {
					TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
					return false;
				}
				return true;
			}
		} else {
			return true;
		}
	}

	@Override
	public boolean isValid(BlockPos from) {
		BlockPos c = this.getCapacitorPos();
		return this.hasWorld() && c != null && c.equals(from) && pos != null && world.getBlockState(pos) != null && world.getBlockState(pos).getBlock() == DefenseInit.securityStation;
	}

	public BlockPos getCapacitorPos() {
		return !inv.getStackInSlot(1).isEmpty() && inv.getStackInSlot(1).getItem() instanceof IPowerLinkCard ? ((IPowerLinkCard) inv.getStackInSlot(1).getItem()).getMaster(inv.getStackInSlot(1)) : null;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.updateAccessMap();
	}

	private void updateAccessMap() {
		this.accessMap.clear();
		{
			ItemStack is = this.getStackInSlot(0);
			if (is != null && is.getItem() instanceof IIdentityCard && ((IIdentityCard) is.getItem()).getUsername(is) != null) {
				IIdentityCard card = (IIdentityCard) is.getItem();
				String name = card.getUsername(is);
				this.accessMap.put(name, AccessType.getFullList());
			}
		}
		for (int i = 4;i < getSizeInventory();i++) {
			ItemStack is = this.getStackInSlot(i);
			if (!is.isEmpty() && is.getItem() instanceof IIdentityCard && ((IIdentityCard) is.getItem()).getUsername(is) != null) {
				IIdentityCard card = (IIdentityCard) is.getItem();
				String name = card.getUsername(is);
				if (this.accessMap.containsKey(name))
					this.accessMap.get(name).addAll(card.getRights(is));
				else
					this.accessMap.put(name, card.getRights(is));
			}
		}
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if (id == 8) {
			rsMode = ForceDeviceControlType.get(extra);
		} else if (id == 0) {
			if (this.energy.getEnergyStored() > 0.1D) {
				ItemStack is = this.getStackInSlot(3);
				ItemStack isN = this.getStackInSlot(4);
				if (!is.isEmpty() && is.getItem() instanceof IIdentityCard && ((IIdentityCard) is.getItem()).getUsername(is) != null && isN != null && isN.getItem() instanceof IIdentityCard && ((IIdentityCard) isN.getItem()).isEmpty(isN)) {
					IIdentityCard c = (IIdentityCard) is.getItem();
					IIdentityCard cN = (IIdentityCard) isN.getItem();
					cN.setUsername(isN, c.getUsername(is));
					cN.setRights(isN, c.getRights(is));
					energy.extractEnergy(0.1 * isN.getCount(), false);
				}
			}
		} else {
			if (id > 0 && id < 8) {
				AccessType button = AccessType.get(id - 1);
				ItemStack is = this.getStackInSlot(2);
				if (!is.isEmpty() && is.getItem() instanceof IIdentityCard && ((IIdentityCard) is.getItem()).getUsername(is) != null) {
					IIdentityCard card = (IIdentityCard) is.getItem();
					List<AccessType> rights = card.getRights(is);
					if (rights.contains(button))
						rights.remove(button);
					else
						rights.add(button);
					card.setRights(is, rights);
				}
			}
		}
	}

	public long getMaxEnergyStored() {
		return this.energy.getMaxEnergyStored();
	}

	public int getCompiledRightsFromEditingCard() {
		ItemStack is = this.getStackInSlot(2);
		if (!is.isEmpty() && is.getItem() instanceof IIdentityCard && ((IIdentityCard) is.getItem()).getUsername(is) != null) {
			IIdentityCard card = (IIdentityCard) is.getItem();
			return IdentityCard.compileRights(card.getRights(is));
		}
		return 0;
	}

	@Override
	public BlockPos getPos2() {
		return pos;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[]{};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public BlockPos getSecurityStationPos() {
		return pos;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack stack = new ItemStack(state.getBlock());
		NBTTagCompound tag = new NBTTagCompound();
		writeToStackNBT(tag);
		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setTag("BlockEntityTag", tag);
		stack.getTagCompound().setBoolean("stored", true);
		drops.add(stack);
	}
}
