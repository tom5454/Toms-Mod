package com.tom.factory.tileentity.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.gui.GuiTomsLib;
import com.tom.factory.tileentity.TileEntityIndustrialBlastFurnace;
import com.tom.factory.tileentity.inventory.ContainerIndustrialBlastFurnace;

public class GuiIndustrialBlastFurnace extends GuiTomsLib {

	public GuiIndustrialBlastFurnace(InventoryPlayer playerInv, TileEntityIndustrialBlastFurnace te) {
		super(new ContainerIndustrialBlastFurnace(playerInv, te), "industrialBlastFurnaceGui");
		this.te = te;
	}

	private int heat;
	private TileEntityIndustrialBlastFurnace te;

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p1Per = (te.clientEnergy * 1F) / te.getMaxEnergyStored(null, null);
		float p2Per = (te.getField(0) * 1F) / 100;
		double p1 = p1Per * 65;
		double p2 = p2Per * 51;
		drawTexturedModalRect(guiLeft + 8, guiTop + 80 - p1, 176, 154 - p1, 12, p1);
		if (te.getField(0) > 0)
			drawTexturedModalRect(guiLeft + 61, guiTop + 46, 176, 65, p2, 16);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 25, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.industrialBlastFurnace.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		if (heat < 1)
			fontRenderer.drawString("M", 164, ySize - 96 + 2, 0xFF0000);
		else
			fontRenderer.drawString(I18n.format("tomsmod.gui.heatCap", heat), 25, ySize - 102, 4210752);
	}

	@Override
	public void initGui() {
		ySize = 176;
		super.initGui();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		heat = te.checkIfMerged(te.getWorld().getBlockState(te.getPos()));
	}
}
