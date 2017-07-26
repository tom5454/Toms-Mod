package com.tom.api.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;

public abstract class SidedInventoryHandler extends InventoryBasic implements ISidedInventory {
	public SidedInventoryHandler(String title, boolean customName, int slotCount) {
		super(title, customName, slotCount);
	}
}
