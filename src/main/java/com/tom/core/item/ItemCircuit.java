package com.tom.core.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;
import com.tom.recipes.ICustomJsonIngerdient;
import com.tom.util.TomsModUtils;

public class ItemCircuit extends Item implements IModelRegisterRequired, ICustomJsonIngerdient {
	public static Map<String, CircuitType> circuitTypes = new HashMap<>();
	public static Map<Integer, String> panelTypes = new HashMap<>();
	public static Map<String, Integer> panelNames = new HashMap<>();
	public static Map<Integer, String> models_p = new HashMap<>();
	public static Map<Integer, String> models_raw = new HashMap<>();
	public static Map<Integer, String> models_ua = new HashMap<>();
	public static Map<Integer, String> models_ph = new HashMap<>();
	public static Map<String, Integer> panelSizes = new HashMap<>();
	public ItemCircuit(CreativeTabs tab) {
		setCreativeTab(tab);
		setUnlocalizedName("tm.circuit");
		setHasSubtypes(true);
	}
	public static void reload(){
		circuitTypes.clear();
		models_p.clear();
		loadCfg();
	}
	public static void loadCfg(){
		TomsModUtils.parseJson("machine_recipes", "circuits", TomsModUtils.gson, map -> {
			JsonArray list = map.getAsJsonArray("circuits");
			for (JsonElement jsonElement : list) {
				JsonObject c = jsonElement.getAsJsonObject();
				String id = c.get("id").getAsString();
				String unloc = c.get("unlocalized_name").getAsString();
				int size = c.get("size").getAsInt();
				int meta = c.get("meta").getAsInt();
				int etchingTime = c.get("etchingTime").getAsInt();
				circuitTypes.put(id, new CircuitType(id, unloc, size, c.get("component").getAsString(), meta, c.get("model").getAsString(), etchingTime));
			}
			list = map.getAsJsonArray("models");
			for (JsonElement jsonElement : list) {
				JsonObject c = jsonElement.getAsJsonObject();
				String model = c.get("model").getAsString();
				String modelr = c.get("raw_model").getAsString();
				String modelu = c.get("ua_model").getAsString();
				String modelp = c.get("ph_model").getAsString();
				String name = c.get("name").getAsString();
				int meta = c.get("meta").getAsInt();
				models_p.put(meta, model);
				models_ph.put(meta, modelp);
				models_raw.put(meta, modelr);
				models_ua.put(meta, modelu);
				panelTypes.put(meta, name);
				panelNames.put(name, meta);
				panelSizes.put(name, c.get("size").getAsInt());
			}
		});
	}
	public static class CircuitType {
		public String id, unloc;
		public int size, meta, etchingTime;
		public String component, model;
		public CircuitType(String id, String unloc, int size, String component, int meta, String model, int etchingTime) {
			this.id = id;
			this.unloc = unloc;
			this.size = size;
			this.component = component;
			this.meta = meta;
			this.model = model;
			this.etchingTime = etchingTime;
		}
		public ItemStack createStack(Item item){
			ItemStack is = new ItemStack(item);
			is.setItemDamage(size);
			NBTTagCompound tag = new NBTTagCompound();
			is.setTagCompound(tag);
			tag.setString("id", id);
			return is;
		}
		public ItemStack createCStack(Item item){
			ItemStack is = new ItemStack(item);
			is.setItemDamage(meta);
			NBTTagCompound tag = new NBTTagCompound();
			is.setTagCompound(tag);
			tag.setString("id", id);
			return is;
		}
		public ItemStack createStack(Item item, int c){
			ItemStack is = new ItemStack(item, c);
			is.setItemDamage(size);
			NBTTagCompound tag = new NBTTagCompound();
			is.setTagCompound(tag);
			tag.setString("id", id);
			return is;
		}
		public ItemStack createCStack(Item item, int c){
			ItemStack is = new ItemStack(item, c);
			is.setItemDamage(meta);
			NBTTagCompound tag = new NBTTagCompound();
			is.setTagCompound(tag);
			tag.setString("id", id);
			return is;
		}
	}
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			circuitTypes.values().stream().map(e -> e.createCStack(this)).forEach(items::add);
		}
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.tm.circuit." + getType(stack);
	}
	public static String getType(ItemStack stack){
		if(!stack.hasTagCompound())return "invalid";
		String id = stack.getTagCompound().getString("id");
		CircuitType t = circuitTypes.get(id);
		return t == null ? "invalid" : t.unloc;
	}
	public static CircuitType getCType(ItemStack stack){
		if(!stack.hasTagCompound())return null;
		String id = stack.getTagCompound().getString("id");
		return circuitTypes.get(id);
	}
	@Override
	public void registerModels() {
		for (Entry<String, CircuitType> item : circuitTypes.entrySet()) {
			CoreInit.registerRender(this, item.getValue().meta, item.getValue().model);
		}
	}
	public static String getPType(ItemStack stack) {
		return panelTypes.getOrDefault(stack.getMetadata(), "invalid");
	}
	@Override
	public Map<String, Object> serialize(ItemStack stack, boolean serializeCount) {
		String id = !stack.hasTagCompound() ? "invalid" : stack.getTagCompound().getString("id");
		return serialize(id, "a", serializeCount ? stack.getCount() : 1);
	}
	public static Map<String, Object> serialize(String id, String type, int count){
		System.out.println("Serializing circuit");
		Map<String, Object> ret = new HashMap<>();
		ret.put("type", "tomsmodcore:circuit");
		ret.put("ctype", type);
		ret.put("id", id);
		ret.put("count", count);
		return ret;
	}
}
