package com.tom.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;

import com.tom.energy.tileentity.TileEntitySolarPanel;

public class BlockSolarPanel extends BlockContainerTomsMod {
	private static final AxisAlignedBB box = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D);
	public BlockSolarPanel() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySolarPanel();
	}
	@Override
	public boolean isOpaqueCube(IBlockState s) {
		return false;
	}
	@Override
	public boolean isFullCube(IBlockState s) {
		return false;
	}
	@Override
	public boolean isFullBlock(IBlockState s) {
		return false;
	}
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return box;
	}
}
