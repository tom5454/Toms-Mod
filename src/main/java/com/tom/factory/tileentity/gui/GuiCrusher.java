package com.tom.factory.tileentity.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.gui.GuiTomsLib;
import com.tom.factory.tileentity.TileEntityCrusher;
import com.tom.factory.tileentity.inventory.ContainerCrusher;

public class GuiCrusher extends GuiTomsLib {
	private TileEntityCrusher te;

	public GuiCrusher(InventoryPlayer playerInv, TileEntityCrusher te) {
		super(new ContainerCrusher(playerInv, te), "crusherGui");
		this.te = te;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p1Per = (te.clientEnergy * 1F) / te.getMaxEnergyStored();
		float p2Per = (te.getMaxProgress() - te.getField(0) * 1F) / te.getMaxProgress();
		double p1 = p1Per * 65;
		double p2 = p2Per * 51;
		drawTexturedModalRect(guiLeft + 10, guiTop + 76 - p1, 176, 65 - p1, 12, p1);
		if (te.getField(0) > 0)
			drawTexturedModalRect(guiLeft + 65, guiTop + 35, 176, 65, p2, 16);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 25, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.crusher.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}
}
