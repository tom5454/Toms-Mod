package com.tom.api.tileentity;

import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.block.BlockMultiblockCasing;

public abstract class TileEntityMultiblockCasingBase extends TileEntityMultiblockPartBase implements IMultiblockCasing{
	//public int texture = 0;
	@Override
	public boolean isPlaceableOnSide() {
		return true;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		//this.texture = tag.getInteger("t");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		//tag.setInteger("t", this.texture);
		return tag;
	}
	//	@Override
	//	public void writeToPacket(ByteBuf buf){
	//		buf.writeBoolean(this.formed);
	//		//buf.writeByte(texture);
	//	}
	//	@Override
	//	public void readFromPacket(ByteBuf buf){
	//		this.formed = buf.readBoolean();
	//		//this.texture = buf.readByte();
	//	}
	@Override
	public void deFormI(int mX, int mY, int mZ) {
		//this.texture = 0;
		BlockMultiblockCasing.setConnection(worldObj, pos, 0);
		this.deForm();
	}
	public abstract void deForm();
	@Override
	public void form(int side, int mX, int mY, int mZ) {
		BlockMultiblockCasing.setConnection(worldObj, pos, side);
		this.formI(mX, mY, mZ);
	}
}
