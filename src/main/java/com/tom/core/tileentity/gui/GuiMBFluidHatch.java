package com.tom.core.tileentity.gui;

import com.tom.core.tileentity.inventory.ContainerMBFluidHatch;
import com.tom.factory.tileentity.TileEntityMBFluidPort;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiMBFluidHatch extends GuiTomsMod{
	public GuiMBFluidHatch(InventoryPlayer playerInv, TileEntityMBFluidPort tileEntityMBFluidPort) {
		super(new ContainerMBFluidHatch(playerInv, tileEntityMBFluidPort), "mbFluidPort");
	}
	public void drawGuiContainerForegroundLayer(int mX, int my){
		String s = I18n.format("tomsmod.gui.mbFluidHatch");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}
}
