package com.tom.factory.tileentity;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.core.CoreInit;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.recipes.OreDict;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;
import com.tom.util.FluidSupplier;

public class TileEntityElectricalRubberProcessor extends TileEntityMachineBase implements ITileFluidHandler {
	private EnergyStorage energy = new EnergyStorage(10000, 100);
	private static final int MAX_PROCESS_TIME = 200;
	public int clientEnergy = 0;
	private int vulcanizing;
	private FluidTank tankIn = new FluidTank(10000), tankCresin = new FluidTank(4000);

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0 ? OreDict.isOre(itemStackIn, "logRubber") : index == 1 ? CraftingMaterial.VULCANIZING_AGENTS.equals(itemStackIn) : false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 2;
	}

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index == 0 ? OreDict.isOre(stack, "logRubber") : index == 1 ? CraftingMaterial.VULCANIZING_AGENTS.equals(stack) : false;
	}

	@Override
	public String getName() {
		return "electricalrubberprocessor";
	}

	@Override
	public EnergyStorage getEnergy() {
		return energy;
	}

	@Override
	public int getUpgradeSlot() {
		return 3;
	}

	@Override
	public int[] getOutputSlots() {
		return new int[]{2};
	}

	@Override
	public int[] getInputSlots() {
		return new int[]{0, 1};
	}

	@Override
	public int getMaxProcessTimeNormal() {
		return MAX_PROCESS_TIME;
	}

	@Override
	public void checkItems() {
		if (OreDict.isOre(inv.getStackInSlot(0), "logRubber")) {
			ItemStackChecker s = new ItemStackChecker(new ItemStack(Items.COAL, 1, 1));
			s.setExtra(1);
			checkItems(s, 2, MAX_PROCESS_TIME, 0, -1);
			setOut(0, s);
		} else if (OreDict.isOre(inv.getStackInSlot(0), "leavesRubber")) {
			ItemStackChecker s = new ItemStackChecker(ItemStack.EMPTY);
			s.setExtra(1);
			s.setExtra2(1);
			checkItems(s, 2, MAX_PROCESS_TIME, 0, -1);
			setOut(0, s);
		} else if (tankCresin.getFluidAmount() >= 200) {
			ItemStackChecker s = new ItemStackChecker(CraftingMaterial.RUBBER.getStackNormal());
			checkItems(s, 2, MAX_PROCESS_TIME, -1, -1);
			setOut(0, s);
		}
	}

	@Override
	public void finish() {
		addItemsAndSetProgress(getOutput(0), 2);
	}

	@Override
	public void updateProgress() {
		int upgradeC = getSpeedUpgradeCount();
		int p = upgradeC + 1 + (upgradeC / 2);
		p = Math.min(p, progress);
		ItemStackChecker s = getOutput(0);
		if (s != null) {
			ItemStack stack = s.getStack();
			if (CraftingMaterial.RUBBER.equals(stack)) {
				p = Math.min(Math.min(p, vulcanizing), tankCresin.getFluidAmount());
				progress = Math.max(0, progress - p);
				tankCresin.drainInternal(p, true);
				energy.extractEnergy(0.5D * p, false);
				vulcanizing -= p;
				return;
			} else if (stack.getItem() == Items.COAL && stack.getMetadata() == 1) {
				p = Math.min(p, tankIn.getCapacity() - tankIn.getFluidAmount());
				tankIn.fillInternal(new FluidStack(CoreInit.resin.get(), p), true);
				progress = Math.max(0, progress - p);
				energy.extractEnergy(0.5D * p, false);
				return;
			} else if (stack.isEmpty() && s.getExtra2() == 1) {
				p = Math.min(p, tankIn.getCapacity() - tankIn.getFluidAmount());
				tankIn.fillInternal(new FluidStack(CoreInit.resin.get(), p), true);
				progress = Math.max(0, progress - p*5);
				energy.extractEnergy(0.5D * p, false);
				return;
			}
		}
	}

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			if (vulcanizing < 1 && CraftingMaterial.VULCANIZING_AGENTS.equals(inv.getStackInSlot(1))) {
				decrStackSize(1, 1);
				vulcanizing = 1600;
			}
			if (tankIn.getFluidAmount() >= 5 && tankCresin.getFluidAmount() < tankCresin.getCapacity()) {
				int a = Math.min(tankIn.getFluidAmount() / 5, tankCresin.getCapacity() - tankCresin.getFluidAmount());
				int upgradeC = getSpeedUpgradeCount();
				int p = upgradeC + 1 + (upgradeC / 2);
				p = Math.min(p, a);
				energy.extractEnergy(0.5D * p, false);
				tankIn.drainInternal(p * 5, true);
				tankCresin.fillInternal(new FluidStack(CoreInit.concentratedResin.get(), p), true);
			}
		}
		super.updateEntity();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.progress = compound.getInteger("progress");
		tankIn.readFromNBT(compound.getCompoundTag("tankIn"));
		tankCresin.readFromNBT(compound.getCompoundTag("tankResin"));
		vulcanizing = compound.getInteger("vulcanizing");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("progress", progress);
		compound.setTag("tankIn", tankIn.writeToNBT(new NBTTagCompound()));
		compound.setTag("tankResin", tankCresin.writeToNBT(new NBTTagCompound()));
		compound.setInteger("vulcanizing", vulcanizing);
		return compound;
	}

	public int getClientEnergyStored() {
		return MathHelper.ceil(energy.getEnergyStored());
	}

	public long getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public ResourceLocation getFront() {
		return new ResourceLocation("tomsmodfactory:textures/blocks/erubberprocessorFront.png");
	}

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return Helper.getFluidHandlerFromTanksWithPredicate(new FluidTank[]{tankIn, tankCresin}, new FluidSupplier[]{CoreInit.resin, CoreInit.concentratedResin}, new boolean[]{true, true}, new boolean[]{false, false});
	}

	public FluidTank getTankIn() {
		return tankIn;
	}

	public FluidTank getTankCresin() {
		return tankCresin;
	}

	@Override
	public int getFieldCount() {
		return super.getFieldCount() + 1;
	}

	@Override
	public int getField(int id) {
		return id == 2 ? vulcanizing : super.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		if (id == 2)
			vulcanizing = value;
		else
			super.setField(id, value);
	}
}
