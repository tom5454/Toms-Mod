package com.tom.api.energy;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemEnergyContainer extends Item implements IEnergyContainerItem {

	protected int capacity;
	protected double maxReceive;
	protected double maxExtract;

	public ItemEnergyContainer(int capacity) {

		this(capacity, capacity, capacity);
	}

	public ItemEnergyContainer(int capacity, double maxTransfer) {

		this(capacity, maxTransfer, maxTransfer);
	}

	public ItemEnergyContainer(int capacity, double maxReceive, double maxExtract) {

		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}

	public ItemEnergyContainer setCapacity(int capacity) {

		this.capacity = capacity;
		return this;
	}

	public void setMaxTransfer(double maxTransfer) {

		setMaxReceive(maxTransfer);
		setMaxExtract(maxTransfer);
	}

	public void setMaxReceive(double maxReceive) {

		this.maxReceive = maxReceive;
	}

	public void setMaxExtract(double maxExtract) {

		this.maxExtract = maxExtract;
	}

	/* IEnergyContainerItem */
	@Override
	public double receiveEnergy(ItemStack container, double maxReceive, boolean simulate) {
		if(!canInteract(container))return 0;
		if (container.getTagCompound() == null) {
			container.setTagCompound(new NBTTagCompound());
		}
		double energy = container.getTagCompound().getDouble("Energy");
		double energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!simulate) {
			energy += energyReceived;
			container.getTagCompound().setDouble("Energy", energy);
		}
		return energyReceived;
	}

	@Override
	public double extractEnergy(ItemStack container, double maxExtract, boolean simulate) {
		if(!canInteract(container))return 0;
		if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) {
			return 0;
		}
		double energy = container.getTagCompound().getDouble("Energy");
		double energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

		if (!simulate) {
			energy -= energyExtracted;
			container.getTagCompound().setDouble("Energy", energy);
		}
		return energyExtracted;
	}

	@Override
	public double getEnergyStored(ItemStack container) {

		if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) {
			return 0;
		}
		return container.getTagCompound().getInteger("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {

		return capacity;
	}
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged || (oldStack != null && newStack != null && (oldStack.getMetadata() != newStack.getMetadata() || oldStack.getItem() != newStack.getItem()));
	}
	public double getPercentStored(ItemStack container){
		return this.getEnergyStored(container) / this.getMaxEnergyStored(container);
	}
	public boolean canInteract(ItemStack container){
		return true;
	}
}
