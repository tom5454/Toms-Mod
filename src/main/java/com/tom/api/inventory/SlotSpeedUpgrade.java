package com.tom.api.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.tom.factory.FactoryInit;

public class SlotSpeedUpgrade extends Slot {
	private final int maxStackSize;
	public SlotSpeedUpgrade(IInventory inventoryIn, int index, int xPosition, int yPosition, int maxStackSize) {
		super(inventoryIn, index, xPosition, yPosition);
		this.maxStackSize = maxStackSize;
	}
	@Override
	public int getSlotStackLimit() {
		return maxStackSize;
	}
	@Override
	public boolean isItemValid(ItemStack stack) {
		return stack != null && stack.getItem() == FactoryInit.speedUpgrade;
	}
}
