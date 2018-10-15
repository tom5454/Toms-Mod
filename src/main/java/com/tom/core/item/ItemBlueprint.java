package com.tom.core.item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.tom.api.research.Research;
import com.tom.core.CoreInit;
import com.tom.core.research.ResearchHandler;
import com.tom.util.TomsModUtils;

public class ItemBlueprint extends Item {
	private static Map<String, PredefinedBlueprint> predifinedBlueprints;
	public ItemBlueprint() {
		predifinedBlueprints = new HashMap<>();
		setMaxStackSize(1);
		CoreInit.addReloadableTask(() -> {
			predifinedBlueprints.clear();
			load();
		});
	}
	public static void load(){
		TomsModUtils.parseJson("machine_recipes", "blueprints", TomsModUtils.gson, o -> {
			JsonArray bl = o.getAsJsonArray("blueprints");
			for (Iterator<JsonElement> iterator = bl.iterator();iterator.hasNext();) {
				JsonObject e = iterator.next().getAsJsonObject();
				boolean circ = e.get("circuit").getAsBoolean();
				boolean chip = e.get("chipset").getAsBoolean();
				String unloc = e.get("unlocalized_name").getAsString();
				String id = e.get("id").getAsString();
				predifinedBlueprints.put(id, new PredefinedBlueprint(circ, chip, unloc));
			}
		});
	}
	public boolean isCircuit(ItemStack stack){
		if(!stack.hasTagCompound())return false;
		PredefinedBlueprint b = predifinedBlueprints.get(stack.getTagCompound().getString("id"));
		return b == null ? false : b.isCircuit;
	}
	public boolean isChipset(ItemStack stack){
		if(!stack.hasTagCompound())return false;
		PredefinedBlueprint b = predifinedBlueprints.get(stack.getTagCompound().getString("id"));
		return b == null ? false : b.isChipset;
	}
	private static class PredefinedBlueprint {
		private boolean isCircuit, isChipset;
		private String unlocName;
		public PredefinedBlueprint(boolean isCircuit, boolean isChipset, String unlocName) {
			this.isCircuit = isCircuit;
			this.isChipset = isChipset;
			this.unlocName = unlocName;
		}
	}
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(!stack.hasTagCompound())return;
		PredefinedBlueprint b = predifinedBlueprints.get(stack.getTagCompound().getString("id"));
		if(b == null){
			Research r = ResearchHandler.getResearchByName(stack.getTagCompound().getString("rid"));
			if(r != null)
				tooltip.add(I18n.format("tomsmod.tooltip.blueprint", I18n.format(r.getUnlocalizedName())));
		}else{
			tooltip.add(I18n.format("tomsmod.tooltip.blueprint", TextFormatting.AQUA + I18n.format(b.unlocName)));
		}
	}
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			predifinedBlueprints.keySet().stream().map(ItemBlueprint::getBlueprintFor).forEach(items::add);
			ResearchHandler.getAllResearches().stream().map(ItemBlueprint::getBlueprintFor).forEach(items::add);
		}
	}
	public static ItemStack getBlueprintFor(String predefid){
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("id", predefid);
		ItemStack s = new ItemStack(CoreInit.blueprint);
		s.setTagCompound(tag);
		return s;
	}
	public static ItemStack getBlueprintFor(Research r){
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("rid", r.delegate.name().toString());
		ItemStack s = new ItemStack(CoreInit.blueprint);
		s.setTagCompound(tag);
		return s;
	}
	public static boolean isResearch(ItemStack stack){
		return stack.getTagCompound() != null ? stack.getTagCompound().hasKey("rid") : true;
	}
}
