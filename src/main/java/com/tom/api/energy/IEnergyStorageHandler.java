package com.tom.api.energy;

import java.util.List;

public interface IEnergyStorageHandler {
	/**
	 * Adds energy to the storage. Returns quantity of energy that was accepted.
	 *
	 * @param maxReceive
	 *            Maximum amount of energy to be inserted.
	 * @param simulate
	 *            If TRUE, the insertion will only be simulated.
	 * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
	 */
	double receiveEnergy(EnergyType type, double maxReceive, boolean simulate);

	/**
	 * Removes energy from the storage. Returns quantity of energy that was removed.
	 *
	 * @param maxExtract
	 *            Maximum amount of energy to be extracted.
	 * @param simulate
	 *            If TRUE, the extraction will only be simulated.
	 * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
	 */
	double extractEnergy(EnergyType type, double maxExtract, boolean simulate);

	/**
	 * Returns the amount of energy currently stored in that type.
	 */
	double getEnergyStored(EnergyType type);

	/**
	 * Returns the maximum amount of energy that can be stored in that type.
	 */
	int getMaxEnergyStored(EnergyType type);

	/**
	 *
	 * @return the valid energy types that can be stored.
	 */
	List<EnergyType> getValidEnergyTypes();

	boolean canConnectEnergy(EnergyType type);
}
