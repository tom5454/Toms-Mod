package com.tom.factory.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import com.tom.api.ITileFluidHandler.Helper;
import com.tom.api.block.BlockMultiblockController;
import com.tom.api.item.IFuelRod;
import com.tom.core.CoreInit;
import com.tom.factory.FactoryInit;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.lib.api.energy.EnergyType;
import com.tom.util.TomsModUtils;

public class TileEntityFusionPreHeater extends TileEntityMultiblockController {
	public static final FluidStack Deuterium = new FluidStack(CoreInit.Deuterium.get(), 1);
	public static final FluidStack Tritium = new FluidStack(CoreInit.Tritium.get(), 1);
	public static final FluidStack fusionFuel = new FluidStack(CoreInit.fusionFuel.get(), 20);
	public static final int FUEL_USAGE = 5, MAX_HEAT = 20;

	public TileEntityFusionPreHeater() {
		super(true);
		tankIn = new FluidTank(10000);
		tankIn2 = new FluidTank(10000);
		tankOut = new FluidTank(10000);
		tankOut.setCanFill(false);
		tankhIn = Helper.getFluidHandlerFromTanks(new FluidTank[]{tankIn, tankIn2}, new Fluid[]{CoreInit.Deuterium.get(), CoreInit.Tritium.get()}, new boolean[]{true, true}, new boolean[]{false, false});
		tankhOut = Helper.getFluidHandlerFromTanks(new FluidTank[]{tankOut}, new Fluid[]{CoreInit.fusionFuel.get()}, new boolean[]{false}, new boolean[]{true});
	}

	private FluidTank tankIn, tankIn2;
	private FluidTank tankOut;
	private EnergyStorage energy = new EnergyStorage(10000);
	private InventoryBasic inv = new InventoryBasic("", false, 1);
	private IItemHandler invRod = new InvWrapper(inv);
	private IFluidHandler tankhIn, tankhOut;
	protected int processTime = -1;
	private int heat;

	@Override
	public void updateEntity(IBlockState state) {
		if (!world.isRemote && getMultiblock(state)) {
			boolean active = false;
			if (energy.getEnergyStored() >= 10 && !inv.getStackInSlot(0).isEmpty() && inv.getStackInSlot(0).getItem() instanceof IFuelRod) {
				if (this.processTime > 0) {
					if (heat < FUEL_USAGE) {
						IFuelRod rod = (IFuelRod) inv.getStackInSlot(0).getItem();
						int heat = rod.getHeat(inv.getStackInSlot(0));
						if (heat <= MAX_HEAT) {
							if (heat >= FUEL_USAGE) {
								inv.setInventorySlotContents(0, rod.useSingle(inv.getStackInSlot(0)));
								this.heat += heat;
							} else {
								double cyclesD = FUEL_USAGE / (double) heat;
								int cycles = MathHelper.ceil(cyclesD);
								if (cycles <= 5) {
									for (int i = 0;i < cycles;i++) {
										if (!(inv.getStackInSlot(0).getItem() instanceof IFuelRod))
											break;
										inv.setInventorySlotContents(0, rod.useSingle(inv.getStackInSlot(0)));
										this.heat += heat;
									}
								}
							}
						}
					}
					if (heat >= FUEL_USAGE) {
						heat -= FUEL_USAGE;
						this.processTime--;
						energy.extractEnergy(50, false);
						active = true;
					}
				} else if (this.processTime == 0) {
					processTime = -1;
					tankOut.fillInternal(fusionFuel, true);
					active = true;
				} else {
					boolean deuterium = Deuterium.isFluidEqual(tankIn.getFluid()) || Deuterium.isFluidEqual(tankIn2.getFluid());
					boolean tritium = Tritium.isFluidEqual(tankIn.getFluid()) || Tritium.isFluidEqual(tankIn2.getFluid());
					if (deuterium && tritium && tankIn.getFluidAmount() >= 5 && tankIn2.getFluidAmount() >= 5 && tankOut.getFluidAmount() <= tankOut.getCapacity()-10) {
						if ((tankOut.getFluidAmount() == 0 || tankOut.getFluid().isFluidEqual(new FluidStack(CoreInit.fusionFuel.get(), 1)))) {
							processTime = 20;
							tankIn.drainInternal(10, true);
							tankIn2.drainInternal(10, true);
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
		energy.readFromNBT(tag);
		tankIn.readFromNBT(tag.getCompoundTag("tankIn"));
		tankIn2.readFromNBT(tag.getCompoundTag("tankIn2"));
		tankOut.readFromNBT(tag.getCompoundTag("tankOut"));
		this.heat = tag.getInteger("heat");
		TomsModUtils.loadAllItems(tag, "inventory", inv);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("p", this.processTime);
		energy.writeToNBT(tag);
		tag.setTag("tankIn", tankIn.writeToNBT(new NBTTagCompound()));
		tag.setTag("tankIn2", tankIn2.writeToNBT(new NBTTagCompound()));
		tag.setTag("tankOut", tankOut.writeToNBT(new NBTTagCompound()));
		TomsModUtils.writeInventory("inventory", tag, inv);
		tag.setInteger("heat", heat);
		return tag;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing, BlockPos from, int id) {
		return id > 0 && (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (id == 10)) || (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (id == 3 || id == 4 || id == 7 || id == 8)) || (capability == EnergyType.ENERGY_HANDLER_CAPABILITY && id == 9);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing, BlockPos from, int id) {
		return (T) (id > 0 ? capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? id == 10 ? invRod : null : capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? id == 3 || id == 7 ? tankhIn : id == 4 || id == 8 ? tankhOut : null : capability == EnergyType.ENERGY_HANDLER_CAPABILITY && id == 9 ? energy.toCapability(true, false, EnergyType.MV) : null : null);
	}

	@Override
	public ItemStack getStack() {
		return new ItemStack(FactoryInit.FusionPreHeater);
	}

	@Override
	public int[] getSlots(int id) {
		return id == 10 ? new int[]{0} : null;
	}

	@Override
	public IInventory getInventory(int id) {
		return id == 10 ? inv : null;
	}
}