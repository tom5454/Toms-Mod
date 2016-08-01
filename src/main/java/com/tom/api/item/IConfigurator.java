package com.tom.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IConfigurator {
	boolean isConfigurator(ItemStack stack, EntityPlayer player);
	boolean use(ItemStack stack, EntityPlayer player, boolean simulate);
}
