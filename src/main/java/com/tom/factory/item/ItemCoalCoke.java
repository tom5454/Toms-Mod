package com.tom.factory.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCoalCoke extends Item  {

	@Override
	public int getItemBurnTime(ItemStack itemStack) {
		return 3200;
	}
}
