package com.tom.energy.tileentity;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

import com.tom.api.energy.EnergyType;
import com.tom.api.energy.ILaserReceiver;
import com.tom.api.tileentity.TileEntityTomsMod;

import com.tom.energy.block.BlockLaserReceiver;

public class TileEntityLaserReceiver extends TileEntityTomsMod implements ILaserReceiver {
	public EnergyType type;

	public TileEntityLaserReceiver(EnergyType type) {
		this.type = type;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		EnumFacing facing = getFacing();
		return canConnectEnergy(from, type) ? type.pushEnergyTo(world, pos, facing, maxReceive, maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return 0;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		EnumFacing facing = getFacing();
		return facing.getAxis() == from.getAxis() && type == this.type;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return type.getList();
	}

	public EnumFacing getFacing() {
		IBlockState state = world.getBlockState(pos);
		return state.getValue(BlockLaserReceiver.FACING);
	}
}
