package com.tom.factory.tileentity;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.TileEntityControllerBase;
import com.tom.core.CoreInit;

public class TileEntityCoolantTower extends TileEntityControllerBase {
	public TileEntityCoolantTower(){
		this.parts.add(MultiblockPartList.FluidPort);
	}
	private int fuel = 0;
	@Override
	public void updateEntityI() {
		TileEntityMBFluidPort fluidIn1 = this.getTileEntityList(true);
		TileEntityMBFluidPort fluidIn2 = this.getTileEntityList(true,1);
		TileEntityMBFluidPort fluidOutPort = this.getTileEntityList(false);
		if(fluidIn1 != null && fluidIn2 != null && fluidOutPort != null){
			Fluid f1 = fluidIn1.getFluid();
			Fluid f2 = fluidIn2.getFluid();
			if(f1 != null && f2 != null){
				boolean f1P = f1.equals(CoreInit.plasma),
						f1W = f1.equals(FluidRegistry.WATER),
						f2P = f2.equals(CoreInit.plasma),
						f2W = f2.equals(FluidRegistry.WATER);
				if(f1P ? f2W : (f1W ? f2P : false)){
					int pA = (f1P ? fluidIn1 : fluidIn2).getFluidAmmount();
					int wA = (f1W ? fluidIn1 : fluidIn2).getFluidAmmount();
					if((pA >= 1 || this.fuel > 0) && wA >= 2000){
						Fluid fOut = fluidOutPort.getFluid();
						int fOutA = fluidOutPort.getFluidAmmount();
						if(fOut == null || (fOut.equals(CoreInit.steam) && fOutA+2000 <= fluidOutPort.getMaxFluidAmount())){
							if(this.fuel == 0){
								if(pA >= 1){
									this.fuel = 100;
									(f1P ? fluidIn1 : fluidIn2).drain(1);
									(f1W ? fluidIn1 : fluidIn2).drain(1000);
									fluidOutPort.fill(1000, CoreInit.steam);
								}
							}else{
								if(this.fuel > 0){
									(f1W ? fluidIn1 : fluidIn2).drain(2000);
									fluidOutPort.fill(2000, CoreInit.steam);
									this.fuel--;
								}
							}
						}
					}
				}
			}
		}
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
		this.fuel = tag.getInteger("fuel");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("fuel", this.fuel);
		return tag;
	}
	public int getFuel(){
		return this.fuel;
	}

}
