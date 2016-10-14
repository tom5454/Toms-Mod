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

import com.tom.apis.TomsModUtils;
import com.tom.storage.StorageInit;
import com.tom.storage.tileentity.TileEntityPatternTerminal;
import com.tom.storage.tileentity.inventory.ContainerBlockPatternTerminal;

@SideOnly(Side.CLIENT)
public class GuiBlockPatternTerminal extends GuiTerminalBase {
	private GuiButtonEncode buttonEncode;
	private GuiButtonCraft buttonCraft;
	private GuiButtonClear buttonClear;
	private GuiButton buttonOptions;
	private GuiButtonUseContainerItems buttonUseContainerItems;
	public GuiBlockPatternTerminal(InventoryPlayer playerInv, TileEntityPatternTerminal te) {
		super(new ContainerBlockPatternTerminal(playerInv, te), "guiPatternTerminal", te, 5);
	}
	@Override
	public void initGui() {
		ySize = 256;
		xSize = 222;
		super.initGui();
		buttonEncode = new GuiButtonEncode(10, guiLeft + 153, guiTop + 134);
		buttonList.add(buttonEncode);
		buttonCraft = new GuiButtonCraft(11, guiLeft + 177, guiTop + 114);
		buttonList.add(buttonCraft);
		buttonClear = new GuiButtonClear(12, guiLeft + 66, guiTop + 109);
		buttonList.add(buttonClear);
		buttonOptions = new GuiButtonExt(13, guiLeft + 70, guiTop + 150, 22, 15, "...");
		buttonList.add(buttonOptions);
		buttonUseContainerItems = new GuiButtonUseContainerItems(14, guiLeft + 78, guiTop + 109);
		buttonList.add(buttonUseContainerItems);
	}
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 10){
			sendButtonUpdate(2, te);
		}else if(button.id == 11){
			sendButtonUpdate(3, te, buttonCraft.e + 1);
		}else if(button.id == 12){
			sendButtonUpdate(1, te);
		}else if(button.id == 13){
			sendButtonUpdate(5, te);
		}else if(button.id == 14){
			sendButtonUpdate(4, te, buttonUseContainerItems.e ? 0 : 1);
		}else
			super.actionPerformed(button);
	}
	@Override
	public void updateScreen() {
		super.updateScreen();
		buttonEncode.enabled = ((TileEntityPatternTerminal)te).hasPattern();
		buttonCraft.e = ((TileEntityPatternTerminal)te).getCraftingBehaviour();
		buttonCraft.visible = ((TileEntityPatternTerminal)te).upgradeInv.getStackInSlot(0) != null && ((TileEntityPatternTerminal)te).upgradeInv.getStackInSlot(0).getItem() == StorageInit.craftingCard;
		buttonUseContainerItems.e = ((TileEntityPatternTerminal)te).properties.useContainerItems;
	}
	public class GuiButtonEncode extends GuiButton{

		public GuiButtonEncode(int buttonId, int x, int y) {
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
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 223 + i * 11, 0, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
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
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 223 + i * 11, 11, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}
	public static class GuiButtonCraft extends GuiButton{
		public int e;
		public GuiButtonCraft(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 16, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if(e < 0)visible = false;
			if (this.visible)
			{
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				//int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 175 + e * 16, 97, this.width, this.height);
				//this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}
	public static class GuiButtonUseContainerItems extends GuiButton{
		public boolean e;
		public GuiButtonUseContainerItems(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 18, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				//int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 211 + (e ? 18 : 0), 79, this.width, this.height);
				//drawModalRectWithCustomSizedTexture(this.xPosition, this.yPosition, 211 + (e ? 18 : 0), 79, this.width, this.height, 64, 64);
				//this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString("Terminal", 6, 6, 4210752);
		drawInventoryText(ySize - 92);
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(buttonOptions.isMouseOver())drawHoveringText(TomsModUtils.getStringList(I18n.format("tomsmod.gui.patternOptions")), mouseX, mouseY);
	}
}
