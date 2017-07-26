package com.tom.storage.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.storage.tileentity.TileEntityBasicTerminal;
import com.tom.storage.tileentity.TileEntityPatternTerminal;

public class BlockPatternTerminal extends BlockTerminalBase {
	public static final PropertyBool HAS_PATTERN = PropertyBool.create("has_pattern");

	@Override
	public TileEntityBasicTerminal createNewTileEntity(World worldIn, int meta) {
		return new TileEntityPatternTerminal();
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityPatternTerminal tile = (TileEntityPatternTerminal) worldIn.getTileEntity(pos);
		InventoryHelper.dropInventoryItems(worldIn, pos, tile.getPatternInv());
		InventoryHelper.dropInventoryItems(worldIn, pos, tile.getUpgradeInv());
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public int getGuiID() {
		return GuiIDs.patternTerminal.ordinal();
	}

	@Override
	public String getName() {
		return "Pattern Terminal";
	}

	@Override
	public boolean hasCustomFront() {
		return true;
	}

	@Override
	public int[][][] getImageIDs() {
		return new int[][][]{{{1, 0, -30}, {2, 0, -10}, {3, 0, 0}}, {{0, 0, -30}, {1, 0, -50}, {2, 0, -100}, {3, 0, -10}, {4, 0, -70}, {5, 0, -10}, {6, 0}, {8, 0, -40}, {9, 0, -85}, {10, 0, 10}, {11}, {12, 0, 30}}};
	}

	@Override
	public String getCategory() {
		return "pattern";
	}

	@Override
	public boolean mirrorModel(int state) {
		return true;
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
		return state.getValue(HAS_PATTERN) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{FACING, HAS_PATTERN}, new IUnlistedProperty[]{COLOR, STATE});
	}

	@Override
	public IBlockState getExtendedState(IExtendedBlockState state, TileEntityBasicTerminal te) {
		return state.withProperty(HAS_PATTERN, ((TileEntityPatternTerminal) te).pattern);
	}
}
