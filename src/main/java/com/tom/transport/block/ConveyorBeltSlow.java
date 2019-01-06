package com.tom.transport.block;

import net.minecraft.world.World;

import com.tom.transport.tileentity.TileEntityConveyorBase;
import com.tom.transport.tileentity.TileEntityConveyorSlow;

public class ConveyorBeltSlow extends ConveyorBeltBase {

	@Override
	public TileEntityConveyorBase createNewTileEntity(World worldIn, int meta) {
		return new TileEntityConveyorSlow();
	}

	@Override
	public int getEPUse() {
		return 1;
	}

}
