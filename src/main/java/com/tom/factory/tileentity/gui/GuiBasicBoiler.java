package com.tom.factory.tileentity.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.gui.GuiFluidTank;
import com.tom.apis.TomsModUtils;
import com.tom.factory.tileentity.TileEntityBasicBoiler;
import com.tom.factory.tileentity.inventory.ContainerBasicBoiler;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiBasicBoiler extends GuiTomsMod {
	private TileEntityBasicBoiler te;

	public GuiBasicBoiler(InventoryPlayer playerInv, TileEntityBasicBoiler te) {
		super(new ContainerBasicBoiler(playerInv, te), "basicBoilerGui");
		this.te = te;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.disableStandardItemLighting();
		renderFluidTooltips(mouseX, mouseY);
		if (isPointInRegion(71, 27, 8, 53, mouseX, mouseY)) {
			drawHoveringText(TomsModUtils.getStringList(te.clientHeat + " °C"), mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 6, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.basicBoiler.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p1Per = (te.clientHeat * 1F) / TileEntityBasicBoiler.MAX_TEMP;
		float p1 = p1Per * 53;
		drawTexturedModalRect(guiLeft + 71, guiTop + 80 - p1, 176, 108 - p1, 8, p1);
		float p2Per = (te.getBurnTime() * 1F) / 100;
		float p2 = p2Per * 13;
		drawTexturedModalRect(guiLeft + 44, guiTop + 60 - p2, 176, 121 - p2, 13, p2);
	}

	@Override
	public void initGui() {
		ySize = 176;
		labelList.clear();
		super.initGui();
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 8, guiTop + 20, te.getTankWater()).setUV(176, 0), labelList);
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 100, guiTop + 20, te.getTankSteam()).setUV(176, 0), labelList);
	}
}
