package com.tom.api.gui;

import static com.tom.core.tileentity.gui.GuiTomsMod.LIST_TEXTURE;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.apis.TomsModUtils.GuiRenderRunnable;

import com.tom.core.tileentity.gui.GuiTomsMod;

@SideOnly(value = Side.CLIENT)
public class GuiFluidTank extends Gui implements GuiRenderRunnable {
	private static final int TEX_WIDTH = 16;
	private static final int TEX_HEIGHT = 16;
	private static final int MIN_FLUID_HEIGHT = 1;
	private static final int TANK_WIDTH = 20;
	private static final int TANK_HEIGHT = 55;
	private Minecraft mc = Minecraft.getMinecraft();
	public int posX, posY, u, v;
	public boolean hasUV = false;
	private GuiTomsMod gui;
	public String name;
	public FluidTank tank;

	public GuiFluidTank(GuiTomsMod gui, String name, int xPos, int yPos, FluidTank tank) {
		this.gui = gui;
		this.name = name;
		this.tank = tank;
		this.posX = xPos;
		this.posY = yPos;
		gui.tanks.add(this);
	}

	public GuiFluidTank setUV(int u, int v) {
		this.u = u;
		this.v = v;
		hasUV = true;
		return this;
	}

	private final void drawFluid(int xPosition, int yPosition, FluidStack fluidStack, int capacityMb) {
		if (fluidStack == null) { return; }
		Fluid fluid = fluidStack.getFluid();
		if (fluid == null) { return; }

		TextureMap textureMapBlocks = mc.getTextureMapBlocks();
		ResourceLocation fluidStill = fluid.getStill();
		TextureAtlasSprite fluidStillSprite = null;
		if (fluidStill != null) {
			fluidStillSprite = textureMapBlocks.getTextureExtry(fluidStill.toString());
		}
		if (fluidStillSprite == null) {
			fluidStillSprite = textureMapBlocks.getMissingSprite();
		}

		int fluidColor = fluid.getColor(fluidStack);

		int scaledAmount = (fluidStack.amount * (TANK_HEIGHT - 8)) / capacityMb;
		if (fluidStack.amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
			scaledAmount = MIN_FLUID_HEIGHT;
		}
		if (scaledAmount > (TANK_HEIGHT - 8)) {
			scaledAmount = (TANK_HEIGHT - 8);
		}

		mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		setGLColorFromInt(fluidColor);

		final int xTileCount = (TANK_WIDTH - 8) / TEX_WIDTH;
		final int xRemainder = (TANK_WIDTH - 8) - (xTileCount * TEX_WIDTH);
		final int yTileCount = scaledAmount / TEX_HEIGHT;
		final int yRemainder = scaledAmount - (yTileCount * TEX_HEIGHT);

		final int yStart = yPosition + (TANK_HEIGHT - 8);

		for (int xTile = 0;xTile <= xTileCount;xTile++) {
			for (int yTile = 0;yTile <= yTileCount;yTile++) {
				int width = (xTile == xTileCount) ? xRemainder : TEX_WIDTH;
				int height = (yTile == yTileCount) ? yRemainder : TEX_HEIGHT;
				int x = xPosition + (xTile * TEX_WIDTH);
				int y = yStart - ((yTile + 1) * TEX_HEIGHT);
				if (width > 0 && height > 0) {
					int maskTop = TEX_HEIGHT - height;
					int maskRight = TEX_WIDTH - width;

					drawFluidTexture(x, y, fluidStillSprite, maskTop, maskRight, 100);
				}
			}
		}
	}

	private static final void setGLColorFromInt(int color) {
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;

		GlStateManager.color(red, green, blue, 1.0F);
	}

	private static final void drawFluidTexture(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
		double uMin = textureSprite.getMinU();
		double uMax = textureSprite.getMaxU();
		double vMin = textureSprite.getMinV();
		double vMax = textureSprite.getMaxV();
		uMax = uMax - (maskRight / 16.0 * (uMax - uMin));
		vMax = vMax - (maskTop / 16.0 * (vMax - vMin));

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexBuffer.pos(xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex();
		vertexBuffer.pos(xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
		vertexBuffer.pos(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
		vertexBuffer.pos(xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex();
		tessellator.draw();
	}

	private final void drawFluidTankTooltip(int mouseX, int mouseY) {
		if (isUnderMouse(mouseX, mouseY)) {
			List<String> tooltip = new ArrayList<>();
			if (tank.getFluid() == null || tank.getFluid().getFluid() == null) {
				tooltip.add(I18n.format("tomsMod.chat.empty"));
			} else {
				tooltip.add(tank.getFluid().getLocalizedName());
				tooltip.add(TextFormatting.GRAY + I18n.format("tomsmod.gui.fluid", tank.getFluidAmount(), tank.getCapacity()));
			}
			gui.drawHoveringText(tooltip, mouseX, mouseY);
		}
	}

	private final void drawFluidTankTooltip(int mouseX, int mouseY, String tankName) {
		if (isUnderMouse(mouseX, mouseY)) {
			List<String> tooltip = new ArrayList<>();
			tooltip.add(tankName);
			if (tank.getFluid() == null || tank.getFluid().getFluid() == null) {
				tooltip.add(I18n.format("tomsMod.chat.empty"));
			} else {
				tooltip.add(TextFormatting.GRAY + I18n.format("tomsmod.gui.fluid", tank.getFluidAmount(), tank.getCapacity()));
			}
			gui.drawHoveringText(tooltip, mouseX, mouseY);
		}
	}

	private final void drawFluidTank() {
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		mc.getTextureManager().bindTexture(LIST_TEXTURE);
		drawTexturedModalRect(posX, posY, 78, 120, TANK_WIDTH, TANK_HEIGHT);
		drawFluid(posX + 4, posY + 4, tank.getFluid(), tank.getCapacity());
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 200);
		mc.getTextureManager().bindTexture(LIST_TEXTURE);
		drawTexturedModalRect(posX, posY, 98, 120, TANK_WIDTH, TANK_HEIGHT);
		GlStateManager.popMatrix();
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.popMatrix();
	}

	private final void drawFluidTank(int texX, int texY) {
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableAlpha();
		if (texX > -1) {
			mc.getTextureManager().bindTexture(gui.getBackgroundTexture());
			drawTexturedModalRect(posX, posY, texX, texY, TANK_WIDTH, TANK_HEIGHT);
		}
		GlStateManager.disableBlend();
		drawFluid(posX + 4, posY + 4, tank.getFluid(), tank.getCapacity());
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 200);
		mc.getTextureManager().bindTexture(LIST_TEXTURE);
		drawTexturedModalRect(posX, posY, 98, 120, TANK_WIDTH, TANK_HEIGHT);
		GlStateManager.popMatrix();
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.popMatrix();
	}

	@Override
	public void run(int mouseX, int mouseY) {
		if (hasUV)
			drawFluidTank(u, v);
		else
			drawFluidTank();
	}

	public void drawTooltip(int mouseX, int mouseY) {
		if (name != null)
			drawFluidTankTooltip(mouseX, mouseY, name);
		else
			drawFluidTankTooltip(mouseX, mouseY);
	}

	public FluidStack getFluid() {
		return tank.getFluid();
	}

	public boolean isUnderMouse(int mouseX, int mouseY) {
		return gui.isPointInRegion(posX, posY, TANK_WIDTH, TANK_HEIGHT, mouseX + gui.getGuiLeft(), mouseY + gui.getGuiTop());
	}
}
