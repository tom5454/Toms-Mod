package com.tom.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.ICustomItemBlock;
import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;

public class BlockHardenedGlass extends Block implements ICustomItemBlock, IModelRegisterRequired {// BlockGlass
	private static final int MAX = 1;
	public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, MAX);

	public BlockHardenedGlass() {
		super(Material.GLASS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks
	 * for render
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state) {
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
	public ItemBlock createItemBlock() {
		ItemBlock i = new ItemBlock(this) {
			@Override
			public String getUnlocalizedName(ItemStack stack) {
				return super.getUnlocalizedName(stack) + (stack.getMetadata() % (MAX + 1));
			}

			@Override
			public int getMetadata(int damage) {
				return damage;
			}
		};
		i.setHasSubtypes(true);
		return i;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, meta % (MAX + 1));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood
	 * returns 4 blocks)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
	}
	@Override
	public void registerModels() {
		Item item = Item.getItemFromBlock(this);
		CoreInit.registerRender(item, 0, "tomsmodcore:hGlass");
		CoreInit.registerRender(item, 1, "tomsmodcore:hGlassE");
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(TYPE);
	}
}
