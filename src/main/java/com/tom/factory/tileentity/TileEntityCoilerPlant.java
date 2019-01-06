package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import com.tom.core.CoreInit;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntityCoilerPlant extends TileEntityMachineBase {
	private EnergyStorage energy = new EnergyStorage(20000, 100);
	private static final int MAX_PROCESS_TIME = 400;
	// private int maxProgress = 1;
	public int clientEnergy = 0;

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index == 3 ? stack.getItem() == CoreInit.emptyWireCoil : true;
	}

	@Override
	public String getName() {
		return "coilerPlant";
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
		return (index == 0 || index == 3) && isItemValidForSlot(index, stack);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 1;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.progress = compound.getInteger("progress");
		// this.maxProgress = compound.getInteger("maxProgress");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("progress", progress);
		// compound.setInteger("mayProgress", maxProgress);
		return compound;
	}

	public int getClientEnergyStored() {
		return MathHelper.ceil(energy.getEnergyStored());
	}

	public long getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public void updateProgress() {
		int upgradeC = getSpeedUpgradeCount();
		int p = upgradeC + (upgradeC / 2) + 1;
		progress = Math.max(0, progress - p);
		energy.extractEnergy(1 * p, false);
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

	@Override
	public ResourceLocation getFront() {
		return new ResourceLocation("tomsmodfactory:textures/blocks/coilerFront.png");
	}

	@Override
	public int[] getOutputSlots() {
		return new int[]{1};
	}

	@Override
	public int[] getInputSlots() {
		return new int[]{0, 3};
	}

	@Override
	public void checkItems() {
		ItemStackChecker s = MachineCraftingHandler.getCoilerOutput(inv.getStackInSlot(0));
		if (s != null && !inv.getStackInSlot(3).isEmpty() && inv.getStackInSlot(3).getItem() == CoreInit.emptyWireCoil) {
			checkItems(s, 1, getMaxProgress(), 0, 3);
			setOut(0, s);
		}
	}

	@Override
	public void finish() {
		ItemStackChecker s = getOutput(0);
		if (s != null && !inv.getStackInSlot(3).isEmpty() && inv.getStackInSlot(3).getItem() == CoreInit.emptyWireCoil) {
			addItemsAndSetProgress(s, 1, 0, 3);
		} else {
			progress = -1;
		}
	}
}