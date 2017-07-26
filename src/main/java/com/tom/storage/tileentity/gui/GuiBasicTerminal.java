package com.tom.storage.tileentity.gui;

import net.minecraft.entity.player.InventoryPlayer;

import com.tom.storage.handler.ITerminal;
import com.tom.storage.tileentity.inventory.ContainerBasicTerminal;

public class GuiBasicTerminal extends GuiTerminalBase {

	public GuiBasicTerminal(InventoryPlayer playerInv, ITerminal te) {
		super(new ContainerBasicTerminal(playerInv, te), "guiStorageTerminal", te, 5, 202, 7, 17);
	}

	@Override
	public void initGui() {
		xSize = 194;
		super.initGui();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString("Terminal", 6, 6, 4210752);
		drawInventoryText(ySize - 92);
	}
}
