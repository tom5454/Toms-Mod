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

public class ItemCircuitPanelPh extends Item implements IModelRegisterRequired, ICustomJsonIngerdient {

	@Override
	public void registerModels() {
		for (Entry<Integer, String> item : ItemCircuit.models_ph.entrySet()) {
			CoreInit.registerRender(this, item.getKey(), item.getValue());
		}
	}
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			ItemCircuit.panelTypes.keySet().stream().map(e -> new ItemStack(this, 1, e)).forEach(items::add);
		}
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.tm.circuitpanelph." + ItemCircuit.getPType(stack);
	}
	@Override
	public Map<String, Object> serialize(ItemStack stack, boolean serializeCount) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("type", "tomsmodcore:circuitpanel");
		ret.put("id", ItemCircuit.panelTypes.get(stack.getMetadata()));
		if(serializeCount)ret.put("count", stack.getCount());
		ret.put("photoactive", "true");
		return ret;
	}
	@Override
	public String getCustomName(ItemStack stack) {
		return "circpanelph_" + ItemCircuit.panelTypes.get(stack.getMetadata());
	}
}
