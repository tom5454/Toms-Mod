package com.tom.energy.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.energy.tileentity.TileEntityLaserMK2;

public class MK2Laser extends BlockLaserBase {

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityLaserMK2();
	}

}
