package com.tom.factory.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import com.tom.factory.tileentity.TileEntityPlasticProcessor;
import com.tom.lib.Configs;
import com.tom.model.IBaseModel;

/**
 * NewProject - Undefined Created using Tabula 5.1.0
 */
public class ModelPlasticProcessorRotor extends ModelBase implements IBaseModel {
	public ModelRenderer Bottom;
	public ModelRenderer top;
	public ModelRenderer Bottom2;
	public ModelRenderer top2;

	public ModelPlasticProcessorRotor() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.Bottom = new ModelRenderer(this, 0, 0);
		this.Bottom.setRotationPoint(-6.0F, 19.0F, -1.0F);
		this.Bottom.addBox(0.0F, 0.0F, 0.0F, 12, 2, 2, 0.0F);
		this.top = new ModelRenderer(this, 0, 4);
		this.top.setRotationPoint(-1.0F, 9.0F, -1.0F);
		this.top.addBox(0.0F, 0.0F, 0.0F, 2, 7, 2, 0.0F);
		this.top2 = new ModelRenderer(this, 16, 4);
		this.top2.setRotationPoint(-1.0F, 18.0F, -1.0F);
		this.top2.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
		this.Bottom2 = new ModelRenderer(this, 0, 5);
		this.Bottom2.setRotationPoint(-1.0F, 16.0F, -6.0F);
		this.Bottom2.addBox(0.0F, 0.0F, 0.0F, 2, 2, 12, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.Bottom.render(f5);
		this.top.render(f5);
		this.top2.render(f5);
		this.Bottom2.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void renderStatic(float size, TileEntity te) {
	}

	@Override
	public void renderDynamic(float size, TileEntity te, float partialTicks) {
		GlStateManager.pushMatrix();
		float f = 0.7f;
		GlStateManager.scale(f, f, f);
		GlStateManager.translate(0, .6, 0);
		TileEntityPlasticProcessor tile = (TileEntityPlasticProcessor) te;
		if (tile.isActive()) {
			GlStateManager.rotate((((tile.getWorld().getTotalWorldTime() % 15) / 16f) / 2f + partialTicks / 16f) * 360, 0, 1, 0);
		}
		this.Bottom.render(size);
		this.top.render(size);
		this.top2.render(size);
		this.Bottom2.render(size);
		GlStateManager.popMatrix();
	}

	@Override
	public ResourceLocation getModelTexture(TileEntity tile) {
		return Configs.plasticProcessor;
	}

	@Override
	public boolean rotateModelBasedOnBlockMeta() {
		return false;
	}
}
