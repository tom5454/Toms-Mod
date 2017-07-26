package com.tom.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IMagCard {
	public boolean isCodeEqual(ItemStack is, World world, String[] code, EntityPlayer player);

	public boolean isCopyable(ItemStack is, World world);

	public String[] getCodes(ItemStack is, World world, EntityPlayer player);

	public void write(String code, String name, ItemStack is, World world, EntityPlayer player);
}
