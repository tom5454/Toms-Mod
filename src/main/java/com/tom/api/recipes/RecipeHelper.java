package com.tom.api.recipes;

import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeHelper {
	public static void addOreDictSmelting(String name, ItemStack output, float xp){
		List<ItemStack> cL = OreDictionary.getOres(name);
		if(cL != null && cL.size() > 0){
			for(ItemStack c : cL){
				GameRegistry.addSmelting(c, output, xp);
			}
		}
	}
	public static void addSmelting(List<ItemStack> input, ItemStack output, float xp){
		if(input != null && input.size() > 0){
			for(ItemStack c : input){
				//CoreInit.log.info("Adding Recipe for " + c + " = " + output);
				if(c != null)GameRegistry.addSmelting(c, output, xp);
			}
		}
	}
	public static void addShapelessRecipe(ItemStack output, Object... inputs) {
		GameRegistry.addRecipe(new ShapelessOreRecipe(output, inputs));
	}
	public static void addRecipe(ItemStack output, Object... inputs){
		GameRegistry.addRecipe(new ShapedOreRecipe(output, inputs));
	}
}
