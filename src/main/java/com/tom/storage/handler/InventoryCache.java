package com.tom.storage.handler;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.inventory.StoredItemStack;

public class InventoryCache implements ICache<StoredItemStack> {
	private final StorageData data;

	public InventoryCache(StorageData data) {
		this.data = data;
	}

	@Override
	public List<StoredItemStack> getStored() {
		return data.storageInv.getStacks(getClass());
	}

	@Override
	public Class<StoredItemStack> getCraftableClass() {
		return StoredItemStack.class;
	}

	@Override
	public StoredItemStack readObjectFromNBT(NBTTagCompound tag) {
		return StoredItemStack.readFromNBT(tag);
	}
}