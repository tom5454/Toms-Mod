package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import com.tom.core.CoreInit;
import com.tom.lib.Configs;
import com.tom.thirdparty.jei.RubberBoilerRecipeCategory.RubberBoilerRecipeJEI;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeCategory;

public class RubberBoilerRecipeCategory implements IRecipeCategory<RubberBoilerRecipeJEI> {
	public static List<RubberBoilerRecipeJEI> get() {
		List<RubberBoilerRecipeJEI> recipes = new ArrayList<>();
		recipes.add(new RubberBoilerRecipeJEI(0));
		recipes.add(new RubberBoilerRecipeJEI(1));
		return recipes;
	}

	public static class RubberBoilerRecipeJEI extends BlankRecipeWrapper {
		private int i;

		public RubberBoilerRecipeJEI(int i) {
			this.i = i;
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			if (i == 0) {
				ingredients.setInput(FluidStack.class, new FluidStack(CoreInit.resin.get(), 5));
				ingredients.setOutput(FluidStack.class, new FluidStack(CoreInit.concentratedResin.get(), 1));
			} else {
				ingredients.setInputs(ItemStack.class, OreDictionary.getOres("logRubber"));
				ingredients.setOutput(ItemStack.class, new ItemStack(Items.COAL, 1, 1));
				ingredients.setOutput(FluidStack.class, new FluidStack(CoreInit.resin.get(), 100));
			}
		}

	}

	@Nonnull
	private IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 1, 5, 85, 50);
	@Nonnull
	private final IDrawable tankOverlay = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/resSelect.png"), 102, 124, 12, 47);
	@Nonnull
	private final IDrawable tankBackground = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/resSelect.png"), 78, 120, 20, 55);

	@Override
	public String getUid() {
		return JEIConstants.RUBBER_BOILER;
	}

	@Override
	public String getTitle() {
		return I18n.format("tile.tm.rubberBoiler.name");
	}

	@Override
	public IDrawable getBackground() {
		background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 1, 0, 85, 80);
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		tankBackground.draw(minecraft, -20, -4);
		tankBackground.draw(minecraft, 85, -4);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RubberBoilerRecipeJEI recipeWrapper, IIngredients ingredients) {
		int x = 6;
		int y = 26;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, false, x + 55, y);
		recipeLayout.getIngredientsGroup(ItemStack.class).set(ingredients);
		recipeLayout.getFluidStacks().init(0, true, -16, 0, 12, 47, recipeWrapper.i == 0 ? 10 : 1000, true, tankOverlay);
		recipeLayout.getFluidStacks().init(1, false, 89, 0, 12, 47, recipeWrapper.i == 0 ? 10 : 1000, true, tankOverlay);
		recipeLayout.getFluidStacks().set(ingredients);
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