package com.tom.recipes;

import net.minecraftforge.fluids.FluidStack;

import com.tom.core.CoreInit;
import com.tom.recipes.handler.CentrifugeRecipeHandler;

public class CentifugeRecipes {
	public static void init(){
		CentrifugeRecipeHandler.add(CoreInit.Hydrogen, 20, 100, new FluidStack(CoreInit.Deuterium,5));
		CentrifugeRecipeHandler.add(CoreInit.Deuterium, 20, 200, new FluidStack(CoreInit.Tritium,5));
	}
}
