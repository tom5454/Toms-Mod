package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import com.tom.api.inventory.SlotOutput;
import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntityPlasticProcessor;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerPlasticProcessor extends ContainerTomsMod {
	private TileEntityPlasticProcessor te;

	public ContainerPlasticProcessor(InventoryPlayer playerInv, TileEntityPlasticProcessor te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 118, 44));
		addSlotToContainer(new Slot(te, 1, 118, 62));
		addSlotToContainer(new SlotOutput(te, 2, 152, 18));
		addSlotToContainer(new SlotSpeedUpgrade(te, 3, 152, 74, 4));
		addPlayerSlots(playerInv, 8, 94);
		syncHandler.registerTank(0, te.getTankWater());
		syncHandler.registerTank(1, te.getTankCreosote());
		syncHandler.registerTank(2, te.getTankKerosene());
		syncHandler.registerTank(3, te.getTankLPG());
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerInt(0, te::getClientEnergyStored, i -> te.clientEnergy = i);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}
}