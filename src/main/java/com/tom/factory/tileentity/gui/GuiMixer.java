package com.tom.factory.tileentity.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.gui.GuiFluidTank;
import com.tom.api.gui.GuiTomsMod;
import com.tom.factory.tileentity.TileEntityMixer;
import com.tom.factory.tileentity.inventory.ContainerMixer;
import com.tom.util.TomsModUtils;

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
		if (te.getField(0) > 0)
			drawTexturedModalRect(guiLeft + 65, guiTop + 19, 176, 65, p2, 16);
	}

	@Override
	public void initGui() {
		ySize = 176;
		labelList.clear();
		super.initGui();
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 25, guiTop + 15, te.getTankIn()).setUV(176, 0).setUV2(125, 138).setPaddingAndSize(4, 20, 55), labelList);
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 45, guiTop + 15, te.getTankIn2()).setUV(176, 0).setUV2(125, 138).setPaddingAndSize(4, 20, 55), labelList);
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 130, guiTop + 15, te.getTankOut()).setUV(176, 0).setUV2(125, 138).setPaddingAndSize(4, 20, 55), labelList);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.disableStandardItemLighting();
		renderFluidTooltips(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 6, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.mixer.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}
}
