package com.tom.storage.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.storage.tileentity.TileEntityAssembler;

public class BlockAssembler extends BlockContainerTomsMod {

	public BlockAssembler() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityAssembler();
	}

}
