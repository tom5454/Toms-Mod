package com.tom.api.multipart;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Lists;

import com.tom.api.block.BlockContainerTomsMod;
import com.tom.api.item.MultipartItem;
import com.tom.apis.TomsModUtils;
import com.tom.handler.WorldHandler;

import mcmultipart.RayTraceHelper;
import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipart;

public abstract class BlockMultipart extends BlockContainerTomsMod implements IMultipart {
	public BlockMultipart(boolean unused) {
		super(Material.GLASS);
		setHardness(0.3F);
		setResistance(10);
	}

	@Override
	public abstract MultipartTomsMod createNewTileEntity(World worldIn, int meta);

	@Override
	public boolean canRenderInLayer(IBlockAccess world, BlockPos pos, IPartInfo part, IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
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

	@SuppressWarnings("deprecation")
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		try {
			Minecraft mc = Minecraft.getMinecraft();
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile == null)
				return super.getSelectedBoundingBox(state, worldIn, pos);
			Pair<Vec3d, Vec3d> vec = RayTraceHelper.getRayTraceVectors(mc.player);
			RayTraceResult ray;
			List<AxisAlignedBB> list = new ArrayList<>();
			if (tile instanceof IMultipartContainer) {
				IMultipartContainer c = (IMultipartContainer) tile;
				IPartInfo i = TomsModUtils.getPartInfo(c, state);
				ray = collisionRayTrace(state, worldIn, pos, vec.getLeft(), vec.getRight());
				addCollisionBoxToList(i, FULL_BLOCK_AABB.offset(pos), list, mc.player, false);
			} else {
				ray = collisionRayTrace(state, worldIn, pos, vec.getLeft(), vec.getRight());
				addCollisionBoxToList(state, worldIn, pos, FULL_BLOCK_AABB.offset(pos), list, mc.player, false);
			}
			// ray = TomsModUtils.getLookedBox(worldIn, pos, vec.getLeft(),
			// vec.getRight(), list, FULL_BLOCK_AABB, this);
			if (ray != null) {
				/*for(int i = 0;i<list.size();i++){
					AxisAlignedBB bb = list.get(i);
					if(bb.isVecInside(ray.hitVec)){
						return bb;
					}
				}*/
				return list.get(ray.subHit).expand(0.000001, 0.000001, 0.000001);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FULL_BLOCK_AABB;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		((MultipartTomsMod) world.getTileEntity(pos)).onNeighborTileChange(false);
	}

	@Override
	public void onPartChanged(IPartInfo part, IPartInfo otherPart) {
		World world = part.getActualWorld();
		BlockPos pos = part.getPartPos();
		((MultipartTomsMod) part.getTile()).onNeighborTileChange(false);
		WorldHandler.queueTask(world.provider.getDimension(), () -> {
			for (EnumFacing f : EnumFacing.VALUES) {
				IBlockState state = world.getBlockState(pos.offset(f));
				state.getBlock().onNeighborChange(world, pos.offset(f), pos);
			}
		});
	}

	@Override
	public void onNeighborChange(IPartInfo part, BlockPos neighbor) {
		((MultipartTomsMod) part.getTile()).onNeighborTileChange(false);
	}

	@SuppressWarnings("deprecation")
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
		List<RayTraceResult> list = Lists.<RayTraceResult>newArrayList();

		for (AxisAlignedBB axisalignedbb : getCollisionBoxList(this.getActualState(blockState, worldIn, pos), worldIn, pos)) {
			list.add(this.rayTrace(pos, start, end, axisalignedbb));
		}

		RayTraceResult raytraceresult1 = null;
		double d1 = 0.0D;
		int element = -1;
		int value = -1;

		for (RayTraceResult raytraceresult : list) {
			element++;
			if (raytraceresult != null) {
				double d0 = raytraceresult.hitVec.squareDistanceTo(end);

				if (d0 > d1) {
					raytraceresult1 = raytraceresult;
					d1 = d0;
					value = element;
				}
			}
		}
		if (raytraceresult1 != null)
			raytraceresult1.subHit = value;
		return raytraceresult1;
	}

	@SuppressWarnings("deprecation")
	private List<AxisAlignedBB> getCollisionBoxList(IBlockState blockState, World worldIn, BlockPos pos) {
		List<AxisAlignedBB> list = new ArrayList<>();
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof IMultipartContainer) {
			IMultipartContainer c = (IMultipartContainer) tile;
			IPartInfo i = TomsModUtils.getPartInfo(c, blockState);
			addCollisionBoxToList(i, FULL_BLOCK_AABB.offset(pos), list, null, false);
		} else {
			addCollisionBoxToList(blockState, worldIn, pos, FULL_BLOCK_AABB.offset(pos), list, null, false);
		}
		List<AxisAlignedBB> ret = new ArrayList<>();
		list.stream().map(i -> i.offset(-pos.getX(), -pos.getY(), -pos.getZ())).forEach(ret::add);
		return ret;
	}

	@Override
	public RayTraceResult collisionRayTrace(IPartInfo part, Vec3d start, Vec3d end) {
		return collisionRayTrace(part.getState(), part.getActualWorld(), part.getPartPos(), start, end);
	}

	@Override
	public ItemBlock createItemBlock() {
		return new MultipartItem(this);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof MultipartTomsMod) {
			((MultipartTomsMod) tile).onNeighborTileChange(true);
		}
		super.breakBlock(worldIn, pos, state);
	}
}
