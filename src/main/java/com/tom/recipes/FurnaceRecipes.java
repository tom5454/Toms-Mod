package com.tom.recipes;

import static com.tom.api.recipes.RecipeHelper.addSmelting;

import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;

public class FurnaceRecipes {
	public static void init(){
		addSmelting(TMResource.IRON.getStackOreDict(Type.INGOT), CraftingMaterial.HOT_IRON.getStackNormal(), 0.3F);
		addSmelting(TMResource.COPPER.getStackOreDict(Type.INGOT), CraftingMaterial.HOT_COPPER.getStackNormal(), 0.3F);
		addSmelting(TMResource.TIN.getStackOreDict(Type.INGOT), CraftingMaterial.HOT_TIN.getStackNormal(), 0.3F);
		addSmelting(CraftingMaterial.REFINED_CLAY.getStackOreDict(), CraftingMaterial.REFINED_BRICK.getStackNormal(), 0.5F);
		addSmelting(CraftingMaterial.RAW_SILICON.getStackOreDict(), CraftingMaterial.SILICON.getStackNormal(8), 0.4F);
		addSmelting(CraftingMaterial.RAW_CICRUIT_BOARD.getStackOreDict(), CraftingMaterial.BASIC_CIRCUIT_PLATE.getStackNormal(), 0.4F);
		addSmelting(CraftingMaterial.BOTTLE_OF_RUBBER.getStackOreDict(), CraftingMaterial.RUBBER.getStackNormal(), 0.3F);
	}
}
