package com.tom.energy.tileentity;

import java.util.List;

import net.minecraft.util.EnumFacing;

import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyProvider;
import com.tom.api.tileentity.TileEntityTomsMod;

public class TileEntityCreativeCell extends TileEntityTomsMod implements IEnergyProvider {
	private static final EnergyType[] TYPES = new EnergyType[]{EnergyType.LV, EnergyType.MV, EnergyType.HV};
	public byte outputSides = 0;

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return true;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return EnergyType.asList(TYPES);
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type, double maxExtract, boolean simulate) {
		return maxExtract;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return 1;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return 1;
	}

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			for (EnergyType type : TYPES) {
				for (EnumFacing f : EnumFacing.VALUES) {
					type.pushEnergyTo(world, pos, f, Integer.MAX_VALUE, Integer.MAX_VALUE, false);
				}
			}
		}
	}

	public boolean contains(EnumFacing side) {
		return (outputSides & (1 << side.ordinal())) != 0;
	}
}
