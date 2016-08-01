package com.tom.factory.tileentity;

/*import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import pneumaticCraft.api.tileentity.AirHandlerSupplier;
import pneumaticCraft.api.tileentity.IAirHandler;
import pneumaticCraft.api.tileentity.IPneumaticMachine;
import pneumaticCraft.api.tileentity.ISidedPneumaticMachine;

import com.tom.lib.Configs;

@SuppressWarnings("deprecation")*/
//@Optional.Interface(iface = "pneumaticCraft.api.tileentity.IPneumaticMachine", modid = Configs.PNEUMATICCRAFT)
public class TileEntityMBPressurePort extends TileEntityMBPressurePortBase /*implements IPneumaticMachine*/{
	/*private IAirHandler air = AirHandlerSupplier.getTierTwoAirHandler(Configs.multiblockPressurePortVolume);
	public TileEntity parentTile;
	public TileEntityMBPressurePort(){
		super();
		//this.air.setUpgradeSlots(null);
	}

	*/public boolean pressurized(){
		//return this.air.getPressure(ForgeDirection.UNKNOWN) >= 12;
		return false;
	}/*
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.masterX = tag.getInteger("mX");
		this.masterY = tag.getInteger("mY");
		this.masterZ = tag.getInteger("mZ");
		this.hasMaster = tag.getBoolean("hM");
		this.formed = tag.getBoolean("formed");
		this.air.readFromNBTI(tag.getCompoundTag("pneumatic"));
	}
	public void writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("mX", this.masterX);
		tag.setInteger("mY", this.masterY);
		tag.setInteger("mZ", this.masterZ);
		tag.setBoolean("hM", this.hasMaster);
		tag.setBoolean("formed", this.formed);
		NBTTagCompound tag2 = new NBTTagCompound();
		this.air.writeToNBTI(tag2);
		tag.setTag("pneumatic", tag2);
	}
	*/public float getPressure(){
		//return this.air.getPressure(ForgeDirection.UNKNOWN);
		return 0;
	}/*
	@Optional.Method(modid = Configs.PNEUMATICCRAFT)
	@Override
	public IAirHandler getAirHandler() {
		return this.air;
	}
	@Optional.Method(modid = Configs.PNEUMATICCRAFT)
	@Override
	public boolean isConnectedTo(ForgeDirection side) {
		 if(parentTile == null) {
	            return true;
	        } else if(parentTile instanceof IPneumaticMachine) {
	            return ((IPneumaticMachine)parentTile).isConnectedTo(side);
	        } else {
	            return ((ISidedPneumaticMachine)parentTile).getAirHandler(side) == this;
	        }
	}
	public void updateEntity(){
		this.air.updateEntityI();
		this.pressure = this.air.getPressure(ForgeDirection.DOWN);
	}
	public void validate(){
		this.air.validateI(this);
	}
	public void onNeighborChange(){
		this.air.onNeighborChange();
	}*/
	@Override
	public void formI(int mX, int mY, int mZ) {
		
	}
	@Override
	public void deFormI(int mX, int mY, int mZ) {
		
	}
	@Override
	public void useAir(int airAmount) {
		//this.air.addAir(-airAmount, ForgeDirection.DOWN);
	}
	
}
