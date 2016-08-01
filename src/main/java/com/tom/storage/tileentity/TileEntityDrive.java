package com.tom.storage.tileentity;

import com.tom.api.grid.GridEnergyStorage;
import com.tom.api.grid.IGridUpdateListener;
import com.tom.api.item.IStorageCell;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.apis.TomsModUtils;
import com.tom.client.ICustomModelledTileEntity;
import com.tom.core.CoreInit;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.storage.block.Drive;
import com.tom.storage.multipart.StorageNetworkGrid;
import com.tom.storage.multipart.StorageNetworkGrid.IStorageData;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class TileEntityDrive extends TileEntityGridDeviceBase<StorageNetworkGrid> implements IInventory, ICustomModelledTileEntity, IGridUpdateListener{
	private ItemStack[] stack = new ItemStack[this.getSizeInventory()];
	private int i = 0;
	private boolean ledOn = false;
	private int[] bootTime = new int[]{-3,-3,-3,-3,-3,-3,-3,-3,-3,-3};
	private byte[] clientLeds = new byte[]{0,0,0,0,0,0,0,0,0,0};
	private IStorageData[] dataList = new IStorageData[10];
	private GridEnergyStorage energy = new GridEnergyStorage(100, 0);
	@Override
	public StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid();
	}
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			//grid.getData().addInventory(inv);
			grid.getData().addEnergyStorage(energy);
			boolean needsSync = false;
			for(int i = 0;i<10;i++){
				if(stack[i] != null && stack[i].getItem() instanceof IStorageCell && ((IStorageCell)stack[i].getItem()).isValid(stack[i])){
					if(grid.isPowered()){
						if(bootTime[i] != 0){
							if(bootTime[i] > 0){
								bootTime[i]--;
								if(bootTime[i] == 0){
									needsSync = true;
									clientLeds[i] = (byte) (((IStorageCell)stack[i].getItem()).getLightState(stack[i], worldObj, pos).ordinal() + 1);
									dataList[i] = ((IStorageCell)stack[i].getItem()).getData(stack[i], worldObj, pos, 0);
								}
							}else{
								bootTime[i] = ((IStorageCell)stack[i].getItem()).getBootTime(stack[i], worldObj, pos);
								needsSync = true;
								clientLeds[i] = 0;
							}
						}else{
							if(dataList[i] == null){
								needsSync = true;
								dataList[i] = ((IStorageCell)stack[i].getItem()).getData(stack[i], worldObj, pos, 0);
							}
							byte old = clientLeds[i];
							clientLeds[i] = (byte) (((IStorageCell)stack[i].getItem()).getLightState(stack[i], worldObj, pos).ordinal() + 1);
							if(old != clientLeds[i])needsSync = true;
						}
						grid.drainEnergy(((IStorageCell)stack[i].getItem()).getPowerDrain(stack[i], worldObj, pos) / 10D);
						if(dataList[i] != null){
							grid.getData().addStorageData(dataList[i]);
							dataList[i].update(stack[i], null, worldObj, 0);
						}
					}else{
						int oldBootTime = bootTime[i];
						bootTime[i] = -1;
						if(oldBootTime != -1){
							clientLeds[i] = -1;
							needsSync = true;
						}
						if(dataList[i] != null){
							grid.getData().removeStorageData(dataList[i]);
							dataList[i] = null;
						}
					}
				}else{
					int oldBootTime = bootTime[i];
					bootTime[i] = -2;
					if(oldBootTime != -2){
						clientLeds[i] = -2;
						needsSync = true;
						if(dataList[i] != null){
							grid.getData().removeStorageData(dataList[i]);
							dataList[i] = null;
						}
					}
				}
			}
			if(needsSync)markBlockForUpdate(pos);
		}else{
			i++;
			if(i == 8){
				i = 0;
				ledOn = !ledOn;
			}
		}
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		//inv.setInventorySlotContents(0, ItemStack.loadItemStackFromNBT(compound.getCompoundTag("storedTag")));
		energy.readFromNBT(compound);
		stack = new ItemStack[this.getSizeInventory()];
		NBTTagList list = compound.getTagList("inventory", 10);
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < this.stack.length)
			{
				this.stack[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		//NBTTagCompound t = new NBTTagCompound();
		//if(inv.getStackInSlot(0) != null)inv.getStackInSlot(0).writeToNBT(t);
		//compound.setTag("storedTag", t);
		energy.writeToNBT(compound);
		NBTTagList list = new NBTTagList();
		for(int i = 0;i<stack.length;i++){
			if(stack[i] != null){
				NBTTagCompound tag = new NBTTagCompound();
				stack[i].writeToNBT(tag);
				tag.setByte("Slot", (byte) i);
				list.appendTag(tag);
			}
		}
		compound.setTag("inventory", list);
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
	public ItemStack getStackInSlot(int index) {
		return stack[index];
	}

	@Override
	public ItemStack decrStackSize(int slot, int par2) {
		if (this.stack[slot] != null) {
			ItemStack itemstack;
			if (this.stack[slot].stackSize <= par2) {
				itemstack = this.stack[slot];
				this.stack[slot] = null;
				return itemstack;
			} else {
				itemstack = this.stack[slot].splitStack(par2);

				if (this.stack[slot].stackSize == 0) {
					this.stack[slot] = null;
				}
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack is = stack[index];
		stack[index] = null;
		return is;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.stack[index] = stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUseable(pos, player, worldObj, this);
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack != null && stack.getItem() instanceof IStorageCell && ((IStorageCell)stack.getItem()).isValid(stack);
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
	public void clear() {
		stack = new ItemStack[this.getSizeInventory()];
	}
	@Override
	public EnumFacing getFacing() {
		IBlockState state = worldObj.getBlockState(pos);
		return state.getValue(Drive.FACING);
	}
	public boolean onBlockActivated(EntityPlayer player, ItemStack held){
		if(!worldObj.isRemote){
			if(held != null && held.getItem() instanceof IStorageCell && ((IStorageCell)held.getItem()).isValid(held)){
				for(int i = 0;i<10;i++){
					if(this.stack[i] == null){
						this.stack[i] = held.splitStack(1);
						break;
					}
				}
			}else{
				player.openGui(CoreInit.modInstance, GuiIDs.drive.ordinal(), worldObj, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}
	/**0:No Cell, 1:Cell Inactive, 2:Cell Green, 3:Cell Orange, 4:Cell Red, 5:Cell Booting*/
	public int getDriveColor(int slot){
		if(slot < 10){
			if(clientLeds[slot] == -2)return 0;
			if(clientLeds[slot] == -1)return 1;
			if(clientLeds[slot] == 0)return ledOn ? 5 : 1;
			if(clientLeds[slot] == 1)return 2;
			if(clientLeds[slot] == 2)return 3;
			if(clientLeds[slot] == 3)return 4;
		}
		return 0;
	}
	@Override
	public void markDirty() {
		super.markDirty();
		for(int i = 0;i<10;i++){
			if(dataList[i] != null){
				grid.getData().removeStorageData(dataList[i]);
				dataList[i] = null;
			}
		}
	}
	@Override
	public void onGridReload() {
		updateEntity();
	}
	@Override
	public void onGridPostReload() {

	}
	public void writeToStackNBT(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for(int i = 0;i<stack.length;i++){
			if(stack[i] != null){
				NBTTagCompound tag = new NBTTagCompound();
				stack[i].writeToNBT(tag);
				tag.setByte("Slot", (byte) i);
				list.appendTag(tag);
			}
		}
		compound.setTag("inventory", list);
	}
}
