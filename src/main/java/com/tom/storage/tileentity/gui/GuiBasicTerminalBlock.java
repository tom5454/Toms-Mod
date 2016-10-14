package com.tom.storage.tileentity.gui;

import net.minecraft.entity.player.InventoryPlayer;

import com.tom.storage.tileentity.TileEntityBasicTerminal;
import com.tom.storage.tileentity.inventory.ContainerBasicTerminalBlock;

public class GuiBasicTerminalBlock extends GuiTerminalBase {

	public GuiBasicTerminalBlock(InventoryPlayer playerInv, TileEntityBasicTerminal te) {
		super(new ContainerBasicTerminalBlock(playerInv, te), "guiStorageTerminal", te, 5);
	}
	@Override
	public void initGui() {
		xSize = 194;
		ySize = 199;
		super.initGui();
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString("Terminal", 6, 6, 4210752);
		drawInventoryText(ySize - 92);
	}
}
