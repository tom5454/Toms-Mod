package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntitySteamCrusherAdv extends TileEntitySteamMachine {
	private static final int[] SLOTS = new int[]{0, 1, 2};
	public static final int MAX_PROCESS_TIME = 250;

	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public String getName() {
		return "steamCrusherAdv";
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
		return 20;
	}

	@Override
	public void checkItems() {
		ItemStackChecker s = MachineCraftingHandler.getCrusherOutput(inv.getStackInSlot(0), 1);
		checkItems(s, 1, MAX_PROCESS_TIME, 0, -1);
	}

	@Override
	public void finish() {
		addItemsAndSetProgress(1);
	}
}
