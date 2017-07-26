package com.tom.storage.tileentity.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.storage.handler.ICraftingTerminal;
import com.tom.storage.tileentity.inventory.ContainerCraftingTerminal;

@SideOnly(Side.CLIENT)
public class GuiCraftingTerminal extends GuiTerminalBase {
	private GuiButtonClear buttonClear;

	public GuiCraftingTerminal(InventoryPlayer playerInv, ICraftingTerminal te) {
		super(new ContainerCraftingTerminal(playerInv, te), "guiCraftingTerminal", te, 5, 256, 7, 17);
	}

	@Override
	public void initGui() {
		xSize = 194;
		super.initGui();
		buttonClear = new GuiButtonClear(10, guiLeft + 80, guiTop + convertToTall(110));
		buttonList.add(buttonClear);
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
				this.drawTexturedModalRect(this.x, this.y, 194 + i * 11, 10, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString("Terminal", 6, 6, 4210752);
		drawInventoryText(ySize - 92);
		fontRenderer.drawString(I18n.format("container.crafting"), 100, convertToTall(110), 4210752);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 10) {
			te.sendUpdate(1, 0, this);
		} else
			super.actionPerformed(button);
	}
}
