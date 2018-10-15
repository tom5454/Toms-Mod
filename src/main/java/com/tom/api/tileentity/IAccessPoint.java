package com.tom.api.tileentity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAccessPoint {
	int getMaxRange();
	BlockPos getPos2();
	boolean isAccessPointValid();
	boolean isAccessible(double x, double y, double z);
	World getWorld2();
	WirelessConnection getConnection();
	String getName();
}
