package com.tom.factory.tileentity.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.apis.TomsModUtils;
import com.tom.apis.TomsModUtils.GuiRenderRunnable;
import com.tom.factory.tileentity.TileEntityMixer;
import com.tom.factory.tileentity.inventory.ContainerMixer;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiMixer extends GuiTomsMod {
	private TileEntityMixer te;
	public GuiMixer(InventoryPlayer playerInv, TileEntityMixer te) {
		super(new ContainerMixer(playerInv, te), "guiMixer");
		this.te = te;
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p1Per = (te.clientEnergy * 1F) / te.getMaxEnergyStored(null, null);
		double p1 = p1Per * 65;
		drawTexturedModalRect(guiLeft + 8, guiTop + 76 - p1, 176, 154 - p1, 12, p1);
		float p2Per = (te.getMaxProgress() - te.getField(0) * 1F) / te.getMaxProgress();
		double p2 = p2Per * 51;
		if(te.getField(0) > 0)drawTexturedModalRect(guiLeft + 65, guiTop + 19, 176, 65, p2, 16);
	}
	@Override
	public void initGui() {
		ySize = 176;
		labelList.clear();
		super.initGui();
		TomsModUtils.addRunnableToLabelList(new GuiRenderRunnable() {

			@Override
			public void run(int mouseX, int mouseY) {
				drawFluidTank(guiLeft + 25, guiTop + 15, te.getTankIn(), 176, 0);
				drawFluidTank(guiLeft + 45, guiTop + 15, te.getTankIn2(), 176, 0);
				drawFluidTank(guiLeft + 130, guiTop + 15, te.getTankOut(), 176, 0);
			}
		}, labelList);
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.disableStandardItemLighting();
		drawFluidTankTooltip(25, 15, te.getTankIn(), mouseX, mouseY);
		drawFluidTankTooltip(45, 15, te.getTankIn2(), mouseX, mouseY);
		drawFluidTankTooltip(130, 15, te.getTankOut(), mouseX, mouseY);
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(I18n.format("container.inventory"), 6, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.mixer.name");
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
	}
}
