package com.tom.storage.tileentity;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;

public abstract class TMTank extends TileEntityTomsMod implements ITileFluidHandler {
	public abstract FluidStack getStack();

	public abstract int getCapacity();

	public abstract int getComparatorValue();

	public abstract void readFromStackNBT(ItemStack stack);

	public abstract void writeToStackNBT(ItemStack stack);
}
