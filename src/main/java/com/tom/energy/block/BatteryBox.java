package com.tom.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import com.tom.energy.tileentity.TileEntityBatteryBox;
import com.tom.energy.tileentity.TileEntityEnergyStorage;

public class BatteryBox extends BlockEnergyStorage {
	public BatteryBox() {
		super(Material.WOOD);
	}

	@Override
	public TileEntityEnergyStorage createNewTileEntity(World world, int meta) {
		return new TileEntityBatteryBox();
	}
}
