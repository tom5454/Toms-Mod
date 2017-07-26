package com.tom.storage.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Function;

import com.tom.apis.EmptyEntry;

public class CacheRegistry {
	private static final Map<Class<? extends ICache<?>>, Entry<ResourceLocation, Function<StorageData, ? extends ICache<?>>>> cacheMap = new HashMap<>();
	private static final String ID_NBT_NAME = "cid";

	public static <T extends ICraftable, C extends ICache<T>> void registerCache(Class<C> clazz, Function<StorageData, C> constructor, ResourceLocation name) {
		cacheMap.put(clazz, new EmptyEntry<ResourceLocation, Function<StorageData, ? extends ICache<?>>>(name, constructor));
	}

	@SuppressWarnings("unchecked")
	public static <T extends ICraftable, C extends ICache<T>> Class<C> getCacheClassFor(T stack) {
		return (Class<C>) stack.getCacheClass();
	}

	public static NetworkCache createNetworkCache(StorageData data) {
		Map<Class<? extends ICache<?>>, ICache<?>> map = new HashMap<>();
		for (Entry<Class<? extends ICache<?>>, Entry<ResourceLocation, Function<StorageData, ? extends ICache<?>>>> i : cacheMap.entrySet()) {
			map.put(i.getKey(), i.getValue().getValue().apply(data));
		}
		return new NetworkCache(map, data);
	}

	public static void writeToNBT(ICraftable c, NBTTagCompound tag) {
		Entry<ResourceLocation, Function<StorageData, ? extends ICache<?>>> entry = cacheMap.get(c.getCacheClass());
		if (entry != null) {
			ResourceLocation id = entry.getKey();
			c.writeObjToNBT(tag);
			tag.setString(ID_NBT_NAME, id.toString());
		}
	}

	public static ICraftable readFromNBT(NBTTagCompound tag) {
		ResourceLocation id = new ResourceLocation(tag.getString(ID_NBT_NAME));
		for (Entry<Class<? extends ICache<?>>, Entry<ResourceLocation, Function<StorageData, ? extends ICache<?>>>> i : cacheMap.entrySet()) {
			if (i.getValue().getKey().equals(id)) { return i.getValue().getValue().apply(null).readObjectFromNBT(tag); }
		}
		return null;
	}

	public static void init() {
		registerCache(InventoryCache.class, new Function<StorageData, InventoryCache>() {
			private InventoryCache nullCache = new InventoryCache(null);

			@Override
			public InventoryCache apply(StorageData t) {
				return t == null ? nullCache : new InventoryCache(t);
			}
		}, new ResourceLocation("tomsmod:itemCache"));
	}
}