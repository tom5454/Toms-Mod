package com.tom.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import com.tom.energy.tileentity.TileEntityEnergyStorage;
import com.tom.energy.tileentity.TileEntityHVBattery;

public class HVBattery extends BlockEnergyStorage {

	public HVBattery() {
		super(Material.IRON);
	}

	@Override
	public TileEntityEnergyStorage createNewTileEntity(World worldIn, int meta) {
		return new TileEntityHVBattery();
	}

}
