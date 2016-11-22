package com.tom.factory.tileentity;

import java.util.List;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.Checker.CheckerPredicate;
import com.tom.apis.Checker.RunnableStorage;
import com.tom.apis.TomsModUtils;
import com.tom.apis.WorldPos;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.SlabState;
import com.tom.core.TMResource.Type;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.BlockComponents.ComponentVariants;
import com.tom.factory.block.PlasticProcessor;
import com.tom.recipes.OreDict;

import com.tom.core.tileentity.TileEntityHidden.ILinkableCapabilities;

public class TileEntityPlasticProcessor extends TileEntityTomsMod implements ILinkableCapabilities, IInventory, IEnergyReceiver {

	private static final Object[][] CONFIG = new Object[][]{{'_', TMResource.STEEL.getSlab(SlabState.BOTTOM),
		'H', getComponent(ComponentVariants.OUTPUT_HATCH) , 'F', getComponent(ComponentVariants.REFINERY_HEATER), 'E', getComponent(ComponentVariants.ENGINEERING_BLOCK), 'C', Blocks.CAULDRON.getDefaultState()},
		{"@F", "H_"}, //1
		{"EC", "EH"}, //2
	};
	private EnergyStorage energy = new EnergyStorage(100000);
	public TileEntityPlasticProcessor() {
		tankKerosene = new FluidTank(10000);
		tankLPG = new FluidTank(10000);
		tankCreosote = new FluidTank(10000);
		tankWater = new FluidTank(20000);
		handlers = new IFluidHandler[]{Helper.getFluidHandlerFromTanks(new FluidTank[]{tankKerosene, tankWater}, new Fluid[]{CoreInit.kerosene, FluidRegistry.WATER}, new boolean[]{true, true}, new boolean[]{false, false}), Helper.getFluidHandlerFromTanks(new FluidTank[]{tankLPG, tankCreosote}, new Fluid[]{CoreInit.lpg, CoreInit.creosoteOil}, new boolean[]{true, true}, new boolean[]{false, false})};
	}
	/**2 Fuel, 2 LPG, 1 Kerosene*/
	private FluidTank tankKerosene, tankLPG, tankCreosote, tankWater;
	private static final FluidStack WATER = new FluidStack(FluidRegistry.WATER, 2000), KEROSENE = new FluidStack(CoreInit.kerosene, 100), LPG = new FluidStack(CoreInit.lpg, 50), CREOSOTE = new FluidStack(CoreInit.creosoteOil, 100);
	private IFluidHandler[] handlers;
	public static final int MAX_PROGRESS = 600;
	public int clientEnergy, progress;
	private ItemStack[] stack = new ItemStack[getSizeInventory()];
	private static final Map<Character, CheckerPredicate<WorldPos>> materialMap = TomsModUtils.createMaterialMap(CONFIG, new ItemStack(FactoryInit.plasticProcessor));
	private static final ItemStack PLASTIC = CraftingMaterial.PLASTIC_SHEET.getStackNormal();
	private RunnableStorage killList = new RunnableStorage(true);

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing, BlockPos from, int id) {
		return id > 0 ? capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && id == 1) : false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing, BlockPos from, int id) {
		if(id > 0){
			if (id == 1 && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
				return this.<T>getInstance(itemHandlerSidedMap, facing, capability);
			}
			if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
				id--;
				return id < handlers.length ? (T) (handlers[id]) : null;
			}
		}
		return null;
	}
	@SuppressWarnings("deprecation")
	private static IBlockState getComponent(ComponentVariants variant){
		return FactoryInit.components.getStateFromMeta(variant.ordinal());
	}
	private boolean getMultiblock(IBlockState state){
		return TomsModUtils.getLayers(CONFIG, materialMap, worldObj, state.getValue(PlasticProcessor.FACING), pos, killList);
	}
	@Override
	public void updateEntity(IBlockState state){
		if(!worldObj.isRemote){
			if(getMultiblock(state)){
				if(energy.extractEnergy(20D, true) == 20D){
					if(progress > 0){
						updateProgress();
					}else if(progress == 0){
						if(stack[2] != null){
							if(TomsModUtils.areItemStacksEqual(stack[2], PLASTIC, true, true, false)){
								stack[2].stackSize += PLASTIC.stackSize;
								progress = -1;
							}
						}else{
							progress = -1;
							stack[2] = PLASTIC.copy();
						}

					}else{
						if(stack[0] != null && stack[1] != null && ((OreDict.isOre(stack[0], CraftingMaterial.RUBBER.getName()) && OreDict.isOre(stack[1], TMResource.COAL.getStackName(Type.DUST)) ) || (OreDict.isOre(stack[1], CraftingMaterial.RUBBER.getName()) && OreDict.isOre(stack[0], TMResource.COAL.getStackName(Type.DUST)) )) && WATER.isFluidStackIdentical(tankWater.drainInternal(WATER, false)) && KEROSENE.isFluidStackIdentical(tankKerosene.drainInternal(KEROSENE, false)) && LPG.isFluidStackIdentical(tankLPG.drainInternal(LPG, false)) && CREOSOTE.isFluidStackIdentical(tankCreosote.drainInternal(CREOSOTE, false))){
							if(stack[2] != null){
								if(TomsModUtils.areItemStacksEqual(stack[2], PLASTIC, true, true, false) && stack[2].stackSize + PLASTIC.stackSize <= PLASTIC.getMaxStackSize() && stack[0].stackSize >= 1 && stack[1].stackSize >= 1){
									progress = MAX_PROGRESS;
								}
							}else{
								progress = MAX_PROGRESS;
							}
						}else{
							progress = -1;
						}
						if(progress > 0){
							decrStackSize(0, 1);
							decrStackSize(1, 1);
							tankWater.drainInternal(WATER, true);
							tankKerosene.drainInternal(KEROSENE, true);
							tankLPG.drainInternal(LPG, true);
							tankCreosote.drainInternal(CREOSOTE, true);
							energy.extractEnergy(5, false);
						}
						TomsModUtils.setBlockStateWithCondition(worldObj, pos, PlasticProcessor.STATE, progress > 0 ? 2 : 1);
					}
				}else{
					TomsModUtils.setBlockStateWithCondition(worldObj, pos, PlasticProcessor.STATE, 1);
				}
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, state, PlasticProcessor.STATE, 0);
				killList.run();
			}
		}
	}
	private void updateProgress(){
		int upgradeC = getSpeedUpgradeCount();
		int p = upgradeC + 1 + (upgradeC / 2);
		progress = Math.max(0, progress - p);
		energy.extractEnergy(1D * p, false);
	}
	public int getSpeedUpgradeCount(){
		int slot = 3;
		return Math.min(slot < 0 ? 0 : stack[slot] != null && stack[slot].getItem() == FactoryInit.speedUpgrade ? stack[slot].stackSize : 0, 4);
	}
	public int getClientEnergyStored() {
		return MathHelper.floor_double(energy.getEnergyStored());
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
		NBTTagList tagList = new NBTTagList();
		for (int i = 0; i < this.stack.length; i++) {
			if (this.stack[i] != null) {
				NBTTagCompound tagCompound1 = new NBTTagCompound();
				tagCompound1.setByte("Slot", (byte) i);
				this.stack[i].writeToNBT(tagCompound1);
				tagList.appendTag(tagCompound1);
			}
		}
		compound.setTag("Items", tagList);
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
		NBTTagList tagList = compound.getTagList("Items", 10);
		this.stack = new ItemStack[this.getSizeInventory()];
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tabCompound1 = tagList.getCompoundTagAt(i);
			byte byte0 = tabCompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < this.stack.length) {
				this.stack[byte0] = ItemStack.loadItemStackFromNBT(tabCompound1);
			}
		}
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
	public ItemStack getStackInSlot(int index) {
		return stack[index];
	}

	@Override
	public ItemStack decrStackSize(int slot, int par2) {
		if (this.stack[slot] != null) {
			ItemStack itemstack;
			if (this.stack[slot].stackSize <= par2) {
				itemstack = this.stack[slot];
				this.stack[slot] = null;
				return itemstack;
			} else {
				itemstack = this.stack[slot].splitStack(par2);

				if (this.stack[slot].stackSize == 0) {
					this.stack[slot] = null;
				}
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack s = stack[index];
		stack[index] = null;
		return s;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.stack[index] = stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUseable(pos, player, worldObj, this);
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
		if(id == 0)progress = value;
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		stack = new ItemStack[getSizeInventory()];
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
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return energy.getMaxEnergyStored();
	}
}
