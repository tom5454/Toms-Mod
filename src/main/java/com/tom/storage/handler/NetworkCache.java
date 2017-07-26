package com.tom.storage.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;

public class NetworkCache {
	public static class CopiedCache<T extends ICraftable> implements ICache<T> {
		private final ICache<T> original;
		private final List<T> storedList;

		private CopiedCache(ICache<T> original) {
			this.original = original;
			storedList = original.getStored();
		}

		@Override
		public List<T> getStored() {
			return storedList;
		}

		@Override
		public Class<T> getCraftableClass() {
			return original.getCraftableClass();
		}

		@Override
		public T readObjectFromNBT(NBTTagCompound tag) {
			return original.readObjectFromNBT(tag);
		}
	}

	private Map<Class<? extends ICache<?>>, ICache<?>> cacheMap;
	private final StorageData data;

	@SuppressWarnings("unchecked")
	public <T extends ICraftable> ICache<T> getCache(Class<? extends ICache<T>> clazz) {
		try {
			return (ICache<T>) cacheMap.get(clazz);
		} catch (ClassCastException e) {
			return null;
		}
	}

	NetworkCache(Map<Class<? extends ICache<?>>, ICache<?>> cacheMap, StorageData data) {
		this.cacheMap = cacheMap;
		this.data = data;
	}

	public StorageData getData() {
		return data;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public NetworkCache createStored() {
		Map<Class<? extends ICache<?>>, ICache<?>> cacheMapOut = new HashMap<>();
		for (Entry<Class<? extends ICache<?>>, ICache<?>> i : cacheMap.entrySet()) {
			cacheMapOut.put(i.getKey(), new NetworkCache.CopiedCache(i.getValue()));
		}
		return new NetworkCache(cacheMapOut, data);
	}
}