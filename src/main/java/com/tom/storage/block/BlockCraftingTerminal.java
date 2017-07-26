package com.tom.storage.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.storage.tileentity.TileEntityBasicTerminal;
import com.tom.storage.tileentity.TileEntityCraftingTerminal;

public class BlockCraftingTerminal extends BlockTerminalBase {

	@Override
	public TileEntityBasicTerminal createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCraftingTerminal();
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityCraftingTerminal tile = (TileEntityCraftingTerminal) worldIn.getTileEntity(pos);
		InventoryHelper.dropInventoryItems(worldIn, pos, tile.craftingInv);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public int getGuiID() {
		return GuiIDs.blockCraftingTerminal.ordinal();
	}

	@Override
	public String getName() {
		return "Crafting Terminal";
	}

	@Override
	public boolean hasCustomFront() {
		return false;
	}

	@Override
	public int[][][] getImageIDs() {
		return new int[][][]{{{1, 0, -30}, {2, 0, -10}, {3, 0, 0}}, {{0, 0, -20}, {1, 0, -100}, {2, 0, -10}, {-1}, {4, 0, -40}, {5, 0, 10}, {6}}};
	}

	@Override
	public String getCategory() {
		return "crafting";
	}
}
