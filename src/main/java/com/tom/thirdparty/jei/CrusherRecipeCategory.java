package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.tom.apis.RecipeData;
import com.tom.apis.TomsModUtils;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.thirdparty.jei.CrusherRecipeCategory.CrusherRecipeJEI;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CrusherRecipeCategory implements IRecipeCategory<CrusherRecipeJEI>{
	public static List<CrusherRecipeJEI> get() {
		List<CrusherRecipeJEI> recipes = new ArrayList<CrusherRecipeJEI>();
		List<RecipeData> recipeList = MachineCraftingHandler.getCrusherRecipes();
		for(int i = 0;i<recipeList.size();i++){
			RecipeData data = recipeList.get(i);
			//ItemStack[] array = {data.itemstack1, data.itemstack2, data.itemstack3, data.itemstack4, data.itemstack5, data.itemstack6, data.itemstack7, data.itemstack8, data.itemstack9};
			CrusherRecipeJEI cr = new CrusherRecipeJEI(data.itemstack0, data.itemstack1);
			recipes.add(cr);
		}
		return recipes;
	}
	@Nonnull
	private final IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 1, 5, 85, 50);
	@Override
	public String getUid() {
		return JEIConstants.CRUSHER_ID;
	}

	@Override
	public String getTitle() {
		return I18n.format("tomsmod.jei.crusher");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {

	}

	@Override
	public void drawAnimations(Minecraft minecraft) {

	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CrusherRecipeJEI recipe) {
		int x = 6;
		int y = 21;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, false, x+55, y);
		recipeLayout.getItemStacks().set(0, recipe.input);
		recipeLayout.getItemStacks().set(1, recipe.output);
	}
	public static class CrusherHandler implements IRecipeHandler<CrusherRecipeJEI>{

		@Override
		public Class<CrusherRecipeJEI> getRecipeClass() {
			return CrusherRecipeJEI.class;
		}

		@Override
		public String getRecipeCategoryUid() {
			return JEIConstants.CRUSHER_ID;
		}

		@Override
		public IRecipeWrapper getRecipeWrapper(CrusherRecipeJEI recipe) {
			return recipe;
		}

		@Override
		public boolean isRecipeValid(CrusherRecipeJEI recipe) {
			return recipe.input != null && recipe.output != null;
		}

		@Override
		public String getRecipeCategoryUid(CrusherRecipeJEI recipe) {
			return JEIConstants.CRUSHER_ID;
		}

	}
	public static class CrusherRecipeJEI extends BlankRecipeWrapper{
		@Nullable
		private final ItemStack input;
		@Nonnull
		private final ItemStack output;
		public CrusherRecipeJEI(ItemStack input, ItemStack output) {
			this.input = input;
			this.output = output;
		}
		@Override
		public List<ItemStack> getInputs()
		{
			return TomsModUtils.getItemStackList(input);
		}

		@Override
		public List<ItemStack> getOutputs()
		{
			return TomsModUtils.getItemStackList(output);
		}
	}
}
