package com.tom.transport.item;

import net.minecraft.world.World;

import com.tom.api.multipart.BlockModuleBase;
import com.tom.api.multipart.PartModule;
import com.tom.transport.multipart.PartFluidServo;

public class FluidServo extends BlockModuleBase {

	public FluidServo() {
		super(0.25, 0.25, 1);
	}

	@Override
	public PartModule<?> createNewTileEntity(World worldIn, int meta) {
		return new PartFluidServo();
	}
}
