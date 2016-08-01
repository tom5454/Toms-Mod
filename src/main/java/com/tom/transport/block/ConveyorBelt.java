package com.tom.transport.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.config.Config;
import com.tom.transport.tileentity.TileEntityConveyor;

public class ConveyorBelt extends BlockContainerTomsMod {
	public static final PropertyDirection POSITION = PropertyDirection.create("pos");
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyInteger BELT_POS = PropertyInteger.create("belt", 0, 15);
	public ConveyorBelt() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityConveyor();
	}
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos,
			EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		if(placer.getHeldItemMainhand() != null){
			if(placer.getHeldItemMainhand().getTagCompound() == null){
				placer.getHeldItemMainhand().setTagCompound(new NBTTagCompound());
				placer.getHeldItemMainhand().getTagCompound().setBoolean("tm_fresh_tag", true);
			}
			placer.getHeldItemMainhand().getTagCompound().setFloat("tm_hitY", hitY);
		}
		return this.getDefaultState().withProperty(POSITION, facing);
	}
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		EnumFacing facing = state.getValue(POSITION);
		TileEntityConveyor te = (TileEntityConveyor) worldIn.getTileEntity(pos);
		EnumFacing box = EnumFacing.DOWN;
		if(facing.getAxis() == Axis.Y){
			box = TomsModUtils.getDirectionFacing(placer, facing.getAxis() != Axis.Y);
		}else{
			float hitY = stack.getTagCompound() != null ? stack.getTagCompound().getFloat("tm_hitY") : 0.5F;
			if(stack.getTagCompound() != null){
				if(stack.getTagCompound().getBoolean("tm_fresh_tag")) stack.setTagCompound(null);
				else stack.getTagCompound().removeTag("tm_hitY");
			}
			if(hitY > 0.75){
				box = EnumFacing.UP;
			}else if(hitY < 0.25){
				box = EnumFacing.DOWN;
			}else{
				EnumFacing f = TomsModUtils.getDirectionFacing(placer, false);
				if(f.getAxis() != facing.getAxis()){
					box = f;
				}else{
					box = EnumFacing.UP;
				}
			}

		}
		te.facing = box;
		te.markBlockForUpdate(pos);
	}
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn,
			BlockPos pos) {
		TileEntityConveyor te = (TileEntityConveyor) worldIn.getTileEntity(pos);
		int belt = 0;
		if(Config.enableConveyorBeltAnimation)belt = MathHelper.floor_double(te.position) % 16;
		return state.withProperty(FACING, te.facing).withProperty(BELT_POS, belt);
	}
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,POSITION,FACING,BELT_POS);
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(POSITION).getIndex();
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		//System.out.println(meta);
		return this.getDefaultState().withProperty(POSITION,  EnumFacing.getFront(meta % 6));
	}
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 6F/16F, state.getValue(POSITION));
	}
	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	@Override
	public boolean isOpaqueCube(IBlockState s)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState s)
	{
		return false;
	}
}
