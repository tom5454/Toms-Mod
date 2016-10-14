package com.tom.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.grid.IGridDevice;
import com.tom.api.tileentity.TileEntityGridDeviceBase;

public abstract class BlockGridDevice extends BlockContainerTomsMod {

	public BlockGridDevice(Material material, MapColor mapColor) {
		super(material, mapColor);
	}
	public BlockGridDevice(Material material) {
		super(material);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileEntityGridDeviceBase && !worldIn.isRemote){
			try{
				((TileEntityGridDeviceBase<?>)tile).constructGrid().forceUpdateGrid(worldIn, (IGridDevice)tile);
			}catch(Exception e){
				//wrong tile
			}
		}
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos,
			BlockPos neighbor) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityGridDeviceBase){
			try{
				((TileEntityGridDeviceBase<?>)tile).constructGrid().forceUpdateGrid(world, (IGridDevice)tile);
			}catch(Exception e){
				//wrong tile
			}
		}
	}
	@Override
	public abstract TileEntityGridDeviceBase<?> createNewTileEntity(World worldIn, int meta);
}
