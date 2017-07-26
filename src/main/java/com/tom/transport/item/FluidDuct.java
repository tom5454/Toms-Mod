package com.tom.transport.item;

import net.minecraft.world.World;

import com.tom.api.multipart.BlockDuctBase;
import com.tom.api.multipart.PartDuct;
import com.tom.transport.multipart.PartFluidDuct;

public class FluidDuct extends BlockDuctBase {

	public FluidDuct() {
		super(2);
	}

	@Override
	public PartDuct<?> createNewTileEntity(World worldIn, int meta) {
		return new PartFluidDuct();
	}
}
