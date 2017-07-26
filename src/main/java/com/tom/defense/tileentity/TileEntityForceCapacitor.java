package com.tom.defense.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.IEnergyStorage;
import com.tom.api.item.IPowerLinkCard;
import com.tom.api.item.ISecurityStationLinkCard;
import com.tom.api.item.ISwitch;
import com.tom.api.tileentity.AccessType;
import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.IForceDevice;
import com.tom.api.tileentity.IForcePowerStation;
import com.tom.api.tileentity.IGuiTile;
import com.tom.api.tileentity.ISecurityStation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.config.ConfigurationForceCapacitorControl;
import com.tom.core.CoreInit;
import com.tom.defense.DefenseInit;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.block.ForceCapacitor;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.lib.Configs;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityForceCapacitor extends TileEntityTomsMod implements IPeripheral, ISidedInventory, IForcePowerStation, IGuiTile, IConfigurable {
	private static final int DEFAULT_RANGE = 2;
	private static final int RANGE_UPGRADE_INCREASE = 2;
	private EnergyStorage energy = new EnergyStorage(10000000, 100000);
	public int clientPer = 0;
	public int linkedDevices = -1;
	public int range = DEFAULT_RANGE;
	public InventoryBasic inv = new InventoryBasic("", false, getSizeInventory());
	private List<IForceDevice> deviceList = new ArrayList<>();
	public boolean active = false;
	public ForceDeviceControlType rsMode = ForceDeviceControlType.LOW_REDSTONE;
	public int clientEnergy = 0;
	private ConfigurationForceCapacitorControl cfgOption = new ConfigurationForceCapacitorControl(this);
	private boolean powersharing = false;

	/*@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == FORCE && from == this.getFacing(world.getBlockState(pos)).rotateY();
	}
	
	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return FORCE.getList();
	}*/
	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public String getType() {
		return "force_capacitor";
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public String[] getMethodNames() {
		return new String[]{"getEnergyStored", "getMaxEnergyStored"};
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		if (method == 0) {
			return new Object[]{energy.getEnergyStored()};
		} else if (method == 1) { return new Object[]{energy.getMaxEnergyStored()}; }
		return null;
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public void attach(IComputerAccess computer) {

	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public void detach(IComputerAccess computer) {

	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public boolean equals(IPeripheral other) {
		return other == this;
	}

	/*@Override
	public double receiveEnergy(EnumFacing from, EnergyType type,
			double maxReceive, boolean simulate) {
		return this.active && this.canConnectEnergy(from, type) ? energy.receiveEnergy(maxReceive, simulate) : 0;
	}
	
	@Override
	public double extractEnergy(EnumFacing from, EnergyType type,
			double maxExtract, boolean simulate) {
		return this.active && this.canConnectEnergy(from, type) ? energy.extractEnergy(maxExtract, simulate) : 0;
	}
	
	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getEnergyStored();
	}
	
	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getMaxEnergyStored();
	}*/
	public EnumFacing getFacing(IBlockState state) {
		return state.getBlock() == DefenseInit.forceCapacitor ? state.getValue(ForceCapacitor.FACING) : EnumFacing.NORTH;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		energy.writeToNBT(compound);
		compound.setTag("inventory", TomsModUtils.saveAllItems(inv));
		compound.setInteger("redstone_mode", rsMode.ordinal());
		compound.setBoolean("active", active);
		compound.setBoolean("powersharing", powersharing);
		return compound;
	}

	public void writeToStackNBT(NBTTagCompound compound) {
		energy.writeToNBT(compound);
		compound.setTag("inventory", TomsModUtils.saveAllItems(inv));
		compound.setInteger("redstone_mode", rsMode.ordinal());
		compound.setBoolean("powersharing", powersharing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound);
		TomsModUtils.loadAllItems(compound.getTagList("inventory", 10), inv);
		rsMode = ForceDeviceControlType.get(compound.getInteger("redstone_mode"));
		this.active = compound.getBoolean("active");
		this.powersharing = compound.getBoolean("powersharing");
	}

	@Override
	public void writeToPacket(NBTTagCompound buf) {
		buf.setInteger("c", clientPer);
		buf.setInteger("d", this.deviceList.size());
		buf.setInteger("r", range);
	}

	@Override
	public void readFromPacket(NBTTagCompound buf) {
		this.clientPer = buf.getInteger("c");
		this.linkedDevices = buf.getInteger("d");
		this.range = buf.getInteger("r");
	}

	@Override
	public void updateEntity(IBlockState currentState) {
		if (!world.isRemote) {
			if (rsMode == ForceDeviceControlType.HIGH_REDSTONE) {
				this.active = world.isBlockIndirectlyGettingPowered(pos) > 0;
			} else if (rsMode == ForceDeviceControlType.LOW_REDSTONE) {
				this.active = world.isBlockIndirectlyGettingPowered(pos) == 0;
			} else if (rsMode == ForceDeviceControlType.IGNORE) {
				this.active = true;
			}
			this.clientEnergy = MathHelper.floor(this.energy.getEnergyStored());
			TomsModUtils.setBlockStateWithCondition(world, pos, currentState, ForceCapacitor.ACTIVE, this.active);
			if (this.active) {
				for (int i = 0;i < deviceList.size();i++) {
					IForceDevice d = deviceList.get(i);
					if (d != null) {
						if (d.isValid(pos)) {
							if (MathHelper.sqrt(d.getPos2().distanceSq(pos)) <= range) {
								double receive = d.receiveEnergy(Math.min(10000, energy.getEnergyStored()), true);
								if (receive > 0) {
									d.receiveEnergy(energy.extractEnergy(receive, false), false);
								}
							}
						} else {
							deviceList.remove(d);
						}
					}
				}
				BlockPos linked = !inv.getStackInSlot(2).isEmpty() && inv.getStackInSlot(2).getItem() instanceof IPowerLinkCard ? ((IPowerLinkCard) inv.getStackInSlot(2).getItem()).getMaster(inv.getStackInSlot(2)) : null;
				if (linked != null) {
					TileEntity tile = world.getTileEntity(linked);
					if (tile instanceof IForcePowerStation) {
						IForcePowerStation te = (IForcePowerStation) tile;
						IEnergyStorage energy = te.getEnergyHandler(pos);
						if (!energy.isDummy() && te.isActive() && MathHelper.sqrt(te.getPos2().distanceSq(pos)) <= range) {
							if (powersharing) {
								EnergyStorage.transfer(energy, this.energy, 10000, false);
							} else {
								long avr = MathHelper.lfloor((energy.getEnergyStored() + this.energy.getEnergyStored()) / 2);
								double c = avr - energy.getEnergyStored();
								if (c < 0)
									EnergyStorage.transfer(energy, this.energy, -c, false);
								else if (c > 0)
									EnergyStorage.transfer(this.energy, energy, c, false);
							}
						}
					}
				}
			}
			int per = MathHelper.floor(energy.getEnergyStored() / energy.getMaxEnergyStored() * 1000);
			boolean update = false;
			if (per != clientPer) {
				clientPer = per;
				update = true;
			}
			if (linkedDevices != deviceList.size()) {
				linkedDevices = deviceList.size();
				update = true;
			}
			if (!inv.getStackInSlot(1).isEmpty() && inv.getStackInSlot(1).getItem() == DefenseInit.rangeUpgrade) {
				int r = inv.getStackInSlot(1).getCount() * RANGE_UPGRADE_INCREASE + DEFAULT_RANGE;
				if (r != range) {
					range = r;
					update = true;
				}
			} else {
				if (range != DEFAULT_RANGE) {
					range = DEFAULT_RANGE;
					update = true;
				}
			}
			if (update)
				markBlockForUpdate(pos);
		}
	}

	@Override
	public String getName() {
		return "forceCapacitor";
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
		return 3;
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
		return true;
	}

	@Override
	public int getField(int id) {
		return id == 0 ? this.linkedDevices : id == 1 ? this.range : id == 2 ? this.clientEnergy : 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0)
			this.linkedDevices = value;
		else if (id == 1)
			this.range = value;
		else if (id == 2)
			this.clientEnergy = value;
	}

	@Override
	public int getFieldCount() {
		return 3;
	}

	@Override
	public BlockPos getSecurityStationPos() {
		return !inv.getStackInSlot(0).isEmpty() && inv.getStackInSlot(0).getItem() instanceof ISecurityStationLinkCard ? ((ISecurityStationLinkCard) inv.getStackInSlot(0).getItem()).getStation(inv.getStackInSlot(0)) : null;
	}

	@Override
	public void registerDevice(IForceDevice device) {
		if (MathHelper.sqrt(device.getPos2().distanceSq(pos)) <= range && !deviceList.contains(device)) {
			deviceList.add(device);
		}
	}

	public boolean onBlockActivated(EntityPlayer player, ItemStack held) {
		if (!world.isRemote) {
			if (!held.isEmpty() && held.getItem() instanceof ISwitch && ((ISwitch) held.getItem()).isSwitch(held, player)) {
				if (rsMode == ForceDeviceControlType.SWITCH) {
					boolean canAccess = true;
					BlockPos securityStationPos = this.getSecurityStationPos();
					if (securityStationPos != null) {
						TileEntity tileentity = world.getTileEntity(securityStationPos);
						if (tileentity instanceof ISecurityStation) {
							ISecurityStation tile = (ISecurityStation) tileentity;
							canAccess = tile.canPlayerAccess(AccessType.SWITCH_DEVICES, player);
						}
					}
					if (canAccess) {
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
			} else if (!held.isEmpty() && held.getItem() instanceof IPowerLinkCard && ((IPowerLinkCard) held.getItem()).isEmpty(held)) {
				boolean canAccess = true;
				BlockPos securityStationPos = this.getSecurityStationPos();
				if (securityStationPos != null) {
					TileEntity tileentity = world.getTileEntity(securityStationPos);
					if (tileentity instanceof ISecurityStation) {
						ISecurityStation tile = (ISecurityStation) tileentity;
						canAccess = tile.canPlayerAccess(AccessType.CONFIGURATION, player);
					}
				}
				if (canAccess) {
					((IPowerLinkCard) held.getItem()).setMaster(held, pos);
					return true;
				} else {
					TomsModUtils.sendAccessDeniedMessageTo(player, "tomsMod.chat.fieldSecurity");
					return false;
				}
			} else {
				boolean canAccess = true;
				BlockPos securityStationPos = this.getSecurityStationPos();
				if (securityStationPos != null) {
					TileEntity tileentity = world.getTileEntity(securityStationPos);
					if (tileentity instanceof ISecurityStation) {
						ISecurityStation tile = (ISecurityStation) tileentity;
						canAccess = tile.canPlayerAccess(AccessType.CONFIGURATION, player);
					}
				}
				if (canAccess) {
					player.openGui(CoreInit.modInstance, GuiIDs.forceCapacitor.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
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
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if (id == 0) {
			rsMode = ForceDeviceControlType.get(extra);
		}
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
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public IEnergyStorage getEnergyHandler(BlockPos from) {
		if (MathHelper.sqrt(from.distanceSq(pos)) <= range) { return energy; }
		return EnergyStorage.DUMMY_STORAGE;
	}

	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		this.rsMode = ForceDeviceControlType.get(message.getInteger("r"));
		this.powersharing = message.getBoolean("s");
	}

	@Override
	public void writeToNBTPacket(NBTTagCompound tag) {
		tag.setInteger("r", this.rsMode.ordinal());
		tag.setBoolean("s", powersharing);
	}

	@Override
	public IConfigurationOption getOption() {
		return cfgOption;
	}

	@Override
	public boolean canConfigure(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public BlockPos getPos2() {
		return pos;
	}

	@Override
	public void setCardStack(ItemStack stack) {
		inv.setInventorySlotContents(0, stack);
	}

	@Override
	public ItemStack getCardStack() {
		return inv.getStackInSlot(0);
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public String getConfigName() {
		return "tile.tm.forceCapacitor.name";
	}
}
