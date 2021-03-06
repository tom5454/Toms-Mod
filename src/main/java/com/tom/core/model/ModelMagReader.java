// Date: 2015.12.05. 19:55:28
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX
package com.tom.core.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import com.tom.lib.Configs;
import com.tom.model.IBaseModel;

import com.tom.core.tileentity.TileEntityMagCardReader;

public class ModelMagReader extends ModelBase implements IBaseModel {
	// fields
	ModelRenderer Back;
	ModelRenderer Front;
	ModelRenderer Front2;
	ModelRenderer Front3;
	ModelRenderer LED1;
	ModelRenderer LED1On;
	ModelRenderer LED2;
	ModelRenderer LED2On;

	public ModelMagReader() {
		textureWidth = 64;
		textureHeight = 64;

		Back = new ModelRenderer(this, 0, 0);
		Back.addBox(0F, 0F, 0F, 5, 8, 1);
		Back.setRotationPoint(-2.5F, 11F, -8F);
		Back.setTextureSize(64, 64);
		Back.mirror = true;
		setRotation(Back, 0F, 0F, 0F);
		Front = new ModelRenderer(this, 0, 0);
		Front.addBox(0F, 0F, 0F, 2, 8, 1);
		Front.setRotationPoint(-2.5F, 11F, -7.4F);
		Front.setTextureSize(64, 64);
		Front.mirror = true;
		setRotation(Front, 0F, 0F, 0F);
		Front2 = new ModelRenderer(this, 0, 0);
		Front2.addBox(0F, 0F, 0F, 1, 8, 1);
		Front2.setRotationPoint(1.5F, 11F, -7.4F);
		Front2.setTextureSize(64, 64);
		Front2.mirror = true;
		setRotation(Front2, 0F, 0F, 0F);
		Front3 = new ModelRenderer(this, 0, 0);
		Front3.addBox(0F, 0F, 0F, 2, 8, 1);
		Front3.setRotationPoint(0F, 11F, -7.4F);
		Front3.setTextureSize(64, 64);
		Front3.mirror = true;
		setRotation(Front3, 0F, 0F, 0F);
		LED1 = new ModelRenderer(this, 56, 34);
		LED1.addBox(0F, 0F, 0F, 1, 1, 1);
		LED1.setRotationPoint(1F, 11.7F, -7F);
		LED1.setTextureSize(64, 64);
		LED1.mirror = true;
		setRotation(LED1, 0F, 0.0174533F, 0F);
		LED1On = new ModelRenderer(this, 60, 34);
		LED1On.addBox(0F, 0F, 0F, 1, 1, 1);
		LED1On.setRotationPoint(1F, 11.7F, -7F);
		LED1On.setTextureSize(64, 64);
		LED1On.mirror = true;
		setRotation(LED1On, 0F, 0.0174533F, 0F);
		LED2 = new ModelRenderer(this, 44, 6);
		LED2.addBox(0F, 0F, 0F, 1, 1, 1);
		LED2.setRotationPoint(1F, 13.4F, -7F);
		LED2.setTextureSize(64, 64);
		LED2.mirror = true;
		setRotation(LED2, 0F, 0.0174533F, 0F);
		LED2On = new ModelRenderer(this, 44, 4);
		LED2On.addBox(0F, 0F, 0F, 1, 1, 1);
		LED2On.setRotationPoint(1F, 13.4F, -7F);
		LED2On.setTextureSize(64, 64);
		LED2On.mirror = true;
		setRotation(LED2On, 0F, 0.0174533F, 0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		Back.render(f5);
		Front.render(f5);
		Front2.render(f5);
		Front3.render(f5);
		LED1.render(f5);
		LED1On.render(f5);
		LED2.render(f5);
		LED2On.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity ent) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, ent);
	}

	@Override
	public ResourceLocation getModelTexture(TileEntity tile) {
		return Configs.contBoxOff;
	}

	@Override
	public boolean rotateModelBasedOnBlockMeta() {
		return true;
	}

	@Override
	public void renderStatic(float size, TileEntity te) {
		GL11.glPushMatrix();
		Back.render(size);
		Front.render(size);
		Front2.render(size);
		Front3.render(size);
		TileEntityMagCardReader t = (TileEntityMagCardReader) te;
		if (t.ledR) {
			LED1On.render(size);
		} else {
			LED1.render(size);
		}
		if (t.ledG) {
			LED2On.render(size);
		} else {
			LED2.render(size);
		}
		GL11.glPopMatrix();
	}

	@Override
	public void renderDynamic(float size, TileEntity te, float partialTicks) {

	}
}
