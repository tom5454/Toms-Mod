package com.tom.energy.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.energy.tileentity.TileEntityCreativeCell;

public class CreativeCell extends BlockPowerStorageBase {

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCreativeCell();
	}

}
