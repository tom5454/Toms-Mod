package com.tom.api.grid;

import com.tom.storage.handler.StorageNetworkGrid;

public interface IDenseGridDevice extends IGridDevice<StorageNetworkGrid> {
	boolean isDenseGridDevice();
}
