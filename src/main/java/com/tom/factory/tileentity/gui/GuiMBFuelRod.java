package com.tom.factory.tileentity.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.gui.GuiTomsLib;
import com.tom.factory.tileentity.TileEntityMultiblockController;
import com.tom.factory.tileentity.inventory.ContainerMBFuelRod;

public class GuiMBFuelRod extends GuiTomsLib {

	public GuiMBFuelRod(InventoryPlayer inventory, TileEntityMultiblockController tileEntity) {
		super(new ContainerMBFuelRod(inventory, tileEntity), "fuelrodgui");
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 25, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.mbFuelRod.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}

	@Override
	public void initGui() {
		ySize = 176;
		super.initGui();
	}
}
