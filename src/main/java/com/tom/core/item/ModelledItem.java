package com.tom.core.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.client.CustomModelLoader;
import com.tom.client.ModelledItemModel;
import com.tom.core.CoreInit;

public class ModelledItem extends Item implements IModelRegisterRequired {
	private int nextID = 0;
	private static final Map<ResourceLocation, ItemStack> REGISTRY = new HashMap<>();
	public final Map<ResourceLocation, ResourceLocation[][]> OVERRIDES = new HashMap<>();
	public final Map<ResourceLocation, Integer> LOCATION_TO_META = new HashMap<>();

	public ModelledItem() {
		setUnlocalizedName("tm.model");
	}

	@Override
	public void registerModels() {
		CustomModelLoader.addOverride(new ResourceLocation("tomsmodcore", getUnlocalizedName().substring(5)), new ModelledItemModel(this));
	}

	public static void registerModel(ResourceLocation loc) {
		ModelledItem[] items = new ModelledItem[]{CoreInit.modelledItem};
		for (ModelledItem item : items) {
			if (item.nextID < Short.MAX_VALUE - 1) {
				int id = item.nextID++;
				item.LOCATION_TO_META.put(loc, id);
				REGISTRY.put(loc, new ItemStack(item, 1, id));
				break;
			}
		}
	}

	public static void registerModel(ResourceLocation loc, ResourceLocation model, ResourceLocation... textures) {
		((ModelledItem) REGISTRY.get(loc).getItem()).OVERRIDES.put(loc, new ResourceLocation[][]{{model}, textures});
	}
}
