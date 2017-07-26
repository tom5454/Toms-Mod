package com.tom.energy.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.FluidTank;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;

public class TileEntityFusionFluidInjector extends TileEntityTomsMod implements ITileFluidHandler {
	private final FluidTank tank = new FluidTank(Configs.BASIC_TANK_SIZE);

	public boolean filled() {
		return this.tank.getFluid() != null && this.tank.getFluidAmount() > 9;
	}

	public void remove(int a) {
		this.tank.drainInternal(a, true);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound tankTag = new NBTTagCompound();
		tank.writeToNBT(tankTag);
		tag.setTag("Tank", tankTag);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag.getCompoundTag("Tank"));
	}

	public int getFluidAmount() {
		return this.tank.getFluidAmount();
	}

	@Override
	public net.minecraftforge.fluids.capability.IFluidHandler getTankOnSide(EnumFacing from) {
		return from == EnumFacing.DOWN || from == EnumFacing.UP ? Helper.getFluidHandlerFromTank(tank, true, false, CoreInit.fusionFuel) : null;
	}

}
