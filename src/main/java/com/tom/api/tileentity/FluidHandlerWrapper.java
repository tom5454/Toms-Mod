package com.tom.api.tileentity;

import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import com.tom.api.ITileFluidHandler;

public class FluidHandlerWrapper implements IFluidHandler {
	EnumFacing f;
	ITileFluidHandler h;

	public FluidHandlerWrapper(EnumFacing f, ITileFluidHandler h) {
		this.h = h;
		this.f = f;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		IFluidHandler h = this.h.getTankOnSide(f);
		return h != null ? h.getTankProperties() : null;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		IFluidHandler h = this.h.getTankOnSide(f);
		return h != null ? h.fill(resource, doFill) : 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		IFluidHandler h = this.h.getTankOnSide(f);
		return h != null ? h.drain(resource, doDrain) : null;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		IFluidHandler h = this.h.getTankOnSide(f);
		return h != null ? h.drain(maxDrain, doDrain) : null;
	}

}
