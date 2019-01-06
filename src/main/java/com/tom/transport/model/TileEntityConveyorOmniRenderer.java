package com.tom.transport.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

import com.tom.client.TileEntitySpecialRendererTomsMod;
import com.tom.transport.tileentity.TileEntityConveyorBase;
import com.tom.transport.tileentity.TileEntityConveyorOmniBase;
import com.tom.util.TomsModUtils;

public class TileEntityConveyorOmniRenderer extends TileEntitySpecialRendererTomsMod<TileEntityConveyorOmniBase> {
	private ModelBelt model = new ModelBelt();
	private static RenderEntityItem RENDER_ITEM;

	@Override
	public void renderTileEntityAt(TileEntityConveyorOmniBase te, double x, double y, double z, float partialTicks, int destroyStage, IBlockState state) {
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
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		bindTexture(te.getTexture());
		EnumFacing pos = te.getFacing();
		EnumFacing facing = te.facing;
		EnumFacing f = facing.rotateAround(pos.getAxis());
		f = f.rotateAround(pos.getAxis());
		if (f.getAxis() == Axis.X)
			f = f.getOpposite();
		switch (pos) {
		case DOWN:
			// GlStateManager.rotate(180, 1, 0, 0);
			GlStateManager.translate(0, 0.35, 0);
			break;
		case EAST:
			GlStateManager.rotate(90, 0, 0, 1);
			GlStateManager.translate(-1, 1.34, 0);
			switch (facing) {
			case DOWN:
				f = EnumFacing.WEST;
				break;
			case EAST:
				break;
			case NORTH:
				f = EnumFacing.SOUTH;
				break;
			case SOUTH:
				f = EnumFacing.NORTH;
				break;
			case UP:
				f = EnumFacing.EAST;
				break;
			case WEST:
				break;
			default:
				break;
			}
			break;
		case NORTH:
			GlStateManager.rotate(90, 1, 0, 0);
			GlStateManager.translate(0, 1.34, 1);
			switch (facing) {
			case DOWN:
				f = EnumFacing.NORTH;
				break;
			case EAST:
				break;
			case NORTH:
				f = EnumFacing.SOUTH;
				break;
			case SOUTH:
				f = EnumFacing.NORTH;
				break;
			case UP:
				f = EnumFacing.SOUTH;
				break;
			case WEST:
				break;
			default:
				break;
			}
			break;
		case SOUTH:
			GlStateManager.rotate(270, 1, 0, 0);
			GlStateManager.translate(0, 1.34, -1);
			switch (facing) {
			case DOWN:
				f = EnumFacing.SOUTH;
				break;
			case EAST:
				break;
			case NORTH:
				f = EnumFacing.SOUTH;
				break;
			case SOUTH:
				f = EnumFacing.NORTH;
				break;
			case UP:
				f = EnumFacing.NORTH;
				break;
			case WEST:
				break;
			default:
				break;
			}
			break;
		case UP:
			GlStateManager.translate(0, -0.02, 0);
			break;
		case WEST:
			GlStateManager.rotate(90, 0, 0, 1);
			GlStateManager.translate(-1, .98, 0);
			switch (facing) {
			case DOWN:
				f = EnumFacing.WEST;
				break;
			case EAST:
				break;
			case NORTH:
				f = EnumFacing.SOUTH;
				break;
			case SOUTH:
				f = EnumFacing.NORTH;
				break;
			case UP:
				f = EnumFacing.EAST;
				break;
			case WEST:
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		TomsModUtils.rotateMatrixByMetadata(f.ordinal());
		GlStateManager.pushMatrix();
		GL11.glScalef(1.0F, -1F, -1F);
		model.render(te.getItemPos(), 0.0625F);
		GlStateManager.popMatrix();
		renderExtra(te, partialTicks, destroyStage, state);
		ItemStack stack = te.getStack();
		if (!stack.isEmpty()) {
			float itemRot = 0.5F;
			EntityItem entityItem = new EntityItem(te.getWorld(), te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), stack);
			entityItem.hoverStart = 0;
			double item = 0;
			double off = 1;
			double off2 = -.5;
			switch (facing) {
			case DOWN:
				item = 1 - (te.getItemPos() / (float) TileEntityConveyorBase.MAX_POS);
				break;
			case EAST:
				item = 1 - (te.getItemPos() / (float) TileEntityConveyorBase.MAX_POS);
				// off = -1.5;
				break;
			case NORTH:
				item = 1 - (te.getItemPos() / (float) TileEntityConveyorBase.MAX_POS);
				break;
			case SOUTH:
				item = 1 - (te.getItemPos() / (float) TileEntityConveyorBase.MAX_POS);
				break;
			case UP:
				item = 1 - (te.getItemPos() / (float) TileEntityConveyorBase.MAX_POS);
				break;
			case WEST:
				item = 1 - (te.getItemPos() / (float) TileEntityConveyorBase.MAX_POS);
				// off = -1.5;
				break;
			default:
				break;
			}
			switch (pos) {
			case DOWN:
				GlStateManager.rotate(180, 1, 0, 0);
				GlStateManager.translate(0, 2.3, 0);
				off = -1;
				off2 = .5;
				break;
			case EAST:
				GlStateManager.rotate(180, 1, 0, 0);
				GlStateManager.translate(0, 2.3, 0);
				off = -1;
				off2 = .5;
				break;
			case NORTH:
				GlStateManager.rotate(180, 1, 0, 0);
				GlStateManager.translate(0, 2.3, 0);
				off = -1;
				off2 = .5;
				break;
			case SOUTH:
				GlStateManager.rotate(180, 1, 0, 0);
				GlStateManager.translate(0, 2.3, 0);
				off = -1;
				off2 = .5;
				break;
			case UP:
				break;
			case WEST:
				break;
			default:
				break;
			}
			RENDER_ITEM.doRender(entityItem, 0, -1.5 + 4F / 16F, item * off + off2, itemRot, 0.0F);
		}
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
	public void renderExtra(TileEntityConveyorOmniBase te, float partialTicks, int destroyStage, IBlockState state){

	}
}
