package com.tom.factory.tileentity.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.gui.GuiTomsMod;
import com.tom.factory.tileentity.TileEntitySteamPlateBendingMachine;
import com.tom.factory.tileentity.inventory.ContainerSteamPlateBender;

public class GuiSteamPlateBender extends GuiTomsMod {
	private TileEntitySteamPlateBendingMachine te;

	public GuiSteamPlateBender(InventoryPlayer playerInv, TileEntitySteamPlateBendingMachine te) {
		super(new ContainerSteamPlateBender(playerInv, te), "steamPlateBenderGui");
		this.te = te;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p2Per = (TileEntitySteamPlateBendingMachine.MAX_PROCESS_TIME - te.getField(0) * 1F) / TileEntitySteamPlateBendingMachine.MAX_PROCESS_TIME;
		double p2 = p2Per * 51;
		if (te.getField(0) > 0)
			drawTexturedModalRect(guiLeft + 65, guiTop + 35, 176, 65, p2, 16);
		renderGearbox(guiLeft + 150, guiTop + 10, te.clientCanRun);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 25, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.steam.plateBendingMachine.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}
}