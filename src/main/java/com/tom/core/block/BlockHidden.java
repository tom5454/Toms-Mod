package com.tom.core.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.MultiBlockPos;

import com.tom.core.tileentity.TileEntityHidden;

public class BlockHidden extends BlockContainerTomsMod {

	public BlockHidden() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityHidden();
	}
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntityHidden te = (TileEntityHidden) worldIn.getTileEntity(pos);
		if(te.master != null){
			IBlockState masterState = worldIn.getBlockState(te.master);
			return masterState.getBlock().onBlockActivated(worldIn, new MultiBlockPos(te.master).setOther(pos).setId(te.id), masterState, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
		}
		return false;
	}
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityHidden te = (TileEntityHidden) worldIn.getTileEntity(pos);
		if(te.drop != null)spawnAsEntity(worldIn, pos, te.drop);
		if(te.master != null){
			IBlockState masterState = worldIn.getBlockState(te.master);
			if(masterState.getBlock() instanceof IBreakingDetector){
				IBreakingDetector d = (IBreakingDetector) masterState.getBlock();
				d.slaveBroken(worldIn, te.master, pos, masterState);
			}
		}
		super.breakBlock(worldIn, pos, state);
	}
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		TileEntityHidden te = (TileEntityHidden) world.getTileEntity(pos);
		return te.pick;
	}
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}
	public static interface IBreakingDetector {
		void slaveBroken(World world, BlockPos pos, BlockPos brokenPos, IBlockState state);
	}
	@Override
	public int getRenderType() {
		return -1;
	}
	@Override
	public boolean isOpaqueCube(IBlockState state) {
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
}
