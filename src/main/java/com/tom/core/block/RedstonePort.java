package com.tom.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;

import com.tom.core.tileentity.TileEntityRedstonePort;

public class RedstonePort extends BlockContainerTomsMod {

	public RedstonePort() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setResistance(2F);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityRedstonePort();
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
		return ((TileEntityRedstonePort) world.getTileEntity(pos)).getComparatorOutput();
	}

	@Override
	public boolean canConnectRedstone(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing par5) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return ((TileEntityRedstonePort) world.getTileEntity(pos)).getOutput() ? 15 : 0;
	}

	@Override
	public boolean canProvidePower(IBlockState s) {
		return true;
	}

	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, EnumFacing s) {
		return true;
	}
}
