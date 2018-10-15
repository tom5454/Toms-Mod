package com.tom.core.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import com.tom.client.TileEntitySpecialRendererTomsMod;

import com.tom.core.tileentity.TileEntityTemplate;

public class TileEntityTemplateSpecialRenderer extends TileEntitySpecialRendererTomsMod<TileEntityTemplate> {

	@Override
	public void renderTileEntityAt(TileEntityTemplate te, double x, double y, double z, float partialTicks, int destroyStage, IBlockState state) {
		GL11.glPushMatrix();// main
		IBlockState s = te.getState();
		if (s != null) {
			GL11.glPushMatrix();// block
			GlStateManager.color(1, 1, 1, 1);
			GL11.glTranslated(x - te.getPos().getX(), y - te.getPos().getY(), z - te.getPos().getZ());
			net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GL11.glEnable(GL11.GL_BLEND);
			GlStateManager.enableDepth();
			// GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_SRC_ALPHA);
			GlStateManager.blendFunc(GL11.GL_ONE_MINUS_SRC_COLOR, GL11.GL_SRC_COLOR);
			// GlStateManager.blendFunc(GL11.GL_SRC_ALPHA,
			// GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModel(te.getWorld(), mc.getBlockRendererDispatcher().getModelForState(s), s, te.getPos(), buffer, false);
			tessellator.draw();
			net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();// block
		}
		GlStateManager.color(1, 1, 1, 1);
		if (this.rendererDispatcher.cameraHitResult != null && te.getPos().equals(this.rendererDispatcher.cameraHitResult.getBlockPos())) {
			this.setLightmapDisabled(true);
			drawNameplate(te, s == null ? "~~NULL~~" : te.getStack().getDisplayName(), x, y, z, 16);
			this.setLightmapDisabled(false);
			GL11.glDisable(GL11.GL_BLEND);
		}
		GlStateManager.color(1, 1, 1, 1);
		GL11.glPopMatrix();// main
	}

}
