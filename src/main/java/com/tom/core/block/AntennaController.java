package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;

import com.tom.core.tileentity.TileEntityAntennaController;

public class AntennaController extends BlockContainerTomsMod {

	protected AntennaController(Material p_i45386_1_) {
		super(p_i45386_1_);
	}

	public AntennaController() {
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityAntennaController();
	}

}
