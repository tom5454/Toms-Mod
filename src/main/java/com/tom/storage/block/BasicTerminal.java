package com.tom.storage.block;

import net.minecraft.world.World;

import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.storage.tileentity.TileEntityBasicTerminal;

public class BasicTerminal extends BlockTerminalBase {
	@Override
	public TileEntityBasicTerminal createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBasicTerminal();
	}

	@Override
	public int getGuiID() {
		return GuiIDs.basicTerminalBlock.ordinal();
	}

	@Override
	public String getName() {
		return "Basic Terminal";
	}

	@Override
	public boolean hasCustomFront() {
		return false;
	}

	@Override
	public int[][][] getImageIDs() {
		return new int[][][]{{{1, 0, -30}, {2, 0, -10}, {3, 0, 0}}, {{1, 0, -30}, {2, 0, -100}, {3, 0, -20}, {4, 0, -10}, {-1}, {6, 0}, {7}}};
	}

	@Override
	public String getCategory() {
		return "basic";
	}
}
