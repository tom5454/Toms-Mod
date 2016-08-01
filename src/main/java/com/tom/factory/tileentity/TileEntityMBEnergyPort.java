package com.tom.factory.tileentity;

import static com.tom.api.energy.EnergyType.MV;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.energy.EnergyStorage;
import com.tom.api.energy.EnergyType;
import com.tom.api.energy.IEnergyReceiver;
import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.TileEntityMultiblockPartBase;

public class TileEntityMBEnergyPort extends TileEntityMultiblockPartBase implements IEnergyReceiver{
	private int masterX = 0;
	private int masterY = 0;
	private int masterZ = 0;
	private boolean formed = false;
	private boolean hasMaster = false;
	//Bottom, Top, North, South, East, West
	private EnergyStorage energy = new EnergyStorage(120000);
	public boolean powered = false;
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.masterX = tag.getInteger("mX");
		this.masterY = tag.getInteger("mY");
		this.masterZ = tag.getInteger("mZ");
		this.hasMaster = tag.getBoolean("hM");
		this.formed = tag.getBoolean("formed");
		this.energy.readFromNBT(tag.getCompoundTag("Energy"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("mX", this.masterX);
		tag.setInteger("mY", this.masterY);
		tag.setInteger("mZ", this.masterZ);
		tag.setBoolean("hM", this.hasMaster);
		tag.setBoolean("formed", this.formed);
		tag.setTag("Energy", energy.writeToNBT(new NBTTagCompound()));
		return tag;
	}
	@Override
	public boolean canConnectEnergy(EnumFacing from, EnergyType type) {
		return type == MV;
	}

	@Override
	public double receiveEnergy(EnumFacing from, EnergyType type, double maxReceive, boolean simulate) {
		/*int[] energyTable = TomsMathHelper.energyCalculator(this.maxEnergy, this.energy, 5000, maxReceive);
		this.energy = !simulate ? energyTable[1] : this.energy;
		return energyTable[0];*/
		return this.canConnectEnergy(from, type) ? energy.receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public double getEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from, EnergyType type) {
		return this.energy.getMaxEnergyStored();
	}
	public double getEnergyStored(){
		return this.energy.getEnergyStored();
	}
	public double getMaxEnergyStored() {
		return this.energy.getMaxEnergyStored();
	}
	public void setEnergy(double energy){
		this.energy.setEnergyStored(energy);
	}
	public boolean removeEnergy(double energy, boolean simulate){
		boolean remove = this.energy.getEnergyStored() >= energy;
		if(remove && !simulate){
			this.energy.extractEnergy(energy, false);
		}
		return remove;
	}
	public boolean isPowered(){
		return this.energy.getEnergyStored() > 10000;
	}
	@SideOnly(Side.CLIENT)
	public int getEnergyBarScale(int par1){
		return MathHelper.floor_double(this.energy.getEnergyStored() * par1 / 200);
	}
	@Override
	public boolean isPlaceableOnSide() {
		return false;
	}
	@Override
	public MultiblockPartList getPartName() {
		return MultiblockPartList.EnergyPort;
	}
	@Override
	public void formI(int mX, int mY, int mZ) {

	}
	@Override
	public void deFormI(int mX, int mY, int mZ) {

	}
	//	@Override
	//	public void writeToPacket(ByteBuf buf){
	//		buf.writeBoolean(this.formed);
	//		buf.writeBoolean(this.isPowered());
	//	}
	//	@Override
	//	public void readFromPacket(ByteBuf buf){
	//		this.formed = buf.readBoolean();
	//		this.powered = buf.readBoolean();
	//		int xCoord = pos.getX();
	//		int yCoord = pos.getY();
	//		int zCoord = pos.getZ();
	//		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	//	}
	@Override
	public List<EnergyType> getValidEnergyTypes() {
		return MV.getList();
	}
}
