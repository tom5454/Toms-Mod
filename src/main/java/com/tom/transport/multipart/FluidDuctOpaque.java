package com.tom.transport.multipart;

import net.minecraft.world.World;

import com.tom.api.multipart.PartDuct;
import com.tom.transport.item.FluidDuct;

public class FluidDuctOpaque extends FluidDuct {
	@Override
	public PartDuct<?> createNewTileEntity(World worldIn, int meta) {
		return new PartFluidDuctOpaque();
	}
}
