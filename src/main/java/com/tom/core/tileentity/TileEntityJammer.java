package com.tom.core.tileentity;

import net.minecraft.block.state.IBlockState;

import com.tom.api.tileentity.TileEntityJammerBase;
import com.tom.apis.TomsModUtils;

import com.tom.core.block.Jammer;

public class TileEntityJammer extends TileEntityJammerBase {
	@Override
	public void updateEntity() {
		super.updateEntity();
		IBlockState state = worldObj.getBlockState(pos);
		boolean act = state.getValue(Jammer.ACTIVE);
		if(this.active){
			if(!act) TomsModUtils.setBlockState(worldObj, pos, state.withProperty(Jammer.ACTIVE, true));
		}else{
			if(act) TomsModUtils.setBlockState(worldObj, pos, state.withProperty(Jammer.ACTIVE, false));
		}
	}
}
