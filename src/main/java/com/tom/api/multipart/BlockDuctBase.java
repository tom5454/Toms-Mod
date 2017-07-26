package com.tom.api.multipart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import com.tom.core.CoreInit;

import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.slot.EnumCenterSlot;
import mcmultipart.api.slot.IPartSlot;

public abstract class BlockDuctBase extends BlockMultipart {
	private int maxIntValueInProperties;

	public static class PropertyList {
		public final PropertyInteger UP;
		public final PropertyInteger DOWN;
		public final PropertyInteger NORTH;
		public final PropertyInteger EAST;
		public final PropertyInteger SOUTH;
		public final PropertyInteger WEST;

		public PropertyList(PropertyInteger up, PropertyInteger down, PropertyInteger north, PropertyInteger east, PropertyInteger south, PropertyInteger west) {
			UP = up;
			DOWN = down;
			NORTH = north;
			EAST = east;
			SOUTH = south;
			WEST = west;
		}
	}

	protected PropertyList propertyList;
	private static Map<Integer, PropertyList> propertyListMap = new HashMap<>();
	private static ThreadLocal<Integer> maxStates = new ThreadLocal<>();

	public BlockDuctBase(int maxStates) {
		super(hackValue(maxStates));
	}

	private static boolean hackValue(int maxStates) {
		BlockDuctBase.maxStates.set(maxStates);
		return false;
	}

	@Override
	public IPartSlot getSlotForPlacement(World world, BlockPos pos, IBlockState state, EnumFacing facing, float hitX, float hitY, float hitZ, EntityLivingBase placer) {
		return EnumCenterSlot.CENTER;
	}

	@Override
	public IPartSlot getSlotFromWorld(IBlockAccess world, BlockPos pos, IBlockState state) {
		return EnumCenterSlot.CENTER;
	}

	@Override
	public void addCollisionBoxToList(IPartInfo part, AxisAlignedBB bbIn, List<AxisAlignedBB> list, Entity entity, boolean unused) {
		PartDuct<?> duct = (PartDuct<?>) part.getTile();
		BlockPos pos = duct.getPos();
		AxisAlignedBB mask = bbIn.offset(-pos.getX(), -pos.getY(), -pos.getZ());
		if (duct.BOXES[6].intersects(mask)) {
			list.add(duct.BOXES[6].offset(pos));
		}
		for (EnumFacing f : EnumFacing.VALUES) {
			if (duct.BOXES[f.ordinal()].intersects(mask) && (duct.connects(f) || duct.connectsM(f) || duct.connectsInv(f) || duct.connectsE1(f) || duct.connectsE2(f))) {
				list.add(duct.BOXES[f.ordinal()].offset(pos));
			}
			if (duct.connectsInv(f) && this instanceof ICustomPartBounds) {
				AxisAlignedBB b = PartDuct.rotateFace(((ICustomPartBounds) this).getBoxForConnect(), f);
				if (b.intersects(mask))
					list.add(b.offset(pos));
			}
		}
	}

	@Override
	public IBlockState getExtendedState(IBlockAccess world, BlockPos pos, IPartInfo part, IBlockState state) {
		return getExtendedState(part.getState(), world, pos, (PartDuct<?>) part.getTile());
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof PartDuct<?>)
			return getExtendedState(state, worldIn, pos, (PartDuct<?>) te);
		else
			return state;
	}

	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos, PartDuct<?> duct) {
		return state;
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos, PartDuct<?> duct) {
		if (propertyList == null)
			return state;
		return state.withProperty(propertyList.DOWN, getPropertyValue(duct, EnumFacing.DOWN)).withProperty(propertyList.UP, getPropertyValue(duct, EnumFacing.UP)).withProperty(propertyList.NORTH, getPropertyValue(duct, EnumFacing.NORTH)).withProperty(propertyList.SOUTH, getPropertyValue(duct, EnumFacing.SOUTH)).withProperty(propertyList.WEST, getPropertyValue(duct, EnumFacing.WEST)).withProperty(propertyList.EAST, getPropertyValue(duct, EnumFacing.EAST));
	}

	@Override
	public IBlockState getActualState(IBlockAccess world, BlockPos pos, IPartInfo part) {
		return getActualState(part.getState(), world, pos, (PartDuct<?>) part.getTile());
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof PartDuct<?>)
			return getActualState(state, worldIn, pos, (PartDuct<?>) te);
		else
			return state;
	}

	public final int getPropertyValue(PartDuct<?> d, EnumFacing side) {
		return d.getPropertyValue(side);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState();
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB bbIn, List<AxisAlignedBB> list, Entity entityIn, boolean unused) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof PartDuct<?>) {
			PartDuct<?> duct = (PartDuct<?>) te;
			AxisAlignedBB mask = bbIn.offset(-pos.getX(), -pos.getY(), -pos.getZ());
			if (duct.BOXES[6].intersects(mask)) {
				list.add(duct.BOXES[6].offset(pos));// .offset(pos.getX() > 0 ?
													// 0 : -1, 0, pos.getZ() > 0
													// ? 0 : -1)
			}
			for (EnumFacing f : EnumFacing.VALUES) {
				if (duct.BOXES[f.ordinal()].intersects(mask) && (duct.connects(f) || duct.connectsM(f) || duct.connectsInv(f) || duct.connectsE1(f) || duct.connectsE2(f))) {
					list.add(duct.BOXES[f.ordinal()].offset(pos));
				}
				if (duct.connectsInv(f) && this instanceof ICustomPartBounds) {
					AxisAlignedBB b = PartDuct.rotateFace(((ICustomPartBounds) this).getBoxForConnect(), f);
					if (b.intersects(mask))
						list.add(b.offset(pos));
				}
			}
		}
	}

	@Override
	public List<AxisAlignedBB> getOcclusionBoxes(IPartInfo part) {
		List<AxisAlignedBB> list = new ArrayList<>();
		PartDuct<?> duct = (PartDuct<?>) part.getTile();
		double start = 0.5 - duct.size;
		double stop = 0.5 + duct.size;
		list.add(new AxisAlignedBB(start, start, start, stop, stop, stop));
		return list;
	}

	@Override
	public BlockStateContainer createBlockState() {
		if (maxStates.get() == -1) {
			maxIntValueInProperties = -1;
			propertyList = null;
		} else {
			maxIntValueInProperties = Math.min(maxStates.get(), 3);
			if (!propertyListMap.containsKey(maxIntValueInProperties)) {
				PropertyInteger UP = PropertyInteger.create("up", 0, maxIntValueInProperties);
				PropertyInteger DOWN = PropertyInteger.create("down", 0, maxIntValueInProperties);
				PropertyInteger NORTH = PropertyInteger.create("north", 0, maxIntValueInProperties);
				PropertyInteger EAST = PropertyInteger.create("east", 0, maxIntValueInProperties);
				PropertyInteger SOUTH = PropertyInteger.create("south", 0, maxIntValueInProperties);
				PropertyInteger WEST = PropertyInteger.create("west", 0, maxIntValueInProperties);
				propertyListMap.put(maxIntValueInProperties, new PropertyList(UP, DOWN, NORTH, EAST, SOUTH, WEST));
			}
			propertyList = propertyListMap.get(maxIntValueInProperties);
		}
		IProperty<?>[] properties = getProperties();
		IUnlistedProperty<?>[] unlistedProperties = getUnlistedProperties();
		if (properties == null) {
			properties = new IProperty[]{propertyList.DOWN, propertyList.UP, propertyList.NORTH, propertyList.SOUTH, propertyList.WEST, propertyList.EAST};
		}
		if (unlistedProperties != null && unlistedProperties.length > 0)
			return new ExtendedBlockState(this, properties, unlistedProperties);
		else
			return new BlockStateContainer(this, properties);
	}

	protected IUnlistedProperty<?>[] getUnlistedProperties() {
		return null;
	}

	protected IProperty<?>[] getProperties() {
		return new IProperty[]{propertyList.DOWN, propertyList.UP, propertyList.NORTH, propertyList.SOUTH, propertyList.WEST, propertyList.EAST};
	}

	@Override
	public abstract PartDuct<?> createNewTileEntity(World worldIn, int meta);

	@Override
	public boolean onPartActivated(IPartInfo part, EntityPlayer player, EnumHand hand, RayTraceResult hit) {
		if (!part.getActualWorld().isRemote && allowWrench() && player.isSneaking() && CoreInit.isWrench(player, hand)) {
			List<ItemStack> drops = getWrenchDrops(part.getActualWorld(), part.getPartPos(), part.getState(), 0, (PartDuct<?>) part.getTile());
			for (int i = 0;i < drops.size();i++)
				spawnAsEntity(part.getActualWorld(), part.getPartPos(), drops.get(i));
			breakBlockI(part.getActualWorld(), part.getPartPos(), part.getState(), (MultipartTomsMod) part.getTile());
			part.getContainer().removePart(EnumCenterSlot.CENTER);
			return true;
		}
		return false;
	}

	@Override
	public final void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		breakBlockI(worldIn, pos, state, (MultipartTomsMod) worldIn.getTileEntity(pos));
		super.breakBlock(worldIn, pos, state);
	}

	public void breakBlockI(World worldIn, BlockPos pos, IBlockState state, MultipartTomsMod tile) {
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote && allowWrench() && playerIn.isSneaking() && CoreInit.isWrench(playerIn, hand)) {
			List<ItemStack> drops = getWrenchDrops(worldIn, pos, state, 0, (PartDuct<?>) worldIn.getTileEntity(pos));
			for (int i = 0;i < drops.size();i++)
				spawnAsEntity(worldIn, pos, drops.get(i));
			worldIn.setBlockToAir(pos);
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	public boolean allowWrench() {
		return true;
	}

	public List<ItemStack> getWrenchDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune, PartDuct<?> ductIn) {
		return getDrops(world, pos, state, fortune);
	}
}
