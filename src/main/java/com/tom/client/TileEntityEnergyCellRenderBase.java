package com.tom.client;

import static org.lwjgl.opengl.ARBDepthClamp.GL_DEPTH_CLAMP;

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

import com.tom.energy.tileentity.TileEntityEnergyCellBase;

@SideOnly(Side.CLIENT)
public class TileEntityEnergyCellRenderBase extends TileEntitySpecialRendererTomsMod<TileEntityEnergyCellBase>{
	//private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	@Override
	public void renderTileEntityAt(TileEntityEnergyCellBase te, double x, double y,
			double z, float partialTicks, int destroyStage, IBlockState state) {//RenderItem
		//Minecraft mc = Minecraft.getMinecraft();
		GlStateManager.pushMatrix();//main
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		//GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.pushMatrix();//side
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer renderer = tessellator.getBuffer();
		double u1 = 0.0D,v1= 1.0D,u2= 1.0D,v2= 0.0D;
		int w = 1;
		int h = 1;
		/*if(te.displayList == -1 || te.sideModified){
			te.compileSideRender();
			te.sideModified = false;
		}
		GlStateManager.callList(te.displayList);*/
		bindTexture(new ResourceLocation("tomsmodenergy:textures/blocks/energyCellOut.png"));
		try{
			for(EnumFacing f : EnumFacing.VALUES){
				if(te.contains(f)){
					boolean isPositive = f.getAxisDirection() == AxisDirection.POSITIVE;
					if(f.getAxis() == Axis.X){
						if(isPositive){
							renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
							renderer.pos(x+1.001, y, z + w).tex(u2, v1).endVertex();
							renderer.pos(x+1.001, y, z).tex(u1, v1).endVertex();
							renderer.pos(x+1.001, y + h, z).tex(u1, v2).endVertex();
							renderer.pos(x+1.001, y + h, z + w).tex(u2, v2).endVertex();
							tessellator.draw();
							continue;
						}else{
							renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
							renderer.pos(x-0.001, y, z - w + 1).tex(u2, v1).endVertex();
							renderer.pos(x-0.001, y, z + 1).tex(u1, v1).endVertex();
							renderer.pos(x-0.001, y + h, z + 1).tex(u1, v2).endVertex();
							renderer.pos(x-0.001, y + h, z - w + 1).tex(u2, v2).endVertex();
							tessellator.draw();
							continue;
						}
					}else if(f.getAxis() == Axis.Y){
						if(isPositive){
							renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
							renderer.pos(x + w, y + 1.001, z).tex(u2, v1).endVertex();
							renderer.pos(x, y + 1.001, z).tex(u1, v1).endVertex();
							renderer.pos(x, y + 1.001, z + h).tex(u1, v2).endVertex();
							renderer.pos(x + w, y + 1.001, z + h).tex(u2, v2).endVertex();
							tessellator.draw();
							continue;
						}else{
							renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
							renderer.pos(x - w + 1, y-0.001, z).tex(u2, v1).endVertex();
							renderer.pos(    x + 1, y-0.001, z).tex(u1, v1).endVertex();
							renderer.pos(    x + 1, y-0.001, z + h).tex(u1, v2).endVertex();
							renderer.pos(x - w + 1, y-0.001, z + h).tex(u2, v2).endVertex();
							tessellator.draw();
							continue;
						}
					}else{
						if(isPositive){
							renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
							renderer.pos(x - w + 1, y, z+1.001).tex(u2, v1).endVertex();
							renderer.pos(x + 1, y, z+1.001).tex(u1, v1).endVertex();
							renderer.pos(x + 1, y + h, z+1.001).tex(u1, v2).endVertex();
							renderer.pos(x - w + 1, y + h, z+1.001).tex(u2, v2).endVertex();
							tessellator.draw();
							continue;
						}
					}
					renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
					renderer.pos(x + w,     y, z-0.001).tex(u2, v1).endVertex();
					renderer.pos(    x,     y, z-0.001).tex(u1, v1).endVertex();
					renderer.pos(    x, y + h, z-0.001).tex(u1, v2).endVertex();
					renderer.pos(x + w, y + h, z-0.001).tex(u2, v2).endVertex();
					tessellator.draw();
				}
			}
		}catch(Exception e){
			//e.printStackTrace();
		}
		//String text = I18n.format("tomsmod.render.energy") + ": "+te.getMaxEnergyStored(null, EnergyType.LASER)+"/"+MathHelper.floor_double(te.energyStoredClient);
		//float scale = 0.005F;
		//int textLenth = this.getFontRenderer().getStringWidth(text);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();//side
		if(te.getStoredPer() > 0){
			GlStateManager.pushMatrix();//energy
			GlStateManager.disableTexture2D();
			//GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GL11.glEnable(GL_DEPTH_CLAMP);
			float red = ((te.color >> 16) & 0xff) / 255.0f;
			float green = ((te.color >> 8) & 0xff) / 255.0f;
			float blue = ((te.color) & 0xff) / 255.0f;
			float alpha = new Float(te.getStoredPer() * 0.8D);
			double size = 0.9D;
			renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_COLOR );
			renderer.pos(x+0.9D, y+0.1, z + size).color(red, green, blue, alpha).endVertex();
			renderer.pos(x+0.9D, y+0.1, z+0.1).color(red, green, blue, alpha).endVertex();
			renderer.pos(x+0.9D, y + size, z+0.1).color(red, green, blue, alpha).endVertex();
			renderer.pos(x+0.9D, y + size, z + size).color(red, green, blue, alpha).endVertex();
			tessellator.draw();

			renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_COLOR );
			renderer.pos(x+0.1D, y+0.1, z - size + 1).color(red, green, blue, alpha).endVertex();
			renderer.pos(x+0.1D, y+0.1, z + 0.9).color(red, green, blue, alpha).endVertex();
			renderer.pos(x+0.1D, y + size, z + 0.9).color(red, green, blue, alpha).endVertex();
			renderer.pos(x+0.1D, y + size, z - size + 1).color(red, green, blue, alpha).endVertex();
			tessellator.draw();

			renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_COLOR );
			renderer.pos(x + size, y + 0.9, z+0.1).color(red, green, blue, alpha).endVertex();
			renderer.pos(x+0.1, y + 0.9, z+0.1).color(red, green, blue, alpha).endVertex();
			renderer.pos(x+0.1, y + 0.9, z + size).color(red, green, blue, alpha).endVertex();
			renderer.pos(x + size, y + 0.9, z + size).color(red, green, blue, alpha).endVertex();
			tessellator.draw();

			renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_COLOR );
			renderer.pos(x - size + 1, y+0.1, z+0.1).color(red, green, blue, alpha).endVertex();
			renderer.pos(x + 0.9, y+0.1, z+0.1).color(red, green, blue, alpha).endVertex();
			renderer.pos(x + 0.9, y+0.1, z + size).color(red, green, blue, alpha).endVertex();
			renderer.pos(x - size + 1, y+0.1, z + size).color(red, green, blue, alpha).endVertex();
			tessellator.draw();

			renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_COLOR );
			renderer.pos(x - size + 1, y+0.1, z+0.9).color(red, green, blue, alpha).endVertex();
			renderer.pos(x + 0.9, y+0.1, z+0.9).color(red, green, blue, alpha).endVertex();
			renderer.pos(x + 0.9, y + size, z+0.9).color(red, green, blue, alpha).endVertex();
			renderer.pos(x - size + 1, y + size, z+0.9).color(red, green, blue, alpha).endVertex();
			tessellator.draw();

			renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_COLOR );
			renderer.pos(x + size,    y+0.1, z+0.1).color(red, green, blue, alpha).endVertex();
			renderer.pos(   x+0.1,    y+0.1, z+0.1).color(red, green, blue, alpha).endVertex();
			renderer.pos(   x+0.1, y + size, z+0.1).color(red, green, blue, alpha).endVertex();
			renderer.pos(x + size, y + size, z+0.1).color(red, green, blue, alpha).endVertex();
			tessellator.draw();

			GlStateManager.enableTexture2D();
			GL11.glDisable(GL_DEPTH_CLAMP);
			GlStateManager.disableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();//energy
		}
		/*GlStateManager.pushMatrix();//energy
		GL11.glTranslated(x+0.5D, y+1.01D, z+0.05D);
		GL11.glScalef(scale,scale,scale);
		Vec3 v = new Vec3(1,1,1);
		Vec3 v3 = v.addVector(0, -2, 2);
		Vec3 vC = v.crossProduct(v3);
		if(te.facing == EnumFacing.EAST){
			//GL11.glRotated(360, vC.xCoord, vC.yCoord, vC.zCoord);
		}else{
			GL11.glRotated(180, 0, -2, 2);
		}
		this.getFontRenderer().drawString(text, -textLenth / 2,0, 0xFFFFFFFF);
		GlStateManager.popMatrix();//energy
		GlStateManager.pushMatrix();//energy2
		GL11.glTranslated(x+0.5D, y+0.99D, z-0.01);
		GL11.glScalef(scale,scale,scale);
		GL11.glRotated(180, 0, 0, 0);
		this.getFontRenderer().drawString(text, -textLenth / 2,0, 0xFFFFFFFF);
		GlStateManager.popMatrix();//energy2*/
		/*GlStateManager.pushMatrix();
		GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(768, 1);
        bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = Minecraft.getSystemTime() % 3000L / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        this.renderModel(-8372020, x, y, z);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = Minecraft.getSystemTime() % 4873L / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        this.renderModel(-8372020, x, y, z);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();*/
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();//main
	}
	/*private void renderModel(int color, double x, double y, double z) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		//double u1 = 0.0D,v1= 1.0D,u2= 1.0D,v2= 0.0D;
		int w = 10;
		int h = 10;
		renderer.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_COLOR );
		renderer.pos(x - w + 1, y, z).color(((color >> 16) & 0xff) / 255.0f, ((color >> 8) & 0xff) / 255.0f, ((color) & 0xff) / 255.0f, ((color >> 24) & 0xff) / 255.0f).endVertex();
		renderer.pos(x + 1, y, z).color(((color >> 16) & 0xff) / 255.0f, ((color >> 8) & 0xff) / 255.0f, ((color) & 0xff) / 255.0f, ((color >> 24) & 0xff) / 255.0f).endVertex();
		renderer.pos(x + 1, y, z + h).color(((color >> 16) & 0xff) / 255.0f, ((color >> 8) & 0xff) / 255.0f, ((color) & 0xff) / 255.0f, ((color >> 24) & 0xff) / 255.0f).endVertex();
		renderer.pos(x - w + 1, y, z + h).color(((color >> 16) & 0xff) / 255.0f, ((color >> 8) & 0xff) / 255.0f, ((color) & 0xff) / 255.0f, ((color >> 24) & 0xff) / 255.0f).endVertex();
		tessellator.draw();

	}*/
}
