package com.tom.transport.multipart;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.grid.GridBase;
import com.tom.api.multipart.MultipartTomsMod;
import com.tom.lib.Configs;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.api.grid.IGridUpdateListener;
import com.tom.util.TomsModUtils;

public class FluidGrid extends GridBase<FluidTank, FluidGrid> {
	private FluidTank tank = new FluidTank(TANK_SIZE);
	public static final int TANK_SIZE = 2000;
	public List<IFluidHandler> rec = new ArrayList<>();
	//public Set<FluidTank> tanks = new HashSet<>();
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
		world.profiler.startSection("FluidGrid.updateGrid");
		FluidStack fluid = tank.getFluid();
		if (fluid != null)
			fluid = fluid.copy();
		world.profiler.startSection("pushFluid");
		if (fluid != null) {
			for (IFluidHandler t : rec) {
				if (tank.getFluidAmount() < 1)
					break;
				int filled = t.fill(tank.getFluid(), false);
				if (filled > 0) {
					FluidStack drained = tank.drain(filled, false);
					if (drained != null && drained.amount > 0) {
						int canDrain = Math.min(filled, Math.min(Configs.fluidDuctMaxInsert, drained.amount));
						t.fill(tank.drain(canDrain, true), true);
					}
				}
			}
		}
		world.profiler.endStartSection("sync");
		if (!TomsModUtils.areFluidStacksEqual(fluid, stackLast)) {
			for (int i = 0;i < parts.size();i++) {
				MultipartTomsMod part = ((MultipartTomsMod) parts.get(i));
				part.sendUpdatePacket();
			}
		}
		if (world.getTotalWorldTime() % 40 == 0)
			for (int i = 0;i < parts.size();i++) {
				MultipartTomsMod part = ((MultipartTomsMod) parts.get(i));
				if (part instanceof IGridUpdateListener) {
					((IGridUpdateListener) part).onGridReload();
				}
			}
		stackLast = fluid;
		world.profiler.endSection();
		world.profiler.endSection();
	}

	@Override
	public void setData(FluidTank data) {
		tank = data;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
	}

	public void addTank(FluidTank tank2) {
		//tanks.add(tank2);
	}
}
