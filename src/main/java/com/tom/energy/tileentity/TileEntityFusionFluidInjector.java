package com.tom.energy.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.FluidTank;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.Configs;

public class TileEntityFusionFluidInjector extends TileEntityTomsMod implements ITileFluidHandler{
	private final FluidTank tank = new FluidTank(Configs.BASIC_TANK_SIZE);

	//	@Override
	//	public boolean canDrain(EnumFacing arg0, Fluid arg1) {
	//		return false;
	//	}
	//
	//	@Override
	//	public boolean canFill(EnumFacing from, Fluid fluid) {
	//		return (from == EnumFacing.DOWN | from == EnumFacing.UP) && fluid == CoreInit.fusionFuel && !(this.tank.getCapacity() == this.tank.getFluidAmount());
	//	}
	//
	//	@Override
	//	public FluidStack drain(EnumFacing arg0, FluidStack arg1, boolean arg2) {
	//		return null;
	//	}
	//
	//	@Override
	//	public FluidStack drain(EnumFacing arg0, int arg1, boolean arg2) {
	//		return null;
	//	}
	//
	//	@Override
	//	public int fill(EnumFacing from, FluidStack fluid, boolean doFill) {
	//		return this.canFill(from, fluid != null ? fluid.getFluid() : null) ? this.tank.fill(fluid, doFill) : 0;
	//	}
	//
	//	@Override
	//	public FluidTankInfo[] getTankInfo(EnumFacing from) {
	//		return (from == EnumFacing.DOWN | from == EnumFacing.UP) ? new FluidTankInfo[]{new FluidTankInfo(this.tank)} : null;
	//	}
	public boolean filled(){
		return this.tank.getFluid() != null && this.tank.getFluidAmount() > 9;
	}
	public void remove(int a){
		this.tank.drain(a, true);
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		NBTTagCompound tankTag = new NBTTagCompound();
		tank.writeToNBT(tankTag);
		tag.setTag("Tank", tankTag);
		//tag.setInteger("fluid", this.tank.getFluidAmount());
		return tag;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		tank.readFromNBT(tag.getCompoundTag("Tank"));
		//this.tank.drain(this.tank.getFluidAmount(), true);
		//this.tank.fill(new FluidStack(CoreInit.fusionFuel, tag.getInteger("fluid")), true);
	}
	public int getFluidAmount(){
		return this.tank.getFluidAmount();
	}
	@Override
	public net.minecraftforge.fluids.capability.IFluidHandler getTankOnSide(EnumFacing from) {
		return from == EnumFacing.DOWN || from == EnumFacing.UP ? tank : null;
	}

}
