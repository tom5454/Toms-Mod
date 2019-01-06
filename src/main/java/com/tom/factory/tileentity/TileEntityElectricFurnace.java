package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import com.tom.lib.api.energy.EnergyStorage;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntityElectricFurnace extends TileEntityMachineBase {
	private EnergyStorage energy = new EnergyStorage(10000, 100);
	private static final int[] SLOTS = new int[]{0, 1};
	private static final int MAX_PROCESS_TIME = 150;
	// private int maxProgress = 1;
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
	public String getName() {
		return "electricFurnace";
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
		int p = upgradeC + 1 + (upgradeC / 2);
		progress = Math.max(0, progress - p);
		energy.extractEnergy(0.3D * p, false);
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
		return new ResourceLocation("tomsmodfactory:textures/blocks/eFurnace.png");
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
		ItemStack s = MachineCraftingHandler.getFurnaceRecipe(inv.getStackInSlot(0));
		if (!s.isEmpty()) {
			ItemStackChecker c = new ItemStackChecker(s);
			c.setExtra(1);
			checkItems(c, 1, getMaxProgress(), 0, -1);
			setOut(0, c);
		}
	}

	@Override
	public void finish() {
		addItemsAndSetProgress(getOutput(0), 1, 0, -1);
	}
}