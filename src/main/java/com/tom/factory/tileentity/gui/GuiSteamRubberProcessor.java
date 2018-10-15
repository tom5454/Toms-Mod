package com.tom.factory.tileentity.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.MathHelper;

import com.tom.api.gui.GuiFluidTank;
import com.tom.api.gui.GuiTomsLib;
import com.tom.factory.tileentity.TileEntitySteamRubberProcessor;
import com.tom.factory.tileentity.inventory.ContainerSteamRubberProcessor;
import com.tom.util.TomsModUtils;

public class GuiSteamRubberProcessor extends GuiTomsLib {
	private TileEntitySteamRubberProcessor te;

	public GuiSteamRubberProcessor(InventoryPlayer playerInv, TileEntitySteamRubberProcessor te) {
		super(new ContainerSteamRubberProcessor(playerInv, te), "steamrubberprocessor");
		this.te = te;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p1Per = (te.getField(1) * 1F) / 1600;
		float p2Per = (TileEntitySteamRubberProcessor.MAX_PROCESS_TIME - te.getField(0) * 1F) / TileEntitySteamRubberProcessor.MAX_PROCESS_TIME;
		int p1 = MathHelper.floor(p1Per * 18);
		double p2 = p2Per * 51;
		drawTexturedModalRect(guiLeft + 39, guiTop + 51 - p1, 176, 101 - p1, 2, p1);
		if (te.getField(0) > 0)
			drawTexturedModalRect(guiLeft + 65, guiTop + 35, 176, 65, p2, 16);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 25, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.rubberProcessor.name");
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
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 8, guiTop + 15, te.getTankIn()).setUV(176, 0).setUV2(125, 138).setPaddingAndSize(4, 20, 55), labelList);
	}
}
