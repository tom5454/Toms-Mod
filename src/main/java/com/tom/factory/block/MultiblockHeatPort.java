package com.tom.factory.block;

import com.tom.api.block.BlockMultiblockPart;
import com.tom.factory.tileentity.TileEntityMBHeatPort;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MultiblockHeatPort extends BlockMultiblockPart {

	protected MultiblockHeatPort(Material arg0) {
		super(arg0);
	}
	public MultiblockHeatPort(){
		this(Material.IRON);
	}
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileEntityMBHeatPort();
	}
	@Override
	public void breakBlockI(World world, int x, int y, int z, IBlockState block) {

	}
	/*@Override
	public void onNeighborBlockChangeI(World world, int x, int y, int z,
			IBlockState b, Block block) {
		/*TileEntityMBHeatPort tile = (TileEntityMBHeatPort) world.getTileEntity(new BlockPos(x, y, z));
		tile.getHeatExchangerLogic(null).initializeAsHull(world,x,y,z,new EnumFacing[]{EnumFacing.UP,EnumFacing.DOWN,EnumFacing.NORTH,EnumFacing.SOUTH,EnumFacing.EAST,EnumFacing.WEST});*/
	//}
	@Override
	public void onNeighborBlockChangeI(IBlockAccess world, int x, int y, int z, IBlockState b, Block block) {

	}

}
