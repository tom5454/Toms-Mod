package com.tom.transport.block;

import java.util.List;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;
import com.tom.transport.tileentity.TileEntityConveyorBase;
import com.tom.transport.tileentity.TileEntityConveyorBeltSlope;
import com.tom.util.TomsModUtils;

public class ConveyorBeltSlope extends ConveyorBeltBase implements IModelRegisterRequired {
	public static final AxisAlignedBB AABB_BOTTOM = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
	public static final PropertyBool IS_DOWN_SLOPE = PropertyBool.create("down");

	@Override
	public TileEntityConveyorBase createNewTileEntity(World worldIn, int meta) {
		return new TileEntityConveyorBeltSlope();
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, TomsModUtils.getDirectionFacing(placer, false)).withProperty(IS_DOWN_SLOPE, placer.getHeldItem(hand).getMetadata() == 1);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, IS_DOWN_SLOPE, FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex() + (state.getValue(IS_DOWN_SLOPE) ? 6 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getFront(meta % 6);
		if (facing.getAxis() == Axis.Y)
			facing = EnumFacing.NORTH;
		return this.getDefaultState().withProperty(FACING, facing).withProperty(IS_DOWN_SLOPE, meta > 5);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	public class SlopeItemBlock extends ItemBlock {
		public SlopeItemBlock() {
			super(ConveyorBeltSlope.this);
			this.setMaxDamage(0);
			this.setHasSubtypes(true);
		}

		/**
		 * Converts the given ItemStack damage value into a metadata value to be
		 * placed in the world when this Item is placed as a Block (mostly used
		 * with ItemBlocks).
		 */
		@Override
		public int getMetadata(int damage) {
			return damage;
		}

		/**
		 * Returns the unlocalized name of this item. This version accepts an
		 * ItemStack so different stacks can have different names based on their
		 * damage or NBT.
		 */
		@Override
		public String getUnlocalizedName(ItemStack stack) {
			return super.getUnlocalizedName() + "." + (stack.getMetadata() == 1 ? "down" : "up");
		}
	}

	@Override
	public void registerModels() {
		CoreInit.registerRender(Item.getItemFromBlock(this), 0, "tomsmodtransport:conveyorSlopeUp");
		CoreInit.registerRender(Item.getItemFromBlock(this), 1, "tomsmodtransport:conveyorSlopeDown");
	}

	@Override
	public ItemBlock createItemBlock() {
		return new SlopeItemBlock();
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB bbIn, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean bool) {
		AxisAlignedBB mask = bbIn.offset(-pos.getX(), -pos.getY(), -pos.getZ());
		if (mask.intersects(AABB_BOTTOM))
			collidingBoxes.add(AABB_BOTTOM.offset(pos));
		AxisAlignedBB bb = setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 0.5F, state.getValue(FACING).getOpposite());
		if (mask.intersects(bb))
			collidingBoxes.add(bb.offset(pos));
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(this, 1, state.getValue(IS_DOWN_SLOPE) ? 1 : 0);
	}

	@Override
	public int getEPUse() {
		return 3;
	}
}
