package com.tom.core.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.core.tileentity.TileEntityHiddenSR;

public class BlockHiddenRenderOldTESR extends BlockHiddenRenderOld {
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityHiddenSR();
	}

	@Override
	public String getName() {
		return "Block Hiddden TESR";
	}
}
