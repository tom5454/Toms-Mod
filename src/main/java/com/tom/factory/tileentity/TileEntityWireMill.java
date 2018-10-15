package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.item.IExtruderModule;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntityWireMill extends TileEntityMachineBase {
	private EnergyStorage energy = new EnergyStorage(20000, 100);
	private static final int MAX_PROCESS_TIME = 350;
	public int clientEnergy = 0;

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public String getName() {
		return "wiremill";
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 1;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.progress = compound.getInteger("progress");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("progress", progress);
		return compound;
	}

	public int getClientEnergyStored() {
		return MathHelper.ceil(energy.getEnergyStored());
	}

	public long getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public void updateProgress() {
		int upgradeC = getSpeedUpgradeCount();
		IExtruderModule m = getBlendingModule();
		int speed = m != null ? m.getSpeed(inv.getStackInSlot(3), world, pos) + 1 : 1;
		int p = upgradeC + (upgradeC / 2) + speed;
		progress = Math.max(0, progress - p);
		energy.extractEnergy(1 * p, false);
	}

	private int getBlendingLevel() {
		int lvl = getBlendingModuleLevel();
		return lvl <= 0 ? 0 : (lvl == 1 ? 2 : (lvl == 2 ? 4 : 5));
	}

	private int getBlendingModuleLevel() {
		IExtruderModule m = getBlendingModule();
		return m != null ? m.getLevel(inv.getStackInSlot(3), world, pos) : 0;
	}

	private IExtruderModule getBlendingModule() {
		return inv.getStackInSlot(3) != null && inv.getStackInSlot(3).getItem() instanceof IExtruderModule ? (IExtruderModule) inv.getStackInSlot(3).getItem() : null;
	}

	@Override
	public EnergyStorage getEnergy() {
		return energy;
	}

	@Override
	public int getUpgradeSlot() {
		return 2;
	}

	@Override
	public void writeToStackNBT(NBTTagCompound tag) {
		super.writeToStackNBT(tag);
		NBTTagList list = tag.getTagList("inventory", 10);
		NBTTagCompound t = new NBTTagCompound();
		inv.getStackInSlot(3).writeToNBT(t);
		t.setByte("Slot", (byte) 3);
		list.appendTag(t);
	}

	@Override
	public int getMaxProcessTimeNormal() {
		return MAX_PROCESS_TIME;
	}

	@Override
	public ResourceLocation getFront() {
		return new ResourceLocation("tomsmodfactory:textures/blocks/wiremillFront.png");
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
		ItemStackChecker s = MachineCraftingHandler.getWireMillOutput(inv.getStackInSlot(0), getBlendingLevel());
		checkItems(s, 1, getMaxProgress(), 0, -1);
		setOut(0, s);
	}

	@Override
	public void finish() {
		ItemStackChecker s = getOutput(0);
		addItemsAndSetProgress(s, 1, 0, -1);
	}
}
