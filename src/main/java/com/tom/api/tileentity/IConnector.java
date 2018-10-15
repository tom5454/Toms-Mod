package com.tom.api.tileentity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IConnector extends IReceivable {
	default int getLevel(){
		return 0;
	}
	default boolean locked(){
		return false;
	}
	BlockPos getPos2();
	World getWorld2();
	boolean isValid();
	boolean isAccessible(double x, double y, double z);
	String getName();
}
