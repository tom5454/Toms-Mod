package com.tom.energy.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.api.ITileFluidHandler;
import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.TomsModUtils;

import com.tom.energy.tileentity.TileEntityGeoGenerator;

public class GeoGenerator extends BlockContainerTomsMod {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool ACTIVE = PropertyBool.create("active");

	public GeoGenerator() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityGeoGenerator();
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing f = TomsModUtils.getDirectionFacing(placer, false);
		return this.getDefaultState().withProperty(FACING, f.getOpposite()).withProperty(ACTIVE, false);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING, ACTIVE});
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta % 6);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}
		// System.out.println("getState");
		return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(ACTIVE, meta > 5);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {// System.out.println("getMeta");
		return state.getValue(FACING).getIndex() + (state.getValue(ACTIVE) ? 6 : 0);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityGeoGenerator te = (TileEntityGeoGenerator) worldIn.getTileEntity(pos);
		ItemStack s = new ItemStack(this);
		s.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = new NBTTagCompound();
		te.writeToStackNBT(tag);
		s.getTagCompound().setTag("BlockEntityTag", tag);
		s.getTagCompound().setBoolean("stored", true);
		spawnAsEntity(worldIn, pos, s);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("stored")) {
			tooltip.add(I18n.format("tomsMod.tooltip.itemsStored"));
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		return TomsModUtils.interactWithFluidHandler(((ITileFluidHandler) worldIn.getTileEntity(pos)).getTankOnSide(side), playerIn, hand);
	}
}
