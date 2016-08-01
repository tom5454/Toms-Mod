package com.tom.api.tileentity;

import com.tom.api.grid.IGrid;
import com.tom.api.grid.IGridDevice;

public interface ICable<G extends IGrid<?,G>> extends IGridDevice<G> {
	@Override
	public G getGrid();
}
