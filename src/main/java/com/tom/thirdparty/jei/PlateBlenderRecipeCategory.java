package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.tom.apis.RecipeData;
import com.tom.apis.TomsModUtils;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.thirdparty.jei.PlateBlenderRecipeCategory.PlateBlenderRecipeJEI;

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

public class PlateBlenderRecipeCategory implements IRecipeCategory<PlateBlenderRecipeJEI>{
	public static List<PlateBlenderRecipeJEI> get() {
		List<PlateBlenderRecipeJEI> recipes = new ArrayList<PlateBlenderRecipeJEI>();
		List<RecipeData> recipeList = MachineCraftingHandler.getPlateBlenderRecipes();
		for(int i = 0;i<recipeList.size();i++){
			RecipeData data = recipeList.get(i);
			//ItemStack[] array = {data.itemstack1, data.itemstack2, data.itemstack3, data.itemstack4, data.itemstack5, data.itemstack6, data.itemstack7, data.itemstack8, data.itemstack9};
			PlateBlenderRecipeJEI cr = new PlateBlenderRecipeJEI(data.itemstack0, data.itemstack1, data.energy);
			recipes.add(cr);
		}
		return recipes;
	}
	@Nonnull
	private final IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 1, 5, 85, 50);
	@Override
	public String getUid() {
		return JEIConstants.PLATE_BLENDER_ID;
	}

	@Override
	public String getTitle() {
		return I18n.format("tomsmod.jei.plateBlender");
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
	public void setRecipe(IRecipeLayout recipeLayout, PlateBlenderRecipeJEI recipe) {
		int x = 6;
		int y = 21;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, false, x+55, y);
		recipeLayout.getItemStacks().set(0, recipe.input);
		recipeLayout.getItemStacks().set(1, recipe.output);
	}
	public static class PlateBlenderHandler implements IRecipeHandler<PlateBlenderRecipeJEI>{

		@Override
		public Class<PlateBlenderRecipeJEI> getRecipeClass() {
			return PlateBlenderRecipeJEI.class;
		}

		@Override
		public String getRecipeCategoryUid() {
			return JEIConstants.PLATE_BLENDER_ID;
		}

		@Override
		public IRecipeWrapper getRecipeWrapper(PlateBlenderRecipeJEI recipe) {
			return recipe;
		}

		@Override
		public boolean isRecipeValid(PlateBlenderRecipeJEI recipe) {
			return recipe.input != null && recipe.output != null && recipe.level >= 0;
		}

		@Override
		public String getRecipeCategoryUid(PlateBlenderRecipeJEI recipe) {
			return JEIConstants.PLATE_BLENDER_ID;
		}

	}
	public static class PlateBlenderRecipeJEI extends BlankRecipeWrapper{
		@Nullable
		private final ItemStack input;
		@Nonnull
		private final ItemStack output;
		private final int level;
		public PlateBlenderRecipeJEI(ItemStack input, ItemStack output, int level) {
			this.input = input;
			this.output = output;
			this.level = level;
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
		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			minecraft.fontRendererObj.drawString(I18n.format("tomsmod.jei.reguiredLevel", level), 0, 40, 4210752);
		}
	}
}