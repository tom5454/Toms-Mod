package com.tom.transport.block;

import net.minecraft.world.World;

import com.tom.transport.tileentity.TileEntityConveyorOmniBase;
import com.tom.transport.tileentity.TileEntityConveyorOmniFast;

public class ConveyorBeltOmniFast extends ConveyorBeltOmniBase {

	@Override
	public TileEntityConveyorOmniBase createNewTileEntity(World worldIn, int meta) {
		return new TileEntityConveyorOmniFast();
	}

}
