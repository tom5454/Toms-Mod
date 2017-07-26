package com.tom.api.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.tileentity.TileEntityGridDeviceBase;

public abstract class BlockGridDevice extends BlockContainerTomsMod {

	public BlockGridDevice(Material material, MapColor mapColor) {
		super(material, mapColor);
	}

	public BlockGridDevice(Material material) {
		super(material);
	}

	@Override
	public BlockGridDevice setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}

	@Override
	public BlockGridDevice setUnlocalizedName(String name) {
		super.setUnlocalizedName(name);
		return this;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityGridDeviceBase) {
			try {
				((TileEntityGridDeviceBase<?>) tile).neighborUpdateGrid(false);
			} catch (Exception e) {
				// wrong tile
			}
		}
	}

	@Override
	public abstract TileEntityGridDeviceBase<?> createNewTileEntity(World worldIn, int meta);

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileEntityGridDeviceBase) {
			try {
				((TileEntityGridDeviceBase<?>) tile).neighborUpdateGrid(true);
			} catch (Exception e) {
				// wrong tile
			}
		}
		super.breakBlock(worldIn, pos, state);
	}
}
