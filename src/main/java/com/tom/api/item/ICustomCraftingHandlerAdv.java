package com.tom.api.item;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface ICustomCraftingHandlerAdv{
	void onCrafingAdv(String player, ItemStack crafting, ItemStack second, IInventory craftMatrix);
	void onUsingAdv(String player, ItemStack crafting, ItemStack second, IInventory craftMatrix, ItemStack s);
}
