package com.tom.recipes.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.apis.RecipeData;
import com.tom.apis.TomsModUtils;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.factory.FactoryInit;

public class MachineCraftingHandler {
	private static Map<ItemStackChecker, ItemStackChecker> crusherRecipes = new HashMap<ItemStackChecker, ItemStackChecker>();
	private static Map<ItemStackChecker, ItemStackChecker> plateBlenderRecipes = new HashMap<ItemStackChecker, ItemStackChecker>();
	private static Map<ItemStackChecker, ItemStackChecker> wireMillRecipes = new HashMap<ItemStackChecker, ItemStackChecker>();
	private static Map<ItemStackChecker, ItemStackChecker> coilerPlantRecipes = new HashMap<ItemStackChecker, ItemStackChecker>();
	private static Map<ItemStackChecker, ItemStackChecker> alloySmelterRecipes = new HashMap<ItemStackChecker, ItemStackChecker>();
	private static Map<ItemStackChecker, ItemStackChecker> cokeOvenRecipes = new HashMap<ItemStackChecker, ItemStackChecker>();
	private static Map<ItemStackChecker, ItemStackChecker> blastFurnaceRecipes = new HashMap<ItemStackChecker, ItemStackChecker>();
	private static Map<ItemStackChecker, ItemStackChecker> fluidTransposerRecipes = new HashMap<ItemStackChecker, ItemStackChecker>();
	public static void addCrusherRecipe(ItemStack input, ItemStack output){
		ItemStackChecker c = new ItemStackChecker(input);
		boolean found = false;
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : crusherRecipes.entrySet()){
			if(recipe.getKey().equals(c))found = true;
		}
		if(!found)crusherRecipes.put(new ItemStackChecker(input), new ItemStackChecker(output).setExtra(input.stackSize));
	}
	public static void addPlateBlenderRecipe(ItemStack input, ItemStack output, int tier){
		ItemStackChecker c = new ItemStackChecker(input);
		boolean found = false;
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : plateBlenderRecipes.entrySet()){
			if(recipe.getKey().equals(c))found = true;
		}
		if(!found)plateBlenderRecipes.put(new ItemStackChecker(input), new ItemStackChecker(output).setExtra(input.stackSize).setExtra2(tier));
	}
	public static void addWireMillRecipe(ItemStack input, ItemStack output, int tier){
		ItemStackChecker c = new ItemStackChecker(input);
		boolean found = false;
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : wireMillRecipes.entrySet()){
			if(recipe.getKey().equals(c))found = true;
		}
		if(!found)wireMillRecipes.put(new ItemStackChecker(input), new ItemStackChecker(output).setExtra(input.stackSize).setExtra2(tier));
	}
	public static void addCoilerPlantRecipe(ItemStack input, ItemStack output){
		ItemStackChecker c = new ItemStackChecker(input);
		boolean found = false;
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : coilerPlantRecipes.entrySet()){
			if(recipe.getKey().equals(c))found = true;
		}
		if(!found)coilerPlantRecipes.put(new ItemStackChecker(input), new ItemStackChecker(output).setExtra(input.stackSize));
	}
	public static ItemStackChecker getCrusherOutput(ItemStack stack){
		if(stack == null)return null;
		ItemStackChecker c = new ItemStackChecker(stack);
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : crusherRecipes.entrySet()){
			if(recipe.getKey().equals(c))return recipe.getValue();
		}
		return null;
	}
	public static ItemStackChecker getPlateBlenderOutput(ItemStack stack, int lvl){
		if(stack == null)return null;
		ItemStackChecker c = new ItemStackChecker(stack);
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : plateBlenderRecipes.entrySet()){
			if(recipe.getKey().equals(c)){
				if(lvl != -1 && recipe.getValue().extra2 > lvl)
					return null;
				else
					return recipe.getValue();
			}
		}
		return null;
	}
	public static ItemStackChecker getWireMillOutput(ItemStack stack, int lvl){
		if(stack == null)return null;
		ItemStackChecker c = new ItemStackChecker(stack);
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : wireMillRecipes.entrySet()){
			if(recipe.getKey().equals(c)){
				if(lvl != -1 && recipe.getValue().extra2 > lvl)
					return null;
				else
					return recipe.getValue();
			}
		}
		return null;
	}
	public static ItemStackChecker getCoilerOutput(ItemStack stack){
		if(stack == null)return null;
		ItemStackChecker c = new ItemStackChecker(stack);
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : coilerPlantRecipes.entrySet()){
			if(recipe.getKey().equals(c)){
				return recipe.getValue();
			}
		}
		return null;
	}
	public static class ItemStackChecker{
		private final ItemStack stack, extraStack;
		private int extra, extra2, extra3;
		private FluidStack fluid = null;
		private boolean mode = false;
		public ItemStackChecker(ItemStack stack) {
			this.stack = stack;
			this.extraStack = null;
		}
		public ItemStackChecker(ItemStack stack, ItemStack extra) {
			this.stack = stack;
			this.extraStack = extra;
		}
		public ItemStack getStack(){
			return stack.copy();
		}
		public ItemStackChecker setExtra(int extra) {
			this.extra = extra;
			return this;
		}
		public ItemStackChecker setExtra2(int extra) {
			this.extra2 = extra;
			return this;
		}
		public ItemStackChecker setExtra3(int extra) {
			this.extra3 = extra;
			return this;
		}
		public ItemStack getExtraStack() {
			return extraStack;
		}
		@Override
		public boolean equals(Object obj) {
			if(this == obj)return true;
			if(!(obj instanceof ItemStackChecker))return false;
			ItemStackChecker other = (ItemStackChecker) obj;
			return TomsModUtils.areItemStacksEqualOreDict(stack, other.stack, true, true, false, true) && mode == other.mode && TomsModUtils.areFluidStacksEqual2(fluid, other.fluid) && other.stack.stackSize >= stack.stackSize && ((extraStack == null && other.extraStack == null) || TomsModUtils.areItemStacksEqualOreDict(extraStack, other.extraStack, true, true, false, true) && other.extraStack.stackSize >= extraStack.stackSize);
		}
		public int getExtra() {
			return extra;
		}
		public int getExtra2() {
			return extra2;
		}
		public int getExtra3() {
			return extra3;
		}
		public FluidStack getExtraF() {
			return fluid;
		}
		public ItemStackChecker setExtraF(FluidStack extra) {
			this.fluid = extra;
			return this;
		}
		public boolean getMode() {
			return mode;
		}
		public ItemStackChecker setMode(boolean mode) {
			this.mode = mode;
			return this;
		}
	}
	public static List<RecipeData> getCrusherRecipes() {
		List<RecipeData> ret = new ArrayList<RecipeData>();
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : crusherRecipes.entrySet()){
			ret.add(new RecipeData(recipe.getKey().stack, recipe.getValue().stack));
		}
		return ret;
	}
	public static List<RecipeData> getPlateBlenderRecipes() {
		List<RecipeData> ret = new ArrayList<RecipeData>();
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : plateBlenderRecipes.entrySet()){
			ret.add(new RecipeData(recipe.getValue().extra2 ,recipe.getKey().stack, recipe.getValue().stack));
		}
		return ret;
	}
	public static List<RecipeData> getWireMillRecipes() {
		List<RecipeData> ret = new ArrayList<RecipeData>();
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : wireMillRecipes.entrySet()){
			ret.add(new RecipeData(recipe.getValue().extra2 ,recipe.getKey().stack, recipe.getValue().stack));
		}
		return ret;
	}
	public static List<RecipeData> getCoilerRecipes() {
		List<RecipeData> ret = new ArrayList<RecipeData>();
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : coilerPlantRecipes.entrySet()){
			ret.add(new RecipeData(recipe.getKey().stack, recipe.getValue().stack));
		}
		return ret;
	}
	public static void loadRecipes(){
		addCrusherRecipe(new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER, 5));
		addCrusherRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.GRAVEL, 1));
		addCrusherRecipe(new ItemStack(Blocks.GRAVEL), new ItemStack(Blocks.SAND, 1));
		addCrusherRecipe(new ItemStack(Blocks.STONE), new ItemStack(Blocks.COBBLESTONE, 1));
		addCrusherRecipe(new ItemStack(Items.REEDS), new ItemStack(Items.SUGAR, 2));
		addAlloySmelterRecipe(TMResource.COPPER.getStackNormal(Type.INGOT, 3), TMResource.TIN.getStackNormal(Type.INGOT), TMResource.BRONZE.getStackNormal(Type.INGOT, 4));
		addAlloySmelterRecipe(TMResource.GOLD.getStackNormal(Type.INGOT), TMResource.SILVER.getStackNormal(Type.INGOT), TMResource.ELECTRUM.getStackNormal(Type.INGOT, 2));
		addCrusherRecipe(new ItemStack(Blocks.NETHERRACK), CraftingMaterial.NETHERRACK_DUST.getStackNormal());
		addAlloySmelterRecipe(TMResource.LEAD.getStackNormal(Type.INGOT), TMResource.TIN.getStackNormal(Type.INGOT), CraftingMaterial.SOLDERING_ALLOY.getStackNormal(2));
		addPlateBlenderRecipe(CraftingMaterial.SILICON.getStackNormal(), CraftingMaterial.SILICON_PLATE.getStackNormal(), 1);
		addCrusherRecipe(new ItemStack(Blocks.GLASS), CraftingMaterial.GLASS_DUST.getStackNormal());
		addCokeOvenRecipe(new ItemStack(Items.COAL), new ItemStack(FactoryInit.coalCoke), 500, 1800);
		addCokeOvenRecipe(new ItemStack(Blocks.COAL_BLOCK), new ItemStack(FactoryInit.blockCoalCoke), 5000, 16500);
		addCokeOvenRecipe(new ItemStack(Blocks.LOG), new ItemStack(Items.COAL, 1, 1), 250, 1600);
		addBlastFurnaceRecipe(new ItemStack(Items.IRON_INGOT), TMResource.STEEL.getStackNormal(Type.INGOT), 3200);
		addBlastFurnaceRecipe(TMResource.IRON.getBlockStackNormal(1), TMResource.STEEL.getBlockStackNormal(1), 28800);
		addCrusherRecipe(TMResource.WOLFRAM.getStackNormal(Type.CRUSHED_ORE), CraftingMaterial.TUNGSTATE_DUST.getStackNormal());
		addBlastFurnaceRecipe(CraftingMaterial.TUNGSTATE_DUST.getStackNormal(), CraftingMaterial.HOT_WOLFRAM_INGOT.getStackNormal(), 6400);
		addAlloySmelterRecipe(new ItemStack(Blocks.REDSTONE_BLOCK), TMResource.ELECTRUM.getStackNormal(Type.INGOT, 4), TMResource.REDSTONE.getStackNormal(Type.INGOT, 4));
		addAlloySmelterRecipe(TMResource.COPPER.getStackNormal(Type.INGOT), TMResource.NICKEL.getStackNormal(Type.INGOT), CraftingMaterial.CUPRONICKEL_INGOT.getStackNormal(2));
		addWireMillRecipe(CraftingMaterial.CUPRONICKEL_INGOT.getStackNormal(4), CraftingMaterial.CUPRONICKEL_HEATING_COIL.getStackNormal(), 2);
	}
	public static ItemStack getFurnaceRecipe(ItemStack in){
		if(in == null)return null;
		ItemStack stack = in.copy();
		stack.stackSize = 1;
		ItemStack s = FurnaceRecipes.instance().getSmeltingResult(stack);
		if(s != null)s = s.copy();
		return s;
	}
	public static List<RecipeData> getAlloySmelterRecipes() {
		List<RecipeData> ret = new ArrayList<RecipeData>();
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : alloySmelterRecipes.entrySet()){
			ret.add(new RecipeData(recipe.getKey().stack, recipe.getKey().extraStack, recipe.getValue().stack));
		}
		return ret;
	}
	public static ItemStackChecker getAlloySmelterOutput(ItemStack stack1, ItemStack stack2){
		if(stack1 == null || stack2 == null)return null;
		ItemStackChecker c = new ItemStackChecker(stack1, stack2);
		ItemStackChecker cs = new ItemStackChecker(stack2, stack1);
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : alloySmelterRecipes.entrySet()){
			if(recipe.getKey().equals(c) || recipe.getKey().equals(cs)){
				return recipe.getValue();
			}
		}
		return null;
	}
	public static void addAlloySmelterRecipe(ItemStack input1, ItemStack input2, ItemStack output){
		ItemStackChecker c = new ItemStackChecker(input1, input2);
		boolean found = false;
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : alloySmelterRecipes.entrySet()){
			if(recipe.getKey().equals(c))found = true;
		}
		if(!found)alloySmelterRecipes.put(new ItemStackChecker(input1, input2), new ItemStackChecker(output).setExtra(input1.stackSize).setExtra2(input2.stackSize));
	}
	public static void addCokeOvenRecipe(ItemStack input1, ItemStack output, int creosote, int time){
		ItemStackChecker c = new ItemStackChecker(input1).setExtra2(creosote);
		boolean found = false;
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : cokeOvenRecipes.entrySet()){
			if(recipe.getKey().equals(c))found = true;
		}
		if(!found)cokeOvenRecipes.put(new ItemStackChecker(input1), new ItemStackChecker(output).setExtra(input1.stackSize).setExtra2(creosote).setExtra3(time));
	}
	public static ItemStackChecker getCokeOvenOutput(ItemStack stack){
		if(stack == null)return null;
		ItemStackChecker c = new ItemStackChecker(stack);
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : cokeOvenRecipes.entrySet()){
			if(recipe.getKey().equals(c)){
				return recipe.getValue();
			}
		}
		return null;
	}
	public static void addBlastFurnaceRecipe(ItemStack input1, ItemStack output, int time){
		ItemStackChecker c = new ItemStackChecker(input1);
		boolean found = false;
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : blastFurnaceRecipes.entrySet()){
			if(recipe.getKey().equals(c))found = true;
		}
		if(!found)blastFurnaceRecipes.put(new ItemStackChecker(input1), new ItemStackChecker(output).setExtra(input1.stackSize).setExtra3(time));
	}
	public static ItemStackChecker getBlastFurnaceOutput(ItemStack stack){
		if(stack == null)return null;
		ItemStackChecker c = new ItemStackChecker(stack);
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : blastFurnaceRecipes.entrySet()){
			if(recipe.getKey().equals(c)){
				return recipe.getValue();
			}
		}
		return null;
	}
	public static void addFluidTransposerRecipe(ItemStack input1, ItemStack output, FluidStack fluid, boolean isExtract){
		ItemStackChecker c = new ItemStackChecker(input1);
		boolean found = false;
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : fluidTransposerRecipes.entrySet()){
			if(recipe.getKey().equals(c))found = true;
		}
		if(!found)fluidTransposerRecipes.put(new ItemStackChecker(input1).setExtraF(fluid).setMode(isExtract), new ItemStackChecker(output).setExtra(fluid.amount));
	}
	public static ItemStackChecker getFluidTransposerOutput(ItemStack stack, FluidTank tank, boolean isExtract) {
		if(stack == null)return null;
		ItemStackChecker c = new ItemStackChecker(stack).setExtraF(tank.getFluid()).setMode(isExtract);
		for(Entry<ItemStackChecker, ItemStackChecker> recipe : fluidTransposerRecipes.entrySet()){
			if(recipe.getKey().equals(c)){
				return recipe.getValue();
			}
		}
		if(FluidUtil.getFluidHandler(stack.copy()) != null){
			ItemStack container = stack.copy();
			IFluidHandler h = FluidUtil.getFluidHandler(container);
			if(isExtract){
				if(tank.getFluidAmount() != 10000){
					if(tank.getFluid() != null){
						FluidStack drained = h.drain(10000 - tank.getFluid().amount, false);
						if(drained != null && drained.amount > 0){
							c = new ItemStackChecker(container);
							c.setExtraF(h.drain(10000 - tank.getFluid().amount, true));
							return c;
						}
					}else{
						FluidStack drained = h.drain(10000, false);
						if(drained != null && drained.amount > 0){
							c = new ItemStackChecker(container);
							c.setExtraF(h.drain(10000, true));
							return c;
						}
					}
				}
			}else{
				int filled = h.fill(tank.getFluid(), false);
				if(filled > 0){
					c = new ItemStackChecker(container);
					c.setExtra(h.fill(tank.getFluid(), true));
					return c;
				}
			}
		}
		return null;
	}
}
