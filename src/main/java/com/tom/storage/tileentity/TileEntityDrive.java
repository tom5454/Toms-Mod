package com.tom.storage.tileentity;

import java.util.stream.Stream;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.tom.api.grid.GridEnergyStorage;
import com.tom.api.inventory.IStorageInventory;
import com.tom.api.inventory.IStorageInventory.IUpdateable;
import com.tom.api.item.IStorageCell;
import com.tom.api.tileentity.IConfigurable;
import com.tom.api.tileentity.ISecuredTileEntity;
import com.tom.client.ICustomModelledTileEntity;
import com.tom.config.ConfigurationOptionDrive;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.storage.block.Drive;
import com.tom.storage.handler.StorageNetworkGrid.IChannelLoadListener;
import com.tom.util.TomsModUtils;

public class TileEntityDrive extends TileEntityChannel implements IInventory, ICustomModelledTileEntity, IChannelLoadListener, IConfigurable, ISecuredTileEntity {
	public InventoryBasic inv = new InventoryBasic("drive", false, 11);
	private int i = 0;
	private boolean ledOn = false;
	private int[] bootTime = new int[]{-3, -3, -3, -3, -3, -3, -3, -3, -3, -3};
	private byte[] clientLeds = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	private IStorageInventory[] dataList = new IStorageInventory[10];
	private int[] priorities = new int[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
	private GridEnergyStorage energy = new GridEnergyStorage(100, 0);
	private double drain;
	private int priority = 0;
	private byte connections = 0;
	private IConfigurationOption cfg = new ConfigurationOptionDrive(this);

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			// grid.getData().addInventory(inv);
			grid.getData().addEnergyStorage(energy);
			drain = 1;
			if (updateCells())
				markBlockForUpdate(pos);
		} else {
			i++;
			if (i == 8) {
				i = 0;
				ledOn = !ledOn;
			}
		}
	}

	private boolean updateCells() {
		boolean needsSync = false;
		for (int i = 0;i < 10;i++) {
			world.profiler.startSection("update:" + i);
			if (!inv.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i).getItem() instanceof IStorageCell && ((IStorageCell) inv.getStackInSlot(i).getItem()).isValid(inv.getStackInSlot(i), grid)) {
				if (isActive().fullyActive()) {
					world.profiler.startSection("bootTime");
					if (bootTime[i] != 0) {
						if (bootTime[i] > 0) {
							bootTime[i]--;
							if (bootTime[i] == 0) {
								needsSync = true;
								dataList[i] = ((IStorageCell) inv.getStackInSlot(i).getItem()).getData(inv.getStackInSlot(i), world, pos, priority * 10 + priorities[i], grid);
								clientLeds[i] = (byte) (((IStorageCell) inv.getStackInSlot(i).getItem()).getLightState(dataList[i]).ordinal() + 1);
							}
						} else {
							bootTime[i] = ((IStorageCell) inv.getStackInSlot(i).getItem()).getBootTime(inv.getStackInSlot(i), world, pos, grid);
							needsSync = true;
							clientLeds[i] = 0;
						}
					} else {
						if (dataList[i] == null) {
							needsSync = true;
							dataList[i] = ((IStorageCell) inv.getStackInSlot(i).getItem()).getData(inv.getStackInSlot(i), world, pos, priority * 10 + priorities[i], grid);
						}
						byte old = clientLeds[i];
						clientLeds[i] = (byte) (((IStorageCell) inv.getStackInSlot(i).getItem()).getLightState(dataList[i]).ordinal() + 1);
						if (old != clientLeds[i])
							needsSync = true;
					}
					world.profiler.endStartSection("handlePower");
					drain += (((IStorageCell) inv.getStackInSlot(i).getItem()).getPowerDrain(inv.getStackInSlot(i), world, pos, grid) / 10D);
					world.profiler.endStartSection("update");
					if (dataList[i] != null) {
						world.profiler.startSection("addToList");
						grid.getData().addInventory(dataList[i]);
						world.profiler.endStartSection("updateData");
						((IUpdateable) dataList[i]).update(inv.getStackInSlot(i), null, priority * 10 + priorities[i]);
						world.profiler.endSection();
					}
					world.profiler.endSection();
				} else {
					int oldBootTime = bootTime[i];
					bootTime[i] = -1;
					if (oldBootTime != -1) {
						clientLeds[i] = -1;
						needsSync = true;
					}
					if (dataList[i] != null) {
						grid.getData().removeInventory(dataList[i]);
						dataList[i] = null;
					}
				}
			} else {
				int oldBootTime = bootTime[i];
				bootTime[i] = -2;
				if (oldBootTime != -2) {
					clientLeds[i] = -2;
					needsSync = true;
					if (dataList[i] != null) {
						grid.getData().removeInventory(dataList[i]);
						dataList[i] = null;
					}
				}
			}
			world.profiler.endSection();
		}
		return needsSync;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		// inv.setInventorySlotContents(0,
		// ItemStack.loadItemStackFromNBT(compound.getCompoundTag("storedTag")));
		energy.readFromNBT(compound);
		TomsModUtils.loadAllItems(compound.getTagList("inventory", 10), inv);
		priority = compound.getInteger("priority");
		priorities = compound.getIntArray("priorities");
		if (priorities.length != 10)
			priorities = new int[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
		connections = compound.getByte("connections");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		// NBTTagCompound t = new NBTTagCompound();
		// if(inv.getStackInSlot(0) != null)inv.getStackInSlot(0).writeToNBT(t);
		// compound.setTag("storedTag", t);
		energy.writeToNBT(compound);
		Stream.of(dataList).filter(v -> v != null).forEach(IStorageInventory::save);
		compound.setTag("inventory", TomsModUtils.saveAllItems(inv));
		compound.setInteger("priority", priority);
		compound.setIntArray("priorities", priorities);
		compound.setByte("connections", connections);
		return compound;
	}

	@Override
	public void writeToPacket(NBTTagCompound buf) {
		buf.setByteArray("l", clientLeds);
	}

	@Override
	public void readFromPacket(NBTTagCompound buf) {
		clientLeds = buf.getByteArray("l");
	}

	@Override
	public String getName() {
		return "drive";
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
		return 10;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
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
		return !stack.isEmpty() && stack.getItem() instanceof IStorageCell && ((IStorageCell) stack.getItem()).isValid(stack, grid);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public EnumFacing getFacing() {
		IBlockState state = world.getBlockState(pos);
		return state.getValue(Drive.FACING);
	}

	public boolean onBlockActivated(EntityPlayer player, ItemStack held) {
		if (!world.isRemote) {
			if (held != null && held.getItem() instanceof IStorageCell && ((IStorageCell) held.getItem()).isValid(held, grid)) {
				for (int i = 0;i < 10;i++) {
					if (this.inv.getStackInSlot(i).isEmpty()) {
						this.inv.setInventorySlotContents(i, held.splitStack(1));
						break;
					}
				}
			} else {
				player.openGui(CoreInit.modInstance, GuiIDs.drive.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	/**
	 * 0:No Cell, 1:Cell Inactive, 2:Cell Green, 3:Cell Orange, 4:Cell Red,
	 * 5:Cell Booting
	 */
	public int getDriveColor(int slot) {
		if (slot < 10) {
			if (clientLeds[slot] == -2)
				return 0;
			if (clientLeds[slot] == -1)
				return 1;
			if (clientLeds[slot] == 0)
				return ledOn ? 5 : 1;
			if (clientLeds[slot] == 1)
				return 2;
			if (clientLeds[slot] == 2)
				return 3;
			if (clientLeds[slot] == 3)
				return 4;
		}
		return 0;
	}

	@Override
	public void markDirty() {
		for (int i = 0;i < 10;i++) {
			if (dataList[i] != null) {
				grid.getData().removeInventory(dataList[i]);
				dataList[i].saveIfNeeded();
				dataList[i] = null;
			}
		}
		updateCells();
		super.markDirty();
	}

	@Override
	public void onGridReload() {

	}

	@Override
	public void onGridPostReload() {
		updateEntity();
	}

	public boolean writeToStackNBT(NBTTagCompound compound, boolean writeInv) {
		boolean ret = false;
		if (writeInv) {
			compound.setTag("inventory", TomsModUtils.saveAllItems(inv));
			inv.clear();
		}
		energy.writeToNBT(compound);
		compound.setInteger("priority", priority);
		compound.setIntArray("priorities", priorities);
		compound.setByte("connections", connections);
		return ret;
	}

	@Override
	public double getPowerDrained() {
		return drain;
	}

	@Override
	public int getPriority() {
		return 5;
	}

	@Override
	public void invalidateGrid() {
		for (int i = 0;i < 10;i++) {
			if (dataList[i] != null) {
				grid.getData().removeInventory(dataList[i]);
			}
		}
		grid.getData().removeEnergyStorage(energy);
		super.invalidateGrid();
	}

	@Override
	public void onPartsUpdate() {
		updateEntity();
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

	@Override
	public void receiveNBTPacket(NBTTagCompound message) {
		connections = message.getByte("s");
		priority = message.getInteger("p");
		markDirty();
	}

	@Override
	public void writeToNBTPacket(NBTTagCompound tag) {
		tag.setByte("s", connections);
		tag.setInteger("p", priority);
	}

	@Override
	public IConfigurationOption getOption() {
		return cfg;
	}

	@Override
	public boolean canConfigure(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public BlockPos getSecurityStationPos() {
		return IConfigurable.super.getSecurityStationPos();
	}

	@Override
	public void setCardStack(ItemStack stack) {
		inv.setInventorySlotContents(11, stack);
	}

	@Override
	public ItemStack getCardStack() {
		return inv.getStackInSlot(11);
	}

	@Override
	public boolean canConnectTo(EnumFacing side) {
		return (connections & (1 << side.ordinal())) == 0;
	}

	@Override
	public String getConfigName() {
		return "tile.tms.drive.name";
	}
}
