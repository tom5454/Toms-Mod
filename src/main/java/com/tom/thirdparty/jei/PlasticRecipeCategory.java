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
import com.tom.thirdparty.jei.PlasticRecipeCategory.PlasticRecipeJEI;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class PlasticRecipeCategory implements IRecipeCategory<PlasticRecipeJEI>{
	public static List<PlasticRecipeJEI> get() {
		List<PlasticRecipeJEI> recipes = new ArrayList<PlasticRecipeJEI>();
		recipes.add(new PlasticRecipeJEI());
		return recipes;
	}
	@Nonnull
	private IDrawable background;
	@Nonnull
	private final IDrawable tankOverlay = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/resSelect.png"), 102, 124, 12, 47);
	@Nonnull
	private final IDrawable tankBackground = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/resSelect.png"), 78, 120, 20, 55);
	@Override
	public String getUid() {
		return JEIConstants.PLASTIC;
	}

	@Override
	public String getTitle() {
		return I18n.format("tomsmod.gui.plasticProcessor");
	}

	@Override
	public IDrawable getBackground() {
		background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 2, 80, 110, 150);
		return background;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		tankBackground.draw(minecraft, -4, -4);
		tankBackground.draw(minecraft, 18, -4);
		tankBackground.draw(minecraft, 40, -4);
		tankBackground.draw(minecraft, 62, -4);
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {

	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, PlasticRecipeJEI recipe) {
		int x = 5;
		int y = 60;
		int tankY = 0;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, true, x+18, y);
		recipeLayout.getItemStacks().init(2, true, x+73, y);
		for(int i = 0;i<2;i++)recipeLayout.getItemStacks().setFromRecipe(i, recipe.inputs.get(i));
		recipeLayout.getItemStacks().set(2, recipe.output);
		recipeLayout.getFluidStacks().init(0, true, 0, tankY, 12, 47, 20000, true, tankOverlay);
		recipeLayout.getFluidStacks().init(1, true, 22, tankY, 12, 47, 10000, true, tankOverlay);
		recipeLayout.getFluidStacks().init(2, true, 44, tankY, 12, 47, 10000, true, tankOverlay);
		recipeLayout.getFluidStacks().init(3, true, 66, tankY, 12, 47, 10000, true, tankOverlay);
		recipeLayout.getFluidStacks().set(0, recipe.fluidInputs.get(0));
		recipeLayout.getFluidStacks().set(1, recipe.fluidInputs.get(1));
		recipeLayout.getFluidStacks().set(2, recipe.fluidInputs.get(2));
		recipeLayout.getFluidStacks().set(3, recipe.fluidInputs.get(3));
	}
	public static class PlasticHandler implements IRecipeHandler<PlasticRecipeJEI>{

		@Override
		public Class<PlasticRecipeJEI> getRecipeClass() {
			return PlasticRecipeJEI.class;
		}

		@Override
		public String getRecipeCategoryUid() {
			return JEIConstants.PLASTIC;
		}

		@Override
		public IRecipeWrapper getRecipeWrapper(PlasticRecipeJEI recipe) {
			return recipe;
		}

		@Override
		public boolean isRecipeValid(PlasticRecipeJEI recipe) {
			return true;
		}

		@Override
		public String getRecipeCategoryUid(PlasticRecipeJEI recipe) {
			return JEIConstants.PLASTIC;
		}

	}
	public static class PlasticRecipeJEI extends BlankRecipeWrapper{
		private final List<Object> inputs;
		private final ItemStack output;
		private final List<FluidStack> fluidInputs;
		public PlasticRecipeJEI() {
			inputs = new ArrayList<Object>();
			fluidInputs = new ArrayList<FluidStack>();
			inputs.add(OreDictionary.getOres(CraftingMaterial.RUBBER.getName()));
			inputs.add(OreDictionary.getOres(TMResource.COAL.getStackName(Type.DUST)));
			output = CraftingMaterial.PLASTIC_SHEET.getStackNormal(2);
			fluidInputs.add(new FluidStack(FluidRegistry.WATER, 2000));
			fluidInputs.add(new FluidStack(CoreInit.kerosene, 100));
			fluidInputs.add(new FluidStack(CoreInit.lpg, 50));
			fluidInputs.add(new FluidStack(CoreInit.creosoteOil, 100));
		}
		@Override
		public List<Object> getInputs(){
			return inputs;
		}
		@Override
		public List<ItemStack> getOutputs() {
			return Collections.singletonList(output);
		}
		@Override
		public List<FluidStack> getFluidInputs() {
			return fluidInputs;
		}
		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

		}
	}
}