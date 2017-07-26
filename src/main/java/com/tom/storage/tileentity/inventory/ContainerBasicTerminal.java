package com.tom.storage.tileentity.inventory;

import net.minecraft.entity.player.InventoryPlayer;

import com.tom.storage.handler.ITerminal;

public class ContainerBasicTerminal extends ContainerTerminalBase {
	public ContainerBasicTerminal(InventoryPlayer playerInv, ITerminal te) {
		super(te, playerInv.player);
		addStorageSlots(5, 8, 18);
		this.addPlayerSlots(playerInv, 8, 120);
	}
}
