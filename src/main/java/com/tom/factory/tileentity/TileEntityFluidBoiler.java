package com.tom.factory.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.IHeatSource;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.factory.block.FluidBoiler;
import com.tom.handler.FuelHandler;
import com.tom.lib.Configs;

public class TileEntityFluidBoiler extends TileEntityTomsMod implements ITileFluidHandler, IHeatSource {
	private FluidTank tankWater = new FluidTank(Configs.BASIC_TANK_SIZE);
	private FluidTank tankSteam = new FluidTank(Configs.BASIC_TANK_SIZE * 2);
	private FluidTank tankFuel = new FluidTank(Configs.BASIC_TANK_SIZE);
	private double heat = 20;
	public int clientHeat;
	public static final int MAX_TEMP = 1000;
	private int burnTime = 0;

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return ITileFluidHandler.Helper.getFluidHandlerFromTanksWithPredicate(new FluidTank[]{tankWater, tankSteam, tankFuel}, new Object[]{FluidRegistry.WATER, CoreInit.steam.get(), FuelHandler.IS_FLUID_FUEL_PREDICATE}, new boolean[]{true, false, true}, new boolean[]{false, false, false});
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("water", tankWater.writeToNBT(new NBTTagCompound()));
		tag.setTag("steam", tankSteam.writeToNBT(new NBTTagCompound()));
		tag.setTag("fuel", tankFuel.writeToNBT(new NBTTagCompound()));
		tag.setDouble("heat", heat);
		tag.setInteger("burnTime", burnTime);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tankWater.readFromNBT(compound.getCompoundTag("water"));
		tankSteam.readFromNBT(compound.getCompoundTag("steam"));
		tankFuel.readFromNBT(compound.getCompoundTag("fuel"));
		heat = compound.getDouble("heat");
		burnTime = compound.getInteger("burnTime");
	}

	@Override
	public void updateEntity(IBlockState state) {
		if (world.isRemote)
			return;
		if (burnTime > 1 || tankFuel.getFluidAmount() > 4) {
			if (burnTime < 1) {
				FluidStack s = tankFuel.drainInternal(heat > MAX_TEMP - 1 ? 4 : 5, true);
				if (s != null) {
					burnTime += FuelHandler.getBurnTimeForFluid(s) / 20;
				}
			} else
				burnTime--;
			if (!state.getValue(FluidBoiler.ACTIVE)) {
				TomsModUtils.setBlockState(world, pos, state.withProperty(FluidBoiler.ACTIVE, true), 2);
				this.markDirty();
			}
			double increase = heat > 400 ? heat > 800 ? 0.06D : 0.08D : 0.1D;
			heat = Math.min(increase + heat, MAX_TEMP);
		} else {
			if (state.getValue(FluidBoiler.ACTIVE)) {
				TomsModUtils.setBlockState(world, pos, state.withProperty(FluidBoiler.ACTIVE, false), 2);
				this.markDirty();
			}
			heat = Math.max(heat - (heat / 550), 20);
		}
		if (heat > 120 && tankWater.getFluidAmount() > 100 && tankSteam.getFluidAmount() != tankSteam.getCapacity()) {
			int p = MathHelper.ceil((heat - 120) / 80);
			tankWater.drainInternal(p / 2, true);
			tankSteam.fillInternal(new FluidStack(CoreInit.steam.get(), p), true);
			heat -= 0.05D;
		}
		if (heat > 120 && world.getTotalWorldTime() % 2 == 0)
			tankWater.drainInternal(1, true);
		if (tankSteam.getFluidAmount() > tankSteam.getCapacity() / 2) {
			EnumFacing f = state.getValue(FluidBoiler.FACING);
			TileEntity tile = world.getTileEntity(pos.offset(f.getOpposite()));
			if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f)) {
				int extra = Math.min(tankSteam.getFluidAmount() - (tankSteam.getCapacity() / 2), 500);
				IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f);
				if (t != null) {
					int filled = t.fill(new FluidStack(CoreInit.steam.get(), extra), false);
					if (filled > 0) {
						FluidStack drained = tankSteam.drainInternal(filled, false);
						if (drained != null && drained.amount > 0) {
							int canDrain = Math.min(filled, Math.min(500, drained.amount));
							t.fill(tankSteam.drainInternal(canDrain, true), true);
						}
					}
				}
			}
		}
	}

	@Override
	public double getHeat() {
		return heat;
	}

	public FluidTank getTankWater() {
		return tankWater;
	}

	public FluidTank getTankSteam() {
		return tankSteam;
	}

	public FluidTank getTankFuel() {
		return tankFuel;
	}

	@Override
	public double getMaxHeat() {
		return MAX_TEMP;
	}

	@Override
	public double transferHeat(double temp, double res) {
		double[] r = IHeatSource.handleHeatLogic(heat, temp, 16, res, 1000);
		heat = r[0];
		return r[1];
	}
}