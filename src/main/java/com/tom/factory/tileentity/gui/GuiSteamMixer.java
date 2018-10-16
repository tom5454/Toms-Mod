package com.tom.factory.tileentity.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.gui.GuiFluidTank;
import com.tom.api.gui.GuiTomsMod;
import com.tom.factory.tileentity.TileEntitySteamMixer;
import com.tom.factory.tileentity.inventory.ContainerSteamMixer;
import com.tom.util.TomsModUtils;

public class GuiSteamMixer extends GuiTomsMod {
	private TileEntitySteamMixer te;

	public GuiSteamMixer(InventoryPlayer inv, TileEntitySteamMixer te) {
		super(new ContainerSteamMixer(inv, te), "guiSteamMixer");
		this.te = te;
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
		String s = I18n.format("tile.tm.steamMixer.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float particalTick, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(particalTick, mouseX, mouseY);
		float p2Per = 1 - (te.clientProgress / 100F);
		double p2 = p2Per * 51;
		if (te.clientProgress > 0)
			drawTexturedModalRect(guiLeft + 65, guiTop + 23, 176, 65, p2, 16);
	}

	@Override
	public void initGui() {
		ySize = 176;
		labelList.clear();
		super.initGui();
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 8, guiTop + 20, te.getTankIn()).setUV(176, 0).setUV2(125, 138).setPaddingAndSize(4, 20, 55), labelList);
		TomsModUtils.addRunnableToLabelList(new GuiFluidTank(this, null, guiLeft + 130, guiTop + 20, te.getTankOut()).setUV(176, 0).setUV2(125, 138).setPaddingAndSize(4, 20, 55), labelList);
	}
}
