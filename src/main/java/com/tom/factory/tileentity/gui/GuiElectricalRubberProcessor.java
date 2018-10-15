package com.tom.factory.tileentity.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.MathHelper;

import com.tom.api.gui.GuiFluidTank;
import com.tom.api.gui.GuiTomsLib;
import com.tom.factory.tileentity.TileEntityElectricalRubberProcessor;
import com.tom.factory.tileentity.inventory.ContainerElectricalRubberProcessor;
import com.tom.util.TomsModUtils;

public class GuiElectricalRubberProcessor extends GuiTomsLib {
	private TileEntityElectricalRubberProcessor te;

	public GuiElectricalRubberProcessor(InventoryPlayer playerInv, TileEntityElectricalRubberProcessor te) {
		super(new ContainerElectricalRubberProcessor(playerInv, te), "erubberprocessor");
		this.te = te;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p1Per = (te.getField(2) * 1F) / 1600;
		float p2Per = (te.getMaxProgress() - te.getField(0) * 1F) / te.getMaxProgress();
		float p3Per = (te.clientEnergy * 1F) / te.getMaxEnergyStored();
		int p1 = MathHelper.floor(p1Per * 18);
		double p2 = p2Per * 51;
		double p3 = p3Per * 65;
		drawTexturedModalRect(guiLeft + 60, guiTop + 59 - p1, 176, 160 - p1, 2, p1);
		if (te.getField(0) > 0)
			drawTexturedModalRect(guiLeft + 83, guiTop + 35, 176, 65, p2, 16);
		drawTexturedModalRect(guiLeft + 10, guiTop + 76 - p3, 176, 65 - p3, 12, p3);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 25, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.erubberProcessor.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.disableStandardItemLighting();
		renderFluidTooltips(mouseX, mouseY);
	}

	@Override
	public void initGui() {
		labelList.clear();
		super.initGui();
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 21, guiTop + 15, te.getTankIn()).setUV(176, 87).setUV2(125, 138).setPaddingAndSize(4, 20, 55), labelList);
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 36, guiTop + 15, te.getTankCresin()).setUV(176, 87).setUV2(125, 138).setPaddingAndSize(4, 20, 55), labelList);
	}
}
