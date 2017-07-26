package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.tom.apis.RecipeData;
import com.tom.lib.Configs;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.thirdparty.jei.AlloySmelterRecipeCategory.AlloySmelterRecipeJEI;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeCategory;

public class AlloySmelterRecipeCategory implements IRecipeCategory<AlloySmelterRecipeJEI> {
	public static List<AlloySmelterRecipeJEI> get() {
		List<AlloySmelterRecipeJEI> recipes = new ArrayList<>();
		List<RecipeData> recipeList = MachineCraftingHandler.getAlloySmelterRecipes();
		for (int i = 0;i < recipeList.size();i++) {
			RecipeData data = recipeList.get(i);
			// ItemStack[] array = {data.itemstack1, data.itemstack2,
			// data.itemstack3, data.itemstack4, data.itemstack5,
			// data.itemstack6, data.itemstack7, data.itemstack8,
			// data.itemstack9};
			AlloySmelterRecipeJEI cr = new AlloySmelterRecipeJEI(data.itemstack0, data.itemstack1, data.itemstack2);
			recipes.add(cr);
		}
		return recipes;
	}

	@Nonnull
	private final IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 2, 120, 98, 45);

	@Override
	public String getUid() {
		return JEIConstants.ALLOY_SMELTER_ID;
	}

	@Override
	public String getTitle() {
		return I18n.format("tomsmod.jei.alloySmelter");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {

	}

	/*public static class AlloySmelterHandler implements IRecipeHandler<AlloySmelterRecipeJEI>{
	
		@Override
		public Class<AlloySmelterRecipeJEI> getRecipeClass() {
			return AlloySmelterRecipeJEI.class;
		}
	
		@Override
		public IRecipeWrapper getRecipeWrapper(AlloySmelterRecipeJEI recipe) {
			return recipe;
		}
	
		@Override
		public boolean isRecipeValid(AlloySmelterRecipeJEI recipe) {
			return recipe.input1 != null && recipe.input2 != null && recipe.output != null;
		}
	
		@Override
		public String getRecipeCategoryUid(AlloySmelterRecipeJEI recipe) {
			return JEIConstants.ALLOY_SMELTER_ID;
		}
	
	}*/
	public static class AlloySmelterRecipeJEI extends BlankRecipeWrapper {
		@Nullable
		private final ItemStack input1;
		@Nonnull
		private final ItemStack output;
		@Nullable
		private final ItemStack input2;

		public AlloySmelterRecipeJEI(ItemStack input1, ItemStack input2, ItemStack output) {
			this.input1 = input1;
			this.output = output;
			this.input2 = input2;
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setOutput(ItemStack.class, output);
			ingredients.setInputs(ItemStack.class, Arrays.asList(new ItemStack[]{input1, input2}));
		}
	}

	@Override
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, AlloySmelterRecipeJEI recipe, IIngredients ingredients) {
		int x = 5;
		int y = 20;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, true, x + 18, y);
		recipeLayout.getItemStacks().init(2, false, x + 73, y);
		recipeLayout.getItemStacks().set(0, recipe.input1);
		recipeLayout.getItemStacks().set(1, recipe.input2);
		recipeLayout.getItemStacks().set(2, recipe.output);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}

	@Override
	public String getModName() {
		return Configs.ModName;
	}
}