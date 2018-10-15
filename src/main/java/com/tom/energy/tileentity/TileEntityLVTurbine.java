package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.LV;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyProvider;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.core.CoreInit;

public class TileEntityLVTurbine extends TileEntityTomsMod implements ITileFluidHandler, IEnergyProvider {
	private EnergyStorage energy = new EnergyStorage(10000, 100);
	private FluidTank tank = new FluidTank(5000);

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == LV;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return LV.getList();
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type, double maxExtract, boolean simulate) {
		return canConnectEnergy(from, type) ? energy.extractEnergy(maxExtract, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return canConnectEnergy(from, type) ? energy.getEnergyStored() : 0;
	}

	@Override
	public long getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return canConnectEnergy(from, type) ? energy.getMaxEnergyStored() : 0;
	}

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return Helper.getFluidHandlerFromTank(tank, CoreInit.steam, true, false);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound);
		tank.readFromNBT(compound.getCompoundTag("tank"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		energy.writeToNBT(compound);
		compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
		return compound;
	}

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			if (tank.getFluidAmount() > 1000 && world.isBlockIndirectlyGettingPowered(pos) > 0) {
				tank.drainInternal(40, true);
				energy.receiveEnergy(20, false);
			}
			if (this.energy.getEnergyStored() > 0) {
				for (EnumFacing f : EnumFacing.VALUES) {
					// TileEntity receiver =
					// worldObj.getTileEntity(pos.offset(f));
					// if(receiver instanceof IEnergyReceiver) {
					// System.out.println("send");
					EnumFacing fOut = f.getOpposite();
					// IEnergyReceiver recv = (IEnergyReceiver)receiver;
					LV.pushEnergyTo(world, pos, fOut, energy, false);
					// }
				}
			}
		}
	}
}
