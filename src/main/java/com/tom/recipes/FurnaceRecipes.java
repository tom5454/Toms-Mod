package com.tom.recipes;

import static com.tom.api.recipes.RecipeHelper.addSmelting;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.registry.GameRegistry;

import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;

public class FurnaceRecipes {
	public static void init() {
		addSmelting(TMResource.COPPER.getStackOreDict(Type.INGOT), CraftingMaterial.HOT_COPPER.getStackNormal(), 0.3F);
		addSmelting(CraftingMaterial.BOTTLE_OF_RESIN.getStackOreDict(), CraftingMaterial.ROSIN.getStackNormal(), 0.3F);
		addSmelting(CraftingMaterial.REFINED_CLAY.getStackOreDict(), CraftingMaterial.REFINED_BRICK.getStackNormal(), 0.5F);
		addSmelting(CraftingMaterial.RAW_SILICON.getStackOreDict(), CraftingMaterial.SILICON.getStackNormal(8), 0.4F);
		addSmelting(CraftingMaterial.RAW_CICRUIT_BOARD.getStackOreDict(), CraftingMaterial.BASIC_CIRCUIT_PLATE.getStackNormal(), 0.4F);
		addSmelting(CraftingMaterial.BIG_REDSTONE.getStackOreDict(), CraftingMaterial.CHARGED_REDSTONE.getStackNormal(), 0.3F);
		addSmelting(CraftingMaterial.BIG_GLOWSTONE.getStackOreDict(), CraftingMaterial.CHARGED_GLOWSTONE.getStackNormal(), 0.3F);
		addSmelting(CraftingMaterial.BIG_ENDER_PEARL.getStackOreDict(), CraftingMaterial.CHARGED_ENDER.getStackNormal(), 0.3F);
		addSmelting(CraftingMaterial.RAW_CHALK.getStackOreDict(), new ItemStack(CoreInit.chalk), 0.2F);
		GameRegistry.addSmelting(new ItemStack(CoreInit.skyStone, 1, 0), new ItemStack(CoreInit.skyStone, 4, 1), 0.2F);
	}
}
