package com.tom.transport.block;

import net.minecraft.world.World;

import com.tom.api.multipart.BlockDuctBase;
import com.tom.api.multipart.PartDuct;
import com.tom.transport.multipart.PartSteamDuct;

public class SteamDuct extends BlockDuctBase {
	public SteamDuct() {
		super(2);
	}

	@Override
	public PartDuct<?> createNewTileEntity(World worldIn, int meta) {
		return new PartSteamDuct();
	}
}
