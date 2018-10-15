package com.tom.core.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;
import com.tom.recipes.ICustomJsonIngerdient;

import com.tom.core.item.ItemChipset.ChipsetType;

public class ItemChipsetBase extends Item implements IModelRegisterRequired, ICustomJsonIngerdient {
	public ItemChipsetBase(CreativeTabs tabTomsModMaterials) {
		setHasSubtypes(true);
		setCreativeTab(tabTomsModMaterials);
		setUnlocalizedName("tm.cchipsetbase");
	}
	@Override
	public Map<String, Object> serialize(ItemStack stack, boolean serializeCount) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("type", "tomsmodcore:chipset");
		ret.put("ctype", "base");
		ret.put("meta", stack.getMetadata());
		if(serializeCount)ret.put("count", stack.getCount());
		return ret;
	}

	@Override
	public void registerModels() {
		for (Entry<Integer, ChipsetType> item : ItemChipset.chipsetTypes.entrySet()) {
			CoreInit.registerRender(this, item.getValue().meta, item.getValue().model + "_base");
		}
	}
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			ItemChipset.chipsetTypes.values().stream().map(e -> e.createCStack(this)).forEach(items::add);
		}
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.tm.cchipsetbase." + getType(stack);
	}
	public static String getType(ItemStack stack){
		ChipsetType t = ItemChipset.chipsetTypes.get(stack.getMetadata());
		return t == null ? "invalid" : t.name;
	}
}
