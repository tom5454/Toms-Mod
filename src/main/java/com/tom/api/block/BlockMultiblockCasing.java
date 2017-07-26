package com.tom.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.apis.TomsModUtils;

public class BlockMultiblockCasing extends Block {
	public BlockMultiblockCasing() {
		super(Material.IRON);
		setHardness(2);
		setResistance(10);
	}

	public static final PropertyEnum<CasingConnectionType> CASING_CONNECTION_TYPE = PropertyEnum.create("type", CasingConnectionType.class);

	public static enum CasingConnectionType implements IStringSerializable {
		NoCT("noct"), Full("full"),;
		public static final CasingConnectionType[] VALUES = values();
		private final String name;

		private CasingConnectionType(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		public static CasingConnectionType get(int index) {
			return VALUES[index % VALUES.length];
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(CASING_CONNECTION_TYPE, CasingConnectionType.get(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(CASING_CONNECTION_TYPE).ordinal();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, CASING_CONNECTION_TYPE);
	}

	public static final void setConnection(World world, BlockPos pos, int to) {
		TomsModUtils.setBlockStateProperty(world, pos, CASING_CONNECTION_TYPE, CasingConnectionType.get(to));
	}
}
