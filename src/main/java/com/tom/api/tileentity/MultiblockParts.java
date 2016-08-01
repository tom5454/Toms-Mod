package com.tom.api.tileentity;

public interface MultiblockParts {
	boolean isPart();
	boolean isPlaceableOnSide();
	void form(int mX, int mY, int mZ);
	void deForm(int mX, int mY, int mZ);
	boolean isFormed();
	MultiblockPartList getPartName();
	void formI(int mX, int mY, int mZ);
	void deFormI(int mX, int mY, int mZ);
}
