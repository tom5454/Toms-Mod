package com.tom.core.tileentity;

import static com.tom.core.block.Antenna.STATE;

import net.minecraft.block.state.IBlockState;

import com.tom.api.tileentity.TileEntityAntennaBase;
import com.tom.apis.TomsModUtils;

public class TileEntityAntenna extends TileEntityAntennaBase{
	@Override
	public void updateEntity() {
		super.updateEntity();
		IBlockState state = worldObj.getBlockState(pos);
		int st = state.getValue(STATE);
		if(this.powered && this.redstone){
			if(this.online){
				if(st != 2)TomsModUtils.setBlockState(worldObj, pos, state.withProperty(STATE, 2));
			}else{
				if(st != 1)TomsModUtils.setBlockState(worldObj, pos, state.withProperty(STATE, 1));
			}
		}else{
			if(st != 0)TomsModUtils.setBlockState(worldObj, pos, state.withProperty(STATE, 0));
		}
	}
}
