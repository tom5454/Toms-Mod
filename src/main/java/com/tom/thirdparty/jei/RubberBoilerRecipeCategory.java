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
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

public class RubberBoilerRecipeCategory implements IRecipeCategory<RubberBoilerRecipeJEI> {
	public static List<RubberBoilerRecipeJEI> get() {
		List<RubberBoilerRecipeJEI> recipes = new ArrayList<>();
		recipes.add(new RubberBoilerRecipeJEI(0));
		recipes.add(new RubberBoilerRecipeJEI(1));
		recipes.add(new RubberBoilerRecipeJEI(2));
		return recipes;
	}

	public static class RubberBoilerRecipeJEI implements IRecipeWrapper {
		private int i;

		public RubberBoilerRecipeJEI(int i) {
			this.i = i;
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			if (i == 0) {
				ingredients.setInput(VanillaTypes.FLUID, new FluidStack(CoreInit.resin.get(), 5));
				ingredients.setOutput(VanillaTypes.FLUID, new FluidStack(CoreInit.concentratedResin.get(), 1));
			} else if (i == 1) {
				ingredients.setInputs(VanillaTypes.ITEM, OreDictionary.getOres("logRubber"));
				ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(Items.COAL, 1, 1));
				ingredients.setOutput(VanillaTypes.FLUID, new FluidStack(CoreInit.resin.get(), 100));
			} else if (i == 2) {
				ingredients.setInputs(VanillaTypes.ITEM, OreDictionary.getOres("leavesRubber"));
				ingredients.setOutput(VanillaTypes.FLUID, new FluidStack(CoreInit.resin.get(), 20));
			}
		}

	}

	@Nonnull
	private IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 130, 25, 125, 80);

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
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RubberBoilerRecipeJEI recipeWrapper, IIngredients ingredients) {
		int x = 21;
		int y = 28;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, false, x + 55, y);
		recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).set(ingredients);
		recipeLayout.getFluidStacks().init(0, true, 0, 0, 16, 58, recipeWrapper.i == 0 ? 10 : 1000, true, JEIHandler.tankOverlay);
		recipeLayout.getFluidStacks().init(1, false, 98, 0, 16, 58, recipeWrapper.i == 0 ? 10 : 1000, true, JEIHandler.tankOverlay);
		recipeLayout.getFluidStacks().setBackground(0, JEIHandler.tankBackground);
		recipeLayout.getFluidStacks().setBackground(1, JEIHandler.tankBackground);
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