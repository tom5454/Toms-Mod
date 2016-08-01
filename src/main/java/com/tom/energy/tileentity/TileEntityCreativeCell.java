package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.LASER;

import com.tom.api.energy.EnergyType;

import net.minecraft.util.EnumFacing;

public class TileEntityCreativeCell extends TileEntityEnergyCellBase {

	public TileEntityCreativeCell() {
		super(1, 0xFF0000);
	}
	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		return 0;
	}
	@Override
	public double extractEnergy(EnumFacing from, EnergyType type, double maxExtract, boolean simulate) {
		return canConnectEnergy(from, type) ? maxExtract : 0;
	}
	@Override
	public void updateEntity(){
		if(worldObj.isRemote){
		}else{
			//worldObj.markBlockForUpdate(pos);
			//System.out.println(" "+outputSides);
			for(EnumFacing f : EnumFacing.VALUES){
				if(contains(f)){
					//TileEntity receiver = worldObj.getTileEntity(pos.offset(f));
					//if(receiver instanceof IEnergyReceiver) {
					/*//System.out.println("send");
						IEnergyReceiver recv = (IEnergyReceiver)receiver;
						EnumFacing fOut = f.getOpposite();
						if(recv.canConnectEnergy(fOut, LASER)) {
							//System.out.println("send2");
							double energyPushed = recv.receiveEnergy(fOut, LASER, Math.min(energy.getMaxExtract(), energy.getEnergyStored()), true);
							if(energyPushed > 0) {
								//System.out.println("push");
								this.markDirty();
								worldObj.markBlockForUpdate(pos);
								this.energy.extractEnergy(recv.receiveEnergy(fOut, LASER, energyPushed, false),false);
							}
						}*/
					LASER.pushEnergyTo(worldObj, pos, f.getOpposite(), 10000, 10000, false);
					//}
				}
			}
		}
	}
	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return canConnectEnergy(from, type) ? -1 : 0;
	}
}
