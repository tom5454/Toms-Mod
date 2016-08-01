package com.tom.api.energy;

import net.minecraft.item.ItemStack;

public interface IEnergyContainerItem {
	public double getEnergyStored(ItemStack container);
	public int getMaxEnergyStored(ItemStack container);
	public double receiveEnergy(ItemStack container, double maxReceive, boolean simulate);
	public double extractEnergy(ItemStack container, double maxExtract, boolean simulate);
}
