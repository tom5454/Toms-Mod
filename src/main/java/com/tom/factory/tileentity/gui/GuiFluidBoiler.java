package com.tom.factory.tileentity.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.apis.TomsModUtils;
import com.tom.apis.TomsModUtils.GuiRenderRunnable;
import com.tom.factory.tileentity.TileEntityFluidBoiler;
import com.tom.factory.tileentity.inventory.ContainerFluidBoiler;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiFluidBoiler extends GuiTomsMod {
	private TileEntityFluidBoiler te;
	public GuiFluidBoiler(InventoryPlayer playerInv, TileEntityFluidBoiler te) {
		super(new ContainerFluidBoiler(playerInv, te), "fluidBoilerGui");
		this.te = te;
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.disableStandardItemLighting();
		drawFluidTankTooltip(8, 20, te.getTankWater(), mouseX, mouseY);
		drawFluidTankTooltip(100, 20, te.getTankSteam(), mouseX, mouseY);
		drawFluidTankTooltip(30, 20, te.getTankFuel(), mouseX, mouseY);
		if(isPointInRegion(71, 27, 8, 53, mouseX, mouseY)){
			drawHoveringText(TomsModUtils.getStringList(te.clientHeat+" °C"), mouseX, mouseY);
		}
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(I18n.format("container.inventory"), 6, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.fluidBoiler.name");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p1Per = (te.clientHeat * 1F) / TileEntityFluidBoiler.MAX_TEMP;
		float p1 = p1Per * 53;
		drawTexturedModalRect(guiLeft + 71, guiTop + 80 - p1, 176, 108 - p1, 8, p1);
	}
	@Override
	public void initGui() {
		ySize = 176;
		super.initGui();
		TomsModUtils.addRunnableToLabelList(new GuiRenderRunnable() {

			@Override
			public void run(int mouseX, int mouseY) {
				drawFluidTank(guiLeft + 8, guiTop + 20, te.getTankWater(), 176, 0);
				drawFluidTank(guiLeft + 100, guiTop + 20, te.getTankSteam(), 176, 0);
				drawFluidTank(guiLeft + 30, guiTop + 20, te.getTankFuel(), 176, 0);
			}
		}, labelList);
	}
}
