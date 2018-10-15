package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.lib.Configs;
import com.tom.thirdparty.jei.PlasticRecipeCategory.PlasticRecipeJEI;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

public class PlasticRecipeCategory implements IRecipeCategory<PlasticRecipeJEI> {
	public static List<PlasticRecipeJEI> get() {
		List<PlasticRecipeJEI> recipes = new ArrayList<>();
		recipes.add(new PlasticRecipeJEI());
		return recipes;
	}

	@Nonnull
	private IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 2, 80, 110, 150);

	@Override
	public String getUid() {
		return JEIConstants.PLASTIC;
	}

	@Override
	public String getModName() {
		return Configs.ModName;
	}

	@Override
	public String getTitle() {
		return I18n.format("tomsmod.gui.plasticProcessor");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
	}

	public static class PlasticRecipeJEI implements IRecipeWrapper {
		private final List<List<ItemStack>> inputs;
		private final ItemStack output;
		private final List<FluidStack> fluidInputs;

		public PlasticRecipeJEI() {
			inputs = new ArrayList<>();
			fluidInputs = new ArrayList<>();
			inputs.add(OreDictionary.getOres(CraftingMaterial.RUBBER.getName()));
			inputs.add(OreDictionary.getOres(TMResource.COAL.getStackName(Type.DUST)));
			output = CraftingMaterial.PLASTIC_SHEET.getStackNormal(2);
			fluidInputs.add(new FluidStack(FluidRegistry.WATER, 2000));
			fluidInputs.add(new FluidStack(CoreInit.kerosene.get(), 100));
			fluidInputs.add(new FluidStack(CoreInit.lpg.get(), 50));
			fluidInputs.add(new FluidStack(CoreInit.creosoteOil.get(), 100));
		}

		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputLists(VanillaTypes.ITEM, inputs);
			ingredients.setOutput(VanillaTypes.ITEM, output);
			ingredients.setInputs(VanillaTypes.FLUID, fluidInputs);
		}
	}

	@Override
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, PlasticRecipeJEI recipe, IIngredients ingredients) {
		int x = 5;
		int y = 60;
		int tankY = -5;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, true, x + 18, y);
		recipeLayout.getItemStacks().init(2, true, x + 73, y);
		for (int i = 0;i < 2;i++)
			recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).set(i, recipe.inputs.get(i));
		recipeLayout.getItemStacks().set(2, recipe.output);
		recipeLayout.getFluidStacks().init(0, true, 0, tankY, 16, 58, 20000, true, JEIHandler.tankOverlay);
		recipeLayout.getFluidStacks().init(1, true, 22, tankY, 16, 58, 10000, true, JEIHandler.tankOverlay);
		recipeLayout.getFluidStacks().init(2, true, 44, tankY, 16, 58, 10000, true, JEIHandler.tankOverlay);
		recipeLayout.getFluidStacks().init(3, true, 66, tankY, 16, 58, 10000, true, JEIHandler.tankOverlay);
		recipeLayout.getFluidStacks().setBackground(0, JEIHandler.tankBackground);
		recipeLayout.getFluidStacks().setBackground(1, JEIHandler.tankBackground);
		recipeLayout.getFluidStacks().setBackground(2, JEIHandler.tankBackground);
		recipeLayout.getFluidStacks().setBackground(3, JEIHandler.tankBackground);
		recipeLayout.getFluidStacks().set(0, recipe.fluidInputs.get(0));
		recipeLayout.getFluidStacks().set(1, recipe.fluidInputs.get(1));
		recipeLayout.getFluidStacks().set(2, recipe.fluidInputs.get(2));
		recipeLayout.getFluidStacks().set(3, recipe.fluidInputs.get(3));
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}
}