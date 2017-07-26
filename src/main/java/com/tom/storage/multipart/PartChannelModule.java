package com.tom.storage.multipart;

import com.tom.api.multipart.PartModule;
import com.tom.storage.handler.NetworkState;
import com.tom.storage.handler.StorageNetworkGrid;
import com.tom.storage.handler.StorageNetworkGrid.IDevice;
import com.tom.storage.handler.StorageNetworkGrid.IPowerDrain;

public abstract class PartChannelModule extends PartModule<StorageNetworkGrid> implements IPowerDrain, IDevice {
	private NetworkState active = NetworkState.OFF;

	public NetworkState isActive() {
		return active;
	}

	@Override
	public void setActive(NetworkState active) {
		this.active = active;
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

	@Override
	public int getState() {
		return active.fullyActive() ? 2 : active.showChannels() ? 1 : 0;
	}

	@Override
	public final StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid();
	}

	@Override
	public void onGridReload() {
	}

	@Override
	public void onGridPostReload() {
	}
}
