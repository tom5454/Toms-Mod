package com.tom.recipes;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.tom.core.CoreInit;
import com.tom.recipes.handler.ElectrolyzerRecipesHandler;

public class ElectrolyzerRecipes {
	public static void init(){
		ElectrolyzerRecipesHandler.add(new FluidStack(FluidRegistry.WATER,300), 200, new FluidStack(CoreInit.Hydrogen,200), new FluidStack(CoreInit.Oxygen,100), null, null);
	}
}
