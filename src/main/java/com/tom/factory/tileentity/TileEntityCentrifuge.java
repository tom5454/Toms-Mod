package com.tom.factory.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import com.tom.api.block.BlockMultiblockController;
import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.tileentity.SidedInventoryHandler;
import com.tom.factory.FactoryInit;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;
import com.tom.util.TomsModUtils;

public class TileEntityCentrifuge extends TileEntityMultiblockController {
	public TileEntityCentrifuge() {
		super(false);
		tankIn = new FluidTank(10000);
		tankOut = new FluidTank(10000);
		tankOut.setCanFill(false);
	}

	private FluidTank tankIn;
	private FluidTank tankOut;
	private EnergyStorage energy = new EnergyStorage(10000);
	private SidedInventoryHandler inv = new SidedInventoryHandler("", false, 5) {

		@Override
		public int[] getSlotsForFace(EnumFacing side) {
			return null;
		}

		@Override
		public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
			return index == 0;
		}

		@Override
		public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
			return index > 0;
		}
	};
	private IItemHandler invIn = new SidedInvWrapper(inv, EnumFacing.DOWN);
	private IItemHandler invOut = new SidedInvWrapper(inv, EnumFacing.UP);
	private ItemStackChecker processing;
	private NonNullList<ItemStack> stuck = NonNullList.withSize(3, ItemStack.EMPTY);
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
					if (processing.getMode())
						tankOut.fillInternal(processing.getExtraF(), true);
					else {
						if (TomsModUtils.isItemListEmpty(stuck)) {
							ItemStack stack = TomsModUtils.putStackInInventoryAllSlots(invOut, processing.getStack());
							if (!stack.isEmpty()) {
								stuck.set(0, stack);
							}
							stack = TomsModUtils.putStackInInventoryAllSlots(invOut, processing.getExtraStack());
							if (!stack.isEmpty()) {
								stuck.set(1, stack);
							}
							stack = TomsModUtils.putStackInInventoryAllSlots(invOut, processing.getExtraStack2());
							if (!stack.isEmpty()) {
								stuck.set(2, stack);
							}
							if (TomsModUtils.isItemListEmpty(stuck)) {
								processing = null;
								processTime = -1;
							}
						} else {
							ItemStack stack;
							if (!stuck.get(0).isEmpty()) {
								stack = TomsModUtils.putStackInInventoryAllSlots(invOut, stuck.get(0));
								if (!stack.isEmpty()) {
									stuck.set(0, stack);
								}
							}
							if (!stuck.get(1).isEmpty()) {
								stack = TomsModUtils.putStackInInventoryAllSlots(invOut, stuck.get(1));
								if (!stack.isEmpty()) {
									stuck.set(1, stack);
								}
							}
							if (!stuck.get(2).isEmpty()) {
								stack = TomsModUtils.putStackInInventoryAllSlots(invOut, stuck.get(2));
								if (!stack.isEmpty()) {
									stuck.set(2, stack);
								}
							}
						}
					}
					active = true;
				} else {
					ItemStackChecker s = MachineCraftingHandler.getCentrifugeOutput(inv.getStackInSlot(0), tankIn.getFluid());
					if (s != null) {
						if (!s.getMode() || (tankOut.getFluidAmount() == 0 || tankOut.getFluid().isFluidEqual(s.getExtraF()))) {
							processTime = processTimeMax = s.getExtra();
							processing = s;
							if (s.getMode())
								tankIn.drainInternal(s.getExtra2(), true);
							else
								inv.decrStackSize(0, s.getExtra2());
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
		processing = ItemStackChecker.load(tag.getCompoundTag("processing"));
		TomsModUtils.loadAllItems(tag, "inventory", inv);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("p", this.processTime);
		tag.setInteger("pmax", processTimeMax);
		energy.writeToNBT(tag);
		tag.setTag("tankIn", tankIn.writeToNBT(new NBTTagCompound()));
		tag.setTag("tankOut", tankOut.writeToNBT(new NBTTagCompound()));
		if (processing != null)
			tag.setTag("processing", processing.writeToNew());
		TomsModUtils.writeInventory("inventory", tag, inv);
		return tag;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing, BlockPos from, int id) {
		return id > 0 && (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (id == 1 || id == 5 || id == 2 || id == 6)) || (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (id == 3 || id == 4 || id == 7 || id == 8)) || (capability == EnergyType.ENERGY_HANDLER_CAPABILITY && id == 9);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing, BlockPos from, int id) {
		return (T) (id > 0 ? capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? id == 1 || id == 5 ? invIn : id == 2 || id == 6 ? invOut : null : capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? id == 3 || id == 7 ? tankIn : id == 4 || id == 8 ? tankOut : null : capability == EnergyType.ENERGY_HANDLER_CAPABILITY && id == 9 ? energy.toCapability(true, false, EnergyType.MV) : null : null);
	}

	@Override
	public ItemStack getStack() {
		return new ItemStack(FactoryInit.Centrifuge);
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
