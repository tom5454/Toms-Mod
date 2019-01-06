package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import com.tom.lib.Configs;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.thirdparty.jei.WireMillRecipeCategory.WireMillRecipeJEI;
import com.tom.util.RecipeData;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

public class WireMillRecipeCategory implements IRecipeCategory<WireMillRecipeJEI> {
	public static List<WireMillRecipeJEI> get() {
		List<WireMillRecipeJEI> recipes = new ArrayList<>();
		List<RecipeData> recipeList = MachineCraftingHandler.getWireMillRecipes();
		for (int i = 0;i < recipeList.size();i++) {
			RecipeData data = recipeList.get(i);
			// ItemStack[] array = {data.itemstack1, data.itemstack2,
			// data.itemstack3, data.itemstack4, data.itemstack5,
			// data.itemstack6, data.itemstack7, data.itemstack8,
			// data.itemstack9};
			WireMillRecipeJEI cr = new WireMillRecipeJEI(data.itemstack0, data.itemstack1, data.energy, data.id);
			recipes.add(cr);
		}
		return recipes;
	}

	@Nonnull
	private final IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 1, 5, 85, 50);

	@Override
	public String getUid() {
		return JEIConstants.WIREMILL;
	}

	@Override
	public String getTitle() {
		return I18n.format("tomsmod.jei.wiremill");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {

	}

	@Override
	public String getModName() {
		return Configs.ModName;
	}

	public static class WireMillRecipeJEI implements IRecipeWrapper {
		@Nullable
		private final ItemStack input;
		@Nonnull
		private final ItemStack output;
		private final int level;
		private final String id;

		public WireMillRecipeJEI(ItemStack input, ItemStack output, int level, String id) {
			this.input = input;
			this.output = output;
			this.level = level;
			this.id = id;
		}

		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			minecraft.fontRenderer.drawString(I18n.format("tomsmod.jei.reguiredLevel", level), 0, 40, 4210752);
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInput(VanillaTypes.ITEM, input);
			ingredients.setOutput(VanillaTypes.ITEM, output);
		}
	}

	@Override
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, WireMillRecipeJEI recipeWrapper, IIngredients ingredients) {
		int x = 6;
		int y = 21;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, false, x + 55, y);
		recipeLayout.getItemStacks().set(0, recipeWrapper.input);
		recipeLayout.getItemStacks().set(1, recipeWrapper.output);
		recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
			if (slotIndex == 1) {
				if(Minecraft.getMinecraft().gameSettings.advancedItemTooltips)
					tooltip.add(TextFormatting.GRAY + recipeWrapper.id);
			}
		});
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}
}
