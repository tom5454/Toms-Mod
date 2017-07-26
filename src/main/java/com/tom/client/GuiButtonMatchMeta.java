package com.tom.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.core.tileentity.gui.GuiTomsMod;

@SideOnly(Side.CLIENT)
public class GuiButtonMatchMeta extends GuiButton {
	private final GuiTomsMod gui;
	public boolean use;
	private static final ItemStack stack = new ItemStack(Items.DIAMOND_SWORD, 1, ((ItemSword) Items.DIAMOND_SWORD).getMaxDamage(new ItemStack(Items.DIAMOND_SWORD)) / 2);

	public GuiButtonMatchMeta(int buttonId, int x, int y, GuiTomsMod gui) {
		super(buttonId, x, y, 16, 16, "");
		this.gui = gui;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			mc.getTextureManager().bindTexture(GuiTomsMod.LIST_TEXTURE);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(this.x, this.y, 175, 161, this.width, this.height);
			GlStateManager.pushMatrix();
			float f = 0.85f;
			RenderHelper.enableGUIStandardItemLighting();
			gui.renderItemInGui(stack, this.x + 1, this.y + 1, -200, -200, f);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popMatrix();
			mc.getTextureManager().bindTexture(GuiTomsMod.LIST_TEXTURE);
			if (!use)
				this.drawTexturedModalRect(this.x, this.y, 191, 161, this.width, this.height);
			this.mouseDragged(mc, mouseX, mouseY);
		}
	}
}
