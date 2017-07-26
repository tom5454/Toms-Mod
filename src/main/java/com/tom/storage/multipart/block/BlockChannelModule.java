package com.tom.storage.multipart.block;

import net.minecraft.world.World;

import com.tom.api.multipart.BlockModuleBase;
import com.tom.storage.multipart.PartChannelModule;

public abstract class BlockChannelModule extends BlockModuleBase {
	public BlockChannelModule(double size, double deep) {
		super(size, deep, 2);
	}

	@Override
	public abstract PartChannelModule createNewTileEntity(World worldIn, int meta);
}
