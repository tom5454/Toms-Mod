package com.tom.defense.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import com.tom.apis.TMLogger;
import com.tom.apis.TomsModUtils;

public class TileEntityForceField extends TileEntity {
	public BlockPos ownerPos;
	public void breakBlock(){
		try{
			if(worldObj != null && !worldObj.isRemote)worldObj.setBlockToAir(pos);
		}catch(Exception e){TMLogger.catching(e, "Exception caught while trying to break a force field block");}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		TomsModUtils.writeBlockPosToNBT(compound, ownerPos);
		return compound;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.ownerPos = TomsModUtils.readBlockPosFromNBT(compound);
	}
	public void update(IBlockAccess worldObj){
		if(ownerPos != null){
			/*TileEntity tile = worldObj.getTileEntity(ownerPos);
			if(tile instanceof TileEntityForceFieldProjector){
				TileEntityForceFieldProjector te = (TileEntityForceFieldProjector) tile;
				if(!te.isValidFieldBlock(pos))
					this.breakBlock();
			}*/
		}else this.breakBlock();
	}
}
