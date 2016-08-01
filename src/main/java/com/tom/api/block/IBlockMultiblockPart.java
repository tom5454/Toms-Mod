package com.tom.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IBlockMultiblockPart {
	/**breakBlock(World world, int x, int y, int z, IBlockState block)*/
	void breakBlockI(World world, int x, int y, int z, IBlockState block);
	/**onNeighborBlockChange(World world, int x, int y, int z, IBlockState b, Block block)*/
	void onNeighborBlockChangeI(IBlockAccess world, int x, int y, int z, IBlockState b, Block block);
}
