package com.tom.storage.tileentity.gui;

import net.minecraft.entity.player.InventoryPlayer;

import com.tom.storage.multipart.PartStorageBus;
import com.tom.storage.tileentity.inventory.ContainerStorageBus;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiStorageBus extends GuiTomsMod {

	public GuiStorageBus(PartStorageBus bus,
			InventoryPlayer inventory) {
		super(new ContainerStorageBus(bus, inventory), "guiStorageBus");
	}
	@Override
	public void initGui() {
		ySize = 176;
		super.initGui();
	}
}
