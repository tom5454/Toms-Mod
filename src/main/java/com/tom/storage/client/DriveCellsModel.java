// Date: 2016.04.17. 12:29:35
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX
package com.tom.storage.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import com.tom.lib.Configs;
import com.tom.model.IBaseModel;
import com.tom.storage.tileentity.TileEntityDrive;

public class DriveCellsModel extends ModelBase implements IBaseModel
{
	//fields
	ModelRenderer base1;
	ModelRenderer base2;
	ModelRenderer base3;
	ModelRenderer base4;
	ModelRenderer base5;
	ModelRenderer base6;
	ModelRenderer base7;
	ModelRenderer base8;
	ModelRenderer base9;
	ModelRenderer base10;
	ModelRenderer baseOn1;
	ModelRenderer baseOn2;
	ModelRenderer baseOn3;
	ModelRenderer baseOn4;
	ModelRenderer baseOn5;
	ModelRenderer baseOn6;
	ModelRenderer baseOn7;
	ModelRenderer baseOn8;
	ModelRenderer baseOn9;
	ModelRenderer baseOn10;
	ModelRenderer baseFull1;
	ModelRenderer baseFull2;
	ModelRenderer baseFull3;
	ModelRenderer baseFull4;
	ModelRenderer baseFull5;
	ModelRenderer baseFull6;
	ModelRenderer baseFull7;
	ModelRenderer baseFull8;
	ModelRenderer baseFull9;
	ModelRenderer baseFull10;
	ModelRenderer baseTypesFull1;
	ModelRenderer baseTypesFull2;
	ModelRenderer baseTypesFull3;
	ModelRenderer baseTypesFull4;
	ModelRenderer baseTypesFull5;
	ModelRenderer baseTypesFull6;
	ModelRenderer baseTypesFull7;
	ModelRenderer baseTypesFull8;
	ModelRenderer baseTypesFull9;
	ModelRenderer baseTypesFull10;
	ModelRenderer baseBooting1;
	ModelRenderer baseBooting2;
	ModelRenderer baseBooting3;
	ModelRenderer baseBooting4;
	ModelRenderer baseBooting5;
	ModelRenderer baseBooting6;
	ModelRenderer baseBooting7;
	ModelRenderer baseBooting8;
	ModelRenderer baseBooting9;
	ModelRenderer baseBooting10;
	ModelRenderer[][] rendererArray;

	public DriveCellsModel()
	{
		textureWidth = 32;
		textureHeight = 32;

		base1 = new ModelRenderer(this, 0, 0);
		base1.addBox(0F, 0F, 0F, 5, 2, 1);
		base1.setRotationPoint(-6F, 21F, -8F);
		base1.setTextureSize(32, 32);
		base1.mirror = true;
		setRotation(base1, 0F, 0F, 0F);
		base2 = new ModelRenderer(this, 0, 0);
		base2.addBox(0F, 0F, 0F, 5, 2, 1);
		base2.setRotationPoint(-6F, 18F, -8F);
		base2.setTextureSize(32, 32);
		base2.mirror = true;
		setRotation(base2, 0F, 0F, 0F);
		base3 = new ModelRenderer(this, 0, 0);
		base3.addBox(0F, 0F, 0F, 5, 2, 1);
		base3.setRotationPoint(-6F, 15F, -8F);
		base3.setTextureSize(32, 32);
		base3.mirror = true;
		setRotation(base3, 0F, 0F, 0F);
		base4 = new ModelRenderer(this, 0, 0);
		base4.addBox(0F, 0F, 0F, 5, 2, 1);
		base4.setRotationPoint(-6F, 12F, -8F);
		base4.setTextureSize(32, 32);
		base4.mirror = true;
		setRotation(base4, 0F, 0F, 0F);
		base5 = new ModelRenderer(this, 0, 0);
		base5.addBox(0F, 0F, 0F, 5, 2, 1);
		base5.setRotationPoint(-6F, 9F, -8F);
		base5.setTextureSize(32, 32);
		base5.mirror = true;
		setRotation(base5, 0F, 0F, 0F);
		base6 = new ModelRenderer(this, 0, 0);
		base6.addBox(0F, 0F, 0F, 5, 2, 1);
		base6.setRotationPoint(1F, 21F, -8F);
		base6.setTextureSize(32, 32);
		base6.mirror = true;
		setRotation(base6, 0F, 0F, 0F);
		base7 = new ModelRenderer(this, 0, 0);
		base7.addBox(0F, 0F, 0F, 5, 2, 1);
		base7.setRotationPoint(1F, 18F, -8F);
		base7.setTextureSize(32, 32);
		base7.mirror = true;
		setRotation(base7, 0F, 0F, 0F);
		base8 = new ModelRenderer(this, 0, 0);
		base8.addBox(0F, 0F, 0F, 5, 2, 1);
		base8.setRotationPoint(1F, 15F, -8F);
		base8.setTextureSize(32, 32);
		base8.mirror = true;
		setRotation(base8, 0F, 0F, 0F);
		base9 = new ModelRenderer(this, 0, 0);
		base9.addBox(0F, 0F, 0F, 5, 2, 1);
		base9.setRotationPoint(1F, 12F, -8F);
		base9.setTextureSize(32, 32);
		base9.mirror = true;
		setRotation(base9, 0F, 0F, 0F);
		base10 = new ModelRenderer(this, 0, 0);
		base10.addBox(0F, 0F, 0F, 5, 2, 1);
		base10.setRotationPoint(1F, 9F, -8F);
		base10.setTextureSize(32, 32);
		base10.mirror = true;
		setRotation(base10, 0F, 0F, 0F);
		baseOn1 = new ModelRenderer(this, 0, 6);
		baseOn1.addBox(0F, 0F, 0F, 5, 2, 1);
		baseOn1.setRotationPoint(-6F, 21F, -8F);
		baseOn1.setTextureSize(32, 32);
		baseOn1.mirror = true;
		setRotation(baseOn1, 0F, 0F, 0F);
		baseOn2 = new ModelRenderer(this, 0, 6);
		baseOn2.addBox(0F, 0F, 0F, 5, 2, 1);
		baseOn2.setRotationPoint(-6F, 18F, -8F);
		baseOn2.setTextureSize(32, 32);
		baseOn2.mirror = true;
		setRotation(baseOn2, 0F, 0F, 0F);
		baseOn3 = new ModelRenderer(this, 0, 6);
		baseOn3.addBox(0F, 0F, 0F, 5, 2, 1);
		baseOn3.setRotationPoint(-6F, 15F, -8F);
		baseOn3.setTextureSize(32, 32);
		baseOn3.mirror = true;
		setRotation(baseOn3, 0F, 0F, 0F);
		baseOn4 = new ModelRenderer(this, 0, 6);
		baseOn4.addBox(0F, 0F, 0F, 5, 2, 1);
		baseOn4.setRotationPoint(-6F, 12F, -8F);
		baseOn4.setTextureSize(32, 32);
		baseOn4.mirror = true;
		setRotation(baseOn4, 0F, 0F, 0F);
		baseOn5 = new ModelRenderer(this, 0, 6);
		baseOn5.addBox(0F, 0F, 0F, 5, 2, 1);
		baseOn5.setRotationPoint(-6F, 9F, -8F);
		baseOn5.setTextureSize(32, 32);
		baseOn5.mirror = true;
		setRotation(baseOn5, 0F, 0F, 0F);
		baseOn6 = new ModelRenderer(this, 0, 6);
		baseOn6.addBox(0F, 0F, 0F, 5, 2, 1);
		baseOn6.setRotationPoint(1F, 21F, -8F);
		baseOn6.setTextureSize(32, 32);
		baseOn6.mirror = true;
		setRotation(baseOn6, 0F, 0F, 0F);
		baseOn7 = new ModelRenderer(this, 0, 6);
		baseOn7.addBox(0F, 0F, 0F, 5, 2, 1);
		baseOn7.setRotationPoint(1F, 18F, -8F);
		baseOn7.setTextureSize(32, 32);
		baseOn7.mirror = true;
		setRotation(baseOn7, 0F, 0F, 0F);
		baseOn8 = new ModelRenderer(this, 0, 6);
		baseOn8.addBox(0F, 0F, 0F, 5, 2, 1);
		baseOn8.setRotationPoint(1F, 15F, -8F);
		baseOn8.setTextureSize(32, 32);
		baseOn8.mirror = true;
		setRotation(baseOn8, 0F, 0F, 0F);
		baseOn9 = new ModelRenderer(this, 0, 6);
		baseOn9.addBox(0F, 0F, 0F, 5, 2, 1);
		baseOn9.setRotationPoint(1F, 12F, -8F);
		baseOn9.setTextureSize(32, 32);
		baseOn9.mirror = true;
		setRotation(baseOn9, 0F, 0F, 0F);
		baseOn10 = new ModelRenderer(this, 0, 6);
		baseOn10.addBox(0F, 0F, 0F, 5, 2, 1);
		baseOn10.setRotationPoint(1F, 9F, -8F);
		baseOn10.setTextureSize(32, 32);
		baseOn10.mirror = true;
		setRotation(baseOn10, 0F, 0F, 0F);
		baseFull1 = new ModelRenderer(this, 0, 3);
		baseFull1.addBox(0F, 0F, 0F, 5, 2, 1);
		baseFull1.setRotationPoint(-6F, 21F, -8F);
		baseFull1.setTextureSize(32, 32);
		baseFull1.mirror = true;
		setRotation(baseFull1, 0F, 0F, 0F);
		baseFull2 = new ModelRenderer(this, 0, 3);
		baseFull2.addBox(0F, 0F, 0F, 5, 2, 1);
		baseFull2.setRotationPoint(-6F, 18F, -8F);
		baseFull2.setTextureSize(32, 32);
		baseFull2.mirror = true;
		setRotation(baseFull2, 0F, 0F, 0F);
		baseFull3 = new ModelRenderer(this, 0, 3);
		baseFull3.addBox(0F, 0F, 0F, 5, 2, 1);
		baseFull3.setRotationPoint(-6F, 15F, -8F);
		baseFull3.setTextureSize(32, 32);
		baseFull3.mirror = true;
		setRotation(baseFull3, 0F, 0F, 0F);
		baseFull4 = new ModelRenderer(this, 0, 3);
		baseFull4.addBox(0F, 0F, 0F, 5, 2, 1);
		baseFull4.setRotationPoint(-6F, 12F, -8F);
		baseFull4.setTextureSize(32, 32);
		baseFull4.mirror = true;
		setRotation(baseFull4, 0F, 0F, 0F);
		baseFull5 = new ModelRenderer(this, 0, 3);
		baseFull5.addBox(0F, 0F, 0F, 5, 2, 1);
		baseFull5.setRotationPoint(-6F, 9F, -8F);
		baseFull5.setTextureSize(32, 32);
		baseFull5.mirror = true;
		setRotation(baseFull5, 0F, 0F, 0F);
		baseFull6 = new ModelRenderer(this, 0, 3);
		baseFull6.addBox(0F, 0F, 0F, 5, 2, 1);
		baseFull6.setRotationPoint(1F, 21F, -8F);
		baseFull6.setTextureSize(32, 32);
		baseFull6.mirror = true;
		setRotation(baseFull6, 0F, 0F, 0F);
		baseFull7 = new ModelRenderer(this, 0, 3);
		baseFull7.addBox(0F, 0F, 0F, 5, 2, 1);
		baseFull7.setRotationPoint(1F, 18F, -8F);
		baseFull7.setTextureSize(32, 32);
		baseFull7.mirror = true;
		setRotation(baseFull7, 0F, 0F, 0F);
		baseFull8 = new ModelRenderer(this, 0, 3);
		baseFull8.addBox(0F, 0F, 0F, 5, 2, 1);
		baseFull8.setRotationPoint(1F, 15F, -8F);
		baseFull8.setTextureSize(32, 32);
		baseFull8.mirror = true;
		setRotation(baseFull8, 0F, 0F, 0F);
		baseFull9 = new ModelRenderer(this, 0, 3);
		baseFull9.addBox(0F, 0F, 0F, 5, 2, 1);
		baseFull9.setRotationPoint(1F, 12F, -8F);
		baseFull9.setTextureSize(32, 32);
		baseFull9.mirror = true;
		setRotation(baseFull9, 0F, 0F, 0F);
		baseFull10 = new ModelRenderer(this, 0, 3);
		baseFull10.addBox(0F, 0F, 0F, 5, 2, 1);
		baseFull10.setRotationPoint(1F, 9F, -8F);
		baseFull10.setTextureSize(32, 32);
		baseFull10.mirror = true;
		setRotation(baseFull10, 0F, 0F, 0F);
		baseTypesFull1 = new ModelRenderer(this, 0, 9);
		baseTypesFull1.addBox(0F, 0F, 0F, 5, 2, 1);
		baseTypesFull1.setRotationPoint(-6F, 21F, -8F);
		baseTypesFull1.setTextureSize(32, 32);
		baseTypesFull1.mirror = true;
		setRotation(baseTypesFull1, 0F, 0F, 0F);
		baseTypesFull2 = new ModelRenderer(this, 0, 9);
		baseTypesFull2.addBox(0F, 0F, 0F, 5, 2, 1);
		baseTypesFull2.setRotationPoint(-6F, 18F, -8F);
		baseTypesFull2.setTextureSize(32, 32);
		baseTypesFull2.mirror = true;
		setRotation(baseTypesFull2, 0F, 0F, 0F);
		baseTypesFull3 = new ModelRenderer(this, 0, 9);
		baseTypesFull3.addBox(0F, 0F, 0F, 5, 2, 1);
		baseTypesFull3.setRotationPoint(-6F, 15F, -8F);
		baseTypesFull3.setTextureSize(32, 32);
		baseTypesFull3.mirror = true;
		setRotation(baseTypesFull3, 0F, 0F, 0F);
		baseTypesFull4 = new ModelRenderer(this, 0, 9);
		baseTypesFull4.addBox(0F, 0F, 0F, 5, 2, 1);
		baseTypesFull4.setRotationPoint(-6F, 12F, -8F);
		baseTypesFull4.setTextureSize(32, 32);
		baseTypesFull4.mirror = true;
		setRotation(baseTypesFull4, 0F, 0F, 0F);
		baseTypesFull5 = new ModelRenderer(this, 0, 9);
		baseTypesFull5.addBox(0F, 0F, 0F, 5, 2, 1);
		baseTypesFull5.setRotationPoint(-6F, 9F, -8F);
		baseTypesFull5.setTextureSize(32, 32);
		baseTypesFull5.mirror = true;
		setRotation(baseTypesFull5, 0F, 0F, 0F);
		baseTypesFull6 = new ModelRenderer(this, 0, 9);
		baseTypesFull6.addBox(0F, 0F, 0F, 5, 2, 1);
		baseTypesFull6.setRotationPoint(1F, 21F, -8F);
		baseTypesFull6.setTextureSize(32, 32);
		baseTypesFull6.mirror = true;
		setRotation(baseTypesFull6, 0F, 0F, 0F);
		baseTypesFull7 = new ModelRenderer(this, 0, 9);
		baseTypesFull7.addBox(0F, 0F, 0F, 5, 2, 1);
		baseTypesFull7.setRotationPoint(1F, 18F, -8F);
		baseTypesFull7.setTextureSize(32, 32);
		baseTypesFull7.mirror = true;
		setRotation(baseTypesFull7, 0F, 0F, 0F);
		baseTypesFull8 = new ModelRenderer(this, 0, 9);
		baseTypesFull8.addBox(0F, 0F, 0F, 5, 2, 1);
		baseTypesFull8.setRotationPoint(1F, 15F, -8F);
		baseTypesFull8.setTextureSize(32, 32);
		baseTypesFull8.mirror = true;
		setRotation(baseTypesFull8, 0F, 0F, 0F);
		baseTypesFull9 = new ModelRenderer(this, 0, 9);
		baseTypesFull9.addBox(0F, 0F, 0F, 5, 2, 1);
		baseTypesFull9.setRotationPoint(1F, 12F, -8F);
		baseTypesFull9.setTextureSize(32, 32);
		baseTypesFull9.mirror = true;
		setRotation(baseTypesFull9, 0F, 0F, 0F);
		baseTypesFull10 = new ModelRenderer(this, 0, 9);
		baseTypesFull10.addBox(0F, 0F, 0F, 5, 2, 1);
		baseTypesFull10.setRotationPoint(1F, 9F, -8F);
		baseTypesFull10.setTextureSize(32, 32);
		baseTypesFull10.mirror = true;
		setRotation(baseTypesFull10, 0F, 0F, 0F);
		baseBooting1 = new ModelRenderer(this, 0, 12);
		baseBooting1.addBox(0F, 0F, 0F, 5, 2, 1);
		baseBooting1.setRotationPoint(-6F, 21F, -8F);
		baseBooting1.setTextureSize(32, 32);
		baseBooting1.mirror = true;
		setRotation(baseBooting1, 0F, 0F, 0F);
		baseBooting2 = new ModelRenderer(this, 0, 12);
		baseBooting2.addBox(0F, 0F, 0F, 5, 2, 1);
		baseBooting2.setRotationPoint(-6F, 18F, -8F);
		baseBooting2.setTextureSize(32, 32);
		baseBooting2.mirror = true;
		setRotation(baseBooting2, 0F, 0F, 0F);
		baseBooting3 = new ModelRenderer(this, 0, 12);
		baseBooting3.addBox(0F, 0F, 0F, 5, 2, 1);
		baseBooting3.setRotationPoint(-6F, 15F, -8F);
		baseBooting3.setTextureSize(32, 32);
		baseBooting3.mirror = true;
		setRotation(baseBooting3, 0F, 0F, 0F);
		baseBooting4 = new ModelRenderer(this, 0, 12);
		baseBooting4.addBox(0F, 0F, 0F, 5, 2, 1);
		baseBooting4.setRotationPoint(-6F, 12F, -8F);
		baseBooting4.setTextureSize(32, 32);
		baseBooting4.mirror = true;
		setRotation(baseBooting4, 0F, 0F, 0F);
		baseBooting5 = new ModelRenderer(this, 0, 12);
		baseBooting5.addBox(0F, 0F, 0F, 5, 2, 1);
		baseBooting5.setRotationPoint(-6F, 9F, -8F);
		baseBooting5.setTextureSize(32, 32);
		baseBooting5.mirror = true;
		setRotation(baseBooting5, 0F, 0F, 0F);
		baseBooting6 = new ModelRenderer(this, 0, 12);
		baseBooting6.addBox(0F, 0F, 0F, 5, 2, 1);
		baseBooting6.setRotationPoint(1F, 21F, -8F);
		baseBooting6.setTextureSize(32, 32);
		baseBooting6.mirror = true;
		setRotation(baseBooting6, 0F, 0F, 0F);
		baseBooting7 = new ModelRenderer(this, 0, 12);
		baseBooting7.addBox(0F, 0F, 0F, 5, 2, 1);
		baseBooting7.setRotationPoint(1F, 18F, -8F);
		baseBooting7.setTextureSize(32, 32);
		baseBooting7.mirror = true;
		setRotation(baseBooting7, 0F, 0F, 0F);
		baseBooting8 = new ModelRenderer(this, 0, 12);
		baseBooting8.addBox(0F, 0F, 0F, 5, 2, 1);
		baseBooting8.setRotationPoint(1F, 15F, -8F);
		baseBooting8.setTextureSize(32, 32);
		baseBooting8.mirror = true;
		setRotation(baseBooting8, 0F, 0F, 0F);
		baseBooting9 = new ModelRenderer(this, 0, 12);
		baseBooting9.addBox(0F, 0F, 0F, 5, 2, 1);
		baseBooting9.setRotationPoint(1F, 12F, -8F);
		baseBooting9.setTextureSize(32, 32);
		baseBooting9.mirror = true;
		setRotation(baseBooting9, 0F, 0F, 0F);
		baseBooting10 = new ModelRenderer(this, 0, 12);
		baseBooting10.addBox(0F, 0F, 0F, 5, 2, 1);
		baseBooting10.setRotationPoint(1F, 9F, -8F);
		baseBooting10.setTextureSize(32, 32);
		baseBooting10.mirror = true;
		setRotation(baseBooting10, 0F, 0F, 0F);
		ModelRenderer[] full = new ModelRenderer[]{baseFull5,
				baseFull10,
				baseFull4,
				baseFull9,
				baseFull3,
				baseFull8,
				baseFull2,
				baseFull7,
				baseFull1,
				baseFull6};
		ModelRenderer[] typesFull = new ModelRenderer[]{baseTypesFull5,
				baseTypesFull10,
				baseTypesFull4,
				baseTypesFull9,
				baseTypesFull3,
				baseTypesFull8,
				baseTypesFull2,
				baseTypesFull7,
				baseTypesFull1,
				baseTypesFull6};
		ModelRenderer[] booting = new ModelRenderer[]{baseBooting5,
				baseBooting10,
				baseBooting4,
				baseBooting9,
				baseBooting3,
				baseBooting8,
				baseBooting2,
				baseBooting7,
				baseBooting1,
				baseBooting6};
		ModelRenderer[] green = new ModelRenderer[]{baseOn5,
				baseOn10,
				baseOn4,
				baseOn9,
				baseOn3,
				baseOn8,
				baseOn2,
				baseOn7,
				baseOn1,
				baseOn6};
		/*for(int i = 0;i<10;i++){
    	  full[i].setTextureOffset(0, 3);
    	  typesFull[i].setTextureOffset(0, 9);
    	  booting[i].setTextureOffset(0, 12);
    	  green[i].setTextureOffset(0, 6);
      }*/
      rendererArray = new ModelRenderer[][]{new ModelRenderer[10],{base5, base10, base4, base9, base3, base8, base2, base7, base1, base6},green,typesFull,full,booting};
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		base1.render(f5);
		base2.render(f5);
		base3.render(f5);
		base4.render(f5);
		base5.render(f5);
		base6.render(f5);
		base7.render(f5);
		base8.render(f5);
		base9.render(f5);
		base10.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float p_78087_1_, float p_78087_2_,
			float p_78087_3_, float p_78087_4_, float p_78087_5_,
			float p_78087_6_, Entity entityIn) {
		super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_,
				p_78087_5_, p_78087_6_, entityIn);
	}

	@Override
	public void renderStatic(float size, TileEntity tile) {
		TileEntityDrive te = (TileEntityDrive) tile;
		int slot1 = te.getDriveColor(0);
		int slot2 = te.getDriveColor(1);
		int slot3 = te.getDriveColor(2);
		int slot4 = te.getDriveColor(3);
		int slot5 = te.getDriveColor(4);
		int slot6 = te.getDriveColor(5);
		int slot7 = te.getDriveColor(6);
		int slot8 = te.getDriveColor(7);
		int slot9 = te.getDriveColor(8);
		int slot10 = te.getDriveColor(9);
		int[] slots = new int[]{slot1, slot2, slot3, slot4, slot5, slot6, slot7, slot8, slot9, slot10};
		for(int i = 0;i<10;i++){
			int slot = slots[i];
			ModelRenderer r = rendererArray[slot][i];
			/*switch(slot){
		  case 0:
			  continue;
		  case 1:
			  GL11.glPushMatrix();
			  GL11.glScalef(1.0F, -1F, -1F);
			  float scale = 1F / 16F;
			  //GL11.glEnable(GL11.GL_TEXTURE_2D);
			  GL11.glTranslatef(0,(-13/16F)-1,0.5001F);
			  GL11.glScalef(scale,scale,scale);
			  GL11.glRotatef(180, 0, 1, 0);
			  GlStateManager.color(1, 1, 1, 1);
			  GL11.glEnable(GL11.GL_TEXTURE_2D);
			  GL11.glEnable(GL11.GL_BLEND);
			  GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			  //GL11.glDisable(GL11.GL_LIGHTING);
			  //drawTexturedModalRect(0, 0, 0, 5, 5, 2);
			  drawTexturedModalRect(MathHelper.floor_double(r.rotationPointX), MathHelper.floor_double(r.rotationPointY), 0, 5, 5, 2);
			  GL11.glPopMatrix();
			  break;
		  case 2:
			  GL11.glPushMatrix();
			  //drawTexturedModalRect(MathHelper.floor_double(r.rotationPointX), MathHelper.floor_double(r.rotationPointY), 0, 7, 5, 2);
			  GL11.glPopMatrix();
			  break;
		  case 3:
			  GL11.glPushMatrix();
			  //drawTexturedModalRect(MathHelper.floor_double(r.rotationPointX), MathHelper.floor_double(r.rotationPointY), 0, 3, 5, 2);
			  GL11.glPopMatrix();
			  break;
		  case 4:
			  GL11.glPushMatrix();
			  //drawTexturedModalRect(MathHelper.floor_double(r.rotationPointX), MathHelper.floor_double(r.rotationPointY), 0, 9, 5, 2);
			  GL11.glPopMatrix();
			  break;
		  }*/
			if(r != null)r.render(size);
		}
		/*if(slot1 != 0){
		  base5.render(size);
	  }
	  if(slot2 != 0){
		  base4.render(size);
	  }
	  if(slot3 != 0){
		  base3.render(size);
	  }
	  if(slot4 != 0){
		  base2.render(size);
	  }
	  if(slot5 != 0){
		  base1.render(size);
	  }
	  if(slot6 != 0){
		  base10.render(size);
	  }
	  if(slot7 != 0){
		  base9.render(size);
	  }
	  if(slot8 != 0){
		  base8.render(size);
	  }
	  if(slot9 != 0){
		  base7.render(size);
	  }
	  if(slot10 != 0){
		  base6.render(size);
	  }*/
	}

	@Override
	public void renderDynamic(float size, TileEntity te, float partialTicks) {

	}

	@Override
	public ResourceLocation getModelTexture(TileEntity tile) {
		return Configs.driveModel;
	}

	@Override
	public boolean rotateModelBasedOnBlockMeta() {
		return true;
	}
	/**
	 * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
	 */
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
	{
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer worldrenderer = tessellator.getBuffer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		double zLevel = 0;
		worldrenderer.pos(x + 0, y + height, zLevel ).tex((textureX + 0) * f, (textureY + height) * f1).endVertex();
		worldrenderer.pos(x + width, y + height, zLevel).tex((textureX + width) * f, (textureY + height) * f1).endVertex();
		worldrenderer.pos(x + width, y + 0, zLevel).tex((textureX + width) * f, (textureY + 0) * f1).endVertex();
		worldrenderer.pos(x + 0, y + 0, zLevel).tex((textureX + 0) * f, (textureY + 0) * f1).endVertex();
		tessellator.draw();
	}
}
