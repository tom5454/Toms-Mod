package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fluids.FluidStack;

import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.TileEntityControllerBase;
import com.tom.apis.RecipeData;
import com.tom.recipes.handler.CentrifugeRecipeHandler;

public class TileEntityCentrifuge extends TileEntityControllerBase {
	public TileEntityCentrifuge(){
		super(3,4);
		this.parts.add(MultiblockPartList.EnergyPort);
		this.parts.add(MultiblockPartList.FluidPort);
		this.parts.add(MultiblockPartList.ItemHatch);
	}
	protected int processTime;
	protected int processTimeMax;
	@Override
	public void updateEntityI() {
		int[][] tePartEnergy = this.getTileEntityList(MultiblockPartList.EnergyPort);
		TileEntityMBFluidPort fInPort = this.getTileEntityList(true);
		int[][] fOutPort = this.getFluidOutput();
		int[][] itemInPorts = this.getItemPorts(true);
		int[][] itemOutPorts = this.getItemPorts(false);
		TileEntityMBEnergyPort energyPort = tePartEnergy.length > 0 && tePartEnergy[0].length >= 3 ? (TileEntityMBEnergyPort) worldObj.getTileEntity(new BlockPos(tePartEnergy[0][0], tePartEnergy[0][1], tePartEnergy[0][2])) : null;
		TileEntityMBHatch hatchIn = itemInPorts.length > 0 && itemInPorts[0].length >= 3 ? (TileEntityMBHatch) worldObj.getTileEntity(new BlockPos(itemInPorts[0][0], itemInPorts[0][1], itemInPorts[0][2])) : null;
		TileEntityMBHatch hatchOut = itemOutPorts.length > 0 && itemOutPorts[0].length >= 3 ? (TileEntityMBHatch) worldObj.getTileEntity(new BlockPos(itemOutPorts[0][0], itemOutPorts[0][1], itemOutPorts[0][2])) : null;
		if(energyPort != null && hatchIn != null && hatchOut != null && fInPort != null){
			double energy = energyPort.getEnergyStored();
			Object[] o = CentrifugeRecipeHandler.process(fInPort.getFluidStack(), hatchIn.getStacks(), energy);
			boolean processable = (Boolean) o[0];
			if(processable && this.processTime == 0 && energyPort != null){
				boolean isItem = (Boolean) o[12];
				boolean craft;
				if(isItem){
					craft = CentrifugeRecipeHandler.getItem(energy, itemOutPorts[0], itemOutPorts[1], itemOutPorts[2], itemOutPorts[3], 1, worldObj, (RecipeData) o[11]);
					if(craft){
						hatchOut.setInventorySlotContents(0, (ItemStack) o[3]);
						this.processTime = (Integer) o[2] + 1;
						this.processTimeMax = this.processTime;
						this.active = true;
					}
				}else{
					craft = CentrifugeRecipeHandler.getTank(fInPort.getFluidAmmount(),energy, fOutPort[0], fOutPort[1], fOutPort[2], fOutPort[3], 1, worldObj, (RecipeData) o[11]);
					if(craft){
						this.processTime = (Integer) o[2] + 1;
						this.processTimeMax = this.processTime;
						this.active = true;
						TileEntityMBFluidPort te1 = fOutPort.length > 0 && fOutPort[0].length >= 3 ? (TileEntityMBFluidPort) worldObj.getTileEntity(new BlockPos(fOutPort[0][0], fOutPort[0][1], fOutPort[0][2])) : null;
						TileEntityMBFluidPort te2 = fOutPort.length > 1 && fOutPort[1].length >= 3 ? (TileEntityMBFluidPort) worldObj.getTileEntity(new BlockPos(fOutPort[1][0], fOutPort[1][1], fOutPort[1][2])) : null;
						TileEntityMBFluidPort te3 = fOutPort.length > 2 && fOutPort[2].length >= 3 ? (TileEntityMBFluidPort) worldObj.getTileEntity(new BlockPos(fOutPort[2][0], fOutPort[2][1], fOutPort[2][2])) : null;
						TileEntityMBFluidPort te4 = fOutPort.length > 3 && fOutPort[3].length >= 3 ? (TileEntityMBFluidPort) worldObj.getTileEntity(new BlockPos(fOutPort[3][0], fOutPort[3][1], fOutPort[3][2])) : null;
						if(te1 != null && o[3] != null){
							FluidStack c = (FluidStack) o[3];
							te1.fill(c.amount, c.getFluid());
						}
						if(te2 != null && o[4] != null){
							FluidStack c = (FluidStack) o[4];
							te2.fill(c.amount, c.getFluid());
						}
						if(te3 != null && o[5] != null){
							FluidStack c = (FluidStack) o[5];
							te3.fill(c.amount, c.getFluid());
						}
						if(te4 != null && o[6] != null){
							FluidStack c = (FluidStack) o[6];
							te4.fill(c.amount, c.getFluid());
						}
						int in = (Integer) o[13];
						fInPort.drain(in);
					}
				}
				energyPort.setEnergy((Integer) o[1]);
			}
		}
		if(this.processTime > 0) this.processTime--;
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
		this.processTime = tag.getInteger("p");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("p", this.processTime);
		return tag;
	}


}
