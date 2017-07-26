package com.tom.api.energy;

import static com.tom.api.energy.EnergyStorage.DUMMY_STORAGE;
import static com.tom.api.energy.EnergyStorage.regulateValue;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		if (!canInteract(container))
			return 0;
		if (container.getTagCompound() == null) {
			container.setTagCompound(new NBTTagCompound());
		}
		double energy = container.getTagCompound().getDouble("Energy");
		double energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!simulate) {
			energy += energyReceived;
			container.getTagCompound().setDouble("Energy", regulateValue(energy));
		}
		return regulateValue(energyReceived);
	}

	@Override
	public double extractEnergy(ItemStack container, double maxExtract, boolean simulate) {
		if (!canInteract(container))
			return 0;
		if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) { return 0; }
		double energy = container.getTagCompound().getDouble("Energy");
		double energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

		if (!simulate) {
			energy -= energyExtracted;
			container.getTagCompound().setDouble("Energy", regulateValue(energy));
		}
		return regulateValue(energyExtracted);
	}

	@Override
	public double getEnergyStored(ItemStack container) {
		if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) { return 0; }
		return regulateValue(container.getTagCompound().getDouble("Energy"));
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {
		return capacity;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || (oldStack != null && newStack != null && (oldStack.getMetadata() != newStack.getMetadata() || oldStack.getItem() != newStack.getItem()));
	}

	public double getPercentStored(ItemStack container) {
		return this.getEnergyStored(container) / this.getMaxEnergyStored(container);
	}

	public boolean canInteract(ItemStack container) {
		return true;
	}

	public static IEnergyStorage getItemContainerAsStorage(ItemStack stack, double maxTransfer) {
		if (stack == null || !(stack.getItem() instanceof ItemEnergyContainer)) {
			return DUMMY_STORAGE;
		} else {
			ItemEnergyContainer c = (ItemEnergyContainer) stack.getItem();
			return new ItemEnergyStorage(maxTransfer, c, stack);
		}
	}

	public static class ItemEnergyStorage implements IEnergyStorage {
		private final ItemEnergyContainer c;
		private final ItemStack stack;
		private final double maxTransfer;

		public ItemEnergyStorage(double maxTransfer, ItemEnergyContainer c, ItemStack stack) {
			this.c = c;
			this.stack = stack;
			this.maxTransfer = maxTransfer;
		}

		@Override
		public double getEnergyStored() {
			return c.getEnergyStored(stack);
		}

		@Override
		public int getMaxEnergyStored() {
			return c.getMaxEnergyStored(stack);
		}

		@Override
		public double receiveEnergy(double maxReceive, boolean simulate) {
			return c.receiveEnergy(stack, Math.min(maxReceive, maxTransfer), simulate);
		}

		@Override
		public double extractEnergy(double maxExtract, boolean simulate) {
			return c.extractEnergy(stack, Math.min(maxExtract, maxTransfer), simulate);
		}

		@Override
		public boolean isFull() {
			return c.isFull(stack);
		}

		@Override
		public boolean hasEnergy() {
			return c.hasEnergy(stack);
		}

		@Override
		public double getMaxExtract() {
			return Math.min(maxTransfer, c.maxExtract);
		}

		@Override
		public double getMaxReceive() {
			return Math.min(maxTransfer, c.maxReceive);
		}
	}

	public boolean isFull(ItemStack stack) {
		return getEnergyStored(stack) == capacity;
	}

	public boolean hasEnergy(ItemStack stack) {
		return getEnergyStored(stack) > 0;
	}

	@SideOnly(Side.CLIENT)
	public static String getInfo(IEnergyContainerItem item, ItemStack stack) {
		double energy = item.getEnergyStored(stack);
		int max = item.getMaxEnergyStored(stack);
		double per = energy / max * 1000;
		double p = MathHelper.floor(per) / 10D;
		return I18n.format("tomsMod.tooltip.charge") + ": " + energy + "/" + max + " " + p + "%";
	}

	@SideOnly(Side.CLIENT)
	public String getInfo(ItemStack stack) {
		return getInfo(this, stack);
	}
}
