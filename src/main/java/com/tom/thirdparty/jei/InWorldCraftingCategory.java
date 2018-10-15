package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.oredict.OreDictionary;

import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.lib.Configs;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackWRng;
import com.tom.thirdparty.jei.InWorldCraftingCategory.InWorldRecipeJEI;

import com.tom.core.item.ItemCircuit;
import com.tom.core.item.ItemCircuit.CircuitType;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

public class InWorldCraftingCategory implements IRecipeCategory<InWorldRecipeJEI> {

	@Override
	public String getUid() {
		return JEIConstants.IN_WORLD;
	}

	@Override
	public String getTitle() {
		return I18n.format("tomsmod.jei.inWorld");
	}

	@Override
	public String getModName() {
		return Configs.ModName;
	}

	@Nonnull
	private final IDrawable background = JEIHandler.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation("tomsmod:textures/gui/jei/jeiCrusher.png"), 1, 5, 85, 50);

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, InWorldRecipeJEI recipe, IIngredients ingredients) {
		recipe.setRecipe(recipeLayout, ingredients);
	}
	public static class InWorldRecipeJEI implements IRecipeWrapper {
		@Nullable
		protected final ItemStack input1;
		@Nonnull
		protected final ItemStack output;
		@Nullable
		protected final ItemStack input2;

		public InWorldRecipeJEI(ItemStack input1, ItemStack input2, ItemStack output) {
			this.input1 = input1;
			this.output = output;
			this.input2 = input2;
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setOutput(VanillaTypes.ITEM, output);
			ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(new ItemStack[]{input1, input2}));
		}//IGuiItemStackGroup g = recipeLayout.getItemStacks();
		public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
			int x = 6;
			int y = 21;
			recipeLayout.getItemStacks().init(0, true, x, y);
			recipeLayout.getItemStacks().init(1, true, x + 30, y);
			recipeLayout.getItemStacks().init(2, false, x + 55, y);
			recipeLayout.getItemStacks().set(0, input1);
			recipeLayout.getItemStacks().set(1, input2);
			recipeLayout.getItemStacks().set(2, output);
		}
	}
	public static Collection<InWorldRecipeJEI> get() {
		List<InWorldRecipeJEI> ret = new ArrayList<>();
		for(CircuitType t : ItemCircuit.circuitTypes.values()){
			ret.add(new InWorldRecipeJEI(t.createStack(CoreInit.circuitRaw), FluidUtil.getFilledBucket(new FluidStack(CoreInit.ironChloride.get(), 1000)), t.createStack(CoreInit.circuitUnassembled)));
		}
		for(Entry<IBlockState, Entry<Integer, List<ItemStackWRng>>> e : MachineCraftingHandler.getHammerRecipes()){
			int meta = e.getKey().getBlock().getMetaFromState(e.getKey());
			ItemStack in = new ItemStack(e.getKey().getBlock(), 1, meta);
			List<ItemStackWRng> l = e.getValue().getValue();
			for (ItemStackWRng i : l) {
				ret.add(new HammerRecipe(in, e.getValue().getKey(), i));
			}
		}
		ret.add(new InWorldRecipeJEI(CraftingMaterial.HOT_COPPER.getStackNormal(), new ItemStack(Items.WATER_BUCKET), TMResource.COPPER.getStackNormal(Type.INGOT)));
		ret.add(new InWorldRecipeJEI(CraftingMaterial.HOT_COPPER_HAMMER_HEAD.getStackNormal(), new ItemStack(Items.WATER_BUCKET), CraftingMaterial.COPPER_HAMMER_HEAD.getStackNormal()));
		ret.add(new InWorldRecipeJEI(CraftingMaterial.HOT_WOLFRAM_INGOT.getStackNormal(), new ItemStack(Items.WATER_BUCKET), TMResource.WOLFRAM.getStackNormal(Type.INGOT)));
		ret.add(new InWorldRecipeJEI(CraftingMaterial.HOT_TUNGSTENSTEEL_INGOT.getStackNormal(), new ItemStack(Items.WATER_BUCKET), TMResource.TUNGSTENSTEEL.getStackNormal(Type.INGOT)));
		ItemStack is = FluidUtil.getFilledBucket(new FluidStack(CoreInit.photoactiveLiquid.get(), 1000));
		NBTTagCompound disp = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		list.appendTag(new NBTTagString(I18n.format("tomsmod.jei.filledWithFluid")));
		disp.setTag("Lore", list);
		is.getTagCompound().setTag("display", disp);
		ret.add(new InWorldRecipeJEI(CraftingMaterial.TIN_CAN.getStackNormal(), is, CraftingMaterial.PHOTOACTIVE_CAN.getStackNormal()));
		is = FluidUtil.getFilledBucket(new FluidStack(CoreInit.heatConductingPaste.get(), 1000));
		list = new NBTTagList();
		list.appendTag(new NBTTagString(I18n.format("tomsmod.jei.filledWithFluid")));
		disp.setTag("Lore", list);
		is.getTagCompound().setTag("display", disp);
		ret.add(new InWorldRecipeJEI(CraftingMaterial.TIN_CAN.getStackNormal(), is, CraftingMaterial.HEATCONDUCTINGPASTE_CAN.getStackNormal()));
		return ret;
	}
	private static class HammerRecipe extends InWorldRecipeJEI {
		private final int lvl;
		private final float chance;
		public HammerRecipe(ItemStack input1, int lvl, ItemStackWRng output) {
			super(input1, null, output.stack);
			this.lvl = lvl;
			this.chance = output.chance;
		}
		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setOutput(VanillaTypes.ITEM, output);
			List<ItemStack> s = new ArrayList<>(OreDictionary.getOres("itemHammer_lvl" + lvl));
			s.add(input1);
			ingredients.setInputs(VanillaTypes.ITEM, s);
		}
		@Override
		public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
			int x = 6;
			int y = 21;
			IGuiItemStackGroup g = recipeLayout.getItemStacks();
			g.init(0, true, x, y);
			g.init(1, true, x + 30, y);
			g.init(2, false, x + 55, y);
			g.set(0, input1);
			g.set(1, OreDictionary.getOres("itemHammer_lvl" + lvl));
			g.set(2, output);
		}
		@Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			minecraft.fontRenderer.drawString(I18n.format("tomsmod.jei.chance", chance + "%"), -19, 5, 4210752);
		}
	}
}
