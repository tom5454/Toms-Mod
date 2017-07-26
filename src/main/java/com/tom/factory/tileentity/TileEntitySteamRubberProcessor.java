package com.tom.factory.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.core.CoreInit;
import com.tom.core.CoreInit.FluidSupplier;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntitySteamRubberProcessor extends TileEntitySteamMachine {
	public static final int MAX_PROCESS_TIME = 200;
	private int vulcanizing;
	private FluidTank tankIn = new FluidTank(4000);

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[]{0, 1};
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
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public int getSteamUsage() {
		return 2;
	}

	@Override
	public void checkItems() {
		ItemStackChecker s = new ItemStackChecker(CraftingMaterial.RUBBER.getStackNormal());
		checkItems(s, 1, MAX_PROCESS_TIME, 0, -1);
	}

	@Override
	public void finish() {
		addItemsAndSetProgress(1);
	}

	@Override
	protected boolean process() {
		if (tankIn.getFluidAmount() > 0 && vulcanizing > 0) {
			tankIn.drainInternal(1, true);
			vulcanizing--;
			return true;
		}
		return false;
	}

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return Helper.getFluidHandlerFromTanksWithPredicate(new FluidTank[]{tank, tankIn}, new FluidSupplier[]{CoreInit.steam, CoreInit.concentratedResin}, new boolean[]{true, true}, new boolean[]{false, false});
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setTag("tankIn", tankIn.writeToNBT(new NBTTagCompound()));
		tag.setInteger("vulcanizing", vulcanizing);
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tankIn.readFromNBT(tag.getCompoundTag("tankIn"));
		vulcanizing = tag.getInteger("vulcanizing");
	}

	@Override
	protected void update0() {
		if (CraftingMaterial.VULCANIZING_AGENTS.equals(inv.getStackInSlot(0)) && vulcanizing < 1) {
			decrStackSize(0, 1);
			vulcanizing = 1600;
			tank.drain(2, true);
		}
	}

	@Override
	public int getField(int id) {
		return id == 0 ? progress : id == 1 ? vulcanizing : 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0)
			progress = value;
		else if (id == 1)
			vulcanizing = value;
	}

	@Override
	public int getFieldCount() {
		return 2;
	}

	public FluidTank getTankIn() {
		return tankIn;
	}
}
