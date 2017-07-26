package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public abstract class TileEntitySteamFurnaceBase extends TileEntitySteamMachine {
	protected static final int[] SLOTS = new int[]{0, 1};

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return null;
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
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public void checkItems() {
		ItemStack s = MachineCraftingHandler.getFurnaceRecipe(inv.getStackInSlot(0));
		if (!s.isEmpty()) {
			ItemStackChecker c = new ItemStackChecker(s);
			c.setExtra(1);
			checkItems(c, 1, getMaxProgressTime(), 0, -1);
		}
	}

	@Override
	public void finish() {
		addItemsAndSetProgress(1);
	}

	public abstract int getMaxProgressTime();
}
