package com.tom.energy.multipart;

import static com.tom.api.energy.EnergyType.LV;

public class PartLVCable extends PartCable {
	public PartLVCable() {
		super("tomsmodenergy:cableLv", 0.1875, LV);
	}

	@Override
	public EnergyGrid constructGrid() {
		return new EnergyGrid(LV);
	}
}
