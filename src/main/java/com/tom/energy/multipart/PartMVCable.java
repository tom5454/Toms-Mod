package com.tom.energy.multipart;

import static com.tom.lib.api.energy.EnergyType.MV;

public class PartMVCable extends PartCable {

	public PartMVCable() {
		super("tomsmodenergy:cableMv", 0.1875, MV);
	}

	@Override
	public EnergyGrid constructGrid() {
		return new EnergyGrid(MV);
	}
}
