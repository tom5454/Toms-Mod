package com.tom.factory.tileentity;

import static com.tom.api.energy.EnergyType.HV;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.ElectricFurnace;
import com.tom.recipes.handler.MachineCraftingHandler;

public class TileEntityElectricFurnaceAdv extends TileEntityTomsMod implements ISidedInventory, IEnergyReceiver {
	private InventoryBasic inv = new InventoryBasic("", false, getSizeInventory());
	private EnergyStorage energy = new EnergyStorage(10000, 100);
	private int progress = -1;
	private static final int[] SLOTS = new int[]{0, 1};
	public static final int MAX_PROCESS_TIME = 50;
	// private int maxProgress = 1;
	public int clientEnergy = 0;

	@Override
	public int getSizeInventory() {
		return 3;
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
		return id == 0 ? progress : 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id == 0)
			progress = value;
		// else if(id == 1)maxProgress = value;
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public String getName() {
		return "advElectricFurnace";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return SLOTS;
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
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == HV;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return HV.getList();
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return canConnectEnergy(from, type) ? energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getMaxEnergyStored();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound);
		TomsModUtils.loadAllItems(compound.getTagList("inventory", 10), inv);
		this.progress = compound.getInteger("progress");
		// this.maxProgress = compound.getInteger("maxProgress");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		energy.writeToNBT(compound);
		compound.setTag("inventory", TomsModUtils.saveAllItems(inv));
		compound.setInteger("progress", progress);
		// compound.setInteger("mayProgress", maxProgress);
		return compound;
	}

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			if (energy.extractEnergy(20D, true) == 20D) {
				if (progress > 0) {
					updateProgress();
				} else if (progress == 0) {
					ItemStack s = MachineCraftingHandler.getFurnaceRecipe(inv.getStackInSlot(0));
					if (!s.isEmpty()) {
						if (!inv.getStackInSlot(1).isEmpty()) {
							if (TomsModUtils.areItemStacksEqual(inv.getStackInSlot(1), s, true, true, false) && inv.getStackInSlot(1).getCount() + s.getCount() <= s.getMaxStackSize() && inv.getStackInSlot(0).getCount() >= 1) {
								inv.getStackInSlot(1).grow(s.getCount());
								progress = -1;
								decrStackSize(0, 1);
							}
						} else {
							progress = -1;
							inv.setInventorySlotContents(1, s);
							decrStackSize(0, 1);
						}
					} else {
						progress = -1;
					}
				} else {
					ItemStack s = MachineCraftingHandler.getFurnaceRecipe(inv.getStackInSlot(0));
					if (!s.isEmpty()) {
						if (!inv.getStackInSlot(1).isEmpty()) {
							if (TomsModUtils.areItemStacksEqual(inv.getStackInSlot(1), s, true, true, false) && inv.getStackInSlot(1).getCount() + s.getCount() <= s.getMaxStackSize() && inv.getStackInSlot(0).getCount() >= 1) {
								progress = MAX_PROCESS_TIME;
							}
						} else {
							progress = MAX_PROCESS_TIME;
						}
					}
					TomsModUtils.setBlockStateWithCondition(world, pos, ElectricFurnace.ACTIVE, progress > 0);
				}
			} else {
				TomsModUtils.setBlockStateWithCondition(world, pos, ElectricFurnace.ACTIVE, false);
			}
		}
	}

	public int getClientEnergyStored() {
		return MathHelper.ceil(energy.getEnergyStored());
	}

	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	private void updateProgress() {
		int upgradeC = getSpeedUpgradeCount();
		int p = upgradeC + 1 + (upgradeC / 2);
		progress = Math.max(0, progress - p);
		energy.extractEnergy(1.5D * p, false);
	}

	private int getSpeedUpgradeCount() {
		return !inv.getStackInSlot(2).isEmpty() && inv.getStackInSlot(2).getItem() == FactoryInit.speedUpgrade ? inv.getStackInSlot(2).getCount() : 0;
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
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public void clear() {
		inv.clear();
	}
}