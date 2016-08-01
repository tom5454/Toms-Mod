package com.tom.api;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerFluidMap;

public interface ITileFluidHandler {
	IFluidHandler getTankOnSide(EnumFacing f);
	public static class Helper{
		public static IFluidHandler getFluidHandlerFromTank(FluidTank tank, Fluid fluid, boolean canFill, boolean canDrain){
			tank.setCanFill(canFill);
			tank.setCanDrain(canDrain);
			FluidHandlerFluidMap m = new FluidHandlerFluidMap();
			m.addHandler(fluid, tank);
			return m;
		}
		public static IFluidHandler getFluidHandlerFromTanks(FluidTank[] tanks, Fluid[] fluid, boolean[] canFill, boolean[] canDrain){
			FluidHandlerFluidMap m = new FluidHandlerFluidMap();
			for(int i = 0;i<tanks.length && i < fluid.length && i < canFill.length && i  < canDrain.length;i++){
				tanks[i].setCanDrain(canDrain[i]);
				tanks[i].setCanFill(canFill[i]);
				m.addHandler(fluid[i], tanks[i]);
			}
			return m;
		}
	}
}
