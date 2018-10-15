package com.tom.energy.tileentity;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import com.tom.api.block.IItemTile;
import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.api.tileentity.ITMPeripheral;

import com.tom.energy.block.BlockEnergyStorage;

public class TileEntityEnergyStorage extends TileEntityTomsMod implements IEnergyHandler, ITMPeripheral, IItemTile {
	private EnergyType energyType;
	private String name;

	public TileEntityEnergyStorage(EnergyType type, int capacity, double maxTransfer, String name) {
		this.energyType = type;
		this.name = name;
		this.energy = new EnergyStorage(capacity, maxTransfer);
	}

	private EnergyStorage energy;

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == energyType;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return energyType.getList();
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return canConnectEnergy(from, type) && from != getFacing(world.getBlockState(pos)) ? energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type, double maxExtract, boolean simulate) {
		return canConnectEnergy(from, type) ? energy.extractEnergy(maxExtract, simulate) : 0;
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
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		energy.writeToNBT(compound);
		return compound;
	}

	@Override
	public String getType() {
		return name;
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"getEnergyStored", "getMaxEnergyStored"};
	}

	@Override
	public Object[] call(IComputer c, String method, Object[] arguments) throws LuaException {
		if (method.equals("getEnergyStored")) {
			return new Object[]{energy.getEnergyStored()};
		} else if (method.equals("getMaxEnergyStored")) { return new Object[]{energy.getMaxEnergyStored()}; }
		return null;
	}

	@Override
	public void updateEntity(IBlockState state) {
		if (!world.isRemote && energy.hasEnergy()) {
			energyType.pushEnergyTo(world, pos, getFacing(state).getOpposite(), energy, false);
		}
	}

	public EnumFacing getFacing(IBlockState state) {
		return state.getValue(BlockEnergyStorage.FACING);
	}

	public void writeToStackNBT(NBTTagCompound tag) {
		energy.writeToNBT(tag);
	}

	public double getEnergyStored() {
		return energy.getEnergyStored();
	}

	public long getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack stack = new ItemStack(state.getBlock());
		NBTTagCompound tag = new NBTTagCompound();
		writeToStackNBT(tag);
		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setTag("BlockEntityTag", tag);
		drops.add(stack);
	}
}
