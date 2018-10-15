package com.tom.recipes;

import java.util.Map;

import net.minecraft.item.ItemStack;

public interface ICustomJsonIngerdient {
	Map<String, Object> serialize(ItemStack stack, boolean serializeCount);
}
