package com.tom.api.tileentity;

import net.minecraft.util.math.BlockPos;

import com.tom.lib.api.energy.IEnergyStorage;

public interface IForcePowerStation extends ISecuredTileEntity {
	IEnergyStorage getEnergyHandler(BlockPos from);

	void registerDevice(IForceDevice device);

	BlockPos getPos2();

	boolean isActive();
}
