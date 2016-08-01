package com.tom.api.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;

public abstract class TileEntityCamoable extends TileEntityTomsMod {
	public abstract ItemStack getCamoStack();
	public abstract AxisAlignedBB getBounds();
	public boolean doRender(){
		return true;
	}
	public abstract IBlockState getDefaultState();
}
