package com.tom.config;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import net.minecraft.world.World;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.tom.api.energy.EnergyType;
import com.tom.core.CoreInit;
import com.tom.handler.FuelHandler;
import com.tom.thirdparty.waila.WailaHandler;
import com.tom.util.TMLogger;
import com.tom.worldgen.WorldGen.OreGenEntry;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaRegistrar;

public class Config {
	public static Configuration configCore, configTransport, configDefense, configEnergy, configWorldGen,
	configStorage, configTools, configFluidFuels, configFactory;
	private static final String CATEGORY_RECIPES = "recipes";
	private static final String CATEGORY_ENERGY_VALUES = "energy_values";
	public static final String[] CATEGORIES = new String[]{Configuration.CATEGORY_GENERAL};
	private static final String CATEGORY_MINECRAFT = "Minecraft", CATEGORY_WAILA = "Waila",
			CATEGORY_STORAGE_SYSTEM = "storageSystem", CATEGORY_ENDERIO = "Ender IO", CATEGORY_RAILCRAFT = "Railcraft";
	public static final String RUBBER_TREES_FEATURE = "rubberTrees", OIL_LAKE = "oilLakes", BROKEN_TREE = "brokenTrees";
	public static boolean enableAdventureItems;
	public static boolean enableHardModeStarting, enableDefenseSystem, enableResearchSystem,
	logOredictNames, easyPlates;
	public static boolean disableWoodenTools, disableStoneTools, disableIronTools, disableGoldTools,
	disableDiamondTools, changeDiamondToolsRecipe, hardToolRecipes, diamondToolsNeedCraftingTable,
	diamondToolHeadsCraftable, toolsNeedHammer, driveKeepInv;
	public static String[] nerfedTools;
	public static boolean enableGrassDrops;
	public static boolean enableCommandExecutor;
	public static double holotapeSpeed, storageSystemUsage;
	public static boolean enableConveyorBeltAnimation;
	public static double forceMultipier, defenseStationUsageDivider;
	public static int minecartMaxStackSize;
	public static boolean saveDeathPoints;
	public static int commandFillMaxSize, scientistHouseWeight, placedBlockLifespan;
	public static boolean commandFillLogging, wailaUsesMultimeterForce, logConfigWarnings, addUnbreakableElytraRecipe,
	enableTickSpeeding, enableChannels, enableProcessors, genScientistHouse, disableScanning;
	public static List<String> warnMessages = new ArrayList<>();
	public static List<Integer> notOverworld, notNether, notEnd;
	public static int[] lvPower, mvPower, hvPower, NOT_OVERWORLD_DEF = new int[]{-1, 1};
	public static int[] max_speed_upgrades = new int[3];
	public static boolean enableHammerOreMining, researchTableRequiresOpenUI;

	public static void init(File configFile) {
		CoreInit.log.info("Init Configuration");
		new File(configFile, "fonts").mkdirs();
		new File(configFile, "recipes/research_table").mkdirs();
		File coreConfigFile = new File(configFile.getAbsolutePath(), "Core.cfg");
		if (configCore == null) {
			configCore = new Configuration(coreConfigFile);
			configCore.load(); // get the actual data from the file.
		}
		File transportConfigFile = new File(configFile.getAbsolutePath(), "transport.cfg");
		if (configTransport == null) {
			configTransport = new Configuration(transportConfigFile);
			configTransport.load(); // get the actual data from the file.
		}
		File defenseConfigFile = new File(configFile.getAbsolutePath(), "defense.cfg");
		if (configDefense == null) {
			configDefense = new Configuration(defenseConfigFile);
			configDefense.load(); // get the actual data from the file.
		}
		File energyConfigFile = new File(configFile.getAbsolutePath(), "energy.cfg");
		if (configEnergy == null) {
			configEnergy = new Configuration(energyConfigFile);
			configEnergy.load(); // get the actual data from the file.
		}
		File worldgenConfigFile = new File(configFile.getAbsolutePath(), "world_generation.cfg");
		if (configWorldGen == null) {
			configWorldGen = new Configuration(worldgenConfigFile);
			configWorldGen.load(); // get the actual data from the file.
		}

		File storageConfigFile = new File(configFile.getAbsolutePath(), "storage.cfg");
		if (configStorage == null) {
			configStorage = new Configuration(storageConfigFile);
			configStorage.load(); // get the actual data from the file.
		}

		File toolsConfigFile = new File(configFile.getAbsolutePath(), "tools_and_combat.cfg");
		if (configTools == null) {
			configTools = new Configuration(toolsConfigFile);
			configTools.load(); // get the actual data from the file.
		}

		File fluidsConfigFile = new File(configFile.getAbsolutePath(), "fluids.cfg");
		if (configFluidFuels == null) {
			configFluidFuels = new Configuration(fluidsConfigFile);
			configFluidFuels.load(); // get the actual data from the file.
		}

		File factoryConfigFile = new File(configFile.getAbsolutePath(), "factory.cfg");
		if (configFactory == null) {
			configFactory = new Configuration(factoryConfigFile);
			configFactory.load(); // get the actual data from the file.
		}

		int[] intArray;
		Property property = configCore.get(Configuration.CATEGORY_GENERAL, "Enable Adventure Items", true);
		enableAdventureItems = property.getBoolean(true);
		property.setRequiresMcRestart(true);

		if (!enableAdventureItems) {
			logWarn("[Item Registry] Adventure Items are disabled.");
		}

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Enable Hard Mode", false);
		property.setComment("Logs and Planks require an axe.");
		enableHardModeStarting = property.getBoolean(false);
		property.setRequiresMcRestart(true);

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Enable Flint drop", false);
		enableGrassDrops = property.getBoolean(false);
		property.setRequiresMcRestart(true);

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Enable Command Executor", true);
		enableCommandExecutor = property.getBoolean(true);
		property.setRequiresMcRestart(true);

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Holotape Speed", 1D);
		property.setComment("Speed of the Holotape Writer. How many characters does the writer write in 1 tick. Default 1");
		holotapeSpeed = property.getDouble(1);

		property = configDefense.get(Configuration.CATEGORY_GENERAL, "Force Power Convert Multipier", 2);
		property.setComment("Force Converter Multiplier calculation: HV / multiplier. Default: 2");
		forceMultipier = property.getDouble();
		property.setRequiresMcRestart(true);

		property = configTransport.get(CATEGORY_MINECRAFT, "Max Stack Size of Minecarts", 3);
		minecartMaxStackSize = property.getInt();
		property.setRequiresMcRestart(true);

		configCore.addCustomCategoryComment(CATEGORY_RECIPES, "All recipes related configurations are here.");

		property = configCore.get(CATEGORY_MINECRAFT, "Fill Command Max Blocks", 32768);
		property.setComment("Maximum amount of blocks that the fill command can replace. Default: 32768");
		commandFillMaxSize = property.getInt(32768);

		property = configCore.get(CATEGORY_MINECRAFT, "Fill Command Log Warning Message", true);
		property.setComment("Logs a message on the Server when filling more than 8192 Blocks.");
		commandFillLogging = property.getBoolean(true);

		addMinecraftComment(configCore);
		addMinecraftComment(configTransport);
		addWailaComment(configEnergy);

		property = configEnergy.get(CATEGORY_WAILA, "Force Waila on Energy Handler", false);
		property.setComment("Forces Waila to require a multimeter in the hotbar. (Also removes the config option on the Waila modules page) (Default: false)");
		wailaUsesMultimeterForce = property.getBoolean(false);

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Log Configuration Warning Messages", true);
		property.setComment("Logs a message in the init states if a warning is in the configuration.");
		logConfigWarnings = property.getBoolean(true);
		if (!logConfigWarnings) {
			TMLogger.warn("[Config]: Warning messages are disabled.");
		}

		property = configCore.get(CATEGORY_RECIPES, "Unbreakable Elytra Recipe", true);
		property.setComment("Add Unbreakable Elytra Crafting recipe");
		addUnbreakableElytraRecipe = property.getBoolean(true);

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Enable Tick Speeding", true);
		property.setComment("Allow external devices to speed up the machines ticks");
		enableTickSpeeding = property.getBoolean(true);

		property = configDefense.get(Configuration.CATEGORY_GENERAL, "Enable Defense System", true);
		property.setComment("Enable Base Defense Items And Blocks");
		enableDefenseSystem = property.getBoolean(true);

		if (configCore.hasKey("backup", "Enable Auto Backup")) {
			property = configCore.get("backup", "Enable Auto Backup", true);
			property.setComment("MOVED TO A SEPARATE MOD!!!");
		}

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Enable Research System", true);
		property.setComment("Enable Research System and Custom Crafting. Can cause recipe conflict if disabled. (Default: true)");
		enableResearchSystem = property.getBoolean(true);

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Log Ore Dictionary Names", false);
		logOredictNames = property.getBoolean(false);

		property = configStorage.get(CATEGORY_STORAGE_SYSTEM, "Enable Channels", true);
		enableChannels = property.getBoolean(true);

		property = configStorage.get(CATEGORY_STORAGE_SYSTEM, "Enable Processor Requirement", true);
		enableProcessors = property.getBoolean(true);

		property = configWorldGen.get("village_gen", "Generate Scientist House", true);
		genScientistHouse = property.getBoolean(true);

		scientistHouseWeight = configWorldGen.getInt("Scientist House Weight", "village_gen", 5, 1, 100, "Weight of generating a scientist house", "");

		property = configStorage.get(CATEGORY_STORAGE_SYSTEM, "Storage System Power Convert Rate", 2D);
		property.setComment("Storage System Power Convertion Rate (1 HV = Set Units) [range: 1 ~ 1024, default: 2]");
		storageSystemUsage = Math.max(Math.min(property.getDouble(2D), 1024), 1);

		property = configDefense.get(Configuration.CATEGORY_GENERAL, "Defense Station Power Usage Divider", 256);
		property.setComment("default: 256, must not be 0");
		if (property.getDouble(256) == 0) {
			property.set(256);
			logWarn("[Config]: Value 'Defense Station Power Usage Divider' is equal to 0! Using default value.");
		}
		defenseStationUsageDivider = property.getDouble(256);

		property = configTools.get(Configuration.CATEGORY_GENERAL, "Disable Wooden Tools", false);
		property.setComment("Disables all wooden tools completelly, only useable for crafting");
		disableWoodenTools = property.getBoolean(false);

		property = configTools.get(Configuration.CATEGORY_GENERAL, "Disable Stone Tools", false);
		property.setComment("Disables all stone tools completelly, only useable for crafting");
		disableStoneTools = property.getBoolean(false);

		property = configTools.get(Configuration.CATEGORY_GENERAL, "Disable Iron Tools", false);
		property.setComment("Disables all iron tools completelly, only useable for crafting");
		disableIronTools = property.getBoolean(false);

		property = configTools.get(Configuration.CATEGORY_GENERAL, "Disable Gold Tools", false);
		property.setComment("Disables all gold tools completelly, only useable for crafting");
		disableGoldTools = property.getBoolean(false);

		property = configTools.get(Configuration.CATEGORY_GENERAL, "Disable Diamond Tools", false);
		property.setComment("Disables all diamond tools completelly, only useable for crafting");
		disableDiamondTools = property.getBoolean(false);

		property = configTools.get(Configuration.CATEGORY_GENERAL, "Extra Nerfed Tools", new String[0]);
		property.setComment("Disables all tools completelly, only useable for crafting");
		nerfedTools = property.getStringList();

		property = configTools.get(Configuration.CATEGORY_GENERAL, "Change Diamond Tools Recipe", false);
		changeDiamondToolsRecipe = property.getBoolean(false);

		property = configTools.get(Configuration.CATEGORY_GENERAL, "Diamond Tools Need Crafting Table to Craft", false);
		property.setComment("Diamond tools need a crafting table to craft, also applies 'Change Diamond Tools Recipe' config");
		diamondToolsNeedCraftingTable = property.getBoolean(false);
		if (diamondToolsNeedCraftingTable)
			changeDiamondToolsRecipe = true;

		property = configTools.get(Configuration.CATEGORY_GENERAL, "Hard Tool Recipes", false);
		property.setComment("Iron, Gold and Diamond Tools Has a harder recipe, also applies 'Change Diamond Tools Recipe' config");
		hardToolRecipes = property.getBoolean(false);
		if (hardToolRecipes)
			changeDiamondToolsRecipe = true;

		property = configTools.get(Configuration.CATEGORY_GENERAL, "Diamond Tool Heads Craftable", false);
		property.setComment("Diamond tool heads can be craftable in a crafting table, also applies 'Change Diamond Tools Recipe' config");
		diamondToolHeadsCraftable = property.getBoolean(false);
		if (diamondToolHeadsCraftable)
			changeDiamondToolsRecipe = true;

		property = configTools.get(Configuration.CATEGORY_GENERAL, "Metal tools need hammer", true);
		property.setComment("Metal tools need a hammer in the crafting recipe");
		toolsNeedHammer = property.getBoolean(true);

		property = configWorldGen.get("dim", "Not Overworld like dimensions", NOT_OVERWORLD_DEF);
		property.setComment("Disable Overworld ore generation in this dimension");
		notOverworld = new ArrayList<>();
		intArray = property.getIntList();
		for (int i = 0;i < intArray.length;i++)
			notOverworld.add(intArray[i]);

		property = configWorldGen.get("dim", "Not Nether like dimensions", new int[]{0, 1});
		property.setComment("Disable Nether ore generation in this dimension");
		notNether = new ArrayList<>();
		intArray = property.getIntList();
		for (int i = 0;i < intArray.length;i++)
			notNether.add(intArray[i]);

		property = configWorldGen.get("dim", "Not End like dimensions", new int[]{0, -1});
		property.setComment("Disable End ore generation in this dimension");
		notEnd = new ArrayList<>();
		intArray = property.getIntList();
		for (int i = 0;i < intArray.length;i++)
			notEnd.add(intArray[i]);

		registerFeature(RUBBER_TREES_FEATURE, NOT_OVERWORLD_DEF);
		registerFeature(OIL_LAKE, NOT_OVERWORLD_DEF);
		registerFeature(BROKEN_TREE, NOT_OVERWORLD_DEF);

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Enable Scanning", true);
		property.setComment("Enable Scanning Requirement for Researches");
		disableScanning = !property.getBoolean(true);

		property = configStorage.get(Configuration.CATEGORY_GENERAL, "Drive Keep Inventory", true);
		property.setComment("Can Drive keep its inventory uppon breaking, doesn't effect already broken drives, can cause lag because of large amounts of NBT data");
		driveKeepInv = property.getBoolean(true);

		int LVCableStorage = configEnergy.getInt("LV Cable storage", CATEGORY_ENERGY_VALUES, 100000, 100, Integer.MAX_VALUE, "");
		int maxLVCableIn = configEnergy.getInt("LV Cable Max In", CATEGORY_ENERGY_VALUES, 100000, 100, Integer.MAX_VALUE, "");
		int maxLVCableOut = configEnergy.getInt("LV Cable Max Out", CATEGORY_ENERGY_VALUES, 100000, 100, Integer.MAX_VALUE, "");

		int MVCableStorage = configEnergy.getInt("MV Cable storage", CATEGORY_ENERGY_VALUES, 100000, 100, Integer.MAX_VALUE, "");
		int maxMVCableIn = configEnergy.getInt("MV Cable Max In", CATEGORY_ENERGY_VALUES, 100000, 100, Integer.MAX_VALUE, "");
		int maxMVCableOut = configEnergy.getInt("MV Cable Max Out", CATEGORY_ENERGY_VALUES, 100000, 100, Integer.MAX_VALUE, "");

		int HVCableStorage = configEnergy.getInt("HV Cable storage", CATEGORY_ENERGY_VALUES, 100000, 100, Integer.MAX_VALUE, "");
		int maxHVCableIn = configEnergy.getInt("HV Cable Max In", CATEGORY_ENERGY_VALUES, 100000, 100, Integer.MAX_VALUE, "");
		int maxHVCableOut = configEnergy.getInt("HV Cable Max Out", CATEGORY_ENERGY_VALUES, 100000, 100, Integer.MAX_VALUE, "");

		lvPower = new int[]{LVCableStorage, maxLVCableIn, maxLVCableOut};
		mvPower = new int[]{MVCableStorage, maxMVCableIn, maxMVCableOut};
		hvPower = new int[]{HVCableStorage, maxHVCableIn, maxHVCableOut};

		placedBlockLifespan = configDefense.getInt("Placed Block Lifespan", "block_protector", 200, -1, Integer.MAX_VALUE, "");

		property = configCore.get(Configuration.CATEGORY_GENERAL, "DebugMode", false);
		property.setComment("WARNING!! CAN CRASH YOUR GAME IF YOU ENABLE THIS! YOU SHOULD ONLY USE THIS IF YOU ARE DEBUGGING!");
		if (property.getBoolean(false)) {
			CoreInit.isDebugging = true;
			logWarn("****************************************");
			for (int i = 0;i < 6;i++)
				logWarn("!! DEBUG MODE ENABLED !!");
			logWarn("****************************************");
		}

		//easyPlates = configCore.getBoolean("Loseless Plates", CATEGORY_RECIPES, false, "");

		String cat = "electric_machines" + Configuration.CATEGORY_SPLITTER + "max_speed_upgrades";
		max_speed_upgrades[2] = configFactory.getInt("lv", cat, 8, 1, 64, "");
		max_speed_upgrades[1] = configFactory.getInt("mv", cat, 16, 1, 64, "");
		max_speed_upgrades[0] = configFactory.getInt("hv", cat, 32, 1, 64, "");

		enableHammerOreMining = configCore.getBoolean("Enable Hammer Ore Mining", CATEGORY_RECIPES, true, "Enable Curshed Ore Drops From blocks");

		researchTableRequiresOpenUI = configCore.getBoolean("researchTableRequiresOpenUI", Configuration.CATEGORY_GENERAL, true, "Research Table Requires Open GUI to process research and crafting");
	}

	public static void save() {
		CoreInit.log.info("Saving configuration");
		configCore.save();
		configTransport.save();
		configDefense.save();
		configEnergy.save();
		configWorldGen.save();
		configStorage.save();
		configTools.save();
		configFactory.save();
	}

	private static void addMinecraftComment(Configuration c) {
		c.addCustomCategoryComment(CATEGORY_MINECRAFT, "All minecraft related configurations are here.");
	}

	private static void addWailaComment(Configuration c) {
		c.addCustomCategoryComment(CATEGORY_WAILA, "All Waila related configurations are here.");
	}

	public static void updateConfig(boolean isInWorld) {
		TMLogger.info("Updating configs...");
	}

	public static boolean isWailaUsesMultimeter(IWailaConfigHandler config) {
		return wailaUsesMultimeterForce ? true : config.getConfig(WailaHandler.ENERGY_HANDLER_ID);
	}

	public static void initWailaConfigs(IWailaRegistrar registrar) {
		registrar.addConfigRemote("Tom's Mod", WailaHandler.ENERGY_HANDLER_ID);
	}

	public static boolean enableOreGen(OreGenEntry name) {
		Property property = configWorldGen.get("ore_generation", "Generate " + name.name, true);
		property.setComment("Generate " + name.name + " in world. (Default: true)");
		boolean ret = property.getBoolean(true);
		int yStart = configWorldGen.getInt("Y Start " + name.name, "ore_generation_adv", name.yStart, 1, 255, "Generate " + name.name + " above set value", name.name + ".ystart");
		int yEnd = configWorldGen.getInt("Y End " + name.name, "ore_generation_adv", name.yStart + name.ySize, 1, 255, "Generate " + name.name + " below set value", name.name + ".yend");
		name.yStart = Math.min(yEnd, yStart);
		yEnd = Math.max(yEnd, yStart);
		name.ySize = yEnd - name.yStart;
		name.maxAmount = configWorldGen.getInt("Max Per Chunk " + name.name, "ore_generation_adv", name.maxAmount, 1, 128, "Max Ore per Chunk " + name.name, name.name + ".maxamount");
		name.veinSize = configWorldGen.getInt("Vein Size Per Chunk " + name.name, "ore_generation_adv", name.veinSize, 1, 128, "Vein Size per Chunk " + name.name, name.name + ".veinsize");
		return ret;
	}

	public static boolean enableToolGroup(String name) {
		Property property = configTools.get("tool_config", "Enable " + name, true);
		property.setComment("Enable " + name + " tools to be added. (Default: true)");
		return property.getBoolean(true);
	}

	public static void printWarnings() {
		if (logConfigWarnings) {
			for (int i = 0;i < warnMessages.size();i++) {
				TMLogger.warn("[Mod Configuration Warning]: " + warnMessages.get(i));
			}
		}
	}

	public static void logWarn(String msg) {
		warnMessages.add(msg);
		TMLogger.warn(msg);
	}
	public static void logErr(String msg) {
		warnMessages.add(msg);
		TMLogger.error(msg);
	}

	private static Map<String, Predicate<World>> features = new HashMap<>();
	private static final Predicate<World> FALSE = p -> false;

	public static boolean enableFeature(String name, World world) {
		if (features.containsKey(name)) {
			return features.get(name).test(world);
		} else {
			return false;
		}
	}

	public static void registerFeature(String name, int[] base) {
		String catName = "feature" + Configuration.CATEGORY_SPLITTER + name;
		Property property = configWorldGen.get(catName, "Generate", true);
		property.setComment("Generate " + name + " in world");
		if (!property.getBoolean(true)) {
			features.put(name, FALSE);
			logWarn("Feature '" + name + "' is disabled");
		} else {
			property = configWorldGen.get(catName, "Blacklist dimenstions", base);
			List<Integer> list = new ArrayList<>();
			int[] intArray = property.getIntList();
			for (int i = 0;i < intArray.length;i++)
				list.add(intArray[i]);
			features.put(name, w -> !list.contains(w.provider.getDimension()));
		}
	}

	private static List<String> ignoreFluidNames = new ArrayList<>();

	public static void initFluids() {
		for (Entry<String, Fluid> e : FluidRegistry.getRegisteredFluids().entrySet()) {
			if (!ignoreFluidNames.contains(e.getKey())) {
				int burnTime = configFluidFuels.getInt("Burn Time " + e.getKey(), Configuration.CATEGORY_GENERAL, 0, 0, Integer.MAX_VALUE, "Fluid Burn Time for " + e.getKey());
				if (burnTime > 0) {
					FuelHandler.registerFluidFuelHandler(e.getValue(), burnTime);
				}
			}
		}
		configFluidFuels.save();
	}

	public static int getFluidBurnTime(String name, int burnTime) {
		ignoreFluidNames.add(name);
		return configFluidFuels.getInt("Burn Time:" + name, Configuration.CATEGORY_GENERAL, burnTime, 0, Integer.MAX_VALUE, "Fluid Burn Time for " + name);
	}

	public static int getEIOBurnTime(String name, int burnTime) {
		return configFluidFuels.getInt("EIO Burn Time:" + name, CATEGORY_ENDERIO, burnTime, 0, Integer.MAX_VALUE, "Ender IO Fluid Burn Time for " + name);
	}

	public static int getEIOPowerPerCycle(String name, int burnTime) {
		return configFluidFuels.getInt("EIO Power Per Cycle:" + name, CATEGORY_ENDERIO, burnTime, 0, Integer.MAX_VALUE, "Ender IO Power Per Cycle Value for " + name);
	}

	public static int getRailcraftHeat(String name, int heat) {
		return configFluidFuels.getInt("Railcraft Heat:" + name, CATEGORY_RAILCRAFT, heat, 0, Integer.MAX_VALUE, "Railcraft Heat Value for " + name);
	}

	public static int[] getPowerValues(EnergyType etype) {
		switch (etype) {
		case FORCE:
			return new int[3];
		case HV:
			return hvPower;
		case LV:
			return lvPower;
		case MV:
			return mvPower;
		default:
			return new int[3];
		}
	}

	public static boolean changeRecipe(String name) {
		return configCore.getBoolean("O " + name, CATEGORY_RECIPES, true, "Overwrite " + name + " recipe");
	}

	public static void error(String msg, Throwable e) {
		logErr("****************************************");
		warnMessages.add(msg);
		e.printStackTrace(ERRORSTREAM);
		TMLogger.error(msg, e);
		logErr("****************************************");
	}
	private static PrintStream ERRORSTREAM = new PrintStream(System.err){
		@Override
		public void println(Object x) {
			String s = String.valueOf(x);
			warnMessages.add(s);
		}
	};
}
