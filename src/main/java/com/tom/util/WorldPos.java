package com.tom.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldPos {
	public final World world;
	public final BlockPos pos, pos2;
	public final int num1, num2;

	public WorldPos(World world, BlockPos pos, BlockPos pos2, int num1, int num2) {
		this.world = world;
		this.pos = pos;
		this.pos2 = pos2;
		this.num1 = num1;
		this.num2 = num2;
	}
}