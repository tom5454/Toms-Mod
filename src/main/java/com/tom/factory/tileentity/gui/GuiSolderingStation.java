package com.tom.factory.tileentity.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.gui.GuiTomsLib;
import com.tom.factory.tileentity.TileEntitySolderingStation;
import com.tom.factory.tileentity.inventory.ContainerSolderingStation;
import com.tom.factory.tileentity.inventory.ContainerSteamSolderingStation;
import com.tom.util.TomsModUtils;

public class GuiSolderingStation extends GuiTomsLib {
	private TileEntitySolderingStation te;

	public GuiSolderingStation(InventoryPlayer playerInv, TileEntitySolderingStation te) {
		super(new ContainerSolderingStation(playerInv, te), "solderingStationGui");
		this.te = te;
		ySize = 176;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		{
			float p1Per = (te.clientEnergy * 1F) / te.getMaxEnergyStored();
			double p1 = p1Per * 65;
			drawTexturedModalRect(guiLeft + 8, guiTop + 80 - p1, 176, 154 - p1, 12, p1);
		}
		float p1Per = te.getField(2) / 4000F;
		float p2Per = ((float) te.getField(0)) / ContainerSteamSolderingStation.MAX_PROGRESS;
		double p1 = p1Per * 16;
		double p2 = p2Per * 51;
		drawTexturedModalRect(guiLeft + 124, guiTop + 22 - p1, 176, 16 - p1, 2, p1);
		if (te.getField(0) > 0)
			drawTexturedModalRect(guiLeft + 79, guiTop + 46, 176, 65, p2, 16);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.solderingStation.name");
		fontRenderer.drawString(s, 6, 6, 4210752);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (te.craftingError > 0) {
			mc.fontRenderer.drawString("!", guiLeft + 132, guiTop + 22, 0xFFFF0000);
			if (isPointInRegion(132, 22, 10, 10, mouseX, mouseY)) {
				drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.craftingError_" + te.craftingError)), mouseX, mouseY);
			}
		}
	}
}
