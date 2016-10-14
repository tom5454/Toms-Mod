package com.tom.energy.tileentity;

import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.TileEntityControllerBase;
import com.tom.lib.Configs;

public class TileEntityEnergyCellCore extends TileEntityControllerBase {
	public TileEntityEnergyCellCore(){
		super(3,3,MultiblockPartList.EnergyCellCasing,2);
		this.parts.add(MultiblockPartList.EnergyCellMid);
	}
	/**Million*/
	protected double energyD = 0;

	@Override
	public void updateEntityI() {

	}

	@Override
	public void validateI() {

	}

	@Override
	public void receiveMessage(int x, int y, int z, byte msg) {

	}

	@Override
	public void formI(int mX, int mY, int mZ) {

	}

	@Override
	public void deFormI(int mX, int mY, int mZ) {

	}

	@Override
	public void updateEntity(boolean redstone) {

	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.energyD = tag.getDouble("energy");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setDouble("energy", this.energyD);
		return tag;
	}

	public double receiveEnergy(double energyReceive, boolean simulate) {
		if(this.energyD == Configs.EnergyCellCoreMax){
			return 0;
		}else{
			double eR = energyReceive >= 1000000 ? 1000000 : energyReceive;
			double eS = (Configs.EnergyCellCoreMax - this.energyD) * 1000000;
			double eR2 = eS >= eR ? eR : eS;
			if(!simulate){
				this.energyD = this.energyD + (eR2 / 1000000);
			}
			return eR2;
		}
	}

	public double extractEnergy(double energyExtract, boolean simulate) {
		if(this.energyD > 0 && this.getRedstone()){
			double eE = energyExtract >= 1000000 ? 1000000 : energyExtract;
			double e = this.energyD * 1000000;
			double eE2 = e >= eE ? eE : e;
			if(!simulate){
				this.energyD = this.energyD - (eE2 / 1000000);
			}
			return eE2;
		}
		return 0;
	}

	public double getEnergyStored(boolean input) {
		return this.getRedstone() ? (input ? (this.energyD == Configs.EnergyCellCoreMax ? 1000000 : 0) : this.getEnergy()) : 0;
	}
	private double getEnergy(){
		if(this.energyD > 1){
			return 1000000;
		}
		return this.energyD * 1000000;
	}

}
