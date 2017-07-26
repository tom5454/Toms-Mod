package com.tom.energy.tileentity;

import com.tom.api.energy.EnergyType;

public class TileEntityHVBattery extends TileEntityEnergyStorage {

	public TileEntityHVBattery() {
		super(EnergyType.HV, 10000000, 64000, "tm_hv_battery");
	}

}
