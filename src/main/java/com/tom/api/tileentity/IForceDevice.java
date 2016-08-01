package com.tom.api.tileentity;

import net.minecraft.util.math.BlockPos;

public interface IForceDevice {
	double receiveEnergy(double maxReceive, boolean simulate);
	BlockPos getPos2();
	boolean isValid(BlockPos from);
}
