package com.tom.storage.tileentity;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.grid.GridEnergyStorage;
import com.tom.api.tileentity.ICustomMultimeterInformation;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.config.Config;
import com.tom.storage.block.EnergyAcceptor;
import com.tom.storage.handler.StorageNetworkGrid;
import com.tom.storage.handler.StorageNetworkGrid.IChannelLoadListener;

public class TileEntityEnergyAcceptor extends TileEntityGridDeviceBase<StorageNetworkGrid> implements IEnergyReceiver, ICustomMultimeterInformation, IChannelLoadListener {
	private GridEnergyStorage energy = new GridEnergyStorage(100, 0);
	private EnergyStorage inEnergy = new EnergyStorage(20000, 10000);

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == getType();
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return getType().getList();
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return canConnectEnergy(from, type) ? inEnergy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return inEnergy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return inEnergy.getMaxEnergyStored();
	}

	@Override
	public StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		energy.writeToNBT(compound);
		NBTTagCompound hvTag = new NBTTagCompound();
		inEnergy.writeToNBT(hvTag);
		compound.setTag("in", hvTag);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound);
		inEnergy.readFromNBT(compound.getCompoundTag("in"));
	}

	@Override
	public void updateEntity(IBlockState currentState) {
		if (!world.isRemote) {
			EnergyType type = currentState.getValue(EnergyAcceptor.ENERGY_TYPE);
			grid.getData().addEnergyStorage(energy);
			double rec = type.convertFrom(EnergyType.HV, grid.getData().receiveEnergy(EnergyType.HV.convertFrom(type, inEnergy.extractEnergy(10000, true)) * Config.storageSystemUsage, true) / Config.storageSystemUsage);
			if (rec > 0) {
				grid.getData().receiveEnergy(EnergyType.HV.convertFrom(type, inEnergy.extractEnergy(rec, false)) * Config.storageSystemUsage, false);
			}
		}
	}

	@Override
	public List<ITextComponent> getInformation(List<ITextComponent> list) {
		// if(!worldObj.isRemote)grid.getData().receiveEnergy(1, false);
		list.add(new TextComponentTranslation("tomsMod.chat.energyStored", new TextComponentString("Unit").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)), grid.getData().getEnergyStored(), grid.getData().getMaxEnergyStored()));
		return list;
	}

	@Override
	public void onGridReload() {

	}

	@Override
	public void onGridPostReload() {

	}

	@Override
	public void onPartsUpdate() {
		grid.getData().addEnergyStorage(energy);
	}

	public EnergyType getType() {
		return world.getBlockState(pos).getValue(EnergyAcceptor.ENERGY_TYPE);
	}
}
