package com.tom.apis;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import com.tom.api.research.IResearch;
import com.tom.recipes.handler.AdvancedCraftingHandler.CraftingLevel;

public class RecipeData {
	public RecipeData(FluidStack f1, FluidStack f2, FluidStack f3, FluidStack f4, int energy,int inputAmount){
		this.f1 = f1;
		this.f2 = f2;
		this.f3 = f3;
		this.f4 = f4;
		this.energy = energy;
		this.inputAmount = inputAmount;
		this.processTime = 1;
	}
	public RecipeData(Block block2, Block output){
		this.output = output;
		this.block2 = block2;
		this.processTime = 1;
	}
	public RecipeData(Block block2, Block output, Item invItem1){
		this.output = output;
		this.block2 = block2;
		this.invItem1 = invItem1;
		this.hasInv = true;
		this.processTime = 1;
	}

	public RecipeData(FluidStack f, int energy, int amount) {
		this.f1 = f;
		this.energy = energy;
		this.inputAmount = amount;
		this.processTime = 1;
	}
	public RecipeData(FluidStack f, int energy, int amount, int processTime) {
		this.f1 = f;
		this.energy = energy;
		this.inputAmount = amount;
		this.processTime = processTime;
	}
	public RecipeData(ItemStack i, int energy, int amount, int processTime) {
		this.itemstack1 = i;
		this.energy = energy;
		this.inputAmount = amount;
		this.processTime = processTime;
	}
	public RecipeData(ItemStack is, int time, ItemStack[] isIn, List<IResearch> researchList, boolean shaped, ItemStack isExtra, CraftingLevel level) {
		this.processTime = time;
		this.requiredResearches = researchList;
		this.itemstack1 = isIn[0];
		this.itemstack2 = isIn[1];
		this.itemstack3 = isIn[2];
		this.itemstack4 = isIn[3];
		this.itemstack5 = isIn[4];
		this.itemstack6 = isIn[5];
		this.itemstack7 = isIn[6];
		this.itemstack8 = isIn[7];
		this.itemstack9 = isIn[8];
		this.itemstack0 = is;
		this.itemstack10 = isExtra;
		this.shaped = shaped;
		this.level = level;
	}

	public RecipeData(ItemStack itemstack0, ItemStack itemstack1) {
		this.itemstack0 = itemstack0;
		this.itemstack1 = itemstack1;
	}

	public RecipeData(int energy, ItemStack itemstack0, ItemStack itemstack1) {
		this.energy = energy;
		this.itemstack0 = itemstack0;
		this.itemstack1 = itemstack1;
	}

	public RecipeData(ItemStack itemstack0, ItemStack itemstack1, ItemStack itemstack2) {
		this.itemstack0 = itemstack0;
		this.itemstack1 = itemstack1;
		this.itemstack2 = itemstack2;
	}

	public FluidStack f1;
	public FluidStack f2;
	public FluidStack f3;
	public FluidStack f4;
	public int energy;
	public int inputAmount;
	public Block block2;
	public Block output;
	public Item invItem1;
	public Item invItem2;
	public Item invItem3;
	public Item invItem4;
	public Item invReturn1;
	public Item invReturn2;
	public boolean hasInv;
	public int processTime;
	public ItemStack itemstack0;
	public ItemStack itemstack1;
	public ItemStack itemstack2;
	public ItemStack itemstack3;
	public ItemStack itemstack4;
	public ItemStack itemstack5;
	public ItemStack itemstack6;
	public ItemStack itemstack7;
	public ItemStack itemstack8;
	public ItemStack itemstack9;
	public ItemStack itemstack10;
	public List<IResearch> requiredResearches;
	public boolean shaped;
	public CraftingLevel level;
}
