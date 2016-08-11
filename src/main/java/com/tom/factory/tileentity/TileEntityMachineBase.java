package com.tom.factory.tileentity;

import static com.tom.api.energy.EnergyType.HV;
import static com.tom.api.energy.EnergyType.LV;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.factory.FactoryInit;

public abstract class TileEntityMachineBase extends TileEntityTomsMod implements ISidedInventory, IEnergyReceiver {
	protected ItemStack[] stack = new ItemStack[this.getSizeInventory()];
	protected EnergyType TYPE = HV;
	protected static final float[] TYPE_MULTIPLIER_SPEED = new float[]{1.0F, 0.85F, 0.7F};
	protected static final int[] MAX_SPEED_UPGRADE_COUNT = new int[]{4, 10, 24};
	protected int maxProgress = 1;
	protected int progress = -1;
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
	public int getFieldCount() {
		return 1;
	}

	@Override
	public void clear() {
		stack = new ItemStack[this.getSizeInventory()];
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
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
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
		getEnergy().readFromNBT(compound);
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
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
		getEnergy().writeToNBT(compound);
		return compound;
	}
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == TYPE;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return TYPE.getList();
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return canConnectEnergy(from, type) ? TYPE.convertFrom(LV, getEnergy().receiveEnergy(LV.convertFrom(TYPE, maxReceive), simulate)) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return getEnergy().getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return getEnergy().getMaxEnergyStored();
	}
	public abstract EnergyStorage getEnergy();

	public void setType(int meta){
		if(meta == 0){
			TYPE = HV;
		}else if(meta == 1){
			TYPE = EnergyType.MV;
		}else{
			TYPE = EnergyType.LV;
		}
	}
	public int getType(){
		return getMetaFromEnergyType(TYPE);
	}
	public void writeToStackNBT(NBTTagCompound tag){
		getEnergy().writeToNBT(tag);
		int i = getUpgradeSlot();
		if(i > -1){
			NBTTagList list = new NBTTagList();
			if(stack[i] != null){
				NBTTagCompound t = new NBTTagCompound();
				stack[i].writeToNBT(t);
				t.setByte("Slot", (byte) i);
				list.appendTag(t);
			}
			tag.setTag("inventory", list);
		}
	}
	public abstract int getUpgradeSlot();

	public int getMaxProgress(){
		return MathHelper.floor_double(getMaxProcessTimeNormal() / TYPE_MULTIPLIER_SPEED[getType()]);
	}
	@Override
	public int getField(int id) {
		return id == 0 ? progress : id == 1 ? maxProgress : 0;
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0)progress = value;
		else if(id == 1)maxProgress = value;
	}
	@Override
	public final void preUpdate() {
		maxProgress = getMaxProgress();
	}
	public abstract int getMaxProcessTimeNormal();

	public static int getMetaFromEnergyType(EnergyType type){
		return type == HV ? 0 : type == EnergyType.MV ? 1 : 2;
	}
	public int getSpeedUpgradeCount(){
		int slot = getUpgradeSlot();
		return Math.min(slot < 0 ? 0 : stack[slot] != null && stack[slot].getItem() == FactoryInit.speedUpgrade ? stack[slot].stackSize : 0, MAX_SPEED_UPGRADE_COUNT[getType()]);
	}
	public int getMaxSpeedUpgradeCount(){
		return MAX_SPEED_UPGRADE_COUNT[getType()];
	}
}
