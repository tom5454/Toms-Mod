package com.tom.energy.multipart;

import static com.tom.lib.api.energy.EnergyType.HV;

public class PartHVCable extends PartCable {

	public PartHVCable() {
		super("tomsmodenergy:cableHv", 0.1875, HV);
	}

	@Override
	public EnergyGrid constructGrid() {
		return new EnergyGrid(HV);
	}
}
