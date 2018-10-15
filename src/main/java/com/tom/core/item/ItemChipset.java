package com.tom.core.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;
import com.tom.recipes.ICustomJsonIngerdient;
import com.tom.util.TomsModUtils;

public class ItemChipset extends Item implements IModelRegisterRequired, ICustomJsonIngerdient {
	public static Map<Integer, ChipsetType> chipsetTypes = new HashMap<>();
	public ItemChipset(CreativeTabs tabTomsModMaterials) {
		setHasSubtypes(true);
		setCreativeTab(tabTomsModMaterials);
		setUnlocalizedName("tm.cchipset");
	}
	public static void reload(){
		chipsetTypes.clear();
		loadCfg();
	}
	public static void loadCfg(){
		TomsModUtils.parseJson("machine_recipes", "chipsets", TomsModUtils.gson, map -> {
			JsonArray list = map.getAsJsonArray("chipsets");
			for (JsonElement jsonElement : list) {
				JsonObject c = jsonElement.getAsJsonObject();
				String unloc = c.get("unlocalized_name").getAsString();
				int meta = c.get("meta").getAsInt();
				chipsetTypes.put(meta, new ChipsetType(unloc, meta, c.get("model").getAsString()));
			}
		});
	}
	public static class ChipsetType {
		public String name, model;
		public int meta;

		public ChipsetType(String name, int meta, String model) {
			this.name = name;
			this.meta = meta;
			this.model = model;
		}

		public ItemStack createCStack(Item item){
			ItemStack is = new ItemStack(item);
			is.setItemDamage(meta);
			return is;
		}
	}

	@Override
	public Map<String, Object> serialize(ItemStack stack, boolean serializeCount) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("type", "tomsmodcore:chipset");
		ret.put("ctype", "chipset");
		ret.put("meta", stack.getMetadata());
		if(serializeCount)ret.put("count", stack.getCount());
		return ret;
	}

	@Override
	public void registerModels() {
		for (Entry<Integer, ChipsetType> item : chipsetTypes.entrySet()) {
			CoreInit.registerRender(this, item.getValue().meta, item.getValue().model);
		}
	}
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			chipsetTypes.values().stream().map(e -> e.createCStack(this)).forEach(items::add);
		}
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.tm.cchipset." + getType(stack);
	}
	public static String getType(ItemStack stack){
		ChipsetType t = chipsetTypes.get(stack.getMetadata());
		return t == null ? "invalid" : t.name;
	}
}
