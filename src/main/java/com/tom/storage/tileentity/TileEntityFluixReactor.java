package com.tom.storage.tileentity;

import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import com.tom.api.ITileFluidHandler.Helper;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.Checker.RunnableStorage;
import com.tom.apis.MultiblockBlockChecker;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.SlabState;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.BlockComponents.ComponentVariants;
import com.tom.factory.block.BlockRefinery;

import com.tom.core.tileentity.TileEntityHidden.ILinkableCapabilities;

public class TileEntityFluixReactor extends TileEntityTomsMod implements ILinkableCapabilities, IInventory {
	private static final Object[][] CONFIG = new Object[][]{{'_', TMResource.STEEL.getSlab(SlabState.BOTTOM), 'H', getComponent(ComponentVariants.OUTPUT_HATCH), 'F', getComponent(ComponentVariants.REFINERY_HEATER), 'M', CoreInit.MachineFrameSteel.getDefaultState(), 'E', getComponent(ComponentVariants.ENGINEERING_BLOCK), 'S', TMResource.STEEL.getBlockState(), 'A', TMResource.ALUMINUM.getBlockState(), 'f', CoreInit.steelFence}, {"__@__", "_FFF_", "HFFFH", "_FFF_", "_____"}, // 1
			{"     ", " MMM ", " MAM ", " MMM ", "  f  "}, // 2
			{"     ", " MMM ", " MAM ", " MMM ", "  f  "}, // 3
			{"     ", " MEM ", " EAE ", " MEM ", "  H  "}, // 4
			{"     ", " MEM ", " EAE ", " MEM ", "  f  "}, // 5
			{"     ", " EME ", " MAM ", " EME ", "  H  "}, // 6
			{"     ", " MEM ", " EAE ", " MEM ", "  f  "}, // 7
			{"     ", " SMS ", " SMS ", " SMS ", "  H  "}, // 8
			{"  _  ", "_SSS_", "_S S_", "_SSS_", "  E  "}, // 9
			{"     ", " ___ ", " _S_ ", " ___ ", "     "}, // 10
			{"     ", "     ", "  _  ", "     ", "     "}, // 11
	};

	public TileEntityFluixReactor() {
		tankIn = new FluidTank(50000);
		tankOut1 = new FluidTank(20000);
		tankOut2 = new FluidTank(20000);
		tankOut3 = new FluidTank(20000);
		handlers = new IFluidHandler[]{null, Helper.getFluidHandlerFromTank(tankIn, CoreInit.oil, true, false), null, null, Helper.getFluidHandlerFromTank(tankOut1, CoreInit.fuel, false, true), null, Helper.getFluidHandlerFromTank(tankOut2, CoreInit.lpg, false, true), null, Helper.getFluidHandlerFromTank(tankOut3, CoreInit.kerosene, false, true)};
	}

	/** 2 Fuel, 2 LPG, 1 Kerosene */
	private FluidTank tankIn, tankOut1, tankOut2, tankOut3;
	private IFluidHandler[] handlers;
	private double heat = 0;
	private int burnTime = 0, maxBurnTime = 0;
	public int clientHeat;
	public static final int MAX_TEMP = 1500;
	private InventoryBasic inv = new InventoryBasic("", false, getSizeInventory());
	private static final Map<Character, MultiblockBlockChecker> materialMap = TomsModUtils.createMaterialMap(CONFIG, new ItemStack(FactoryInit.refinery));
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
			if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) { return id < handlers.length ? (T) (handlers[id]) : null; }
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private static IBlockState getComponent(ComponentVariants variant) {
		return FactoryInit.components.getStateFromMeta(variant.ordinal());
	}

	private boolean getMultiblock(IBlockState state) {
		return TomsModUtils.getLayers(CONFIG, materialMap, world, state.getValue(BlockRefinery.FACING), pos, killList);
	}

	@Override
	public void updateEntity(IBlockState state) {
		if (!world.isRemote) {
			if (getMultiblock(state)) {
				if (this.burnTime < 1 && this.getStackInSlot(0) != null && ((pos.getY() > 48 && pos.getY() < 150) || world.getWorldType() == WorldType.FLAT)) {
					ItemStack fss = this.getStackInSlot(0);
					int itemBurnTime = TomsModUtils.getBurnTime(fss);
					if (itemBurnTime > 0) {
						this.maxBurnTime = this.burnTime = itemBurnTime;
						this.decrStackSize(0, 1);
						if (fss.getItem().getContainerItem(fss) != null) {
							ItemStack s = fss.getItem().getContainerItem(fss);
							EnumFacing f = state.getValue(BlockRefinery.FACING);
							EnumFacing facing = f.getOpposite();
							BlockPos invP = pos.offset(facing);
							IInventory inv = TileEntityHopper.getInventoryAtPosition(world, invP.getX(), invP.getY(), invP.getZ());
							if (inv != null)
								s = TileEntityHopper.putStackInInventoryAllSlots(inv, inv, s, facing);
							if (s != null) {
								EntityItem item = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, fss.getItem().getContainerItem(fss));
								item.motionX = facing.getFrontOffsetX() * 0.3;
								item.motionZ = facing.getFrontOffsetZ() * 0.3;
								world.spawnEntity(item);
							}
						}
						heat = Math.min(0.06D + heat, MAX_TEMP);
					} else {
						if (fss != null) {
							EntityItem item = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, fss);
							world.spawnEntity(item);
						}
					}
					this.markDirty();
				} else if (burnTime > 0) {
					burnTime = Math.max(burnTime - 5, 0);
					if (state.getValue(BlockRefinery.STATE) == 1) {
						TomsModUtils.setBlockState(world, pos, state.withProperty(BlockRefinery.STATE, 2), 2);
						this.markDirty();
					}
					double increase = heat > 400 ? heat > 800 ? 0.08D : 0.09D : 0.11D;
					heat = Math.min(increase + heat, MAX_TEMP);
				} else {
					if (state.getValue(BlockRefinery.STATE) == 2) {
						TomsModUtils.setBlockState(world, pos, state.withProperty(BlockRefinery.STATE, 1), 2);
						this.markDirty();
					}
					heat = Math.max(heat - (heat / 500), 20);
					this.maxBurnTime = 0;
				}
				if (!inv.getStackInSlot(0).isEmpty()) {
					inv.setInventorySlotContents(0, inv.getStackInSlot(1));
					inv.setInventorySlotContents(1, inv.getStackInSlot(2));
					inv.setInventorySlotContents(2, inv.getStackInSlot(3));
					inv.setInventorySlotContents(3, ItemStack.EMPTY);
				} else if (inv.getStackInSlot(0).getCount() < inv.getStackInSlot(0).getMaxStackSize()) {
					for (int i = 1;i < getSizeInventory();i++) {
						/*if(stack[i-1] == null){
							stack[i-1] = stack[i];
							stack[i] = null;
						}*/
						if (ItemStack.areItemsEqual(inv.getStackInSlot(0), inv.getStackInSlot(i)) && ItemStack.areItemStackTagsEqual(inv.getStackInSlot(0), inv.getStackInSlot(i))) {
							int space = inv.getStackInSlot(0).getMaxStackSize() - inv.getStackInSlot(0).getCount();
							ItemStack s = decrStackSize(i, space);
							if (!s.isEmpty())
								inv.getStackInSlot(0).setCount(s.getCount());
						}
						/*if(stack[i - 1] != null && stack[i] != null && ItemStack.areItemsEqual(stack[i-1], stack[i]) && ItemStack.areItemStackTagsEqual(stack[i-1], stack[i])){
							int space = stack[i-1].getMaxStackSize() - stack[i-1].stackSize;
							ItemStack s = decrStackSize(i, space);
							if(s != null)stack[i-1].stackSize = s.stackSize;
						}*/
					}
				}
				process();
				if (heat > 900)
					process();
				if (heat > 1498)
					process();
				if (state.getValue(BlockRefinery.STATE) == 0)
					TomsModUtils.setBlockStateWithCondition(world, pos, state, BlockRefinery.STATE, 1);
			} else {
				TomsModUtils.setBlockStateWithCondition(world, pos, state, BlockRefinery.STATE, 0);
				killList.run();
			}
		}
	}

	private void process() {
		if (tankIn.getFluidAmount() >= 5) {
			int t = 2;
			if (tankIn.getFluid() != null && tankIn.getFluid().getFluid() == CoreInit.oil.get() && heat > 500) {
				if ((tankOut1.getFluid() == null || (tankOut1.getFluid().getFluid() == CoreInit.fuel.get() && tankOut1.getFluidAmount() + t <= tankOut1.getCapacity())) && (tankOut2.getFluid() == null || (tankOut2.getFluid().getFluid() == CoreInit.lpg.get() && tankOut2.getFluidAmount() + t <= tankOut2.getCapacity())) && (tankOut3.getFluid() == null || (tankOut3.getFluid().getFluid() == CoreInit.kerosene.get() && tankOut3.getFluidAmount() + (t / 2) <= tankOut3.getCapacity()))) {
					tankIn.drainInternal(5, true);// (heat > 900 ? heat > 1498 ?
													// 15 : 10 : 5
					tankOut1.fillInternal(new FluidStack(CoreInit.fuel.get(), t), true);
					tankOut2.fillInternal(new FluidStack(CoreInit.lpg.get(), t), true);
					tankOut3.fillInternal(new FluidStack(CoreInit.kerosene.get(), t / 2), true);
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagCompound tankTag = new NBTTagCompound();
		tankIn.writeToNBT(tankTag);
		compound.setTag("oil", tankTag);
		tankTag = new NBTTagCompound();
		tankOut2.writeToNBT(tankTag);
		compound.setTag("lpg", tankTag);
		tankTag = new NBTTagCompound();
		tankOut1.writeToNBT(tankTag);
		compound.setTag("fuel", tankTag);
		tankTag = new NBTTagCompound();
		tankOut3.writeToNBT(tankTag);
		compound.setTag("kerosene", tankTag);
		compound.setDouble("heat", heat);
		compound.setTag("Items", TomsModUtils.saveAllItems(inv));
		compound.setInteger("burnTime", burnTime);
		compound.setInteger("burnTimeMax", maxBurnTime);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagCompound tankTag = compound.getCompoundTag("oil");
		tankIn.readFromNBT(tankTag);
		tankTag = compound.getCompoundTag("lpg");
		tankOut2.readFromNBT(tankTag);
		tankTag = compound.getCompoundTag("fuel");
		tankOut1.readFromNBT(tankTag);
		tankTag = compound.getCompoundTag("kerosene");
		tankOut3.readFromNBT(tankTag);
		heat = compound.getDouble("heat");
		TomsModUtils.loadAllItems(compound.getTagList("Items", 10), inv);
		burnTime = compound.getInteger("burnTime");
		maxBurnTime = compound.getInteger("burnTimeMax");
	}

	@Override
	public String getName() {
		return "refinery";
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
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	public double getHeat() {
		return heat;
	}

	public int getBurnTime() {
		return burnTime;
	}

	public int getMaxBurnTime() {
		return maxBurnTime;
	}

	public FluidTank getTankIn() {
		return tankIn;
	}

	public FluidTank getTankOut1() {
		return tankOut1;
	}

	public FluidTank getTankOut2() {
		return tankOut2;
	}

	public FluidTank getTankOut3() {
		return tankOut3;
	}

	public void setBurnTime(int data) {
		this.burnTime = data;
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