package com.tom.transport.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import com.tom.client.TileEntitySpecialRendererTomsMod;
import com.tom.transport.tileentity.TileEntityConveyorBase;
import com.tom.util.TomsModUtils;

public class TileEntityConveyorRenderer extends TileEntitySpecialRendererTomsMod<TileEntityConveyorBase> {
	private ModelBelt model = new ModelBelt();
	private static RenderEntityItem RENDER_ITEM;

	@Override
	public void renderTileEntityAt(TileEntityConveyorBase te, double x, double y, double z, float partialTicks, int destroyStage, IBlockState state) {
		if (RENDER_ITEM == null)
			RENDER_ITEM = new RenderEntityItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()) {
				@Override
				public boolean shouldBob() {
					return false;
				}

				@Override
				public boolean shouldSpreadItems() {
					return false;
				}
			};
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableLighting();
		GlStateManager.pushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.48F, (float) z + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		bindTexture(te.getTexture());
		EnumFacing facing = te.getFacing();
		TomsModUtils.rotateMatrixByMetadata(facing.getOpposite().ordinal() % 6);
		model.render(te.getItemPos(), 0.0625F);
		GlStateManager.popMatrix();
		ItemStack stack = te.getStack();
		if (!stack.isEmpty()) {
			float itemRot = 0.5F;
			EntityItem entityItem = new EntityItem(te.getWorld(), te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), stack);
			entityItem.hoverStart = 0;
			double itemX = 0;
			double itemZ = 0;
			switch (facing) {
			case DOWN:
				break;
			case EAST:
				itemX = te.getItemPos() / (float) TileEntityConveyorBase.MAX_POS;
				itemZ = .5;
				break;
			case NORTH:
				itemZ = 1 - (te.getItemPos() / (float) TileEntityConveyorBase.MAX_POS);
				itemX = .5;
				break;
			case SOUTH:
				itemZ = te.getItemPos() / (float) TileEntityConveyorBase.MAX_POS;
				itemX = .5;
				break;
			case UP:
				break;
			case WEST:
				itemX = 1 - (te.getItemPos() / (float) TileEntityConveyorBase.MAX_POS);
				itemZ = .5;
				break;
			default:
				break;
			}
			RENDER_ITEM.doRender(entityItem, itemX + x, y + 4F / 16F - .02, itemZ + z, itemRot, 0.0F);
		}
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

}
