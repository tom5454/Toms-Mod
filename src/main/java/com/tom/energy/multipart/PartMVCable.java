package com.tom.energy.multipart;

import static com.tom.api.energy.EnergyType.MV;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.energy.IEnergyStorageHandler;
import com.tom.api.multipart.PartDuct;
import com.tom.apis.TomsModUtils;
import com.tom.energy.EnergyInit;

public class PartMVCable extends PartDuct<EnergyGrid>implements IEnergyHandler{

	public PartMVCable() {
		super(EnergyInit.mvCable, "tomsmodenergy:cableMv",0.1875, 1);
	}

	@Override
	public boolean isValidConnection(EnumFacing side, TileEntity tile) {
		if(tile != null){
			IEnergyStorageHandler cap = tile.getCapability(EnergyType.ENERGY_HANDLER_CAPABILITY, side.getOpposite());
			return tile.hasCapability(EnergyType.ENERGY_HANDLER_CAPABILITY, side.getOpposite()) && cap != null && cap.canConnectEnergy(MV);
		}else return false;
	}

	@Override
	public void updateEntity() {
		if(worldObj.isRemote)return;
		if(this.grid.getData().getEnergyStored() > 0){
			for(EnumFacing f : EnumFacing.VALUES){
				TileEntity receiver = worldObj.getTileEntity(pos.offset(f));
				/*if(receiver instanceof IEnergyReceiver) {
					//System.out.println("send");
					EnumFacing fOut = f.getOpposite();
					IEnergyReceiver recv = (IEnergyReceiver)receiver;
					if(recv.canConnectEnergy(fOut, HV)) {
						//System.out.println("send2");
						double energyPushed = recv.receiveEnergy(fOut, HV, grid.getData().getEnergyStored(), true);
						if(energyPushed > 0) {
							//System.out.println("push");
							this.markDirty();
							this.grid.getData().extractEnergy(recv.receiveEnergy(fOut, HV, energyPushed, false),false);
						}
					}
				}*/
				if(this.isValidConnection(f, receiver)){
					MV.pushEnergyTo(worldObj, pos, f.getOpposite(), grid.getData(), false);
				}
			}
		}
	}
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == MV && TomsModUtils.occlusionTest(this, BOXES[from.getOpposite().ordinal()]);
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return MV.getList();
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type,
			double maxReceive, boolean simulate) {
		return this.canConnectEnergy(from, type) ? grid.getData().receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type,
			double maxExtract, boolean simulate) {
		return this.canConnectEnergy(from, type) ? grid.getData().extractEnergy(maxExtract, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return this.canConnectEnergy(from, type) ? grid.getData().getEnergyStored() : 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return this.canConnectEnergy(from, type) ? grid.getData().getMaxEnergyStored() : 0;
	}

	@Override
	public EnergyGrid constructGrid() {
		return new EnergyGrid();
	}

	@Override
	public int getPropertyValue(EnumFacing side) {
		return connects(side) || connectsInv(side) ? 1 : 0;
	}
}
