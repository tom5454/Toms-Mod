package com.tom.factory.block;

import com.tom.api.block.BlockMultiblockPart;
import com.tom.factory.tileentity.TileEntityMBCompressor;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MultiblockCompressor extends BlockMultiblockPart {

	protected MultiblockCompressor(Material p_i45386_1_) {
		super(p_i45386_1_);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityMBCompressor();
	}
	public MultiblockCompressor(){
		this(Material.IRON);
	}

	@Override
	public void breakBlockI(World world, int x, int y, int z, IBlockState block) {

	}

	@Override
	public void onNeighborBlockChangeI(IBlockAccess world, int x, int y, int z, IBlockState b, Block block) {

	}
}
