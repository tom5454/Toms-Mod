package com.tom.storage.tileentity.gui;

import net.minecraft.entity.player.InventoryPlayer;

import com.tom.storage.multipart.PartExportBus;
import com.tom.storage.tileentity.inventory.ContainerExportBus;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiExportBus extends GuiTomsMod {

	public GuiExportBus(PartExportBus bus, InventoryPlayer playerInv) {
		super(new ContainerExportBus(bus, playerInv),"GuiImportBus");
	}
	@Override
	public void initGui() {
		ySize = 176;
		super.initGui();
	}
}
