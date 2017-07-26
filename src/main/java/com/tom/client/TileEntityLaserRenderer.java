package com.tom.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import com.tom.energy.tileentity.TileEntityLaserBase;

public class TileEntityLaserRenderer extends TileEntitySpecialRendererTomsMod<TileEntityLaserBase> {

	@Override
	public void renderTileEntityAt(TileEntityLaserBase te, double x, double y, double z, float partialTicks, int destroyStage, IBlockState state) {
		if (state.getBlock() != te.getBlockType())
			return;
		EnumFacing facing = te.getFacing();
		BlockPos posR = te.getReceiver();
		BlockPos pos = te.getPos();
		GlStateManager.pushMatrix();// main
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer renderer = tessellator.getBuffer();
		// float fAlpha = 0.125f;
		if (posR != null) {
			this.bindTexture(new ResourceLocation(te.beamTexture));
			double u1 = 0.0D, v1 = 1.0D, u2 = 1.0D, v2 = 0.0D;
			int dist;
			switch (facing) {
			case DOWN:
				dist = pos.getY() - posR.getY();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x, y - dist + 0.9D, z + 0.5).tex(u2, v1).endVertex();
				renderer.pos(x, y + 0.2, z + 0.5).tex(u1, v1).endVertex();
				renderer.pos(x + 1, y + 0.2, z + 0.5).tex(u1, v2).endVertex();
				renderer.pos(x + 1, y - dist + 0.9D, z + 0.5).tex(u2, v2).endVertex();
				tessellator.draw();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + 0.5, y - dist + 0.9D, z).tex(u2, v1).endVertex();
				renderer.pos(x + 0.5, y + 0.2, z).tex(u1, v1).endVertex();
				renderer.pos(x + 0.5, y + 0.2, z + 1).tex(u1, v2).endVertex();
				renderer.pos(x + 0.5, y - dist + 0.9D, z + 1).tex(u2, v2).endVertex();
				tessellator.draw();
				// GL11.glRotated(180, 0, 1, 0);
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x, y + 0.2, z + 0.5).tex(u1, v1).endVertex();
				renderer.pos(x, y - dist + 0.9D, z + 0.5).tex(u2, v1).endVertex();
				renderer.pos(x + 1, y - dist + 0.9D, z + 0.5).tex(u2, v2).endVertex();
				renderer.pos(x + 1, y + 0.2, z + 0.5).tex(u1, v2).endVertex();
				tessellator.draw();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + 0.5, y + 0.2, z).tex(u1, v1).endVertex();
				renderer.pos(x + 0.5, y - dist + 0.9D, z).tex(u2, v1).endVertex();
				renderer.pos(x + 0.5, y - dist + 0.9D, z + 1).tex(u2, v2).endVertex();
				renderer.pos(x + 0.5, y + 0.2, z + 1).tex(u1, v2).endVertex();
				tessellator.draw();
				break;
			case EAST:
				dist = posR.getX() - pos.getX();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + dist + 0.2, y + 1, z + 0.5).tex(u2, v1).endVertex();
				renderer.pos(x + 0.8, y + 1, z + 0.5).tex(u1, v1).endVertex();
				renderer.pos(x + 0.8, y, z + 0.5).tex(u1, v2).endVertex();
				renderer.pos(x + dist + 0.2, y, z + 0.5).tex(u2, v2).endVertex();
				tessellator.draw();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + dist + 0.2, y + 0.5, z).tex(u2, v1).endVertex();
				renderer.pos(x + 0.8, y + 0.5, z).tex(u1, v1).endVertex();
				renderer.pos(x + 0.8, y + 0.5, z + 1).tex(u1, v2).endVertex();
				renderer.pos(x + dist + 0.2, y + 0.5, z + 1).tex(u2, v2).endVertex();
				tessellator.draw();

				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + 0.8, y + 1, z + 0.5).tex(u1, v1).endVertex();
				renderer.pos(x + dist + 0.2, y + 1, z + 0.5).tex(u2, v1).endVertex();
				renderer.pos(x + dist + 0.2, y, z + 0.5).tex(u2, v2).endVertex();
				renderer.pos(x + 0.8, y, z + 0.5).tex(u1, v2).endVertex();
				tessellator.draw();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + 0.8, y + 0.5, z).tex(u1, v1).endVertex();
				renderer.pos(x + dist + 0.2, y + 0.5, z).tex(u2, v1).endVertex();
				renderer.pos(x + dist + 0.2, y + 0.5, z + 1).tex(u2, v2).endVertex();
				renderer.pos(x + 0.8, y + 0.5, z + 1).tex(u1, v2).endVertex();
				tessellator.draw();
				break;
			case NORTH:
				dist = pos.getZ() - posR.getZ();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + 0.5, y + 1, z - dist + 0.8).tex(u2, v1).endVertex();
				renderer.pos(x + 0.5, y + 1, z + 0.2).tex(u1, v1).endVertex();
				renderer.pos(x + 0.5, y, z + 0.2).tex(u1, v2).endVertex();
				renderer.pos(x + 0.5, y, z - dist + 0.8).tex(u2, v2).endVertex();
				tessellator.draw();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x, y + 0.5, z - dist + 0.8).tex(u2, v1).endVertex();
				renderer.pos(x, y + 0.5, z + 0.2).tex(u1, v1).endVertex();
				renderer.pos(x + 1, y + 0.5, z + 0.2).tex(u1, v2).endVertex();
				renderer.pos(x + 1, y + 0.5, z - dist + 0.8).tex(u2, v2).endVertex();
				tessellator.draw();

				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + 0.5, y + 1, z + 0.2).tex(u1, v1).endVertex();
				renderer.pos(x + 0.5, y + 1, z - dist + 0.8).tex(u2, v1).endVertex();
				renderer.pos(x + 0.5, y, z - dist + 0.8).tex(u2, v2).endVertex();
				renderer.pos(x + 0.5, y, z + 0.2).tex(u1, v2).endVertex();
				tessellator.draw();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x, y + 0.5, z + 0.2).tex(u1, v1).endVertex();
				renderer.pos(x, y + 0.5, z - dist + 0.8).tex(u2, v1).endVertex();
				renderer.pos(x + 1, y + 0.5, z - dist + 0.8).tex(u2, v2).endVertex();
				renderer.pos(x + 1, y + 0.5, z + 0.2).tex(u1, v2).endVertex();
				tessellator.draw();
				break;
			case SOUTH:
				dist = posR.getZ() - pos.getZ();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + 0.5, y + 1, z + dist + 0.2).tex(u2, v1).endVertex();
				renderer.pos(x + 0.5, y + 1, z + 0.8).tex(u1, v1).endVertex();
				renderer.pos(x + 0.5, y, z + 0.8).tex(u1, v2).endVertex();
				renderer.pos(x + 0.5, y, z + dist + 0.2).tex(u2, v2).endVertex();
				tessellator.draw();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x, y + 0.5, z + dist + 0.2).tex(u2, v1).endVertex();
				renderer.pos(x, y + 0.5, z + 0.8).tex(u1, v1).endVertex();
				renderer.pos(x + 1, y + 0.5, z + 0.8).tex(u1, v2).endVertex();
				renderer.pos(x + 1, y + 0.5, z + dist + 0.2).tex(u2, v2).endVertex();
				tessellator.draw();

				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + 0.5, y + 1, z + 0.8).tex(u1, v1).endVertex();
				renderer.pos(x + 0.5, y + 1, z + dist + 0.2).tex(u2, v1).endVertex();
				renderer.pos(x + 0.5, y, z + dist + 0.2).tex(u2, v2).endVertex();
				renderer.pos(x + 0.5, y, z + 0.8).tex(u1, v2).endVertex();
				tessellator.draw();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x, y + 0.5, z + 0.8).tex(u1, v1).endVertex();
				renderer.pos(x, y + 0.5, z + dist + 0.2).tex(u2, v1).endVertex();
				renderer.pos(x + 1, y + 0.5, z + dist + 0.2).tex(u2, v2).endVertex();
				renderer.pos(x + 1, y + 0.5, z + 0.8).tex(u1, v2).endVertex();
				tessellator.draw();
				break;
			case UP:
				dist = posR.getY() - pos.getY();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x, y + dist + 0.9D, z + 0.5).tex(u2, v1).endVertex();
				renderer.pos(x, y + 0.8, z + 0.5).tex(u1, v1).endVertex();
				renderer.pos(x + 1, y + 0.8, z + 0.5).tex(u1, v2).endVertex();
				renderer.pos(x + 1, y + dist + 0.9D, z + 0.5).tex(u2, v2).endVertex();
				tessellator.draw();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + 0.5, y + dist + 0.9D, z).tex(u2, v1).endVertex();
				renderer.pos(x + 0.5, y + 0.8, z).tex(u1, v1).endVertex();
				renderer.pos(x + 0.5, y + 0.8, z + 1).tex(u1, v2).endVertex();
				renderer.pos(x + 0.5, y + dist + 0.9D, z + 1).tex(u2, v2).endVertex();
				tessellator.draw();

				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x, y + 0.8, z + 0.5).tex(u1, v1).endVertex();
				renderer.pos(x, y + dist + 0.9D, z + 0.5).tex(u2, v1).endVertex();
				renderer.pos(x + 1, y + dist + 0.9D, z + 0.5).tex(u2, v2).endVertex();
				renderer.pos(x + 1, y + 0.8, z + 0.5).tex(u1, v2).endVertex();
				tessellator.draw();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + 0.5, y + 0.8, z).tex(u1, v1).endVertex();
				renderer.pos(x + 0.5, y + dist + 0.9D, z).tex(u2, v1).endVertex();
				renderer.pos(x + 0.5, y + dist + 0.9D, z + 1).tex(u2, v2).endVertex();
				renderer.pos(x + 0.5, y + 0.8, z + 1).tex(u1, v2).endVertex();
				tessellator.draw();
				break;
			case WEST:
				dist = pos.getX() - posR.getX();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x - dist + 0.8, y + 1, z + 0.5).tex(u2, v1).endVertex();
				renderer.pos(x + 0.2, y + 1, z + 0.5).tex(u1, v1).endVertex();
				renderer.pos(x + 0.2, y, z + 0.5).tex(u1, v2).endVertex();
				renderer.pos(x - dist + 0.8, y, z + 0.5).tex(u2, v2).endVertex();
				tessellator.draw();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x - dist + 0.8, y + 0.5, z).tex(u2, v1).endVertex();
				renderer.pos(x + 0.2, y + 0.5, z).tex(u1, v1).endVertex();
				renderer.pos(x + 0.2, y + 0.5, z + 1).tex(u1, v2).endVertex();
				renderer.pos(x - dist + 0.8, y + 0.5, z + 1).tex(u2, v2).endVertex();
				tessellator.draw();

				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + 0.2, y + 1, z + 0.5).tex(u1, v1).endVertex();
				renderer.pos(x - dist + 0.8, y + 1, z + 0.5).tex(u2, v1).endVertex();
				renderer.pos(x - dist + 0.8, y, z + 0.5).tex(u2, v2).endVertex();
				renderer.pos(x + 0.2, y, z + 0.5).tex(u1, v2).endVertex();
				tessellator.draw();
				renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(x + 0.2, y + 0.5, z).tex(u1, v1).endVertex();
				renderer.pos(x - dist + 0.8, y + 0.5, z).tex(u2, v1).endVertex();
				renderer.pos(x - dist + 0.8, y + 0.5, z + 1).tex(u2, v2).endVertex();
				renderer.pos(x + 0.2, y + 0.5, z + 1).tex(u1, v2).endVertex();
				tessellator.draw();
				break;
			default:
				break;
			}
		}
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();// main
	}
}
