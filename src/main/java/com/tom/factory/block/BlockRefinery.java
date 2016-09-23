package com.tom.factory.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.MultiBlockPos;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.factory.tileentity.TileEntityRefinery;
import com.tom.handler.GuiHandler.GuiIDs;

import com.tom.core.block.BlockHidden.IBreakingDetector;

public class BlockRefinery extends BlockContainerTomsMod implements IBreakingDetector{
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);
	public BlockRefinery() {
		super(Material.IRON);
		translucent = true;
		lightOpacity = 0;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityRefinery();
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

	@Override
	public void slaveBroken(World world, BlockPos pos, BlockPos brokenPos, IBlockState state) {
		TomsModUtils.setBlockState(world, pos, state.withProperty(STATE, 0));
	}
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING,STATE});
	}
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
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
	public int getMetaFromState(IBlockState state)
	{//System.out.println("getMeta");
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
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer) {
		EnumFacing f = TomsModUtils.getDirectionFacing(placer, false);
		return getDefaultState().withProperty(FACING, f).withProperty(STATE, 0);
	}
	@Override
	public boolean isFullyOpaque(IBlockState state) {
		return false;
	}
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(state.getValue(STATE) != 0){
			TileEntityRefinery te = (TileEntityRefinery) worldIn.getTileEntity(pos);
			boolean gui = true;
			if(pos instanceof MultiBlockPos){
				IFluidHandler c = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side, ((MultiBlockPos) pos).getOther(), ((MultiBlockPos) pos).getId());
				if(c != null){
					gui = !FluidUtil.interactWithFluidHandler(heldItem, c, playerIn);
				}
			}
			if(gui){
				if(!worldIn.isRemote)playerIn.openGui(CoreInit.modInstance, GuiIDs.refinery.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
		}
		return pos instanceof MultiBlockPos;
	}
}
