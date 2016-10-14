package com.tom.recipes.handler;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLLog;

import com.tom.apis.RecipeData;
import com.tom.core.CoreInit;
import com.tom.factory.tileentity.TileEntityMBFluidPort;
import com.tom.factory.tileentity.TileEntityMBHatch;

public class CentrifugeRecipeHandler {
	public static Map<Fluid, RecipeData> recipeListFluid = new HashMap<Fluid, RecipeData>();
	public static Map<ItemStack, RecipeData> recipeListItem = new HashMap<ItemStack, RecipeData>();
	public static void add(Fluid input, int inputAmount, int energy, FluidStack f1){
		try{	
			if(input != null){
				if(!recipeListFluid.containsKey(input)){
					RecipeData current = new RecipeData(f1, energy, inputAmount);
					recipeListFluid.put(input, current);
				}else{
					FMLLog.bigWarning("FluidStack input has already registered! Ignore the adding!");
				}
			}else{
				FMLLog.bigWarning("FluidStack input is null! Ignore the adding!");
			}
		}catch(Exception e){
			CoreInit.log.error("Unexcepted error while adding a Recipe");
			e.printStackTrace();
		}
	}
	public static Object[] process(FluidStack input, ItemStack[] inputItem, double energy){
		Object[] o = new Object[14];
		if(input != null && recipeListFluid.containsKey(input.getFluid())){
			o = processFluid(input, energy);
		}else if(inputItem != null && recipeListItem.containsKey(inputItem)){
			o = processItem(inputItem[0], energy);
		}else{
			o[0] = false;
		}
		return o;
	}
	private static Object[] processFluid(FluidStack input, double energy){
		Object[] o = new Object[14];
		RecipeData current = recipeListFluid.get(input.getFluid());
		boolean ret = false;
		if(current != null){
			int e = current.energy;
			if(e <= energy){
				o[1] = energy - e;
				o = addOutput(o,current);
				ret = true;
				o[13] = current.inputAmount;
			}
			o[11] = current;
		}
		o[12] = false;
		o[0] = ret;
		return o;
	}
	private static Object[] processItem(ItemStack input, double energy){
		Object[] o = new Object[14];
		o[1] = energy;
		RecipeData current = recipeListItem.get(input);
		boolean ret = false;
		if(current != null){
			int e = current.energy;
			if(e <= energy){
				o[1] = energy - e;
				o = addOutput(o,current);
				ret = true;
			}
			o[11] = current;
		}
		o[12] = true;
		o[0] = ret;
		return o;
	}
	private static Object[] addOutput(Object[] o, RecipeData current){
		o[2] = current.processTime;
		if(current.f1 != null) o[3] = current.f1.copy();
		if(current.f2 != null) o[4] = current.f2.copy();
		if(current.f3 != null) o[5] = current.f3.copy();
		if(current.f4 != null) o[6] = current.f4.copy();
		if(current.itemstack1 != null) o[7] = current.itemstack1.copy();
		if(current.itemstack2 != null) o[8] = current.itemstack2.copy();
		if(current.itemstack3 != null) o[9] = current.itemstack3.copy();
		if(current.itemstack4 != null) o[10] = current.itemstack4.copy();
		return o;
	}
	public static boolean getTank(int inputAmount, double currentEnergy, int[] t1C, int[] t2C, int[] t3C, int[] t4C, int m, World world, RecipeData current){
		FluidStack t1 = getFluidStackFromTileEntity(world, t1C);
		FluidStack t2 = getFluidStackFromTileEntity(world, t2C);
		FluidStack t3 = getFluidStackFromTileEntity(world, t3C);
		FluidStack t4 = getFluidStackFromTileEntity(world, t4C);
		boolean ret = current.energy <= currentEnergy;
		ret = ret && inputAmount >= current.inputAmount * m;
		boolean f1 = t1 == null || current.f1 == null || t1.isFluidEqual(current.f1),
				f2 = t2 == null || current.f2 == null || t2.isFluidEqual(current.f2),
				f3 = t3 == null || current.f3 == null || t3.isFluidEqual(current.f3),
				f4 = t4 == null || current.f4 == null || t4.isFluidEqual(current.f4);
		ret = ret && f1 && f2 && f3 && f4;
		return ret;
	}
	public static boolean getItem(double currentEnergy, int[] t1C, int[] t2C, int[] t3C, int[] t4C, int m, World world, RecipeData current){
		ItemStack[] t1 = getItemStacksFromTileEntity(world, t1C);
		ItemStack[] t2 = getItemStacksFromTileEntity(world, t2C);
		ItemStack[] t3 = getItemStacksFromTileEntity(world, t3C);
		ItemStack[] t4 = getItemStacksFromTileEntity(world, t4C);
		boolean ret = current.energy <= currentEnergy;
		boolean found1 = false,found2 = false,found3 = false,found4 = false;
		//ret = ret && input.amount >= current.inputAmount * m;
		for(ItemStack stack : t1){
			if(stack != null && stack.isItemEqual(current.itemstack1)){
				found1 = true;
				break;
			}
		}
		for(ItemStack stack : t2){
			if(stack != null && stack.isItemEqual(current.itemstack2)){
				found2 = true;
				break;
			}
		}
		for(ItemStack stack : t3){
			if(stack != null && stack.isItemEqual(current.itemstack3)){
				found3 = true;
				break;
			}
		}
		for(ItemStack stack : t4){
			if(stack != null && stack.isItemEqual(current.itemstack4)){
				found4 = true;
				break;
			}
		}
		boolean f1 = t1 == null || current.itemstack1 == null || found1,
				f2 = t2 == null || current.itemstack2 == null || found2,
				f3 = t3 == null || current.itemstack3 == null || found3,
				f4 = t4 == null || current.itemstack4 == null || found4;
		ret = ret && f1 && f2 && f3 && f4;
		return ret;
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
	private static ItemStack[] getItemStacksFromTileEntity(World world, int[] coords){
		TileEntity tilee = world.getTileEntity(new BlockPos(coords[0], coords[1], coords[2]));
		if(tilee instanceof TileEntityMBFluidPort){
			TileEntityMBHatch te = (TileEntityMBHatch) tilee;
			return te.getStacks();
		}else{
			return null;
		}
		
	}
}
