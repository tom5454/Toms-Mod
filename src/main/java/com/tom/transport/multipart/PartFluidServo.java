package com.tom.transport.multipart;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.multipart.PartModule;
import com.tom.lib.Configs;

public class PartFluidServo extends PartModule<FluidGrid> {

	@Override
	public void onGridReload() {

	}

	@Override
	public void onGridPostReload() {

	}

	@Override
	public FluidGrid constructGrid() {
		return new FluidGrid();
	}

	@Override
	public void updateEntityI() {
		if (!world.isRemote) {
			if (grid.getData().getFluid() == null || grid.getData().getCapacity() >= grid.getData().getFluidAmount()) {
				EnumFacing facing = getFacing();
				TileEntity tile = world.getTileEntity(pos.offset(facing));
				if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
					IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
					if (t != null) {
						FluidStack drained = t.drain(Configs.fluidDuctMaxExtract, false);
						if (drained != null && drained.amount > 0) {
							// if(t.canDrain(f, t.drain(1, false).getFluid())){
							int filled = grid.getData().fill(drained, false);
							if (filled > 0) {
								int canDrain = Math.min(filled, Math.min(Configs.fluidDuctMaxExtract, drained.amount));
								grid.getData().fill(t.drain(canDrain, true), true);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public int getState() {
		return 1;
	}
}
