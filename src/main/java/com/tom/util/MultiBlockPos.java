package com.tom.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class MultiBlockPos extends BlockPos {
	private BlockPos other;
	private int id;

	public MultiBlockPos(int x, int y, int z) {
		super(x, y, z);
	}

	public MultiBlockPos(double x, double y, double z) {
		super(x, y, z);
	}

	public MultiBlockPos(Entity source) {
		super(source.posX, source.posY, source.posZ);
	}

	public MultiBlockPos(Vec3d vec) {
		super(vec.x, vec.y, vec.z);
	}

	public MultiBlockPos(Vec3i source) {
		super(source.getX(), source.getY(), source.getZ());
	}

	public MultiBlockPos setOther(int x, int y, int z) {
		other = new BlockPos(x, y, z);
		return this;
	}

	public MultiBlockPos setOther(double x, double y, double z) {
		other = new BlockPos(x, y, z);
		return this;
	}

	public MultiBlockPos setOther(Entity source) {
		other = new BlockPos(source.posX, source.posY, source.posZ);
		return this;
	}

	public MultiBlockPos setOther(Vec3d vec) {
		other = new BlockPos(vec.x, vec.y, vec.z);
		return this;
	}

	public MultiBlockPos setOther(Vec3i source) {
		other = new BlockPos(source.getX(), source.getY(), source.getZ());
		return this;
	}

	public MultiBlockPos setId(int id) {
		this.id = id;
		return this;
	}

	public BlockPos getOther() {
		return other;
	}

	public int getId() {
		return id;
	}

	@Override
	public BlockPos toImmutable() {
		return new BlockPos(this);
	}
}