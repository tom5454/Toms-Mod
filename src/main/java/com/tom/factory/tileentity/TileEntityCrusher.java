package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

import com.tom.api.energy.EnergyStorage;
import com.tom.apis.TomsModUtils;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.BlockCrusher;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntityCrusher extends TileEntityMachineBase {
	private EnergyStorage energy = new EnergyStorage(10000, 100);
	private static final int[] SLOTS = new int[]{0,1};
	private static final int MAX_PROCESS_TIME = 300;
	public int clientEnergy = 0;
	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
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
	public String getName() {
		return "crusher";
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
		//compound.setInteger("maxProgress", maxProgress);
		return compound;
	}
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			if(energy.extractEnergy(20D, true) == 20D){
				if(progress > 0){
					updateProgress();
				}else if(progress == 0){
					ItemStackChecker s = MachineCraftingHandler.getCrusherOutput(stack[0]);
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
					ItemStackChecker s = MachineCraftingHandler.getCrusherOutput(stack[0]);
					if(s != null){
						if(stack[1] != null){
							if(TomsModUtils.areItemStacksEqual(stack[1], s.getStack(), true, true, false) && stack[1].stackSize + s.getStack().stackSize <= s.getStack().getMaxStackSize() && stack[0].stackSize >= s.getExtra()){
								progress = getMaxProgress();
							}
						}else{
							progress = getMaxProgress();
						}
					}
					TomsModUtils.setBlockStateWithCondition(worldObj, pos, BlockCrusher.ACTIVE, progress > 0);
				}
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, BlockCrusher.ACTIVE, false);
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
		energy.extractEnergy(0.5D * p, false);
	}
	private int getSpeedUpgradeCount(){
		return stack[2] != null && stack[2].getItem() == FactoryInit.speedUpgrade ? stack[2].stackSize : 0;
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
	public int getMaxProcessTimeNormal() {
		return MAX_PROCESS_TIME;
	}
}