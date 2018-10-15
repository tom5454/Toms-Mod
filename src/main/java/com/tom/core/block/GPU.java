package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;

import com.tom.core.tileentity.TileEntityGPU;

public class GPU extends BlockContainerTomsMod {

	protected GPU(Material material) {
		super(material);
	}

	public GPU() {
		this(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}
	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityGPU();
	}

}
