package com.tom.defense.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.DamageSourceTomsMod;
import com.tom.defense.tileentity.TileEntityForceField;

public class BlockForceField extends BlockContainerTomsMod {
	public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 2);

	public BlockForceField() {
		super(Material.BARRIER);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityForceField();
	}

	/**
	 * Called When an Entity Collided with the Block
	 */
	/*@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		entityIn.attackEntityFrom(DamageSourceTomsMod.fieldDamage, 6.0F * (state.getValue(TYPE) == 2 ? 2 : 1));
	}*/
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		TileEntityForceField te = (TileEntityForceField) worldIn.getTileEntity(pos);
		te.update(worldIn);
	}

	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		entityIn.fall(fallDistance, 2.0F);
		IBlockState state = worldIn.getBlockState(pos);
		if (fallDistance > 5)
			entityIn.attackEntityFrom(DamageSourceTomsMod.fieldDamage, 4.0F * ((fallDistance - 4.5F) / 2) * (state.getValue(TYPE) == 2 ? 2 : 1));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(TYPE, meta % 3);
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
		entityIn.attackEntityFrom(DamageSourceTomsMod.fieldDamage, 6.0F * (state.getValue(TYPE) == 2 ? 2 : 1));
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		super.onEntityWalk(worldIn, pos, entityIn);
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getValue(TYPE) == 2)
			entityIn.attackEntityFrom(DamageSourceTomsMod.fieldDamage, 6.0F * (state.getValue(TYPE) == 2 ? 3 : 1));
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		IBlockState state = worldIn.getBlockState(pos);
		playerIn.attackEntityFrom(DamageSourceTomsMod.fieldDamage, 6.0F * (state.getValue(TYPE) == 2 ? 3 : 1));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return blockState.getValue(TYPE) == 2 ? new AxisAlignedBB(0.1, 0.1, 0.1, 0.9, 0.9, 0.9) : FULL_BLOCK_AABB;
	}

	/* @Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}*/
	@Override
	public boolean isOpaqueCube(IBlockState s) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState s) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState s) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
		Block block = iblockstate.getBlock();
		if (blockState != iblockstate) { return true; }

		if (block == this) { return false; }

		return block == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}
}
