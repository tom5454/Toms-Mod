package com.tom.factory.tileentity.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.gui.GuiTomsLib;
import com.tom.factory.tileentity.TileEntitySteamSolderingStation;
import com.tom.factory.tileentity.inventory.ContainerSteamSolderingStation;
import com.tom.util.TomsModUtils;

public class GuiSteamSolderingStation extends GuiTomsLib {
	private TileEntitySteamSolderingStation te;

	public GuiSteamSolderingStation(InventoryPlayer playerInv, TileEntitySteamSolderingStation te) {
		super(new ContainerSteamSolderingStation(playerInv, te), "steamSolderingStationGui");
		this.te = te;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p1Per = te.getField(2) / 1000F;
		float p2Per = ((float) te.getField(0)) / ContainerSteamSolderingStation.MAX_PROGRESS;
		double p1 = p1Per * 16;
		double p2 = p2Per * 51;
		drawTexturedModalRect(guiLeft + 69, guiTop + 23 - p1, 176, 16 - p1, 2, p1);
		if (te.getField(0) > 0)
			drawTexturedModalRect(guiLeft + 65, guiTop + 35, 176, 65, p2, 16);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
		// String s = I18n.format("tile.tm.steamSolderingStation.name");
		// fontRendererObj.drawString(s, xSize / 2 -
		// fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (te.craftingError > 0) {
			fontRenderer.drawString("!", guiLeft + 132, guiTop + 22, 0xFFFF0000);
			if (isPointInRegion(132, 22, 10, 10, mouseX, mouseY)) {
				drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.craftingError_" + te.craftingError)), mouseX, mouseY);
			}
		}
	}
}
