package com.tom.core.item;

import net.minecraft.item.ItemStack;

import com.tom.api.energy.ItemEnergyContainer;

public class ItemBattery extends ItemEnergyContainer {

	public ItemBattery() {
		super(10000, 750, 1000);
		setMaxStackSize(16);
	}
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return getEnergyStored(stack) > 0;
	}
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1 - (getEnergyStored(stack) / capacity);
	}
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return getEnergyStored(stack) > 0 ? 1 : super.getItemStackLimit(stack);
	}
	@Override
	public boolean canInteract(ItemStack container) {
		return container.stackSize == 1;
	}
}
