package com.tom.transport.block;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.transport.tileentity.TileEntityConveyorOmniBase;
import com.tom.util.TomsModUtils;

public abstract class ConveyorBeltOmniBase extends BlockContainerTomsMod {
	public static final PropertyDirection POSITION = PropertyDirection.create("pos");
	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	public ConveyorBeltOmniBase() {
		super(Material.IRON);
	}

	@Override
	public abstract TileEntityConveyorOmniBase createNewTileEntity(World worldIn, int meta);

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntityConveyorOmniBase te = (TileEntityConveyorOmniBase) worldIn.getTileEntity(pos);
		return state.withProperty(FACING, te.facing);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, POSITION, FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(POSITION).getIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		// System.out.println(meta);
		return this.getDefaultState().withProperty(POSITION, EnumFacing.getFront(meta % 6));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 6F / 16F, state.getValue(POSITION));
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks
	 * for render
	 */
	@Override
	public boolean isOpaqueCube(IBlockState s) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState s) {
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(POSITION, facing);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		Pair<Vec3d, Vec3d> vec = getRayTraceVectors(placer);
		RayTraceResult r = collisionRayTrace(state, worldIn, pos, vec.getKey(), vec.getRight());
		if (r != null && r.hitVec != null) {
			EnumFacing facing = state.getValue(POSITION);
			TileEntityConveyorOmniBase te = (TileEntityConveyorOmniBase) worldIn.getTileEntity(pos);
			EnumFacing box = EnumFacing.DOWN;
			if (facing.getAxis() == Axis.Y) {
				box = TomsModUtils.getDirectionFacing(placer, facing.getAxis() != Axis.Y);
			} else {
				float hitY = (float) (r.hitVec.y - pos.getY());
				if (hitY > 0.75) {
					box = EnumFacing.UP;
				} else if (hitY < 0.25) {
					box = EnumFacing.DOWN;
				} else {
					EnumFacing f = TomsModUtils.getDirectionFacing(placer, false);
					if (f.getAxis() != facing.getAxis()) {
						box = f;
					} else {
						box = EnumFacing.UP;
					}
				}
			}
			te.facing = box;
			te.markBlockForUpdate(pos);
			// float hitX = (float)(r.hitVec.xCoord - pos.getX());
			// float hitZ = (float)(r.hitVec.zCoord - pos.getZ());
		} else {
			EnumFacing facing = state.getValue(POSITION);
			TileEntityConveyorOmniBase te = (TileEntityConveyorOmniBase) worldIn.getTileEntity(pos);
			EnumFacing box = EnumFacing.DOWN;
			if (facing.getAxis() == Axis.Y) {
				box = TomsModUtils.getDirectionFacing(placer, facing.getAxis() != Axis.Y);
			} else {
				float hitY = 0;
				if (hitY > 0.75) {
					box = EnumFacing.UP;
				} else if (hitY < 0.25) {
					box = EnumFacing.DOWN;
				} else {
					EnumFacing f = TomsModUtils.getDirectionFacing(placer, false);
					if (f.getAxis() != facing.getAxis()) {
						box = f;
					} else {
						box = EnumFacing.UP;
					}
				}
			}
			te.facing = box;
			te.markBlockForUpdate(pos);
			// float hitX = (float)(r.hitVec.xCoord - pos.getX());
			// float hitZ = (float)(r.hitVec.zCoord - pos.getZ());
		}
	}

	public static Pair<Vec3d, Vec3d> getRayTraceVectors(EntityLivingBase player) {
		float pitch = player.rotationPitch;
		float yaw = player.rotationYaw;
		Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		float f1 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
		float f2 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
		float f3 = -MathHelper.cos(-pitch * 0.017453292F);
		float f4 = MathHelper.sin(-pitch * 0.017453292F);
		float f5 = f2 * f3;
		float f6 = f1 * f3;
		double d3 = 5.0D;
		if (player instanceof EntityPlayerMP) {
			d3 = ((EntityPlayerMP) player).getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		}
		Vec3d end = start.addVector(f5 * d3, f4 * d3, f6 * d3);
		return Pair.of(start, end);
	}
}
