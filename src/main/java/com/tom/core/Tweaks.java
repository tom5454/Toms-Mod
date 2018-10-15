package com.tom.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.handler.WrenchHandler;

public class Tweaks {
	public static boolean wrench(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float a, float b, float c, EnumHand hand) {
		return WrenchHandler.use(itemStack, player, world, pos, side, a, b, c, hand);
	}
}
