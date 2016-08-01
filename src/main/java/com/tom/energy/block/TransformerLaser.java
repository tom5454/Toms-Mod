package com.tom.energy.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.energy.tileentity.TileEntityTransformerLaser;

public class TransformerLaser extends BlockLaserBase {

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityTransformerLaser();
	}

}
