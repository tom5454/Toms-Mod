package com.tom.storage.handler;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

public interface ICache<T extends ICraftable> {
	List<T> getStored();

	Class<T> getCraftableClass();

	T readObjectFromNBT(NBTTagCompound tag);
}