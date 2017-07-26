package com.tom.energy.tileentity;

import com.tom.api.energy.EnergyType;

public class TileEntityMVBattery extends TileEntityEnergyStorage {

	public TileEntityMVBattery() {
		super(EnergyType.MV, 1000000, 10000, "tm_mv_battery");
	}

}
