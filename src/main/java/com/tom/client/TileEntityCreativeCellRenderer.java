package com.tom.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.energy.tileentity.TileEntityCreativeCell;

@SideOnly(Side.CLIENT)
public class TileEntityCreativeCellRenderer extends TileEntitySpecialRendererTomsMod<TileEntityCreativeCell> {
	@Override
	public void renderTileEntityAt(TileEntityCreativeCell te, double x, double y, double z, float partialTicks, int destroyStage, IBlockState state) {
		GlStateManager.pushMatrix();// main
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.pushMatrix();// side
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer renderer = tessellator.getBuffer();
		double u1 = 0.0D, v1 = 1.0D, u2 = 1.0D, v2 = 0.0D;
		int w = 1;
		int h = 1;
		bindTexture(new ResourceLocation("tomsmodenergy:textures/blocks/energyCellOut.png"));
		try {
			for (EnumFacing f : EnumFacing.VALUES) {
				if (te.contains(f)) {
					boolean isPositive = f.getAxisDirection() == AxisDirection.POSITIVE;
					if (f.getAxis() == Axis.X) {
						if (isPositive) {
							renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
							renderer.pos(x + 1.001, y, z + w).tex(u2, v1).endVertex();
							renderer.pos(x + 1.001, y, z).tex(u1, v1).endVertex();
							renderer.pos(x + 1.001, y + h, z).tex(u1, v2).endVertex();
							renderer.pos(x + 1.001, y + h, z + w).tex(u2, v2).endVertex();
							tessellator.draw();
							continue;
						} else {
							renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
							renderer.pos(x - 0.001, y, z - w + 1).tex(u2, v1).endVertex();
							renderer.pos(x - 0.001, y, z + 1).tex(u1, v1).endVertex();
							renderer.pos(x - 0.001, y + h, z + 1).tex(u1, v2).endVertex();
							renderer.pos(x - 0.001, y + h, z - w + 1).tex(u2, v2).endVertex();
							tessellator.draw();
							continue;
						}
					} else if (f.getAxis() == Axis.Y) {
						if (isPositive) {
							renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
							renderer.pos(x + w, y + 1.001, z).tex(u2, v1).endVertex();
							renderer.pos(x, y + 1.001, z).tex(u1, v1).endVertex();
							renderer.pos(x, y + 1.001, z + h).tex(u1, v2).endVertex();
							renderer.pos(x + w, y + 1.001, z + h).tex(u2, v2).endVertex();
							tessellator.draw();
							continue;
						} else {
							renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
							renderer.pos(x - w + 1, y - 0.001, z).tex(u2, v1).endVertex();
							renderer.pos(x + 1, y - 0.001, z).tex(u1, v1).endVertex();
							renderer.pos(x + 1, y - 0.001, z + h).tex(u1, v2).endVertex();
							renderer.pos(x - w + 1, y - 0.001, z + h).tex(u2, v2).endVertex();
							tessellator.draw();
							continue;
						}
					} else {
						if (isPositive) {
							renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
							renderer.pos(x - w + 1, y, z + 1.001).tex(u2, v1).endVertex();
							renderer.pos(x + 1, y, z + 1.001).tex(u1, v1).endVertex();
							renderer.pos(x + 1, y + h, z + 1.001).tex(u1, v2).endVertex();
							renderer.pos(x - w + 1, y + h, z + 1.001).tex(u2, v2).endVertex();
							tessellator.draw();
							continue;
						}
					}
					renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					renderer.pos(x + w, y, z - 0.001).tex(u2, v1).endVertex();
					renderer.pos(x, y, z - 0.001).tex(u1, v1).endVertex();
					renderer.pos(x, y + h, z - 0.001).tex(u1, v2).endVertex();
					renderer.pos(x + w, y + h, z - 0.001).tex(u2, v2).endVertex();
					tessellator.draw();
				}
			}
		} catch (Exception e) {
		}
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();// side
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();// main
	}
}
