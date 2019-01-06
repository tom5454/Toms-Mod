package com.tom.energy.tileentity;

import com.tom.lib.api.energy.EnergyType;

public class TileEntityHVCapacitor extends TileEntityEnergyStorage {
	public TileEntityHVCapacitor() {
		super(EnergyType.HV, 2000000, 1000000, "tm_hv_capacitor");
	}
}
