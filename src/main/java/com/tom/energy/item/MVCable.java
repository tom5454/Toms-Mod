package com.tom.energy.item;

import net.minecraft.world.World;

import com.tom.api.multipart.PartDuct;
import com.tom.energy.multipart.PartMVCable;

public class MVCable extends BlockCableBase {

	@Override
	public PartDuct<?> createNewTileEntity(World worldIn, int meta) {
		return new PartMVCable();
	}
}
