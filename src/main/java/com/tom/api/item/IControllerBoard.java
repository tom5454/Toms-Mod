package com.tom.api.item;

import net.minecraft.item.ItemStack;

public interface IControllerBoard {
	boolean canBeInsertedIntoTower(ItemStack s);

	int[] getCompatibleProcessorSlotTypes(ItemStack s);

	int[] getCompatibleMemorySlotTypes(ItemStack s);

	int[] getCompatibleChipsetTypes(ItemStack s);

	int getMaxMemorySlots(ItemStack s);

	double getMaxPowerUsage(ItemStack s);

	double getBasePowerUsage(ItemStack s);

	int getBusSpeed(ItemStack s);

	int getNetworkBusBandwith(ItemStack s);

	int getTowerBusBandwith(ItemStack s);

	int getMaxAutoCrafting(ItemStack s);
}
