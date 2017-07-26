package com.tom.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.energy.tileentity.TileEntityHVReceiver;

public class HVLaserReceiver extends BlockLaserReceiver {

	public HVLaserReceiver() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityHVReceiver();
	}

}
