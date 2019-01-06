package com.tom.energy.multipart;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.tom.api.multipart.MultipartTomsMod;
import com.tom.config.Config;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.lib.api.energy.EnergyType;
import com.tom.lib.api.energy.IEnergyStorageHandlerCapability;
import com.tom.lib.api.grid.GridBase;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.api.grid.IGridUpdateListener;

public class EnergyGrid extends GridBase<EnergyStorage, EnergyGrid> {
	private EnergyStorage energy;
	public List<IEnergyStorageHandlerCapability> rec = new ArrayList<>();
	private EnergyType type;

	public EnergyGrid(EnergyType etype) {
		int[] power = Config.getPowerValues(etype);
		energy = new EnergyStorage(power[0], power[1], power[2]);
		this.type = etype;
	}

	@Override
	public EnergyStorage getData() {
		return energy;
	}

	@Override
	public EnergyGrid importFromNBT(NBTTagCompound tag) {
		energy.readFromNBT(tag);
		return this;
	}

	@Override
	public void updateGrid(World world, IGridDevice<EnergyGrid> master) {
		world.profiler.startSection("EnergyGrid.updateGrid/pushEnergy");
		if (energy.hasEnergy() && !rec.isEmpty()) {
			double m = energy.getEnergyStored() / rec.size();
			rec.forEach(r -> type.pushEnergyTo(r, energy, m, false));
			if (energy.hasEnergy()) {
				rec.forEach(r -> type.pushEnergyTo(r, energy, false));
			}
		}
		world.profiler.endSection();
		if (world.getTotalWorldTime() % 40 == 0) {
			for (int i = 0;i < parts.size();i++) {
				MultipartTomsMod part = ((MultipartTomsMod) parts.get(i));
				if (part instanceof IGridUpdateListener) {
					((IGridUpdateListener) part).onGridReload();
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		energy.writeToNBT(tag);
	}

	@Override
	public void setData(EnergyStorage data) {
		this.energy = data;
	}
}
