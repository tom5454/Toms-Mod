package com.tom.storage.tileentity;

import java.util.List;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.grid.GridEnergyStorage;
import com.tom.api.tileentity.ICustomMultimeterInformation;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.storage.multipart.StorageNetworkGrid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class TileEntityEnergyAcceptor extends
TileEntityGridDeviceBase<StorageNetworkGrid> implements IEnergyReceiver, ICustomMultimeterInformation {
	private GridEnergyStorage energy = new GridEnergyStorage(100, 0);
	private EnergyStorage hvEnergy = new EnergyStorage(20000, 10000);
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == EnergyType.HV;
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return EnergyType.HV.getList();
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type,
			double maxReceive, boolean simulate) {
		return canConnectEnergy(from, type) ? hvEnergy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return hvEnergy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return hvEnergy.getMaxEnergyStored();
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
		hvEnergy.writeToNBT(hvTag);
		compound.setTag("hv", hvTag);
		return compound;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound);
		hvEnergy.readFromNBT(compound.getCompoundTag("hv"));
	}
	@Override
	public void updateEntity(IBlockState currentState) {
		if(!worldObj.isRemote){
			grid.getData().addEnergyStorage(energy);
			double rec = grid.getData().receiveEnergy(hvEnergy.extractEnergy(10000, true) * 2, true) / 2;
			if(rec > 0){
				grid.getData().receiveEnergy(hvEnergy.extractEnergy(rec, false) * 2, false);
			}
		}
	}

	@Override
	public List<ITextComponent> getInformation(List<ITextComponent> list) {
		//if(!worldObj.isRemote)grid.getData().receiveEnergy(1, false);
		list.add(new TextComponentTranslation("tomsMod.chat.energyStored",new TextComponentString("Unit").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)), grid.getData().getEnergyStored(), grid.getData().getMaxEnergyStored()));
		return list;
	}
}
