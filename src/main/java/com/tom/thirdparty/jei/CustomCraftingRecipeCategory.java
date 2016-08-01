package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.tom.apis.RecipeData;
import com.tom.apis.TomsModUtils;
import com.tom.core.research.handler.ResearchHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.CraftingLevel;
import com.tom.thirdparty.jei.CustomCraftingRecipeCategory.CustomCrafingRecipeJEI;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CustomCraftingRecipeCategory implements IRecipeCategory<CustomCrafingRecipeJEI>{
	public static List<CustomCrafingRecipeJEI> get() {
		List<CustomCrafingRecipeJEI> recipes = new ArrayList<CustomCrafingRecipeJEI>();
		List<RecipeData> recipeList = AdvancedCraftingHandler.getRecipes();
		for(int i = 0;i<recipeList.size();i++){
			RecipeData data = recipeList.get(i);
			ItemStack[] array = {data.itemstack1, data.itemstack2, data.itemstack3, data.itemstack4, data.itemstack5, data.itemstack6, data.itemstack7, data.itemstack8, data.itemstack9};
			CustomCrafingRecipeJEI cr = new CustomCrafingRecipeJEI(array, data.itemstack0, data.itemstack10, ResearchHandler.getResearchNames(data.requiredResearches), data.level);
			recipes.add(cr);
		}
		return recipes;
	}
	@Nonnull
	private final IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/advCrafting.png"), 5, 5, 130, 71);
	@Override
	public String getUid() {
		return JEIConstants.CUSTOM_CRAFTING_ID;
	}

	@Override
	public String getTitle() {
		return I18n.format("tomsmod.jei.advCrafing");
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
	public void setRecipe(IRecipeLayout recipeLayout,
			CustomCrafingRecipeJEI recipe) {
		int x = 5;
		int y = 4;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, true, x+18, y);
		recipeLayout.getItemStacks().init(2, true, x+36, y);
		recipeLayout.getItemStacks().init(3, true, x, y+18);
		recipeLayout.getItemStacks().init(4, true, x+18, y+18);
		recipeLayout.getItemStacks().init(5, true, x+36, y+18);
		recipeLayout.getItemStacks().init(6, true, x, y+36);
		recipeLayout.getItemStacks().init(7, true, x+18, y+36);
		recipeLayout.getItemStacks().init(8, true, x+36, y+36);
		recipeLayout.getItemStacks().init(9, false, x+94, y+18);
		recipeLayout.getItemStacks().init(10, false, x+94, y+45);
		for(int i = 0;i<10;i++)setSlot(recipeLayout, recipe, i);
		recipeLayout.getItemStacks().set(9, recipe.output);
		if(recipe.extra != null)recipeLayout.getItemStacks().set(10, recipe.extra);
	}
	private static void setSlot(IRecipeLayout recipeLayout, CustomCrafingRecipeJEI recipe, int index){
		try{
			if(index < recipe.input.length && recipe.input[index] != null){
				IGuiItemStackGroup g = recipeLayout.getItemStacks();
				g.set(index, recipe.input[index]);
			}
		}catch(Exception e){
			e.printStackTrace();
			System.err.println(index);
		}
	}
	public static class CustomCraftingHandler implements IRecipeHandler<CustomCrafingRecipeJEI>{

		@Override
		public Class<CustomCrafingRecipeJEI> getRecipeClass() {
			return CustomCrafingRecipeJEI.class;
		}

		@Override
		public String getRecipeCategoryUid() {
			return JEIConstants.CUSTOM_CRAFTING_ID;
		}

		@Override
		public IRecipeWrapper getRecipeWrapper(CustomCrafingRecipeJEI recipe) {
			return recipe;
		}

		@Override
		public boolean isRecipeValid(CustomCrafingRecipeJEI recipe) {
			return recipe.output != null;
		}

		@Override
		public String getRecipeCategoryUid(CustomCrafingRecipeJEI recipe) {
			return JEIConstants.CUSTOM_CRAFTING_ID;
		}

	}
	public static class CustomCrafingRecipeJEI extends BlankRecipeWrapper{
		@Nonnull
		private final ItemStack[] input;

		@Nonnull
		private final ItemStack output;
		@Nullable
		private final ItemStack extra;
		private final CraftingLevel level;
		@Nonnull
		private final List<String> requiredResearches;

		public CustomCrafingRecipeJEI(@Nonnull ItemStack[] input, @Nonnull ItemStack output, @Nullable ItemStack extra, @Nonnull List<String> requiredResearches, CraftingLevel level){
			this.input = input;
			this.output = output;
			this.extra = extra;
			this.requiredResearches = requiredResearches;
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
			return TomsModUtils.getItemStackList(output,extra);
		}

		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			if(level.isAdvanced()){
				String lvl = I18n.format(level.getName());
				minecraft.fontRendererObj.drawString(I18n.format("tomsmod.jei.recipeLevel", lvl), 0, -5, 4210752);
			}
		}
	}

}
