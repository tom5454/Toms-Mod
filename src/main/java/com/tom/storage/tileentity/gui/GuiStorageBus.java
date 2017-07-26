package com.tom.storage.tileentity.gui;

import java.io.IOException;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import com.tom.api.inventory.IStorageInventory.BasicFilter.Mode;
import com.tom.apis.TomsModUtils;
import com.tom.client.GuiButtonMatchMeta;
import com.tom.storage.multipart.PartStorageBus;
import com.tom.storage.tileentity.gui.GuiImportBus.GuiButtonWhiteList;
import com.tom.storage.tileentity.inventory.ContainerStorageBus;

public class GuiStorageBus extends GuiMultipartBase {
	private GuiButtonWhiteList buttonWhiteList;
	private GuiButtonMatchMeta buttonMatchMeta;
	private GuiButtonMatchNBT buttonMatchNBT;
	private GuiButtonMatchMod buttonMatchMod;
	private GuiButtonCanViewAll buttonCanViewAll;
	private GuiButtonIOMode buttonIOMode;

	public GuiStorageBus(PartStorageBus bus, InventoryPlayer inventory) {
		super(new ContainerStorageBus(bus, inventory), "guiStorageBus", bus);
	}

	@Override
	public void initGui() {
		ySize = 176;
		super.initGui();
		buttonWhiteList = new GuiButtonWhiteList(0, guiLeft - 18, guiTop);
		buttonMatchMeta = new GuiButtonMatchMeta(1, guiLeft - 18, guiTop + 18, this);
		buttonMatchNBT = new GuiButtonMatchNBT(2, guiLeft - 18, guiTop + 18 + 18);
		buttonMatchMod = new GuiButtonMatchMod(3, guiLeft - 18, guiTop + 18 + 18 + 18);
		buttonCanViewAll = new GuiButtonCanViewAll(4, guiLeft - 18, guiTop + 18 + 18 + 18 + 18);
		buttonIOMode = new GuiButtonIOMode(5, guiLeft - 18, guiTop + 18 + 18 + 18 + 18 + 18);
		buttonList.add(buttonWhiteList);
		buttonList.add(buttonMatchMeta);
		buttonList.add(buttonMatchNBT);
		buttonList.add(buttonMatchMod);
		buttonList.add(buttonIOMode);
		buttonList.add(buttonCanViewAll);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		buttonWhiteList.isWhiteList = ((PartStorageBus) te).isWhiteList();
		buttonMatchMeta.use = ((PartStorageBus) te).isCheckMeta();
		buttonMatchNBT.useNBT = ((PartStorageBus) te).isCheckNBT();
		buttonMatchMod.useMod = ((PartStorageBus) te).isCheckMod();
		buttonCanViewAll.canViewAll = ((PartStorageBus) te).isCanViewAll();
		buttonIOMode.mode = ((PartStorageBus) te).getMode();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (buttonWhiteList.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod." + (buttonWhiteList.isWhiteList ? "whiteList" : "blackList"))), mouseX, mouseY);
		}
		if (buttonMatchMeta.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod." + (buttonMatchMeta.use ? "matchMeta" : "ignoreMeta"))), mouseX, mouseY);
		}
		if (buttonMatchNBT.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod." + (buttonMatchNBT.useNBT ? "matchNBT" : "ignoreNBT"))), mouseX, mouseY);
		}
		if (buttonMatchMod.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod." + (buttonMatchMod.useMod ? "matchMod" : "ignoreMod"))), mouseX, mouseY);
		}
		if (buttonCanViewAll.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod." + (buttonCanViewAll.canViewAll ? "canViewAll" : "cantViewAll"))), mouseX, mouseY);
		}
		if (buttonIOMode.isMouseOver()) {
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsMod.storagebus." + (buttonIOMode.mode.name().toLowerCase(Locale.ROOT)))), mouseX, mouseY);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			sendButtonUpdateP(0, te, (((PartStorageBus) te).isWhiteList() ? 0 : 1));
		} else if (button.id == 1) {
			sendButtonUpdateP(1, te, (((PartStorageBus) te).isCheckMeta() ? 0 : 1));
		} else if (button.id == 2) {
			sendButtonUpdateP(2, te, (((PartStorageBus) te).isCheckNBT() ? 0 : 1));
		} else if (button.id == 3) {
			sendButtonUpdateP(3, te, (((PartStorageBus) te).isCheckMod() ? 0 : 1));
		} else if (button.id == 4) {
			sendButtonUpdateP(4, te, (((PartStorageBus) te).isCanViewAll() ? 0 : 1));
		} else if (button.id == 5) {
			sendButtonUpdateP(5, te, (((PartStorageBus) te).getMode().ordinal() + 1));
		}
	}

	public static class GuiButtonMatchNBT extends GuiButton {

		public GuiButtonMatchNBT(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}

		public boolean useNBT = true;

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, useNBT ? 175 : 191, 193, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public static class GuiButtonMatchMod extends GuiButton {

		public GuiButtonMatchMod(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}

		public boolean useMod = true;

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, 175, 161, this.width, this.height);
				mc.fontRenderer.drawString("@", this.x + this.width / 2 - 3, this.y + (this.height - 6) / 2, 0x404040);
				if (!useMod) {
					mc.getTextureManager().bindTexture(LIST_TEXTURE);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					this.drawTexturedModalRect(this.x, this.y, 191, 161, this.width, this.height);
				}
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public static class GuiButtonCanViewAll extends GuiButton {

		public GuiButtonCanViewAll(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}

		public boolean canViewAll = true;

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, canViewAll ? 175 : 191, 209, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public static class GuiButtonIOMode extends GuiButton {

		public GuiButtonIOMode(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}

		public Mode mode = Mode.IO;

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, 175 + mode.ordinal() * 16, 177, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}
}
