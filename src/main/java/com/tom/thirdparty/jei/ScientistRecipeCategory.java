package com.tom.thirdparty.jei;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import com.tom.core.VillageHandler;
import com.tom.core.VillageHandler.Trade;
import com.tom.lib.Configs;
import com.tom.thirdparty.jei.ScientistRecipeCategory.ScientistRecipeJEI;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

public class ScientistRecipeCategory implements IRecipeCategory<ScientistRecipeJEI> {
	public static List<ScientistRecipeJEI> get() {
		return VillageHandler.getTrades().stream().map(ScientistRecipeJEI::new).collect(Collectors.toList());
	}
	public static class ScientistRecipeJEI implements IRecipeWrapper {
		private final Trade trade;

		public ScientistRecipeJEI(Trade trade) {
			this.trade = trade;
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setOutputLists(VanillaTypes.ITEM, Arrays.asList(trade.selll));
			ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(trade.buy1l, trade.buy2l));
		}
	}
	@Nonnull
	private final IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 2, 120, 98, 45);
	@Override
	public String getUid() {
		return JEIConstants.SCIENTIST;
	}
	@Override
	public String getTitle() {
		return I18n.format("tomsmod.jei.scientist");
	}
	@Override
	public String getModName() {
		return Configs.ModName;
	}
	@Override
	public IDrawable getBackground() {
		return background;
	}
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ScientistRecipeJEI recipe, IIngredients ingredients) {
		int x = 5;
		int y = 20;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, true, x + 18, y);
		recipeLayout.getItemStacks().init(2, false, x + 73, y);
		recipeLayout.getItemStacks().set(0, recipe.trade.buy1l);
		recipeLayout.getItemStacks().set(1, recipe.trade.buy2l);
		recipeLayout.getItemStacks().set(2, recipe.trade.selll);
		recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
			int rng;
			if(slotIndex == 0)rng = recipe.trade.in_rng;
			else if(slotIndex == 1)rng = recipe.trade.in2_rng;
			else if(slotIndex == 2)rng = recipe.trade.out_rng;
			else rng = 0;
			if(rng > 0){
				int c = ingredient.getCount();
				tooltip.add("+- " + (rng - c));//\u00B1
			}
		});
	}
}
