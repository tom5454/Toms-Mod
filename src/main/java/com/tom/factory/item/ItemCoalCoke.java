package com.tom.factory.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.tom.handler.FuelHandler.IBurnable;

public class ItemCoalCoke extends Item implements IBurnable {

	@Override
	public int getBurnTime(ItemStack stack) {
		return 3200;
	}

}
