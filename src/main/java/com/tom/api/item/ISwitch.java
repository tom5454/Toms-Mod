package com.tom.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ISwitch {
	boolean isSwitch(ItemStack stack, EntityPlayer player);
}
