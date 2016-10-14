package com.tom.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;

import com.tom.energy.tileentity.TileEntityMVTurbine;

public class MVSteamTurbinbe extends BlockContainerTomsMod {

	public MVSteamTurbinbe() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMVTurbine();
	}

}
