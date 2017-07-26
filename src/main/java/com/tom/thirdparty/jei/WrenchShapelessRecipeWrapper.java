package com.tom.thirdparty.jei;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.tom.recipes.WrenchShapelessCraftingRecipe;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.recipes.BrokenCraftingRecipeException;
import mezz.jei.util.ErrorUtil;

@SuppressWarnings("deprecation")
public class WrenchShapelessRecipeWrapper extends BlankRecipeWrapper implements ICraftingRecipeWrapper {
	private final IJeiHelpers jeiHelpers;
	private final WrenchShapelessCraftingRecipe recipe;

	public WrenchShapelessRecipeWrapper(IJeiHelpers jeiHelpers, WrenchShapelessCraftingRecipe recipe) {
		this.jeiHelpers = jeiHelpers;
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		IStackHelper stackHelper = jeiHelpers.getStackHelper();
		ItemStack recipeOutput = recipe.getRecipeOutput();

		try {
			List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(recipe.getInput());
			ingredients.setInputLists(ItemStack.class, inputs);
			ingredients.setOutput(ItemStack.class, recipeOutput);
		} catch (RuntimeException e) {
			String info = ErrorUtil.getInfoFromBrokenCraftingRecipe(recipe, recipe.getInput(), recipeOutput);
			throw new BrokenCraftingRecipeException(info, e);
		}
	}
}