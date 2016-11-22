package com.tom.energy.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;

import com.tom.energy.block.FusionFluidExtractor;

public class TileEntityFusionFluidExtractor extends TileEntityTomsMod implements ITileFluidHandler{
	private final FluidTank tank;
	public TileEntityFusionFluidExtractor() {
		tank = new FluidTank(Configs.BASIC_TANK_SIZE);
		tank.setCanFill(false);
	}
	private boolean isValidOutputSide(EnumFacing current){
		IBlockState state = worldObj.getBlockState(pos);
		if(state.getBlock() != Blocks.AIR){
			EnumFacing ret = state.getValue(FusionFluidExtractor.FACING);
			boolean r = ret == current;
			return r;
		}else{
			return false;
		}
	}
	public void add(){
		this.add(1);
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		NBTTagCompound tankTag = new NBTTagCompound();
		tank.writeToNBT(tankTag);
		tag.setTag("Tank", tankTag);
		return tag;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		tank.readFromNBT(tag.getCompoundTag("Tank"));
	}
	public int getAmount(){
		return this.tank.getFluidAmount();
	}

	public void add(int a) {
		FluidStack fluid;
		if(this.tank.getFluid() != null){
			fluid = this.tank.getFluid();
			fluid.amount = fluid.amount + a;
		}else{
			if (CoreInit.plasma == null){
				System.err.println("ERROR: Plasma is null");
			}
			fluid = new FluidStack(CoreInit.plasma, a);
		}
		this.tank.fillInternal(fluid, true);
	}
	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return this.isValidOutputSide(f) ? tank : null;
	}
}
