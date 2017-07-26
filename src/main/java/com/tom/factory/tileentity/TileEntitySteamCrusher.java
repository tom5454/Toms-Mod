package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntitySteamCrusher extends TileEntitySteamMachine {
	private static final int[] SLOTS = new int[]{0, 1, 2};
	public static final int MAX_PROCESS_TIME = 500;

	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public String getName() {
		return "steamCrusher";
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
	public int getSteamUsage() {
		return 10;
	}

	@Override
	public void checkItems() {
		ItemStackChecker s = MachineCraftingHandler.getCrusherOutput(inv.getStackInSlot(0));
		checkItems(s, 1, MAX_PROCESS_TIME, 0, -1);
	}

	@Override
	public void finish() {
		addItemsAndSetProgress(1);
	}
}