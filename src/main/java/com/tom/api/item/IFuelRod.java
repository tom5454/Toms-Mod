package com.tom.api.item;

import net.minecraft.item.ItemStack;

public interface IFuelRod {
	public int getAmount(ItemStack is);
	public ItemStack getReturnStack(ItemStack is);
}
