package com.tom.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IScroller {
	void scroll(ItemStack stack, EntityPlayer player, ScrollDirection dir);

	boolean canScroll(ItemStack stack);

	public static enum ScrollDirection {
		UP, DOWN;
		public static final ScrollDirection[] VALUES = values();
	}
}
