package com.tom.storage.tileentity.gui;

import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;

import mapwriterTm.util.Render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import com.tom.apis.TMLogger;
import com.tom.apis.TomsModUtils;
import com.tom.storage.multipart.StorageNetworkGrid.IStorageTerminalGui;

public class GuiCraftingAmountSelection extends GuiScreen {
	private static final ResourceLocation gui = new ResourceLocation("tomsmod:textures/gui/crafting1.png");
	private IStorageTerminalGui parent;
	private ItemStack stack, backButton;
	private GuiButton buttonNext;
	protected int guiLeft;
	protected int guiTop;
	private GuiButtonNum up1, up10, up100, up1000, down1, down10, down100, down1000;
	private int xSize = 176;
	private int ySize = 107;
	private GuiTextField numberField;
	private GuiButtonHidden buttonBack;
	public GuiCraftingAmountSelection(IStorageTerminalGui parent, ItemStack stack, ItemStack backButton) {
		this.parent = parent;
		this.stack = stack.copy();
		this.stack.stackSize = 1;
		this.backButton = backButton;
	}
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 0){
			close(true);
		}else if(button.id == 2){
			close(false);
		}else if(button instanceof GuiButtonNum){
			GuiButtonNum n = (GuiButtonNum) button;
			int amount = 1;
			try{
				amount = Integer.parseInt(numberField.getText());
			}catch(NumberFormatException e){
				TMLogger.catching(e, "Exception occurred while reading a number from a number field! THIS SHOULDN'T BE POSSIBLE!");
			}
			amount = Math.max(Math.min((amount == 1 && MathHelper.abs_int(n.getNum()) != 1 ? n.getNum() : amount + n.getNum()), 1000000), 1);
			numberField.setText(""+amount);
		}
	}
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		mc.getTextureManager().bindTexture(gui);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		super.drawScreen(mouseX, mouseY, partialTicks);
		GL11.glPushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		renderItemInGui(stack, guiLeft + 34, guiTop + 53, mouseX, mouseY, 0xFFFFFF);
		RenderHelper.enableGUIStandardItemLighting();
		renderItemInGui(backButton, guiLeft + 157, guiTop + 3, -20, -20, 0xFFFFFF);
		GL11.glPopMatrix();
		if(buttonBack.isMouseOver())drawHoveringText(TomsModUtils.getStringList(I18n.format(backButton.getUnlocalizedName()+".name")), mouseX, mouseY);
		mc.fontRendererObj.drawString(I18n.format("tomsmod.gui.selectAmount"), guiLeft+5, guiTop+5, 4210752);
	}
	@Override
	public void initGui() {
		labelList.clear();
		super.initGui();
		this.guiLeft = (this.width - xSize) / 2;
		this.guiTop = (this.height - ySize) / 2;
		buttonNext = new GuiButton(0, guiLeft + 130, guiTop + 51, 30, 20, I18n.format("tomsmod.gui.next"));
		buttonList.add(buttonNext);
		up1 = new GuiButtonNum(1, guiLeft + 20, guiTop + 26, 1, 1, 20);
		up10 = new GuiButtonNum(1, guiLeft + 45, guiTop + 26, 10, 16, 25);
		up100 = new GuiButtonNum(1, guiLeft + 75, guiTop + 26, 100, 32, 30);
		up1000 = new GuiButtonNum(1, guiLeft + 110, guiTop + 26, 1000, 64, 35);
		down1 = new GuiButtonNum(1, guiLeft + 20, guiTop + 76, -1, -1, 20);
		down10 = new GuiButtonNum(1, guiLeft + 45, guiTop + 76, -10, -16, 25);
		down100 = new GuiButtonNum(1, guiLeft + 75, guiTop + 76, -100, -32, 30);
		down1000 = new GuiButtonNum(1, guiLeft + 110, guiTop + 76, -1000, -64, 35);
		buttonList.add(down1);
		buttonList.add(down10);
		buttonList.add(down100);
		buttonList.add(down1000);
		buttonList.add(up1);
		buttonList.add(up10);
		buttonList.add(up100);
		buttonList.add(up1000);
		numberField = new GuiTextField(1, mc.fontRendererObj, guiLeft + 61, guiTop + 57, 59, 10);
		numberField.setTextColor(0xFFFFFF);
		numberField.setEnableBackgroundDrawing(false);
		numberField.setText("1");
		numberField.setCanLoseFocus(false);
		numberField.setFocused(true);
		numberField.setMaxStringLength(6);
		TomsModUtils.addTextFieldToLabelList(numberField, labelList);
		buttonBack = new GuiButtonHidden(2, guiLeft + 155, guiTop + 2, 18, 18);
		buttonList.add(buttonBack);
	}
	private void close(boolean craft){
		mc.displayGuiScreen(parent.getScreen());
		int amount = 1;
		try{
			amount = Integer.parseInt(numberField.getText());
		}catch(NumberFormatException e){
			TMLogger.catching(e, "Exception occurred while reading a number from a number field! THIS SHOULDN'T BE POSSIBLE!");
		}
		if(craft)parent.openCraftingReport(stack, amount);
	}
	private void renderItemInGui(ItemStack stack, int x, int y, int mouseX, int mouseY, int color, String... extraInfo){
		if(stack != null){
			boolean hasBg = mouseX >= x-1 && mouseY >= y-1 && mouseX < x + 17 && mouseY < y + 17;
			if(hasBg){
				Render.setColourWithAlphaPercent(color,50);
				Render.drawRect(x, y, 16, 16);
			}
			GlStateManager.translate(0.0F, 0.0F, 32.0F);
			this.zLevel = 100.0F;
			this.itemRender.zLevel = 100.0F;
			FontRenderer font = null;
			if (stack != null) font = stack.getItem().getFontRenderer(stack);
			if (font == null) font = fontRendererObj;
			GlStateManager.enableDepth();
			this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
			this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, null);
			this.zLevel = 0.0F;
			this.itemRender.zLevel = 0.0F;
			if(hasBg){
				List<String> list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
				//list.add(I18n.format("tomsmod.gui.amount", stack.stackSize));
				if(extraInfo != null && extraInfo.length > 0){
					list.addAll(TomsModUtils.getStringList(extraInfo));
				}
				for (int i = 0; i < list.size(); ++i)
				{
					if (i == 0)
					{
						list.set(i, stack.getRarity().rarityColor + list.get(i));
					}
					else
					{
						list.set(i, TextFormatting.GRAY + list.get(i));
					}
				}
				this.drawHoveringText(list, mouseX, mouseY);
			}
		}
	}
	public static class GuiButtonNum extends GuiButton{
		public final int normalState, shiftState;
		public GuiButtonNum(int buttonId, int x, int y, int normal, int shift, int lenth) {
			super(buttonId, x, y, lenth, 20, "");
			normalState = normal;
			shiftState = shift;
		}
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible)
			{
				FontRenderer fontrenderer = mc.fontRendererObj;
				mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
				this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
				int j = 14737632;

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
					}
				int num = getNum();
				String s = num > 0 ? "+"+num : ""+num;
				this.drawCenteredString(fontrenderer, s, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
			}
		}
		public int getNum(){
			return isShiftKeyDown() ? shiftState : normalState;
		}
	}
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1)
		{
			close(false);
		}else if(Character.isDigit(typedChar) || keyCode == 14){
			numberField.textboxKeyTyped(typedChar, keyCode);
		}
	}
	@Override
	public void updateScreen() {
		buttonNext.enabled = !numberField.getText().isEmpty();
	}
	public static class GuiButtonHidden extends GuiButton{

		public GuiButtonHidden(int buttonId, int x, int y, int widthIn,
				int heightIn) {
			super(buttonId, x, y, widthIn, heightIn, "");
		}
		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				/*FontRenderer fontrenderer = mc.fontRendererObj;
	            mc.getTextureManager().bindTexture(buttonTextures);
	            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);*/
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				/* int i = this.getHoverState(this.hovered);
	            GlStateManager.enableBlend();
	            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	            GlStateManager.blendFunc(770, 771);
	            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
	            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
	            this.mouseDragged(mc, mouseX, mouseY);
	            int j = 14737632;

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
	            }

	            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);*/
			}
		}
	}
}
