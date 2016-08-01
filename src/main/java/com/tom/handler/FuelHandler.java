package com.tom.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.tom.apis.TomsModUtils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;

public class FuelHandler implements IFuelHandler{
	public static final FuelHandler INSTANCE = new FuelHandler();
	private static Map<ItemStack, Integer> extra = new HashMap<ItemStack, Integer>();
	@Override
	public int getBurnTime(ItemStack fuel) {
		return fuel != null && fuel.getItem() instanceof IBurnable ? ((IBurnable)fuel.getItem()).getBurnTime(fuel) : getOtherValue(fuel);
	}
	private static int getOtherValue(ItemStack stack){
		for(Entry<ItemStack, Integer> e : extra.entrySet()){
			if(TomsModUtils.areItemStacksEqual(stack, e.getKey(), true, false, false)){
				return e.getValue();
			}
		}
		return 0;
	}
	public static interface IBurnable{
		int getBurnTime(ItemStack stack);
	}
	public static void registerExtraFuelHandler(ItemStack stack, int burnTime){
		extra.put(stack, burnTime);
	}
}
