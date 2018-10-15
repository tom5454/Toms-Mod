package com.tom.defense.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;

import com.tom.client.TileEntitySpecialRendererTomsMod;
import com.tom.defense.tileentity.TileEntityForceCapacitor;
import com.tom.util.TomsModUtils;

public class TileEntityForceCapacitorRenderer extends TileEntitySpecialRendererTomsMod<TileEntityForceCapacitor> {

	@Override
	public void renderTileEntityAt(TileEntityForceCapacitor te, double x, double y, double z, float partialTicks, int destroyStage, IBlockState state) {
		String header = I18n.format("tile.tm.forceCapacitor.name");
		String capacity = I18n.format("tomsmod.render.capacity") + ":";
		String range = I18n.format("tomsmod.render.range") + ":";
		String linkedDevices = I18n.format("tomsmod.render.linkedDevices") + ":";
		String capValue = te.clientPer / 10D + "%";
		String rangeValue = "" + te.range;
		String linkedValue = "" + te.linkedDevices;
		GL11.glPushMatrix(); // start
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		TomsModUtils.rotateMatrixByMetadata(te.getFacing(state).ordinal());
		GL11.glTranslatef(0, 1, 0.5F - 1 - 0.01F);
		float f = 0.0075F;
		int i = MathHelper.floor((0.5 - (1.2 / 16D)) / f);
		GL11.glScalef(f, f, f);
		GlStateManager.translate(0.0F, 0.5F * f, 0.07F * f);
		getFontRenderer().drawString(header, -getFontRenderer().getStringWidth(header) / 2, -i, 0);
		getFontRenderer().drawString(capacity, -i, -i + 80, 0);
		getFontRenderer().drawString(range, -i, -i + 90, 0);
		getFontRenderer().drawString(linkedDevices, -i, -i + 100, 0);
		getFontRenderer().drawString(capValue, -i + 110 - getFontRenderer().getStringWidth(capValue), -i + 80, 0);
		getFontRenderer().drawString(rangeValue, -i + 110 - getFontRenderer().getStringWidth(rangeValue), -i + 90, 0);
		getFontRenderer().drawString(linkedValue, -i + 110 - getFontRenderer().getStringWidth(linkedValue), -i + 100, 0);
		GL11.glPopMatrix(); // end
	}
}
