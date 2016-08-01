package com.tom.factory.tileentity;

import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.TileEntityMultiblockCasingBase;

public class TileEntityAdvMBCasing extends TileEntityMultiblockCasingBase {

	@Override
	public void formI(int mX, int mY, int mZ) {
		if(!this.formed){
			this.formed = true;
			//this.texture = side;
			this.masterX = mX;
			this.masterY = mY;
			this.masterZ = mZ;
			this.hasMaster = true;
			this.markDirty();
		}
	}

	@Override
	public MultiblockPartList getPartName() {
		return MultiblockPartList.AdvCasing;
	}

	@Override
	public void deForm() {

	}

}
