package com.tom.transport.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;

import com.tom.client.TileEntitySpecialRendererTomsMod;
import com.tom.transport.multipart.FluidGrid;
import com.tom.transport.multipart.PartFluidDuctBase;

public class FluidDuctSpecialRenderer extends TileEntitySpecialRendererTomsMod<PartFluidDuctBase> {
	private static void draw(PartFluidDuctBase part, double x, double y, double z, TextureAtlasSprite s, double yPer) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		// x+0.5-part.getSize() x+0.5+part.getSize()
		double yPos = y + 0.5 - (part.getSize() * 1.1) + yPer;
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, yPos, z + 0.5 + part.getSize() * 0.9).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, yPos, z + 0.5 + part.getSize() * 0.9).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, yPos, z + 0.5 - part.getSize() * 0.9).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, yPos, z + 0.5 - part.getSize() * 0.9).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, yPos, z + 0.5 - part.getSize() * 0.9).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, yPos, z + 0.5 - part.getSize() * 0.9).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, yPos, z + 0.5 + part.getSize() * 0.9).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, yPos, z + 0.5 + part.getSize() * 0.9).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, yPos, z + 0.5 + part.getSize() * 0.9).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, yPos, z + 0.5 - part.getSize() * 0.9).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, yPos, z + 0.5 - part.getSize() * 0.9).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, yPos, z + 0.5 + part.getSize() * 0.9).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
	}

	private static void drawUp(PartFluidDuctBase part, double x, double y, double z, TextureAtlasSprite s, double yPer) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		double w = ((part.getSize() * 1.1) * yPer) * 2;
		double w2 = 0.15 - w;
		double yPos = (y + 0.839 - (part.getSize() * 0.9));
		y = y - (w2 * 2.5);
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + w2, yPos, z + 0.5 - part.getSize() * 0.9 + 0.15 - w).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - w2, yPos, z + 0.5 - part.getSize() * 0.9 + 0.15 - w).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - w2, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9 + 0.15 - w).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + w2, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9 + 0.15 - w).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - w2, yPos, z + 0.5 + part.getSize() * 0.9 - 0.15 + w).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + w2, yPos, z + 0.5 + part.getSize() * 0.9 - 0.15 + w).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + w2, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9 - 0.15 + w).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - w2, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9 - 0.15 + w).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + 0.15 - w, yPos, z + 0.5 + part.getSize() * 0.9 - w2).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + 0.15 - w, yPos, z + 0.5 - part.getSize() * 0.9 + w2).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + 0.15 - w, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9 + w2).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + 0.15 - w, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9 - w2).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - 0.15 + w, yPos, z + 0.5 - part.getSize() * 0.9 + w2).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - 0.15 + w, yPos, z + 0.5 + part.getSize() * 0.9 - w2).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - 0.15 + w, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9 - w2).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - 0.15 + w, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9 + w2).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
	}

	private static void drawDown(PartFluidDuctBase part, double x, double y, double z, TextureAtlasSprite s, double yPer) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		double w = ((part.getSize() * 1.1) * yPer) * 2;
		double w2 = 0.15 - w;
		double yPos = (y + 0.815 - (part.getSize() * 0.9));
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + w2, yPos, z + 0.5 - part.getSize() * 0.9 + 0.15 - w).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - w2, yPos, z + 0.5 - part.getSize() * 0.9 + 0.15 - w).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - w2, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9 + 0.15 - w).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + w2, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9 + 0.15 - w).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - w2, yPos, z + 0.5 + part.getSize() * 0.9 - 0.15 + w).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + w2, yPos, z + 0.5 + part.getSize() * 0.9 - 0.15 + w).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + w2, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9 - 0.15 + w).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - w2, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9 - 0.15 + w).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + 0.15 - w, yPos, z + 0.5 + part.getSize() * 0.9 - w2).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + 0.15 - w, yPos, z + 0.5 - part.getSize() * 0.9 + w2).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + 0.15 - w, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9 + w2).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9 + 0.15 - w, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9 - w2).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
		{
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - 0.15 + w, yPos, z + 0.5 - part.getSize() * 0.9 + w2).tex(s.getMaxU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - 0.15 + w, yPos, z + 0.5 + part.getSize() * 0.9 - w2).tex(s.getMinU(), s.getMinV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - 0.15 + w, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9 - w2).tex(s.getMinU(), s.getMaxV()).endVertex();
			vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9 - 0.15 + w, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9 + w2).tex(s.getMaxU(), s.getMaxV()).endVertex();
			tessellator.draw();
		}
	}

	@Override
	public void renderTileEntityAt(PartFluidDuctBase part, double x, double y, double z, float partialTicks, int destroyStage, IBlockState state) {
		if (part.stack != null) {
			// if((part.stackOld == null || part.stackOld != part.stack ||
			// !(part.stack.isFluidStackIdentical(part.stackOld))) ||
			// part.render == null){
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			GlStateManager.color(1, 1, 1, 1);
			// GL11.glNewList(part.render, GL11.GL_COMPILE_AND_EXECUTE);
			// part.stackOld = part.stack;
			mc.mcProfiler.startSection("predraw");
			GL11.glPushMatrix();
			GlStateManager.color(1, 1, 1, 1);
			ResourceLocation fluidStill = part.stack.getFluid().getStill(part.stack);
			TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
			TextureAtlasSprite s = null;
			if (fluidStill != null) {
				s = textureMapBlocks.getTextureExtry(fluidStill.toString());
			}
			if (s == null) {
				s = textureMapBlocks.getMissingSprite();
			}
			int fluidColor = part.stack.getFluid().getColor(part.stack);
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			{
				int color = fluidColor;
				float red = (color >> 16 & 0xFF) / 255.0F;
				float green = (color >> 8 & 0xFF) / 255.0F;
				float blue = (color & 0xFF) / 255.0F;

				GlStateManager.color(red, green, blue, 1.0F);
			}
			// bindTexture(new
			// ResourceLocation(fluidTexture.getResourceDomain(),
			// "textures/"+fluidTexture.getResourcePath()+".png"));
			float per = ((float) part.stack.amount) / ((float) FluidGrid.TANK_SIZE);
			double yPer = per * part.getSize() * 2;
			mc.mcProfiler.endStartSection("draw");
			GL11.glPushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			/*Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vertexbuffer = tessellator.getBuffer();
			//x+0.5-part.getSize() x+0.5+part.getSize()
			double yPos = y + 0.5 - (part.getSize() * 1.1) + yPer;
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, yPos, z + 0.5 + part.getSize() * 0.9) .tex(s.getMaxU(), s.getMinV()) .endVertex();
				vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, yPos, z + 0.5 + part.getSize() * 0.9) .tex(s.getMinU(), s.getMinV()) .endVertex();
				vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, yPos, z + 0.5 - part.getSize() * 0.9) .tex(s.getMinU(), s.getMaxV()) .endVertex();
				vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, yPos, z + 0.5 - part.getSize() * 0.9) .tex(s.getMaxU(), s.getMaxV()) .endVertex();
				tessellator.draw();
			}{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9) .tex(s.getMaxU(), s.getMinV()) .endVertex();
				vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9) .tex(s.getMinU(), s.getMinV()) .endVertex();
				vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9) .tex(s.getMinU(), s.getMaxV()) .endVertex();
				vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9) .tex(s.getMaxU(), s.getMaxV()) .endVertex();
				tessellator.draw();
			}
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, yPos,   z + 0.5 - part.getSize() * 0.9) .tex(s.getMaxU(), s.getMinV()) .endVertex();
				vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, yPos,   z + 0.5 - part.getSize() * 0.9) .tex(s.getMinU(), s.getMinV()) .endVertex();
				vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9) .tex(s.getMinU(), s.getMaxV()) .endVertex();
				vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9) .tex(s.getMaxU(), s.getMaxV()) .endVertex();
				tessellator.draw();
			}{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, yPos,   z + 0.5 + part.getSize() * 0.9) .tex(s.getMaxU(), s.getMinV()) .endVertex();
				vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, yPos,   z + 0.5 + part.getSize() * 0.9) .tex(s.getMinU(), s.getMinV()) .endVertex();
				vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9) .tex(s.getMinU(), s.getMaxV()) .endVertex();
				vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9) .tex(s.getMaxU(), s.getMaxV()) .endVertex();
				tessellator.draw();
			}
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, yPos,   z + 0.5 + part.getSize() * 0.9) .tex(s.getMaxU(), s.getMinV()) .endVertex();
				vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, yPos,   z + 0.5 - part.getSize() * 0.9) .tex(s.getMinU(), s.getMinV()) .endVertex();
				vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9) .tex(s.getMinU(), s.getMaxV()) .endVertex();
				vertexbuffer.pos(x + 0.5 - part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9) .tex(s.getMaxU(), s.getMaxV()) .endVertex();
				tessellator.draw();
			}{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, yPos,   z + 0.5 - part.getSize() * 0.9) .tex(s.getMaxU(), s.getMinV()) .endVertex();
				vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, yPos,   z + 0.5 + part.getSize() * 0.9) .tex(s.getMinU(), s.getMinV()) .endVertex();
				vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 + part.getSize() * 0.9) .tex(s.getMinU(), s.getMaxV()) .endVertex();
				vertexbuffer.pos(x + 0.5 + part.getSize() * 0.9, y + 0.5 - (part.getSize() * 0.9), z + 0.5 - part.getSize() * 0.9) .tex(s.getMaxU(), s.getMaxV()) .endVertex();
				tessellator.draw();
			}*/
			mc.mcProfiler.startSection("base");
			draw(part, 0, 0, 0, s, yPer);
			mc.mcProfiler.endSection();
			for (EnumFacing f : EnumFacing.VALUES) {
				mc.mcProfiler.startSection(f.getName());
				if (part.connectsInv(f) || part.connects(f) || part.connectsM(f)) {
					if (f.getAxis() != Axis.Y) {
						draw(part, 0 + (0.331 * f.getFrontOffsetX()), 0, 0 + (0.331 * f.getFrontOffsetZ()), s, yPer);
					} else {
						if (f == EnumFacing.UP)
							drawUp(part, 0, 0 + (0.331 * f.getFrontOffsetY()), 0, s, yPer);
						else
							drawDown(part, 0, 0 + (0.331 * f.getFrontOffsetY()), 0, s, yPer);
					}
				}
				mc.mcProfiler.endSection();
			}
			/*draw(part, x+0.331, y, z, s, yPer);
			draw(part, x, y, z+0.331, s, yPer);
			draw(part, x-0.331, y, z, s, yPer);
			draw(part, x, y, z-0.331, s, yPer);*/
			GL11.glPopMatrix();
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.enableLighting();
			GL11.glPopMatrix();
			GlStateManager.disableBlend();
			// GL11.glEndList();
			GL11.glPopMatrix();
			mc.mcProfiler.endSection();
			/*}else{
				GL11.glPushMatrix();
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glTranslated(x, y, z);
				GlStateManager.callList(part.render);
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				GL11.glPopMatrix();
			}*/
		}
	}
}
