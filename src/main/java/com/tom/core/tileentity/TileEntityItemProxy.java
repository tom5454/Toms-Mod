package com.tom.core.tileentity;

import static com.tom.api.energy.EnergyType.HV;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.IGuiTile;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;

public class TileEntityItemProxy extends TileEntityTomsMod implements IEnergyReceiver, ISidedInventory, IGuiTile {
	private EnergyStorage energy = new EnergyStorage(10000000, 10000, 10000);
	private InventoryBasic inv = new InventoryBasic("", false, getSizeInventory());
	public String owner = "";
	public boolean redstone = true;
	/** White List ? */
	public boolean mode = false;
	public boolean isItemMode = true;
	public boolean isLocked = false;
	public boolean useNBT = true;

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		TomsModUtils.loadAllItems(tag.getTagList("Items", 10), inv);
		this.energy.readFromNBT(tag);
		this.owner = tag.getString("owner");
		this.mode = tag.getBoolean("mode");
		this.isItemMode = tag.getBoolean("isItemMode");
		this.isLocked = tag.getBoolean("isLocked");
		this.useNBT = tag.getBoolean("useNBT");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("Items", TomsModUtils.saveAllItems(inv));
		this.energy.writeToNBT(tag);
		tag.setString("owner", this.owner);
		tag.setBoolean("mode", this.mode);
		tag.setBoolean("isItemMode", this.isItemMode);
		tag.setBoolean("isLocked", this.isLocked);
		tag.setBoolean("useNBT", this.useNBT);
		return tag;
	}

	@Override
	public String getName() {
		return "Item Proxy";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int getSizeInventory() {
		return 27 + 4;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return true;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUseable(player, this);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == HV;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return this.canConnectEnergy(from, type) ? this.energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getMaxEnergyStored();
	}

	public boolean isPlayerAccess(EntityPlayer player) {
		return this.isLocked ? player.getName().equals(owner) : true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, EnumFacing arg2) {
		return slot > 26;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack is, EnumFacing arg2) {
		return false;
	}

	@Override
	public void updateEntity() {
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		this.redstone = !(world.isBlockIndirectlyGettingPowered(pos) > 0);
		if (this.redstone && this.energy.getEnergyStored() > 100 && !world.isRemote) {
			TileEntity te = world.getTileEntity(pos.offset(EnumFacing.UP, 2));
			if (te instanceof net.minecraft.util.ITickable) {
				net.minecraft.util.ITickable t = (ITickable) te;
				for (int i = 0;i < 8;i++)
					t.update();
			}
			this.energy.extractEnergy(100, false);
			List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(xCoord - 16, yCoord - 16, zCoord - 16, xCoord + 17, yCoord + 17, zCoord + 17));
			List<EntityItem> entitiyItems = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(xCoord - 16, yCoord - 16, zCoord - 16, xCoord + 17, yCoord + 17, zCoord + 17));
			for (Entity e : entities) {
				if (e instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) e;
					if (this.isLocked && player.getName().equals(owner))
						continue;
					InventoryPlayer inv = player.inventory;
					for (int i = 0;i < inv.getSizeInventory();i++) {
						ItemStack c = inv.getStackInSlot(i);
						if (!c.isEmpty()) {
							boolean found = false;
							// ItemStack item = null;
							int invSlot = this.mode ? i : -1;
							for (int j = 0;j < 27;j++) {
								if (this.inv.getStackInSlot(j).isEmpty() && this.inv.getStackInSlot(j).isItemEqual(c) && ((!this.useNBT) || (this.inv.getStackInSlot(j).getTagCompound() == null && c.getTagCompound() == null) || (this.inv.getStackInSlot(j).getTagCompound() != null && c.getTagCompound() != null && this.inv.getStackInSlot(j).getTagCompound().equals(c.getTagCompound())))) {
									found = true;
									// item = this.stack[j];
									invSlot = i;
									break;
								}
							}
							if (invSlot != -1 && ((found && !this.mode) || (!found && this.mode))) {
								for (int j = 27;j < 31;j++) {
									ItemStack slotStack = this.inv.getStackInSlot(j);
									if (!c.isEmpty()) {
										if (!slotStack.isEmpty()) {
											this.inv.setInventorySlotContents(j, c);
											int ex = c.getCount() + 10;
											double energyEx = this.energy.extractEnergy(ex, true);
											if (energyEx == ex) {
												inv.setInventorySlotContents(invSlot, ItemStack.EMPTY);
												c = ItemStack.EMPTY;
											}
											this.energy.extractEnergy(energyEx, false);
										} else {
											if (slotStack.isItemEqual(c)) {
												int space = Math.min(this.getInventoryStackLimit(), c.getMaxStackSize()) - slotStack.getCount();
												double energyEx = this.energy.extractEnergy(10, true);
												if (energyEx == 10) {
													if (space < c.getCount()) {
														double ex = space + 10;
														energyEx = this.energy.extractEnergy(ex, true);
														// int ext =
														// energyEx-10;
														space = Math.min(Math.min(this.getInventoryStackLimit(), c.getCount()), Math.min(c.getMaxStackSize(), MathHelper.floor(this.energy.getEnergyStored() - 10))) - slotStack.getCount();
														slotStack.setCount(Math.min(this.getInventoryStackLimit(), Math.min(c.getMaxStackSize(), MathHelper.floor(this.energy.getEnergyStored() - 10))));
														c.shrink(MathHelper.floor(space));
														energyEx = space + 10;
														if (c.getCount() < 1) {
															inv.setInventorySlotContents(invSlot, ItemStack.EMPTY);
															c = ItemStack.EMPTY;
														}
													} else {
														int ex = c.getCount() + 10;
														energyEx = this.energy.extractEnergy(ex, true);
														// int ext =
														// energyEx-10;
														space = Math.min(Math.min(this.getInventoryStackLimit(), c.getCount()), Math.min(c.getMaxStackSize(), MathHelper.floor(this.energy.getEnergyStored() - 10)));
														slotStack.grow(space);
														c.shrink(space);
														energyEx = space + 10;
														if (c.getCount() < 1) {
															inv.setInventorySlotContents(invSlot, ItemStack.EMPTY);
															c = ItemStack.EMPTY;
														}
													}
													this.energy.extractEnergy(energyEx, false);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if (this.isItemMode) {
				for (Entity e : entitiyItems) {
					if (e instanceof EntityItem) {
						ItemStack c = ((EntityItem) e).getItem();
						if (c.isEmpty()) {
							boolean found = false;
							// ItemStack item = null;
							for (int j = 0;j < 27;j++) {
								if (!this.inv.getStackInSlot(j).isEmpty() && this.inv.getStackInSlot(j).isItemEqual(c) && ((!this.useNBT) || ((this.inv.getStackInSlot(j).getTagCompound() == null && c.getTagCompound() == null) || (this.inv.getStackInSlot(j).getTagCompound() != null && c.getTagCompound() != null && this.inv.getStackInSlot(j).getTagCompound().equals(c.getTagCompound()))))) {
									found = true;
									// item = this.stack[j];
									break;
								}
							}
							if ((found && !this.mode) || (!found && this.mode)) {
								for (int j = 27;j < 31;j++) {
									ItemStack slotStack = this.inv.getStackInSlot(j);
									if (c != null) {
										if (slotStack == null) {
											this.inv.setInventorySlotContents(j, c);
											int ex = c.getCount() + 10;
											double energyEx = this.energy.extractEnergy(ex, true);
											if (energyEx == ex) {
												e.setDead();
												c = ItemStack.EMPTY;
											}
											this.energy.extractEnergy(energyEx, false);
										} else {
											if (slotStack.isItemEqual(c)) {
												int space = Math.min(this.getInventoryStackLimit(), c.getMaxStackSize()) - slotStack.getCount();
												double energyEx = this.energy.extractEnergy(10, true);
												if (energyEx == 10) {
													if (space < c.getCount()) {
														double ex = space + 10;
														energyEx = this.energy.extractEnergy(ex, true);
														// int ext =
														// energyEx-10;
														space = Math.min(Math.min(this.getInventoryStackLimit(), c.getCount()), Math.min(c.getMaxStackSize(), MathHelper.floor(this.energy.getEnergyStored() - 10))) - slotStack.getCount();
														slotStack.setCount(Math.min(this.getInventoryStackLimit(), Math.min(c.getMaxStackSize(), MathHelper.floor(this.energy.getEnergyStored() - 10))));
														c.shrink(space);
														energyEx = space + 10;
														if (c.getCount() < 1) {
															e.setDead();
															c = ItemStack.EMPTY;
														}
													} else {
														int ex = c.getCount() + 10;
														energyEx = this.energy.extractEnergy(ex, true);
														// int ext =
														// energyEx-10;
														space = Math.min(Math.min(this.getInventoryStackLimit(), c.getCount()), Math.min(c.getMaxStackSize(), MathHelper.floor(this.energy.getEnergyStored() - 10)));
														slotStack.grow(space);
														c.shrink(space);
														energyEx = space + 10;
														if (c.getCount() < 1) {
															e.setDead();
															c = ItemStack.EMPTY;
														}
													}
													this.energy.extractEnergy(energyEx, false);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void writeToPacket(NBTTagCompound buf) {
		buf.setString("n", owner);
	}

	@Override
	public void readFromPacket(NBTTagCompound buf) {
		this.owner = buf.getString("n");
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if (this.isLocked && !player.getName().equals(this.owner))
			return;
		if (id == 0)
			this.mode = !this.mode;
		else if (id == 1)
			this.isItemMode = !this.isItemMode;
		else if (id == 2)
			this.isLocked = !this.isLocked;
		else if (id == 3)
			this.useNBT = !this.useNBT;
		markBlockForUpdate(pos);
	}

	@Override
	public void closeInventory(EntityPlayer arg0) {

	}

	@Override
	public int getField(int arg0) {
		return 0;
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void openInventory(EntityPlayer arg0) {

	}

	@Override
	public void setField(int arg0, int arg1) {

	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing arg0) {
		return new int[]{27, 28, 29, 30};
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return HV.getList();
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
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public void clear() {
		inv.clear();
	}
}
