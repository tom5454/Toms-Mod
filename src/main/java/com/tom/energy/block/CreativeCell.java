package com.tom.energy.block;

import com.tom.energy.tileentity.TileEntityCreativeCell;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CreativeCell extends BlockPowerStorageBase {

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCreativeCell();
	}

}
