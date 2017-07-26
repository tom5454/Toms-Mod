package com.tom.storage.item;

import net.minecraft.world.World;

import com.tom.storage.multipart.PartCraftingTerminal;
import com.tom.storage.multipart.PartTerminal;

public class ItemPartCraftingTerminal extends ItemPartTerminal {
	public ItemPartCraftingTerminal() {
		super(0.35, 0.15);
	}

	@Override
	public PartTerminal createNewTileEntity(World worldIn, int meta) {
		return new PartCraftingTerminal();
	}

	@Override
	public boolean hasCustomFront() {
		return false;
	}

	@Override
	public String getName() {
		return "Crafting Part Terminal";
	}

	@Override
	public String getCategory() {
		return "crafting";
	}

	@Override
	public int[][][] getImageIDs() {
		return new int[][][]{{{1, 0, 0}, {2, 0, -30}}, {{1, 0, -20}, {2, 0, -10}, {3}, {4, 0, -40}, {5}, {6, 0, 0}}};
	}
}
