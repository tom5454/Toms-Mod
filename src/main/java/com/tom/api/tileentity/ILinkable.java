package com.tom.api.tileentity;

import net.minecraft.util.EnumFacing;

import com.tom.apis.ExtraBlockHitInfo;

public interface ILinkable {
	boolean link(int x, int y, int z, EnumFacing side, ExtraBlockHitInfo bhp, int dim);
}
