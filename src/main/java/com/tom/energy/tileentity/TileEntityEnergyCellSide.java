package com.tom.energy.tileentity;

import static com.tom.api.energy.EnergyType.HV;

import java.util.List;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyHandler;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.IPeripheralProxyControllable;
import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.TileEntityMultiblockPartBase;
import com.tom.lib.Configs;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TileEntityEnergyCellSide extends TileEntityMultiblockPartBase implements IEnergyHandler, IPeripheralProxyControllable{
	public boolean input = true;
	private final EnergyStorage energy = new EnergyStorage(100000);
	@Override
	public boolean isPlaceableOnSide() {
		return false;
	}

	@Override
	public MultiblockPartList getPartName() {
		return MultiblockPartList.EnergyCellMid;
	}

	@Override
	public void formI(int mX, int mY, int mZ) {
	}

	@Override
	public void deFormI(int mX, int mY, int mZ) {

	}

	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return this.formed || type == HV;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		if(this.formed && this.hasMaster && this.input){
			TileEntityEnergyCellCore te = (TileEntityEnergyCellCore) this.worldObj.getTileEntity(new BlockPos(masterX, masterY, masterZ));
			return te.receiveEnergy(maxReceive, simulate);
		}
		return 0;
	}

	@Override
	public double extractEnergy(EnumFacing from, EnergyType type, double maxExtract, boolean simulate) {
		System.out.println("ex");
		if(this.formed && this.hasMaster && !this.input){
			TileEntityEnergyCellCore te = (TileEntityEnergyCellCore) this.worldObj.getTileEntity(new BlockPos(masterX, masterY, masterZ));
			return te.extractEnergy(maxExtract, simulate);
		}
		return 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		if(this.formed && this.hasMaster){
			TileEntityEnergyCellCore te = (TileEntityEnergyCellCore) this.worldObj.getTileEntity(new BlockPos(masterX, masterY, masterZ));
			return this.formed ? te.getEnergyStored(this.input) : 0;
		}
		return 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return 1000000;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setBoolean("input", this.input);
		return tag;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.input = tag.getBoolean("input");
	}
	/*@Override
	public void writeToPacket(ByteBuf buf){
		buf.writeBoolean(this.input);
	}
	@Override
	public void readFromPacket(ByteBuf buf){
		this.input = buf.readBoolean();
		worldObj.markBlockRangeForRenderUpdate(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
	}*/
	public double getCoreEnergy(){
		if(this.formed && this.hasMaster){
			TileEntityEnergyCellCore te = (TileEntityEnergyCellCore) this.worldObj.getTileEntity(new BlockPos(masterX, masterY, masterZ));
			return te.energyD * 1000000;
		}
		return 0;
	}

	/*@Override*/
	public int getInfoEnergyPerTick() {
		return 0;
	}

	/*@Override*/
	public int getInfoMaxEnergyPerTick() {
		return 1000000;
	}
	/*
	@Override
	public int getInfoEnergyStored() {
		return this.getEnergyStored(null);
	}

	@Override
	public int getInfoMaxEnergyStored() {
		return 1000000;
	}*/

	@Override
	public void updateEntity(){
		if(this.formed && this.hasMaster){
			TileEntityEnergyCellCore te = (TileEntityEnergyCellCore) this.worldObj.getTileEntity(new BlockPos(masterX, masterY, masterZ));
			this.energy.setEnergyStored(te.getEnergyStored(input));
			double energyLast = this.energy.getEnergyStored();
			if(!worldObj.isRemote) {
				TileEntity receiver = getTileEntityReceiver();
				if(receiver instanceof IEnergyReceiver) {
					IEnergyReceiver recv = (IEnergyReceiver)receiver;
					if(recv.canConnectEnergy(getRotation(), EnergyType.HV)) {
						double extracted = energy.extractEnergy(this.getInfoMaxEnergyPerTick(), true);
						double energyPushed = recv.receiveEnergy(getRotation(),EnergyType.HV, extracted, true);

						if(energyPushed > 0) {
							recv.receiveEnergy(getRotation(),EnergyType.HV, energy.extractEnergy(energyPushed, false), false);
						}
					}
				}
			}
			if(this.energy.getEnergyStored() != energyLast && !worldObj.isRemote){
				te.extractEnergy(energyLast - this.energy.getEnergyStored(), false);
			}
		}
	}

	private TileEntity getTileEntityReceiver() {
		int rX = (this.pos.getX() - this.masterX);
		int rY = (this.pos.getY() - this.masterY);
		int rZ = (this.pos.getZ() - this.masterZ);
		int x = this.pos.getX() + rX;
		int y = this.pos.getY() + rY;
		int z = this.pos.getZ() + rZ;
		return worldObj.getTileEntity(new BlockPos(x, y, z));
	}

	private EnumFacing getRotation() {
		int rX = this.pos.getX() - this.masterX;
		int rY = this.pos.getY() - this.masterY;
		int rZ = this.pos.getZ() - this.masterZ;
		if(rY == 1){
			return EnumFacing.UP;
		}
		if(rY == -1){
			return EnumFacing.DOWN;
		}
		if(rX == 1){
			return EnumFacing.NORTH;
		}
		if(rX == -1){
			return EnumFacing.SOUTH;
		}
		if(rZ == 1){
			return EnumFacing.EAST;
		}
		if(rZ == -1){
			return EnumFacing.WEST;
		}
		return null;
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"isFormed","getCoreEnergyStored","getMaxCoreEnergyStored","getEnergyStored","getMaxEnergyStored"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
	InterruptedException {
		if(method == 0){
			return new Object[]{this.formed};
		}else if(method == 1){
			return new Object[]{this.getCoreEnergy()};
		}else if(method == 2){
			return new Object[]{Configs.EnergyCellCoreMax};
		}else if(method == 3){
			return new Object[]{this.getEnergyStored(null, EnergyType.HV)};
		}else if(method == 4){
			return new Object[]{this.getMaxEnergyStored(null, EnergyType.HV)};
		}
		return null;
	}

	@Override
	public String getName() {
		return "BigEnergyCell";
	}

	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return HV.getList();
	}
}
