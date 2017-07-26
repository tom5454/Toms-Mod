package com.tom.transport.tileentity;

import net.minecraft.util.ResourceLocation;

public class TileEntityConveyorOmniFast extends TileEntityConveyorOmniBase {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tomsmodtransport:textures/models/conveyormodel.png");

	@Override
	public int getSpeed() {
		return 4;
	}

	@Override
	public ResourceLocation getTexture() {
		return TEXTURE;
	}

}
