// Date: 2016.03.18. 15:20:48
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX
package com.tom.transport.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelBelt extends ModelBase
{
  //fields
    ModelRenderer belt;
  
  public ModelBelt()
  {
    textureWidth = 64;
    textureHeight = 64;
    
      belt = new ModelRenderer(this, 0, 0);
      belt.addBox(0F, 0F, 0F, 14, 1, 16);
      belt.setRotationPoint(-7F, 18F, -8F);
      belt.setTextureSize(64, 64);
      belt.mirror = true;
      setRotation(belt, 0F, 0F, 0F);
  }
  
  @Override
public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5,entity);
    belt.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  @Override
public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5,Entity ent)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5,ent);
  }
  public void render(float f){
	belt.render(f);
	}
	public void setTexOffset(double offset){
		belt.cubeList.clear();
		belt.setTextureOffset(0, MathHelper.floor_double(offset));
		belt.addBox(0F, 0F, 0F, 14, 1, 16);
		belt.setRotationPoint(-7F, 18F, -8F);
		belt.setTextureSize(64, 64);
		belt.mirror = true;
	}
	public void rotate(float angleX, float angleY, float angleZ){
		setRotation(belt, angleX, angleY, angleZ);
	}
}
