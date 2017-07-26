package com.tom.factory.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.energy.EnergyStorage;
import com.tom.api.tileentity.IGuiTile;
import com.tom.apis.TomsModUtils;
import com.tom.factory.block.FluidTransposer;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntityFluidTransposer extends TileEntityMachineBase implements ITileFluidHandler, IGuiTile {
	private EnergyStorage energy = new EnergyStorage(20000, 1000);
	private FluidTank tank = new FluidTank(10000);
	private boolean isExtract = false;
	public int clientEnergy;

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[]{0, 2};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0;
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
		return true;
	}

	@Override
	public String getName() {
		return "fluidTransposer";
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
	public int getMaxProcessTimeNormal() {
		return 300;
	}

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return tank;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("progress", progress);
		compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
		compound.setBoolean("mode", isExtract);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tank.readFromNBT(compound.getCompoundTag("tank"));
		progress = compound.getInteger("progress");
		isExtract = compound.getBoolean("mode");
		// tank.fill(new FluidStack(FluidRegistry.WATER, 5000), true);
	}

	@Override
	public void updateEntity(IBlockState state) {
		if (!world.isRemote) {
			clientEnergy = MathHelper.ceil(energy.getEnergyStored());
			if (inv.getStackInSlot(1).isEmpty()) {
				inv.setInventorySlotContents(1, decrStackSize(0, 1));
				TomsModUtils.setBlockStateWithCondition(world, pos, FluidTransposer.ACTIVE, false);
			} else {
				super.updateEntity(state);
			}
		}
	}

	@Override
	public void updateProgress() {
		int upgradeC = getSpeedUpgradeCount();
		int p = upgradeC + 1 + (upgradeC / 2);
		progress = Math.max(0, progress - p);
		energy.extractEnergy(1D * p, false);
	}

	public int getClientEnergyStored() {
		return clientEnergy;
	}

	public FluidTank getTank() {
		return tank;
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if (id == 0) {
			isExtract = extra == 1;
		}
	}

	public boolean getMode() {
		return isExtract;
	}

	public void setMode(int mode) {
		isExtract = mode == 1;
	}

	@Override
	public ResourceLocation getFront() {
		return new ResourceLocation("tomsmodfactory:textures/blocks/fluidTransposer.png");
	}

	@Override
	public int[] getOutputSlots() {
		return new int[]{2};
	}

	@Override
	public int[] getInputSlots() {
		return new int[]{0};
	}

	@Override
	public void checkItems() {
		ItemStackChecker s = MachineCraftingHandler.getFluidTransposerOutput(inv.getStackInSlot(1), tank, isExtract);
		if (s != null) {
			checkItems(s, 2, getMaxProgress(), 1, -1, () -> {
				if (!isExtract)
					tank.drain(s.getExtra(), true);
			});
			setOut(0, s);
		}
	}

	@Override
	public void finish() {
		ItemStackChecker s = getOutput(0);
		addItemsAndSetProgress(s, 2, 1, -1, () -> {
			if (isExtract)
				tank.fill(s.getExtraF(), true);
		});
	}
}
