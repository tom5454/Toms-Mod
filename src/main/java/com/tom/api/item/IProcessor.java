package com.tom.api.item;

import net.minecraft.item.ItemStack;

public interface IProcessor {
	int getMaxProcessingPower(ItemStack s);

	int getMaxMemory(ItemStack s);

	double getPowerDrained(ItemStack s);

	double getPowerDrainedPerOperation(ItemStack s);

	int getProcessorTier(ItemStack s);

	int getCoreCount(ItemStack s);

	int getMaxCompatibleTowers(ItemStack s);

	int getMaxChannels(ItemStack s);

	int getMaxAutoCrafting(ItemStack s);

	int getMaxAutoCraftingOperations(ItemStack s);

	int getMaxAutoCraftingStorage(ItemStack s);

	int getMaxStorage(ItemStack s);

	double getHeatProduction(ItemStack s);
}
