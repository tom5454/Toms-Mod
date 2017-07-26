package com.tom.factory.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.ICustomItemBlock;
import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;

public class BlockComponents extends Block implements IModelRegisterRequired, ICustomItemBlock {
	public static final AxisAlignedBB HALF = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);
	public static final AxisAlignedBB CUBE = new AxisAlignedBB(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);
	public static final PropertyEnum<ComponentVariants> VARIANT = PropertyEnum.<ComponentVariants>create("variant", ComponentVariants.class);

	public BlockComponents() {
		super(Material.IRON);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for (int i = 0;i < ComponentVariants.VALUES.length;i++)
			list.add(new ItemStack(itemIn, 1, i));
	}

	@Override
	public void registerModels() {
		Item item = Item.getItemFromBlock(this);
		String type = CoreInit.getNameForItem(item).replace("|", "");
		for (int i = 0;i < ComponentVariants.VALUES.length;i++)
			CoreInit.registerRender(item, i, type + "." + ComponentVariants.get(i).getName());
	}

	public static enum ComponentVariants implements IStringSerializable {
		REFINERY_HEATER(FULL_BLOCK_AABB), OUTPUT_HATCH(FULL_BLOCK_AABB), ENGINEERING_BLOCK(FULL_BLOCK_AABB), MACHINE_BASE(HALF), STEEL_SCAFFOLDING(CUBE), IRON_SHEETS(FULL_BLOCK_AABB), STEEL_SHEETS(FULL_BLOCK_AABB),;
		public static final ComponentVariants[] VALUES = values();
		private final AxisAlignedBB bb;

		private ComponentVariants(AxisAlignedBB bb) {
			this.bb = bb;
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		public static ComponentVariants get(int i) {
			return VALUES[MathHelper.abs(i % VALUES.length)];
		}

		public boolean isLadder() {
			return this == STEEL_SCAFFOLDING;
		}

		public AxisAlignedBB getBoundingBox() {
			return bb;
		}
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, ComponentVariants.get(meta));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}

	@Override
	public ItemBlock createItemBlock() {
		return new BlockComponentsItemBlock(this);
	}

	public static class BlockComponentsItemBlock extends ItemBlock {

		public BlockComponentsItemBlock(Block block) {
			super(block);
			setHasSubtypes(true);
		}

		@Override
		public String getUnlocalizedName(ItemStack stack) {
			return super.getUnlocalizedName(stack) + "." + ComponentVariants.get(stack.getMetadata()).getName();
		}

		@Override
		public int getMetadata(int damage) {
			return damage;
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}

	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return state.getValue(VARIANT).isLadder();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return state.getValue(VARIANT).getBoundingBox();
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
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
