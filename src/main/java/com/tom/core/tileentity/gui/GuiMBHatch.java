package com.tom.core.tileentity.gui;

import com.tom.core.tileentity.inventory.ContainerMBHatch;
import com.tom.factory.tileentity.TileEntityMBHatch;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiMBHatch extends GuiTomsMod{

	public GuiMBHatch(InventoryPlayer playerInv, TileEntityMBHatch te) {
		super(new ContainerMBHatch(playerInv, te), "mbHatch");
	}
	public void drawGuiContainerForegroundLayer(int mX, int my){
		String s = I18n.format("tomsmod.gui.mbHatch");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

}
