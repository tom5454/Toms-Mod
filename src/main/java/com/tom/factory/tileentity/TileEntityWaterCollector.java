package com.tom.factory.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;

public class TileEntityWaterCollector extends TileEntityTomsMod implements ITileFluidHandler {
	private final FluidTank tank = new FluidTank(1000);
	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return f == EnumFacing.UP ? tank : null;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
		return compound;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tank.readFromNBT(compound.getCompoundTag("tank"));
	}
	@Override
	public void updateEntity() {
		if(worldObj.isRemote)return;
		int rs = worldObj.isBlockIndirectlyGettingPowered(pos);
		if(rs > 0){
			IBlockState b = worldObj.getBlockState(pos.down());
			if(b.getBlock() == Blocks.REDSTONE_BLOCK)rs = 16;
			int water = calcWaterBlocks();
			if(water > 0){
				int a = water * rs;
				tank.fill(new FluidStack(FluidRegistry.WATER, a), true);
			}
			if(tank.getFluidAmount() > 250){
				EnumFacing f = EnumFacing.DOWN;
				TileEntity tile = worldObj.getTileEntity(pos.offset(f.getOpposite()));
				if(tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f)){
					int extra = Math.min(tank.getFluidAmount() - (tank.getCapacity() / 2), 80);
					IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f);
					if(t != null){
						int filled = t.fill(new FluidStack(FluidRegistry.WATER, extra), false);
						if(filled > 0){
							FluidStack drained = tank.drainInternal(filled, false);
							if(drained != null && drained.amount > 0){
								int canDrain = Math.min(filled, Math.min(80, drained.amount));
								t.fill(tank.drainInternal(canDrain, true), true);
							}
						}
					}
				}
			}
		}
	}
	private boolean isWaterBlock(EnumFacing f){
		IBlockState state = worldObj.getBlockState(pos.offset(f));
		return state.getBlock() == Blocks.WATER;
	}
	private int calcWaterBlocks(){
		boolean n = isWaterBlock(EnumFacing.NORTH);
		boolean s = isWaterBlock(EnumFacing.SOUTH);
		boolean e = isWaterBlock(EnumFacing.EAST);
		boolean w = isWaterBlock(EnumFacing.WEST);
		return TomsModUtils.getAllTrues(n, s, e, w);
	}
}
