package com.tom.factory.tileentity;

public class TileEntitySteamFurnaceAdv extends TileEntitySteamFurnaceBase {
	public static final int MAX_PROCESS_TIME = 70;

	@Override
	public String getName() {
		return "steamFurnaceAdv";
	}

	@Override
	public int getMaxProgressTime() {
		return MAX_PROCESS_TIME;
	}

	@Override
	public int getSteamUsage() {
		return 15;
	}
}