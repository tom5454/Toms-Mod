package com.tom.core.fluid;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;

public class FluidList {
	public static Map<String,Integer> map = new HashMap<String,Integer>();
	public static void put(Fluid fluid, int color){
		map.put(fluid.getName(), color);
	}
	public static int getColor(Fluid fluid){
		return map.get(fluid.getName());
	}
	
}
