package com.tom.energy.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.IEnergyContainerItem;
import com.tom.apis.TomsModUtils;
import com.tom.factory.tileentity.TileEntityMachineBase;

import com.tom.energy.block.BlockCharger;

public class TileEntityCharger extends TileEntityMachineBase {
	public int clientEnergy = 0;
	private EnergyStorage energy = new EnergyStorage(1000000, 10000);

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0 ? isItemValidForSlot(index, itemStackIn) : false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 1;
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack != null && stack.getItem() instanceof IEnergyContainerItem;
	}

	@Override
	public String getName() {
		return "charger";
	}

	@Override
	public EnergyStorage getEnergy() {
		return energy;
	}

	@Override
	public int getUpgradeSlot() {
		return -1;
	}

	@Override
	public int getMaxProcessTimeNormal() {
		return -1;
	}
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			if(energy.hasEnergy() && canRun()){
				if(stack[0] != null && stack[0].getItem() instanceof IEnergyContainerItem){
					if(((IEnergyContainerItem) stack[0].getItem()).getEnergyStored(stack[0]) == ((IEnergyContainerItem) stack[0].getItem()).getMaxEnergyStored(stack[0])){
						TomsModUtils.setBlockStateWithCondition(worldObj, pos, BlockCharger.ACTIVE, false);
						if(stack[1] == null){
							stack[1] = stack[0];
							stack[0] = null;
						}
					}else{
						TomsModUtils.setBlockStateWithCondition(worldObj, pos, BlockCharger.ACTIVE, true);
						double extract = energy.extractEnergy(Math.pow(10, (2 + (2 - getType()))), true);
						if(extract > 0){
							double receive = ((IEnergyContainerItem) stack[0].getItem()).receiveEnergy(stack[0], extract, true);
							if(receive > 0){
								((IEnergyContainerItem) stack[0].getItem()).receiveEnergy(stack[0], energy.extractEnergy(receive, false), false);
							}
						}
					}
				}
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, BlockCharger.ACTIVE, false);
			}
		}
	}

	public int getClientEnergyStored() {
		return MathHelper.ceiling_double_int(energy.getEnergyStored());
	}
	@Override
	public int getField(int id) {
		return id == 0 ? getChargedPer() : 0;
	}
	public int p;
	@Override
	public void setField(int id, int value) {
		if(id == 0)p = value;
	}
	private int getChargedPer(){
		if(stack[0] != null && stack[0].getItem() instanceof IEnergyContainerItem){
			IEnergyContainerItem c = (IEnergyContainerItem) stack[0].getItem();
			return MathHelper.ceiling_double_int((c.getEnergyStored(stack[0]) / c.getMaxEnergyStored(stack[0])) * 100);
		}else return 0;
	}

	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public ResourceLocation getFront() {
		return new ResourceLocation("tomsmodenergy:textures/blocks/charger.png");
	}

	@Override
	public int[] getOutputSlots() {
		return new int[]{1};
	}

	@Override
	public int[] getInputSlots() {
		return new int[]{0};
	}
}
