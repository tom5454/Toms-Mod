package com.tom.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.gui.GuiTomsMod;
import com.tom.defense.ForceDeviceControlType;
import com.tom.lib.utils.RenderUtil;

import com.tom.core.tileentity.gui.GuiConfigurator.GuiButtonConfig;

@SideOnly(Side.CLIENT)
public class GuiButtonRedstoneMode extends GuiButtonConfig {
	public ForceDeviceControlType controlType;

	public GuiButtonRedstoneMode(int buttonId, int x, int y, ForceDeviceControlType type) {
		super(buttonId, x, y, 20, 20, "");
		this.controlType = type;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partTicks) {
		if (this.visible) {
			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);
			this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
			this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
			this.mouseDragged(mc, mouseX, mouseY);
			mc.getTextureManager().bindTexture(controlType.iconLocation);
			RenderUtil.drawTexturedRect(x + 2, y + 2, 16, 16);
		}
	}

	@Override
	public void postDraw(Minecraft mc, int mouseX, int mouseY, GuiTomsMod gui) {
		if (this.visible) {
			if (hovered) {
				gui.drawHoveringTextI(I18n.format(controlType.name), mouseX, mouseY);
			}
		}
	}
}