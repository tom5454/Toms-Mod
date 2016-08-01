package com.tom.api.tileentity;

public class TileEntityBase extends TileEntityTomsMod {
	@Override
	public void markDirty(){
		super.markDirty();
		//worldObj.markBlockForUpdate(pos);
		//worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}
}
