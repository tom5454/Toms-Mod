package com.tom.transport.tileentity;

import net.minecraft.util.ResourceLocation;

public class TileEntityConveyorSlow extends TileEntityConveyorBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tomsmodtransport:textures/models/conveyormodelslow.png");

	@Override
	public int getSpeed() {
		return 1;
	}

	@Override
	public ResourceLocation getTexture() {
		return TEXTURE;
	}

}
