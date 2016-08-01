package com.tom.factory.tileentity;

import static com.tom.api.energy.EnergyType.HV;

import java.util.List;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.ElectricFurnace;
import com.tom.recipes.handler.MachineCraftingHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class TileEntityElectricFurnaceAdv extends TileEntityTomsMod implements ISidedInventory, IEnergyReceiver {
	private ItemStack[] stack = new ItemStack[this.getSizeInventory()];
	private EnergyStorage energy = new EnergyStorage(10000, 100);
	private int progress = -1;
	private static final int[] SLOTS = new int[]{0,1};
	public static final int MAX_PROCESS_TIME = 50;
	//private int maxProgress = 1;
	public int clientEnergy = 0;
	@Override
	public int getSizeInventory() {
		return 3;
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
		return 64;
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
		return true;
	}

	@Override
	public int getField(int id) {
		return id == 0 ? progress : 0;
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0)progress = value;
		//else if(id == 1)maxProgress = value;
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public void clear() {
		stack = new ItemStack[this.getSizeInventory()];
	}

	@Override
	public String getName() {
		return "advElectricFurnace";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 1;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == HV;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return HV.getList();
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return canConnectEnergy(from, type) ? energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getMaxEnergyStored();
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
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
		this.progress = compound.getInteger("progress");
		//this.maxProgress = compound.getInteger("maxProgress");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
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
		compound.setInteger("progress", progress);
		//compound.setInteger("mayProgress", maxProgress);
		return compound;
	}
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			if(energy.extractEnergy(20D, true) == 20D){
				if(progress > 0){
					updateProgress();
				}else if(progress == 0){
					ItemStack s = MachineCraftingHandler.getFurnaceRecipe(stack[0]);
					if(s != null){
						if(stack[1] != null){
							if(TomsModUtils.areItemStacksEqual(stack[1], s, true, true, false) && stack[1].stackSize + s.stackSize <= s.getMaxStackSize() && stack[0].stackSize >= 1){
								stack[1].stackSize += s.stackSize;
								progress = -1;
								decrStackSize(0, 1);
							}
						}else{
							progress = -1;
							stack[1] = s;
							decrStackSize(0, 1);
						}
					}else{
						progress = -1;
					}
				}else{
					ItemStack s = MachineCraftingHandler.getFurnaceRecipe(stack[0]);
					if(s != null){
						if(stack[1] != null){
							if(TomsModUtils.areItemStacksEqual(stack[1], s, true, true, false) && stack[1].stackSize + s.stackSize <= s.getMaxStackSize() && stack[0].stackSize >= 1){
								progress = MAX_PROCESS_TIME;
							}
						}else{
							progress = MAX_PROCESS_TIME;
						}
					}
					TomsModUtils.setBlockStateWithCondition(worldObj, pos, ElectricFurnace.ACTIVE, progress > 0);
				}
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, ElectricFurnace.ACTIVE, false);
			}
		}
	}

	public int getClientEnergyStored() {
		return MathHelper.ceiling_double_int(energy.getEnergyStored());
	}

	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}
	private void updateProgress(){
		int upgradeC = getSpeedUpgradeCount();
		int p = upgradeC + 1 + (upgradeC / 2);
		progress = Math.max(0, progress - p);
		energy.extractEnergy(1.5D * p, false);
	}
	private int getSpeedUpgradeCount(){
		return stack[2] != null && stack[2].getItem() == FactoryInit.speedUpgrade ? stack[2].stackSize : 0;
	}
}