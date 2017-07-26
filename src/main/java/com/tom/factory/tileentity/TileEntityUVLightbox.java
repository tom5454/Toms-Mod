package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import com.tom.api.energy.EnergyStorage;
import com.tom.core.TMResource.CraftingMaterial;
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

	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public void updateProgress() {
		int upgradeC = getSpeedUpgradeCount();
		int p = upgradeC + MathHelper.floor(10 * (getMaxProcessTimeNormal() / TYPE_MULTIPLIER_SPEED[getType()])) + (upgradeC / 2);
		progress = Math.max(0, progress - p);
		energy.extractEnergy(0.1D * p, false);
	}

	private ItemStack getRecipe() {
		int lvl = 2 - getType();
		if (!inv.getStackInSlot(2).isEmpty() && CraftingMaterial.equals(inv.getStackInSlot(2).getItem())) {
			if (CraftingMaterial.BLUEPRINT_BASIC_CIRCUIT.equals(inv.getStackInSlot(2)) && CraftingMaterial.PHOTOACTIVE_BASIC_CIRCUIT_PLATE.equals(inv.getStackInSlot(0))) {
				return CraftingMaterial.RAW_BASIC_CIRCUIT_PANEL.getStackNormal();
			} else if (CraftingMaterial.BLUEPRINT_NORMAL_CIRCUIT.equals(inv.getStackInSlot(2)) && CraftingMaterial.PHOTOACTIVE_BASIC_CIRCUIT_PLATE.equals(inv.getStackInSlot(0))) {
				return CraftingMaterial.RAW_NORMAL_CIRCUIT_PANEL.getStackNormal();
			} else if (CraftingMaterial.BLUEPRINT_ADVANCED_CIRCUIT.equals(inv.getStackInSlot(2)) && CraftingMaterial.PHOTOACTIVE_ADVANCED_CIRCUIT_PLATE.equals(inv.getStackInSlot(0))) {
				return lvl > 0 ? CraftingMaterial.RAW_ADVANCED_CIRCUIT_PANEL.getStackNormal() : ItemStack.EMPTY;
			} else if (CraftingMaterial.BLUEPRINT_ELITE_CIRCUIT.equals(inv.getStackInSlot(2)) && CraftingMaterial.PHOTOACTIVE_ADVANCED_CIRCUIT_PLATE.equals(inv.getStackInSlot(0))) { return lvl > 1 ? CraftingMaterial.RAW_ELITE_CIRCUIT_PANEL.getStackNormal() : ItemStack.EMPTY; }
		}
		return ItemStack.EMPTY;
	}

	private int getTime() {
		if (!inv.getStackInSlot(2).isEmpty() && CraftingMaterial.equals(inv.getStackInSlot(2).getItem())) {
			if (CraftingMaterial.BLUEPRINT_BASIC_CIRCUIT.equals(inv.getStackInSlot(2)) && CraftingMaterial.PHOTOACTIVE_BASIC_CIRCUIT_PLATE.equals(inv.getStackInSlot(0))) {
				return 500;
			} else if (CraftingMaterial.BLUEPRINT_NORMAL_CIRCUIT.equals(inv.getStackInSlot(2)) && CraftingMaterial.PHOTOACTIVE_BASIC_CIRCUIT_PLATE.equals(inv.getStackInSlot(0))) {
				return 1000;
			} else if (CraftingMaterial.BLUEPRINT_ADVANCED_CIRCUIT.equals(inv.getStackInSlot(2)) && CraftingMaterial.PHOTOACTIVE_ADVANCED_CIRCUIT_PLATE.equals(inv.getStackInSlot(0))) {
				return 1600;
			} else if (CraftingMaterial.BLUEPRINT_ELITE_CIRCUIT.equals(inv.getStackInSlot(2)) && CraftingMaterial.PHOTOACTIVE_ADVANCED_CIRCUIT_PLATE.equals(inv.getStackInSlot(0))) { return 2500; }
		}
		return 0;
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
		ItemStack s = getRecipe();
		if (!s.isEmpty()) {
			ItemStack s2 = s.copy();
			s2.setCount(1);
			ItemStackChecker c = new ItemStackChecker(s2).setExtra2(s.getCount());
			checkItems(c, 1, maxProgress = getTime(), 4, 0);
			setOut(0, c);
		}
	}

	@Override
	public void finish() {
		addItemsAndSetProgress(getOutput(0), 1, 4, 0);
	}
}
