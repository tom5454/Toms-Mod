package com.tom.api.grid;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class BlockAccess {
	private final BlockPos pos;
	private final EnumFacing facing;

	public BlockAccess(BlockPos pos, EnumFacing facing) {
		this.pos = pos;
		this.facing = facing;
	}

	public BlockPos getPos() {
		return pos;
	}

	public EnumFacing getFacing() {
		return facing;
	}
}