package com.tom.factory.tileentity;

import java.util.List;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import com.tom.api.ITileFluidHandler.Helper;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.BlockComponents.ComponentVariants;
import com.tom.factory.block.PlasticProcessor;
import com.tom.lib.api.energy.EnergyStorage;
import com.tom.lib.api.energy.EnergyType;
import com.tom.lib.api.energy.IEnergyReceiver;
import com.tom.recipes.OreDict;
import com.tom.util.Checker.RunnableStorage;
import com.tom.util.MultiblockBlockChecker;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.TileEntityHidden.BlockProperties;

public class TileEntityPlasticProcessor extends TileEntityMultiblock implements IInventory, IEnergyReceiver {
	private static final BlockProperties ROTOR = new BlockProperties().setTesrID(0);
	private static final Object[][] CONFIG = new Object[][]{{'_', getComponent(ComponentVariants.MACHINE_BASE), 'H', new Object[]{getComponent(ComponentVariants.OUTPUT_HATCH), TileEntityRefinery.HATCH_PROPERTIES}, 'F', getComponent(ComponentVariants.REFINERY_HEATER), 'E', getComponent(ComponentVariants.ENGINEERING_BLOCK), 'C', new Object[]{Blocks.CAULDRON.getDefaultState(), ROTOR}, 'S', FactoryInit.steelBoiler}, {"@F", "H_"}, // 1
		{"EC", "SH"}, // 2
	};
	private EnergyStorage energy = new EnergyStorage(100000);
	private InventoryBasic inv = new InventoryBasic("", false, getSizeInventory());

	public TileEntityPlasticProcessor() {
		tankKerosene = new FluidTank(10000);
		tankLPG = new FluidTank(10000);
		tankCreosote = new FluidTank(10000);
		tankWater = new FluidTank(20000);
		handlers = new IFluidHandler[]{Helper.getFluidHandlerFromTanks(new FluidTank[]{tankKerosene, tankWater}, new Fluid[]{CoreInit.kerosene.get(), FluidRegistry.WATER}, new boolean[]{true, true}, new boolean[]{false, false}), Helper.getFluidHandlerFromTanks(new FluidTank[]{tankLPG, tankCreosote}, new Fluid[]{CoreInit.lpg.get(), CoreInit.creosoteOil.get()}, new boolean[]{true, true}, new boolean[]{false, false})};
	}

	/** 2 Fuel, 2 LPG, 1 Kerosene */
	private FluidTank tankKerosene, tankLPG, tankCreosote, tankWater;
	private static final FluidStack WATER = new FluidStack(FluidRegistry.WATER, 2000),
			KEROSENE = new FluidStack(CoreInit.kerosene.get(), 100), LPG = new FluidStack(CoreInit.lpg.get(), 50),
			CREOSOTE = new FluidStack(CoreInit.creosoteOil.get(), 100);
	private IFluidHandler[] handlers;
	public static final int MAX_PROGRESS = 600;
	public int clientEnergy, progress = -1;
	private static final Map<Character, MultiblockBlockChecker> materialMap = TomsModUtils.createMaterialMap(CONFIG, new ItemStack(FactoryInit.plasticProcessor));
	private static final ItemStack PLASTIC = CraftingMaterial.PLASTIC_SHEET.getStackNormal();
	private RunnableStorage killList = new RunnableStorage(true);

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing, BlockPos from, int id) {
		return id > 0 ? capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && id == 1) : false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing, BlockPos from, int id) {
		if (id > 0) {
			if (id == 1 && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return this.<T>getInstance(capabilityMap.get(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY), facing, capability); }
			if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
				id--;
				return id < handlers.length ? (T) (handlers[id]) : null;
			}
		}
		return null;
	}

	@Override
	public void updateEntity(IBlockState state) {
		if (!world.isRemote) {
			if (getMultiblock(state)) {
				if (energy.extractEnergy(20D, true) == 20D) {
					if (progress > 0) {
						updateProgress();
					} else if (progress == 0) {
						if (!inv.getStackInSlot(2).isEmpty()) {
							if (TomsModUtils.areItemStacksEqual(inv.getStackInSlot(2), PLASTIC, true, true, false)) {
								inv.getStackInSlot(2).grow(PLASTIC.getCount());
								progress = -1;
							}
						} else {
							progress = -1;
							inv.setInventorySlotContents(2, PLASTIC.copy());
						}

					} else {
						if (!inv.getStackInSlot(0).isEmpty() && !inv.getStackInSlot(1).isEmpty() && ((OreDict.isOre(inv.getStackInSlot(0), CraftingMaterial.RUBBER.getName()) && OreDict.isOre(inv.getStackInSlot(1), TMResource.COAL.getStackName(Type.DUST))) || (OreDict.isOre(inv.getStackInSlot(1), CraftingMaterial.RUBBER.getName()) && OreDict.isOre(inv.getStackInSlot(0), TMResource.COAL.getStackName(Type.DUST)))) && WATER.isFluidStackIdentical(tankWater.drainInternal(WATER, false)) && KEROSENE.isFluidStackIdentical(tankKerosene.drainInternal(KEROSENE, false)) && LPG.isFluidStackIdentical(tankLPG.drainInternal(LPG, false)) && CREOSOTE.isFluidStackIdentical(tankCreosote.drainInternal(CREOSOTE, false))) {
							if (!inv.getStackInSlot(2).isEmpty()) {
								if (TomsModUtils.areItemStacksEqual(inv.getStackInSlot(2), PLASTIC, true, true, false) && inv.getStackInSlot(2).getCount() + PLASTIC.getCount() <= PLASTIC.getMaxStackSize() && inv.getStackInSlot(0).getCount() >= 1 && inv.getStackInSlot(1).getCount() >= 1) {
									progress = MAX_PROGRESS;
								}
							} else {
								progress = MAX_PROGRESS;
							}
						} else {
							progress = -1;
						}
						if (progress > 0) {
							decrStackSize(0, 1);
							decrStackSize(1, 1);
							tankWater.drainInternal(WATER, true);
							tankKerosene.drainInternal(KEROSENE, true);
							tankLPG.drainInternal(LPG, true);
							tankCreosote.drainInternal(CREOSOTE, true);
							energy.extractEnergy(5, false);
						}
						TomsModUtils.setBlockStateWithCondition(world, pos, PlasticProcessor.STATE, progress > 0 ? 2 : 1);
					}
				} else {
					TomsModUtils.setBlockStateWithCondition(world, pos, PlasticProcessor.STATE, 1);
				}
			} else {
				TomsModUtils.setBlockStateWithCondition(world, pos, state, PlasticProcessor.STATE, 0);
				killList.run();
			}
		}
	}

	private void updateProgress() {
		int upgradeC = TomsModUtils.getSpeedUpgradeCount(inv, 3, 4);
		int p = upgradeC + 1 + (upgradeC / 2);
		progress = Math.max(0, progress - p);
		energy.extractEnergy(1D * p, false);
	}

	public int getClientEnergyStored() {
		return MathHelper.floor(energy.getEnergyStored());
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagCompound tankTag = new NBTTagCompound();
		tankKerosene.writeToNBT(tankTag);
		compound.setTag("kerosene", tankTag);
		tankTag = new NBTTagCompound();
		tankCreosote.writeToNBT(tankTag);
		compound.setTag("creosote", tankTag);
		tankTag = new NBTTagCompound();
		tankLPG.writeToNBT(tankTag);
		compound.setTag("lpg", tankTag);
		tankTag = new NBTTagCompound();
		tankWater.writeToNBT(tankTag);
		compound.setTag("water", tankTag);
		compound.setTag("Items", TomsModUtils.saveAllItems(inv));
		energy.writeToNBT(compound);
		compound.setInteger("progress", progress);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagCompound tankTag = compound.getCompoundTag("kerosene");
		tankKerosene.readFromNBT(tankTag);
		tankTag = compound.getCompoundTag("creosote");
		tankCreosote.readFromNBT(tankTag);
		tankTag = compound.getCompoundTag("lpg");
		tankLPG.readFromNBT(tankTag);
		tankTag = compound.getCompoundTag("water");
		tankWater.readFromNBT(tankTag);
		TomsModUtils.loadAllItems(compound.getTagList("Items", 10), inv);
		energy.readFromNBT(compound);
		progress = compound.getInteger("progress");
	}

	@Override
	public String getName() {
		return "plasticProcessor";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 4;
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
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	public FluidTank getTankKerosene() {
		return tankKerosene;
	}

	public FluidTank getTankLPG() {
		return tankLPG;
	}

	public FluidTank getTankCreosote() {
		return tankCreosote;
	}

	public FluidTank getTankWater() {
		return tankWater;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == EnergyType.MV && from == EnumFacing.DOWN;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return EnergyType.MV.getList();
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
	public long getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getMaxEnergyStored();
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

	public boolean isActive() {
		return world.getBlockState(pos).getValue(PlasticProcessor.STATE) == 2;
	}

	@Override
	public Object[][] getConfig() {
		return CONFIG;
	}

	@Override
	public ItemStack getStack() {
		return new ItemStack(FactoryInit.plasticProcessor);
	}

	@Override
	public Map<Character, MultiblockBlockChecker> getMaterialMap() {
		return materialMap;
	}

	@Override
	protected void setMaterialMap(Map<Character, MultiblockBlockChecker> value) {
	}
}
