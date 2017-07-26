package com.tom.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockRubberWood extends Block {
	public static final PropertyEnum<WoodType> TYPE = PropertyEnum.create("type", WoodType.class);

	public BlockRubberWood() {
		super(Material.WOOD);
		this.setHardness(2.0F);
		this.setSoundType(SoundType.WOOD);// BlockLog
		this.setDefaultState(getDefaultState().withProperty(TYPE, WoodType.NORMAL));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, WoodType.get(meta));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

	public static enum WoodType implements IStringSerializable {
		NORMAL("normal", 0, null), HOLE_NORTH("hole_north", 2, EnumFacing.NORTH), HOLE_CUT_NORTH("cut_hole_north", 0, EnumFacing.NORTH), HOLE_SOUTH("hole_south", 4, EnumFacing.SOUTH), HOLE_CUT_SOUTH("cut_hole_south", 0, EnumFacing.SOUTH), HOLE_EAST("hole_east", 6, EnumFacing.EAST), HOLE_CUT_EAST("cut_hole_east", 0, EnumFacing.EAST), HOLE_WEST("hole_west", 8, EnumFacing.WEST), HOLE_CUT_WEST("cut_hole_west", 0, EnumFacing.WEST),;
		public static final WoodType[] VALUES = values();
		private final String name;
		private final int cutType;
		private final EnumFacing facing;

		private WoodType(String name, int cut, EnumFacing f) {
			this.name = name;
			cutType = cut;
			this.facing = f;
		}

		@Override
		public String getName() {
			return name;
		}

		public static WoodType get(int index) {
			return VALUES[MathHelper.abs(index % VALUES.length)];
		}

		public boolean isHole() {
			return this == HOLE_EAST || this == HOLE_WEST || this == HOLE_NORTH || this == HOLE_SOUTH;
		}

		public boolean isCutHole() {
			return this == HOLE_CUT_EAST || this == HOLE_CUT_WEST || this == HOLE_CUT_NORTH || this == HOLE_CUT_SOUTH;
		}

		public WoodType getCut() {
			return get(cutType);
		}

		public static WoodType getNorm(int f) {
			if (f == 0)
				return HOLE_NORTH;
			else if (f == 1)
				return HOLE_SOUTH;
			else if (f == 2)
				return HOLE_EAST;
			else
				return HOLE_WEST;
		}

		public EnumFacing getFacing() {
			return facing;
		}
	}

	@Override
	public boolean canSustainLeaves(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isWood(net.minecraft.world.IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		int i = 4;
		int j = i + 1;

		if (worldIn.isAreaLoaded(pos.add(-j, -j, -j), pos.add(j, j, j))) {
			for (BlockPos blockpos : BlockPos.getAllInBox(pos.add(-i, -i, -i), pos.add(i, i, i))) {
				IBlockState iblockstate = worldIn.getBlockState(blockpos);

				if (iblockstate.getBlock().isLeaves(iblockstate, worldIn, blockpos)) {
					iblockstate.getBlock().beginLeavesDecay(iblockstate, worldIn, blockpos);
				}
			}
		}
	}
}
