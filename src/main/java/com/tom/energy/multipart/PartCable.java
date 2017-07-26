package com.tom.energy.multipart;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.energy.IEnergyStorageHandler;
import com.tom.api.grid.IGridUpdateListener;
import com.tom.api.multipart.PartDuct;
import com.tom.apis.TomsModUtils;

import mcmultipart.api.multipart.MultipartHelper;

public abstract class PartCable extends PartDuct<EnergyGrid> implements IGridUpdateListener, IEnergyHandler {
	private final EnergyType etype;

	public PartCable(String type, double size, EnergyType etype) {
		super(type, size);
		this.etype = etype;
	}

	private List<IEnergyStorageHandler> rec = new ArrayList<>();

	@Override
	public void onGridReload() {
		grid.rec.removeAll(rec);
		rec.clear();
		for (EnumFacing f : EnumFacing.VALUES) {
			IEnergyStorageHandler in = EnergyType.getHandlerFrom(world, pos, f.getOpposite(), t -> isValidConnection(f, t));
			if (in != null) {
				rec.add(in);
				grid.rec.add(in);
			}
		}
	}

	@Override
	public void onGridPostReload() {
	}

	@Override
	public boolean isValidConnection(EnumFacing side, TileEntity tile) {
		if (tile != null) {
			IEnergyStorageHandler cap = tile.getCapability(EnergyType.ENERGY_HANDLER_CAPABILITY, side.getOpposite());
			return tile.hasCapability(EnergyType.ENERGY_HANDLER_CAPABILITY, side.getOpposite()) && cap != null && cap.canConnectEnergy(etype);
		} else
			return false;
	}

	@Override
	public int getPropertyValue(EnumFacing side) {
		return connects(side) || connectsInv(side) ? 1 : 0;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == etype && TomsModUtils.occlusionTest(MultipartHelper.getContainer(world, pos).orElse(null), this, BOXES[from.getOpposite().ordinal()]);
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return etype.getList();
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return this.canConnectEnergy(from, type) ? grid.getData().receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type, double maxExtract, boolean simulate) {
		return this.canConnectEnergy(from, type) ? grid.getData().extractEnergy(maxExtract, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return this.canConnectEnergy(from, type) ? grid.getData().getEnergyStored() : 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return this.canConnectEnergy(from, type) ? grid.getData().getMaxEnergyStored() : 0;
	}

	@Override
	public void onNeighborTileChange(boolean force) {
		super.onNeighborTileChange(force);
		if (!force) {
			onGridReload();
		}
	}
}
