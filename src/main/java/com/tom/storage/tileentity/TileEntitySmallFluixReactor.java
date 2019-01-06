package com.tom.storage.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.lib.api.energy.EnergyType;
import com.tom.lib.api.energy.IEnergyReceiver;
import com.tom.util.TomsModUtils;

public class TileEntitySmallFluixReactor extends TileEntityTomsMod implements ITileFluidHandler, IEnergyReceiver, ISidedInventory {
	private InventoryBasic inv = new InventoryBasic("", false, getSizeInventory());
	private EnergyStorage energy = new EnergyStorage(10000);

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return false;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return null;
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUsable(pos, player, world, this);
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[]{};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getEnergyStored();
	}

	@Override
	public long getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getMaxEnergyStored();
	}

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return null;
	}

}
