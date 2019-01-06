package com.tom.storage.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.inventory.IStorageInventory.BasicFilter.Mode;
import com.tom.api.inventory.SlotPhantom;
import com.tom.storage.multipart.PartStorageBus;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerStorageBus extends ContainerTomsMod {
	//private PartStorageBus part;

	public ContainerStorageBus(PartStorageBus bus, InventoryPlayer inventory) {
		//this.part = bus;
		for (int i = 0;i < 4;++i) {
			for (int j = 0;j < 9;++j) {
				addSlotToContainer(new SlotPhantom(bus.filterInv, j + i * 9, 8 + j * 18, 11 + i * 18));
			}
		}
		addPlayerSlots(inventory, 8, 94);
		syncHandler.registerBoolean(0, bus::isWhiteList, bus::setWhiteList);
		syncHandler.registerBoolean(1, bus::isCheckMeta, bus::setCheckMeta);
		syncHandler.registerBoolean(2, bus::isCheckNBT, bus::setCheckNBT);
		syncHandler.registerBoolean(3, bus::isCheckMod, bus::setCheckMod);
		syncHandler.registerBoolean(4, bus::isCanViewAll, bus::setCanViewAll);
		syncHandler.registerEnum(5, bus::getMode, bus::setMode, Mode.VALUES);
		syncHandler.setReceiver(bus);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}
