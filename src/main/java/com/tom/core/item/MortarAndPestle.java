package com.tom.core.item;

import net.minecraft.item.ItemStack;

import com.tom.api.item.ItemCraftingTool;

public class MortarAndPestle extends ItemCraftingTool {

	@Override
	public int getDurability(ItemStack stack) {
		return /*Config.enableHardRecipes ? 150 : */200;
	}

}
