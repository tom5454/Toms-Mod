package com.tom.api.energy;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Reference implementation of {@link IEnergyStorage}. Use/extend this or
 * implement your own.
 *
 * @author tom5454
 *
 */
public class EnergyStorage implements IEnergyStorage {
	protected double energy;
	protected int capacity;
	protected double maxReceive;
	protected double maxExtract;
	protected boolean dummy;
	private static final double DIGIT_LIMITER = 10000;

	public static final EnergyStorage DUMMY_STORAGE = new EnergyStorage(0).setDummy();

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
		this.energy = regulateValue(energy);
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (energy < 0) {
			energy = 0;
		}
		nbt.setDouble("Energy", regulateValue(energy));
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

	@Override
	public double getMaxReceive() {
		return maxReceive;
	}

	@Override
	public double getMaxExtract() {
		return maxExtract;
	}

	/**
	 * This function is included to allow for server -&gt; client sync. Do not
	 * call this externally to the containing Tile Entity, as not all
	 * IEnergyHandlers are guaranteed to have it.
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
		this.energy = regulateValue(energy);
	}

	/**
	 * This function is included to allow the containing tile to directly and
	 * efficiently modify the energy contained in the EnergyStorage. Do not rely
	 * on this externally, as not all IEnergyHandlers are guaranteed to have it.
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
		this.energy = regulateValue(energy);
	}

	/* IEnergyStorage */
	@Override
	public double receiveEnergy(double maxReceive, boolean simulate) {

		double energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!simulate) {
			energy += energyReceived;
			energy = regulateValue(energy);
		}
		return regulateValue(energyReceived);
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

	public static double regulateValue(double value) {
		long v = (long) Math.floor(value * DIGIT_LIMITER);
		return v / DIGIT_LIMITER;
	}

	public float getEnergyStoredPer() {
		return (float) (energy / capacity);
	}

	public EnergyStorage setDummy() {
		dummy = true;
		return this;
	}

	@Override
	public boolean isDummy() {
		return dummy;
	}

	public static double transfer(IEnergyStorage from, IEnergyStorage to, double maxTransfer, boolean simulate) {
		if (from == null || from.isDummy() || to == null || to.isDummy() || maxTransfer <= 0)
			return 0;
		double maxTransfer1 = Math.min(maxTransfer, Math.min(from.getMaxExtract(), to.getMaxReceive()));
		double extract1 = from.extractEnergy(maxTransfer1, true);
		if (extract1 > 0) {
			double receive1 = to.receiveEnergy(extract1, true);
			if (receive1 > 0) {
				if (simulate)
					return receive1;
				else
					return to.receiveEnergy(from.extractEnergy(receive1, false), false);
			}
		}
		return 0;
	}

	public IEnergyStorageHandler toCapability(boolean canInsert, boolean canExtract, EnergyType type) {
		return new EnergyStorageWrapper(this, canInsert, canExtract, type);
	}

	private static class EnergyStorageWrapper implements IEnergyStorageHandler {
		private EnergyType type;
		private IEnergyStorage storage;
		private boolean canInsert, canExtract;

		public EnergyStorageWrapper(IEnergyStorage storage, boolean canInsert, boolean canExtract, EnergyType type) {
			this.storage = storage;
			this.canInsert = canInsert;
			this.canExtract = canExtract;
			this.type = type;
		}

		@Override
		public double receiveEnergy(EnergyType type, double maxReceive, boolean simulate) {
			return canConnectEnergy(type) && canInsert ? storage.receiveEnergy(maxReceive, simulate) : 0;
		}

		@Override
		public double extractEnergy(EnergyType type, double maxExtract, boolean simulate) {
			return canConnectEnergy(type) && canExtract ? storage.extractEnergy(maxExtract, simulate) : 0;
		}

		@Override
		public double getEnergyStored(EnergyType type) {
			return canConnectEnergy(type) ? storage.getEnergyStored() : 0;
		}

		@Override
		public int getMaxEnergyStored(EnergyType type) {
			return canConnectEnergy(type) ? storage.getMaxEnergyStored() : 0;
		}

		@Override
		public List<EnergyType> getValidEnergyTypes() {
			return type.getList();
		}

		@Override
		public boolean canConnectEnergy(EnergyType type) {
			return type == this.type;
		}
	}
}
