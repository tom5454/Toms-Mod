package com.tom.api.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.apis.TomsModUtils;
import com.tom.factory.tileentity.TileEntityMultiblock;

import com.tom.core.block.BlockHidden.IBreakingDetector;

public abstract class BlockMultiblockController extends BlockContainerTomsMod implements IBreakingDetector {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);

	public BlockMultiblockController() {
		super(Material.IRON);
		translucent = true;
		lightOpacity = 0;
	}

	@Override
	public abstract TileEntityMultiblock createNewTileEntity(World worldIn, int meta);

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

	@Override
	public void slaveBroken(World world, BlockPos pos, BlockPos brokenPos, IBlockState state) {
		TomsModUtils.setBlockState(world, pos, state.withProperty(STATE, 0));
		TileEntityMultiblock te = (TileEntityMultiblock) world.getTileEntity(pos);
		te.kill();
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
		/*EnumFacing enumfacing = EnumFacing.getFront(meta % 4+2);
		
		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
		    enumfacing = EnumFacing.NORTH;
		}
		//System.out.println("getState");
		return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(STATE, meta / 4);*/
		return TomsModUtils.getBlockStateFromMeta(meta, STATE, FACING, getDefaultState(), 2);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {// System.out.println("getMeta");
		/*EnumFacing enumfacing = state.getValue(FACING);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
		    enumfacing = EnumFacing.NORTH;
		}
		return enumfacing.getIndex() * (state.getValue(STATE)+1);*/
		return TomsModUtils.getMetaFromState(state.getValue(FACING), state.getValue(STATE));
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing f = TomsModUtils.getDirectionFacing(placer, false);
		return getDefaultState().withProperty(FACING, f).withProperty(STATE, 0);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityMultiblock te = (TileEntityMultiblock) worldIn.getTileEntity(pos);
		te.kill();
		super.breakBlock(worldIn, pos, state);
	}
}
