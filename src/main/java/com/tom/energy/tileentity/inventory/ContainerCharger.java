package com.tom.energy.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import com.tom.api.inventory.SlotOutput;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

import com.tom.energy.tileentity.TileEntityCharger;

public class ContainerCharger extends ContainerTomsMod {
	private TileEntityCharger te;

	public ContainerCharger(InventoryPlayer playerInv, TileEntityCharger te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 44, 35));
		addSlotToContainer(new SlotOutput(te, 1, 130, 36));
		addPlayerSlots(playerInv, 8, 84);
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerInt(0, te::getClientEnergyStored, d -> te.clientEnergy = d);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

}
