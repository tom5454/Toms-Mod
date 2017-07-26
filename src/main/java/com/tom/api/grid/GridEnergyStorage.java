package com.tom.api.grid;

import com.tom.api.energy.EnergyStorage;
import com.tom.storage.handler.StorageNetworkGrid.IGridEnergyStorage;

public class GridEnergyStorage extends EnergyStorage implements IGridEnergyStorage {
	private final int priority;

	public GridEnergyStorage(int capacity, double maxReceive, double maxExtract, int priority) {
		super(capacity, maxReceive, maxExtract);
		this.priority = priority;
	}

	public GridEnergyStorage(int capacity, double maxTransfer, int priority) {
		super(capacity, maxTransfer);
		this.priority = priority;
	}

	public GridEnergyStorage(int capacity, int priority) {
		super(capacity);
		this.priority = priority;
	}

	@Override
	public int getPriority() {
		return priority;
	}

}
