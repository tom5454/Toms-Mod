package com.tom.core.tileentity.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.gui.GuiTomsMod;

import com.tom.core.tileentity.TileEntityTabletCrafter;
import com.tom.core.tileentity.inventory.ContainerTabletCrafter;

public class GuiTabletCrafter extends GuiTomsMod {

	public GuiTabletCrafter(InventoryPlayer playerInv, TileEntityTabletCrafter te) {
		super(new ContainerTabletCrafter(playerInv, te), "tabletCrafter");
	}

	public void drawGuiContainerForegroundLayer(int mX, int my) {
		String s = I18n.format("tomsmod.gui.tabCrafter");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}
}
