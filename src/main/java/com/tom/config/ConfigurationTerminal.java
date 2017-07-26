package com.tom.config;

import java.awt.Color;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiSlider.FormatHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.tileentity.IConfigurable.IConfigurationOption;
import com.tom.config.ConfigurationTerminal.SliderHandler.Handler;
import com.tom.storage.multipart.block.StorageNetworkCable.CableColor;

import com.tom.core.tileentity.gui.GuiConfigurator.GuiButtonConfig;
import com.tom.core.tileentity.gui.GuiTomsMod;

public final class ConfigurationTerminal implements IConfigurationOption {
	// private int lastButtonID = -1;
	private int color;
	@SideOnly(Side.CLIENT)
	private GuiSlider r, g, b;
	@SideOnly(Side.CLIENT)
	private SliderHandler h;

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(Minecraft mc, int x, int y) {
		drawColoredRect(x + 170, y + 40, 18, 18, 0xFFA0A0A0);
		drawColoredRect(x + 171, y + 41, 16, 16, color | 0xFF000000);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderForeground(Minecraft mc, int x, int y, int mouseX, int mouseY) {
	}

	@Override
	public int getWidth() {
		return 200;
	}

	@Override
	public int getHeight() {
		return 100;
	}

	@Override
	public void readFromNBTPacket(NBTTagCompound tag) {
		color = tag.getInteger("color");
		Color c = new Color(color);
		h.setEnabled(false);
		r.setSliderPosition(c.getRed() / 255f);
		g.setSliderPosition(c.getGreen() / 255f);
		b.setSliderPosition(c.getBlue() / 255f);
		h.setEnabled(true);
	}

	@Override
	public void writeModificationNBTPacket(NBTTagCompound tag) {
		tag.setInteger("color", color);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void actionPreformed(Minecraft mc, GuiButton button) {
		if (button instanceof GuiButtonColor) {
			color = ((GuiButtonColor) button).getColor();
			Color c = new Color(color);
			h.setEnabled(false);
			r.setSliderPosition(c.getRed() / 255f);
			g.setSliderPosition(c.getGreen() / 255f);
			b.setSliderPosition(c.getBlue() / 255f);
			h.setEnabled(true);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void init(Minecraft mc, int x, int y, int lastButtonID, List<GuiButton> buttonList, List<GuiLabel> labelList) {
		// this.lastButtonID = lastButtonID;
		for (int i = 0;i < CableColor.VALUES.length;i++) {
			GuiButtonColor c = new GuiButtonColor(lastButtonID + 1 + i, x + 10 + (i % 9) * 18, y + 60 + (i / 9) * 18, CableColor.VALUES[i]);
			buttonList.add(c);
		}
		h = new SliderHandler(lastButtonID + CableColor.VALUES.length + 1, lastButtonID + CableColor.VALUES.length + 2, lastButtonID + CableColor.VALUES.length + 3, new Handler() {

			@Override
			public Integer get() {
				return color;
			}

			@Override
			public void accept(Integer t) {
				color = t;
			}
		});
		r = new GuiSlider(h, lastButtonID + CableColor.VALUES.length + 1, x + 10, y - 10, "tomsmod.color.red", 0, 255, 0, h);
		g = new GuiSlider(h, lastButtonID + CableColor.VALUES.length + 2, x + 10, y + 10, "tomsmod.color.green", 0, 255, 0, h);
		b = new GuiSlider(h, lastButtonID + CableColor.VALUES.length + 3, x + 10, y + 30, "tomsmod.color.blue", 0, 255, 0, h);
		buttonList.add(r);
		buttonList.add(g);
		buttonList.add(b);
		Color c = new Color(color);
		h.setEnabled(false);
		r.setSliderPosition(c.getRed() / 255f);
		g.setSliderPosition(c.getGreen() / 255f);
		b.setSliderPosition(c.getBlue() / 255f);
		h.setEnabled(true);
	}

	@Override
	public IInventory getInventory() {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void update(Minecraft mc, int x, int y) {
	}

	@SideOnly(Side.CLIENT)
	public static class GuiButtonColor extends GuiButtonConfig {
		private int color;
		private String tooltip;

		public GuiButtonColor(int buttonId, int x, int y, CableColor color) {
			super(buttonId, x, y, 16, 16, "");
			this.color = color.getTint();
			this.tooltip = I18n.format("tomsmod.color." + color.getName());
		}

		public GuiButtonColor(int buttonId, int x, int y, int color) {
			super(buttonId, x, y, 16, 16, "");
			this.color = color;
			Color c = new Color(color);
			this.tooltip = "[R=" + c.getRed() + ", G=" + c.getGreen() + ", B=" + c.getBlue() + "]";
		}

		public int getColor() {
			return color;
		}

		public void setColor(CableColor color) {
			this.color = color.getTint();
			this.tooltip = color.getDyeMeta();
		}

		public void setColor(int color) {
			this.color = color;
			Color c = new Color(color);
			this.tooltip = "[R=" + c.getRed() + ", G=" + c.getGreen() + ", B=" + c.getBlue() + "]";
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				// int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				int j = 0;
				if (enabled) {
					if (this.hovered) {
						j = 16777120;
					} else {
						j = 0xA0A0A0;
					}
				}
				drawColoredRect(x, y, width, height, j | 0xFF000000);
				drawColoredRect(x + 1, y + 1, width - 2, height - 2, color | 0xFF000000);
				/*mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				int i = this.getHoverState(this.hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
				this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
				this.mouseDragged(mc, mouseX, mouseY);
				mc.getTextureManager().bindTexture(controlType.iconLocation);
				Render.drawTexturedRect(xPosition+2, yPosition+2, 16, 16);16777120*/
			}
		}

		@Override
		public void postDraw(Minecraft mc, int mouseX, int mouseY, GuiTomsMod gui) {
			if (this.visible) {
				if (hovered) {
					gui.drawHoveringTextI(tooltip, mouseX, mouseY);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void drawColoredRect(int x, int y, int w, int h, int color) {
		Gui.drawRect(x, y, x + w, y + h, color);
	}

	@SideOnly(Side.CLIENT)
	public static class SliderHandler implements GuiResponder, FormatHelper {
		private final int r, g, b;
		private boolean enabled = true;
		private Handler update;

		public SliderHandler(int r, int g, int b, Handler update) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.update = update;
		}

		public void setEnabled(boolean c) {
			enabled = c;
		}

		@Override
		public void setEntryValue(int id, boolean value) {
		}

		@Override
		public void setEntryValue(int id, float value) {
			if (!enabled)
				return;
			int color = MathHelper.floor(value);
			if (id == r)
				update.accept(color(update.get(), color, -1, -1));
			else if (id == g)
				update.accept(color(update.get(), -1, color, -1));
			else if (id == b)
				update.accept(color(update.get(), -1, -1, color));
		}

		private static int color(int color, int r, int g, int b) {
			Color c = new Color(color);
			int ro = c.getRed(), go = c.getGreen(), bo = c.getBlue();
			if (r != -1) {
				ro = r;
			}
			if (g != -1) {
				go = g;
			}
			if (b != -1) {
				bo = b;
			}
			return new Color(ro, go, bo).getRGB() & 0xFFFFFF;
		}

		@Override
		public void setEntryValue(int id, String value) {
		}

		public static interface Handler extends Consumer<Integer>, Supplier<Integer> {
		}

		@Override
		public String getText(int id, String name, float value) {
			return I18n.format(name) + ": " + MathHelper.floor(value);
		}
	}
}