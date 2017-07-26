package com.tom.storage.item;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import com.tom.api.multipart.PartModule;
import com.tom.storage.block.BlockPatternTerminal;
import com.tom.storage.block.BlockTerminalBase;
import com.tom.storage.multipart.PartPatternTerminal;
import com.tom.storage.multipart.PartTerminal;

public class ItemPartPatternTerminal extends ItemPartTerminal {
	public ItemPartPatternTerminal() {
		super(0.35, 0.15);
	}

	@Override
	public PartTerminal createNewTileEntity(World worldIn, int meta) {
		return new PartPatternTerminal();
	}

	@Override
	public boolean hasCustomFront() {
		return true;
	}

	@Override
	public String getName() {
		return "Pattern Part Terminal";
	}

	@Override
	public String getCategory() {
		return "pattern";
	}

	@Override
	public int[][][] getImageIDs() {
		return new int[][][]{{{1, 0, 0}, {2, 0, -30}}, {{1, 0, -30}, {2, 0, 30}, {3, 0, -40}, {5, 0, -50}, {6}, {7, 0, -40}, {8}, {9, 0, 0}}};
	}

	@Override
	public ResourceLocation getFrontModelMapper(int state, ResourceLocation loc) {
		return state == 0 ? loc : new ResourceLocation(loc.getResourceDomain(), loc.getResourcePath() + "2");
	}

	@Override
	public int getStates() {
		return 2;
	}

	@Override
	public int getState(IBlockState state) {
		return state.getValue(BlockPatternTerminal.HAS_PATTERN) ? 1 : 0;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos, PartModule<?> duct) {
		return super.getActualState(state, worldIn, pos, duct).withProperty(BlockPatternTerminal.HAS_PATTERN, ((PartPatternTerminal) duct).pattern);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		STATE = PropertyInteger.create("state", 0, 2);
		return new ExtendedBlockState(this, new IProperty[]{BlockTerminalBase.FACING, STATE, BlockPatternTerminal.HAS_PATTERN}, new IUnlistedProperty[]{BlockTerminalBase.COLOR, BlockTerminalBase.STATE});
	}
}
