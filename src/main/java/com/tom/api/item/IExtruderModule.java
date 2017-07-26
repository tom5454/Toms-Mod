package com.tom.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IExtruderModule {
	int getLevel(ItemStack stack, World world, BlockPos pos);

	int getSpeed(ItemStack stack, World world, BlockPos pos);
}
