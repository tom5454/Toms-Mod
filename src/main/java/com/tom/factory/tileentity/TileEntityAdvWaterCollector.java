package com.tom.factory.tileentity;

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
import com.tom.handler.TMPlayerHandler;
import com.tom.lib.api.tileentity.IOwnable;

public class TileEntityAdvWaterCollector extends TileEntityTomsMod implements ITileFluidHandler, IOwnable {
	private FluidTank tank = new FluidTank(8000){
		@Override
		protected void onContentsChanged() {
			if(getFluidAmount() < 8000){
				refillTank();
			}
		}
	};
	protected TMPlayerHandler playerHandler;
	public String playerName;
	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return tank;
	}
	private void refillTank() {
		int filled = 8000 - tank.getFluidAmount();
		if(playerHandler != null && playerHandler.checkAndUseGridPower(filled / 200 + 1)){
			tank.setFluid(new FluidStack(FluidRegistry.WATER, 8000));
		}
	}
	@Override
	public void updateEntity() {
		if(!world.isRemote && playerHandler != null){
			if(tank.getFluidAmount() < 8000)refillTank();
			else if(playerHandler.checkAndUseGridPower(1)){
				EnumFacing f = EnumFacing.DOWN;
				TileEntity tile = world.getTileEntity(pos.offset(f.getOpposite()));
				if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f)) {
					IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f);
					if (t != null) {
						int filled = t.fill(new FluidStack(FluidRegistry.WATER, 2000), false);
						if (filled > 0) {
							FluidStack drained = tank.drainInternal(filled, false);
							if (drained != null && drained.amount > 0) {
								int canDrain = Math.min(filled, Math.min(2000, drained.amount));
								t.fill(tank.drainInternal(canDrain, true), true);
							}
						}
					}
				}
			}
		}
	}
	@Override
	public String getOwnerName() {
		return playerName;
	}

	@Override
	public void onLoad() {
		tileOnLoad();
	}

	@Override
	public void updatePlayerHandler() {
		playerHandler = TMPlayerHandler.getPlayerHandlerForName(playerName);
	}

	@Override
	public void setOwner(String owner) {
		this.playerName = owner;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tank.readFromNBT(compound.getCompoundTag("tank"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
		return super.writeToNBT(compound);
	}
}
