package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import com.tom.api.inventory.SlotOutput;
import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntityElectricalRubberProcessor;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerElectricalRubberProcessor extends ContainerTomsMod {
	private TileEntityElectricalRubberProcessor te;

	public ContainerElectricalRubberProcessor(InventoryPlayer playerInv, TileEntityElectricalRubberProcessor te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 65, 25));
		addSlotToContainer(new Slot(te, 1, 65, 43));
		addSlotToContainer(new SlotOutput(te, 2, 148, 36));
		addSlotToContainer(new SlotSpeedUpgrade(te, 3, 152, 63, 24));
		addPlayerSlots(playerInv, 8, 84);
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerInventoryFieldShort(te, 1);
		syncHandler.registerInventoryFieldShort(te, 2);
		syncHandler.registerInt(0, te::getClientEnergyStored, i -> te.clientEnergy = i);
		syncHandler.registerTank(0, te.getTankIn());
		syncHandler.registerTank(1, te.getTankCresin());
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}
}
