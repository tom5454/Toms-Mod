package com.tom.storage.tileentity.gui;

import net.minecraft.entity.player.InventoryPlayer;

import com.tom.storage.multipart.PartExportBus;
import com.tom.storage.tileentity.inventory.ContainerExportBus;

public class GuiExportBus extends GuiMultipartBase {

	public GuiExportBus(PartExportBus bus, InventoryPlayer playerInv) {
		super(new ContainerExportBus(bus, playerInv), "GuiImportBus", bus);
	}

	@Override
	public void initGui() {
		ySize = 176;
		super.initGui();
	}
}
