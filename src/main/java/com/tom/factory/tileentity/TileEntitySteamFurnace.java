package com.tom.factory.tileentity;

public class TileEntitySteamFurnace extends TileEntitySteamFurnaceBase {
	public static final int MAX_PROCESS_TIME = 150;

	@Override
	public String getName() {
		return "steamFurnace";
	}

	@Override
	public int getMaxProgressTime() {
		return MAX_PROCESS_TIME;
	}

	@Override
	public int getSteamUsage() {
		return 8;
	}
}