package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fluids.FluidStack;

import com.tom.core.CoreInit;
import com.tom.lib.Configs;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.thirdparty.jei.CokeOvenCategory.CokeOvenJEIRecipe;
import com.tom.util.RecipeData;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CokeOvenCategory implements IRecipeCategory<CokeOvenJEIRecipe> {

	@Override
	public String getUid() {
		return JEIConstants.COKE_OVEN;
	}

	@Override
	public String getTitle() {
		return I18n.format("tomsmod.jei.cokeOven");
	}

	@Override
	public String getModName() {
		return Configs.ModName;
	}
	@Nonnull
	private final IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 0, 0, 120, 60);

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CokeOvenJEIRecipe recipe, IIngredients ingredients) {
		int x = 7;
		int y = 26;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, false, x + 55, y);
		int tankY = 0;
		recipeLayout.getFluidStacks().init(0, false, 110, tankY, 16, 58, 10000, true, JEIHandler.tankOverlay);
		recipeLayout.getFluidStacks().setBackground(0, JEIHandler.tankBackground);
		recipeLayout.getItemStacks().set(0, recipe.input1);
		recipeLayout.getItemStacks().set(1, recipe.output);
		recipeLayout.getFluidStacks().set(0, new FluidStack(CoreInit.creosoteOil.get(), recipe.creosote));
		recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
			if (slotIndex == 1) {
				if(Minecraft.getMinecraft().gameSettings.advancedItemTooltips)
					tooltip.add(TextFormatting.GRAY + recipe.id);
			}
		});
	}
	public static class CokeOvenJEIRecipe implements IRecipeWrapper {
		@Nullable
		protected final ItemStack input1;
		@Nonnull
		protected final ItemStack output;
		protected final int time, creosote;
		private final String id;
		public CokeOvenJEIRecipe(ItemStack input1, ItemStack output, int time, int creosote, String id) {
			this.input1 = input1;
			this.output = output;
			this.time = time;
			this.creosote = creosote;
			this.id = id;
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setOutput(VanillaTypes.ITEM, output);
			ingredients.setOutput(VanillaTypes.FLUID, new FluidStack(CoreInit.creosoteOil.get(), creosote));
			ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(new ItemStack[]{input1}));
		}
		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			minecraft.fontRenderer.drawString(I18n.format("tomsmod.jei.time", time), -25, 50, 4210752);
		}
	}
	public static Collection<CokeOvenJEIRecipe> get() {
		List<CokeOvenJEIRecipe> recipes = new ArrayList<>();
		List<RecipeData> recipeList = MachineCraftingHandler.getCokeOvenRecipes();
		for (int i = 0;i < recipeList.size();i++) {
			RecipeData data = recipeList.get(i);
			CokeOvenJEIRecipe cr = new CokeOvenJEIRecipe(data.itemstack0, data.itemstack1, data.processTime, data.energy, data.id);
			recipes.add(cr);
		}
		return recipes;
	}
	@Override
	public void drawExtras(Minecraft minecraft) {
		//tankBackground.draw(minecraft, 95, 6);
	}
}
