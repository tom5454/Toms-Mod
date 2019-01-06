package com.tom.storage.tileentity.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.storage.multipart.PartImportBus;
import com.tom.storage.tileentity.inventory.ContainerImportBus;
import com.tom.util.TomsModUtils;

public class GuiImportBus extends GuiMultipartBase {
	public GuiImportBus(PartImportBus bus, InventoryPlayer playerInv) {
		super(new ContainerImportBus(bus, playerInv), "GuiImportBus", bus);
	}

	private GuiButtonWhiteList buttonWhiteList;

	@Override
	public void initGui() {
		ySize = 176;
		super.initGui();
		buttonWhiteList = new GuiButtonWhiteList(0, guiLeft - 19, guiTop);
		buttonList.add(buttonWhiteList);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		buttonWhiteList.isWhiteList = ((PartImportBus) te).isWhiteList();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (buttonWhiteList.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod." + (buttonWhiteList.isWhiteList ? "whiteList" : "blackList"))), mouseX, mouseY);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			sendButtonUpdateToTile(0, (((PartImportBus) te).isWhiteList() ? 0 : 1));
		}
	}

	public static class GuiButtonWhiteList extends GuiButton {

		public GuiButtonWhiteList(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}

		public boolean isWhiteList = true;

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float pt) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, isWhiteList ? 175 : 191, 113, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}
}
