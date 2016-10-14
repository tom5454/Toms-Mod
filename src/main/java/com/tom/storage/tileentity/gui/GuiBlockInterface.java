package com.tom.storage.tileentity.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.storage.tileentity.TileEntityInterface;
import com.tom.storage.tileentity.inventory.ContainerBlockInterface;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiBlockInterface extends GuiTomsMod {

	public GuiBlockInterface(InventoryPlayer playerInv, TileEntityInterface te) {
		super(new ContainerBlockInterface(playerInv, te), "interfaceGui");
	}
	@Override
	public void initGui() {
		ySize = 176;
		super.initGui();
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(I18n.format("tile.tm.interface.name"), 6, 6, 4210752);
		drawInventoryText(ySize - 92);
	}
}
