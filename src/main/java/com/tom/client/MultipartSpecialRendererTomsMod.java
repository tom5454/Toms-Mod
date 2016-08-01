package com.tom.client;

import mcmultipart.client.multipart.MultipartSpecialRenderer;
import mcmultipart.multipart.IMultipart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public abstract class MultipartSpecialRendererTomsMod<T extends IMultipart> extends
MultipartSpecialRenderer<T> {
	@Override
	public void renderMultipartAt(T part, double x, double y,
			double z, float partialTicks, int destroyStage) {
		if(part != null && part.getWorld() != null && part.getPos() != null){
			IBlockState state = part.getWorld().getBlockState(part.getPos());
			if(state.getBlock() != Blocks.AIR){
				this.renderMultipartAtI(part, x, y, z, partialTicks, destroyStage);
			}
		}
	}
	public abstract void renderMultipartAtI(T part, double x, double y,
			double z, float partialTicks, int destroyStage);
}
