package com.tom.factory.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.factory.FactoryInit;

public class BlockSteelBoiler extends Block {
	public static final PropertyBool CONNECTED = PropertyBool.create("connected");

	public BlockSteelBoiler() {
		super(Material.IRON);
		this.setHardness(5);
		this.setResistance(10);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, CONNECTED);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(CONNECTED) ? 1 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(CONNECTED, meta == 1);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState statedown = worldIn.getBlockState(pos.down());
		return statedown.getBlock().onBlockActivated(worldIn, pos.down(), statedown, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		IBlockState stateUp = worldIn.getBlockState(pos.up());
		if (stateUp.getBlock() == FactoryInit.rubberBoiler) {
			if (!state.getValue(CONNECTED))
				worldIn.setBlockState(pos, state.withProperty(CONNECTED, true));
		} else {
			if (state.getValue(CONNECTED))
				worldIn.setBlockState(pos, state.withProperty(CONNECTED, false));
		}
	}
}
