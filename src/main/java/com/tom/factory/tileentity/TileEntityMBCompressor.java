package com.tom.factory.tileentity;

import static com.tom.api.energy.EnergyType.HV;

import java.util.List;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityMBCompressor extends TileEntityMBPressurePortBase implements IEnergyReceiver{
	protected EnergyStorage energy = new EnergyStorage(1000000, 1200, 100);
	private int i = 0;
	@Override
	public void formI(int mX, int mY, int mZ) {

	}

	@Override
	public void deFormI(int mX, int mY, int mZ) {

	}

	@Override
	public void useAir(int airAmount) {
		this.pressure = this.pressure - (airAmount/1000);
	}

	@Override
	public boolean pressurized() {
		return this.pressure >= 12;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == HV;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive,
			boolean simulate) {
		return this.canConnectEnergy(from, type) ? this.energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getMaxEnergyStored();
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setTag("energy",this.energy.writeToNBT(new NBTTagCompound()));
		return tag;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.energy.readFromNBT(tag.getCompoundTag("energy"));
	}
	@Override
	public void updateEntity(){
		if(this.pressure < 12 && this.energy.extractEnergy(100, true) >= 100){
			if(this.i == 0) this.pressure = this.pressure + 0.1F;
			this.energy.extractEnergy(100, false);
			this.i = 6;
		}
		if(this.i > 0)this.i--;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return HV.getList();
	}

}
