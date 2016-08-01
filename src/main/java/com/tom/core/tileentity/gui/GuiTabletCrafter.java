package com.tom.core.tileentity.gui;

import com.tom.core.tileentity.TileEntityTabletCrafter;
import com.tom.core.tileentity.inventory.ContainerTabletCrafter;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiTabletCrafter extends GuiTomsMod {

	public GuiTabletCrafter(InventoryPlayer playerInv, TileEntityTabletCrafter te) {
		super(new ContainerTabletCrafter(playerInv, te), "tabletCrafter");
	}
	public void drawGuiContainerForegroundLayer(int mX, int my){
		String s = I18n.format("tomsmod.gui.tabCrafter");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}
}
