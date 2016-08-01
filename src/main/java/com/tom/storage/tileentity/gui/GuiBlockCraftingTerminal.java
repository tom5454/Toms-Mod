package com.tom.storage.tileentity.gui;

import java.io.IOException;

import com.tom.storage.tileentity.TileEntityCraftingTerminal;
import com.tom.storage.tileentity.inventory.ContainerBlockCraftingTerminal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBlockCraftingTerminal extends GuiTerminalBase {
	private GuiButtonClear buttonClear;
	public GuiBlockCraftingTerminal(InventoryPlayer playerInv, TileEntityCraftingTerminal te) {
		super(new ContainerBlockCraftingTerminal(playerInv, te), "guiCraftingTerminal", te, 5);
	}
	@Override
	public void initGui() {
		ySize = 256;
		xSize = 194;
		super.initGui();
		buttonClear = new GuiButtonClear(10, guiLeft + 80, guiTop + 110);
		buttonList.add(buttonClear);
	}
	public class GuiButtonClear extends GuiButton{

		public GuiButtonClear(int buttonId, int x, int y) {
			super(buttonId, x, y, 11, 11, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				mc.getTextureManager().bindTexture(gui);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 194 + i * 11, 10, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString("Terminal", 6, 6, 4210752);
		drawInventoryText(ySize - 92);
		fontRendererObj.drawString(I18n.format("container.crafting"), 100, 110, 4210752);
	}
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 10){
			sendButtonUpdate(1, te);
		}else
			super.actionPerformed(button);
	}
}
