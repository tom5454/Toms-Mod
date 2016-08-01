package com.tom.storage.tileentity.gui;

import com.tom.core.tileentity.gui.GuiTomsMod;
import com.tom.storage.tileentity.TileEntityDrive;
import com.tom.storage.tileentity.inventory.ContainerDrive;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiDrive extends GuiTomsMod {

	public GuiDrive(InventoryPlayer playerInv, TileEntityDrive te) {
		super(new ContainerDrive(playerInv, te), "guiDrive");
	}
	@Override
	public void initGui() {
		ySize = 199;
		super.initGui();
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tms.drive.name");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 5, 4210752);
	}
}
