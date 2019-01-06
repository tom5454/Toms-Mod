package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import com.tom.api.inventory.SlotOutput;
import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntityAlloySmelter;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerAlloySmelter extends ContainerTomsMod {
	private TileEntityAlloySmelter te;

	public ContainerAlloySmelter(InventoryPlayer playerInv, TileEntityAlloySmelter te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 26, 35));
		addSlotToContainer(new Slot(te, 1, 44, 35));
		addSlotToContainer(new SlotOutput(te, 2, 130, 36));
		addSlotToContainer(new SlotSpeedUpgrade(te, 3, 152, 63, 24));
		addPlayerSlots(playerInv, 8, 84);
		syncHandler.registerInt(0, te::getClientEnergyStored, d -> te.clientEnergy = d);
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerInventoryFieldShort(te, 1);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}
}