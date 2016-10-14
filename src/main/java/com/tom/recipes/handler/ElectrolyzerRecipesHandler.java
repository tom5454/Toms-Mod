package com.tom.recipes.handler;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLLog;

import com.tom.apis.RecipeData;
import com.tom.factory.tileentity.TileEntityMBFluidPort;

public class ElectrolyzerRecipesHandler {
	public static Map<Fluid, RecipeData> recipeList = new HashMap<Fluid, RecipeData>();
	/**@param input FluidStack = input fluid and input amount
	 * @param energy Required energy for one cycle
	 * @param f1 Output 1 Nullable
	 * @param f2 Output 2 Nullable
	 * @param f3 Output 3 Nullable
	 * @param f4 Output 4 Nullable
	 * @author tom
	 * */
	public static void add(FluidStack input, int energy, FluidStack f1, FluidStack f2, FluidStack f3, FluidStack f4){
		if(input != null){
			if(!recipeList.containsKey(input.getFluid())){
				RecipeData current = new RecipeData(f1, f2, f3, f4, energy, input.amount);
				recipeList.put(input.getFluid(), current);
			}else{
				FMLLog.bigWarning("FluidStack input is already registered! Ignore the adding!");
			}
		}else{
			FMLLog.bigWarning("FluidStack input is null! Ignore the adding!");
		}
	}
	public static FluidStack[] get(FluidStack input, int m){
		if(input != null){
			RecipeData current = recipeList.get(input.getFluid());
			FluidStack f1 = current.f1.copy();
			FluidStack f2 = current.f2.copy();
			FluidStack f3 = current.f3.copy();
			FluidStack f4 = current.f4.copy();
			f1.amount = f1.amount * m;
			f2.amount = f2.amount * m;
			f3.amount = f3.amount * m;
			f4.amount = f4.amount * m;
			return new FluidStack[]{f1,f2,f3,f4};
		}else{
			return new FluidStack[]{};
		}
	}
	public static int getEnergyUsage(FluidStack input, int m){
		if(input != null){
			RecipeData current = recipeList.get(input.getFluid());
			return current.energy * (m + (m / 2));
		}else{
			return 0;
		}
	}
	public static int getFluidUsage(FluidStack input, int m){
		if(input != null){
			RecipeData current = recipeList.get(input.getFluid());
			return current.inputAmount * m;
		}else{
			return 0;
		}
	}
	public static boolean processable(FluidStack input, double currentEnergy, int[] t1C, int[] t2C, int[] t3C, int[] t4C, int m, World world){
		if(input != null){
			RecipeData current = recipeList.get(input.getFluid());
			FluidStack t1 = getFluidStackFromTileEntity(world, t1C);
			FluidStack t2 = getFluidStackFromTileEntity(world, t2C);
			FluidStack t3 = getFluidStackFromTileEntity(world, t3C);
			FluidStack t4 = getFluidStackFromTileEntity(world, t4C);
			if(current != null){
				int energyUsage = current.energy;
				boolean ret = energyUsage * (m + (m / 2)) <= currentEnergy;
				ret = ret && input.amount >= current.inputAmount * m;
				boolean f1 = t1 == null || current.f1 == null || t1.getFluid() == current.f1.getFluid(),
						f2 = t2 == null || current.f2 == null || t2.getFluid() == current.f2.getFluid(),
						f3 = t3 == null || current.f3 == null || t3.getFluid() == current.f3.getFluid(),
						f4 = t4 == null || current.f4 == null || t4.getFluid() == current.f4.getFluid();
				ret = ret && f1 && f2 && f3 && f4;
				return ret;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	public static boolean processable(FluidStack input, double currentEnergy, int[] t1, int[] t2, int[] t3, int[] t4, World world){
		return processable(input, currentEnergy, t1, t2, t3, t4, 1, world);
	}
	public static int[] process(FluidStack input, int m, int[] t1C, int[] t2C, int[] t3C, int[] t4C, double energy, boolean processable, World world){
		int inputAmount = 0;
		int t1A = 0;
		int t2A = 0;
		int t3A = 0;
		int t4A = 0;
		FluidStack t1 = getFluidStackFromTileEntity(world, t1C);
		FluidStack t2 = getFluidStackFromTileEntity(world, t2C);
		FluidStack t3 = getFluidStackFromTileEntity(world, t3C);
		FluidStack t4 = getFluidStackFromTileEntity(world, t4C);
		if(processable){
			RecipeData current = recipeList.get(input.getFluid());
			boolean f1N = t1 == null,
					f2N = t2 == null,
					f3N = t3 == null,
					f4N = t4 == null,
					c1N = current.f1 == null,
					c2N = current.f2 == null,
					c3N = current.f3 == null,
					c4N = current.f4 == null,
					c1 = (!f1N) && (!c1N) && t1.getFluid() == current.f1.getFluid(),
					c2 = (!f2N) && (!c2N) && t2.getFluid() == current.f2.getFluid(),
					c3 = (!f3N) && (!c3N) && t3.getFluid() == current.f3.getFluid(),
					c4 = (!f4N) && (!c4N) && t4.getFluid() == current.f4.getFluid()/*,
					f1 = f1N || c1N || c1,
					f2 = f2N || c2N || c2,
					f3 = f3N || c3N || c3,
					f4 = f4N || c4N || c4,
					ok = f1 && f2 && f3 && f4*/;
			if(!c1N && (c1 || f1N)){
				t1A = current.f1.amount * m;
			}
			if(!c2N && (c2 || f2N)){
				t2A = current.f2.amount * m;
			}
			if(!c3N && (c3 || f3N)){
				t3A = current.f3.amount * m;
			}
			if(!c4N && (c4 || f4N)){
				t4A = current.f4.amount * m;
			}
			inputAmount = current.inputAmount * m;
		}
		return new int[]{inputAmount,t1A,t2A,t3A,t4A};
	}
	public static int calcEnergy(FluidStack input, int energy, int m){
		return energy - getEnergyUsage(input, m);
	}
	public static FluidStack[] getFluidStackFromTileEntity(TileEntityMBFluidPort[] tilee){
		FluidStack[] ret = new FluidStack[4];
		for(int i = 0;i<tilee.length && i<4;i++){
			if (tilee[i] != null){
				ret[i] = tilee[i].getFluidStack();
			}else{
				ret[i] = null;
			}
		}
		return ret;
	}
	public static Fluid[] getFluid(FluidStack input){
		RecipeData current = recipeList.get(input.getFluid());
		Fluid f1 = current.f1 != null ? current.f1.getFluid() : null;
		Fluid f2 = current.f2 != null ? current.f2.getFluid() : null;
		Fluid f3 = current.f3 != null ? current.f3.getFluid() : null;
		Fluid f4 = current.f4 != null ? current.f4.getFluid() : null;
		return new Fluid[]{f1,f2,f3,f4};
	}
	private static FluidStack getFluidStackFromTileEntity(World world, int[] coords){
		TileEntity tilee = world.getTileEntity(new BlockPos(coords[0], coords[1], coords[2]));
		if(tilee instanceof TileEntityMBFluidPort){
			TileEntityMBFluidPort te = (TileEntityMBFluidPort) tilee;
			return te.getFluidStack();
		}else{
			return null;
		}
		
	}
	
}
