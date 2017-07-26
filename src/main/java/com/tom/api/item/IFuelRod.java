package com.tom.api.item;

import net.minecraft.item.ItemStack;

public interface IFuelRod {
	public int getHeat(ItemStack is);

	public ItemStack useSingle(ItemStack is);
}
