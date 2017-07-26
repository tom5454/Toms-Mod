package com.tom.api.energy;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import net.minecraft.util.EnumFacing;

import net.minecraftforge.energy.IEnergyStorage;

public class RFStorage implements IEnergyStorage {
	private BiFunction<Integer, Boolean, Integer> extract, insert;
	private Supplier<Integer> energy, maxEnergy;

	public RFStorage(IRFProvider p, EnumFacing side) {
		extract = (maxReceive, simulate) -> (int) p.extractRF(side, maxReceive, simulate);
		energy = () -> p.getEnergyStored(side);
		maxEnergy = () -> p.getMaxEnergyStored(side);
	}

	public RFStorage(IRFReceiver p, EnumFacing side) {
		insert = (maxReceive, simulate) -> (int) p.receiveRF(side, maxReceive, simulate);
		energy = () -> p.getEnergyStored(side);
		maxEnergy = () -> p.getMaxEnergyStored(side);
	}

	public RFStorage(IRFHandler p, EnumFacing side, boolean __) {
		insert = (maxReceive, simulate) -> (int) p.receiveRF(side, maxReceive, simulate);
		extract = (maxReceive, simulate) -> (int) p.extractRF(side, maxReceive, simulate);
		energy = () -> p.getEnergyStored(side);
		maxEnergy = () -> p.getMaxEnergyStored(side);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return insert != null ? insert.apply(maxReceive, simulate) : 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return extract != null ? extract.apply(maxExtract, simulate) : 0;
	}

	@Override
	public int getEnergyStored() {
		return energy.get();
	}

	@Override
	public int getMaxEnergyStored() {
		return maxEnergy.get();
	}

	@Override
	public boolean canExtract() {
		return extract != null;
	}

	@Override
	public boolean canReceive() {
		return insert != null;
	}

}