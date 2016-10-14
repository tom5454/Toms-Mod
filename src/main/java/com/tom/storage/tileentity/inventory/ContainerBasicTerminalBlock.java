package com.tom.storage.tileentity.inventory;

import net.minecraft.entity.player.InventoryPlayer;

import com.tom.storage.tileentity.TileEntityBasicTerminal;

public class ContainerBasicTerminalBlock extends ContainerTerminalBase{
	public ContainerBasicTerminalBlock(InventoryPlayer playerInv, TileEntityBasicTerminal te) {
		super(te, playerInv.player);
		addStorageSlots(5, 8, 18);
		this.addPlayerSlots(playerInv, 8, 117);
	}
}
