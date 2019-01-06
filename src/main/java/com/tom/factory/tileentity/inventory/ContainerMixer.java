package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntityMixer;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerMixer extends ContainerTomsMod {
	private TileEntityMixer te;

	public ContainerMixer(InventoryPlayer playerInv, TileEntityMixer te) {
		this.te = te;
		int x = 72, y = 43;
		addSlotToContainer(new Slot(te, 0, x, y));
		addSlotToContainer(new Slot(te, 1, x + 18, y));
		addSlotToContainer(new Slot(te, 2, x, y + 18));
		addSlotToContainer(new Slot(te, 3, x + 18, y + 18));
		addSlotToContainer(new SlotSpeedUpgrade(te, 4, 152, 74, 24));
		addPlayerSlots(playerInv, 8, 94);
		syncHandler.registerTank(0, te.getTankIn());
		syncHandler.registerTank(1, te.getTankIn2());
		syncHandler.registerTank(2, te.getTankOut());
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerInventoryFieldShort(te, 1);
		syncHandler.registerInt(0, te::getClientEnergyStored, i -> te.clientEnergy = i);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

}