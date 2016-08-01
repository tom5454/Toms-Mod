package com.tom.recipes;

import com.tom.core.CoreInit;
import com.tom.recipes.handler.ElectrolyzerRecipesHandler;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ElectrolyzerRecipes {
	public static void init(){
		ElectrolyzerRecipesHandler.add(new FluidStack(FluidRegistry.WATER,3), 20, new FluidStack(CoreInit.Hydrogen,2), null, null, null);
	}
}
