package com.tom.storage.tileentity;

import com.tom.api.grid.StorageNetworkGrid;
import com.tom.api.grid.StorageNetworkGrid.IDevice;
import com.tom.api.grid.StorageNetworkGrid.IPowerDrain;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.storage.handler.NetworkState;

public abstract class TileEntityChannel extends TileEntityGridDeviceBase<StorageNetworkGrid> implements IPowerDrain, IDevice {
	private NetworkState active = NetworkState.OFF;

	@Override
	public StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid();
	}

	@Override
	public void setActive(NetworkState active) {
		this.active = active;
	}

	public NetworkState isActive() {
		return active;
	}

	public boolean isActiveForUpdate() {
		if (!active.fullyActive())
			return false;
		if (active.isActiveInTick(world.getTotalWorldTime()))
			return true;
		return false;
	}

	@Override
	public int getProcessingPower() {
		return 0;
	}

	@Override
	public int getMemoryUsage() {
		return 0;
	}
}
