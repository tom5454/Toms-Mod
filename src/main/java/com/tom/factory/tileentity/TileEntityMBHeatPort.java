package com.tom.factory.tileentity;

/*import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import pneumaticCraft.api.IHeatExchangerLogic;
import pneumaticCraft.api.PneumaticRegistry;
import pneumaticCraft.api.tileentity.IHeatExchanger;
*/
import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.TileEntityMultiblockPartBase;
/*import com.tom.lib.Configs;

@Optional.Interface(iface = "pneumaticCraft.api.tileentity.IHeatExchanger", modid = Configs.PNEUMATICCRAFT)*/
public class TileEntityMBHeatPort extends TileEntityMultiblockPartBase/* implements IHeatExchanger*/{
	/*public TileEntityMBHeatPort(){
	}
	protected IHeatExchangerLogic heat = PneumaticRegistry.getInstance().getHeatExchangerLogic();
	protected boolean firstRun = true;
	*/@Override
	public boolean isPlaceableOnSide() {
		return false;
	}

	@Override
	public MultiblockPartList getPartName() {
		return MultiblockPartList.HeatPort;
	}
	@Override
	public void formI(int mX, int mY, int mZ) {
		
	}

	@Override
	public void deFormI(int mX, int mY, int mZ) {
		
	}/*
	@Optional.Method(modid = Configs.PNEUMATICCRAFT)
	@Override
	public IHeatExchangerLogic getHeatExchangerLogic(ForgeDirection side) {
		return this.heat;
	}
	public void updateEntity(){
		if(firstRun && !worldObj.isRemote) {
			//this.heat.initializeAsHull(worldObj, xCoord, yCoord, zCoord, ForgeDirection.UP,ForgeDirection.DOWN, ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.SOUTH);
			this.heat.initializeAsHull(worldObj, xCoord, yCoord, zCoord, this.getConnectedHeatExchangerSides());
			this.heat.setThermalResistance(5);
			this.heat.setThermalCapacity(100);
		}
		this.firstRun = false;
		if(!worldObj.isRemote) this.heat.update();
	}
	public int getTemp(){
		return (int) this.heat.getTemperature()-275;
	}
	public void cool(int amount){
		this.heat.addHeat(-amount);
	}
	public void writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		NBTTagCompound h = new NBTTagCompound();
		this.heat.writeToNBT(h);
		tag.setTag("heat", h);
	}

    public void readFromNBT(NBTTagCompound tag){
    	super.readFromNBT(tag);
    	this.heat.readFromNBT(tag.getCompoundTag("heat"));
    }
    protected ForgeDirection[] getConnectedHeatExchangerSides(){
        return new ForgeDirection[]{ForgeDirection.UP,ForgeDirection.DOWN,ForgeDirection.NORTH,ForgeDirection.SOUTH,ForgeDirection.EAST,ForgeDirection.WEST};
    }*/
}
