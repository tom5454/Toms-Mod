package com.tom.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;

import com.tom.api.client.MultiblockRenderer;

import com.tom.core.tileentity.TileEntityHidden;

public class TileEntityHiddenSpecialRenderer extends TileEntitySpecialRendererTomsMod<TileEntityHidden> {

	@Override
	public void renderTileEntityAt(TileEntityHidden te, double x, double y, double z, float partialTicks, int destroyStage, IBlockState state) {
		TileEntity master = te.getMaster();
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);
		if (master != null && te.blockProperties != null && te.blockProperties.tesrID != -1)
			MultiblockRenderer.render(te.getWorld().getBlockState(te.master), te.getPos(), master, te.blockProperties.tesrID, x, y, z, partialTicks);
		GlStateManager.popMatrix();
	}

}