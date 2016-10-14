package com.tom.energy.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.energy.tileentity.TileEntityEnergyCellMK3;


public class MK3Storage extends BlockPowerStorageBase {

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityEnergyCellMK3();
	}
	
}
