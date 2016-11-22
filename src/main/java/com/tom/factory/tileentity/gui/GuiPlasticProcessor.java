package com.tom.factory.tileentity.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.apis.TomsModUtils;
import com.tom.apis.TomsModUtils.GuiRenderRunnable;
import com.tom.factory.tileentity.TileEntityPlasticProcessor;
import com.tom.factory.tileentity.inventory.ContainerPlasticProcessor;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiPlasticProcessor extends GuiTomsMod {
	private TileEntityPlasticProcessor te;
	public GuiPlasticProcessor(InventoryPlayer playerInv, TileEntityPlasticProcessor te) {
		super(new ContainerPlasticProcessor(playerInv, te), "guiPlasticProcessor");
		this.te = te;
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.disableStandardItemLighting();
		drawFluidTankTooltip(24, 20, te.getTankWater(), mouseX, mouseY, I18n.format("tile.water.name"));
		drawFluidTankTooltip(46, 20, te.getTankKerosene(), mouseX, mouseY, I18n.format("fluid.kerosene"));
		drawFluidTankTooltip(68, 20, te.getTankLPG(), mouseX, mouseY, I18n.format("fluid.lpg"));
		drawFluidTankTooltip(90, 20, te.getTankCreosote(), mouseX, mouseY, I18n.format("fluid.creosote"));
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(I18n.format("container.inventory"), 6, ySize - 96 + 2, 4210752);
		String s = I18n.format("tomsmod.gui.plasticProcessor");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p1Per = (te.clientEnergy * 1F) / te.getMaxEnergyStored(null, null);
		double p1 = p1Per * 65;
		drawTexturedModalRect(guiLeft + 8, guiTop + 76 - p1, 176, 154 - p1, 12, p1);
		float p2Per = (TileEntityPlasticProcessor.MAX_PROGRESS - te.getField(0) * 1F) / TileEntityPlasticProcessor.MAX_PROGRESS;
		double p2 = p2Per * 31;
		if(te.getField(0) > 0)drawTexturedModalRect(guiLeft + 118, guiTop + 19, 176, 65, p2, 16);
	}
	@Override
	public void initGui() {
		ySize = 176;
		labelList.clear();
		super.initGui();
		TomsModUtils.addRunnableToLabelList(new GuiRenderRunnable() {

			@Override
			public void run(int mouseX, int mouseY) {
				drawFluidTank(guiLeft + 24, guiTop + 20, te.getTankWater(), 176, 0);
				drawFluidTank(guiLeft + 46, guiTop + 20, te.getTankKerosene(), 176, 0);
				drawFluidTank(guiLeft + 68, guiTop + 20, te.getTankLPG(), 176, 0);
				drawFluidTank(guiLeft + 90, guiTop + 20, te.getTankCreosote(), 176, 0);
			}
		}, labelList);
	}
}
