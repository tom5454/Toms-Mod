package com.tom.thirdparty.jei;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.tom.api.research.Research;
import com.tom.core.CoreInit;
import com.tom.core.research.ResearchHandler;
import com.tom.lib.Configs;
import com.tom.thirdparty.jei.ResearchCategory.ResearchJEI;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

public class ResearchCategory implements IRecipeCategory<ResearchJEI> {
	public static class ResearchJEI implements IRecipeWrapper {
		private final Research research;
		public ResearchJEI(Research research) {
			this.research = research;
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			List<Research> parents = research.getParents();
			ingredients.setInputs(JEIHandler.RESEARCH, parents == null ? Collections.emptyList() : parents);
			ingredients.setInputs(VanillaTypes.ITEM, research.getResearchRequirements());
			ingredients.setOutput(JEIHandler.RESEARCH, research);
		}

	}

	@Nonnull
	private final IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 120, 120, 120, 80);
	@Nonnull
	private final IDrawable icon = JEIHandler.jeiHelper.getGuiHelper().createDrawableIngredient(new ItemStack(CoreInit.blueprint));

	@Override
	public String getUid() {
		return JEIConstants.RESEARCH;
	}

	@Override
	public String getTitle() {
		return I18n.format("tomsmod.jei.research");
	}

	@Override
	public String getModName() {
		return Configs.ModName;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ResearchJEI recipe, IIngredients ingredients) {
		IGuiIngredientGroup<Research> gr = recipeLayout.getIngredientsGroup(JEIHandler.RESEARCH);
		int x = 6;
		int y = -10;
		gr.init(0, false, x+90, y + 30);
		int i = 1;
		gr.set(0, recipe.research);
		if(recipe.research.getParents() != null){
			for(Research r : recipe.research.getParents()){
				int j = i++;
				gr.init(j, true, x, y + j*18);
				gr.set(j, r);
			}
		}
		x += 40;
		y += 31;
		i = 0;
		for(ItemStack st : recipe.research.getResearchRequirements()){
			int j = i++;
			recipeLayout.getItemStacks().init(j, true, x + (j % 2) * 18, y + (j / 2) * 18);
			recipeLayout.getItemStacks().set(j, st);
		}
	}
	public static List<ResearchJEI> get(){
		return ResearchHandler.getAllResearches().stream().map(ResearchJEI::new).collect(Collectors.toList());
	}
}
