package com.tom.api.multipart;

import java.util.List;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.tom.core.CoreInit;

import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.slot.EnumFaceSlot;
import mcmultipart.api.slot.IPartSlot;

public abstract class BlockModuleBase extends BlockMultipart {
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public PropertyInteger STATE;
	protected final double size, deep;
	private static ThreadLocal<Integer> maxStates = new ThreadLocal<>();

	public BlockModuleBase(double size, double deep, int maxStates) {
		super(hackValue(maxStates));
		this.size = size;
		this.deep = deep;
	}

	private static boolean hackValue(int maxStates) {
		BlockModuleBase.maxStates.set(maxStates);
		return false;
	}

	protected static final AxisAlignedBB rotateFace(AxisAlignedBB box, EnumFacing facing) {
		switch (facing) {
		case DOWN:
		default:
			return box;
		case UP:
			return new AxisAlignedBB(box.minX, 1 - box.maxY, box.minZ, box.maxX, 1 - box.minY, box.maxZ);
		case NORTH:
			return new AxisAlignedBB(box.minX, box.minZ, box.minY, box.maxX, box.maxZ, box.maxY);
		case SOUTH:
			return new AxisAlignedBB(box.minX, box.minZ, 1 - box.maxY, box.maxX, box.maxZ, 1 - box.minY);
		case WEST:
			return new AxisAlignedBB(box.minY, box.minZ, box.minX, box.maxY, box.maxZ, box.maxX);
		case EAST:
			return new AxisAlignedBB(1 - box.maxY, box.minZ, box.minX, 1 - box.minY, box.maxZ, box.maxX);
		}
	}

	@Override
	public IPartSlot getSlotForPlacement(World world, BlockPos pos, IBlockState state, EnumFacing facing, float hitX, float hitY, float hitZ, EntityLivingBase placer) {
		return EnumFaceSlot.fromFace(getFacing(world, pos, facing, hitX, hitY, hitZ, placer));
	}

	@Override
	public IPartSlot getSlotFromWorld(IBlockAccess world, BlockPos pos, IBlockState state) {
		return EnumFaceSlot.fromFace(getFacing(state));
	}

	public EnumFacing getFacing(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityLivingBase placer) {
		return hitX > 0.3 && hitX < 0.7 && hitY > 0.3 && hitY < 0.7 && hitZ > 0.3 && hitZ < 0.7 ? facing : facing.getOpposite();
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, getFacing(world, pos, facing, hitX, hitY, hitZ, placer));
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).ordinal();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		STATE = PropertyInteger.create("state", 0, maxStates.get());
		return new BlockStateContainer(this, FACING, STATE);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return constructBox(getFacing(state));
	}

	public EnumFacing getFacing(IBlockState state) {
		return state.getValue(FACING);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IPartInfo part) {
		return constructBox(getFacing(part.getState()));
	}

	protected AxisAlignedBB constructBox(EnumFacing facing) {
		double start = 0.5 - size;
		double stop = 0.5 + size;
		return rotateFace(new AxisAlignedBB(start, 0, start, stop, deep, stop), facing);
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean canRenderInLayer(IBlockAccess world, BlockPos pos, IPartInfo part, IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean onPartActivated(IPartInfo part, EntityPlayer player, EnumHand hand, RayTraceResult hit) {
		return tryWrench(part, player, hand, hit);
	}

	public boolean tryWrench(IPartInfo part, EntityPlayer player, EnumHand hand, RayTraceResult hit) {
		if (!part.getActualWorld().isRemote && player.isSneaking() && CoreInit.isWrench(player, hand)) {
			List<ItemStack> drops = getDrops(part.getActualWorld(), part.getPartPos(), part.getState(), 0);
			for (int i = 0;i < drops.size();i++)
				spawnAsEntity(part.getActualWorld(), part.getPartPos(), drops.get(i));
			breakBlockI(part.getActualWorld(), part.getPartPos(), part.getState(), (MultipartTomsMod) part.getTile());
			part.getContainer().removePart(EnumFaceSlot.fromFace(getFacing(part.getState())));
			return true;
		}
		return false;
	}

	public boolean tryWrench(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote && playerIn.isSneaking() && CoreInit.isWrench(playerIn, hand)) {
			List<ItemStack> drops = getDrops(worldIn, pos, state, 0);
			for (int i = 0;i < drops.size();i++)
				spawnAsEntity(worldIn, pos, drops.get(i));
			worldIn.setBlockToAir(pos);
			return true;
		}
		return false;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!tryWrench(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ))
			return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		else
			return true;
	}

	@Override
	public final void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		breakBlockI(worldIn, pos, state, (MultipartTomsMod) worldIn.getTileEntity(pos));
		super.breakBlock(worldIn, pos, state);
	}

	public void breakBlockI(World worldIn, BlockPos pos, IBlockState state, MultipartTomsMod tile) {
	}

	@Override
	public IBlockState getActualState(IBlockAccess world, BlockPos pos, IPartInfo part) {
		return getActualState(part.getState(), world, pos, (PartModule<?>) part.getTile());
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof PartModule<?>)
			return getActualState(state, worldIn, pos, (PartModule<?>) te);
		else
			return state;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof PartModule<?>)
			return getExtendedState(state, worldIn, pos, (PartModule<?>) te);
		else
			return state;
	}

	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos, PartModule<?> duct) {
		return state;
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos, PartModule<?> module) {
		return state.withProperty(STATE, module.getStateClient());
	}

	@Override
	public abstract PartModule<?> createNewTileEntity(World worldIn, int meta);
}
