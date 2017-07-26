package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntitySteamAlloySmelter extends TileEntitySteamMachine {
	private static final int[] SLOTS = new int[]{0, 1, 2};
	public static final int MAX_PROCESS_TIME = 400;

	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public String getName() {
		return "steamAlloySmelter";
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0 || index == 1;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 2;
	}

	@Override
	public int getSteamUsage() {
		return 12;
	}

	@Override
	public void checkItems() {
		ItemStackChecker s = MachineCraftingHandler.getAlloySmelterOutput(inv.getStackInSlot(0), inv.getStackInSlot(1));
		checkItems(s, 2, MAX_PROCESS_TIME, 0, 1);
	}

	@Override
	public void finish() {
		addItemsAndSetProgress(2);
	}
}