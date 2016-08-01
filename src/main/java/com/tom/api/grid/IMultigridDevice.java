package com.tom.api.grid;

import net.minecraft.util.EnumFacing;

public interface IMultigridDevice<G extends IGrid<?,G>> extends IGridDevice<G> {
	IGridDevice<?> getOtherGridDevice();
	EnumFacing getSide();
}
