package com.tom.api.tileentity;

import com.tom.apis.ExtraBlockHitInfo;

import net.minecraft.util.EnumFacing;

public interface ILinkable {
	boolean link(int x, int y, int z, EnumFacing side, ExtraBlockHitInfo bhp, int dim);
}
