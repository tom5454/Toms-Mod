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
import com.tom.handler.TMPlayerHandler;
import com.tom.lib.api.tileentity.IOwnable;
import com.tom.util.TomsModUtils;

public class TileEntityWaterCollector extends TileEntityTomsMod implements ITileFluidHandler, IOwnable {
	private final FluidTank tank;
	protected TMPlayerHandler playerHandler;
	public String playerName;

	public TileEntityWaterCollector() {
		tank = new FluidTank(1000);
		tank.setCanFill(false);
	}

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return f == EnumFacing.UP ? tank : null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
		compound.setString("placer", playerName);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tank.readFromNBT(compound.getCompoundTag("tank"));
		playerName = compound.getString("placer");
	}

	@Override
	public void updateEntity() {
		if (world.isRemote)
			return;
		int rs = world.isBlockIndirectlyGettingPowered(pos);
		if (rs > 0 && playerHandler != null && playerHandler.checkAndUseGridPower(1)) {
			IBlockState b = world.getBlockState(pos.down());
			if (b.getBlock() == Blocks.REDSTONE_BLOCK)
				rs = 16;
			int water = calcWaterBlocks();
			if (water > 0) {
				int a = water * rs;
				tank.fillInternal(new FluidStack(FluidRegistry.WATER, a), true);
			}
			if (tank.getFluidAmount() > 250) {
				EnumFacing f = EnumFacing.DOWN;
				TileEntity tile = world.getTileEntity(pos.offset(f.getOpposite()));
				if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f)) {
					int extra = Math.min(tank.getFluidAmount() - (tank.getCapacity() / 2), 80);
					IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f);
					if (t != null) {
						int filled = t.fill(new FluidStack(FluidRegistry.WATER, extra), false);
						if (filled > 0) {
							FluidStack drained = tank.drainInternal(filled, false);
							if (drained != null && drained.amount > 0) {
								int canDrain = Math.min(filled, Math.min(80, drained.amount));
								t.fill(tank.drainInternal(canDrain, true), true);
							}
						}
					}
				}
			}
		}
	}

	private boolean isWaterBlock(EnumFacing f) {
		IBlockState state = world.getBlockState(pos.offset(f));
		return state.getBlock() == Blocks.WATER;
	}

	private int calcWaterBlocks() {
		boolean n = isWaterBlock(EnumFacing.NORTH);
		boolean s = isWaterBlock(EnumFacing.SOUTH);
		boolean e = isWaterBlock(EnumFacing.EAST);
		boolean w = isWaterBlock(EnumFacing.WEST);
		return TomsModUtils.getAllTrues(n, s, e, w);
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
}
