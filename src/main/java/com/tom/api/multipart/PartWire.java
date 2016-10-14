package com.tom.api.multipart;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.capabilities.Capability;

import mcmultipart.capabilities.ISlottedCapabilityProvider;
import mcmultipart.multipart.IRedstonePart;
import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.PartMOP;

public abstract class PartWire extends MultipartTomsMod implements
IRedstonePart.ISlottedRedstonePart,
ITickable, ISlottedCapabilityProvider{
	public final ItemStack pick;
	protected byte internalConnections, externalConnections, cornerConnections, occludedSides, cornerOccludedSides;
	public PartWire(ItemStack drop) {
		this.pick = drop;
	}
	@Override
	public EnumSet<PartSlot> getSlotMask() {
		return null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, PartSlot slot,
			EnumFacing facing) {
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, PartSlot slot,
			EnumFacing facing) {
		return null;
	}

	@Override
	public void update() {
		this.updateEntity();
	}

	/*@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(IHitEffectsPart.AdvancedEffectRenderer advancedEffectRenderer) {
		return false;
	}

	@Override
	public boolean addHitEffects(PartMOP hit,
			AdvancedEffectRenderer effectRenderer) {
		return false;
	}*/
	public abstract void updateEntity();
	@Override
	public ItemStack getPickBlock(EntityPlayer player, PartMOP hit) {
		return this.pick.copy();
	}
	@Override
	public float getHardness(PartMOP hit) {
		return 0.2F;
	}
	@Override
	public ResourceLocation getModelPath() {
		return null;
	}
	/*public boolean connectsInternal(WireFace side) {
		return (internalConnections & (1 << side.ordinal())) != 0;
	}*/

	public boolean connectsExternal(EnumFacing side) {
		return (externalConnections & (1 << side.ordinal())) != 0;
	}

	public boolean connectsAny(EnumFacing direction) {
		return ((internalConnections | externalConnections | cornerConnections) & (1 << direction.ordinal())) != 0;
	}

	public boolean connectsCorner(EnumFacing direction) {
		return (cornerConnections & (1 << direction.ordinal())) != 0;
	}

	public boolean connects(EnumFacing direction) {
		return ((internalConnections | externalConnections) & (1 << direction.ordinal())) != 0;
	}
}
