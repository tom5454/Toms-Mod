package com.tom.factory.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.factory.tileentity.TileEntityPump;

public class BlockPump extends BlockMachineBase {

	public BlockPump() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityPump();
	}
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this);
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState();
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos,
			EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		return this.getDefaultState();
	}
}
