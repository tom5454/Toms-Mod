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
		return new int[]{0,2};
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
	}
	@Override
	public void updateEntity(IBlockState state) {
		if(!worldObj.isRemote){
			clientEnergy = MathHelper.ceiling_double_int(energy.getEnergyStored());
			if(stack[1] == null){
				stack[1] = decrStackSize(0, 1);
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, FluidTransposer.ACTIVE, false);
			}else{
				if(energy.extractEnergy(20D, true) == 20D && canRun()){
					if(progress > 0){
						updateProgress();
					}else if(progress == 0){
						ItemStackChecker s = MachineCraftingHandler.getFluidTransposerOutput(stack[1], tank, isExtract);
						if(s != null){
							if(stack[2] != null){
								if(TomsModUtils.areItemStacksEqual(stack[2], s.getStack(), true, true, false) && stack[2].stackSize + s.getStack().stackSize <= s.getStack().getMaxStackSize() && stack[0].stackSize >= s.getExtra()){
									stack[2].stackSize += s.getStack().stackSize;
									progress = -1;
									decrStackSize(1, 1);
									if(isExtract)tank.fill(s.getExtraF(), true);
									else tank.drain(s.getExtra(), true);
								}
							}else{
								progress = -1;
								stack[2] = s.getStack();
								decrStackSize(1, 1);
								if(isExtract)tank.fill(s.getExtraF(), true);
								else tank.drain(s.getExtra(), true);
							}
						}else{
							progress = -1;
						}
					}else{
						ItemStackChecker s = MachineCraftingHandler.getFluidTransposerOutput(stack[1], tank, isExtract);
						if(s != null){
							if(stack[2] != null){
								if(TomsModUtils.areItemStacksEqual(stack[2], s.getStack(), true, true, false) && stack[2].stackSize + s.getStack().stackSize <= s.getStack().getMaxStackSize() && stack[0].stackSize >= s.getExtra()){
									progress = getMaxProgress();
								}
							}else{
								progress = getMaxProgress();
							}
						}
						TomsModUtils.setBlockStateWithCondition(worldObj, pos, FluidTransposer.ACTIVE, progress > 0);
					}
				}else{
					TomsModUtils.setBlockStateWithCondition(worldObj, pos, FluidTransposer.ACTIVE, false);
				}
			}
		}
	}

	private void updateProgress(){
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
		if(id == 0){
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
}
