package com.tom.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mapwriterTm.util.Reference;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.tom.apis.TMLogger;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.thirdparty.waila.Waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaRegistrar;

public class Config {
	public static Configuration configCore, configMinimap, configTransport, configDefense, configEnergy, configWorldGen;
	private static final String CATEGORY_RECIPES = "recipes";
	public static final String[] CATEGORIES = new String[]{Configuration.CATEGORY_GENERAL};
	private static final String CATEGORY_MINECRAFT = "Minecraft", CATEGORY_WAILA = "Waila";
	@Deprecated
	private static final String CATEGORY_WORLDGEN = "WorldGen";
	public static boolean enableAdventureItems;
	public static boolean enableHardMode;
	//public static boolean enable18AdvMode;
	//public static boolean enableFlintAxe;
	public static boolean enableGrassDrops;
	public static boolean enableCommandExecutor;
	public static boolean enableMiniMap;
	//public static boolean enableMineCamera;
	public static double holotapeSpeed;
	public static double markerUnloadDist;
	public static boolean enableConveyorBeltAnimation;
	public static double forceMultipier;
	public static int minecartMaxStackSize;
	public static boolean saveDeathPoints;
	public static UUID tomsmodFakePlayerUUID;
	//public static String minecameraCommand;
	public static int commandFillMaxSize;
	public static boolean commandFillLogging, wailaUsesMultimeterForce, advEntityTrackerTexture, enableBronkenTreeGen, genOilLakes, genRubberTrees, logConfigWarnings, addUnbreakableElytraRecipe, enableTickSpeeding;
	public static List<String> warnMessages = new ArrayList<String>();
	public static void init(File configFile){
		CoreInit.log.info("Init Configuration");
		File coreConfigFile = new File(configFile.getAbsolutePath(), "Core.cfg");
		if(configCore == null) {
			configCore = new Configuration(coreConfigFile);
			configCore.load(); // get the actual data from the file.
		}
		File minimapConfigFile = new File(configFile.getAbsolutePath(), "minimap.cfg");
		if(configMinimap == null) {
			configMinimap = new Configuration(minimapConfigFile);
			configMinimap.load(); // get the actual data from the file.
		}
		File transportConfigFile = new File(configFile.getAbsolutePath(), "transport.cfg");
		if(configTransport == null) {
			configTransport = new Configuration(transportConfigFile);
			configTransport.load(); // get the actual data from the file.
		}
		File defenseConfigFile = new File(configFile.getAbsolutePath(), "defense.cfg");
		if(configDefense == null) {
			configDefense = new Configuration(defenseConfigFile);
			configDefense.load(); // get the actual data from the file.
		}
		File energyConfigFile = new File(configFile.getAbsolutePath(), "energy.cfg");
		if(configEnergy == null) {
			configEnergy = new Configuration(energyConfigFile);
			configEnergy.load(); // get the actual data from the file.
		}
		File worldgenConfigFile = new File(configFile.getAbsolutePath(), "world_generation.cfg");
		if(configWorldGen == null) {
			configWorldGen = new Configuration(worldgenConfigFile);
			configWorldGen.load(); // get the actual data from the file.
		}
		//configMinimap.addCustomCategoryComment(categoryMinimap, "Minimap settings");

		Property property = configCore.get(Configuration.CATEGORY_GENERAL, "Enable Adventure Items", true);
		enableAdventureItems = property.getBoolean(true);
		property.setRequiresMcRestart(true);

		if(!enableAdventureItems){
			String msg = "[Item Registry] Adventure Items are disabled.";
			warnMessages.add(msg);
			TMLogger.warn(msg);
		}

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Enable Hard Mode", false);
		enableHardMode = property.getBoolean(false);
		property.setRequiresMcRestart(true);

		/*property = config.get(Configuration.CATEGORY_GENERAL, "1.8 Adventure Mode", false);
        enable18AdvMode = property.getBoolean(false);*/

		//property = configCore.get(Configuration.CATEGORY_GENERAL, "Enable Flint Axe", true);
		//enableFlintAxe = property.getBoolean(true);

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Enable Flint drop", false);
		enableGrassDrops = property.getBoolean(false);
		property.setRequiresMcRestart(true);

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Enable Command Executor", true);
		enableCommandExecutor = property.getBoolean(true);
		property.setRequiresMcRestart(true);

		/*property = config.get(categoryMineCamera, "Enable MineCamera Support", true);
        enableMineCamera = property.getBoolean(true);

        property = config.get(categoryMineCamera, "MineCamera addCommand", "camera create");
        property.comment = "Write whitout '/'";
        minecameraCommand = property.getString();*/
		property = configMinimap.get(Configuration.CATEGORY_GENERAL, "Enable Mini Map", true);
		enableMiniMap = property.getBoolean(true);
		property.setRequiresMcRestart(true);

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Holotape Speed", 1D);
		property.setComment("Speed of the Holotape Writer. How many characters does the writer write in 1 tick. Default 1");
		holotapeSpeed = property.getDouble(1);

		if(enableMiniMap){
			property = configMinimap.get(Reference.catOptions, "Marker Unload Distance", 128D);
			property.setComment("Marker Unload Distance in the Minimap (in blocks). Default: 128");
			markerUnloadDist = property.getDouble(128);

			property = configMinimap.get(Reference.catOptions, "Save Death Points", false);
			property.setComment("Save Markers which was created on Deaths");
			saveDeathPoints = property.getBoolean(false);
		}

		property = configTransport.get(Configuration.CATEGORY_GENERAL, "Enable Conveyor Belt Animation", true);
		property.setComment("Enable the Conveyor Belt moving animation. Might causes frame rate issues");
		enableConveyorBeltAnimation = property.getBoolean();
		property.setRequiresMcRestart(true);

		property = configDefense.get(Configuration.CATEGORY_GENERAL, "Force Power Convert Multipier", 2);
		property.setComment("Force Converter Multiplier calculation: HV / multiplier. Default: 2");
		forceMultipier = property.getDouble();
		property.setRequiresMcRestart(true);

		property = configTransport.get(CATEGORY_MINECRAFT, "Max Stack Size of Minecarts", 3);
		minecartMaxStackSize = property.getInt();
		property.setRequiresMcRestart(true);

		UUID randomUUID = MathHelper.getRandomUUID();
		NBTTagCompound tag = new NBTTagCompound();
		tag.setUniqueId("uuid", randomUUID);
		property = configCore.get(Configuration.CATEGORY_GENERAL, "Tom's Mod Fake Player UUID", tag.toString());
		String uuid = property.getString();
		property.setComment("!!! DO NOT MODIFY THIS IF YOU DON'T KNOW WHAT YOU ARE DOING !!! CAN CORRUPT SAVED WORLDS, because it may break codes which saves UUID !!! Also this code is in JSON format DO NOT brake the format, it will lead to an exception which CAN CORRUPT SAVES, a new UUID will get generated.");
		try{
			tomsmodFakePlayerUUID = readUUID(uuid);
		}catch(NBTException e){
			catchException(e, randomUUID, property, tag, uuid);
		} catch (NoSuchFieldException e) {
			catchException(e, randomUUID, property, tag, uuid);
		}
		TomsModUtils.constructFakePlayer();
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

		property = configMinimap.get(Configuration.CATEGORY_GENERAL, "Advanced Entity Tracker Texture", false);
		property.setComment("High Resolution texture for the entity tracker. (Default: false)");
		advEntityTrackerTexture = property.getBoolean(false);

		Property property2 = configWorldGen.get(Configuration.CATEGORY_GENERAL, "Gen Broken Trees", true);
		property2.setComment("Generate broken trees in the world. (Default: true)");
		enableBronkenTreeGen = property2.getBoolean(true);
		boolean hasWorldgen = false;
		if(configCore.hasKey(CATEGORY_WORLDGEN, "Gen Broken Trees")){
			property = configCore.get(CATEGORY_WORLDGEN, "Gen Broken Trees", true);
			property.setComment("!!! MOVED TO world_generation.cfg !!!");
			enableBronkenTreeGen = property.getBoolean(true);
			property2.set(enableBronkenTreeGen);
			hasWorldgen = true;
			TMLogger.warn("[Configuration] Option 'Gen Broken Trees' was moved under world_generation.cfg from Core.cfg. Value: " + enableBronkenTreeGen);
		}
		property2 = configWorldGen.get(Configuration.CATEGORY_GENERAL, "Gen Rubber Trees", true);
		property2.setComment("Generate Rubber trees in the world. (Default: true)");
		genRubberTrees = property2.getBoolean(true);
		if(configCore.hasKey(CATEGORY_WORLDGEN, "Gen Rubber Trees")){
			property = configCore.get(CATEGORY_WORLDGEN, "Gen Broken Trees", true);
			property.setComment("!!! MOVED TO world_generation.cfg !!!");
			genRubberTrees = property.getBoolean(true);
			property2.set(genRubberTrees);
			hasWorldgen = true;
			TMLogger.warn("[Configuration] Option 'Gen Rubber Trees' was moved under world_generation.cfg from Core.cfg. Value: " + genRubberTrees);
		}
		if(!genRubberTrees){
			String msg = "[World Gen] Rubber Tree generation is disabled.";
			warnMessages.add(msg);
			TMLogger.warn(msg);
		}
		property2 = configWorldGen.get(Configuration.CATEGORY_GENERAL, "Gen Oil", true);
		property2.setComment("Generate Oil Lakes in the world. (Default: true)");
		genOilLakes = property2.getBoolean(true);
		if(configCore.hasKey(CATEGORY_WORLDGEN, "Gen Oil")){
			property = configCore.get(CATEGORY_WORLDGEN, "Gen Oil", true);
			property.setComment("!!! MOVED TO world_generation.cfg !!!");
			genOilLakes = property.getBoolean(true);
			property2.set(genOilLakes);
			hasWorldgen = true;
			TMLogger.warn("[Configuration] Option 'Gen Oil' was moved under world_generation.cfg from Core.cfg. Value: " + genOilLakes);
		}
		if(!genOilLakes){
			String msg = "[World Gen] Oil Lake generation is disabled.";
			warnMessages.add(msg);
			TMLogger.warn(msg);
		}
		if(hasWorldgen){
			ConfigCategory c = configCore.getCategory(CATEGORY_WORLDGEN);
			if(c != null){
				configCore.removeCategory(c);
				TMLogger.warn("[Configuration] Removed category '" + CATEGORY_WORLDGEN + "'.");
			}
		}
		property = configCore.get(Configuration.CATEGORY_GENERAL, "Log Configuration Warning Messages", true);
		property.setComment("Logs a message in the init states if a warning is in the configuration.");
		logConfigWarnings = property.getBoolean(true);
		if(!logConfigWarnings){
			TMLogger.warn("[Config]: Warning messages are disabled.");
		}

		property = configCore.get(CATEGORY_RECIPES, "Unbreakable Elytra Recipe", true);
		property.setComment("Add Unbreakable Elytra Crafting recipe");
		addUnbreakableElytraRecipe = property.getBoolean(true);

		property = configCore.get(Configuration.CATEGORY_GENERAL, "Enable Tick Speeding", true);
		property.setComment("Allow external devices to speed up the machines ticks");
		enableTickSpeeding = property.getBoolean(true);
	}
	public static void save(){
		CoreInit.log.info("Saving configuration");
		configCore.save();
		configMinimap.save();
		configTransport.save();
		configDefense.save();
		configEnergy.save();
		configWorldGen.save();
	}
	private static void addMinecraftComment(Configuration c){
		c.addCustomCategoryComment(CATEGORY_MINECRAFT, "All minecraft related configurations are here.");
	}
	private static void addWailaComment(Configuration c){
		c.addCustomCategoryComment(CATEGORY_WAILA, "All Waila related configurations are here.");
	}
	public static void updateConfig(boolean isInWorld){
		TMLogger.info("Updating configs...");
	}
	private static UUID readUUID(String uuid) throws NBTException, NoSuchFieldException{
		NBTTagCompound readTag = JsonToNBT.getTagFromJson(uuid);
		if(readTag.hasUniqueId("uuid")){
			return readTag.getUniqueId("uuid");
		}else{
			throw new NoSuchFieldException("Missing field in the Json: uuid");
		}
	}
	private static void catchException(Exception e, UUID randomUUID, Property property, NBTTagCompound tag, String uuid){
		TMLogger.bigCatching(e, "SOMEONE EDITED THE CONFIG FILE AND CORRUPTED THE UUID!!! A NEW ONE WILL GET GENERATED. Errored UUID Json: " + uuid);
		property.set(tag.toString());
		tomsmodFakePlayerUUID = randomUUID;
	}
	public static boolean isWailaUsesMultimeter(IWailaConfigHandler config){
		return wailaUsesMultimeterForce ? true : config.getConfig(Waila.ENERGY_HANDLER_ID);
	}
	public static void initWailaConfigs(IWailaRegistrar registrar){
		registrar.addConfigRemote("Tom's Mod", Waila.ENERGY_HANDLER_ID);
	}
	public static boolean enableOreGen(String name){
		Property property = configWorldGen.get("ore_generation", "Generate "+name, true);
		property.setComment("Generate "+name+" in world. (Default: true)");
		return property.getBoolean(true);
	}
	public static void printWarnings(){
		if(logConfigWarnings){
			for(int i = 0;i<warnMessages.size();i++){
				TMLogger.warn("[Mod Configuration Warning]: " + warnMessages.get(i));
			}
		}
	}
}
