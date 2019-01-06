package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;

import com.tom.factory.tileentity.TileEntitySteamFurnace;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSteamFurnace extends ContainerTomsMod {
	private TileEntitySteamFurnace te;

	public ContainerSteamFurnace(InventoryPlayer playerInv, TileEntitySteamFurnace te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 44, 35));
		addSlotToContainer(new SlotFurnaceOutput(playerInv.player, te, 1, 130, 36));
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