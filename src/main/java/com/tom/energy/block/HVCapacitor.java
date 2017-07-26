package com.tom.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import com.tom.energy.tileentity.TileEntityEnergyStorage;
import com.tom.energy.tileentity.TileEntityHVCapacitor;

public class HVCapacitor extends BlockEnergyStorage {
	public HVCapacitor() {
		super(Material.IRON);
	}

	@Override
	public TileEntityEnergyStorage createNewTileEntity(World worldIn, int meta) {
		return new TileEntityHVCapacitor();
	}
}
