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

public class ItemCircuitComponent extends Item implements IModelRegisterRequired, ICustomJsonIngerdient {
	public static Map<String, CircuitComponentType> componentTypes = new HashMap<>();
	public ItemCircuitComponent() {
		setHasSubtypes(true);
	}
	public static void reload(){
		componentTypes.clear();
		loadCfg();
	}
	public static void loadCfg(){
		TomsModUtils.parseJson("machine_recipes", "circuit_components", TomsModUtils.gson, map -> {
			JsonArray list = map.getAsJsonArray("components");
			for (JsonElement jsonElement : list) {
				JsonObject c = jsonElement.getAsJsonObject();
				String id = c.get("id").getAsString();
				String unloc = c.get("unlocalized_name").getAsString();
				int size = c.get("meta").getAsInt();
				componentTypes.put(id, new CircuitComponentType(id, unloc, size, c.get("model").getAsString()));
			}
		});
	}
	@Override
	public void registerModels() {
		for (Entry<String, CircuitComponentType> item : componentTypes.entrySet()) {
			CoreInit.registerRender(this, item.getValue().meta, item.getValue().model);
		}
	}
	public static class CircuitComponentType {
		public String id, unloc, model;
		public int meta;
		public CircuitComponentType(String id, String unloc, int meta, String model) {
			this.id = id;
			this.unloc = unloc;
			this.meta = meta;
			this.model = model;
		}
		public ItemStack createStack(Item item){
			ItemStack is = new ItemStack(item);
			is.setItemDamage(meta);
			NBTTagCompound tag = new NBTTagCompound();
			is.setTagCompound(tag);
			tag.setString("id", id);
			return is;
		}
		public ItemStack createStack(Item item, int count){
			ItemStack is = new ItemStack(item, count);
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
			componentTypes.values().stream().map(e -> e.createStack(this)).forEach(items::add);
		}
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.tm.circuitcomp." + getType(stack);
	}
	public static String getType(ItemStack stack){
		if(!stack.hasTagCompound())return "invalid";
		String id = stack.getTagCompound().getString("id");
		CircuitComponentType t = componentTypes.get(id);
		return t == null ? "invalid" : t.unloc;
	}
	@Override
	public Map<String, Object> serialize(ItemStack stack, boolean serializeCount) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("type", "tomsmodcore:circuitcomp");
		String id = !stack.hasTagCompound() ? "invalid" : stack.getTagCompound().getString("id");
		ret.put("id", id);
		if(serializeCount)ret.put("count", stack.getCount());
		return ret;
	}
	@Override
	public String getCustomName(ItemStack stack) {
		String id = !stack.hasTagCompound() ? "invalid" : stack.getTagCompound().getString("id");
		return "circcomp_" + id;
	}
}
