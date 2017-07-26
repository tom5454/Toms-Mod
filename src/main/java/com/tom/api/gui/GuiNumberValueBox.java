package com.tom.api.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiNumberValueBox {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tomsmod:textures/gui/resSelect.png");
	private GuiButtonNextNum nextNum, prevNum;
	private final int minNum, maxNum;
	private int xPosition, yPosition;
	public int num, id, color;

	public GuiNumberValueBox(int id, int x, int y, int maxValue, int minValue) {
		this(id, x, y, maxValue, minValue, 0);
	}

	public GuiNumberValueBox(int id, int x, int y, int maxValue, int minValue, int color) {
		this.minNum = minValue;
		this.maxNum = maxValue;
		this.nextNum = new GuiButtonNextNum(id, false, this);
		this.prevNum = new GuiButtonNextNum(id, true, this);
		this.setPosition(x, y);
		this.id = id;
		this.color = color;
	}

	public class GuiButtonNextNum extends GuiButton {
		private boolean isDown;
		public GuiNumberValueBox parent;

		public GuiButtonNextNum(int id, boolean isDown, GuiNumberValueBox parent) {
			super(id, 0, 0, 9, 9, "");
			this.isDown = isDown;
			this.parent = parent;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.x, this.y, 175 + i * 9, isDown ? 16 : 25, this.width, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
			}
		}
	}

	public void onClicked(int mouseX, int mouseY) {
		if (nextNum.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
			num += (GuiScreen.isShiftKeyDown() ? (GuiScreen.isCtrlKeyDown() ? 20 : 5) : (GuiScreen.isCtrlKeyDown() ? 10 : 1));
		}
		if (prevNum.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
			num -= (GuiScreen.isShiftKeyDown() ? (GuiScreen.isCtrlKeyDown() ? 20 : 5) : (GuiScreen.isCtrlKeyDown() ? 10 : 1));
		}
		num = Math.max(minNum, Math.min(maxNum, num));
	}

	public void update(boolean enable) {
		num = Math.max(minNum, Math.min(maxNum, num));
		nextNum.enabled = num < maxNum && enable;
		prevNum.enabled = num > minNum && enable;
	}

	public void draw(int mouseX, int mouseY, boolean drawButtons) {
		Minecraft mc = Minecraft.getMinecraft();
		if (drawButtons) {
			nextNum.drawButton(mc, mouseX, mouseY);
			prevNum.drawButton(mc, mouseX, mouseY);
		}
		this.drawText(mc.fontRenderer, color);
	}

	public int getMinNum() {
		return minNum;
	}

	public int getMaxNum() {
		return maxNum;
	}

	public int getXPosition() {
		return xPosition;
	}

	public int getYPosition() {
		return yPosition;
	}

	public void setPosition(int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
		prevNum.x = x;
		prevNum.y = y;
		nextNum.x = x + getWidth();
		nextNum.y = y;
	}

	private int getWidth() {
		int numLength = ((Integer) maxNum).toString().length();
		return Math.max(27, numLength * 10);
	}

	public List<GuiButton> addToList(List<GuiButton> buttonList) {
		buttonList.add(prevNum);
		buttonList.add(nextNum);
		return buttonList;
	}

	public void drawText(FontRenderer fontRendererObj, int color) {
		String s = "" + num;
		fontRendererObj.drawString(s, xPosition + 10, yPosition, color);
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return isPointInRegion(xPosition, yPosition, getWidth() + 9, 9, mouseX, mouseY);
	}

	private boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
		return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
	}
}
