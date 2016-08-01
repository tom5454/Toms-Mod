package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.HV;
import static com.tom.api.energy.EnergyType.MV;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.tileentity.ICustomMultimeterInformation;
import com.tom.api.tileentity.TileEntityTomsMod;

import com.tom.energy.block.TransformerMH;

public class TileEntityTransformerMHV extends TileEntityTomsMod implements
IEnergyHandler, ICustomMultimeterInformation {
	//private EnergyStorage energy = new EnergyStorage(10000);
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		EnumFacing facing = this.getFacing();
		return (type == HV && from == facing) || (type == MV && from == facing.getOpposite());
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return MV.getList(HV);
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type,
			double maxReceive, boolean simulate) {
		EnumFacing facing = this.getFacing();
		if(type == HV && from == facing && !getMode()){
			/*TileEntity receiver = worldObj.getTileEntity(pos.offset(facing));
			if(receiver instanceof IEnergyReceiver) {
				//System.out.println("send");
				EnumFacing fOut = facing.getOpposite();
				IEnergyReceiver recv = (IEnergyReceiver)receiver;
				if(recv.canConnectEnergy(fOut, LASER)) {
					//System.out.println("send2");
					double energyPushed = recv.receiveEnergy(fOut, LASER, Math.min(100, LASER.convertFrom(HV, maxReceive)), true);
					if(energyPushed > 0) {
						//System.out.println("push");
						this.markDirty();
						return recv.receiveEnergy(fOut, LASER, energyPushed, simulate);
					}
				}
			}*/
			return HV.convertFrom(MV, MV.pushEnergyTo(worldObj, pos, facing, MV.convertFrom(HV, maxReceive), 5000, simulate));
		}else if(type == MV && from == facing.getOpposite() && getMode()){
			/*TileEntity receiver = worldObj.getTileEntity(pos.offset(facing));
			if(receiver instanceof IEnergyReceiver) {
				//System.out.println("send");
				EnumFacing fOut = facing.getOpposite();
				IEnergyReceiver recv = (IEnergyReceiver)receiver;
				if(recv.canConnectEnergy(fOut, HV)) {
					//System.out.println("send2");
					double energyPushed = recv.receiveEnergy(fOut, HV, Math.min(100, HV.convertFrom(LASER, maxReceive)), true);
					if(energyPushed > 0) {
						//System.out.println("push");
						this.markDirty();
						return recv.receiveEnergy(fOut, HV, energyPushed, simulate);
					}
				}
			}else{
				EnumFacing fOut = facing.getOpposite();
				IMultipartContainer container = MultipartHelper.getPartContainer(worldObj, pos.offset(facing));
				if (container == null) {
					return 0;
				}

				if (fOut != null) {
					ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(fOut));
					if (part instanceof IMicroblock.IFaceMicroblock && !((IMicroblock.IFaceMicroblock) part).isFaceHollow()) {
						return 0;
					}
				}

				ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
				try{
					if (part instanceof PartDuct<?>) {
						@SuppressWarnings("unchecked")
						IEnergyStorage recv = ((PartDuct<EnergyGrid>) part).getGrid().getData();
						double energyPushed = recv.receiveEnergy(Math.min(100, HV.convertFrom(LASER, maxReceive)), true);
						if(energyPushed > 0) {
							//System.out.println("push");
							this.markDirty();
							return recv.receiveEnergy(energyPushed, simulate);
						}
					} else {
						return 0;
					}
				}catch (ClassCastException e){
					return 0;
				}
			}*/
			return MV.convertFrom(HV, HV.pushEnergyTo(worldObj, pos, facing.getOpposite(), HV.convertFrom(MV, maxReceive), HV.convertFrom(MV, 5000), simulate));
		}
		return 0;
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type,
			double maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return 0;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		//this.energy.writeToNBT(tag);
		return tag;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		//this.energy.writeToNBT(tag);
	}
	public EnumFacing getFacing(){
		IBlockState state = worldObj.getBlockState(pos);
		return state.getValue(TransformerMH.FACING);
	}
	public boolean getMode(){
		IBlockState state = worldObj.getBlockState(pos);
		return state.getValue(TransformerMH.MODE);
	}
	@Override
	public List<ITextComponent> getInformation(List<ITextComponent> list) {
		list.add(new TextComponentTranslation("tomsMod.chat.step" + (getMode() ? "Up" : "Down")));
		return list;
	}
}
