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
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.thirdparty.jei.BlastFurnaceRecipeCategory.BlastFurnaceRecipeJEI;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.plugins.jei.description.ItemDescriptionRecipeCategory;

public class BlastFurnaceRecipeCategory implements IRecipeCategory<BlastFurnaceRecipeJEI> {

	public static List<BlastFurnaceRecipeJEI> get() {
		List<BlastFurnaceRecipeJEI> recipes = new ArrayList<BlastFurnaceRecipeJEI>();
		List<RecipeData> recipeList = MachineCraftingHandler.getBlastFurnaceRecipes();
		for(int i = 0;i<recipeList.size();i++){
			RecipeData data = recipeList.get(i);
			//ItemStack[] array = {data.itemstack1, data.itemstack2, data.itemstack3, data.itemstack4, data.itemstack5, data.itemstack6, data.itemstack7, data.itemstack8, data.itemstack9};
			BlastFurnaceRecipeJEI cr = new BlastFurnaceRecipeJEI(data.itemstack0, data.itemstack1, data.itemstack2, data.energy, data.processTime);
			recipes.add(cr);
		}
		return recipes;
	}
	@Nonnull
	private final IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 2, 120, 98, 45);
	@Override
	public String getUid() {
		return JEIConstants.BLAST_FURNACE_ID;
	}

	@Override
	public String getTitle() {
		return I18n.format("tomsmod.jei.blastFurnace");
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
	public void setRecipe(IRecipeLayout recipeLayout, BlastFurnaceRecipeJEI recipe) {
		int x = 5;
		int y = 20;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, true, x+18, y);
		recipeLayout.getItemStacks().init(2, false, x+73, y);
		recipeLayout.getItemStacks().set(0, recipe.input1);
		recipeLayout.getItemStacks().set(1, recipe.input2);
		recipeLayout.getItemStacks().set(2, recipe.output);
	}
	public static class BlastFurnaceHandler implements IRecipeHandler<BlastFurnaceRecipeJEI>{

		@Override
		public Class<BlastFurnaceRecipeJEI> getRecipeClass() {
			return BlastFurnaceRecipeJEI.class;
		}

		@Override
		public String getRecipeCategoryUid() {
			return JEIConstants.BLAST_FURNACE_ID;
		}

		@Override
		public IRecipeWrapper getRecipeWrapper(BlastFurnaceRecipeJEI recipe) {
			return recipe;
		}

		@Override
		public boolean isRecipeValid(BlastFurnaceRecipeJEI recipe) {
			return recipe.input1 != null && recipe.output != null;
		}

		@Override
		public String getRecipeCategoryUid(BlastFurnaceRecipeJEI recipe) {
			return JEIConstants.BLAST_FURNACE_ID;
		}

	}
	public static class BlastFurnaceRecipeJEI extends BlankRecipeWrapper{
		@Nullable
		private final ItemStack input1;
		@Nonnull
		private final ItemStack output;
		@Nullable
		private final ItemStack input2;
		private final int heat;
		private final int time;
		public BlastFurnaceRecipeJEI(ItemStack input1, ItemStack input2, ItemStack output, int heat, int time) {
			this.input1 = input1;
			this.output = output;
			this.input2 = input2;
			this.heat = heat;
			this.time = time;
		}
		@Override
		public List<ItemStack> getInputs()
		{
			return TomsModUtils.getItemStackList(input1, input2);
		}

		@Override
		public List<ItemStack> getOutputs()
		{
			return TomsModUtils.getItemStackList(output);
		}
		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			String mode;
			if(heat > 0){
				mode = I18n.format("tomsmod.jei.heatRequired", heat);
			}else
				mode = I18n.format("tomsmod.jei.normalBlastFurnaceRequired");
			List<String> textLines = minecraft.fontRendererObj.listFormattedStringToWidth(mode, ItemDescriptionRecipeCategory.recipeWidth);
			int yPos = 0;
			for (String descriptionLine : textLines) {
				minecraft.fontRendererObj.drawString(descriptionLine, -20, yPos, 4210752);
				yPos += minecraft.fontRendererObj.FONT_HEIGHT + 2;
			}
			minecraft.fontRendererObj.drawString(I18n.format("tomsmod.jei.time", time), -20, 40, 4210752);
		}
	}
}
