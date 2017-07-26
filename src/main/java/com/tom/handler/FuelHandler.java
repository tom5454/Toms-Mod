package com.tom.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import com.google.common.base.Predicate;

import com.tom.apis.DefaultedHashMap;
import com.tom.apis.TMLogger;
import com.tom.apis.TomsModUtils;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;

public class FuelHandler implements IFuelHandler {
	public static final FuelHandler INSTANCE = new FuelHandler();
	private static final Map<ItemStack, Integer> extra = new HashMap<>();
	private static final Map<Fluid, Integer> fluidBurnTimes = new DefaultedHashMap<>(-1);
	public static final Predicate<Fluid> IS_FLUID_FUEL_PREDICATE = new Predicate<Fluid>() {

		@Override
		public boolean apply(Fluid input) {
			return input != null ? getBurnTimeForFluid(input) > 0 : false;
		}
	};
	public static final Predicate<FluidStack> IS_FLUID_STACK_FUEL_PREDICATE = new Predicate<FluidStack>() {

		@Override
		public boolean apply(FluidStack input) {
			return input != null ? IS_FLUID_FUEL_PREDICATE.apply(input.getFluid()) : false;
		}
	};

	@Override
	public int getBurnTime(ItemStack fuel) {
		return fuel != null && fuel.getItem() instanceof IBurnable ? ((IBurnable) fuel.getItem()).getBurnTime(fuel) : getOtherValue(fuel);
	}

	private static int getOtherValue(ItemStack stack) {
		for (Entry<ItemStack, Integer> e : extra.entrySet()) {
			if (TomsModUtils.areItemStacksEqual(stack, e.getKey(), true, false, false)) { return e.getValue(); }
		}
		return 0;
	}

	public static interface IBurnable {
		int getBurnTime(ItemStack stack);
	}

	public static void registerExtraFuelHandler(ItemStack stack, int burnTime) {
		extra.put(stack, burnTime);
	}

	public static void registerFluidFuelHandler(Fluid fluid, int burnTime) {
		fluidBurnTimes.put(fluid, burnTime);
	}

	private static void registerExtraFuelHandler(Fluid fluid, int burnTime) {
		burnTime = Config.getFluidBurnTime(fluid.getName(), burnTime);
		registerFluidFuelHandler(fluid, burnTime);
		callEnderio(fluid, burnTime);
		callRailcraft(fluid, burnTime);
	}

	public static void init() {
		TMLogger.info("Loading Fluid Fuel Handler");
		registerExtraFuelHandler(CoreInit.creosoteOil.get(), 200);
		registerExtraFuelHandler(CoreInit.oil.get(), 180);
		registerExtraFuelHandler(CoreInit.fuel.get(), 400);
		registerExtraFuelHandler(CoreInit.lpg.get(), 400);
		registerExtraFuelHandler(CoreInit.kerosene.get(), 500);
		checkAndRegister(Constants.IE_BIODIESEL, 250);
		checkAndRegister(Constants.FORESTRY_BIOFUEL, 250);
		checkAndRegister(EIOConstants.HOOTCH_NAME, 100);
		checkAndRegister(EIOConstants.ROCKET_FUEL_NAME, 240);
		checkAndRegister(EIOConstants.FIRE_WATER_NAME, 180);
	}

	private static void callEnderio(Fluid fluid, int burnTime) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(EIOConstants.KEY_FLUID_NAME, fluid.getName());
		tag.setInteger(EIOConstants.KEY_TOTAL_BURN_TIME, Config.getEIOBurnTime(fluid.getName(), burnTime * 5));
		tag.setInteger(EIOConstants.KEY_POWER_PER_CYCLE, Config.getEIOPowerPerCycle(fluid.getName(), burnTime * 2 / 10));
		FMLInterModComms.sendMessage(Configs.EIO, EIOConstants.FLUID_FUEL_ADD, tag);
	}

	private static void callRailcraft(Fluid fluid, int burnTime) {
		FMLInterModComms.sendMessage(Configs.Railcraft, RailcraftConstants.FLUID_FUEL_ADD, RailcraftConstants.format(fluid.getName(), burnTime));
	}

	public static class EIOConstants {
		/**
		 * Key for an NBT message to register a fluid fuel. Calls
		 * {@link FluidFuelRegister#addFuel(net.minecraft.nbt.NBTTagCompound)}
		 * with the NBT value of the message.
		 */
		public static final String FLUID_FUEL_ADD = "fluidFuel:add";
		public static final String KEY_FLUID_NAME = "fluidName";
		public static final String KEY_POWER_PER_CYCLE = "powerPerCycle";
		public static final String KEY_TOTAL_BURN_TIME = "totalBurnTime";
		public static final String HOOTCH_NAME = "hootch";
		public static final String ROCKET_FUEL_NAME = "rocket_fuel";
		public static final String FIRE_WATER_NAME = "fire_water";
	}

	public static class RailcraftConstants {
		public static final String FLUID_FUEL_ADD = "boiler-fuel-liquid";

		public static String format(String fluidName, int burnTime) {
			int heat = burnTime * 2;
			return fluidName + "@" + Config.getRailcraftHeat(fluidName, heat);
		}
	}

	public static class Constants {
		public static final String IE_BIODIESEL = "biodiesel";
		public static final String FORESTRY_BIOFUEL = "bio.ethanol";
	}

	private static void checkAndRegister(String name, int burnTime) {
		burnTime = Config.getFluidBurnTime(name, burnTime);
		if (burnTime > 0) {
			TMLogger.info("Checking fluid " + name + " for fuel handler. Burn time: " + burnTime);
			if (FluidRegistry.isFluidRegistered(name)) {
				TMLogger.info("Fluid " + name + " successfully found. Fuel value: " + burnTime);
				registerFluidFuelHandler(FluidRegistry.getFluid(name), burnTime);
			} else {
				TMLogger.info("Fluid " + name + " not found.");
			}
		}
	}

	public static int getBurnTimeForFluid(FluidStack stack) {
		return getBurnTimeForFluid(stack.getFluid());
	}

	public static int getBurnTimeForFluid(Fluid fluid) {
		return fluidBurnTimes.get(fluid);
	}
}
