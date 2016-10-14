package com.tom.core.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.tileentity.TileEntityTabletAccessPointBase;
import com.tom.apis.TomsModUtils;

import com.tom.core.block.TabletAccessPoint;

public class TileEntityTabletAccessPoint extends TileEntityTabletAccessPointBase{
	//	public float thickness = 0.125F;
	//	public float width = 0.1875F;
	//	public float height = 0.1875F;
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		//		this.thickness = tag.getFloat("thick");
		//		this.width = tag.getFloat("width");
		//		this.height = tag.getFloat("height");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		//		tag.setFloat("thick", this.thickness);
		//		tag.setFloat("width",this.width);
		//		tag.setFloat("height", this.height);
		return tag;
	}
	//	@Override
	//	public void writeToPacket(NBTTagCompound buf){
	//		//buf.writeBoolean(connected);
	//		buf.writeInt(direction);
	//		buf.writeInt(this.d.ordinal());
	//		buf.writeBoolean(locked);
	//		buf.writeInt(tier);
	//		buf.writeFloat(thickness);
	//		buf.writeFloat(width);
	//		buf.writeFloat(height);
	//	}
	@Override
	public void updateEntity(){
		super.updateEntity();
		IBlockState state = worldObj.getBlockState(pos);
		boolean act = state.getValue(TabletAccessPoint.ACTIVE);
		if(this.connected){
			if(!act) TomsModUtils.setBlockState(worldObj, pos, state.withProperty(TabletAccessPoint.ACTIVE, true));
		}else{
			if(act) TomsModUtils.setBlockState(worldObj, pos, state.withProperty(TabletAccessPoint.ACTIVE, false));
		}
	}

	//	@Override
	//	public void readFromPacket(NBTTagCompound buf){
	//		//this.connected = buf.readBoolean();
	//		this.direction = buf.readInt();
	//		this.d = EnumFacing.values()[buf.readInt()];
	//		this.locked = buf.readBoolean();
	//		this.tier = buf.readInt();
	//		this.thickness = buf.readFloat();
	//		this.width = buf.readFloat();
	//		this.height = buf.readFloat();
	//		int xCoord = pos.getX();
	//		int yCoord = pos.getY();
	//		int zCoord = pos.getZ();
	//		this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	//	}
}
