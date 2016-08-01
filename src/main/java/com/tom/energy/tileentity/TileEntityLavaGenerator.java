package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.LASER;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import net.minecraftforge.fluids.FluidTank;

import com.tom.api.ITileFluidHandler;
import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyProvider;
import com.tom.api.tileentity.ICustomMultimeterInformation;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;

import com.tom.energy.block.LavaGenerator;

public class TileEntityLavaGenerator extends TileEntityTomsMod implements
IEnergyProvider, ITileFluidHandler,ICustomMultimeterInformation {
	private EnergyStorage energy = new EnergyStorage(10000,100);
	private FluidTank tank = new FluidTank(2000);
	public int fuel = 0;
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return false;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return LASER.getList();
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type,
			double maxExtract, boolean simulate) {
		return energy.extractEnergy(maxExtract, simulate);
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getMaxEnergyStored();
	}

	//	@Override
	//	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
	//		return resource != null && resource.getFluid() == FluidRegistry.LAVA ? tank.fill(resource, doFill) : 0;
	//	}
	//
	//	@Override
	//	public FluidStack drain(EnumFacing from, FluidStack resource,
	//			boolean doDrain) {
	//		return null;
	//	}
	//
	//	@Override
	//	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
	//		return null;
	//	}
	//
	//	@Override
	//	public boolean canFill(EnumFacing from, Fluid fluid) {
	//		return fluid == FluidRegistry.LAVA;
	//	}
	//
	//	@Override
	//	public boolean canDrain(EnumFacing from, Fluid fluid) {
	//		return false;
	//	}
	//
	//	@Override
	//	public FluidTankInfo[] getTankInfo(EnumFacing from) {
	//		return new FluidTankInfo[]{tank.getInfo()};
	//	}

	@Override
	public List<ITextComponent> getInformation(List<ITextComponent> list) {
		if(fuel > 0)list.add(new TextComponentTranslation("tomsMod.chat.burnTime",fuel));
		return list;
	}
	@Override
	public void updateEntity(IBlockState state) {
		if(!worldObj.isRemote){
			if(this.fuel < 1 && this.tank.getFluid() != null && (!energy.isFull()) && this.tank.getFluidAmount() > 4){
				tank.drain(5, true);
				this.fuel = 25;
			}else if(fuel > 0){
				fuel--;
				energy.receiveEnergy(4.0, false);
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, LavaGenerator.ACTIVE, true);
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, LavaGenerator.ACTIVE, false);
			}
			if(this.energy.getEnergyStored() > 0){
				for(EnumFacing f : EnumFacing.VALUES){
					//	TileEntity receiver = worldObj.getTileEntity(pos.offset(f));
					//if(receiver instanceof IEnergyReceiver) {
					//System.out.println("send");
					EnumFacing fOut = f.getOpposite();
					//IEnergyReceiver recv = (IEnergyReceiver)receiver;
					LASER.pushEnergyTo(worldObj, pos, fOut, energy, false);
					//}
				}
			}
		}
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound);
		tank.readFromNBT(compound.getCompoundTag("tank"));
		this.fuel = compound.getInteger("fuel");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagCompound tankTag = new NBTTagCompound();
		energy.writeToNBT(compound);
		tank.writeToNBT(tankTag);
		compound.setInteger("fuel", fuel);
		compound.setTag("tank", tankTag);
		return compound;
	}

	@Override
	public net.minecraftforge.fluids.capability.IFluidHandler getTankOnSide(EnumFacing f) {
		return tank;
	}
}
