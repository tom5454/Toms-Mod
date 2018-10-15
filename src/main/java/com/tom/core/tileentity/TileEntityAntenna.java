package com.tom.core.tileentity;

import static com.tom.core.block.Antenna.STATE;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

import com.tom.api.tileentity.IAccessPoint;
import com.tom.api.tileentity.WirelessConnection;
import com.tom.handler.TMWorldHandler;
import com.tom.storage.handler.NetworkState;
import com.tom.storage.tileentity.TileEntityChannel;
import com.tom.util.TomsModUtils;

public class TileEntityAntenna extends TileEntityChannel implements IAccessPoint {
	@Override
	public void updateEntity() {
		if(!world.isRemote){
			IBlockState state = world.getBlockState(pos);
			int st = state.getValue(STATE);
			if (isActive().isPowered() && world.isBlockIndirectlyGettingPowered(pos) == 0) {
				if (isActive().fullyActive()) {
					if (st != 3)
						TomsModUtils.setBlockState(world, pos, state.withProperty(STATE, 3));
					TMWorldHandler.addAccessPoint(this);
				} else if (isActive() == NetworkState.LOADING_CHANNELS) {
					if (st != 2)
						TomsModUtils.setBlockState(world, pos, state.withProperty(STATE, 2));
				} else {
					if (st != 1)
						TomsModUtils.setBlockState(world, pos, state.withProperty(STATE, 1));
				}
			} else {
				if (st != 0)
					TomsModUtils.setBlockState(world, pos, state.withProperty(STATE, 0));
			}
		}
	}

	@Override
	public int getMaxRange() {
		return 64;
	}

	@Override
	public double getPowerDrained() {
		return 4;
	}

	@Override
	public int getPriority() {
		return -1;
	}
	@Override
	public boolean canConnectTo(EnumFacing f) {
		return f == EnumFacing.DOWN;
	}

	@Override
	public WirelessConnection getConnection() {
		return null;
	}

	@Override
	public boolean isAccessPointValid() {
		return isValid() && isActive().fullyActive();
	}

	@Override
	public boolean isAccessible(double x, double y, double z) {
		return isAccessPointValid() && pos.distanceSqToCenter(x, y, z) < getMaxRange()*getMaxRange();
	}

	@Override
	public String getName() {
		return "Antenna[" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "]";
	}

}
