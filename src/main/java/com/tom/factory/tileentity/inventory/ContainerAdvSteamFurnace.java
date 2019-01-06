package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;

import com.tom.factory.tileentity.TileEntitySteamFurnaceAdv;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerAdvSteamFurnace extends ContainerTomsMod {
	private TileEntitySteamFurnaceAdv te;

	public ContainerAdvSteamFurnace(InventoryPlayer playerInv, TileEntitySteamFurnaceAdv te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 44, 35));
		addSlotToContainer(new SlotFurnaceOutput(playerInv.player, te, 1, 130, 36));
		addPlayerSlots(playerInv, 8, 84);
		syncHandler.registerInventoryFieldInt(te, 0);
		syncHandler.registerBoolean(1, te::canRun, b -> te.clientCanRun = b);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

}