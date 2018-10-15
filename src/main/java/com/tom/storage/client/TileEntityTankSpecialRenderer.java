package com.tom.storage.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import com.tom.client.TileEntitySpecialRendererTomsMod;
import com.tom.storage.tileentity.TMTank;

public class TileEntityTankSpecialRenderer extends TileEntitySpecialRendererTomsMod<TMTank> {
	public boolean drawing;

	@Override
	public void renderTileEntityAt(TMTank te, double x, double y, double z, float partialTicks, int destroyStage, IBlockState state) {
		drawing = true;
		if (te.getStack() != null && te.getStack().amount > 0) {
			ResourceLocation fluidStill = te.getStack().getFluid().getStill(te.getStack());
			TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
			TextureAtlasSprite s = null;
			if (fluidStill != null) {
				s = textureMapBlocks.getTextureExtry(fluidStill.toString());
			}
			if (s == null) {
				s = textureMapBlocks.getMissingSprite();
			}
			int fluidColor = te.getStack().getFluid().getColor(te.getStack());
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
			float per = ((float) te.getStack().amount) / ((float) te.getCapacity());
			double yPer = per * 0.99;
			GL11.glPushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexbuffer = tessellator.getBuffer();
			// x+0.5-part.getSize() x+0.5+part.getSize()
			double yPos = y + yPer;
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(x, yPos, z + 1).tex(s.getMaxU(), s.getMinV()).endVertex();
				vertexbuffer.pos(x + 0.99, yPos, z + 1).tex(s.getMinU(), s.getMinV()).endVertex();
				vertexbuffer.pos(x + 0.99, yPos, z + 0).tex(s.getMinU(), s.getMaxV()).endVertex();
				vertexbuffer.pos(x, yPos, z + 0).tex(s.getMaxU(), s.getMaxV()).endVertex();
				tessellator.draw();
			}
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(x + 0.99, y + 0.01, z + 0.99).tex(s.getMaxU(), s.getMinV()).endVertex();
				vertexbuffer.pos(x + 0.01, y + 0.01, z + 0.99).tex(s.getMinU(), s.getMinV()).endVertex();
				vertexbuffer.pos(x + 0.01, y + 0.01, z + 0.01).tex(s.getMinU(), s.getMaxV()).endVertex();
				vertexbuffer.pos(x + 0.99, y + 0.01, z + 0.01).tex(s.getMaxU(), s.getMaxV()).endVertex();
				tessellator.draw();
			}
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(x + 0.01, yPos, z + 0.01).tex(s.getMaxU(), s.getMinV()).endVertex();
				vertexbuffer.pos(x + 0.99, yPos, z + 0.01).tex(s.getMinU(), s.getMinV()).endVertex();
				vertexbuffer.pos(x + 0.99, y + 0.01, z + 0.01).tex(s.getMinU(), s.getMaxV()).endVertex();
				vertexbuffer.pos(x + 0.01, y + 0.01, z + 0.01).tex(s.getMaxU(), s.getMaxV()).endVertex();
				tessellator.draw();
			}
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(x + 0.99, yPos, z + 0.99).tex(s.getMaxU(), s.getMinV()).endVertex();
				vertexbuffer.pos(x + 0.01, yPos, z + 0.99).tex(s.getMinU(), s.getMinV()).endVertex();
				vertexbuffer.pos(x + 0.01, y + 0.01, z + 0.99).tex(s.getMinU(), s.getMaxV()).endVertex();
				vertexbuffer.pos(x + 0.99, y + 0.01, z + 0.99).tex(s.getMaxU(), s.getMaxV()).endVertex();
				tessellator.draw();
			}
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(x + 0.01, yPos, z + 0.99).tex(s.getMaxU(), s.getMinV()).endVertex();
				vertexbuffer.pos(x + 0.01, yPos, z + 0.01).tex(s.getMinU(), s.getMinV()).endVertex();
				vertexbuffer.pos(x + 0.01, y + 0.01, z + 0.01).tex(s.getMinU(), s.getMaxV()).endVertex();
				vertexbuffer.pos(x + 0.01, y + 0.01, z + 0.99).tex(s.getMaxU(), s.getMaxV()).endVertex();
				tessellator.draw();
			}
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(x + 0.99, yPos, z + 0.01).tex(s.getMaxU(), s.getMinV()).endVertex();
				vertexbuffer.pos(x + 0.99, yPos, z + 0.99).tex(s.getMinU(), s.getMinV()).endVertex();
				vertexbuffer.pos(x + 0.99, y + 0.01, z + 0.99).tex(s.getMinU(), s.getMaxV()).endVertex();
				vertexbuffer.pos(x + 0.99, y + 0.01, z + 0.01).tex(s.getMaxU(), s.getMaxV()).endVertex();
				tessellator.draw();
			}
			GL11.glPopMatrix();
			GlStateManager.enableLighting();
		}
		drawing = false;
	}

}
