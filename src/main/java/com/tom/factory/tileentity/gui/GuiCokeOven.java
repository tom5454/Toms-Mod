package com.tom.factory.tileentity.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.gui.GuiFluidTank;
import com.tom.api.gui.GuiTomsLib;
import com.tom.factory.tileentity.TileEntityCokeOven;
import com.tom.factory.tileentity.inventory.ContainerCokeOven;
import com.tom.util.TomsModUtils;

public class GuiCokeOven extends GuiTomsLib {
	private TileEntityCokeOven te;

	public GuiCokeOven(InventoryPlayer playerInv, TileEntityCokeOven te) {
		super(new ContainerCokeOven(playerInv, te), "cokeOvenGui");
		this.te = te;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 6, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.cokeOven.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}

	@Override
	public void initGui() {
		ySize = 176;
		labelList.clear();
		super.initGui();
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 130, guiTop + 20, te.getTank()), labelList);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p1Per = te.getField(0) / 100F;
		float p1 = (1 - p1Per) * 22;
		if (te.getField(0) > 0)
			drawTexturedModalRect(guiLeft + 53, guiTop + 41, 176, 0, p1, 16);
		float p2 = p1Per * 13;
		drawTexturedModalRect(guiLeft + 29, guiTop + 38 - p2, 176, 29 - p2, 13, p2);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.disableStandardItemLighting();
		renderFluidTooltips(mouseX, mouseY);
	}
}
