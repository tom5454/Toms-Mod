package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fluids.Fluid;

import com.tom.api.item.IFuelRod;
import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.TileEntityControllerBase;
import com.tom.core.CoreInit;

public class TileEntityFusionPreHeater extends TileEntityControllerBase {
	public TileEntityFusionPreHeater(){
		super(MultiblockPartList.AdvCasing);
		this.parts.add(MultiblockPartList.FuelRod);
		this.parts.add(MultiblockPartList.FluidPort);
		this.parts.add(MultiblockPartList.ItemHatch);
		this.parts.add(MultiblockPartList.EnergyPort);
		this.parts.add(MultiblockPartList.PressurePort);
	}
	private int fuel = -1;
	private ItemStack rodStack = null;
	@Override
	public void updateEntityI() {
		//System.out.println("updateEntity");
		int[][] itemInPorts = this.getItemPorts(true);
		int[][] itemOutPorts = this.getItemPorts(false);
		int[][] tePartEnergy = this.getTileEntityList(MultiblockPartList.EnergyPort);
		TileEntityMBFluidPort fluidIn1 = this.getTileEntityList(true);
		TileEntityMBFluidPort fluidIn2 = this.getTileEntityList(true,1);
		TileEntityMBPressurePortBase pPort = this.getPressurePort();
		TileEntityMBFluidPort fluidOutPort = this.getTileEntityList(false);
		TileEntityMBEnergyPort energyPort = tePartEnergy.length > 0 && tePartEnergy[0].length >= 3 ? (TileEntityMBEnergyPort) worldObj.getTileEntity(new BlockPos(tePartEnergy[0][0], tePartEnergy[0][1], tePartEnergy[0][2])) : null;
		TileEntityMBHatch hatchIn = itemInPorts.length > 0 && itemInPorts[0].length >= 3 ? (TileEntityMBHatch) worldObj.getTileEntity(new BlockPos(itemInPorts[0][0], itemInPorts[0][1], itemInPorts[0][2])) : null;
		TileEntityMBHatch hatchOut = itemOutPorts.length > 0 && itemOutPorts[0].length >= 3 ? (TileEntityMBHatch) worldObj.getTileEntity(new BlockPos(itemOutPorts[0][0], itemOutPorts[0][1], itemOutPorts[0][2])) : null;
		//TileEntityMBFluidPort fluidOutPort = fluidOutPorts.length > 0 && fluidOutPorts[0].length > 3 ? (TileEntityMBFluidPort) worldObj.getTileEntity(fluidOutPorts[0][0], fluidOutPorts[0][1], fluidOutPorts[0][2]) : null;
		//System.out.println(fluidIn1.xCoord + " " + fluidIn1.yCoord + " " + fluidIn1.zCoord);
		//System.out.println(fluidIn2.xCoord + " " + fluidIn2.yCoord + " " + fluidIn2.zCoord);
		//System.out.println(fluidOutPort);
		if(fluidIn1 != null && pPort != null && energyPort != null && hatchIn != null && hatchOut != null && fluidOutPort != null && fluidIn2 != null){
			Fluid f1 = fluidIn1.getFluid();
			Fluid f2 = fluidIn2.getFluid();
			//System.out.println("bF");
			if(f1 != null && f2 != null){
				boolean f1D = f1.equals(CoreInit.Deuterium),
						f1T = f1.equals(CoreInit.Tritium),
						f2D = f2.equals(CoreInit.Deuterium),
						f2T = f2.equals(CoreInit.Tritium);
				if(f1D ? f2T : (f1T ? f2D : false)){
					int dA = (f1D ? fluidIn1 : fluidIn2).getFluidAmmount();
					int tA = (f1T ? fluidIn1 : fluidIn2).getFluidAmmount();
					if(dA >= 10 && tA >= 10){
						double e = energyPort.getEnergyStored();
						float p = pPort.pressure;
						if(e > 450 && p > 10){
							if(this.fuel <= 0){
								int fuelOld = this.fuel;
								ItemStack[] stackIn = hatchIn.getStacks();
								ItemStack[] stackOut = hatchOut.getStacks();
								boolean in = false;
								boolean out = false;
								int inSlot = 0;
								int outSlot = 0;
								ItemStack inStack = null;
								ItemStack outStack = null;
								int i = 0;
								for(ItemStack currentStack : stackIn){
									if(currentStack != null && currentStack.getItem() instanceof IFuelRod){
										inSlot = i;
										in = true;
										inStack = currentStack;
										break;
									}
									i++;
								}
								i = 0;
								for(ItemStack currentStack : stackOut){
									if(currentStack == null || (currentStack.getItem() == (this.rodStack != null ? ((IFuelRod)this.rodStack.getItem()).getReturnStack(rodStack).getItem() : CoreInit.dUraniumRod) && currentStack.stackSize < 64)){
										outSlot = i;
										out = true;
										outStack = currentStack;
										break;
									}
									i++;
								}
								if(in && out && inStack != null){
									inStack.stackSize--;
									if(inStack.stackSize < 1) inStack = null;
									hatchIn.setInventorySlotContents(inSlot, inStack);
									this.fuel = ((IFuelRod)inStack.getItem()).getAmount(inStack);
									if(outStack == null) outStack = this.rodStack != null ? ((IFuelRod)this.rodStack.getItem()).getReturnStack(rodStack) : new ItemStack(CoreInit.dUraniumRod);
									else outStack.stackSize++;
									if(fuelOld == 0) hatchOut.setInventorySlotContents(outSlot, outStack);
									energyPort.removeEnergy(fuelOld == 0 ? 100 : 50, false);
								}else if(out){
									if(outStack == null) outStack = this.rodStack != null ? ((IFuelRod)this.rodStack.getItem()).getReturnStack(rodStack) : new ItemStack(CoreInit.dUraniumRod);
									else outStack.stackSize++;
									if(fuelOld == 0) hatchOut.setInventorySlotContents(outSlot, outStack);
									energyPort.removeEnergy(50, false);
									this.fuel = -1;
								}
							}else{
								Fluid fOut = fluidOutPort.getFluid();
								int fOutA = fluidOutPort.getFluidAmmount();
								if(fOut == null || (fOut.equals(CoreInit.fusionFuel) && fOutA+20 <= fluidOutPort.getMaxFluidAmount())){
									fluidIn1.drain(10);
									fluidIn2.drain(10);
									energyPort.removeEnergy(450, false);
									pPort.useAir(1000);
									this.fuel--;
									fluidOutPort.fill(20, CoreInit.fusionFuel);
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
		this.rodStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("rod"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("fuel", this.fuel);
		NBTTagCompound rodTag = new NBTTagCompound();
		if(this.rodStack != null) this.rodStack.writeToNBT(rodTag);
		tag.setTag("rod", rodTag);
		return tag;
	}
	public int getFuel(){
		return this.fuel;
	}

}
