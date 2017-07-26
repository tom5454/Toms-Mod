package com.tom.core.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.core.CoreInit;
import com.tom.handler.AchievementHandler;

import com.tom.core.tileentity.TileEntityTreeTap;

public class BlockTreeTap extends BlockContainerTomsMod {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);

	public BlockTreeTap() {
		super(Material.WOOD);
		setResistance(0.5F);
		setHardness(0.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityTreeTap();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(CoreInit.treeTap);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return CoreInit.treeTap;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityTreeTap te = (TileEntityTreeTap) worldIn.getTileEntity(pos);
		if (!te.getBottleStack().isEmpty()) {
			InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), te.getBottleStack());
			te.setBottleStack(ItemStack.EMPTY);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntityTreeTap te = (TileEntityTreeTap) worldIn.getTileEntity(pos);
		ItemStack held = playerIn.getHeldItem(hand);
		int s = state.getValue(STATE);
		if (s == 2) {
			if (!worldIn.isRemote) {
				boolean added = playerIn.inventory.addItemStackToInventory(te.getBottleStack());
				if (added) {
					te.setBottleStack(ItemStack.EMPTY);
				}
				AchievementHandler.giveAchievement(playerIn, "rubber");
			}
			playerIn.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.4F, 0.5F);
		} else {
			if (s == 0) {
				if (!held.isEmpty() && held.getItem() == Items.GLASS_BOTTLE) {
					te.setBottleStack(held.splitStack(1));// EntityItem
					playerIn.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.4F, 0.5F);
				}
			}
		}
		// playerIn.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.4F, 0.5F);
		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return setBlockBounds(0.3F, 0.3F, 0.0F, 0.7F, 0.7F, 0.6F, state.getValue(FACING));
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING, STATE});
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean formed = (meta & 8) > 0;
		boolean isRight = (meta & 4) > 0;
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(STATE, formed ? isRight ? 2 : 1 : 0);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		boolean formed = state.getValue(STATE) > 0;
		boolean isRight = state.getValue(STATE) == 2;
		int i = 0;
		i = i | state.getValue(FACING).getHorizontalIndex();

		if (formed) {
			i |= 8;
		}

		if (isRight) {
			i |= 4;
		}

		return i;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
}
