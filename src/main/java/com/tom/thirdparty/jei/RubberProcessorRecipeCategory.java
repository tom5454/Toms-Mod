package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import com.tom.core.CoreInit;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.lib.Configs;
import com.tom.thirdparty.jei.RubberProcessorRecipeCategory.RubberProcessorRecipeJEI;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeCategory;

public class RubberProcessorRecipeCategory implements IRecipeCategory<RubberProcessorRecipeJEI> {
	public static List<RubberProcessorRecipeJEI> get() {
		List<RubberProcessorRecipeJEI> recipes = new ArrayList<>();
		recipes.add(new RubberProcessorRecipeJEI());
		return recipes;
	}

	public static class RubberProcessorRecipeJEI extends BlankRecipeWrapper {

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInput(ItemStack.class, CraftingMaterial.VULCANIZING_AGENTS.getStackNormal());
			ingredients.setOutput(ItemStack.class, CraftingMaterial.RUBBER.getStackNormal());
			ingredients.setInput(FluidStack.class, new FluidStack(CoreInit.concentratedResin.get(), 200));
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
		return JEIConstants.RUBBER_PROCESSOR;
	}

	@Override
	public String getTitle() {
		return I18n.format("tile.tm.rubberProcessor.name");
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
		tankBackground.draw(minecraft, -20, -4);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RubberProcessorRecipeJEI recipeWrapper, IIngredients ingredients) {
		int x = 6;
		int y = 21;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, false, x + 55, y);
		recipeLayout.getIngredientsGroup(ItemStack.class).set(0, CraftingMaterial.VULCANIZING_AGENTS.getStackNormal());
		recipeLayout.getIngredientsGroup(ItemStack.class).set(1, CraftingMaterial.RUBBER.getStackNormal());
		recipeLayout.getFluidStacks().init(0, true, -16, 0, 12, 47, 1000, true, tankOverlay);
		recipeLayout.getFluidStacks().set(0, new FluidStack(CoreInit.concentratedResin.get(), 200));
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
