package com.tom.api.grid;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IGrid<D,T extends IGrid<D,T>> {
	D getData();
	NBTTagCompound exportToNBT();
	T importFromNBT(NBTTagCompound tag);
	void updateGrid(World world, IGridDevice<T> master);
	void reloadGrid(World world, IGridDevice<T> master);
	void forceUpdateGrid(IBlockAccess world, IGridDevice<T> master);
	List<IGridDevice<T>> getParts();
	IGridDevice<T> getMaster();
	void setData(D data);
	void onForceUpdateDone();
	void setMaster(IGridDevice<T> master);
}
