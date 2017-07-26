package com.tom.storage.tileentity.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.tileentity.IPatternTerminal;
import com.tom.apis.TomsModUtils;
import com.tom.storage.item.ItemCard.CardType;
import com.tom.storage.tileentity.inventory.ContainerPatternTerminal;

@SideOnly(Side.CLIENT)
public class GuiPatternTerminal extends GuiTerminalBase {
	private GuiButtonEncode buttonEncode;
	private GuiButtonCraft buttonCraft;
	private GuiButtonClear buttonClear;
	private GuiButton buttonOptions;
	private GuiButtonUseContainerItems buttonUseContainerItems;

	public GuiPatternTerminal(InventoryPlayer playerInv, IPatternTerminal te) {
		super(new ContainerPatternTerminal(playerInv, te), "guiPatternTerminal", te, 5, 256, 7, 17);
	}

	@Override
	public void initGui() {
		xSize = 222;
		super.initGui();
		buttonEncode = new GuiButtonEncode(10, guiLeft + 153, guiTop + convertToTall(134));
		buttonList.add(buttonEncode);
		buttonCraft = new GuiButtonCraft(11, guiLeft + 177, guiTop + convertToTall(114));
		buttonList.add(buttonCraft);
		buttonClear = new GuiButtonClear(12, guiLeft + 66, guiTop + convertToTall(109));
		buttonList.add(buttonClear);
		buttonOptions = new GuiButtonExt(13, guiLeft + 70, guiTop + convertToTall(150), 22, 15, "...");
		buttonList.add(buttonOptions);
		buttonUseContainerItems = new GuiButtonUseContainerItems(14, guiLeft + 78, guiTop + convertToTall(109));
		buttonList.add(buttonUseContainerItems);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 10) {
			te.sendUpdate(2, 0, this);
		} else if (button.id == 11) {
			te.sendUpdate(3, buttonCraft.e + 1, this);
		} else if (button.id == 12) {
			te.sendUpdate(1, 0, this);
		} else if (button.id == 13) {
			te.sendUpdate(5, 0, this);
		} else if (button.id == 14) {
			te.sendUpdate(4, buttonUseContainerItems.e ? 0 : 1, this);
		} else
			super.actionPerformed(button);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		buttonEncode.enabled = ((IPatternTerminal) te).hasPattern();
		buttonCraft.e = ((IPatternTerminal) te).getCraftingBehaviour();
		buttonCraft.visible = ((IPatternTerminal) te).getUpgradeInv().getStackInSlot(0) != null && CardType.CRAFTING.equal(((IPatternTerminal) te).getUpgradeInv().getStackInSlot(0));
		buttonUseContainerItems.e = ((IPatternTerminal) te).getProperties().useContainerItems;
	}

	public class GuiButtonEncode extends GuiButton {

		public GuiButtonEncode(int buttonId, int x, int y) {
			super(buttonId, x, y, 11, 11, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(gui);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.drawTexturedModalRect(this.x, this.y, 223 + i * 11, 0, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public class GuiButtonClear extends GuiButton {

		public GuiButtonClear(int buttonId, int x, int y) {
			super(buttonId, x, y, 11, 11, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(gui);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.drawTexturedModalRect(this.x, this.y, 223 + i * 11, 11, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public static class GuiButtonCraft extends GuiButton {
		public int e;

		public GuiButtonCraft(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (e < 0)
				visible = false;
			if (this.visible) {
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, 175 + e * 16, 97, this.width, this.height);
				// this.drawTexturedModalRect(this.xPosition + this.width / 2,
				// this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width
				// / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public static class GuiButtonUseContainerItems extends GuiButton {
		public boolean e;

		public GuiButtonUseContainerItems(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 18, "");
		}

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
				this.drawTexturedModalRect(this.x, this.y, 211 + (e ? 18 : 0), 79, this.width, this.height);
				// drawModalRectWithCustomSizedTexture(this.xPosition,
				// this.yPosition, 211 + (e ? 18 : 0), 79, this.width,
				// this.height, 64, 64);
				// this.drawTexturedModalRect(this.xPosition + this.width / 2,
				// this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width
				// / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString("Terminal", 6, 6, 4210752);
		drawInventoryText(ySize - 92);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (buttonOptions.isMouseOver())
			drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsmod.gui.patternOptions")), mouseX, mouseY);
	}
}
