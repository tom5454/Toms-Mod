package com.tom.factory.tileentity.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import com.tom.api.gui.GuiTomsLib;
import com.tom.factory.tileentity.TileEntityBlastFurnace;
import com.tom.factory.tileentity.inventory.ContainerBlastFurnace;

public class GuiBlastFurnace extends GuiTomsLib {

	public GuiBlastFurnace(InventoryPlayer playerInv, TileEntityBlastFurnace te) {
		super(new ContainerBlastFurnace(playerInv, te), "");
		this.te = te;
	}

	private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/furnace.png");
	private TileEntityBlastFurnace te;

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("container.inventory"), 6, ySize - 96 + 2, 4210752);
		String s = I18n.format("tile.tm.blastFurnace.name");
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}

	/**
	 * Draws the background layer of this container (behind the items).
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(FURNACE_GUI_TEXTURES);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		float p1Per = 1 - (te.getField(0) / 100F);
		float p1 = p1Per * 24;
		if (te.getField(0) > 0)
			drawTexturedModalRect(guiLeft + 79, guiTop + 35, 176, 14, p1, 16);
		float p2Per = te.getField(2) / 100F;
		float p2 = p2Per * 13;
		if (p2 > 0.1f)
			drawTexturedModalRect(i + 56, j + 36 + 12 - p2, 176, 12 - p2, 14, p2 + 1);
	}
}
