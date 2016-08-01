package com.tom.api.tileentity;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface IExtendedInventory{
	boolean canInsertItemFrom(EnumFacing side, BlockPos from);
}
