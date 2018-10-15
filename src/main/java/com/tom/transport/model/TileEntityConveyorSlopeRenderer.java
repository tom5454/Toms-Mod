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
import net.minecraft.util.EnumFacing.AxisDirection;

import com.tom.client.TileEntitySpecialRendererTomsMod;
import com.tom.transport.block.ConveyorBeltSlope;
import com.tom.transport.tileentity.TileEntityConveyorBeltSlope;
import com.tom.util.TomsModUtils;

public class TileEntityConveyorSlopeRenderer extends TileEntitySpecialRendererTomsMod<TileEntityConveyorBeltSlope> {
	private ModelBelt model = new ModelBelt();
	private static RenderEntityItem RENDER_ITEM;

	@Override
	public void renderTileEntityAt(TileEntityConveyorBeltSlope te, double x, double y, double z, float partialTicks, int destroyStage, IBlockState state) {
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
		boolean down = state.getValue(ConveyorBeltSlope.IS_DOWN_SLOPE);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableLighting();
		GlStateManager.pushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.48F, (float) z + 0.5F);
		EnumFacing facing = down ? te.getFacing() : te.getFacing().getOpposite();
		if (down && facing.getAxis() == Axis.X)
			facing = facing.getOpposite();
		if (!down && facing.getAxis() == Axis.Z)
			facing = facing.getOpposite();
		TomsModUtils.rotateMatrixByMetadata(facing.getOpposite().ordinal() % 6);
		if (down) {
			GlStateManager.translate(0, -1.41, .1);
			GL11.glScalef(1.0F, 2, 1.05F);
		} else {
			GL11.glScalef(1.0F, -2F, -1.05F);
		}
		bindTexture(te.getTexture());
		GlStateManager.rotate(27, 1, 0, 0);
		GlStateManager.translate(0, -0.86, -.2);
		GL11.glScaled(1, 1, 1.2);
		model.render(te.getItemPos(), 0.0625F);
		GlStateManager.popMatrix();
		ItemStack stack = te.getStack();
		if (!stack.isEmpty()) {
			// float itemRot = 0.5F;
			EntityItem entityItem = new EntityItem(te.getWorld(), te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), stack);
			entityItem.hoverStart = 0;
			/*double itemX = 0;
			double itemZ = 0;
			int rx = 0, rz = 0;
			switch(facing){
			case DOWN:
				break;
			case EAST:
				itemX = te.getItemPos() / (float)TileEntityConveyorBase.MAX_POS;
				if(down) itemX = 1-itemX;
				itemZ = .5;
				rz = 1;
				break;
			case NORTH:
				itemZ = 1-(te.getItemPos() / (float)TileEntityConveyorBase.MAX_POS);
				if(down) itemZ = 1-itemZ;
				itemX = .5;
				rx = 1;
				break;
			case SOUTH:
				itemZ = te.getItemPos() / (float)TileEntityConveyorBase.MAX_POS;
				if(down) itemZ = 1-itemZ;
				itemX = .5;
				rx = -1;
				break;
			case UP:
				break;
			case WEST:
				itemX = 1-(te.getItemPos() / (float)TileEntityConveyorBase.MAX_POS);
				if(down) itemX = 1-itemX;
				itemZ = .5;
				rz = -1;
				break;
			default:
				break;
			}
			GlStateManager.translate(x, y+.9, z);
			TomsModUtils.rotateMatrixByMetadata(facing.getOpposite().ordinal() % 6);
			GlStateManager.rotate(45, rx, 0, rz);
			/*if(down)GlStateManager.rotate(45, 1, 0, 0);
			else{
				GlStateManager.translate(0, -.7, 0);
				GlStateManager.rotate(-45, 1, 0, 0);
			}*/
			// RENDER_ITEM.doRender(entityItem, itemX, 4F/16F-.02, itemZ,
			// itemRot, 0.0F);
			GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
			double offset = down ? 0.4 : 0.6;
			double offset3 = facing.getAxisDirection() == AxisDirection.POSITIVE ? -.13 : 0;
			double offsetX = facing.getAxis() == Axis.X ? offset3 : 0;
			double offsetZ = facing.getAxis() == Axis.Z ? offset3 : 0;
			int r = facing.getAxisDirection() == AxisDirection.POSITIVE ? -45 : 45;
			if (down) {
				double posY = -te.getItemPos() / (float) TileEntityConveyorBeltSlope.MAX_POS + offset;
				double posX = facing.getAxis() == Axis.X ? posY * -facing.getAxisDirection().getOffset() : 0;
				double posZ = facing.getAxis() == Axis.Z ? posY * facing.getAxisDirection().getOffset() : 0;
				GlStateManager.translate(posX, posY - 0.6, posZ);
				GlStateManager.rotate(r, facing.getAxis() == Axis.Z ? 1 : 0, 0, facing.getAxis() == Axis.X ? 1 : 0);
				RENDER_ITEM.doRender(entityItem, offsetX, -.1, offsetZ, 0.0F, 0.0F);
			} else {
				double posY = te.getItemPos() / (float) TileEntityConveyorBeltSlope.MAX_POS + offset;
				double posX = facing.getAxis() == Axis.X ? (-posY + 1.3) * facing.getAxisDirection().getOffset() : 0;
				double posZ = facing.getAxis() == Axis.Z ? (-posY + 1.3) * -facing.getAxisDirection().getOffset() : 0;
				GlStateManager.translate(posX, posY - 1.9, posZ);
				GlStateManager.rotate(r, facing.getAxis() == Axis.Z ? 1 : 0, 0, facing.getAxis() == Axis.X ? 1 : 0);
				RENDER_ITEM.doRender(entityItem, offsetX, -.1, offsetZ, 0.0F, 0.0F);
			}
		}
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

}
