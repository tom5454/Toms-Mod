package com.tom.factory.tileentity;

import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.TileEntityMultiblockPartBase;

public abstract class TileEntityMBPressurePortBase extends TileEntityMultiblockPartBase{
	public float pressure = 0;
	@Override
	public boolean isPlaceableOnSide() {
		return false;
	}

	@Override
	public MultiblockPartList getPartName() {
		return MultiblockPartList.PressurePort;
	}
	public abstract void useAir(int airAmount);
	public abstract boolean pressurized();
}
