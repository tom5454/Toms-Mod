package com.tom.storage.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.apis.TomsModUtils;
import com.tom.storage.tileentity.TileEntityStorageNetworkController;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerController extends ContainerTomsMod {
	private TileEntityStorageNetworkController te;

	public ContainerController(InventoryPlayer playerInv, TileEntityStorageNetworkController te) {
		this.te = te;
		addPlayerSlots(playerInv, 8, 94);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return TomsModUtils.isUseable(playerIn, te);
	}

}
