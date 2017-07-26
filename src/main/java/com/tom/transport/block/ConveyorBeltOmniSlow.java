package com.tom.transport.block;

import net.minecraft.world.World;

import com.tom.transport.tileentity.TileEntityConveyorOmniBase;
import com.tom.transport.tileentity.TileEntityConveyorOmniSlow;

public class ConveyorBeltOmniSlow extends ConveyorBeltOmniBase {

	@Override
	public TileEntityConveyorOmniBase createNewTileEntity(World worldIn, int meta) {
		return new TileEntityConveyorOmniSlow();
	}

}
