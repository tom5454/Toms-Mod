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

public class ItemCircuitPanel extends Item implements IModelRegisterRequired, ICustomJsonIngerdient {

	@Override
	public void registerModels() {
		for (Entry<Integer, String> item : ItemCircuit.models_p.entrySet()) {
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
		return "item.tm.circuitpanel." + ItemCircuit.getPType(stack);
	}
	@Override
	public Map<String, Object> serialize(ItemStack stack, boolean serializeCount) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("type", "tomsmodcore:circuitpanel");
		ret.put("id", ItemCircuit.panelTypes.get(stack.getMetadata()));
		if(serializeCount)ret.put("count", stack.getCount());
		return ret;
	}
	@Override
	public String getCustomName(ItemStack stack) {
		return "circpanel_" + ItemCircuit.panelTypes.get(stack.getMetadata());
	}
}
