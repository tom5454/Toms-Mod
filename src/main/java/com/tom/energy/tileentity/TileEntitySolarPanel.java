package com.tom.energy.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyProvider;
import com.tom.api.tileentity.TileEntityTomsMod;

public class TileEntitySolarPanel extends TileEntityTomsMod implements IEnergyProvider {
	private EnergyStorage energy = new EnergyStorage(10000, 50, 50);
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return from == EnumFacing.DOWN && type == EnergyType.LV;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return EnergyType.LV.getList();
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type, double maxExtract, boolean simulate) {
		return canConnectEnergy(from, type) ? energy.extractEnergy(maxExtract, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getMaxEnergyStored();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		energy.writeToNBT(compound);
		return compound;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound);
	}
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			if(worldObj.isDaytime()){
				int light = worldObj.getLightFor(EnumSkyBlock.SKY, pos.up());
				float biomeTemp = worldObj.getBiomeForCoordsBody(pos).getTemperature();
				double e = (light / 15) * 20000;
				double tempPer = biomeTemp * 0.2;
				e /= (0.8D + tempPer);
				//long div = ticks / 12000;
				long ticksCR = worldObj.getWorldTime() - 6000;
				long ticksC = ticksCR < 0 ? -ticksCR : ticksCR;
				double ticksM = (6000 / e) - (ticksC / e);
				energy.receiveEnergy(ticksM * 5, false);
			}
			EnergyType.LV.pushEnergyTo(worldObj, pos, EnumFacing.UP, energy, false);
		}
	}
}
