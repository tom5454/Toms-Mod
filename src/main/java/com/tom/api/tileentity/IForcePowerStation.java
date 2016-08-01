package com.tom.api.tileentity;

import net.minecraft.util.math.BlockPos;

public interface IForcePowerStation {
	double pullEnergy(BlockPos from, double amount, boolean simulate);
	void registerDevice(IForceDevice device);
}
