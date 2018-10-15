package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.tom.api.research.Research;
import com.tom.lib.Configs;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.CraftingLevel;
import com.tom.thirdparty.jei.CustomCraftingRecipeCategory.CustomCrafingRecipeJEI;
import com.tom.util.RecipeData;
import com.tom.util.TomsModUtils;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CustomCraftingRecipeCategory implements IRecipeCategory<CustomCrafingRecipeJEI> {
	public static List<CustomCrafingRecipeJEI> get() {
		List<CustomCrafingRecipeJEI> recipes = new ArrayList<>();
		List<RecipeData> recipeList = AdvancedCraftingHandler.getRecipes();
		for (int i = 0;i < recipeList.size();i++) {
			RecipeData data = recipeList.get(i);
			// ItemStack[] array = {data.itemstack1, data.itemstack2,
			// data.itemstack3, data.itemstack4, data.itemstack5,
			// data.itemstack6, data.itemstack7, data.itemstack8,
			// data.itemstack9};
			CustomCrafingRecipeJEI cr = new CustomCrafingRecipeJEI(data.recipe, data.itemstack10, data.requiredResearches, data.level, new ResourceLocation(data.id));
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

	private static void setSlot(IRecipeLayout recipeLayout, List<List<ItemStack>> recipe, int index) {
		try {
			if (index < recipe.size()) {
				IGuiItemStackGroup g = recipeLayout.getItemStacks();
				g.set(index, recipe.get(index));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(index);
		}
	}

	public static class CustomCrafingRecipeJEI implements IRecipeWrapper {
		@Nonnull
		private final IRecipe input;
		@Nullable
		private final ItemStack extra;
		private final CraftingLevel level;
		private final boolean shaped;
		@Nonnull
		private final List<Research> requiredResearches;
		private final List<String> locResearches;
		private final ResourceLocation loc;

		public CustomCrafingRecipeJEI(@Nonnull IRecipe input, @Nullable ItemStack extra, @Nonnull List<Research> requiredResearches, CraftingLevel level, ResourceLocation loc) {
			this.input = input;
			this.extra = extra;
			this.requiredResearches = requiredResearches;
			this.level = level;
			this.shaped = !(input instanceof ShapelessOreRecipe || input instanceof ShapelessRecipes);
			this.loc = loc;
			locResearches = requiredResearches.stream().map(Research::getUnlocalizedName).map(I18n::format).map(e -> I18n.format("tomsMod.chat.tabulator", e)).collect(Collectors.toList());
		}

		public List<List<ItemStack>> getInputs() {
			if (input instanceof ShapelessOreRecipe)
				return JEIHandler.jeiHelper.getStackHelper().expandRecipeItemStackInputs(((ShapelessOreRecipe) input).getIngredients());
			if (input instanceof ShapelessRecipes)
				return JEIHandler.jeiHelper.getStackHelper().expandRecipeItemStackInputs(((ShapelessRecipes) input).recipeItems);
			return input instanceof ShapedOreRecipe ? JEIHandler.jeiHelper.getStackHelper().expandRecipeItemStackInputs(((ShapedOreRecipe) input).getIngredients()) : (input instanceof ShapedRecipes ? JEIHandler.jeiHelper.getStackHelper().expandRecipeItemStackInputs(((ShapedRecipes) input).recipeItems) : (null));
		}

		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			if (level.isAdvanced()) {
				String lvl = I18n.format(level.getName());
				minecraft.fontRenderer.drawString(I18n.format("tomsmod.jei.recipeLevel", lvl), -19, -5, 4210752);
			}
			//JEIHandler.recipeInfoIcon.draw(minecraft);
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setOutputs(VanillaTypes.ITEM, TomsModUtils.getItemStackList(input.getRecipeOutput(), extra));
			List<List<ItemStack>> inputs = getInputs();
			if (inputs != null)
				ingredients.setInputLists(VanillaTypes.ITEM, inputs);
			ingredients.setInputs(JEIHandler.RESEARCH, requiredResearches);
		}
		/*@Override
		public List<String> getTooltipStrings(int mouseX, int mouseY) {
			return JEIHandler.recipeInfoIcon.getTooltipStrings(loc, mouseX, mouseY);
		}*/
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CustomCrafingRecipeJEI recipe, IIngredients ingredients) {
		int x = 5;
		int y = 4;
		List<List<ItemStack>> inputs = recipe.getInputs();
		recipeLayout.getItemStacks().init(0, true, x, y);
		recipeLayout.getItemStacks().init(1, true, x + 18, y);
		recipeLayout.getItemStacks().init(2, true, x + 36, y);
		recipeLayout.getItemStacks().init(3, true, x, y + 18);
		recipeLayout.getItemStacks().init(4, true, x + 18, y + 18);
		recipeLayout.getItemStacks().init(5, true, x + 36, y + 18);
		recipeLayout.getItemStacks().init(6, true, x, y + 36);
		recipeLayout.getItemStacks().init(7, true, x + 18, y + 36);
		recipeLayout.getItemStacks().init(8, true, x + 36, y + 36);
		recipeLayout.getItemStacks().init(9, false, x + 94, y + 18);
		recipeLayout.getItemStacks().init(10, false, x + 94, y + 45);
		for (int i = 0;i < 10;i++)
			setSlot(recipeLayout, inputs, i);
		recipeLayout.getItemStacks().set(9, recipe.input.getRecipeOutput());
		if (recipe.extra != null)
			recipeLayout.getItemStacks().set(10, recipe.extra);
		if (!recipe.shaped)
			recipeLayout.setShapeless();
		IGuiIngredientGroup<Research> gr = recipeLayout.getIngredientsGroup(JEIHandler.RESEARCH);
		int i = 0;
		for(Research r : recipe.requiredResearches){
			int j = i++;
			gr.init(j, true, x + 60 + j*18, y - 4);
			gr.set(j, r);
		}
		recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
			if (slotIndex == 9) {
				if(Minecraft.getMinecraft().gameSettings.advancedItemTooltips)
					tooltip.add(TextFormatting.GRAY + recipe.loc.toString());
				if(!recipe.locResearches.isEmpty()){
					if(GuiScreen.isShiftKeyDown()){
						tooltip.add(I18n.format("tomsmod.jei.requiredResearches"));
						tooltip.addAll(recipe.locResearches);
					}else{
						tooltip.add(I18n.format("tomsmod.jei.showRequiredResearches"));
					}
				}
			}
		});
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
