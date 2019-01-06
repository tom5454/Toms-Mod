package com.tom.transport.tileentity;

import net.minecraft.util.ResourceLocation;

public class TileEntityConveyorFast extends TileEntityConveyorBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tomsmodtransport:textures/models/conveyormodel.png");

	@Override
	public int getSpeed() {
		return 4;
	}

	@Override
	public ResourceLocation getTexture() {
		return TEXTURE;
	}

	@Override
	public int getPowerUse() {
		return 2;
	}

}
