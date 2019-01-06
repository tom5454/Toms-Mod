package com.tom.factory.tileentity;

import java.util.Map;

import net.minecraft.block.state.IBlockState;

import com.tom.api.block.BlockMultiblockController;
import com.tom.api.tileentity.IMultiblockController;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.BlockComponents.ComponentVariants;
import com.tom.util.Checker.RunnableStorage;
import com.tom.util.MultiblockBlockChecker;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.TileEntityHidden.ILinkableCapabilities;

public abstract class TileEntityMultiblock extends TileEntityTomsMod implements ILinkableCapabilities, IMultiblockController {
	protected RunnableStorage killList = new RunnableStorage(true);
	private boolean merged = false;

	@SuppressWarnings("deprecation")
	public static IBlockState getComponent(ComponentVariants variant) {
		return FactoryInit.components.getStateFromMeta(variant.ordinal());
	}

	@Override
	public boolean getMultiblock(IBlockState state) {
		if (getMaterialMap() == null) {
			setMaterialMap(TomsModUtils.createMaterialMap(getConfig(), getStack()));
		}
		if (world.getTotalWorldTime() % 10 == 0)
			return merged = TomsModUtils.getLayers(getConfig(), getMaterialMap(), world, state.getValue(BlockMultiblockController.FACING), pos, killList);
		if (merged)
			return true;
		else
			return false;
	}

	protected abstract void setMaterialMap(Map<Character, MultiblockBlockChecker> value);

	public void kill() {
		killList.run();
	}
}
