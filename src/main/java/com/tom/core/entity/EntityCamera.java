package com.tom.core.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

import com.tom.core.tileentity.TileEntityCamera;

public class EntityCamera extends EntityLivingBase {
	public boolean enableControls, eEsc;
	public float yawMin;
	public float pitchMin;
	public float yawMax;
	public float pitchMax;
	public int contX;
	public int contY;
	public int contZ;
	public TileEntityCamera te;

	public EntityCamera(World world, double posX, double posY, double posZ, float yaw, float pitch, boolean eC, float yawMin, float pitchMin, float yawMax, float pitchMax, int cX, int cY, int cZ, boolean eEsc, TileEntityCamera te) {
		super(world);
		this.setLocationAndAngles(posX, posY, posZ, yaw, pitch);
		this.setPositionAndRotation(posX, posY, posZ, yaw, pitch);
		this.enableControls = eC;
		this.contX = cX;
		this.contY = cY;
		this.contZ = cZ;
		this.yawMin = Math.min(yawMin, yawMax);
		this.yawMax = Math.max(yawMin, yawMax);
		this.pitchMin = Math.min(pitchMin, pitchMax);
		this.pitchMax = Math.max(pitchMin, pitchMax);
		this.te = te;
		this.eEsc = eEsc;
	}

	public EntityCamera(World world) {
		super(world);
	}

	public void setCameraInfo(float yaw, float pitch) {
		this.setLocationAndAngles(posX, posY, posZ, yaw, pitch);
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return null;
	}

	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
		return null;
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {

	}

	@Override
	public EnumHandSide getPrimaryHand() {
		return EnumHandSide.RIGHT;
	}
}
