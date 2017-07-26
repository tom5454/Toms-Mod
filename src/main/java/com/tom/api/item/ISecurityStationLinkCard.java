package com.tom.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface ISecurityStationLinkCard {
	BlockPos getStation(ItemStack stack);

	void setStation(ItemStack stack, BlockPos pos);

	boolean isEmpty(ItemStack stack);
}
