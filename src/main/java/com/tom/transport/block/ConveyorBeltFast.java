package com.tom.transport.block;

import net.minecraft.world.World;

import com.tom.transport.tileentity.TileEntityConveyorBase;
import com.tom.transport.tileentity.TileEntityConveyorFast;

public class ConveyorBeltFast extends ConveyorBeltBase {

	@Override
	public TileEntityConveyorBase createNewTileEntity(World worldIn, int meta) {
		return new TileEntityConveyorFast();
	}

}
