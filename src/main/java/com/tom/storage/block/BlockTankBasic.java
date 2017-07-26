package com.tom.storage.block;

import net.minecraft.world.World;

import com.tom.lib.Configs;
import com.tom.storage.tileentity.TMTank;
import com.tom.storage.tileentity.TileEntityBasicTank;

public class BlockTankBasic extends BlockTankBase {

	@Override
	public TMTank createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBasicTank();
	}

	@Override
	public int getTankSize() {
		return Configs.BASIC_TANK_SIZE;
	}
}
