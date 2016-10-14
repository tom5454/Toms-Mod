package com.tom.core.item;

import net.minecraft.item.ItemStack;

import com.tom.api.item.ItemCraftingTool;
import com.tom.config.Config;

public class MortarAndPestle extends ItemCraftingTool {

	@Override
	public int getDurability(ItemStack stack) {
		return Config.enableHardMode ? 150 : 200;
	}

}
