package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import com.tom.apis.TomsModUtils;
import com.tom.factory.tileentity.TileEntityMixer;
import com.tom.thirdparty.jei.MixerRecipeCategory.MixerRecipeJEI;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class MixerRecipeCategory implements IRecipeCategory<MixerRecipeJEI>{
	public static List<MixerRecipeJEI> get(MixerRecipeCategory c) {
		List<MixerRecipeJEI> recipes = new ArrayList<MixerRecipeJEI>();
		for(int i = 0;i<TileEntityMixer.RECIPES.length;i++){
			recipes.add(c.new MixerRecipeJEI(TileEntityMixer.RECIPES[i]));
		}
		return recipes;
	}
	@Nonnull
	private final IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 145, 120, 85, 80);
	@Nonnull
	private final IDrawable tankOverlay = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/resSelect.png"), 102, 124, 12, 47);
	@Nonnull
	private final IDrawable tankBackground = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/resSelect.png"), 78, 120, 20, 55);
	@Override
	public String getUid() {
		return JEIConstants.MIXER;
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
		tankBackground.draw(minecraft, -27, 6);
		tankBackground.draw(minecraft, 76, 6);
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MixerRecipeJEI recipe) {
		int x = 21;
		int y = 21;
		int tankY = 10;
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, true, x+18, y);
		recipeLayout.getItemStacks().init(2, true, x, y+18);
		recipeLayout.getItemStacks().init(3, true, x+18, y+18);
		for(int i = 0;i<4;i++)recipeLayout.getItemStacks().setFromRecipe(i, ((List<Object>) recipe.recipe[0]).get(i));
		recipeLayout.getFluidStacks().init(0, true, -23, tankY, 12, 47, 10000, true, tankOverlay);
		recipeLayout.getFluidStacks().init(1, false, 80, tankY, 12, 47, 10000, true, tankOverlay);
		if((Boolean) recipe.recipe[2])recipeLayout.getFluidStacks().init(2, true, 0, tankY, 12, 47, 10000, true, tankOverlay);
		recipeLayout.getFluidStacks().set(0, (FluidStack) ((Object[][])recipe.recipe[1])[0][0]);
		recipeLayout.getFluidStacks().set(1, (FluidStack) ((Object[][])recipe.recipe[1])[1][0]);
		if((Boolean) recipe.recipe[2])recipeLayout.getFluidStacks().set(2, (FluidStack) ((Object[][])recipe.recipe[1])[2][0]);
	}
	public static class MixerHandler implements IRecipeHandler<MixerRecipeJEI>{

		@Override
		public Class<MixerRecipeJEI> getRecipeClass() {
			return MixerRecipeJEI.class;
		}

		@Override
		public String getRecipeCategoryUid() {
			return JEIConstants.MIXER;
		}

		@Override
		public IRecipeWrapper getRecipeWrapper(MixerRecipeJEI recipe) {
			return recipe;
		}

		@Override
		public boolean isRecipeValid(MixerRecipeJEI recipe) {
			return recipe.recipe != null;
		}

		@Override
		public String getRecipeCategoryUid(MixerRecipeJEI recipe) {
			return JEIConstants.MIXER;
		}

	}
	public class MixerRecipeJEI extends BlankRecipeWrapper{
		@Nullable
		private final Object[] recipe;
		public MixerRecipeJEI(Object[] recipe) {
			this.recipe = recipe;
		}
		@SuppressWarnings("unchecked")
		@Override
		public List<Object> getInputs()
		{
			return (List<Object>) recipe[0];
		}
		@Override
		public List<FluidStack> getFluidInputs() {
			return TomsModUtils.getFluidStack((Object[][]) recipe[1], true);
		}
		@Override
		public List<FluidStack> getFluidOutputs() {
			return TomsModUtils.getFluidStack((Object[][]) recipe[1], false);
		}
		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			if((Boolean) recipe[2]){
				tankBackground.draw(minecraft, -4, 6);
				minecraft.fontRendererObj.drawString(I18n.format("tomsmod.jei.electricalRequired"), 0, 63, 4210752);
			}
		}
	}
}
