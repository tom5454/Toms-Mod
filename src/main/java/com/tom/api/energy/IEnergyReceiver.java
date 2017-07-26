package com.tom.api.energy;

import net.minecraft.util.EnumFacing;

/**
 * Implement this interface on Tile Entities which should receive energy,
 * generally storing it in one or more internal {@link IEnergyStorage} objects.
 * 
 * @author tom5454
 *
 */
public interface IEnergyReceiver extends IEnergyConnection, IEnergyStorageTile {

	/**
	 * Add energy to an IEnergyReceiver, internal distribution is left entirely
	 * to the IEnergyReceiver.
	 *
	 * @param from
	 *            Orientation the energy is received from.
	 * @param maxReceive
	 *            Maximum amount of energy to receive.
	 * @param simulate
	 *            If TRUE, the charge will only be simulated.
	 * @return Amount of energy that was (or would have been, if simulated)
	 *         received.
	 */
	double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate);

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
