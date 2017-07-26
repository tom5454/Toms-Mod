package com.tom.factory.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.factory.tileentity.TileEntityAdvBlastFurnace;

public class BlockAdvBlastFurnace extends BlockBlastFurnace {

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityAdvBlastFurnace();
	}

}
