package com.tom.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.storage.multipart.StorageNetworkGrid.CellLight;
import com.tom.storage.multipart.StorageNetworkGrid.IStorageData;

public interface IStorageCell {
	IStorageData getData(ItemStack stack, World world, BlockPos pos, int priority);
	CellLight getLightState(ItemStack stack, World world, BlockPos pos);
	double getPowerDrain(ItemStack stack, World world, BlockPos pos);
	int getBootTime(ItemStack stack, World world, BlockPos pos);
	boolean isValid(ItemStack stack);
}
