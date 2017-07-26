package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.HV;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.Configs;

public class TileEntityFusionCharger extends TileEntityTomsMod implements IEnergyReceiver {
	private EnergyStorage energy = new EnergyStorage(Configs.ChargerMaxEnergy, Configs.InjectorMaxEnergyInput);

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == HV;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		/*int energyReceive = 0;
		//boolean canReceive = this.maxEnergy > this.energy;
		if(this.maxEnergy > this.energy && this.canConnectEnergy(from)){
			int canReceive = (this.energy + this.maxEnergyInput) < this.maxEnergy ? this.maxEnergyInput : this.maxEnergy - this.energy;
			energyReceive = maxReceive >= canReceive ? canReceive : maxReceive;
		}
		//if(canReceive){
		
		//}
		
		if(!simulate) this.energy = this.energy + energyReceive;*/
		return energy.receiveEnergy(maxReceive, simulate);
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
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		energy.readFromNBT(tag.getCompoundTag("Energy"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("Energy", energy.writeToNBT(new NBTTagCompound()));
		return tag;
	}

	public void disCharge() {
		this.energy.extractEnergy(Configs.ChargerUsage, false);
	}

	public double getEnergyStored() {
		return this.energy.getMaxEnergyStored();
	}

	public double getMaxEnergyStored() {
		return this.energy.getEnergyStored();
	}

	public boolean ready() {
		return (this.energy.getEnergyStored() - Configs.InjectorUsage) >= 0;
	}

	public void disCharge(double ammount) {
		this.energy.extractEnergy(ammount, false);
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return HV.getList();
	}
}
