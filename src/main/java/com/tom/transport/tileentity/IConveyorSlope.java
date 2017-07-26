package com.tom.transport.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IConveyorSlope {
	boolean isValid();

	ItemStack insert(ItemStack stack, EnumFacing facing);
}
