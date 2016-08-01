package com.tom.storage.tileentity.gui;

import com.tom.core.tileentity.gui.GuiTomsMod;
import com.tom.storage.multipart.PartExportBus;
import com.tom.storage.tileentity.inventory.ContainerExportBus;

import net.minecraft.entity.player.InventoryPlayer;

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
