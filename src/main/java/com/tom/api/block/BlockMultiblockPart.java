package com.tom.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.tileentity.TileEntityMultiblockPartBase;

public abstract class BlockMultiblockPart extends BlockContainerTomsMod implements IBlockMultiblockPart{

	protected BlockMultiblockPart(Material p_i45386_1_) {
		super(p_i45386_1_);
		this.setHardness(2F);
		this.setResistance(2F);
	}
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState block){
		TileEntity tilee = world.getTileEntity(pos);
		TileEntityMultiblockPartBase te = (TileEntityMultiblockPartBase)tilee;
		te.breakBlock();
		this.breakBlockI(world, pos.getX(),pos.getY(),pos.getZ(), block);
		super.breakBlock(world, pos,block);
	}
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn){
		TileEntity tilee = world.getTileEntity(pos);
		TileEntityMultiblockPartBase te = (TileEntityMultiblockPartBase)tilee;
		te.onNeighborBlockChange();
		this.onNeighborBlockChangeI(world, pos.getX(),pos.getY(),pos.getZ(), state, blockIn);
	}

}
