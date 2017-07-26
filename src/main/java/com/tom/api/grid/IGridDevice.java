package com.tom.api.grid;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IGridDevice<G extends IGrid<?, G>> extends IGridAccess<G> {

	public static final String GRID_TAG_NAME = "grid";
	public static final String MASTER_NBT_NAME = "isMaster";

	boolean isMaster();

	void setMaster(IGridDevice<G> master, int size);

	BlockPos getPos2();

	World getWorld2();

	@Override
	G getGrid();

	IGridDevice<G> getMaster();

	boolean isConnected(EnumFacing side);

	boolean isValidConnection(EnumFacing side);

	void invalidateGrid();

	/** Used for path finding. */
	void setSuctionValue(int suction);

	/** Used for path finding. */
	int getSuctionValue();

	void updateState();

	void setGrid(G newGrid);

	boolean isValid();

	NBTTagCompound getGridData();

	default List<BlockAccess> next() {
		List<BlockAccess> ret = new ArrayList<>();
		for (EnumFacing d : EnumFacing.VALUES) {
			if (isConnected(d)) {
				ret.add(new BlockAccess(getPos2().offset(d), d.getOpposite()));
			}
		}
		return ret;
	}
}
