package com.tom.factory.tileentity.gui;

import com.tom.core.tileentity.gui.GuiTomsMod;
import com.tom.factory.tileentity.TileEntityElectricFurnaceAdv;
import com.tom.factory.tileentity.inventory.ContainerAdvElectricFurnace;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiAdvElectricFurnace extends GuiTomsMod {
	private TileEntityElectricFurnaceAdv te;
	public GuiAdvElectricFurnace(InventoryPlayer playerInv, TileEntityElectricFurnaceAdv te) {
		super(new ContainerAdvElectricFurnace(playerInv, te), "eFurnaceAdvGui");
		this.te = te;
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p1Per = (te.clientEnergy * 1F) / te.getMaxEnergyStored();
		float p2Per = (TileEntityElectricFurnaceAdv.MAX_PROCESS_TIME - te.getField(0) * 1F) / TileEntityElectricFurnaceAdv.MAX_PROCESS_TIME;
		double p1 = p1Per * 65;
		double p2 = p2Per * 51;
		drawTexturedModalRect(guiLeft + 10, guiTop + 76 - p1, 176, 65 - p1, 12, p1);
		if(te.getField(0) > 0)drawTexturedModalRect(guiLeft + 65, guiTop + 35, 176, 65, p2, 16);
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(I18n.format("container.inventory"), 25, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.advElectricFurnace.name");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
	}
}