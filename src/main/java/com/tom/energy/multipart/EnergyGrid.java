package com.tom.energy.multipart;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.grid.GridBase;
import com.tom.api.grid.IGridDevice;

public class EnergyGrid extends GridBase<EnergyStorage,EnergyGrid> {
	private EnergyStorage energy = new EnergyStorage(100000, 10000, 12000);
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
