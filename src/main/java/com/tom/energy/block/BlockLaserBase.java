package com.tom.energy.block;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.TomsModUtils;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockLaserBase extends BlockContainerTomsMod {
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public BlockLaserBase() {
		super(Material.IRON, MapColor.BLUE);
		this.setHardness(2F);
		this.setResistance(2F);
	}
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.getFront(meta % 6);

		/*if (enumfacing.getAxis() == EnumFacing.Axis.Y)
	    {
	        enumfacing = EnumFacing.NORTH;
	    }*/
		//System.out.println("getState");
		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{//System.out.println("getMeta");
		return state.getValue(FACING).getIndex();
	}
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos,IBlockState state, EntityLivingBase entity, ItemStack itemstack){
		EnumFacing f = TomsModUtils.getDirectionFacing(entity, true);
		world.setBlockState(pos, state.withProperty(FACING,f.getOpposite()), 2);
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
	public boolean isOpaqueCube(IBlockState s) {
		return false;
	}
}
