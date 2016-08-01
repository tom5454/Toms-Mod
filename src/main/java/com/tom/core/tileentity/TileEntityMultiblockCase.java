package com.tom.core.tileentity;

import com.tom.api.tileentity.MultiblockPartList;
import com.tom.api.tileentity.TileEntityMultiblockCasingBase;

public class TileEntityMultiblockCase extends TileEntityMultiblockCasingBase {
	//Bottom, Top, North, South, East, West
	//private boolean[] connected = {false,false,false,false,false,false};
	//Bottom 0, Top 1, North 2, South 3, East 4, West 5
	@Override
	public void formI(int mX, int mY, int mZ){
		if(!this.formed){
			this.formed = true;
			/*for(int i = 0;i<this.connected.length;i++)
				this.connected[i] = false;
			for(int i = 0;i<sides.length;i++)
				this.connected[sides[i]] = true;*/
			//this.texture = side;
			this.masterX = mX;
			this.masterY = mY;
			this.masterZ = mZ;
			this.hasMaster = true;
			this.markDirty();
			//System.out.println(this.texture[0]);
		}
	}
	@Override
	public MultiblockPartList getPartName() {
		return MultiblockPartList.Casing;
	}
	@Override
	public void deForm() {
		
	}
}
