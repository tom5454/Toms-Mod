package com.tom.transport.multipart;

import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.multipart.PartDuct;
import com.tom.core.CoreInit;

public class PartSteamDuct extends PartFluidDuctBase {
	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return Helper.getFluidHandlerFromTank(grid.getData(), true, true, CoreInit.steam);
	}

	@Override
	protected byte canConnect(PartDuct<?> part, EnumFacing side) {
		return (byte) (part instanceof PartSteamDuct ? 1 : 0);
	}
}
