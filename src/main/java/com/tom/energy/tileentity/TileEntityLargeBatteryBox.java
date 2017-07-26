package com.tom.energy.tileentity;

import com.tom.api.energy.EnergyType;

public class TileEntityLargeBatteryBox extends TileEntityEnergyStorage {

	public TileEntityLargeBatteryBox() {
		super(EnergyType.LV, 1000000, 5000, "tm_large_battery_box");
	}

}
