package com.tom.energy.block;

import com.tom.energy.tileentity.TileEntityLaserMK3;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public class MK3Laser extends BlockLaserBase {

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityLaserMK3();
	}

}
