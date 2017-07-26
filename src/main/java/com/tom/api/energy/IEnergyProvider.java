package com.tom.api.energy;

import net.minecraft.util.EnumFacing;

/**
 * Implement this interface on Tile Entities which should provide energy,
 * generally storing it in one or more internal {@link IEnergyStorage} objects.
 *
 * @author tom5454
 *
 */
public interface IEnergyProvider extends IEnergyConnection, IEnergyStorageTile {

	/**
	 * Remove energy from an IEnergyProvider, internal distribution is left
	 * entirely to the IEnergyProvider.
	 *
	 * @param from
	 *            Orientation the energy is extracted from.
	 * @param maxExtract
	 *            Maximum amount of energy to extract.
	 * @param simulate
	 *            If TRUE, the extraction will only be simulated.
	 * @return Amount of energy that was (or would have been, if simulated)
	 *         extracted.
	 */
	double extractEnergy(EnumFacing from, EnergyType type, double maxExtract, boolean simulate);

	/**
	 * Returns the amount of energy currently stored.
	 */
	@Override
	double getEnergyStored(EnumFacing from, EnergyType type);

	/**
	 * Returns the maximum amount of energy that can be stored.
	 */
	@Override
	int getMaxEnergyStored(EnumFacing from, EnergyType type);

}
