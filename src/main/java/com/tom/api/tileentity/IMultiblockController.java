package com.tom.api.tileentity;

import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import com.tom.util.MultiblockBlockChecker;

public interface IMultiblockController {
	boolean getMultiblock(IBlockState state);

	Object[][] getConfig();

	ItemStack getStack();

	Map<Character, MultiblockBlockChecker> getMaterialMap();
}
