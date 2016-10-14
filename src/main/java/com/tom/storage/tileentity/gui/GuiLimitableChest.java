package com.tom.storage.tileentity.gui;

import java.io.IOException;

import mapwriterTm.util.Render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import com.tom.storage.tileentity.TileEntityLimitableChest;
import com.tom.storage.tileentity.inventory.ContainerLimitableChest;

import com.tom.core.tileentity.gui.GuiTomsMod;

public class GuiLimitableChest extends GuiTomsMod {
	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	//private static final ResourceLocation LIST_TEXTURE = new ResourceLocation("tomsmod:textures/gui/resSelect.png");
	private TileEntityLimitableChest te;
	private GuiButtonLock buttonLock;
	private boolean selecting = false;
	private int selected = 0;
	public GuiLimitableChest(InventoryPlayer inv, TileEntityLimitableChest chest) {
		super(new ContainerLimitableChest(inv, chest), "");
		te = chest;
	}
	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRendererObj.drawString(this.te.getDisplayName().getUnformattedText(), 8, 6, 4210752);
		fontRendererObj.drawString(I18n.format("container.inventory"), 6, ySize - 92, 4210752);
	}
	private void drawRedBackgrounds(int a){
		for(int i = 0;i<a;i++){
			int x = guiLeft + (18 * 8) + 8 - (18 * ((i < 9 ? i + 1 : i) % 9));
			int y = guiTop + (18 * 2) + 18 - (18 * (i / 9));
			Render.setColourWithAlphaPercent(0xFF0000,50);
			Render.drawRect(x, y, 16, 16);
		}
	}
	@Override
	public void initGui() {
		super.initGui();
		buttonLock = new GuiButtonLock(0, guiLeft+8+18*8, guiTop+18+18*2);
		this.buttonList.add(buttonLock);
	}
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 0){
			if(this.selecting){
				this.sendButtonUpdate(0, te, 0);
				this.selected = 0;
			}
			this.selecting = !this.selecting;
		}
	}
	/**
	 * Args : renderPartialTicks, mouseX, mouseY
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, 3 * 18 + 17);
		this.drawTexturedModalRect(i, j + 3*18 + 17, 0, 126, this.xSize, 96);
		if(this.selecting){
			if(this.getSlotUnderMouse() != null && this.getSlotUnderMouse().inventory instanceof TileEntityLimitableChest){
				int slotId = this.getSlotUnderMouse().slotNumber;
				this.selected = 17 < slotId ? 26-slotId : 27-slotId;
			}
			this.drawRedBackgrounds(this.selected);
		}else{
			this.drawRedBackgrounds(te.getField(0));
		}
	}
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
			throws IOException {
		if(this.selecting){
			if(!this.buttonLock.mousePressed(mc, mouseX, mouseY)){
				this.sendButtonUpdate(0, te, selected);
				this.selecting = false;
				this.selected = 0;
				return;
			}
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	public class GuiButtonLock extends GuiButton{

		public GuiButtonLock(int buttonId, int x, int y) {
			super(buttonId, x, y,16,16, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				//FontRenderer fontrenderer = mc.fontRendererObj;
				mc.getTextureManager().bindTexture(LIST_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition,/**u*/ 175 + i * 16, 0,/**u*/ this.width, this.height);
				//this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, /**u*/200 - this.width / 2, 46 + i * 20,/**u*/ this.width / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
				/*int j = 14737632;

	            if (packedFGColour != 0)
	            {
	                j = packedFGColour;
	            }
	            else
	            if (!this.enabled)
	            {
	                j = 10526880;
	            }
	            else if (this.hovered)
	            {
	                j = 16777120;
	            }*/

				//this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
			}
		}
		/**
		 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
		 * this button.
		 */
		@Override
		protected int getHoverState(boolean mouseOver)
		{
			int i = 2;

			if (selecting)
			{
				if (mouseOver)i = 1;
				else i = 0;
			}
			else if (mouseOver)
			{
				i = 3;
			}

			return i;
		}
	}

}
