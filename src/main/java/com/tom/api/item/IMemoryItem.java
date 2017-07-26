package com.tom.api.item;

import net.minecraft.item.ItemStack;

public interface IMemoryItem {
	int getMemory(ItemStack s);

	int getTier(ItemStack s);
}
