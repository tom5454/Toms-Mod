package com.tom.factory.tileentity;

import java.util.List;

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
import com.tom.api.energy.EnergyStorage;
import com.tom.api.inventory.InventorySection;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.Type;
import com.tom.factory.block.BlockCrusher;

public class TileEntityMixer extends TileEntityMachineBase implements ITileFluidHandler{
	private FluidTank tankIn = new FluidTank(10000);
	private FluidTank tankIn2 = new FluidTank(10000);
	private FluidTank tankOut = new FluidTank(10000);
	private EnergyStorage energy = new EnergyStorage(10000, 100);
	private static final int MAX_PROCESS_TIME = 300;
	public int clientEnergy = 0;
	private static final Object[][] RECIPES_NEW = new Object[][]{
		{TomsModUtils.createRecipe(new Object[]{
				new ItemStack(Items.GUNPOWDER, 2), new ItemStack(Items.ROTTEN_FLESH, 8), TMResource.SULFUR.getStackName(Type.DUST), Items.SUGAR}),
			new Object[][]{{new FluidStack(FluidRegistry.WATER, 1000), true, 0}, {new FluidStack(CoreInit.hydrogenChlorine, 500), false, 1}, {new FluidStack(CoreInit.Hydrogen, 1000), true, 2}},
			true,
		},
	};
	public static final Object[][] RECIPES = new Object[RECIPES_NEW.length + TileEntitySteamMixer.RECIPES.length][];
	static{
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
	public void updateEntity() {
		if(!worldObj.isRemote){
			if(energy.extractEnergy(20D, true) == 20D && canRun()){
				if(progress > 0){
					updateProgress();
				}else if(progress == 0){
					findRecipe(true);
					progress = -1;
				}else{
					if(findRecipe(false) > -1){
						progress = getMaxProgress();
					}
					TomsModUtils.setBlockStateWithCondition(worldObj, pos, BlockCrusher.ACTIVE, progress > 0);
				}
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, BlockCrusher.ACTIVE, false);
			}
		}
	}

	public int getClientEnergyStored() {
		return MathHelper.ceiling_double_int(energy.getEnergyStored());
	}

	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}
	private void updateProgress(){
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
		return Helper.getFluidHandlerFromTanksWithPredicate(new FluidTank[]{tankIn, tankIn2, tankOut}, new Object[]{FluidRegistry.WATER, CoreInit.Hydrogen, null}, new boolean[]{true, true, false}, new boolean[]{false, false, true});
	}
	@SuppressWarnings("unchecked")
	private int findRecipe(boolean apply) {
		Object[] obj = TomsModUtils.checkAndConsumeMatch(RECIPES, new InventorySection(this, 0, 4), new Object[]{tankIn, tankOut, tankIn2});
		if((Integer)obj[0] > -1){
			if(apply)TomsModUtils.runAll((List<Runnable>) obj[1]);
			return (Integer) obj[0];
		}
		return -1;
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
		if(tankOut.getFluidAmount() > 0){
			TileEntity tile = worldObj.getTileEntity(pos.offset(side));
			if(tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite())){
				IFluidHandler t = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
				if(t != null){
					int filled = t.fill(tankOut.getFluid(), false);
					if(filled > 0){
						FluidStack drained = tankOut.drain(filled, false);
						if(drained != null && drained.amount > 0){
							int canDrain = Math.min(filled, Math.min(100, drained.amount));
							t.fill(tankOut.drain(canDrain, true), true);
						}
					}
				}
			}
		}
	}
}
