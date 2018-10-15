package com.tom.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;

import com.tom.api.tileentity.TileEntityCamoable;
import com.tom.util.TomsModUtils;

public class TileEntityCamoableSpecialRenderer extends TileEntitySpecialRendererTomsMod<TileEntityCamoable> {

	@Override
	public void renderTileEntityAt(TileEntityCamoable te, double x, double y, double z, float partialTicks, int destroyStage, IBlockState state) {
		if (!te.doRender())
			return;
		AxisAlignedBB bbRaw = te.getBounds();
		// BlockPos pos = te.getPos();
		double xStart = bbRaw.minX/* - pos.getX() */ + x;
		double xStop = bbRaw.maxX/* - pos.getX() */ + x;
		double yStart = bbRaw.minY/* - pos.getY() */ + y;
		double yStop = bbRaw.maxY/* - pos.getY() */ + y;
		double zStart = bbRaw.minZ/* - pos.getZ() */ + z;
		double zStop = bbRaw.maxZ/* - pos.getZ() */ + z;
		// double maxX = bbRaw.maxX - bbRaw.minX;
		// double maxY = bbRaw.maxY - bbRaw.minY;
		// double maxZ = bbRaw.maxZ - bbRaw.minZ;
		IBakedModel m = TomsModUtils.getBakedModelFromItemBlockStack(te.getCamoStack(), te.getDefaultState());
		GL11.glPushMatrix();
		try {
			TextureAtlasSprite s = m.getParticleTexture();
			GlStateManager.enableTexture2D();
			// ResourceLocation l = new ResourceLocation(s.getIconName());
			// bindTexture(new ResourceLocation(l.getResourceDomain(),
			// "textures/" + l.getResourcePath() + ".png"));
			// bindTexture(DESTROY_STAGES[5]);
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			GlStateManager.enableRescaleNormal();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			/*drawTexturedModalRect(xStart, yStart, zStart, s, bbRaw.maxX, bbRaw.maxY);
			drawTexturedModalRect(xStart, yStart, zStop, s, bbRaw.maxX, bbRaw.maxY);*/
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexbuffer = tessellator.getBuffer();
			/*{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(xStart + 0, yStart + bbRaw.maxY, zStart).tex(0, 1).endVertex();
				vertexbuffer.pos(xStart + bbRaw.maxX, yStart + bbRaw.maxY, zStart).tex(1, 1).endVertex();
				vertexbuffer.pos(xStart + bbRaw.maxX, yStart + 0, zStart).tex(1, 0).endVertex();
				vertexbuffer.pos(xStart + 0, yStart + 0, zStart).tex(0, 0).endVertex();
				tessellator.draw();
			}*/
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(xStart, yStop, zStart).tex(s.getMaxU(), s.getMinV()).endVertex();
				vertexbuffer.pos(xStop, yStop, zStart).tex(s.getMinU(), s.getMinV()).endVertex();
				vertexbuffer.pos(xStop, yStart, zStart).tex(s.getMinU(), s.getMaxV()).endVertex();
				vertexbuffer.pos(xStart, yStart, zStart).tex(s.getMaxU(), s.getMaxV()).endVertex();
				tessellator.draw();
			}
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(xStop, yStop, zStop).tex(s.getMaxU(), s.getMinV()).endVertex();
				vertexbuffer.pos(xStart, yStop, zStop).tex(s.getMinU(), s.getMinV()).endVertex();
				vertexbuffer.pos(xStart, yStart, zStop).tex(s.getMinU(), s.getMaxV()).endVertex();
				vertexbuffer.pos(xStop, yStart, zStop).tex(s.getMaxU(), s.getMaxV()).endVertex();
				tessellator.draw();
			}
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(xStart, yStop, zStop).tex(s.getMaxU(), s.getMinV()).endVertex();
				vertexbuffer.pos(xStart, yStop, zStart).tex(s.getMinU(), s.getMinV()).endVertex();
				vertexbuffer.pos(xStart, yStart, zStart).tex(s.getMinU(), s.getMaxV()).endVertex();
				vertexbuffer.pos(xStart, yStart, zStop).tex(s.getMaxU(), s.getMaxV()).endVertex();
				tessellator.draw();
			}
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(xStop, yStop, zStart).tex(s.getMaxU(), s.getMinV()).endVertex();
				vertexbuffer.pos(xStop, yStop, zStop).tex(s.getMinU(), s.getMinV()).endVertex();
				vertexbuffer.pos(xStop, yStart, zStop).tex(s.getMinU(), s.getMaxV()).endVertex();
				vertexbuffer.pos(xStop, yStart, zStart).tex(s.getMaxU(), s.getMaxV()).endVertex();
				tessellator.draw();
			}
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(xStart, yStart, zStart).tex(s.getMaxU(), s.getMinV()).endVertex();
				vertexbuffer.pos(xStop, yStart, zStart).tex(s.getMinU(), s.getMinV()).endVertex();
				vertexbuffer.pos(xStop, yStart, zStop).tex(s.getMinU(), s.getMaxV()).endVertex();
				vertexbuffer.pos(xStart, yStart, zStop).tex(s.getMaxU(), s.getMaxV()).endVertex();
				tessellator.draw();
			}
			{
				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				vertexbuffer.pos(xStart, yStop, zStop).tex(s.getMaxU(), s.getMinV()).endVertex();
				vertexbuffer.pos(xStop, yStop, zStop).tex(s.getMinU(), s.getMinV()).endVertex();
				vertexbuffer.pos(xStop, yStop, zStart).tex(s.getMinU(), s.getMaxV()).endVertex();
				vertexbuffer.pos(xStart, yStop, zStart).tex(s.getMaxU(), s.getMaxV()).endVertex();
				tessellator.draw();
			}
			if (destroyStage >= 0) {

			}
			GlStateManager.disableBlend();
		} catch (Throwable e) {
		}
		GL11.glPopMatrix();
	}
}
