package com.tom.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntitySpecialRendererTomsMod<T extends TileEntity>
extends TileEntitySpecialRenderer<T> {
	protected Minecraft mc;
	@Override
	public void renderTileEntityAt(T te, double x, double y, double z,
			float partialTicks, int destroyStage) {
		mc = Minecraft.getMinecraft();
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		if(state.getBlock() != Blocks.AIR){
			this.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage, state);
		}
	}
	public abstract void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage, IBlockState state);

}
