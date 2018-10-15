package com.tom.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.lib.utils.RenderUtil;

@SideOnly(Side.CLIENT)
public class GuiButtonSelection extends GuiButton {

	public GuiButtonSelection(int buttonId, int x, int y) {
		super(buttonId, x, y, 16, 16, "");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partTicks) {
		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		int i = this.getHoverState(this.hovered);
		if (i == 2) {
			RenderUtil.setColourWithAlphaPercent(0xFFFFFF, 20);
			RenderUtil.drawRect(x, y, width, height);
		}
	}
}