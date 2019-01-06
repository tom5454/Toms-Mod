package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import com.tom.api.inventory.SlotOutput;
import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntityFluidTransposer;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerFluidTransposer extends ContainerTomsMod {
	private TileEntityFluidTransposer te;

	public ContainerFluidTransposer(InventoryPlayer playerInv, TileEntityFluidTransposer te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 41, 21));
		addSlotToContainer(new Slot(te, 1, 77, 21));
		addSlotToContainer(new SlotOutput(te, 2, 77, 59));
		addSlotToContainer(new SlotSpeedUpgrade(te, 3, 152, 74, 24));
		addPlayerSlots(playerInv, 8, 94);
		syncHandler.registerTank(0, te.getTank());
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerInventoryFieldShort(te, 1);
		syncHandler.registerInt(0, te::getClientEnergyStored, i -> te.clientEnergy = i);
		syncHandler.registerBoolean(2, te::getMode, te::setMode);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

}
