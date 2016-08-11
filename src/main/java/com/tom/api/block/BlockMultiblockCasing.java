package com.tom.api.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.apis.TomsModUtils;

public abstract class BlockMultiblockCasing extends BlockMultiblockPart {

	protected BlockMultiblockCasing(Material p_i45386_1_) {
		super(p_i45386_1_);
	}
	public static final PropertyEnum<CasingConnectionType> CASING_CONNECTION_TYPE = PropertyEnum.create("type", CasingConnectionType.class);
	/**if(t == 0){
			return this.NoC;
		}else if(t == 1){
			return this.UD;
		}else if(t == 2){
			return this.dot;
		}else if(t == 3){
			return this.f;
		}else if(t == 4){
			return this.RL;
		}else{
			return this.blockIcon;
		}*/
	public static enum CasingConnectionType implements IStringSerializable{
		NoCT("noct"),
		UDCT("udct"),
		DOTC("dotc"),
		Full("full"),
		RLCT("rlct"),
		RLAT("rlat"),
		;
		public static final CasingConnectionType[] VALUES = values();
		private final String name;
		private CasingConnectionType(String name) {
			this.name = name;
		}
		@Override
		public String getName() {
			return name;
		}
		public static CasingConnectionType get(int index){
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
		return new BlockStateContainer(this,CASING_CONNECTION_TYPE);
	}
	public static final void setConnection(World world, BlockPos pos, int to){
		TomsModUtils.setBlockStateProperty(world, pos, CASING_CONNECTION_TYPE, CasingConnectionType.get(to));
	}
}
