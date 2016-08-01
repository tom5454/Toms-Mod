package com.tom.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
/**Also DO NOT forget to call:
 * <br>{@link com.tom.api.TomsModAPIMain#useWrench(ItemStack, EntityPlayer, net.minecraft.world.World, net.minecraft.util.BlockPos, net.minecraft.util.EnumFacing, float, float, float)}
 * <br> in <br>{@link net.minecraft.item.Item#onItemUse(ItemStack, EntityPlayer, net.minecraft.world.World, net.minecraft.util.BlockPos, net.minecraft.util.EnumFacing, float, float, float)}*/
public interface IWrench {
	boolean isWrench(ItemStack is, EntityPlayer player);
}
