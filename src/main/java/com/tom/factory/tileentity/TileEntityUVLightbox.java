package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import com.tom.lib.api.energy.EnergyStorage;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntityUVLightbox extends TileEntityMachineBase {
	private EnergyStorage energy = new EnergyStorage(10000, 100);
	public int clientEnergy = 0;
	private int maxProgress = 0;

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 1;
	}

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
		return "uvLightBox";
	}

	@Override
	public EnergyStorage getEnergy() {
		return energy;
	}

	@Override
	public int getUpgradeSlot() {
		return 3;
	}

	@Override
	public int getMaxProcessTimeNormal() {
		return 2;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.progress = compound.getInteger("progress");
		this.maxProgress = compound.getInteger("maxProgress");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("progress", progress);
		compound.setInteger("maxProgress", maxProgress);
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
		int p = upgradeC + MathHelper.floor(10 * (getMaxProcessTimeNormal() / TYPE_MULTIPLIER_SPEED[getType()])) + (upgradeC / 2);
		progress = Math.max(0, progress - p);
		energy.extractEnergy(0.1D * p, false);
	}

	@Override
	public int getField(int id) {
		return id == 1 ? maxProgress : super.getField(id);
	}

	@Override
	public ResourceLocation getFront() {
		return new ResourceLocation("tomsmodfactory:textures/blocks/uvBox.png");
	}

	@Override
	public int[] getOutputSlots() {
		return new int[]{1};
	}

	@Override
	public int[] getInputSlots() {
		return new int[]{0};
	}
	@Override
	public void checkItems() {
		ItemStackChecker s = MachineCraftingHandler.getUVBoxOutput(inv.getStackInSlot(0), inv.getStackInSlot(2));
		if (s != null) {
			checkItems(s, 1, maxProgress = s.getExtra3(), 0, -1);
			setOut(0, s);
		}
	}

	@Override
	public void finish() {
		addItemsAndSetProgress(getOutput(0), 1, 0, -1);
	}
}
