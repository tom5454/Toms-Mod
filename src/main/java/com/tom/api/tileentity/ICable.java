package com.tom.api.tileentity;

import com.tom.lib.api.grid.IGrid;
import com.tom.lib.api.grid.IGridDevice;

public interface ICable<G extends IGrid<?, G>> extends IGridDevice<G> {
	@Override
	public G getGrid();
}
