package com.tom.energy.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import com.tom.factory.tileentity.TileEntityMachineBase;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.lib.api.energy.IEnergyContainerItem;
import com.tom.util.TomsModUtils;

import com.tom.energy.block.BlockCharger;

public class TileEntityCharger extends TileEntityMachineBase {
	public int clientEnergy = 0;
	private EnergyStorage energy = new EnergyStorage(1000000, 10000);

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0 ? isItemValidForSlot(index, itemStackIn) : false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 1;
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack != null && stack.getItem() instanceof IEnergyContainerItem;
	}

	@Override
	public String getName() {
		return "charger";
	}

	@Override
	public EnergyStorage getEnergy() {
		return energy;
	}

	@Override
	public int getUpgradeSlot() {
		return -1;
	}

	@Override
	public int getMaxProcessTimeNormal() {
		return -1;
	}

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			if (energy.hasEnergy() && canRun()) {
				if (!inv.getStackInSlot(0).isEmpty() && inv.getStackInSlot(0).getItem() instanceof IEnergyContainerItem) {
					if (((IEnergyContainerItem) inv.getStackInSlot(0).getItem()).getEnergyStored(inv.getStackInSlot(0)) == ((IEnergyContainerItem) inv.getStackInSlot(0).getItem()).getMaxEnergyStored(inv.getStackInSlot(0))) {
						TomsModUtils.setBlockStateWithCondition(world, pos, BlockCharger.ACTIVE, false);
						if (inv.getStackInSlot(1).isEmpty()) {
							inv.setInventorySlotContents(1, inv.getStackInSlot(0));
							inv.setInventorySlotContents(0, ItemStack.EMPTY);
						}
					} else {
						TomsModUtils.setBlockStateWithCondition(world, pos, BlockCharger.ACTIVE, true);
						double extract = energy.extractEnergy(Math.pow(10, (2 + (2 - getType()))), true);
						if (extract > 0) {
							double receive = ((IEnergyContainerItem) inv.getStackInSlot(0).getItem()).receiveEnergy(inv.getStackInSlot(0), extract, true);
							if (receive > 0) {
								((IEnergyContainerItem) inv.getStackInSlot(0).getItem()).receiveEnergy(inv.getStackInSlot(0), energy.extractEnergy(receive, false), false);
							}
						}
					}
				}
			} else {
				TomsModUtils.setBlockStateWithCondition(world, pos, BlockCharger.ACTIVE, false);
			}
		}
	}

	public int getClientEnergyStored() {
		return MathHelper.ceil(energy.getEnergyStored());
	}

	@Override
	public int getField(int id) {
		return id == 0 ? getChargedPer() : 0;
	}

	public int p;

	@Override
	public void setField(int id, int value) {
		if (id == 0)
			p = value;
	}

	private int getChargedPer() {
		if (!inv.getStackInSlot(0).isEmpty() && inv.getStackInSlot(0).getItem() instanceof IEnergyContainerItem) {
			IEnergyContainerItem c = (IEnergyContainerItem) inv.getStackInSlot(0).getItem();
			return MathHelper.ceil((c.getEnergyStored(inv.getStackInSlot(0)) / c.getMaxEnergyStored(inv.getStackInSlot(0))) * 100);
		} else
			return 0;
	}

	public long getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public ResourceLocation getFront() {
		return new ResourceLocation("tomsmodenergy:textures/blocks/charger.png");
	}

	@Override
	public int[] getOutputSlots() {
		return new int[]{1};
	}

	@Override
	public int[] getInputSlots() {
		return new int[]{0};
	}

	@Override
	public void checkItems() {
	}

	@Override
	public void finish() {
	}

	@Override
	public void updateProgress() {
	}
}
