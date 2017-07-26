package com.tom.energy.tileentity;

import com.tom.api.energy.EnergyType;

public class TileEntityBatteryBox extends TileEntityEnergyStorage {
	public TileEntityBatteryBox() {
		super(EnergyType.LV, 100000, 1000, "tm_battery_box");
	}
}
