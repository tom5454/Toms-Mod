package com.tom.model;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public interface IBaseModel {
	public void renderStatic(float size, TileEntity te);

	public void renderDynamic(float size, TileEntity te, float partialTicks);

	public ResourceLocation getModelTexture(TileEntity tile);

	public boolean rotateModelBasedOnBlockMeta();
}
