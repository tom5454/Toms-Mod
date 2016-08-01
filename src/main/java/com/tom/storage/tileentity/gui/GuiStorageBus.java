package com.tom.storage.tileentity.gui;

import com.tom.core.tileentity.gui.GuiTomsMod;
import com.tom.storage.multipart.PartStorageBus;
import com.tom.storage.tileentity.inventory.ContainerStorageBus;

import net.minecraft.entity.player.InventoryPlayer;

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
