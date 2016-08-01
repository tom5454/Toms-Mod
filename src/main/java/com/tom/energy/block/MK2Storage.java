package com.tom.energy.block;

import com.tom.energy.tileentity.TileEntityEnergyCellMK2;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public class MK2Storage extends BlockPowerStorageBase {

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityEnergyCellMK2();
	}
	
}
