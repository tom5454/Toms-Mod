package com.tom.api.grid;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IGridDevice<G extends IGrid<?,G>> {

	boolean isMaster();

	void setMaster(IGridDevice<G> master, int size);

	BlockPos getPos2();

	World getWorld2();

	G getGrid();

	IGridDevice<G> getMaster();

	boolean isConnected(EnumFacing side);

	boolean isValidConnection(EnumFacing side);

	void invalidateGrid();
	/**Used for path finding.*/
	void setSuctionValue(int suction);
	/**Used for path finding.*/
	int getSuctionValue();

	void updateState();

	void setGrid(G newGrid);
}
