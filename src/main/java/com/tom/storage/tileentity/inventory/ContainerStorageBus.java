package com.tom.storage.tileentity.inventory;

import com.tom.api.inventory.SlotPhantom;
import com.tom.core.tileentity.inventory.ContainerTomsMod;
import com.tom.storage.multipart.PartStorageBus;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerStorageBus extends ContainerTomsMod {

	public ContainerStorageBus(PartStorageBus bus,
			InventoryPlayer inventory) {
		for(int i = 0; i < 4; ++i) {
			for(int j = 0; j < 9; ++j) {
				addSlotToContainer(new SlotPhantom(bus.filterInv, j + i * 9, 8 + j * 18, 11 + i * 18));
			}
		}
		addPlayerSlots(inventory, 8, 94);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return null;
	}

}
