package com.tom.transport.block;

import net.minecraft.world.World;

import com.tom.transport.tileentity.TileEntityConveyorExtract;
import com.tom.transport.tileentity.TileEntityConveyorOmniBase;

public class ConveyorBeltExtract extends ConveyorBeltOmniBase {

	@Override
	public TileEntityConveyorOmniBase createNewTileEntity(World worldIn, int meta) {
		return new TileEntityConveyorExtract();
	}
	@Override
	protected String getBaseModel() {
		return "tomsmodtransport:block/conveyor_extract";
	}
	@Override
	public int getEPUse() {
		return 10;
	}
}
