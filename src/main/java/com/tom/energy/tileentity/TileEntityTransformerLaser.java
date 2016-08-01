package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.HV;
import static com.tom.api.energy.EnergyType.LASER;

import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

import com.tom.api.energy.EnergyType;

public class TileEntityTransformerLaser extends TileEntityLaserBase {

	public TileEntityTransformerLaser() {
		super(200, "tm:misc/laserBeam3.png");
	}
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		EnumFacing facing = this.getFacing();
		return (type == LASER && facing != from) || (type == HV && (facing.getAxis() == Axis.Y ? from == facing.rotateAround(Axis.X).getOpposite() : from == EnumFacing.DOWN));
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return LASER.getList(HV);
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type,
			double maxReceive, boolean simulate) {
		return this.canConnectEnergy(from, type) && type == HV ? HV.convertFrom(LASER, this.energy.receiveEnergy(LASER.convertFrom(HV, maxReceive) , simulate)) : 0;
	}
	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return type == LASER ? this.energy.getEnergyStored() : 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return type == LASER ? this.energy.getMaxEnergyStored() : 0;
	}
}
