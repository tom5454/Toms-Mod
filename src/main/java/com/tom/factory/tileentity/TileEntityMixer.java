package com.tom.factory.tileentity;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.inventory.InventorySection;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;
import com.tom.util.TomsModUtils;
import com.tom.util.TomsModUtils.FillRunnable;

public class TileEntityMixer extends TileEntityMachineBase implements ITileFluidHandler {
	private FluidTank tankIn = new FluidTank(10000);
	private FluidTank tankIn2 = new FluidTank(10000);
	private FluidTank tankOut = new FluidTank(10000);
	private EnergyStorage energy = new EnergyStorage(10000, 100);
	private static final int MAX_PROCESS_TIME = 300;
	public int clientEnergy = 0;
	private static final Object[][] RECIPES_NEW = new Object[][]{{TomsModUtils.createRecipe(new Object[]{new ItemStack(Items.GUNPOWDER, 2), new ItemStack(Items.ROTTEN_FLESH, 8), "dyeWhite", Items.SUGAR}), new Object[][]{{new FluidStack(FluidRegistry.WATER, 1000), true, 0}, {new FluidStack(CoreInit.hydrogenChloride.get(), 500), false, 1}, {new FluidStack(CoreInit.Hydrogen.get(), 1000), true, 2}}, true,}, {TomsModUtils.createRecipe(new Object[]{CraftingMaterial.PLASTIC_SHEET.getStack(), new Object[]{"dyeWhite", 2}, new Object[]{TMResource.IRON.getStackName(Type.DUST), 3}, Items.SUGAR}), new Object[][]{{new FluidStack(FluidRegistry.WATER, 1000), true, 0}, {new FluidStack(CoreInit.heatConductingPaste.get(), 100), false, 1}}, true,},};
	public static final Object[][] RECIPES = new Object[RECIPES_NEW.length + TileEntitySteamMixer.RECIPES.length][];
	static {
		System.arraycopy(TileEntitySteamMixer.RECIPES, 0, RECIPES, 0, TileEntitySteamMixer.RECIPES.length);
		System.arraycopy(RECIPES_NEW, 0, RECIPES, TileEntitySteamMixer.RECIPES.length, RECIPES_NEW.length);
	}

	@Override
	public int getSizeInventory() {
		return 5;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index < 4;
	}

	@Override
	public String getName() {
		return "mixer";
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 1;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.progress = compound.getInteger("progress");
		tankIn.readFromNBT(compound.getCompoundTag("tankIn"));
		tankIn2.readFromNBT(compound.getCompoundTag("tankIn2"));
		tankOut.readFromNBT(compound.getCompoundTag("tankOut"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("progress", progress);
		compound.setTag("tankIn", tankIn.writeToNBT(new NBTTagCompound()));
		compound.setTag("tankIn2", tankIn2.writeToNBT(new NBTTagCompound()));
		compound.setTag("tankOut", tankOut.writeToNBT(new NBTTagCompound()));
		return compound;
	}

	@Override
	public void writeToStackNBT(NBTTagCompound tag) {
		super.writeToStackNBT(tag);
		tag.setTag("tankIn", tankIn.writeToNBT(new NBTTagCompound()));
		tag.setTag("tankIn2", tankIn2.writeToNBT(new NBTTagCompound()));
		tag.setTag("tankOut", tankOut.writeToNBT(new NBTTagCompound()));
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
		int p = upgradeC + 1 + (upgradeC / 2);
		progress = Math.max(0, progress - p);
		energy.extractEnergy(0.5D * p, false);
	}

	@Override
	public EnergyStorage getEnergy() {
		return energy;
	}

	@Override
	public int getUpgradeSlot() {
		return 4;
	}

	@Override
	public int getMaxProcessTimeNormal() {
		return MAX_PROCESS_TIME;
	}

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return Helper.getFluidHandlerFromTanksWithPredicate(new FluidTank[]{tankIn, tankIn2, tankOut}, new Object[]{FluidRegistry.WATER, CoreInit.Hydrogen.get(), null}, new boolean[]{true, true, false}, new boolean[]{false, false, true});
	}

	@SuppressWarnings("unchecked")
	private Object[] findRecipe(boolean apply) {
		Object[] obj = TomsModUtils.checkAndConsumeMatch(RECIPES, new InventorySection(this, 0, 4), new Object[]{tankIn, tankOut, tankIn2});
		if ((Integer) obj[0] > -1) {
			if (apply)
				TomsModUtils.runAll(((List<Runnable>) obj[1]).stream().filter(r -> !(r instanceof FillRunnable)).collect(Collectors.toList()));
			return new Object[]{obj[0], obj};
		}
		return new Object[]{-1};
	}

	public FluidTank getTankIn() {
		return tankIn;
	}

	public FluidTank getTankIn2() {
		return tankIn2;
	}

	public FluidTank getTankOut() {
		return tankOut;
	}

	@Override
	public ResourceLocation getFront() {
		return new ResourceLocation("tomsmodfactory:textures/blocks/mixer.png");
	}

	@Override
	public int[] getOutputSlots() {
		return null;
	}

	@Override
	public int[] getInputSlots() {
		return new int[]{0, 1, 2, 3};
	}

	@Override
	public void pushOutput(EnumFacing side) {
		if (tankOut.getFluidAmount() > 0) {
			TileEntity tile = world.getTileEntity(pos.offset(side));
			if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite())) {
				IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
				if (t != null) {
					int filled = t.fill(tankOut.getFluid(), false);
					if (filled > 0) {
						FluidStack drained = tankOut.drain(filled, false);
						if (drained != null && drained.amount > 0) {
							int canDrain = Math.min(filled, Math.min(100, drained.amount));
							t.fill(tankOut.drain(canDrain, true), true);
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void checkItems() {
		if (((Integer) findRecipe(false)[0]) > -1) {
			Object[] r = findRecipe(true);
			progress = getMaxProgress();
			setOut(0, ((List<ItemStackChecker>) ((Object[]) r[1])[2]).get(0));
		}
	}

	@Override
	public void finish() {
		progress = -1;
		ItemStackChecker c = getOutput(0);
		if (c != null && c.getExtraF() != null) {
			int f = tankOut.fillInternal(c.getExtraF(), false);
			if (f == c.getExtraF().amount) {
				tankOut.fillInternal(c.getExtraF(), true);
			}
		}
	}
}
