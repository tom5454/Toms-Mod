package com.tom.recipes;

import java.util.Map;

import net.minecraft.item.ItemStack;

public interface ICustomJsonIngerdient {
	default Map<String, Object> serialize(ItemStack stack, boolean serializeCount){return null;}
	default String getCustomName(ItemStack stack){return null;}
}
