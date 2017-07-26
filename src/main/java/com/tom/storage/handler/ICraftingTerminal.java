package com.tom.storage.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;

import com.tom.storage.tileentity.inventory.ContainerCraftingTerminal;
import com.tom.storage.tileentity.inventory.ContainerCraftingTerminal.SlotTerminalCrafting;

public interface ICraftingTerminal extends ITerminal {
	InventoryCrafting getCraftingInv();

	IInventory getCraftResult();

	void craft(EntityPlayer playerIn, ContainerCraftingTerminal containerCraftingTerminal, SlotTerminalCrafting slot);
}