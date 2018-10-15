package com.tom.api.tileentity;

import net.minecraft.world.World;

public interface IJammer {

	World getWorld2();
	boolean isValid();
	boolean isAccessible(double x, double y, double z);

}
