package com.tom.factory.tileentity;

public class TileEntityAdvBlastFurnace extends TileEntityBlastFurnace {
	@Override
	protected void updateProgress() {
		if (world.getTotalWorldTime() % 2 == 0 && burnTime > 2) {
			progress -= 2;
			burnTime -= 2;
		} else {
			progress--;
			burnTime--;
		}
	}
}
