package com.tom.core;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.CheckResult;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;

import com.tom.api.Capabilities;
import com.tom.api.TomsModAPIMain;
import com.tom.api.block.ICustomItemBlock;
import com.tom.api.block.IMethod.IClientMethod;
import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.block.IMultiBlockInstance;
import com.tom.api.block.IRegisterRequired;
import com.tom.api.event.TMReloadEvent;
import com.tom.api.item.IWrench;
import com.tom.api.item.ItemDamagableCrafting;
import com.tom.api.item.ItemDamagableCrafting.ItemDamagableCraftingNormal;
import com.tom.api.recipes.RecipeHelper;
import com.tom.client.CustomModelLoader;
import com.tom.client.EventHandlerClient;
import com.tom.config.Config;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.core.commands.CommandResearch;
import com.tom.core.commands.CommandTMReload;
import com.tom.core.commands.CommandWaypoint;
import com.tom.core.map.MapHandler;
import com.tom.core.research.ResearchHandler;
import com.tom.core.research.ResearchLoader;
import com.tom.core.transformers.Transformers;
import com.tom.defense.DefenseInit;
import com.tom.handler.FuelHandler;
import com.tom.handler.IMCHandler;
import com.tom.handler.TMPlayerHandler;
import com.tom.handler.TMWorldHandler;
import com.tom.lib.Configs;
import com.tom.lib.utils.EmptyEntry;
import com.tom.lib.utils.Modids;
import com.tom.lib.utils.ReflectionUtils;
import com.tom.network.NetworkInit;
import com.tom.proxy.CommonProxy;
import com.tom.recipes.AdvancedCraftingRecipes;
import com.tom.recipes.CraftingRecipes;
import com.tom.recipes.FurnaceRecipes;
import com.tom.recipes.OreDict;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.util.FluidSupplier;
import com.tom.util.TMLogger;
import com.tom.util.TomsModUtils;
import com.tom.worldgen.WorldGen;
import com.tom.worldgen.WorldGen.OreGenEntry;

import com.tom.core.block.Antenna;
import com.tom.core.block.AntennaController;
import com.tom.core.block.BlockAcid;
import com.tom.core.block.BlockHardenedGlass;
import com.tom.core.block.BlockHidden;
import com.tom.core.block.BlockHiddenRenderOld;
import com.tom.core.block.BlockHiddenRenderOldTESR;
import com.tom.core.block.BlockHiddenTESR;
import com.tom.core.block.BlockOil;
import com.tom.core.block.BlockOre;
import com.tom.core.block.BlockRsDoor;
import com.tom.core.block.BlockRubberWood;
import com.tom.core.block.BlockSkyQuartzOre;
import com.tom.core.block.BlockSkyStone;
import com.tom.core.block.BlockTemplate;
import com.tom.core.block.BlockTreeTap;
import com.tom.core.block.Camera;
import com.tom.core.block.CommandExecutor;
import com.tom.core.block.ControllerBox;
import com.tom.core.block.EnderMemory;
import com.tom.core.block.EnderPlayerSensor;
import com.tom.core.block.GPU;
import com.tom.core.block.HardenedGlassPane;
import com.tom.core.block.HolotapeReader;
import com.tom.core.block.HolotapeWriter;
import com.tom.core.block.Jammer;
import com.tom.core.block.MagCardDevice;
import com.tom.core.block.MagCardReader;
import com.tom.core.block.MaterialBlock;
import com.tom.core.block.MaterialSlab;
import com.tom.core.block.Monitor;
import com.tom.core.block.RedstonePort;
import com.tom.core.block.ResearchTable;
import com.tom.core.block.RubberLeaves;
import com.tom.core.block.RubberSapling;
import com.tom.core.block.TabletAccessPoint;
import com.tom.core.block.TabletController;
import com.tom.core.block.TabletCrafter;
import com.tom.core.block.WirelessPeripheral;

import com.tom.core.item.Configurator;
import com.tom.core.item.ConnectionModem;
import com.tom.core.item.ElectricalMagCard;
import com.tom.core.item.Hammer;
import com.tom.core.item.Holotape;
import com.tom.core.item.ItemBigNoteBook;
import com.tom.core.item.ItemBlueprint;
import com.tom.core.item.ItemBuildGuide;
import com.tom.core.item.ItemChipset;
import com.tom.core.item.ItemChipsetBase;
import com.tom.core.item.ItemCircuit;
import com.tom.core.item.ItemCircuitComponent;
import com.tom.core.item.ItemCircuitDrawingPen;
import com.tom.core.item.ItemCircuitPanel;
import com.tom.core.item.ItemCircuitPanelPh;
import com.tom.core.item.ItemCircuitRaw;
import com.tom.core.item.ItemCircuitUnassembled;
import com.tom.core.item.ItemEntityTracker;
import com.tom.core.item.ItemMGlass;
import com.tom.core.item.ItemResearchTableUpgrade;
import com.tom.core.item.ItemTreeTap;
import com.tom.core.item.ItemWrench;
import com.tom.core.item.LinkedChipset;
import com.tom.core.item.Linker;
import com.tom.core.item.MagCard;
import com.tom.core.item.ModelledItem;
import com.tom.core.item.MortarAndPestle;
import com.tom.core.item.ResourceItem;
import com.tom.core.item.ResourceItem.CraftingItem;
import com.tom.core.item.RsDoor;
import com.tom.core.item.Tablet;
import com.tom.core.item.TabletHouse;
import com.tom.core.item.TrProcessor;
import com.tom.core.item.UraniumRod;
import com.tom.core.item.WireCutters;

import com.tom.core.tileentity.TileEntityAntenna;
import com.tom.core.tileentity.TileEntityAntennaController;
import com.tom.core.tileentity.TileEntityCamera;
import com.tom.core.tileentity.TileEntityCommandExecutor;
import com.tom.core.tileentity.TileEntityControllerBox;
import com.tom.core.tileentity.TileEntityEnderMemory;
import com.tom.core.tileentity.TileEntityEnderSensor;
import com.tom.core.tileentity.TileEntityGPU;
import com.tom.core.tileentity.TileEntityHidden;
import com.tom.core.tileentity.TileEntityHiddenSR;
import com.tom.core.tileentity.TileEntityHolotapeReader;
import com.tom.core.tileentity.TileEntityHolotapeWriter;
import com.tom.core.tileentity.TileEntityJammer;
import com.tom.core.tileentity.TileEntityMagCardDevice;
import com.tom.core.tileentity.TileEntityMagCardReader;
import com.tom.core.tileentity.TileEntityMonitor;
import com.tom.core.tileentity.TileEntityRSDoor;
import com.tom.core.tileentity.TileEntityRedstonePort;
import com.tom.core.tileentity.TileEntityResearchTable;
import com.tom.core.tileentity.TileEntityTabletAccessPoint;
import com.tom.core.tileentity.TileEntityTabletController;
import com.tom.core.tileentity.TileEntityTabletCrafter;
import com.tom.core.tileentity.TileEntityTemplate;
import com.tom.core.tileentity.TileEntityTreeTap;
import com.tom.core.tileentity.TileEntityWirelessPeripheral;

@Mod(modid = CoreInit.modid, name = CoreInit.modName, version = Configs.version, dependencies = Configs.mainDependencies, updateJSON = Configs.updateJson)

public final class CoreInit {
	public static final String modid = Configs.ModidL + "core";
	public static final String modName = Configs.ModName + " Core";
	public static final List<IMod> modids = new ArrayList<>();
	public static final Logger log = LogManager.getLogger(modName);
	public static Material hackedWood;
	// Fluid stuffs
	public static final List<FluidSupplier> fluids = new ArrayList<>();
	public static final BiMap<String, Fluid> fluidList = HashBiMap.create();
	public static final List<Item> itemList = new ArrayList<>();
	public static final List<Block> blockList = new ArrayList<>();
	private static boolean isPreInit = false, isInit = false, hadPostPreInit = false, hadInit = false;
	public static String configFolder = "";
	public static boolean isDebugging;
	private static final List<Entry<Item, Entry<ModelResourceLocation, Integer>>> modelList = new ArrayList<>();
	public static final List<String> ignoredLocations = new ArrayList<>();
	/** Block Ore, y, dim, a */
	public static final Map<Predicate<World>, List<OreGenEntry>> oreList = new HashMap<>();
	public static boolean isCCLoaded = false, /*isPneumaticCraftLoaded = false, */isAdventureItemsLoaded = false, isOCLoaded = false;
	private static CheckResult versionCheckResult;
	private static final List<IModelRegisterRequired> customModelRegisterRequired = new ArrayList<>();
	public static Stack<Runnable> initRunnables = new Stack<>();
	public static final Map<ResourceLocation, Block> blocks = new HashMap<>();
	public static final Map<ResourceLocation, Block> items = new HashMap<>();
	public static List<Runnable> reloadables = new ArrayList<>();
	private static ModContainer mc;
	// public static File mapFolder;
	// Fluids
	public static FluidSupplier plasma;
	public static FluidSupplier fusionFuel;
	public static FluidSupplier Deuterium, Hydrogen, Oxygen;
	public static FluidSupplier Tritium;
	// public static Fluid ePlasma;
	// public static Fluid hCoolant;
	// public static Fluid coolant;
	public static FluidSupplier steam;
	public static FluidSupplier nuclearWaste;
	public static FluidSupplier oil, fuel, lpg, kerosene;
	public static FluidSupplier sulfuricAcid, hydrogenChloride, creosoteOil, photoactiveLiquid, heatConductingPaste, resin, ironChloride, concentratedResin;
	// Items
	public static Item memoryCard, TabletHouse, linkedChipset, connectionModem, trProcessor, connectionBoxModem;
	public static Item linker, wrenchA, entityTracker, holotape, portableReader, magCard, electricalMagCard;
	public static Tablet Tablet;
	// public static Item itemGpuCable;
	private static ResourceItem dust;
	private static ResourceItem dustTiny;
	private static ResourceItem ingot;
	private static ResourceItem nugget;
	private static ResourceItem plate;
	private static ResourceItem cable;
	private static ResourceItem gem;
	private static ResourceItem coil;
	private static ResourceItem crushedOre, crushedOreN, crushedOreE;
	// private static ResourceItem shard, clump;
	private static ResourceItem researchPod;
	public static CraftingItem craftingMaterial;
	public static ItemDamagableCrafting hammer, mortarAndPestle, wireCutters, chalk, acidResistantInkBottle, photoactiveMaterialCan;
	private static MaterialBlock materialBlock1, materialBlock2;
	private static MaterialSlab materialSlab1;
	public static Item emptyWireCoil, emptyResearchComponentBase;
	public static Item uraniumRod, dUraniumRod, uraniumRodEmpty;
	public static Item rsDoor, wrench, bigNoteBook, noteBook, magnifyingGlass, configurator, treeTap, researchTableUpgrade, buildGuide;
	public static ModelledItem modelledItem;
	public static ItemCircuitDrawingPen circuitDrawingPen;
	public static ItemBlueprint blueprint;
	public static ItemCircuitComponent circuitComponent;
	public static ItemCircuit circuit;
	public static ItemCircuitRaw circuitRaw;
	public static ItemCircuitUnassembled circuitUnassembled;
	public static Item circuitPanel, circuitPanelP;
	public static ItemChipset chipset;
	public static Item chipsetBase;
	// Multiparts
	// Blocks
	public static Block GPU, Monitor;
	public static Block TabletController, TabletAccessPoint, Antenna, AntennaController, WirelessPeripheral, TabletCrafter, ControllerBox, Jammer;
	// public static Block GpuCable;
	public static Block MachineFrameBronze, MachineFrameBasic, MachineFrameSteel, MachineFrameChrome, MachineFrameTitanium, MachineFrameAluminum, hardenedGlass;
	public static Block enderMemory, /*CCProxy,*/ holotapeWriter, holotapeReader, MagCardDevice, MagCardReader, RedstonePort/*, ComputerRegulator*/;
	public static Block EnergySensor, blockRsDoor;
	public static BlockOre oreBlueMetal, oreTin, oreNickel, oreTitanium, oreCopper, oreUranium, oreRedDiamond,
	/*oreLithium,*/ orePlatinum, /*oreQuartz, */oreSilver, oreLead, oreZinc, oreChrome, oreSulfur, oreMercury, oreBauxite, oreWolfram, oreEnderium, oreNether, oreEnd, oreOil;
	public static Block oreCyanite, oreSkyQuartz;
	// public static Block EnderMinningWell, ExtendedEnderMinningWell;
	public static Block /*ItemProxy, */EnderPlayerSensor, CommandExecutor, Camera, researchTable, rubberWood, rubberLeaves, rubberSapling;
	public static Block skyStone, blockTreetap, steelFence, hardenedGlassPane, flintBlock, blockTemplate;
	public static BlockHidden blockHidden, blockHiddenTESR, blockHiddenRenderOld, blockHiddenRenderOldTESR;

	public static VillagerProfession professionScientist;

	private static boolean hadPreInit = false;

	@SidedProxy(clientSide = Configs.CLIENT_PROXY_CLASS, serverSide = Configs.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	@Instance(modid)
	public static CoreInit modInstance;

	@EventHandler
	public static void construction(FMLConstructionEvent event) {
		log.info("Tom's Mod Version: " + Configs.version);
		FluidRegistry.enableUniversalBucket();
		modids.add(new IMod() {
			@Override
			public String getModID() {
				return modid;
			}

			@Override
			public boolean hadPreInit() {
				return hadPreInit;
			}
		});
		proxy.construction();
	}

	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent) {
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		isPreInit = true;
		isCCLoaded = Loader.isModLoaded(Modids.COMPUTERCRAFT);
		isOCLoaded = Loader.isModLoaded(Modids.OPEN_COMPUTERS);
		mc = Loader.instance().activeModContainer();
		//isPneumaticCraftLoaded = Loader.isModLoaded(Configs.PNEUMATICCRAFT);
		String configPathRaw = PreEvent.getSuggestedConfigurationFile().getAbsolutePath();
		String configPath = configPathRaw.substring(0, configPathRaw.length() - 8) + File.separator;
		Config.init(new File(configPath));
		configFolder = configPath;
		if (Config.enableResearchSystem)
			ResearchHandler.init();
		// log.info(configPath);
		/** Items */
		uraniumRod = new UraniumRod().setUnlocalizedName("uraniumRod").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:uranCellFull")*/;
		dUraniumRod = new Item().setUnlocalizedName("dUraniumRod").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:uranCellDepleted")*/;
		uraniumRodEmpty = new Item().setUnlocalizedName("fuelRodEmpty")/*.setTextureName("minecraft:uranCellEmpty")*/.setCreativeTab(tabTomsModItems);
		memoryCard = new Item().setUnlocalizedName("memoryCard").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:memoryCard")*/;
		linker = new Linker().setUnlocalizedName("linker").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:linker")*/;
		rsDoor = new RsDoor().setUnlocalizedName("rsDoor").setCreativeTab(tabTomsModBlocks)/*.setTextureName("minecraft:tm/door")*/.setMaxStackSize(4);
		plate = new ResourceItem(Type.PLATE);
		ingot = new ResourceItem(Type.INGOT);
		dust = new ResourceItem(Type.DUST);
		dustTiny = new ResourceItem(Type.DUST_TINY);
		cable = new ResourceItem(Type.CABLE);
		nugget = new ResourceItem(Type.NUGGET);
		wrench = new ItemWrench().setUnlocalizedName("wrench").setCreativeTab(tabTomsModToolsAndCombat).setMaxStackSize(1);
		gem = new ResourceItem(Type.GEM);
		coil = new ResourceItem(Type.COIL);
		emptyWireCoil = new Item().setUnlocalizedName("emptyCoil").setCreativeTab(tabTomsModMaterials);
		crushedOre = new ResourceItem(Type.CRUSHED_ORE);
		crushedOreN = new ResourceItem(Type.CRUSHED_ORE_NETHER);
		crushedOreE = new ResourceItem(Type.CRUSHED_ORE_END);
		researchPod = new ResourceItem(Type.RESEARCH_ITEM);
		craftingMaterial = new CraftingItem().setCreativeTab(tabTomsModMaterials);
		if (Config.enableResearchSystem) {
			bigNoteBook = new ItemBigNoteBook().setUnlocalizedName("bigNoteBook").setCreativeTab(tabTomsModItems).setMaxStackSize(1);
			magnifyingGlass = new ItemMGlass().setUnlocalizedName("mGlass").setCreativeTab(tabTomsModItems).setMaxStackSize(1);
			noteBook = new Item().setUnlocalizedName("noteBook").setCreativeTab(tabTomsModItems).setMaxStackSize(1);
			researchTableUpgrade = new ItemResearchTableUpgrade().setUnlocalizedName("tm.researchTableUpgrade").setCreativeTab(tabTomsModItems).setMaxStackSize(1);
		}
		configurator = new Configurator().setCreativeTab(tabTomsModToolsAndCombat).setMaxStackSize(1).setUnlocalizedName("tm.configurator");
		hammer = new Hammer().setUnlocalizedName("tm.hammer");
		mortarAndPestle = new MortarAndPestle().setUnlocalizedName("tm.mortarAndPestle");
		wireCutters = new WireCutters().setUnlocalizedName("tm.wireCutters");
		treeTap = new ItemTreeTap().setCreativeTab(tabTomsModBlocks).setUnlocalizedName("tm.itemTreeTap").setMaxStackSize(4);
		circuitDrawingPen = new ItemCircuitDrawingPen().setUnlocalizedName("tm.pen");
		chalk = new ItemDamagableCraftingNormal(8).setUnlocalizedName("tm.chalk");
		acidResistantInkBottle = new ItemDamagableCraftingNormal(/*Config.enableHardRecipes ? 16 : */32, new ItemStack(Items.GLASS_BOTTLE)).setUnlocalizedName("tm.acidRInkBottle");
		photoactiveMaterialCan = new ItemDamagableCraftingNormal(/*Config.enableHardRecipes ? 8 : */16, CraftingMaterial.TIN_CAN.getStackNormal()).setUnlocalizedName("tm.photoactiveCan");
		emptyResearchComponentBase = new Item().setUnlocalizedName("emptyResearchComponentBase").setCreativeTab(tabTomsModMaterials);
		modelledItem = new ModelledItem();
		buildGuide = new ItemBuildGuide().setCreativeTab(tabTomsModItems).setUnlocalizedName("tm.buildGuide").setMaxStackSize(1);
		blueprint = (ItemBlueprint) new ItemBlueprint().setCreativeTab(tabTomsModItems).setUnlocalizedName("tm.blueprint");
		circuit = new ItemCircuit(tabTomsModItems);
		circuitRaw = new ItemCircuitRaw(tabTomsModMaterials);
		circuitUnassembled = new ItemCircuitUnassembled(tabTomsModMaterials);
		circuitComponent = (ItemCircuitComponent) new ItemCircuitComponent().setCreativeTab(tabTomsModMaterials).setUnlocalizedName("tm.circuitComponent");
		circuitPanel = new ItemCircuitPanel().setCreativeTab(tabTomsModMaterials).setUnlocalizedName("item.tm.circuitpanel").setHasSubtypes(true);
		circuitPanelP = new ItemCircuitPanelPh().setCreativeTab(tabTomsModMaterials).setUnlocalizedName("item.tm.circuitpanelph").setHasSubtypes(true);
		chipset = new ItemChipset(tabTomsModMaterials);
		chipsetBase = new ItemChipsetBase(tabTomsModMaterials);
		/** Blocks */
		MachineFrameBasic = new Block(Material.IRON).setHardness(2F).setResistance(10F).setUnlocalizedName("MachineFrameBasic").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:BasicMachineFrame")*/;
		MachineFrameSteel = new Block(Material.IRON).setHardness(4F).setResistance(20F).setUnlocalizedName("MachineFrameSteel").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:SteelMachineFrame")*/;
		MachineFrameChrome = new Block(Material.IRON).setHardness(4F).setResistance(10F).setUnlocalizedName("MachineFrameChrome").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:ChromeMachineFrame")*/;
		MachineFrameTitanium = new Block(Material.IRON).setHardness(5F).setResistance(20F).setUnlocalizedName("MachineFrameTitanium").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:TitaniumMachineFrame")*/;
		MachineFrameBronze = new Block(Material.IRON).setHardness(2F).setResistance(10F).setUnlocalizedName("MachineFrameBronze").setCreativeTab(tabTomsModBlocks);
		MachineFrameAluminum = new Block(Material.IRON).setHardness(2F).setResistance(10F).setUnlocalizedName("MachineFrameAluminum").setCreativeTab(tabTomsModBlocks);
		hardenedGlass = new BlockHardenedGlass().setUnlocalizedName("tm.hardenedGlass").setCreativeTab(tabTomsModBlocks).setHardness(2.5F).setResistance(15.5F);
		steelFence = new BlockFence(Material.IRON, MapColor.GRAY).setUnlocalizedName("tm.steelFence").setCreativeTab(tabTomsModBlocks).setHardness(1.5F).setResistance(20);
		hardenedGlassPane = new HardenedGlassPane().setCreativeTab(tabTomsModBlocks).setUnlocalizedName("tm.hardenedGlassPane").setHardness(1.25F).setResistance(9.5F);
		oreSkyQuartz = new BlockSkyQuartzOre().setCreativeTab(tabTomsModMaterials).setUnlocalizedName("tm.oreSkyQuartz");
		oreCyanite = new Block(Material.ROCK).setCreativeTab(tabTomsModMaterials).setUnlocalizedName("tm.oreCyanite").setHardness(50.0F).setResistance(2000.0F);
		materialBlock1 = new MaterialBlock(TMResource.ALUMINUM, TMResource.advAlloyMK1, TMResource.advAlloyMK2, TMResource.BLUE_METAL, TMResource.BRONZE, TMResource.SKY_QUARTZ, TMResource.CHROME, TMResource.COPPER, TMResource.ELECTRUM, TMResource.ENDERIUM, TMResource.FLUIX, TMResource.GREENIUM, TMResource.LEAD, TMResource.BRASS, TMResource.NICKEL, TMResource.PLATINUM).setUnlocalizedName("materialStorage");
		materialBlock2 = new MaterialBlock(TMResource.RED_DIAMOND, TMResource.SILVER, TMResource.STEEL, TMResource.TIN, TMResource.TITANIUM, TMResource.URANIUM, TMResource.ZINC, TMResource.WOLFRAM, TMResource.TUNGSTENSTEEL, TMResource.SULFUR, TMResource.MERCURY).setUnlocalizedName("materialStorage2");
		rubberLeaves = new RubberLeaves().setCreativeTab(tabTomsModBlocks).setUnlocalizedName("tm.rubberLeaves");
		skyStone = new BlockSkyStone().setUnlocalizedName("tm.skyStone").setCreativeTab(tabTomsModMaterials);
		rubberSapling = new RubberSapling().setCreativeTab(tabTomsModBlocks).setUnlocalizedName("tm.rubberSapling");
		flintBlock = new Block(Material.ROCK).setCreativeTab(tabTomsModBlocks).setUnlocalizedName("flintBlock").setHardness(1.5F).setResistance(10.0F);
		/** TileEntities */
		// EnergySensor = new
		// EnergySensor().setUnlocalizedName("energySensor").setCreativeTab(tabTomsModBlocks);
		Antenna = new Antenna().setUnlocalizedName("antenna").setCreativeTab(tabTomsModBlocks);
		//ItemProxy = new ItemProxy().setUnlocalizedName("ItemProxy").setCreativeTab(tabTomsModBlocks);
		blockRsDoor = new BlockRsDoor().setUnlocalizedName("brsDoor");
		if (Config.enableResearchSystem)
			researchTable = new ResearchTable().setCreativeTab(tabTomsModBlocks).setUnlocalizedName("resTable");
		rubberWood = new BlockRubberWood().setUnlocalizedName("tm.rubberWood").setCreativeTab(tabTomsModBlocks);
		blockTreetap = new BlockTreeTap().setUnlocalizedName("tm.blockTreeTap");
		materialSlab1 = new MaterialSlab(TMResource.STEEL).setUnlocalizedName("materialSlab");
		blockHidden = new BlockHidden().setUnlocalizedName("tm.hidden");
		blockHiddenTESR = new BlockHiddenTESR().setUnlocalizedName("tm.hiddentesr");
		blockHiddenRenderOld = new BlockHiddenRenderOld().setUnlocalizedName("tm.hiddenrender");
		blockHiddenRenderOldTESR = new BlockHiddenRenderOldTESR().setUnlocalizedName("tm.hiddenrenderertesr");
		blockTemplate = new BlockTemplate().setUnlocalizedName("tm.blocktemplate");
		/** Ores */
		oreBlueMetal = BlockOre.create(60, 5, TMResource.BLUE_METAL).setUnlocalizedName("oreBlueMetal");
		oreCopper = BlockOre.create(70, 9, TMResource.COPPER).setUnlocalizedName("oreCopper");
		oreTin = BlockOre.create(60, 8, TMResource.TIN).setUnlocalizedName("oreTin");
		oreNickel = BlockOre.create(50, 6, TMResource.NICKEL).setUnlocalizedName("oreNickel");
		oreTitanium = BlockOre.create(20, 2, TMResource.TITANIUM).setUnlocalizedName("oreTitanium");
		oreUranium = BlockOre.create(25, 3, TMResource.URANIUM).setUnlocalizedName("oreUranium");
		oreRedDiamond = BlockOre.create(12, 1, TMResource.RED_DIAMOND.getStackNormal(Type.GEM), TMResource.RED_DIAMOND).setUnlocalizedName("oreRedDiamond");
		orePlatinum = BlockOre.create(10, 1, TMResource.PLATINUM).setUnlocalizedName("orePlatinum");
		oreSilver = BlockOre.create(30, 3, TMResource.SILVER).setUnlocalizedName("oreSilver");
		oreLead = BlockOre.create(30, 2, TMResource.LEAD).setUnlocalizedName("oreLead");
		oreZinc = BlockOre.create(65, 5, TMResource.ZINC).setUnlocalizedName("oreZinc");
		oreChrome = BlockOre.create(16, 3, TMResource.CHROME).setUnlocalizedName("oreChrome");
		oreSulfur = BlockOre.create(65, 6, TMResource.SULFUR.getStackNormal(Type.DUST, 2), TMResource.SULFUR).setUnlocalizedName("oreSulfur");
		oreMercury = BlockOre.create(30, 1, TMResource.MERCURY).setUnlocalizedName("oreMercury");
		oreBauxite = BlockOre.create(55, 6, TMResource.ALUMINUM).setUnlocalizedName("oreBauxite");
		oreWolfram = BlockOre.create(30, 3, TMResource.WOLFRAM).setUnlocalizedName("oreTungstate");
		oreEnderium = new BlockOre(60, WorldGen.END, 2, WorldGen.END_STONE, 1).setUnlocalizedName("oreEnderium");
		oreNether = new BlockOre(80, WorldGen.NETHER, 4, WorldGen.NETHERRACK, 5, TMResource.IRON.getHarvestLevel()).addExtraState(WorldGen.NETHER, 60, 2, WorldGen.NETHERRACK, "gold").addExtraState(WorldGen.NETHER, 60, 2, WorldGen.NETHERRACK, "lapis", new ItemStack(Items.DYE, 10, EnumDyeColor.BLUE.getDyeDamage())).addExtraState(WorldGen.NETHER, 60, 2, WorldGen.NETHERRACK, "redstone", new ItemStack(Items.REDSTONE, 6)).addExtraState(WorldGen.NETHER, 60, 1, WorldGen.NETHERRACK, "diamond", new ItemStack(Items.DIAMOND)).setUnlocalizedName("oreNetherVanilla");
		oreEnd = new BlockOre(40, WorldGen.END, 3, WorldGen.END_STONE, 2, TMResource.GOLD.getHarvestLevel()).addExtraState(WorldGen.END, 30, 1, WorldGen.END_STONE, "diamond", new ItemStack(Items.DIAMOND)).setUnlocalizedName("oreEndVanilla");
		oreOil = new BlockOre(80, WorldGen.OVERWORLD, 1, WorldGen.STONE, 3, 2).addExtraState(WorldGen.OVERWORLD, 80, 1, WorldGen.SAND, "sand").addExtraState(WorldGen.OVERWORLD, 80, 1, WorldGen.RED_SAND, "red_sand").setUnlocalizedName("oreOil");
		// oreLithium = new BlockOre(50, OVERWORLD, 4,
		// TMResource.LITHIUM).setUnlocalizedName("oreLithium");
		// oreQuartz = new BlockOre(40, OVERWORLD, 1,
		// TMResource.QUARTZ.getStackNormal(Type.GEM),
		// TMResource.QUARTZ).setUnlocalizedName("oreQuartz");
		// ComputerRegulator = new
		// ComputerRegulator().setUnlocalizedName("computerRegulator").setCreativeTab(tabTomsModBlocks);
		// ExtendedEnderMinningWell = new
		// ExtendedEnderMinningWell().setBlockName("ExtendedEnderMinningWell").setCreativeTab(tabTomsModBlocks);
		/** Fluids */
		plasma = createFluid("tomsmodPlasma").setTemperature(10000).setLuminosity(10).setViscosity(1100).setRarity(EnumRarity.UNCOMMON);
		Deuterium = createFluid("tomsmodDeuterium").setGaseous(true).setDensity(-400).setViscosity(600);
		Tritium = createFluid("tomsmodTritium").setGaseous(true).setDensity(-400).setViscosity(600);
		steam = createFluid("steam").setTemperature(500).setGaseous(true).setDensity(-200).setViscosity(800);
		fusionFuel = createFluid("tomsmodFusionFuel");
		Hydrogen = createFluid("hydrogen").setGaseous(true).setDensity(-500).setViscosity(500);
		nuclearWaste = createFluid("tmNuclearWaste").setLuminosity(7).setTemperature(400).setViscosity(600);
		oil = createFluid("oil").setViscosity(5000).setDensity(5000);
		sulfuricAcid = createFluid("tmSulfuricAcid");
		hydrogenChloride = createFluid("hydrogenChloride").setGaseous(true).setDensity(-500).setViscosity(500);
		creosoteOil = createFluid("creosote");
		Oxygen = createFluid("oxygen").setGaseous(true);
		fuel = createFluid("fuel");
		lpg = createFluid("lpg");
		kerosene = createFluid("kerosene");
		photoactiveLiquid = createFluid("photoactiveLiquid");
		heatConductingPaste = createFluid("heatconductingpaste");
		resin = createFluid("resin");
		ironChloride = createFluid("ironchloride");
		concentratedResin = createFluid("concentratedresin");
		// ePlasma = new
		// Fluid("tomsmodEPlasma").setTemperature(10000).setLuminosity(12).setDensity(4000);
		// hCoolant = new Fluid("tomsmodCoolantHot").setTemperature(500);
		// coolant = new Fluid("tomsmodCoolant");
		/*sulfurDioxide = createFluid("tmSulfurDioxide".toLowerCase(), new ResourceLocation("tomsmodcore:blocks/sulfurDioxide_still"),new ResourceLocation("tomsmodcore:blocks/sulfurDioxide_flow")).setGaseous(true).setDensity(-500).setViscosity(500);
		sulfurTrioxide = createFluid("tmSulfurTrioxide".toLowerCase(), new ResourceLocation("tomsmodcore:blocks/sulfurTrioxide_still"),new ResourceLocation("tomsmodcore:blocks/sulfurTrioxide_flow")).setGaseous(true).setDensity(-500).setViscosity(500);
		chlorine = createFluid("chlorine", new ResourceLocation("tomsmodcore:blocks/chlorine_still"),new ResourceLocation("tomsmodcore:blocks/chlorine_flow")).setGaseous(true).setDensity(-600).setViscosity(400);*/
		/** Fluid Registry */
		/*fluids.add(plasma);
		//fluids.add(ePlasma);
		fluids.add(Deuterium);
		fluids.add(Tritium);
		//fluids.add(coolant);
		fluids.add(fusionFuel);
		//fluids.add(hCoolant);
		fluids.add(Hydrogen);
		fluids.add(steam);
		fluids.add(nuclearWaste);
		fluids.add(oil);
		fluids.add(sulfuricAcid);
		/*fluids.add(sulfurDioxide);
		fluids.add(sulfurTrioxide);
		fluids.add(chlorine);*/
		/*fluids.add(hydrogenChlorine);
		fluids.add(creosoteOil);
		fluids.add(Oxygen);
		fluids.add(fuel);
		fluids.add(lpg);
		fluids.add(kerosene);
		fluids.add(photoactiveLiquid);
		fluids.add(heatConductingPaste);*/
		/** Registry */
		/** Items */
		addItemToGameRegistry(plate, plate.getUnlocalizedName().substring(5));
		addItemToGameRegistry(ingot, ingot.getUnlocalizedName().substring(5));
		addItemToGameRegistry(dust, dust.getUnlocalizedName().substring(5));
		addItemToGameRegistry(dustTiny, dustTiny.getUnlocalizedName().substring(5));
		addItemToGameRegistry(cable, cable.getUnlocalizedName().substring(5));
		addItemToGameRegistry(nugget, nugget.getUnlocalizedName().substring(5));
		addItemToGameRegistry(gem, gem.getUnlocalizedName().substring(5));
		addItemToGameRegistry(coil, coil.getUnlocalizedName().substring(5));
		addItemToGameRegistry(crushedOre, crushedOre.getUnlocalizedName().substring(5));
		addItemToGameRegistry(crushedOreN, crushedOreN.getUnlocalizedName().substring(5));
		addItemToGameRegistry(crushedOreE, crushedOreE.getUnlocalizedName().substring(5));
		addItemToGameRegistry(researchPod, researchPod.getUnlocalizedName().substring(5));
		registerItem(craftingMaterial);
		// addItemToGameRegistry(shard,
		// shard.getUnlocalizedName().substring(5));
		// addItemToGameRegistry(clump,
		// clump.getUnlocalizedName().substring(5));
		// addItemToGameRegistry(dirtyDust,
		// dirtyDust.getUnlocalizedName().substring(5));
		registerItem(uraniumRod);
		registerItem(dUraniumRod);
		registerItem(uraniumRodEmpty);
		registerItem(memoryCard);
		registerItem(linker);
		registerItem(rsDoor);
		registerItem(wrench);
		registerItem(emptyWireCoil);
		if (Config.enableResearchSystem) {
			registerItem(bigNoteBook);
			registerItem(magnifyingGlass);
			registerItem(noteBook);
			registerItem(researchTableUpgrade);
			registerItem(emptyResearchComponentBase);
		}
		registerItem(configurator);
		registerItem(hammer);
		registerItem(mortarAndPestle);
		registerItem(wireCutters);
		registerItem(treeTap);
		registerItem(circuitDrawingPen);
		registerItem(chalk);
		registerItem(acidResistantInkBottle);
		registerItem(photoactiveMaterialCan);
		// registerItem(modelledItem,
		// modelledItem.getUnlocalizedName().substring(5));
		registerItem(buildGuide);
		registerItem(blueprint);
		registerItem(circuit);
		registerItem(circuitComponent);
		registerItem(circuitRaw);
		registerItem(circuitUnassembled);
		registerItem(circuitPanel);
		registerItem(circuitPanelP);
		registerItem(chipsetBase);
		registerItem(chipset);
		/** Blocks */
		registerBlock(MachineFrameBasic);
		registerBlock(MachineFrameSteel);
		registerBlock(MachineFrameChrome);
		registerBlock(MachineFrameTitanium);
		registerBlock(MachineFrameBronze);
		registerBlock(MachineFrameAluminum);
		//registerBlock(ItemProxy);
		addOnlyBlockToGameRegisty(blockRsDoor, blockRsDoor.getUnlocalizedName().substring(5));
		registerBlock(oreBlueMetal);
		registerBlock(oreCopper);
		registerBlock(oreTin);
		registerBlock(oreNickel);
		registerBlock(oreTitanium);
		registerBlock(oreRedDiamond);
		registerBlock(orePlatinum);
		// registerBlock(oreQuartz);
		registerBlock(oreSilver);
		registerBlock(oreLead);
		registerBlock(oreUranium);
		// registerBlock(oreLithium);
		registerBlock(oreZinc);
		registerBlock(oreChrome);
		registerBlock(oreSulfur);
		registerBlock(oreMercury);
		registerBlock(oreBauxite);
		registerBlock(oreSkyQuartz);
		registerBlock(oreWolfram);
		registerBlock(oreEnderium);
		registerBlock(oreCyanite);
		registerBlock(oreNether);
		registerBlock(oreEnd);
		registerBlock(oreOil);
		registerBlock(rubberWood);
		if (Config.enableResearchSystem)
			registerBlock(researchTable);
		registerBlock(materialBlock1, materialBlock1.itemBlock);
		registerBlock(materialBlock2, materialBlock2.itemBlock);
		registerBlock(rubberLeaves);
		registerBlock(skyStone);
		registerBlock(rubberSapling);
		addOnlyBlockToGameRegisty(blockTreetap, blockTreetap.getUnlocalizedName().substring(5));
		registerMultiBlock(materialSlab1);
		addOnlyBlockToGameRegisty(blockHidden, blockHidden.getUnlocalizedName().substring(5));
		addOnlyBlockToGameRegisty(blockHiddenTESR, blockHiddenTESR.getUnlocalizedName().substring(5));
		addOnlyBlockToGameRegisty(blockHiddenRenderOld, blockHiddenRenderOld.getUnlocalizedName().substring(5));
		addOnlyBlockToGameRegisty(blockHiddenRenderOldTESR, blockHiddenRenderOldTESR.getUnlocalizedName().substring(5));
		registerBlock(hardenedGlass);
		registerBlock(steelFence);
		registerBlock(hardenedGlassPane);
		registerBlock(flintBlock);
		addOnlyBlockToGameRegisty(blockTemplate, blockTemplate.getUnlocalizedName().substring(5));
		/** TileEntities */
		//GameRegistry.registerTileEntity(TileEntityItemProxy.class, Configs.Modid + "ItemProxy");
		registerTileEntity(TileEntityRSDoor.class, "rsDoor");
		if (Config.enableResearchSystem)
			registerTileEntity(TileEntityResearchTable.class, "researchTable");
		registerTileEntity(TileEntityTreeTap.class, "treetap");
		registerTileEntity(TileEntityHidden.class, "hidden");
		registerTileEntity(TileEntityHiddenSR.class, "hiddensr");
		registerTileEntity(TileEntityTemplate.class, "template");
		/** Integration */
		log.info("Loading integration items...");
		holotape = new Holotape().setUnlocalizedName("holotape").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tm/holotape")*/.setMaxStackSize(1);
		magCard = new MagCard().setUnlocalizedName("magCard").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tm/magCard")*/.setMaxStackSize(1);
		electricalMagCard = new ElectricalMagCard().setUnlocalizedName("eMagCard").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tm/eMagCard")*/.setMaxStackSize(1);
		GPU = new GPU().setUnlocalizedName("gpu").setCreativeTab(tabTomsModBlocks);
		Monitor = new Monitor().setUnlocalizedName("monitor").setCreativeTab(tabTomsModBlocks);
		enderMemory = new EnderMemory().setUnlocalizedName("enderMemory").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:EnderMemory3")*/;
		WirelessPeripheral = new WirelessPeripheral().setUnlocalizedName("wirelessPeripheral").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:tm/TabContSideAct")*/;
		EnderPlayerSensor = new EnderPlayerSensor().setUnlocalizedName("EnderPlayerSensor").setCreativeTab(tabTomsModBlocks);
		holotapeWriter = new HolotapeWriter().setUnlocalizedName("holotapeWriter").setCreativeTab(tabTomsModBlocks);
		holotapeReader = new HolotapeReader().setUnlocalizedName("holotapeReader").setCreativeTab(tabTomsModBlocks);
		MagCardDevice = new MagCardDevice().setUnlocalizedName("magCardDevice").setCreativeTab(tabTomsModBlocks);
		MagCardReader = new MagCardReader().setUnlocalizedName("magCardReader").setCreativeTab(tabTomsModBlocks);
		RedstonePort = new RedstonePort().setUnlocalizedName("rsPort").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:rsPort")*/;
		registerItem(holotape);
		registerItem(magCard);
		registerBlock(GPU);
		registerBlock(Monitor);
		registerBlock(WirelessPeripheral);
		registerBlock(EnderPlayerSensor);
		registerBlock(enderMemory);
		registerBlock(holotapeWriter);
		registerBlock(holotapeReader);
		registerBlock(MagCardDevice);
		registerBlock(MagCardReader);
		registerBlock(RedstonePort);
		registerTileEntity(TileEntityEnderSensor.class, "EnderSensor");
		registerTileEntity(TileEntityMonitor.class, "mBlack");
		registerTileEntity(TileEntityGPU.class, "GPU");
		registerTileEntity(TileEntityWirelessPeripheral.class, "WirelessPeripheral");
		registerTileEntity(TileEntityEnderMemory.class, "enderMemory");
		registerTileEntity(TileEntityHolotapeWriter.class, "holotapeWriter");
		registerTileEntity(TileEntityHolotapeReader.class, "holotapeReader");
		registerTileEntity(TileEntityMagCardDevice.class, "MagCardDevice");
		registerTileEntity(TileEntityMagCardReader.class, "MagCardReader");
		registerTileEntity(TileEntityRedstonePort.class, "RsPort");
		if (Config.enableAdventureItems) {
			Tablet = new Tablet().setUnlocalizedName("tablet").setCreativeTab(tabTomsModItems);
			connectionBoxModem = new Item().setUnlocalizedName("cBoxModem").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tm/modem2")*/.setMaxStackSize(1);
			wrenchA = new Item().setUnlocalizedName("wrenchA").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tm/wrench")*/.setMaxStackSize(1);
			ControllerBox = new ControllerBox().setUnlocalizedName("ControllerBox").setCreativeTab(tabTomsModBlocks);
			TabletHouse = new TabletHouse().setUnlocalizedName("TabletHouse").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tablet/TabletOff")*/;
			trProcessor = new TrProcessor().setUnlocalizedName("trProcessor").setCreativeTab(tabTomsModItems).setMaxStackSize(1)/*.setTextureName("minecraft:chip")*/;
			linkedChipset = new LinkedChipset().setUnlocalizedName("linkedChipset").setCreativeTab(tabTomsModItems).setMaxStackSize(1)/*.setTextureName("minecraft:chip")*/;
			connectionModem = new ConnectionModem().setUnlocalizedName("connectionModem").setCreativeTab(tabTomsModItems).setMaxStackSize(1)/*.setTextureName("minecraft:tm/modem")*/;
			Camera = new Camera().setUnlocalizedName("Camera").setCreativeTab(tabTomsModBlocks);
			Jammer = new Jammer().setUnlocalizedName("Jammer").setCreativeTab(tabTomsModBlocks);
			AntennaController = new AntennaController().setUnlocalizedName("AntennaController").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:tm/TabContSideAct")*/;
			TabletCrafter = new TabletCrafter().setUnlocalizedName("TabletCrafter").setCreativeTab(tabTomsModBlocks);
			TabletAccessPoint = new TabletAccessPoint().setUnlocalizedName("tabletAccessPoint").setCreativeTab(tabTomsModBlocks);
			TabletController = new TabletController().setUnlocalizedName("tabletController").setCreativeTab(tabTomsModBlocks);
			registerItem(Tablet);
			registerItem(TabletHouse);
			registerItem(connectionBoxModem);
			registerItem(trProcessor);
			registerItem(linkedChipset);
			registerItem(connectionModem);
			registerItem(wrenchA);
			registerItem(electricalMagCard);
			registerBlock(TabletAccessPoint);
			registerBlock(Antenna);
			registerBlock(TabletController);
			registerBlock(AntennaController);
			registerBlock(ControllerBox);
			registerBlock(TabletCrafter);
			registerBlock(Camera);
			registerBlock(Jammer);
			registerTileEntity(TileEntityTabletController.class, "TabletController");
			registerTileEntity(TileEntityAntennaController.class, "AntennaCont");
			registerTileEntity(TileEntityTabletAccessPoint.class, "TabletAccessPoint");
			registerTileEntity(TileEntityAntenna.class, "Antenna");
			registerTileEntity(TileEntityControllerBox.class, "ControllerBox");
			registerTileEntity(TileEntityTabletCrafter.class, "TabletCrafter");
			registerTileEntity(TileEntityCamera.class, "Camera");
			registerTileEntity(TileEntityJammer.class, "Jammer");
			isAdventureItemsLoaded = true;
		} else {
			log.warn("Adventure items disabled");
		}
		if (Config.enableCommandExecutor) {
			CommandExecutor = new CommandExecutor().setUnlocalizedName("CommandExecutor").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:command_block")*/.setBlockUnbreakable().setResistance(18000000F);
			registerBlock(CommandExecutor);
			registerTileEntity(TileEntityCommandExecutor.class, "CommandExecutor");
		}
		TileEntityGPU.init();
		//isMapEnabled = Config.enableMiniMap;
		//if (isMapEnabled) {
		//log.info("Mini Map Enabled. Init minimap");
		entityTracker = new ItemEntityTracker().setUnlocalizedName("itemEntityTracker").setCreativeTab(tabTomsModItems).setMaxStackSize(1);
		registerItem(entityTracker);
		//log.info("Minimap loaded");
		//}
		/*if(isPneumaticCraftLoaded){
			registerBlock(MultiblockPressurePort, MultiblockPressurePort.getUnlocalizedName().substring(5));
			registerBlock(MultiblockHeatPort, MultiblockHeatPort.getUnlocalizedName().substring(5));
			GameRegistry.registerTileEntity(TileEntityMBPressurePort.class, Configs.Modid+"mbPressurePort");
			GameRegistry.registerTileEntity(TileEntityMBHeatPort.class, Configs.Modid+"mbHeatPort");
			GameRegistry.addRecipe(new ItemStack(MultiblockCompressor, 1), new Object[]{"MCM","IBI","MRM",'R',chargedRedstone,'M',MultiblockCase,'B',Blocks.iron_block,'I',Items.iron_ingot,'C',plateChrome});
		}*/
		/*GameRegistry.registerTileEntity(TileEntityAntTop.class, Configs.Modid + "AntTop");
		GameRegistry.registerTileEntity(TileEntityAntMid.class, Configs.Modid + "AntMid");
		GameRegistry.registerTileEntity(TileEntityAntBase.class, Configs.Modid + "AntBase");*/
		/** Other */
		CraftingMaterial.inited = true;
		proxy.preInit();
		NetworkInit.init();
		WorldGen.init();
		// TMResource.loadFluids(false);
		// registerEntity(EntityCamera.class, Configs.Modid+":Camera",false);
		// FMLCommonHandler.instance().bus().register(new Config());
		proxy.registerKeyBindings();
		FMLInterModComms.sendMessage("waila", "register", "com.tom.thirdparty.waila.WailaHandler.onWailaCall");
		FMLInterModComms.sendFunctionMessage(Modids.TheOneProbe, "getTheOneProbe", "com.tom.thirdparty.theoneprobe.TheOneProbeHandler");
		oreOil.setHarvestLevel("shovel", 0, oreOil.getStateFromMeta(1));
		oreOil.sound.put(1, SoundType.SAND);
		LootTableList.register(new ResourceLocation("tomsmod", "scientist"));
		LootTableList.register(new ResourceLocation("tomsmod", "scientist2"));
		CraftingRecipes.patchRecipes();
		initializeFluidBlocksAndBuckets();
		ItemCircuit.loadCfg();
		ItemCircuitComponent.loadCfg();
		ItemBlueprint.load();
		ItemChipset.loadCfg();
		addReloadableTask(ItemCircuit::reload);
		addReloadableTask(ItemCircuitComponent::reload);
		addReloadableTask(ItemChipset::reload);
		TMPlayerHandler.register();
		// FMLInterModComms.sendFunctionMessage("rftoolsdim", "getDimletConfigurationManager", "com.tom.thirdparty.rftools.RFToolsDim$GetDimletConfigurationManager");
		/*if(isMapEnabled){
		}*/
		isPreInit = false;
		hadPreInit = true;
		tryLoadAfterPreInit(log);
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in " + time + " milliseconds");
	}

	private static void postPreInit(Logger logIn) {
		long tM = System.currentTimeMillis();
		logIn.info("All parts loaded. Calling PostPreInit");
		Config.save();
		proxy.registerRenders();
		MinecraftForge.EVENT_BUS.register(com.tom.handler.EventHandler.instance);
		hadPostPreInit = true;
		long time = System.currentTimeMillis() - tM;
		logIn.info("PostPre Initialization took in " + time + " milliseconds");
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemRenders() {
		log.info("Loading Renderers");
		long tM = System.currentTimeMillis();
		CustomModelLoader.clearExceptions();
		OBJLoader.INSTANCE.addDomain("tmobj");
		itemList.forEach(CoreInit::registerRender);
		customModelRegisterRequired.forEach(IModelRegisterRequired::registerModels);
		log.info("Loading Material Renderers");
		for (Type t : Type.VALUES) {
			for (TMResource r : TMResource.VALUES) {
				if (r.isValid(t))
					registerRender(t.getItem(), r.ordinal(), "tomsmodcore:resources/" + t.getName() + "_" + r.getName());
			}
		}
		processRenderers();
		long time = System.currentTimeMillis() - tM;
		log.info("Loaded Renderers in " + time + " milliseconds");
	}

	@SideOnly(Side.CLIENT)
	private static void processRenderers() {
		log.info("Start processing " + modelList.size() + " models");
		ProgressBar bar = ProgressManager.push("Loading Renderers", modelList.size());
		for (Entry<Item, Entry<ModelResourceLocation, Integer>> e : modelList) {
			addRenderToRegistry(e.getKey(), e.getValue().getValue(), e.getValue().getKey(), bar);
		}
		log.info("Loaded " + modelList.size() + " models.");
		modelList.clear();
		ProgressManager.pop(bar);
	}

	@EventHandler
	public static void load(FMLInitializationEvent event) {
		log.info("Start Initialization");
		long tM = System.currentTimeMillis();
		isInit = true;
		proxy.init();
		log.info("Registering world generator...");
		GameRegistry.registerWorldGenerator(WorldGen.instance, 1);
		OreDict.init();
		if (Config.enableGrassDrops) {
			log.info("Adding Grass Drops...");
			MinecraftForge.addGrassSeed(new ItemStack(Items.FLINT), 15);
			MinecraftForge.addGrassSeed(new ItemStack(Items.STICK), 15);
		}
		Stack<Runnable> r = initRunnables;
		initRunnables = new Stack<>();
		log.info("Processing " + r.size() + " task(s)...");
		ProgressBar bar = ProgressManager.push("Processing Tasks", r.size());
		int start = r.size();
		while (!r.isEmpty()){
			r.pop().run();
			bar.step(start + "/" + (start - r.size()));
		}
		ProgressManager.pop(bar);
		log.info("Task(s) completed");
		TMResource.IRON.registerOre(-1, new ItemStack(oreNether, 1, 0));
		TMResource.GOLD.registerOre(-1, new ItemStack(oreNether, 1, 1));
		TMResource.LAPIS.registerOre(-1, new ItemStack(oreNether, 1, 2));
		TMResource.REDSTONE.registerOre(-1, new ItemStack(oreNether, 1, 3));
		TMResource.DIAMOND.registerOre(-1, new ItemStack(oreNether, 1, 4));
		TMResource.GOLD.registerOre(1, new ItemStack(oreEnd, 1, 0));
		TMResource.DIAMOND.registerOre(1, new ItemStack(oreEnd, 1, 1));
		Capabilities.init();
		CraftingMaterial.init();
		DefenseInit.registerPlaceables();
		TMWorldHandler.init();
		// MaterialOverride.override();
		log.info("Loading Vanilla Material Blocks");
		TMResource.DIAMOND.storageBlock = new EmptyEntry<>(Blocks.DIAMOND_BLOCK, 0);
		TMResource.IRON.storageBlock = new EmptyEntry<>(Blocks.IRON_BLOCK, 0);
		TMResource.GOLD.storageBlock = new EmptyEntry<>(Blocks.GOLD_BLOCK, 0);
		TMResource.COAL.storageBlock = new EmptyEntry<>(Blocks.COAL_BLOCK, 0);
		TMResource.GLOWSTONE.storageBlock = new EmptyEntry<>(Blocks.GLOWSTONE, 0);
		TMResource.LAPIS.storageBlock = new EmptyEntry<>(Blocks.LAPIS_BLOCK, 0);
		TMResource.NETHER_QUARTZ.storageBlock = new EmptyEntry<>(Blocks.QUARTZ_BLOCK, 0);
		TMResource.OBSIDIAN.storageBlock = new EmptyEntry<>(Blocks.OBSIDIAN, 0);
		TMResource.REDSTONE.storageBlock = new EmptyEntry<>(Blocks.REDSTONE_BLOCK, 0);
		VillageHandler.init();
		log.info("Loading Recipes");
		if(RecipeHelper.genJson())CraftingRecipes.init();
		FurnaceRecipes.init();
		if (Config.enableResearchSystem)ResearchLoader.init();
		if(RecipeHelper.genJson())AdvancedCraftingRecipes.init();
		TMResource.loadRecipes();
		MachineCraftingHandler.loadRecipes();
		FuelHandler.init();
		RecipeHelper.writeFactories();
		r = initRunnables;
		initRunnables = new Stack<>();
		start = r.size() + 1;
		log.info("Processing " + start + " task(s)...");
		bar = ProgressManager.push("Processing Tasks 2", start);
		while (!r.isEmpty()){
			r.pop().run();
			bar.step((start - r.size()) + "/" + start);
		}
		bar.step((start - r.size()) + "/" + start);
		try {
			AdvancedCraftingHandler.loadRecipes();
		} catch (Exception e) {
			log.error("Error loading advanced recipes, things might not going to work!", e);
			throw new RuntimeException("Error loading advanced recipes, things might not going to work!", e);
		}
		ProgressManager.pop(bar);
		if(!initRunnables.isEmpty()){
			log.info("Post Processing tasks");
			while(!initRunnables.isEmpty())initRunnables.pop().run();
		}
		initRunnables = null;
		log.info("Task(s) completed");
		reloadables.add(AdvancedCraftingHandler::loadRecipes);
		log.info("Reloading Recipes");
		RecipeHelper.askReload();
		reload();
		Config.printWarnings();
		isInit = false;
		hadInit = true;
		long time = System.currentTimeMillis() - tM;
		log.info("Initialization took in " + time + " milliseconds");
	}

	@EventHandler
	public static void PostLoad(FMLPostInitializationEvent PostEvent) {
		log.info("Start Post Initialization");
		long tM = System.currentTimeMillis();
		proxy.postInit();
		TomsModAPIMain.init();
		log.info("Setting Max Stack Size of Minecarts to " + Config.minecartMaxStackSize + ".");
		Items.MINECART.setMaxStackSize(Config.minecartMaxStackSize);
		Items.CHEST_MINECART.setMaxStackSize(Config.minecartMaxStackSize);
		Items.TNT_MINECART.setMaxStackSize(Config.minecartMaxStackSize);
		Items.FURNACE_MINECART.setMaxStackSize(Config.minecartMaxStackSize);
		Items.HOPPER_MINECART.setMaxStackSize(Config.minecartMaxStackSize);
		versionCheckResult = ForgeVersion.getResult(mc);
		Logger vcLog = LogManager.getLogger(Configs.ModName + "] [Version Checker");
		if (versionCheckResult.status == Status.OUTDATED) {
			vcLog.warn("****************************************");
			vcLog.warn("* Tom's Mod is OUTDATED!!");
			vcLog.warn("* Current version: " + Configs.version);
			vcLog.warn("* Online version: " + versionCheckResult.target.toString());
			vcLog.warn("****************************************");
		} else if (versionCheckResult.status == Status.AHEAD) {
			vcLog.warn("??? status == AHEAD ???");
			vcLog.warn("?? Current version: " + Configs.version);
		} else if (versionCheckResult.status == Status.PENDING || versionCheckResult.status == Status.FAILED) {
			vcLog.warn("Tom's Mod version checking failed.");
		} else {
			vcLog.info("Tom's Mod is up to date");
		}
		String[] tools = new String[]{"!_axe", "!_pickaxe", "!_shovel", "!_sword", "!_hoe"};
		List<String> toNerf = new ArrayList<>();
		if (Config.nerfedTools != null && Config.nerfedTools.length > 0)
			Stream.of(Config.nerfedTools).map(ResourceLocation::new).filter(Item.REGISTRY::containsKey).map(ResourceLocation::toString).forEach(toNerf::add);
		if (Config.disableWoodenTools) {
			log.info("Nerfing wooden tools");
			Stream.of(tools).map(t -> t.replace("!", "wooden")).forEach(toNerf::add);
		}
		if (Config.disableStoneTools) {
			log.info("Nerfing stone tools");
			Stream.of(tools).map(t -> t.replace("!", "stone")).forEach(toNerf::add);
		}
		if (Config.disableIronTools) {
			log.info("Nerfing iron tools");
			Stream.of(tools).map(t -> t.replace("!", "iron")).forEach(toNerf::add);
		}
		if (Config.disableGoldTools) {
			log.info("Nerfing gold tools");
			Stream.of(tools).map(t -> t.replace("!", "gold")).forEach(toNerf::add);
		}
		if (Config.disableDiamondTools) {
			log.info("Nerfing diamond tools");
			Stream.of(tools).map(t -> t.replace("!", "diamond")).forEach(toNerf::add);
		}
		if (Config.enableHardModeStarting) {
			log.info("Overwriting Wood Harvest Levels");
			hackedWood = new HackedWoodMaterial();
			List<ItemStack> list = new ArrayList<>(OreDictionary.getOres("logWood"));
			list.addAll(OreDictionary.getOres("plankWood"));
			list.addAll(OreDictionary.getOres("slabWood"));
			list.addAll(OreDictionary.getOres("stairWood"));
			for (ItemStack stack : list) {
				Block block = Block.getBlockFromItem(stack.getItem());
				if (block != Blocks.AIR) {
					block.setHarvestLevel("axe", 0);
					ReflectionUtils.trySetFinalField(Block.class, Material.class, block, hackedWood, log, "Failed to set Material value for " + block.getUnlocalizedName());
				}
			}
		}
		toNerf.stream().map(ResourceLocation::new).map(Item.REGISTRY::getObject).filter(i -> i != null).forEach(CoreInit::nerfTool);
		Config.initFluids();
		Config.printWarnings();
		long time = System.currentTimeMillis() - tM;
		log.info("Post Initialization took in " + time + " milliseconds");
	}

	private static void nerfTool(Item tool) {
		try {
			if (tool instanceof ItemTool) {
				ReflectionHelper.setPrivateValue(ItemTool.class, (ItemTool) tool, null, "toolClass");// Remove tool class
				Field[] fields = ItemTool.class.getDeclaredFields();
				boolean s = false;
				for (Field f : fields) {
					f.setAccessible(true);
					Object o = f.get(tool);
					if (o instanceof Set) {
						ReflectionUtils.setFinalField(f, tool, Sets.newHashSet());
						log.info("Hacked effectiveBlocks name: " + f.getName());
						s = true;
					} else if (o instanceof Float) {
						f.setFloat(tool, 1);
					}
				}
				if (!s)
					log.error("Didn't find effectiveBlocks field");
				com.tom.handler.EventHandler.disabledItems.add(tool);
			} else if (tool instanceof ItemSword) {
				com.tom.handler.EventHandler.disabledItems.add(tool);
			} else if (tool instanceof ItemHoe) {
				com.tom.handler.EventHandler.disabledItems.add(tool);
			}
			proxy.runMethod((IClientMethod) () -> EventHandlerClient.addTooltipOverride(tool, "tomsMod.tooltip.nerfed", TextFormatting.RED));
			log.info("Successfully nerfed " + tool.getUnlocalizedName());
		} catch (Throwable e) {
			log.error("Unable to nerf " + tool.getUnlocalizedName() + " " + tool.getClass().getName(), e);
		}
	}

	@EventHandler
	public static void onServerStart(FMLServerStartingEvent event) {
		log.info("Server Start");
		proxy.serverStart();
		Transformers.injectNewFillCmd(event.getServer());
		event.registerServerCommand(new CommandWaypoint(false));
		event.registerServerCommand(new CommandWaypoint(true));
		MapHandler.init(new File(TomsModUtils.getSavedFile(), MapHandler.worldDirConfigName));
		if (Config.enableResearchSystem)
			event.registerServerCommand(new CommandResearch());
		event.registerServerCommand(new CommandTMReload());
		Config.printWarnings();
		log.info("Loading Completed");
	}

	@EventHandler
	public static void onServerStop(FMLServerStoppingEvent event) {
		log.info("Stopping the Server");
		TileEntityEnderMemory.EnderMemoryIComputerAccess.clear();
		TileEntityEnderMemory.globals.clear();
		MapHandler.close();
		log.info("Server Stopped");
	}
	private static void initializeFluidBlocksAndBuckets() {
		log.info("Loading Fluids...");
		for (FluidSupplier fluids : fluids) {
			Fluid fluid = fluids.get();
			if (!FluidRegistry.isFluidRegistered(fluid.getName())) {
				FluidRegistry.registerFluid(fluid);
			}
			if (fluid.getName().equals("oil") && fluid.getBlock() == null) {
				Block blockOil = new BlockOil();
				addOnlyBlockToGameRegisty(blockOil, blockOil.getUnlocalizedName().substring(5));
			} else if (fluid.getName().equals("ironchloride".toLowerCase()) && fluid.getBlock() == null) {
				Block blockOil = new BlockAcid();
				addOnlyBlockToGameRegisty(blockOil, blockOil.getUnlocalizedName().substring(5));
			}
			fluid = FluidRegistry.getFluid(fluid.getName());
			log.info("Loading Fluid: " + fluid.getUnlocalizedName());
			fluidList.put(fluid.getName(), fluid);
			ignoredLocations.add(fluid.getName());
			FluidRegistry.addBucketForFluid(fluid);
			fluids.update();
		}
	}

	public static void registerBlock(Block block) {
		registerBlock(block, block.getUnlocalizedName().substring(5));
	}

	public static class BlockFluidTomsMod extends BlockFluidClassic {

		public BlockFluidTomsMod(Fluid fluid, Material material) {
			super(fluid, material);
			setUnlocalizedName(fluid.getName());
		}

		public BlockFluidTomsMod(Fluid fluid) {
			this(fluid, Material.WATER);
		}
	}

	public static void registerBlock(Block block, String name) {
		addBlockToGameRegistry(block, name);
		Item item = Item.getItemFromBlock(block);
		if (block instanceof IModelRegisterRequired) {
			customModelRegisterRequired.add((IModelRegisterRequired) block);
		} else {
			blockList.add(block);
			if (item instanceof IModelRegisterRequired) {
				customModelRegisterRequired.add((IModelRegisterRequired) item);
			} else
				itemList.add(item);
		}
	}

	public static void registerMultiBlock(IMultiBlockInstance block) {
		for (Block b : block.getBlocks()) {
			addOnlyBlockToGameRegisty(b, b.getUnlocalizedName().substring(5));
		}
		Item item = block.createItemBlock();
		addItemToGameRegistry(item, item.getUnlocalizedName().substring(5));
		if (block instanceof IModelRegisterRequired) {
			customModelRegisterRequired.add((IModelRegisterRequired) block);
		} else {
			if (item instanceof IModelRegisterRequired) {
				customModelRegisterRequired.add((IModelRegisterRequired) item);
			} else
				itemList.add(item);
		}
	}

	public static void registerBlock(Block block, ItemBlock itemBlock) {
		addBlockToGameRegistry(block, block.getUnlocalizedName().substring(5), itemBlock);
		if (block instanceof IModelRegisterRequired) {
			customModelRegisterRequired.add((IModelRegisterRequired) block);
		} else {
			blockList.add(block);
			if (itemBlock instanceof IModelRegisterRequired) {
				customModelRegisterRequired.add((IModelRegisterRequired) itemBlock);
			} else
				itemList.add(itemBlock);
		}
	}

	public static void registerItem(Item item) {
		registerItem(item, item.getUnlocalizedName().substring(5));
	}

	public static void registerItem(Item item, String registerName) {
		if (item instanceof IModelRegisterRequired) {
			customModelRegisterRequired.add((IModelRegisterRequired) item);
		} else
			itemList.add(item);
		addItemToGameRegistry(item, registerName);
	}

	public static void addItemToGameRegistry(Item item, String name) {
		addItemToGameRegistry(item, name, true);
	}

	public static void addBlockToGameRegistry(Block block, String name) {
		if (block instanceof ICustomItemBlock)
			addBlockToGameRegistry(block, name, ((ICustomItemBlock) block).createItemBlock());
		else
			addBlockToGameRegistry(block, name, new ItemBlock(block));
	}

	public static void addBlockToGameRegistry(Block block, String name, ItemBlock itemBlock) {
		addOnlyBlockToGameRegisty(block, name, false);
		addItemToGameRegistry(itemBlock, name, false);
		if (block instanceof IRegisterRequired)
			((IRegisterRequired) block).register();
		if (itemBlock instanceof IRegisterRequired)
			((IRegisterRequired) itemBlock).register();
	}

	public static void addOnlyBlockToGameRegisty(Block block, String name) {
		addOnlyBlockToGameRegisty(block, name, true);
	}

	private static void addOnlyBlockToGameRegisty(Block block, String name, boolean callRegister) {
		ForgeRegistries.BLOCKS.register(block.getRegistryName() == null ? block.setRegistryName(name) : block);
		if (callRegister && block instanceof IRegisterRequired)
			((IRegisterRequired) block).register();
		if (callRegister && block instanceof IModelRegisterRequired) {
			customModelRegisterRequired.add((IModelRegisterRequired) block);
		}
	}

	private static void addItemToGameRegistry(Item item, String name, boolean callRegister) {
		ForgeRegistries.ITEMS.register(item.getRegistryName() == null ? item.setRegistryName(name) : item);
		if (callRegister && item instanceof IRegisterRequired)
			((IRegisterRequired) item).register();
	}
	public static void registerTileEntity(Class<? extends TileEntity> class1, String string) {
		GameRegistry.registerTileEntity(class1, new ResourceLocation(Configs.Modid, string));
	}

	public static CreativeTabs tabTomsModBlocks = new CreativeTabs("tabTomsModBlocks") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(MachineFrameTitanium);
		}

	};
	public static CreativeTabs tabTomsModItems = new CreativeTabs("tabTomsModItems") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(buildGuide);
		}

	};
	public static CreativeTabs tabTomsModMaterials = new CreativeTabs("tabTomsModMaterials") {

		@Override
		public ItemStack getTabIconItem() {
			return TMResource.BLUE_METAL.getStackNormal(Type.INGOT);
		}

	};
	public static void registerFluid(Fluid fluid, String fluidName) {

		if (!FluidRegistry.isFluidRegistered(fluidName)) {
			FluidRegistry.registerFluid(fluid);
		}
		fluid = FluidRegistry.getFluid(fluidName);
		fluidList.put(fluidName, fluid);
	}

	public static Fluid getFluid(String name) {
		return fluidList.get(name);
	}

	@EventHandler
	public static void onIMCMessages(IMCEvent event) {
		log.info("Receiving IMC");
		for (IMCMessage message : event.getMessages()) {
			try {
				IMCHandler.receive(message);
			} catch (Exception e) {
				log.error("CRITICAL EXCEPTION occurred while handling IMC. Ignoring the current IMC message!");
				log.error(e.toString());
				e.printStackTrace();
				log.error("Message sent by: " + message.getSender());
			}
		}
	}
	public static void registerRender(Item item) {
		registerRender(item, 0);
	}

	public static void registerRender(Item item, int meta) {
		String type = getNameForItem(item).replace("|", "");
		registerRender(item, meta, type);
	}

	public static void registerRender(Item item, int meta, String name) {
		modelList.add(new EmptyEntry<Item, Entry<ModelResourceLocation, Integer>>(item, new EmptyEntry<>(new ModelResourceLocation(name, "inventory"), meta)));
	}

	public static void registerRender(ItemStack stack, String name) {
		registerRender(stack.getItem(), stack.getMetadata(), name);
	}

	@SideOnly(Side.CLIENT)
	private static void addRenderToRegistry(Item item, int meta, ModelResourceLocation loc, ProgressBar bar) {
		String toString = loc.toString() + ":" + meta;
		bar.step(toString);
		ModelLoader.setCustomModelResourceLocation(item, meta, loc);
	}

	public static String getNameForItem(Item item) {
		Object obj = Item.REGISTRY.getNameForObject(item);
		if (obj == null) { return null; }
		return obj.toString();
	}

	public static String getNameForBlock(Block block) {
		Object obj = Block.REGISTRY.getNameForObject(block);
		if (obj == null) { return null; }
		return obj.toString();
	}

	@SideOnly(Side.CLIENT)
	public static ModelResourceLocation getItemResourceLocation(Item item) {
		String type = getNameForItem(item).replace("|", "");
		return new ModelResourceLocation(type, "inventory");
	}

	@SideOnly(Side.CLIENT)
	public static ModelResourceLocation getBlockResourceLocation(Block block) {
		String type = getNameForBlock(block).replace("|", "");
		return new ModelResourceLocation(type);
	}

	public static FluidSupplier createFluid(String name) {
		name = name.toLowerCase(Locale.ROOT);
		return createFluid(new Fluid(name, new ResourceLocation("tomsmodcore:blocks/" + name + "_still"), new ResourceLocation("tomsmodcore:blocks/" + name + "_flow")));
	}

	public static FluidSupplier createFluid(Fluid fluid) {
		FluidSupplier f = new FluidSupplier(fluid);
		fluids.add(f);
		return f;
	}

	/*
	 *
	 *         TMResource
	 *
	 */
	public static boolean hadPostPreInit() {
		return hadPostPreInit;
	}

	public static boolean hadInit() {
		return hadInit;
	}

	public static boolean isInit() {
		return isPreInit || isInit;
	}

	public static CreativeTabs tabTomsModToolsAndCombat = new CreativeTabs("tabTomsModToolsAndCombat") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(CoreInit.wrench);
		}
	};

	public static boolean isWrench(EntityPlayer player, EnumHand hand) {
		ItemStack held = player.getHeldItem(hand);
		return held.getItem() instanceof IWrench && ((IWrench) held.getItem()).isWrench(held, player);
	}

	public static CheckResult getVersionCheckResult() {
		return versionCheckResult;
	}

	public static void tryLoadAfterPreInit(Logger logIn) {
		for (IMod mod : modids) {
			if (!mod.hadPreInit()) { return; }
		}
		postPreInit(logIn);
		TMLogger.info("Pre Init done");
	}
	public static void addReloadableTask(Runnable r){
		reloadables.add(r);
	}
	public static void reload(){
		log.info("Reloading configurations");
		for (Runnable r : reloadables) {
			try {
				r.run();
			} catch (Exception e) {
				log.error("Failed to reload", e);
			}
		}
		MinecraftForge.EVENT_BUS.post(new TMReloadEvent());
		log.info("Reloading finished");
	}
	/**
	 * Minecraft.getMinecraft().getBlockRenderDispatcher().renderBlockBrightness(IBlockState, brightness)
	 */
}
