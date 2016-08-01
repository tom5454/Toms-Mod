package com.tom.transport.model;

import com.tom.client.MultipartSpecialRendererTomsMod;
import com.tom.transport.multipart.PartItemDuct;

public class ItemDuctCustomRenderer extends
MultipartSpecialRendererTomsMod<PartItemDuct> {
	//	private static final RenderEntityItem RENDER_ITEM = new RenderEntityItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()) {
	//		@Override
	//		public boolean shouldBob() {
	//			return false;
	//		}
	//
	//		@Override
	//		public boolean shouldSpreadItems() {
	//			return false;
	//		}
	//	};
	@Override
	public void renderMultipartAtI(PartItemDuct part, double x, double y,
			double z, float partialTicks, int destroyStage) {
		//System.out.println(partialTicks);
		/*for(TransferingItemStack s : part.getTransferingItemStacks()){
			if(s.getStack() != null){
				GL11.glPushMatrix();
				float pos = s.getPosition() + part.getMovementSpeed();
				EnumFacing dir;
				if(pos > 8){
					dir = s.getDirection();
				}else{
					dir = s.getDirection().getOpposite();
				}
				EntityItem entityItem = new EntityItem(part.getWorld(), part.getPos().getX(), part.getPos().getY(), part.getPos().getZ(), s.getStack());
				double xOffset = dir.getFrontOffsetX() * 0.5 + (dir.getAxis() == Axis.X ? pos/16 : 0),
						yOffset = dir.getFrontOffsetY() * 0.5 + (dir.getAxis() == Axis.Y ? pos/16 : 0),
						zOffset = dir.getFrontOffsetZ() * 0.5 + (dir.getAxis() == Axis.Z ? pos/16 : 0);
				entityItem.hoverStart = 0;
				GL11.glTranslated(x+0.5+xOffset, y+0.3+yOffset, z+0.5+zOffset);
				float f = 0.8f;
				GL11.glScalef(f, f, f);
				RENDER_ITEM.doRender(entityItem, 0, 0, 0, 0.0f, 0);
				GL11.glPopMatrix();
			}
		}*/
	}

}
