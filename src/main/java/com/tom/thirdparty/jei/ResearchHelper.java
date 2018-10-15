package com.tom.thirdparty.jei;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import com.tom.api.research.Research;
import com.tom.lib.Configs;

import com.tom.core.item.ItemBlueprint;

import mezz.jei.api.ingredients.IIngredientHelper;

public class ResearchHelper implements IIngredientHelper<Research> {

	@Override
	public List<Research> expandSubtypes(List<Research> ingredients) {
		return ingredients;
	}

	@Override
	public Research getMatch(Iterable<Research> ingredients, Research ingredientToMatch) {
		return ingredientToMatch;
	}

	@Override
	public String getDisplayName(Research ingredient) {
		return I18n.format(ingredient.getUnlocalizedName());
	}

	@Override
	public String getUniqueId(Research ingredient) {
		return ingredient.prefix + ingredient.getName();
	}

	@Override
	public String getWildcardId(Research ingredient) {
		return getUniqueId(ingredient);
	}

	@Override
	public String getModId(Research ingredient) {
		return Configs.Modid;
	}

	@Override
	public String getDisplayModId(Research ingredient) {
		return Configs.ModName + " Research";
	}

	@Override
	public Iterable<Color> getColors(Research ingredient) {
		return Collections.emptyList();
	}

	@Override
	public String getResourceId(Research ingredient) {
		return ingredient.getName();
	}

	@Override
	public Research copyIngredient(Research ingredient) {
		return ingredient;
	}

	@Override
	public String getErrorInfo(Research ingredient) {
		return ingredient.getName();
	}
	@Override
	public ItemStack getCheatItemStack(Research ingredient) {
		return ItemBlueprint.getBlueprintFor(ingredient);
	}
}
