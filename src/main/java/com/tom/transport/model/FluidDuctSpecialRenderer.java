package com.tom.transport.model;

import org.lwjgl.opengl.GL11;

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

import com.tom.client.MultipartSpecialRendererTomsMod;
import com.tom.transport.multipart.FluidGrid;
import com.tom.transport.multipart.PartFluidDuct;

public class FluidDuctSpecialRenderer extends MultipartSpecialRendererTomsMod<PartFluidDuct> {

	@Override
	public void renderMultipartAtI(PartFluidDuct part, double x, double y, double z, float partialTicks,
			int destroyStage) {
		if(part.stack != null && part.stack.amount > 0){
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
			//bindTexture(new ResourceLocation(fluidTexture.getResourceDomain(), "textures/"+fluidTexture.getResourcePath()+".png"));
			float per = ((float) part.stack.amount) / ((float) FluidGrid.TANK_SIZE);
			double yPer = per * part.getSize() * 2;
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
			draw(part, x, y, z, s, yPer);
			for(EnumFacing f : EnumFacing.VALUES){
				if(part.connectsInv(f) || part.connects(f) || part.connectsM(f)){
					if(f.getAxis() != Axis.Y){
						draw(part, x+(0.331*f.getFrontOffsetX()), y, z+(0.331*f.getFrontOffsetZ()), s, yPer);
					}else{
						drawY(part, x, y+(0.331*f.getFrontOffsetY()), z, s, yPer);
					}
				}
			}
			/*draw(part, x+0.331, y, z, s, yPer);
			draw(part, x, y, z+0.331, s, yPer);
			draw(part, x-0.331, y, z, s, yPer);
			draw(part, x, y, z-0.331, s, yPer);*/
			GL11.glPopMatrix();
			GlStateManager.enableLighting();
		}
	}
	private static void draw(PartFluidDuct part, double x, double y, double z, TextureAtlasSprite s, double yPer){
		Tessellator tessellator = Tessellator.getInstance();
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
		}
	}
	private static void drawY(PartFluidDuct part, double x, double y, double z, TextureAtlasSprite s, double yPer){
		//TODO
	}
}
