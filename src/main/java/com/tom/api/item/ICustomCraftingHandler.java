package com.tom.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface ICustomCraftingHandler {
	void onCrafing(EntityPlayer crafter, ItemStack returnStack, IInventory crafingTableInventory);
	void onUsing(EntityPlayer crafter, ItemStack returnStack, IInventory crafingTableInventory, ItemStack stack);
}
