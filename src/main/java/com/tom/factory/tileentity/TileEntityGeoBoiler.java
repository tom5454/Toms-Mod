package com.tom.factory.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.factory.block.BlockGeoBoiler;
import com.tom.lib.Configs;

public class TileEntityGeoBoiler extends TileEntityTomsMod implements ITileFluidHandler {

	private FluidTank tankWater = new FluidTank(MathHelper.floor_double(Configs.BASIC_TANK_SIZE * 2.5D));
	private FluidTank tankSteam = new FluidTank(Configs.BASIC_TANK_SIZE * 5);
	private FluidTank tankLava = new FluidTank(Configs.BASIC_TANK_SIZE);
	private double heat = 20;
	public int clientHeat;
	public static final int MAX_TEMP = 1600;
	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return ITileFluidHandler.Helper.getFluidHandlerFromTanks(new FluidTank[]{tankWater, tankSteam, tankLava}, new Fluid[]{FluidRegistry.WATER, CoreInit.steam, FluidRegistry.LAVA}, new boolean[]{true, false, true}, new boolean[]{false, false, false});
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("water", tankWater.writeToNBT(new NBTTagCompound()));
		tag.setTag("steam", tankSteam.writeToNBT(new NBTTagCompound()));
		tag.setTag("lava", tankLava.writeToNBT(new NBTTagCompound()));
		tag.setDouble("heat", heat);
		return tag;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tankWater.readFromNBT(compound.getCompoundTag("water"));
		tankSteam.readFromNBT(compound.getCompoundTag("steam"));
		tankLava.readFromNBT(compound.getCompoundTag("lava"));
		heat = compound.getDouble("heat");
	}
	@Override
	public void updateEntity(IBlockState state) {
		if(worldObj.isRemote)return;
		if(tankLava.getFluidAmount() > 2){
			tankLava.drainInternal(heat > MAX_TEMP-1 ? 2 : 3, true);
			if(!state.getValue(BlockGeoBoiler.ACTIVE)){
				TomsModUtils.setBlockState(worldObj, pos, state.withProperty(BlockGeoBoiler.ACTIVE, true), 2);
				this.markDirty();
			}
			double increase = heat > 400 ? heat > 800 ? heat > 1200 ? 0.18D : 0.2D : 0.28D : 0.35D;
			heat = Math.min(increase + heat, MAX_TEMP);
		}else{
			if(state.getValue(BlockGeoBoiler.ACTIVE)){
				TomsModUtils.setBlockState(worldObj, pos, state.withProperty(BlockGeoBoiler.ACTIVE, false), 2);
				this.markDirty();
			}
			heat = Math.max(heat - (heat / 550), 20);
		}
		if(heat > 130 && tankWater.getFluidAmount() > 100 && tankSteam.getFluidAmount() != tankSteam.getCapacity()){
			int p = MathHelper.ceiling_double_int((heat - 130) / 20);
			tankWater.drainInternal(p / 2, true);
			tankSteam.fillInternal(new FluidStack(CoreInit.steam, p), true);
			heat -= 0.07D;
		}
		if(tankSteam.getFluidAmount() > tankSteam.getCapacity() / 2){
			EnumFacing f = state.getValue(BlockGeoBoiler.FACING);
			TileEntity tile = worldObj.getTileEntity(pos.offset(f.getOpposite()));
			if(tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f)){
				int extra = Math.min(tankSteam.getFluidAmount() - (tankSteam.getCapacity() / 2), 1000);
				IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f);
				if(t != null){
					int filled = t.fill(new FluidStack(CoreInit.steam, extra), false);
					if(filled > 0){
						FluidStack drained = tankSteam.drainInternal(filled, false);
						if(drained != null && drained.amount > 0){
							int canDrain = Math.min(filled, Math.min(1000, drained.amount));
							t.fill(tankSteam.drainInternal(canDrain, true), true);
						}
					}
				}
			}
		}
	}
	public double getHeat() {
		return heat;
	}
	public FluidTank getTankWater() {
		return tankWater;
	}
	public FluidTank getTankSteam() {
		return tankSteam;
	}
	public FluidTank getTankLava() {
		return tankLava;
	}
}
