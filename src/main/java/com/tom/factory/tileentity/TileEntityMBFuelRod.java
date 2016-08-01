package com.tom.factory.tileentity;

import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.MultiblockPartSides;
import com.tom.api.tileentity.TileEntityMultiblockPartBase;

public class TileEntityMBFuelRod extends TileEntityMultiblockPartBase {

	@Override
	public boolean isPlaceableOnSide() {
		return false;
	}

	@Override
	public MultiblockPartList getPartName() {
		return MultiblockPartList.FuelRod;
	}

	@Override
	public void formI(int mX, int mY, int mZ) {
		
	}

	@Override
	public void deFormI(int mX, int mY, int mZ) {
		
	}
	@Override
	public MultiblockPartSides isPlaceable(){
		return MultiblockPartSides.Top;
	}
}
