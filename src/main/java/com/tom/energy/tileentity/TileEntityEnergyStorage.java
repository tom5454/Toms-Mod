package com.tom.energy.tileentity;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fml.common.Optional;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.lib.Configs;

import com.tom.energy.block.BlockEnergyStorage;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = Configs.COMPUTERCRAFT)
public class TileEntityEnergyStorage extends TileEntityTomsMod implements IEnergyHandler, IPeripheral {
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
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
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
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public String getType() {
		return name;
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public String[] getMethodNames() {
		return new String[]{"getEnergyStored", "getMaxEnergyStored"};
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		if (method == 0) {
			return new Object[]{energy.getEnergyStored()};
		} else if (method == 1) { return new Object[]{energy.getMaxEnergyStored()}; }
		return null;
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public void attach(IComputerAccess computer) {
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public void detach(IComputerAccess computer) {
	}

	@Override
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	public boolean equals(IPeripheral other) {
		return other == this;
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

	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}
}
