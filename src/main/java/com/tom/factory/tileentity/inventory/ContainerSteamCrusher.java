package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import com.tom.api.inventory.SlotOutput;
import com.tom.factory.tileentity.TileEntitySteamCrusher;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSteamCrusher extends ContainerTomsMod {
	private TileEntitySteamCrusher te;

	public ContainerSteamCrusher(InventoryPlayer playerInv, TileEntitySteamCrusher te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 44, 35));
		addSlotToContainer(new SlotOutput(te, 1, 126, 36));
		addSlotToContainer(new SlotOutput(te, 2, 144, 36));
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
