package com.tom.storage.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.inventory.SlotPhantom;
import com.tom.storage.multipart.PartExportBus;
import com.tom.storage.tileentity.inventory.ContainerImportBus.SlotSpeedCard;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerExportBus extends ContainerTomsMod {
	// private PartExportBus part;
	public ContainerExportBus(PartExportBus bus, InventoryPlayer playerInv) {
		// part = bus;
		int i = 0, x = 57, y = 22;
		addSlotToContainer(new SlotPhantom(bus.filterInv, i, x, y));
		addSlotToContainer(new SlotPhantom(bus.filterInv, i + 1, x + 18, y));
		addSlotToContainer(new SlotPhantom(bus.filterInv, i + 2, x + 36, y));
		i += 3;
		y += 18;
		addSlotToContainer(new SlotPhantom(bus.filterInv, i, x, y));
		addSlotToContainer(new SlotPhantom(bus.filterInv, i + 1, x + 18, y));
		addSlotToContainer(new SlotPhantom(bus.filterInv, i + 2, x + 36, y));
		i += 3;
		y += 18;
		addSlotToContainer(new SlotPhantom(bus.filterInv, i, x, y));
		addSlotToContainer(new SlotPhantom(bus.filterInv, i + 1, x + 18, y));
		addSlotToContainer(new SlotPhantom(bus.filterInv, i + 2, x + 36, y));
		addSlotToContainer(new SlotSpeedCard(bus.upgradeInv, 0, 140, 36, 8));
		addPlayerSlots(playerInv, 8, 94);
		syncHandler.setReceiver(bus);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}
