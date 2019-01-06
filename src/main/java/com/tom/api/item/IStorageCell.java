package com.tom.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.grid.StorageNetworkGrid;
import com.tom.api.inventory.IStorageInventory;
import com.tom.storage.item.ItemStorageCell;

public interface IStorageCell {
	IStorageInventory getData(ItemStack stack, World world, BlockPos pos, int priority, StorageNetworkGrid grid);

	ItemStorageCell.CellLight getLightState(IStorageInventory data);

	double getPowerDrain(ItemStack stack, World world, BlockPos pos, StorageNetworkGrid grid);

	int getBootTime(ItemStack stack, World world, BlockPos pos, StorageNetworkGrid grid);

	boolean isValid(ItemStack stack, StorageNetworkGrid grid);
}
