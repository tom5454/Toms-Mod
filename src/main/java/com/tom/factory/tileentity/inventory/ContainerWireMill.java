package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import com.tom.api.inventory.SlotOutput;
import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntityWireMill;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerWireMill extends ContainerTomsMod {
	private TileEntityWireMill te;

	public ContainerWireMill(InventoryPlayer playerInv, TileEntityWireMill te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 58, 35));
		addSlotToContainer(new SlotOutput(te, 1, 113, 36));
		addSlotToContainer(new SlotSpeedUpgrade(te, 2, 152, 63, 24));
		addPlayerSlots(playerInv, 8, 84);
		syncHandler.registerInt(0, te::getClientEnergyStored, i -> te.clientEnergy = i);
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerInventoryFieldShort(te, 1);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}
}