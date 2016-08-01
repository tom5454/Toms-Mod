package com.tom.api.energy;

import java.util.List;

import net.minecraft.util.EnumFacing;

public interface IEnergyStorageTile {
	/**
	 * Returns the amount of energy currently stored in that type.
	 */
	double getEnergyStored(EnumFacing from, EnergyType type);

	/**
	 * Returns the maximum amount of energy that can be stored in that type.
	 */
	int getMaxEnergyStored(EnumFacing from, EnergyType type);
	/**
	 *
	 * @return the valid energy types that can be stored.
	 */
	List<EnergyType> getValidEnergyTypes();
}
