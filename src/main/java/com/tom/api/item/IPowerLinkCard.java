package com.tom.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IPowerLinkCard {
	BlockPos getMaster(ItemStack stack);

	void setMaster(ItemStack stack, BlockPos pos);

	boolean isEmpty(ItemStack stack);
}
