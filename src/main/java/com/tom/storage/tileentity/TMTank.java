package com.tom.storage.tileentity;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;

import net.minecraftforge.fluids.FluidStack;

public abstract class TMTank extends TileEntityTomsMod implements ITileFluidHandler{
	public abstract FluidStack getStack();
	public abstract int getCapacity();
}
