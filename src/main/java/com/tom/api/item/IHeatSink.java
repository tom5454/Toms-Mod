package com.tom.api.item;

import net.minecraft.item.ItemStack;

public interface IHeatSink {
	double getPassiveHeat(ItemStack s);

	double getHactiveHeat(ItemStack s);
}
