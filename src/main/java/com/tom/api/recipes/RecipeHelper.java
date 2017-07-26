package com.tom.api.recipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.tom.apis.TMLogger;
import com.tom.config.Config;
import com.tom.core.CoreInit;

public class RecipeHelper {
	public static List<Runnable> recipesToPatch = new ArrayList<>();
	public static final Logger log = LogManager.getLogger("Tom's Mod] [RecipePatcher");
	private static boolean scheduled = false, ran = false;

	public static void addOreDictSmelting(String name, ItemStack output, float xp) {
		List<ItemStack> cL = OreDictionary.getOres(name);
		if (cL != null && cL.size() > 0) {
			for (ItemStack c : cL) {
				GameRegistry.addSmelting(c, output, xp);
			}
		}
	}

	public static void addSmelting(List<ItemStack> input, ItemStack output, float xp) {
		if (input != null && input.size() > 0) {
			for (ItemStack c : input) {
				// CoreInit.log.info("Adding Recipe for " + c + " = " + output);
				if (c != null)
					GameRegistry.addSmelting(c, output, xp);
			}
		}
	}

	public static void addShapelessRecipe(ItemStack output, Object... inputs) {
		GameRegistry.addRecipe(new ShapelessOreRecipe(output, inputs));
	}

	public static void addRecipe(ItemStack output, Object... inputs) {
		GameRegistry.addRecipe(new ShapedOreRecipe(output, inputs));
	}

	public static void removeAllRecipes(ItemStack stack) {
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		Iterator<IRecipe> itr = recipes.iterator();

		while (itr.hasNext()) {
			ItemStack is = itr.next().getRecipeOutput();
			if (ItemStack.areItemsEqual(stack, is))
				itr.remove();
		}
	}

	public static void patchRecipe(ItemStack stack, boolean shaped, Supplier<Object[][]> recipe) {
		RecipeHelper.removeAllRecipes(stack);
		if (shaped)
			for (Object[] o : recipe.get())
				addRecipe(stack, o);
		else
			for (Object[] o : recipe.get())
				addShapelessRecipe(stack, o);
	}

	public static void patchRecipe(String name, boolean shaped, ItemStack stack, Supplier<Object[][]> recipe) {
		if (!CoreInit.hadPostPreInit() && Config.changeRecipe(name)) {
			if (!scheduled) {
				CoreInit.initRunnables.add(RecipeHelper::runRecipePatcher);
				scheduled = true;
			}
			if (ran)
				TMLogger.bigWarn("A mod trying to patch a recipe after the patcher has run!");
			else
				recipesToPatch.add(() -> {
					log.info("Patching " + name + " recipe");
					patchRecipe(stack, shaped, recipe);
				});
		}
	}

	public static void patchShapedRecipe(String name, ItemStack stack, Supplier<Object[][]> recipe) {
		patchRecipe(name, true, stack, recipe);
	}

	public static void patchShapedRecipe(String name, Block block, Supplier<Object[][]> recipe) {
		patchRecipe(name, true, new ItemStack(block), recipe);
	}

	public static void patchShapedRecipe(String name, Item item, Supplier<Object[][]> recipe) {
		patchRecipe(name, true, new ItemStack(item), recipe);
	}

	public static void patchShapelessRecipe(String name, ItemStack stack, Supplier<Object[][]> recipe) {
		patchRecipe(name, false, stack, recipe);
	}

	public static void runRecipePatcher() {
		if (recipesToPatch.size() > 0) {
			log.info("Patching " + recipesToPatch.size() + " vanilla recipes...");
			recipesToPatch.forEach(Runnable::run);
			ran = true;
		}
	}
}
