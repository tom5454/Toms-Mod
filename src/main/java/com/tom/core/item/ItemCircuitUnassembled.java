package com.tom.core.item;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;
import com.tom.recipes.ICustomJsonIngerdient;

public class ItemCircuitUnassembled extends Item implements IModelRegisterRequired, ICustomJsonIngerdient {
	public ItemCircuitUnassembled(CreativeTabs tab) {
		setCreativeTab(tab);
		setUnlocalizedName("tm.circuitunassembled");
		setHasSubtypes(true);
	}
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			ItemCircuit.circuitTypes.values().stream().map(e -> e.createStack(this)).forEach(items::add);
		}
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.tm.circuitunassembled." + ItemCircuit.getType(stack);
	}
	@Override
	public void registerModels() {
		for (Entry<Integer, String> item : ItemCircuit.models_ua.entrySet()) {
			CoreInit.registerRender(this, item.getKey(), item.getValue());
		}
	}
	@Override
	public Map<String, Object> serialize(ItemStack stack, boolean serializeCount) {
		String id = !stack.hasTagCompound() ? "invalid" : stack.getTagCompound().getString("id");
		return ItemCircuit.serialize(id, "ua", serializeCount ? stack.getCount() : 1);
	}
}
