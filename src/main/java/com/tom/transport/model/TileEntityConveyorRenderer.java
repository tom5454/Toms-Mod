package com.tom.transport.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;

import com.tom.client.TileEntitySpecialRendererTomsMod;
import com.tom.transport.tileentity.TileEntityConveyor;

public class TileEntityConveyorRenderer extends
		TileEntitySpecialRendererTomsMod<TileEntityConveyor> {
	//private static final ResourceLocation GRAY_RESOURCE_LOCATION = new ResourceLocation("tomsmodcore:textures/blocks/GrayD.png");
	//private static final ResourceLocation BELT_RESOURCE_LOCATION = new ResourceLocation("tomsmodtransport:textures/models/conveyor.png");
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
	public void renderTileEntityAt(TileEntityConveyor te, double x, double y,
		double z, float partialTicks, int destroyStage, IBlockState state) {
		GL11.glPushMatrix();//main
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
		//GL11.glScalef(1.0F, -1F, -1F);
		EnumFacing stateFacing = te.getPosition(state);
		//EnumFacing facing = stateFacing.rotateAround(Axis.X);
		/*GL11.glPushMatrix();//belt
		if(facing == EnumFacing.UP){
			GL11.glTranslated(0,0,-0.125D);
		}
		TomsModUtils.rotateMatrixByMetadata(facing.ordinal() % 6);
		if(facing == EnumFacing.NORTH) GL11.glTranslatef(1, -2.15F, 0.5F);//*/
		//Tessellator tes = Tessellator.getInstance();
		//this.bindTexture(GRAY_RESOURCE_LOCATION);
		/*GL11.glPushMatrix();//belt2
		EnumFacing f = EnumFacing.NORTH;
		/*if(stateFacing.getAxis() != Axis.Y){
			EnumFacing fL = facing;
			int i = 0;
			try{
				for(EnumFacing fac = EnumFacing.UP;fac != EnumFacing.UP && fL != facing;fac = fac.rotateAround(facing.getAxis())){
					fL = fac;
					if(te.facing == fac){
						break;
					}
					i++;
				}
				f = EnumFacing.VALUES[i+2];
			}catch(Exception e){}
		}else*/
		
		//int rotAngle = f.getHorizontalIndex()*90;
		//te.beltModel.rotate(rotAngle);
		/*GL11.glPushMatrix();//belt3
		if(f == EnumFacing.UP){
			GL11.glTranslated(0,0,-0.125D);
		}
		TomsModUtils.rotateMatrixByMetadata(f.ordinal() % 6);
		if(f == EnumFacing.NORTH) GL11.glTranslatef(1, -2.15F, 0.5F);
		//EnumFacing f2 = f.getOpposite();
		//GL11.glRotated(rotAngle, stateFacing.getAxis() == Axis.Z ? 1 : 0, stateFacing.getAxis() == Axis.Y ? 1 : 0, stateFacing.getAxis() == Axis.X ? 1 : 0);
		//float itemX = stateFacing.getAxis() == Axis.X ? rotAngle : f.getAxis() == Axis.X ? f.getAxisDirection().getOffset() * rotAngle : 0;
		//float itemY = stateFacing.getAxis() == Axis.Y ? rotAngle : f.getAxis() == Axis.Y ? f.getAxisDirection().getOffset() * rotAngle : 0;
		//float itemZ = stateFacing.getAxis() == Axis.Z ? rotAngle : f.getAxis() == Axis.Z ? f.getAxisDirection().getOffset() * rotAngle : 0;
		//te.beltModel.rotate((stateFacing.getAxis() == Axis.Y ? 1*stateFacing.getAxisDirection().getOffset() : 0)*rotAngle, (stateFacing.getAxis() == Axis.Z ? 1*stateFacing.getAxisDirection().getOffset() : 0)*rotAngle, (stateFacing.getAxis() == Axis.X ? 1*stateFacing.getAxisDirection().getOffset() : 0)*rotAngle);
		//te.beltModel.rotate(itemX, itemY, itemZ);
		//this.bindTexture(BELT_RESOURCE_LOCATION);
		//te.beltModel.render(0.0625F);
		GL11.glPopMatrix();//belt3
		GL11.glPopMatrix();//belt2
		GL11.glPopMatrix();//belt*/
		EnumFacing f = te.facing;
		ItemStack s = te.getStack();
		float itemRot = 0.5F;
		if(s != null && s.getItem() != null){
			//if(te.partialTicksLast != partialTicks) te.posLast = te.posLast + te.getMovementSpeed() * partialTicks;
			//te.partialTicksLast = partialTicks;
			EntityItem entityItem = new EntityItem(te.getWorld(), te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), s);
			entityItem.hoverStart = 0;
			te.posLast = new Float(te.position);
			if(stateFacing.getAxis() != Axis.Y){
				if(te.facing.getAxis() != Axis.Y){
					double itemX = te.facing.getAxis() == Axis.X ? te.posLast / 16 * te.facing.getAxisDirection().getOffset() - f.getAxisDirection().getOffset() * 0.5D : 0;
					double itemZ = te.facing.getAxis() == Axis.Z ? te.posLast / 16 * te.facing.getAxisDirection().getOffset() - f.getAxisDirection().getOffset() * 0.5D : 0;
					RENDER_ITEM.doRender(entityItem, itemX, -1.2, itemZ, itemRot, 0.0F);
					//System.out.println(te.facing);
				}else{
					double h = te.facing.getAxisDirection() == AxisDirection.POSITIVE ? 1.7 : 0.8;
					//System.out.println(h + " " + te.facing);
					double itemX = stateFacing.getAxis() == Axis.X ? -0.055 * stateFacing.getAxisDirection().getOffset() : 0;
					double itemZ = stateFacing.getAxis() == Axis.Z ? 0.055 * stateFacing.getAxisDirection().getOffset() : 0;
					RENDER_ITEM.doRender(entityItem, itemX, ((te.posLast / 16)*te.facing.getAxisDirection().getOffset()) - h, itemZ, itemRot, 0.0F);
					//RENDER_ITEM.doRender(entityItem, 0, (-(te.posLast / 16)*te.facing.getAxisDirection().getOffset()) + h, 0, 0.0f, 0.0F);
				}
			}else{
				double h = stateFacing.getAxisDirection() == AxisDirection.POSITIVE ? 1.3 : 1.2;
				//double itemX = stateFacing.getAxis() == Axis.X ? h : f.getAxisDirection().getOffset() * f.getFrontOffsetX() * te.posLast / 16 - f.getAxisDirection().getOffset() * 0.5D;
				//double itemY = stateFacing.getAxis() == Axis.Y ? h : f.getAxisDirection().getOffset() * f.getFrontOffsetY() * te.posLast / 16 - f.getAxisDirection().getOffset() * 0.5D;
				//double itemZ = stateFacing.getAxis() == Axis.Z ? h : f.getAxisDirection().getOffset() * f.getFrontOffsetZ() * te.posLast / 16 - f.getAxisDirection().getOffset() * 0.5D;
				RENDER_ITEM.doRender(entityItem, (f.getAxis() == Axis.X ? f.getAxisDirection().getOffset() * te.posLast / 16 - f.getAxisDirection().getOffset() * 0.5D : 0), -h, (f.getAxis() == Axis.Z ? f.getAxisDirection().getOffset() * te.posLast / 16 - f.getAxisDirection().getOffset() * 0.5D : 0), itemRot, 0.0F);
			}
			
			//te.beltModel.setTexOffset(te.posLast);
			
			
			//if(stateFacing.getAxis() != Axis.Y)System.out.println(f);
			//RENDER_ITEM.doRender(entityItem, te.posLast / 16,0.8,0, 0.0f, 0.0F);
		}else{
			//te.beltModel.setTexOffset(0);
			te.posLast = 0;
		}
		/*EnumFacing f = facing;
		try{
			f = te.facing.rotateAround(facing.getAxis());
			//System.out.println(f);
		}catch(Exception e){
			
		}
		int angle = 90*f.getHorizontalIndex()+90;
		GL11.glRotated(angle, 0, 0, 1);
		if(angle != 360){
			GL11.glTranslated(0, -1, 0);
		}
		WorldRenderer t = tes.getWorldRenderer();
		t.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
		double w = 1;
		double h = 1;
		double xP = -0.5D;
		double yP = 0.375D;//0.375D
		double zP = 0.17D;
		double u1 = 1D/16D,v1 = 0.0D,u2 =  1.0D-1D/16D, v2 = 1.0D;
		t.pos(xP + w, yP, zP).tex(u2, v1).endVertex();
		t.pos(xP, yP, zP).tex(u1, v1).endVertex();
		t.pos(xP, yP + h, zP).tex(u1, v2).endVertex();
		t.pos(xP + w, yP + h, zP).tex(u2, v2).endVertex();
		tes.draw();
		/*t.begin(GL11.GL_QUADS , DefaultVertexFormats.POSITION_TEX );
		double yPE = 0.75D;
		t.pos(-xP, yPE-yP + w, zP).tex(u2, v1).endVertex();
		t.pos(-xP, yPE-yP, zP).tex(u1, v1).endVertex();
		t.pos(-xP, yPE-yP, zP + h).tex(u1, v2).endVertex();
		t.pos(-xP, yPE-yP + w, zP + h).tex(u2, v2).endVertex();
		tes.draw();*/
		GL11.glPopMatrix();//main
	}
}
