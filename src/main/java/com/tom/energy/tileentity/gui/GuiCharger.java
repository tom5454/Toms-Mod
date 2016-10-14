package com.tom.energy.tileentity.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.core.tileentity.gui.GuiTomsMod;

import com.tom.energy.tileentity.TileEntityCharger;
import com.tom.energy.tileentity.inventory.ContainerCharger;

public class GuiCharger extends GuiTomsMod {

	private TileEntityCharger te;
	public GuiCharger(InventoryPlayer playerInv, TileEntityCharger te) {
		super(new ContainerCharger(playerInv, te), "chargerGui");
		this.te = te;
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p1Per = (te.clientEnergy * 1F) / te.getMaxEnergyStored();
		float p2Per = te.p / 100F;
		double p1 = p1Per * 65;
		double p2 = p2Per * 16;
		drawTexturedModalRect(guiLeft + 10, guiTop + 76 - p1, 176, 65 - p1, 12, p1);
		if(te.p > 0)drawTexturedModalRect(guiLeft + 80, guiTop + 51 - p2, 176, 81 - p2, 20, p2);
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(I18n.format("container.inventory"), 25, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.charger.name");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
	}

}
