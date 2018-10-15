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

import net.minecraftforge.fluids.FluidStack;

import com.tom.factory.tileentity.TileEntityMixer;
import com.tom.lib.Configs;
import com.tom.thirdparty.jei.MixerRecipeCategory.MixerRecipeJEI;
import com.tom.util.TomsModUtils;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

public class MixerRecipeCategory implements IRecipeCategory<MixerRecipeJEI> {
	public static List<MixerRecipeJEI> get(MixerRecipeCategory c) {
		List<MixerRecipeJEI> recipes = new ArrayList<>();
		for (int i = 0;i < TileEntityMixer.RECIPES.length;i++) {
			recipes.add(c.new MixerRecipeJEI(TileEntityMixer.RECIPES[i]));
		}
		return recipes;
	}

	@Nonnull
	private final IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 125, 120, 120, 80);

	@Override
	public String getUid() {
		return JEIConstants.MIXER;
	}

	@Override
	public String getModName() {
		return Configs.ModName;
	}

	@Override
	public String getTitle() {
		return I18n.format("tomsmod.jei.mixer");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
	}

	public class MixerRecipeJEI implements IRecipeWrapper {
		@Nullable
		private final Object[] recipe;

		public MixerRecipeJEI(Object[] recipe) {
			this.recipe = recipe;
		}

		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			if ((Boolean) recipe[2]) {
				minecraft.fontRenderer.drawString(I18n.format("tomsmod.jei.electricalRequired"), 0, 70, 4210752);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputs(VanillaTypes.FLUID, TomsModUtils.getFluidStack((Object[][]) recipe[1], true));
			ingredients.setOutputs(VanillaTypes.FLUID, TomsModUtils.getFluidStack((Object[][]) recipe[1], false));
			ingredients.setInputs(VanillaTypes.ITEM, (List<ItemStack>) recipe[0]);
		}
	}

	@Override
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MixerRecipeJEI recipe, IIngredients ingredients) {
		int x = 41;
		int y = 21;
		int tankY = 10;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, true, x + 18, y);
		recipeLayout.getItemStacks().init(2, true, x, y + 18);
		recipeLayout.getItemStacks().init(3, true, x + 18, y + 18);
		for (int i = 0;i < 4;i++)
			recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).set(i, getItemStack(((List<?>) recipe.recipe[0]).get(i)));
		recipeLayout.getFluidStacks().init(0, true, 0, tankY, 16, 58, 10000, true, JEIHandler.tankOverlay);
		recipeLayout.getFluidStacks().init(1, false, 80, tankY, 16, 58, 10000, true, JEIHandler.tankOverlay);
		if ((Boolean) recipe.recipe[2])
			recipeLayout.getFluidStacks().init(2, true, 23, tankY, 16, 58, 10000, true, JEIHandler.tankOverlay);
		recipeLayout.getFluidStacks().setBackground(0, JEIHandler.tankBackground);
		recipeLayout.getFluidStacks().setBackground(1, JEIHandler.tankBackground);
		if ((Boolean) recipe.recipe[2])recipeLayout.getFluidStacks().setBackground(2, JEIHandler.tankBackground);
		recipeLayout.getFluidStacks().set(0, (FluidStack) ((Object[][]) recipe.recipe[1])[0][0]);
		recipeLayout.getFluidStacks().set(1, (FluidStack) ((Object[][]) recipe.recipe[1])[1][0]);
		if ((Boolean) recipe.recipe[2]) {
			Object[][] d = (Object[][]) recipe.recipe[1];
			if (d.length > 2)
				recipeLayout.getFluidStacks().set(2, (FluidStack) d[2][0]);
		}
	}

	@SuppressWarnings("unchecked")
	private List<ItemStack> getItemStack(Object in) {
		if (in instanceof ItemStack)
			return Collections.singletonList((ItemStack) in);
		else
			return (List<ItemStack>) in;
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}
}
