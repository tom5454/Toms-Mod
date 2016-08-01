package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.item.IExtruderModule;
import com.tom.apis.TomsModUtils;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.PlateBlendingMachine;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntityPlateBlendingMachine extends TileEntityMachineBase {
	private EnergyStorage energy = new EnergyStorage(20000, 100);
	private static final int[] SLOTS = new int[]{0,1};
	private static final int MAX_PROCESS = 400;
	//private int maxProgress = 1;
	public int clientEnergy = 0;
	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public String getName() {
		return "plateBlender";
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
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.progress = compound.getInteger("progress");
		//this.maxProgress = compound.getInteger("maxProgress");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("progress", progress);
		//compound.setInteger("mayProgress", maxProgress);
		return compound;
	}
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			if(energy.extractEnergy(30D, true) == 30D){
				if(progress > 0){
					updateProgress();
				}else if(progress == 0){
					ItemStackChecker s = MachineCraftingHandler.getPlateBlenderOutput(stack[0], getBlendingLevel());
					if(s != null){
						if(stack[1] != null){
							if(TomsModUtils.areItemStacksEqual(stack[1], s.getStack(), true, true, false) && stack[1].stackSize + s.getStack().stackSize <= s.getStack().getMaxStackSize() && stack[0].stackSize >= s.getExtra()){
								stack[1].stackSize += s.getStack().stackSize;
								progress = -1;
								decrStackSize(0, s.getExtra());
							}
						}else{
							progress = -1;
							stack[1] = s.getStack();
							decrStackSize(0, s.getExtra());
						}
					}else{
						progress = -1;
					}
				}else{
					ItemStackChecker s = MachineCraftingHandler.getPlateBlenderOutput(stack[0], getBlendingLevel());
					if(s != null){
						if(stack[1] != null){
							if(TomsModUtils.areItemStacksEqual(stack[1], s.getStack(), true, true, false) && stack[1].stackSize + s.getStack().stackSize <= s.getStack().getMaxStackSize() && stack[0].stackSize >= s.getExtra()){
								progress = getMaxProgress();
							}
						}else{
							progress = getMaxProgress();
						}
					}
					TomsModUtils.setBlockStateWithCondition(worldObj, pos, PlateBlendingMachine.ACTIVE, progress > 0);
				}
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, PlateBlendingMachine.ACTIVE, false);
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
		IExtruderModule m = getBlendingModule();
		int speed = m != null ? m.getSpeed(stack[3], worldObj, pos) + 1 : 1;
		int p = upgradeC + (upgradeC / 2) + speed;
		progress = Math.max(0, progress - p);
		energy.extractEnergy(1 * p, false);
	}
	private int getSpeedUpgradeCount(){
		return stack[2] != null && stack[2].getItem() == FactoryInit.speedUpgrade ? stack[2].stackSize : 0;
	}
	private int getBlendingLevel(){
		int lvl = getBlendingModuleLevel();
		return lvl <= 0 ? 0 : (lvl == 1 ? 2 : (lvl == 2 ? 4 : 5));
	}
	private int getBlendingModuleLevel(){
		IExtruderModule m = getBlendingModule();
		return m != null ? m.getLevel(stack[3], worldObj, pos) : 0;
	}
	private IExtruderModule getBlendingModule(){
		return stack[3] != null && stack[3].getItem() instanceof IExtruderModule ? (IExtruderModule) stack[3].getItem() : null;
	}

	@Override
	public EnergyStorage getEnergy() {
		return energy;
	}

	@Override
	public int getUpgradeSlot() {
		return 2;
	}
	@Override
	public void writeToStackNBT(NBTTagCompound tag) {
		super.writeToStackNBT(tag);
		NBTTagList list = tag.getTagList("inventory", 10);
		if(stack[3] != null){
			NBTTagCompound t = new NBTTagCompound();
			stack[3].writeToNBT(t);
			t.setByte("Slot", (byte) 3);
			list.appendTag(t);
		}
	}

	@Override
	public int getMaxProcessTimeNormal() {
		return MAX_PROCESS;
	}
}
