package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.tom.apis.RecipeData;
import com.tom.apis.TomsModUtils;
import com.tom.core.research.ResearchHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.CraftingLevel;
import com.tom.thirdparty.jei.CustomCraftingRecipeCategory.CustomCrafingRecipeJEI;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.config.Constants;
import mezz.jei.plugins.vanilla.crafting.CraftingRecipeCategory;
import mezz.jei.util.Translator;

public class CustomCraftingRecipeCategory implements IRecipeCategory<CustomCrafingRecipeJEI>{
	private static final double shapelessIconScale = 0.5;

	public static List<CustomCrafingRecipeJEI> get() {
		List<CustomCrafingRecipeJEI> recipes = new ArrayList<CustomCrafingRecipeJEI>();
		List<RecipeData> recipeList = AdvancedCraftingHandler.getRecipes();
		for(int i = 0;i<recipeList.size();i++){
			RecipeData data = recipeList.get(i);
			//ItemStack[] array = {data.itemstack1, data.itemstack2, data.itemstack3, data.itemstack4, data.itemstack5, data.itemstack6, data.itemstack7, data.itemstack8, data.itemstack9};
			CustomCrafingRecipeJEI cr = new CustomCrafingRecipeJEI(data.recipe, data.itemstack10, ResearchHandler.getResearchNames(data.requiredResearches), data.level);
			recipes.add(cr);
		}
		return recipes;
	}
	@Nonnull
	private final IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/advCrafting.png"), 5, 5, 130, 71);
	@Override
	public String getUid() {
		return JEIConstants.CUSTOM_CRAFTING_ID;
	}

	@Override
	public String getTitle() {
		return I18n.format("tomsmod.jei.advCrafing");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {

	}

	@Override
	public void drawAnimations(Minecraft minecraft) {

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setRecipe(IRecipeLayout recipeLayout,
			CustomCrafingRecipeJEI recipe) {
		int x = 5;
		int y = 4;
		List inputs = recipe.getInputs();
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, true, x+18, y);
		recipeLayout.getItemStacks().init(2, true, x+36, y);
		recipeLayout.getItemStacks().init(3, true, x, y+18);
		recipeLayout.getItemStacks().init(4, true, x+18, y+18);
		recipeLayout.getItemStacks().init(5, true, x+36, y+18);
		recipeLayout.getItemStacks().init(6, true, x, y+36);
		recipeLayout.getItemStacks().init(7, true, x+18, y+36);
		recipeLayout.getItemStacks().init(8, true, x+36, y+36);
		recipeLayout.getItemStacks().init(9, false, x+94, y+18);
		recipeLayout.getItemStacks().init(10, false, x+94, y+45);
		for(int i = 0;i<10;i++)setSlot(recipeLayout, inputs, i);
		recipeLayout.getItemStacks().set(9, recipe.input.getRecipeOutput());
		if(recipe.extra != null)recipeLayout.getItemStacks().set(10, recipe.extra);
	}
	@SuppressWarnings("rawtypes")
	private static void setSlot(IRecipeLayout recipeLayout, List recipe, int index){
		try{
			if(index < recipe.size()){
				IGuiItemStackGroup g = recipeLayout.getItemStacks();
				g.setFromRecipe(index, recipe.get(index));
			}
		}catch(Exception e){
			e.printStackTrace();
			System.err.println(index);
		}
	}
	public static class CustomCraftingHandler implements IRecipeHandler<CustomCrafingRecipeJEI>{

		@Override
		public Class<CustomCrafingRecipeJEI> getRecipeClass() {
			return CustomCrafingRecipeJEI.class;
		}

		@Override
		public String getRecipeCategoryUid() {
			return JEIConstants.CUSTOM_CRAFTING_ID;
		}

		@Override
		public IRecipeWrapper getRecipeWrapper(CustomCrafingRecipeJEI recipe) {
			return recipe;
		}

		@Override
		public boolean isRecipeValid(CustomCrafingRecipeJEI recipe) {
			return recipe.input != null && recipe.input.getRecipeOutput() != null;
		}

		@Override
		public String getRecipeCategoryUid(CustomCrafingRecipeJEI recipe) {
			return JEIConstants.CUSTOM_CRAFTING_ID;
		}

	}
	public static class CustomCrafingRecipeJEI extends BlankRecipeWrapper{
		@Nonnull
		private final IRecipe input;
		@Nullable
		private final ItemStack extra;
		private final CraftingLevel level;
		private final boolean shaped;
		@Nonnull
		private final List<String> requiredResearches;
		@Nonnull
		private final IDrawable shapelessIcon;
		@Nonnull
		private final HoverChecker shapelessIconHoverChecker;

		public CustomCrafingRecipeJEI(@Nonnull IRecipe input, @Nullable ItemStack extra, @Nonnull List<String> requiredResearches, CraftingLevel level){
			this.input = input;
			this.extra = extra;
			this.requiredResearches = requiredResearches;
			this.level = level;
			this.shaped = !(input instanceof ShapelessOreRecipe || input instanceof ShapelessRecipes);
			shapelessIcon = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation(Constants.RESOURCE_DOMAIN, Constants.TEXTURE_GUI_PATH + "recipeBackground.png"), 196, 0, 19, 15);
			int iconBottom = (int) (shapelessIcon.getHeight() * shapelessIconScale);
			int iconLeft = CraftingRecipeCategory.width - (int) (shapelessIcon.getWidth() * shapelessIconScale);
			int iconRight = iconLeft + (int) (shapelessIcon.getWidth() * shapelessIconScale);
			shapelessIconHoverChecker = new HoverChecker(0, iconBottom, iconLeft, iconRight, 0);
		}
		@SuppressWarnings("rawtypes")
		@Override
		public List getInputs()
		{
			if(input instanceof ShapelessOreRecipe)return ((ShapelessOreRecipe)input).getInput();
			if(input instanceof ShapelessRecipes)return ((ShapelessRecipes)input).recipeItems;
			return Arrays.asList(input instanceof ShapedOreRecipe ? ((ShapedOreRecipe)input).getInput() : (input instanceof ShapedRecipes ? ((ShapedRecipes)input).recipeItems : (null) ));
		}

		@Override
		public List<ItemStack> getOutputs()
		{
			return TomsModUtils.getItemStackList(input.getRecipeOutput(),extra);
		}

		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			if(level.isAdvanced()){
				String lvl = I18n.format(level.getName());
				minecraft.fontRendererObj.drawString(I18n.format("tomsmod.jei.recipeLevel", lvl), -19, -5, 4210752);
			}
			if(!shaped){
				int shapelessIconX = recipeWidth - (int) (shapelessIcon.getWidth() * shapelessIconScale);
				GlStateManager.pushMatrix();
				GlStateManager.scale(shapelessIconScale, shapelessIconScale, 1.0);
				GlStateManager.color(1f, 1f, 1f, 1f);
				shapelessIcon.draw(minecraft, (int) (shapelessIconX / shapelessIconScale), 0);
				GlStateManager.popMatrix();
			}
		}
		@Nullable
		@Override
		public List<String> getTooltipStrings(int mouseX, int mouseY) {
			if (!shaped && shapelessIconHoverChecker.checkHover(mouseX, mouseY)) {
				return Collections.singletonList(Translator.translateToLocal("jei.tooltip.shapeless.recipe"));
			}

			return super.getTooltipStrings(mouseX, mouseY);
		}
	}
}
