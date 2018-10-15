package com.tom.util;

import net.minecraft.util.EnumFacing;

public class Coords {
	public Coords(int x, int y, int z) {
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
	}

	public Coords(int x, int y, int c, String s) {
		this.xCoord = x;
		this.yCoord = y;
		this.c = c;
		this.s = s;
	}

	public Coords(double x, double y, double z, double yaw, double pitch) {
		this.xCoordD = x;
		this.yCoordD = y;
		this.zCoordD = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public int xCoord;
	public int yCoord;
	public int zCoord;
	public int c;
	public String s;
	public double yaw;
	public double pitch;
	public double xCoordD;
	public double yCoordD;
	public double zCoordD;
	public EnumFacing facing;

	public Coords setFacing(EnumFacing f) {
		this.facing = f;
		return this;
	}
}
