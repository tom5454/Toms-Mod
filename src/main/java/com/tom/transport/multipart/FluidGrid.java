package com.tom.transport.multipart;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import com.tom.api.grid.GridBase;
import com.tom.api.grid.IGridDevice;
import com.tom.api.multipart.MultipartTomsMod;
import com.tom.apis.TomsModUtils;

public class FluidGrid extends GridBase<FluidTank, FluidGrid> {
	private FluidTank tank = new FluidTank(TANK_SIZE);
	public static final int TANK_SIZE = 2000;
	private FluidStack stackLast = null;
	@Override
	public FluidTank getData() {
		return tank;
	}

	@Override
	public FluidGrid importFromNBT(NBTTagCompound tag) {
		tank.readFromNBT(tag.getCompoundTag("tank"));
		return this;
	}

	@Override
	public void updateGrid(World world, IGridDevice<FluidGrid> master) {
		FluidStack fluid = tank.getFluid();
		if(fluid != null)fluid = fluid.copy();
		if(!TomsModUtils.areFluidStacksEqual(fluid, stackLast)){
			for(int i = 0;i<parts.size();i++){
				((MultipartTomsMod) parts.get(i)).sendUpdatePacket();
			}
		}
		stackLast = fluid;
	}

	@Override
	public void setData(FluidTank data) {
		tank = data;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
	}
}
