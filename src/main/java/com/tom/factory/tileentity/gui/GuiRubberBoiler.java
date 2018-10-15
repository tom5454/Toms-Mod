package com.tom.factory.tileentity.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.gui.GuiFluidTank;
import com.tom.api.gui.GuiTomsLib;
import com.tom.factory.tileentity.TileEntityRubberBoiler;
import com.tom.factory.tileentity.inventory.ContainerRubberBoiler;
import com.tom.util.TomsModUtils;

public class GuiRubberBoiler extends GuiTomsLib {
	private TileEntityRubberBoiler te;

	public GuiRubberBoiler(InventoryPlayer playerInv, TileEntityRubberBoiler te) {
		super(new ContainerRubberBoiler(playerInv, te), "rubberboilergui");
		this.te = te;
	}

	@Override
	public void initGui() {
		ySize = 176;
		labelList.clear();
		super.initGui();
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 30, guiTop + 8, te.getTankIn()).setUV(176, 0).setUV2(125, 138).setPaddingAndSize(4, 20, 55), labelList);
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 140, guiTop + 20, te.getTankOut()).setUV(176, 0).setUV2(125, 138).setPaddingAndSize(4, 20, 55), labelList);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.disableStandardItemLighting();
		renderFluidTooltips(mouseX, mouseY);
		if (isPointInRegion(12, 20, 8, 53, mouseX, mouseY)) {
			drawHoveringText(TomsModUtils.getStringList(te.clientHeat + " °C"), mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 6, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.rubberBoiler.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p1Per = (te.clientHeat * 1F) / te.maxHeat;
		float p1 = p1Per * 53;
		drawTexturedModalRect(guiLeft + 12, guiTop + 73 - p1, 176, 108 - p1, 8, p1);
		float p2Per = (te.getProgress() * 1F) / 100;
		double p2 = (1 - p2Per) * 51;
		if (te.getProgress() > 0)
			drawTexturedModalRect(guiLeft + 59, guiTop + 65, 176, 108, p2, 16);
	}
}
