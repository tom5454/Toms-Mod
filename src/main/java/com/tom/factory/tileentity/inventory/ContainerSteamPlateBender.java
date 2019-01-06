package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import com.tom.api.inventory.SlotOutput;
import com.tom.factory.tileentity.TileEntitySteamPlateBendingMachine;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSteamPlateBender extends ContainerTomsMod {
	private TileEntitySteamPlateBendingMachine te;

	public ContainerSteamPlateBender(InventoryPlayer playerInv, TileEntitySteamPlateBendingMachine te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 44, 35));
		addSlotToContainer(new SlotOutput(te, 1, 130, 36));
		addPlayerSlots(playerInv, 8, 84);
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerBoolean(1, te::canRun, te::setClCanRun);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

}