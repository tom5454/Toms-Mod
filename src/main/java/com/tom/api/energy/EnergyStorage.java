package com.tom.api.energy;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Reference implementation of {@link IEnergyStorage}. Use/extend this or implement your own.
 * 
 * @author tom5454
 * 
 */
public class EnergyStorage implements IEnergyStorage {

	protected double energy;
	protected int capacity;
	protected double maxReceive;
	protected double maxExtract;

	public EnergyStorage(int capacity) {

		this(capacity, capacity);
	}

	public EnergyStorage(int capacity, double maxTransfer) {

		this(capacity, maxTransfer, maxTransfer);
	}

	public EnergyStorage(int capacity, double maxReceive, double maxExtract) {

		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}

	public EnergyStorage readFromNBT(NBTTagCompound nbt) {

		this.energy = nbt.getInteger("Energy");

		if (energy > capacity) {
			energy = capacity;
		}
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		if (energy < 0) {
			energy = 0;
		}
		nbt.setDouble("Energy", energy);
		return nbt;
	}

	public void setCapacity(int capacity) {

		this.capacity = capacity;

		if (energy > capacity) {
			energy = capacity;
		}
	}

	public void setMaxTransfer(double maxTransfer) {

		setMaxReceive(maxTransfer);
		setMaxExtract(maxTransfer);
	}

	public void setMaxReceive(double maxReceive) {

		this.maxReceive = maxReceive;
	}

	public void setMaxExtract(double maxExtract) {

		this.maxExtract = maxExtract;
	}

	public double getMaxReceive() {

		return maxReceive;
	}

	public double getMaxExtract() {

		return maxExtract;
	}

	/**
	 * This function is included to allow for server -&gt; client sync. Do not call this externally to the containing Tile Entity, as not all IEnergyHandlers
	 * are guaranteed to have it.
	 * 
	 * @param energy
	 */
	public void setEnergyStored(double energy) {

		this.energy = energy;

		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
	}

	/**
	 * This function is included to allow the containing tile to directly and efficiently modify the energy contained in the EnergyStorage. Do not rely on this
	 * externally, as not all IEnergyHandlers are guaranteed to have it.
	 * 
	 * @param energy
	 */
	public void modifyEnergyStored(double energy) {

		this.energy += energy;

		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
	}

	/* IEnergyStorage */
	@Override
	public double receiveEnergy(double maxReceive, boolean simulate) {

		double energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!simulate) {
			energy += energyReceived;
		}
		return energyReceived;
	}

	@Override
	public double extractEnergy(double maxExtract, boolean simulate) {

		double energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

		if (!simulate) {
			energy -= energyExtracted;
		}
		return energyExtracted;
	}

	@Override
	public double getEnergyStored() {

		return energy;
	}

	@Override
	public int getMaxEnergyStored() {

		return capacity;
	}

	@Override
	public boolean isFull() {
		return energy == capacity;
	}

	@Override
	public boolean hasEnergy() {
		return energy > 0;
	}

}
