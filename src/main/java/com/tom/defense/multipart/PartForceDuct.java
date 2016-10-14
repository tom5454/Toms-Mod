package com.tom.defense.multipart;

import static com.tom.api.energy.EnergyType.FORCE;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyConnection;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.multipart.ICustomPartBounds;
import com.tom.api.multipart.PartDuct;
import com.tom.apis.TomsModUtils;
import com.tom.defense.DefenseInit;
import com.tom.energy.multipart.EnergyGrid;

public class PartForceDuct extends PartDuct<EnergyGrid> implements
ICustomPartBounds, IEnergyHandler {
	private final AxisAlignedBB connectionBox;
	public PartForceDuct() {
		super(DefenseInit.forceDuct, "tomsmoddefense:forceDuct", 0.1875, 2);
		double start = 0.5 - 0.25;
		double stop = 0.5 + 0.25;
		this.connectionBox = new AxisAlignedBB(start, 0, start, stop, start, stop);
	}

	@Override
	public boolean isValidConnection(EnumFacing side, TileEntity tile) {
		return tile != null && tile instanceof IEnergyConnection && ((IEnergyConnection)tile).canConnectEnergy(side.getOpposite(), FORCE);
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
					FORCE.pushEnergyTo(worldObj, pos, f.getOpposite(), grid.getData(), false);
				}
			}
		}
	}
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == FORCE && TomsModUtils.occlusionTest(this, BOXES[from.getOpposite().ordinal()]);
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return FORCE.getList();
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
	public AxisAlignedBB getBoxForConnect() {
		return connectionBox;
	}

	@Override
	public int getPropertyValue(EnumFacing side) {
		return connectsInv(side) ? 2 : connects(side) ? 1 : 0;
	}
}
