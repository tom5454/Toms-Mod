package com.tom.storage.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.energy.IEnergyStorage;
import com.tom.api.grid.GridEnergyStorage;
import com.tom.storage.handler.StorageNetworkGrid.IGridEnergyStorage;
import com.tom.storage.handler.StorageNetworkGrid.PriorityComparator;

public class PowerCache implements IEnergyStorage {
	public static class WrappedPowerCache extends PowerCache {
		@Override
		public void setActive(boolean active) {
		}

		@Override
		public String toString() {
			return "W:" + super.toString();
		}
	}

	public PowerCache() {
		energy = new GridEnergyStorage(50, 0);
		energyStorages = new ArrayList<>();
		this.energyStorages.add(energy);
	}

	private List<IGridEnergyStorage> energyStorages;
	private GridEnergyStorage energy;
	private boolean active = true;

	public void addEnergyStorage(IGridEnergyStorage storage) {
		if (!energyStorages.contains(storage)) {
			energyStorages.add(storage);
			Collections.sort(energyStorages, new PriorityComparator());
		}
	}

	public void removeEnergyStorage(IGridEnergyStorage storage) {
		if (energyStorages.contains(storage)) {
			energyStorages.remove(storage);
			Collections.sort(energyStorages, new PriorityComparator());
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		energy.readFromNBT(tag);
	}

	public void writeToNBT(NBTTagCompound tag) {
		energy.writeToNBT(tag);
	}

	@Override
	public double receiveEnergy(double maxReceive, boolean simulate) {
		double received = 0;
		for (int i = 0;i < energyStorages.size();i++) {
			received += energyStorages.get(i).receiveEnergy(maxReceive - received, simulate);
			if (maxReceive == received)
				return received;
		}
		return received;
	}

	@Override
	public double extractEnergy(double maxExtract, boolean simulate) {
		double extracted = 0;
		for (int i = 0;i < energyStorages.size();i++) {
			extracted += energyStorages.get(i).extractEnergy(maxExtract - extracted, simulate);
			if (maxExtract == extracted)
				return extracted;
		}
		return extracted;
	}

	@Override
	public double getEnergyStored() {
		double stored = 0;
		for (int i = 0;i < energyStorages.size();i++) {
			stored += energyStorages.get(i).getEnergyStored();
		}
		return stored;
	}

	@Override
	public int getMaxEnergyStored() {
		int maxStored = 0;
		for (int i = 0;i < energyStorages.size();i++) {
			maxStored += energyStorages.get(i).getMaxEnergyStored();
		}
		return maxStored;
	}

	@Override
	public boolean isFull() {
		double stored = 0;
		int maxStored = 0;
		for (int i = 0;i < energyStorages.size();i++) {
			IEnergyStorage s = energyStorages.get(i);
			stored += s.getEnergyStored();
			maxStored += s.getMaxEnergyStored();
		}
		return stored == maxStored;
	}

	@Override
	public boolean hasEnergy() {
		return this.getEnergyStored() > 0;
	}

	@Override
	public double getMaxExtract() {
		return 100000;
	}

	@Override
	public double getMaxReceive() {
		return 100000;
	}

	@Override
	public String toString() {
		return getEnergyStored() + "U: " + energyStorages.toString();
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}
}