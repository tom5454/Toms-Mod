package com.tom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public abstract class TileEntityMultiblockPartBase extends TileEntityBase implements MultiblockParts {
	protected int masterX = 0;
	protected int masterY = 0;
	protected int masterZ = 0;
	protected boolean formed = false;
	protected boolean hasMaster = false;
	@Override
	public boolean isPart() {
		return true;
	}
	public void breakBlock() {
		this.sendToMaster(1);
	}
	private void sendToMaster(int msg){
		if(this.hasMaster){
			TileEntity tilee = worldObj.getTileEntity(new BlockPos(this.masterX, this.masterY, this.masterZ));
			boolean c = Controllers.tileEntityExists(tilee);
			if(c){
				Controllers.send(tilee,msg, pos.getX(), pos.getY(), pos.getZ());
			}
		}
	}
	@Override
	public boolean isFormed(){
		return this.formed;
	}
	public void onNeighborBlockChange(){
		this.sendToMaster(1);
	}
	protected void sendToMaster(byte msg){
		int message = msg;
		if(msg == 1){
			message = 201;
		}else if(msg == 2){
			message = 202;
		}
		this.sendToMaster(message);
	}
	@Override
	public void form(int mX, int mY, int mZ) {
		if(!this.formed){
			this.formed = true;
			this.masterX = mX;
			this.masterY = mY;
			this.masterZ = mZ;
			this.hasMaster = true;
			this.formI(mX, mY, mZ);
			this.markDirty();
		}
	}
	@Override
	public void deForm(int mX, int mY, int mZ) {
		if(this.masterX == mX && this.masterY == mY && this.masterZ == mZ){
			this.formed = false;
			this.hasMaster = false;
			this.deFormI(mX, mY, mZ);
			this.markDirty();
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("mX", this.masterX);
		tag.setInteger("mY", this.masterY);
		tag.setInteger("mZ", this.masterZ);
		tag.setBoolean("hM", this.hasMaster);
		tag.setBoolean("formed", this.formed);
		return tag;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.masterX = tag.getInteger("mX");
		this.masterY = tag.getInteger("mY");
		this.masterZ = tag.getInteger("mZ");
		this.hasMaster = tag.getBoolean("hM");
		this.formed = tag.getBoolean("formed");
	}
	public MultiblockPartSides isPlaceable(){
		if(this.isPlaceableOnSide()){
			return MultiblockPartSides.All;
		}else{
			return MultiblockPartSides.Middle;
		}
	}
}
