package com.tom.transport.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

import com.tom.client.TileEntitySpecialRendererTomsMod;
import com.tom.transport.tileentity.TileEntityConveyorSlope;

public class TileEntityConveyorSlopeRenderer extends
		TileEntitySpecialRendererTomsMod<TileEntityConveyorSlope> {
	private static final RenderEntityItem RENDER_ITEM = new RenderEntityItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()) {
		@Override
		public boolean shouldBob() {
			return false;
		}

		@Override
		public boolean shouldSpreadItems() {
			return false;
		}
	};
	@Override
	public void renderTileEntityAt(TileEntityConveyorSlope te, double x,
			double y, double z, float partialTicks, int destroyStage,
			IBlockState state) {
		GL11.glPushMatrix();//main
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
		//GL11.glScalef(1.0F, -1F, -1F);
		ItemStack s = te.getStack();
		if(s != null && s.getItem() != null){
			EnumFacing facing = te.getFacing(state);
			EntityItem entityItem = new EntityItem(te.getWorld(), te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), s);
			entityItem.hoverStart = 0;
			te.posLast = new Float(te.position);
			//float itemRot = 0;
			//if(te.posLast < 15)itemRot = 45;
			double offset = te.isDownSlope(state) ? 0.3 : 0.6;
			if(te.isDownSlope(state)){
				double posY = -te.posLast/16 + offset;
				double posX = facing.getAxis() == Axis.X ? posY * facing.getAxisDirection().getOffset() : 0;
				double posZ = facing.getAxis() == Axis.Z ? posY * facing.getAxisDirection().getOffset() : 0;
				RENDER_ITEM.doRender(entityItem, posX, posY-0.6, posZ, 0.0F, 0.0F);
			}else{
				double posY = te.posLast/16 + offset;
				double posX = facing.getAxis() == Axis.X ? (-posY+1.3) * -facing.getAxisDirection().getOffset() : 0;
				double posZ = facing.getAxis() == Axis.Z ? (-posY+1.3) * -facing.getAxisDirection().getOffset() : 0;
				RENDER_ITEM.doRender(entityItem, posX, posY-1.9, posZ, 0.0F, 0.0F);
			}
		}else{
			te.posLast = 0;
		}
		GL11.glPopMatrix();//main
	}

}
