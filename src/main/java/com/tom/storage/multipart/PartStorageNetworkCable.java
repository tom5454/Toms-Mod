package com.tom.storage.multipart;

import com.tom.api.grid.IGridDevice;
import com.tom.api.multipart.PartDuct;
import com.tom.storage.StorageInit;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class PartStorageNetworkCable extends PartDuct<StorageNetworkGrid> {

	public PartStorageNetworkCable() {
		super(StorageInit.cable, "tomsmodstorage:tm.cable", 2D/16D,2);
	}

	@Override
	public boolean isValidConnection(EnumFacing side, TileEntity tile) {
		return tile instanceof IGridDevice && ((IGridDevice<?>)tile).getGrid().getClass() == this.grid.getClass();
	}

	@Override
	public void updateEntity() {
		
	}

	@Override
	public StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid();
	}

	@Override
	public int getPropertyValue(EnumFacing side) {
		return connectsM(side) ? 2 : connects(side) || connectsInv(side) ? 1 : 0;
	}

}
