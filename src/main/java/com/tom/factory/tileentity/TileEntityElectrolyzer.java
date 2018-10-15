package com.tom.factory.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import com.tom.api.block.BlockMultiblockController;
import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.factory.FactoryInit;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;
import com.tom.util.TomsModUtils;

public class TileEntityElectrolyzer extends TileEntityMultiblockController {
	public TileEntityElectrolyzer() {
		super(false);
		tankIn = new FluidTank(10000);
		tankOut = new FluidTank(10000);
		tankOut2 = new FluidTank(10000);
		tankOut2.setCanFill(false);
	}

	private FluidTank tankIn;
	private FluidTank tankOut;
	private FluidTank tankOut2;
	private EnergyStorage energy = new EnergyStorage(10000);
	private ItemStackChecker processing;
	protected int processTime = -1;
	protected int processTimeMax;

	@Override
	public void updateEntity(IBlockState state) {
		if (!world.isRemote && getMultiblock(state)) {
			boolean active = false;
			if (energy.getEnergyStored() >= 10) {
				if (this.processTime > 0) {
					this.processTime--;
					energy.extractEnergy(10, false);
					active = true;
				} else if (this.processTime == 0) {
					tankOut.fillInternal(processing.getExtraF(), true);
					tankOut2.fillInternal(processing.getExtraF2(), true);
					processing = null;
					processTime = -1;
					active = true;
				} else {
					ItemStackChecker s = MachineCraftingHandler.getElectrolyzerOutput(tankIn.getFluid());
					if (s != null) {
						if (!s.getMode() || (tankOut.getFluidAmount() == 0 || tankOut.getFluid().isFluidEqual(s.getExtraF()))) {
							processTime = processTimeMax = s.getExtra();
							processing = s;
							tankIn.drainInternal(s.getExtra2(), true);
							active = true;
						}
					}
				}
			}
			TomsModUtils.setBlockStateWithCondition(world, pos, state, BlockMultiblockController.STATE, active ? 2 : 1);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.processTime = tag.getInteger("p");
		this.processTimeMax = tag.getInteger("pmax");
		energy.readFromNBT(tag);
		tankIn.readFromNBT(tag.getCompoundTag("tankIn"));
		tankOut.readFromNBT(tag.getCompoundTag("tankOut"));
		tankOut2.readFromNBT(tag.getCompoundTag("tankOut2"));
		processing = ItemStackChecker.load(tag.getCompoundTag("processing"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("p", this.processTime);
		tag.setInteger("pmax", processTimeMax);
		energy.writeToNBT(tag);
		tag.setTag("tankIn", tankIn.writeToNBT(new NBTTagCompound()));
		tag.setTag("tankOut", tankOut.writeToNBT(new NBTTagCompound()));
		tag.setTag("tankOut2", tankOut2.writeToNBT(new NBTTagCompound()));
		if (processing != null)
			tag.setTag("processing", processing.writeToNew());
		return tag;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing, BlockPos from, int id) {
		return id > 0 && (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (id == 3 || id == 4 || id == 7 || id == 8)) || (capability == EnergyType.ENERGY_HANDLER_CAPABILITY && id == 9);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing, BlockPos from, int id) {
		return (T) (id > 0 ? capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? id == 3 || id == 7 ? tankIn : id == 4 || id == 8 ? tankOut : null : capability == EnergyType.ENERGY_HANDLER_CAPABILITY && id == 9 ? energy.toCapability(true, false, EnergyType.MV) : null : null);
	}

	@Override
	public ItemStack getStack() {
		return new ItemStack(FactoryInit.Electrolyzer);
	}

	@Override
	public int[] getSlots(int id) {
		return null;
	}

	@Override
	public IInventory getInventory(int id) {
		return null;
	}
}
