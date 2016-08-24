package com.tom.core;

import static com.tom.api.recipes.RecipeHelper.addRecipe;
import static com.tom.worldgen.WorldGen.OVERWORLD;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mapwriterTm.util.Reference;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

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
import net.minecraftforge.fml.common.Optional;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import com.tom.api.TomsModAPIMain;
import com.tom.api.block.ICustomItemBlock;
import com.tom.api.block.IIconRegisterRequired;
import com.tom.api.block.IRegisterRequired;
import com.tom.api.energy.EnergyType;
import com.tom.api.item.IWrench;
import com.tom.api.item.ItemCraftingTool;
import com.tom.api.item.MultipartItem;
import com.tom.api.tileentity.MultiblockPartList;
import com.tom.apis.EmptyEntry;
import com.tom.apis.TMLogger;
import com.tom.apis.TomsModUtils;
import com.tom.client.CustomModelLoader;
import com.tom.config.Config;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.core.research.handler.ResearchLoader;
import com.tom.defense.DefenseInit;
import com.tom.energy.EnergyInit;
import com.tom.factory.FactoryInit;
import com.tom.handler.AchievementHandler;
import com.tom.handler.FuelHandler;
import com.tom.handler.IMCHandler;
import com.tom.handler.PlayerHandler;
import com.tom.handler.WorldHandler;
import com.tom.lib.Configs;
import com.tom.lib.GlobalFields;
import com.tom.network.NetworkInit;
import com.tom.proxy.CommonProxy;
import com.tom.recipes.AdvancedCraftingRecipes;
import com.tom.recipes.CentifugeRecipes;
import com.tom.recipes.CraftingRecipes;
import com.tom.recipes.ElectrolyzerRecipes;
import com.tom.recipes.FurnaceRecipes;
import com.tom.recipes.MultiblockCrafterRecipes;
import com.tom.recipes.OreDict;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.storage.StorageInit;
import com.tom.thirdparty.computercraft.MultipartProvider;
import com.tom.transport.TransportInit;
import com.tom.worldgen.WorldGen;
import com.tom.worldgen.WorldGen.OreGenEntry;

import com.tom.core.block.Antenna;
import com.tom.core.block.AntennaController;
import com.tom.core.block.BlockCertusOre;
import com.tom.core.block.BlockOil;
import com.tom.core.block.BlockOre;
import com.tom.core.block.BlockRsDoor;
import com.tom.core.block.BlockRubberWood;
import com.tom.core.block.BlockTreeTap;
import com.tom.core.block.Camera;
import com.tom.core.block.CommandExecutor;
import com.tom.core.block.ControllerBox;
import com.tom.core.block.EnderMemory;
import com.tom.core.block.EnderPlayerSensor;
import com.tom.core.block.GPU;
import com.tom.core.block.HolotapeReader;
import com.tom.core.block.HolotapeWriter;
import com.tom.core.block.ItemProxy;
import com.tom.core.block.Jammer;
import com.tom.core.block.MagCardDevice;
import com.tom.core.block.MagCardReader;
import com.tom.core.block.MaterialBlock;
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
import com.tom.core.item.ItemAdvancedElectronicParts;
import com.tom.core.item.ItemBattery;
import com.tom.core.item.ItemBigNoteBook;
import com.tom.core.item.ItemCircuitAdvanced;
import com.tom.core.item.ItemCircuitBasic;
import com.tom.core.item.ItemElectronicParts;
import com.tom.core.item.ItemEntityTracker;
import com.tom.core.item.ItemMGlass;
import com.tom.core.item.ItemTreeTap;
import com.tom.core.item.ItemWrench;
import com.tom.core.item.LinkedChipset;
import com.tom.core.item.Linker;
import com.tom.core.item.MagCard;
import com.tom.core.item.MortarAndPestle;
import com.tom.core.item.ResourceItem;
import com.tom.core.item.ResourceItem.CraftingItem;
import com.tom.core.item.RsDoor;
import com.tom.core.item.Tablet;
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
import com.tom.core.tileentity.TileEntityHolotapeReader;
import com.tom.core.tileentity.TileEntityHolotapeWriter;
import com.tom.core.tileentity.TileEntityItemProxy;
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
import com.tom.core.tileentity.TileEntityTreeTap;
import com.tom.core.tileentity.TileEntityWirelessPeripheral;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.MultipartRegistry;

@Mod(modid = CoreInit.modid,name = "Tom's Mod Core", version = Configs.version, dependencies = Configs.mainDependencies, updateJSON = Configs.updateJson)

public final class CoreInit {
	public static final String modid = Configs.Modid + "|Core";
	public static final List<String> modids = new ArrayList<String>();
	public static final Logger log = LogManager.getLogger(modid);
	//Fluid stuffs
	public static final List<Fluid> fluids = new ArrayList<Fluid>();
	//public static List<Boolean> nativeFluids = new ArrayList<Boolean>();
	//public static Map<Block, Item> fluidBlockToBucketMap = new HashMap<Block, Item>();
	//public static Map<Fluid, Block> fluidToBlockMap = new HashMap<Fluid, Block>();
	public static final BiMap<String, Fluid> fluidList = HashBiMap.create();
	public static final List<Item> itemList = new ArrayList<Item>();
	public static final List<Block> blockList = new ArrayList<Block>();
	private static final List<Entry<Entry<String, String>, Class<? extends IMultipart>>> multipartList = new ArrayList<Entry<Entry<String, String>, Class<? extends IMultipart>>>();
	private static boolean isPreInit = false, isInit = false;
	public static String configFolder = "";
	private static final List<Entry<Item, Entry<ModelResourceLocation, Integer>>> modelList = new ArrayList<Map.Entry<Item,Entry<ModelResourceLocation,Integer>>>();
	public static final List<String> ignoredLocations = new ArrayList<String>();
	/**Block Ore, y, dim, a*/
	public static final Map<Predicate<World>, List<OreGenEntry>> oreList = new HashMap<Predicate<World>, List<OreGenEntry>>();
	public static boolean isCCLoaded = false, isPneumaticCraftLoaded = false, isMapEnabled = false, isAdventureItemsLoaded = false/*, isSingle*/;
	private static CheckResult versionCheckResult;
	private static final List<IIconRegisterRequired> customIconRegisterRequired = new ArrayList<IIconRegisterRequired>();
	//public static File mapFolder;
	//public static BiMap<Fluid, IIcon[]> fluidIcons = HashBiMap.create();
	//Fluids
	public static Fluid plasma;
	public static Fluid fusionFuel;
	public static Fluid Deuterium, Hydrogen, Oxygen;
	public static Fluid Tritium;
	//public static Fluid ePlasma;
	//public static Fluid hCoolant;
	//public static Fluid coolant;
	public static Fluid steam;
	public static Fluid nuclearWaste;
	public static Fluid oil;
	public static Fluid sulfuricAcid, sulfurDioxide, sulfurTrioxide, chlorine, hydrogenChlorine, creosoteOil;
	//Items
	public static Item ItemRawCircuit, ItemElectronicParts, ItemCircuitBasic, ItemCircuitAdvanced, ItemAdvancedElectronicParts;
	public static Item memoryCard, TabletHouse, Battery, Display, linkedChipset, connectionModem, trProcessor, connectionBoxModem;
	public static Item linker, wrenchA, entityTracker, holotape, portableReader, magCard, electricalMagCard;
	public static Tablet Tablet;
	//public static Item itemGpuCable;
	public static Item flintAxeHead;
	private static ResourceItem dust;
	private static ResourceItem dustTiny;
	private static ResourceItem ingot;
	private static ResourceItem nugget;
	private static ResourceItem plate;
	private static ResourceItem cable;
	private static ResourceItem gem;
	private static ResourceItem coil;
	private static ResourceItem crushedOre, crushedOreN, crushedOreE, shard, clump;
	private static ResourceItem researchPod;
	protected static CraftingItem craftingMaterial;
	public static ItemCraftingTool hammer, mortarAndPestle, wireCutters;
	private static MaterialBlock materialBlock1, materialBlock2;
	public static Item emptyWireCoil;
	public static Item itemPump, uraniumRod, dUraniumRod, uraniumRodEmpty;
	public static Item rsDoor, wrench, bigNoteBook, noteBook, magnifyingGlass, configurator, treeTap;
	//Multiparts
	//Blocks
	public static Block GPU, Monitor;
	public static Block TabletController, TabletAccessPoint, Antenna, AntennaController, WirelessPeripheral, TabletCrafter, ControllerBox, Jammer/*, AntBase, AntMid, AntTop*/;
	//public static Block GpuCable;
	public static Block MachineFrameBronze, MachineFrameBasic, MachineFrameSteel, MachineFrameChrome, MachineFrameTitanium;
	public static Block enderMemory, CCProxy, holotapeWriter, holotapeReader, MagCardDevice, MagCardReader, RedstonePort/*, ComputerRegulator*/;
	//public static Block RedstoneDuct, ItemDuct, FluidDuct;
	public static Block EnergySensor, blockRsDoor;
	public static Block oreBlueMetal, oreTin, oreNickel, oreTitanium, oreCopper, oreUranium, oreRedDiamond, oreLithium, oreCyanite, orePlatinum, oreQuartz, oreSilver, oreLead, oreZinc, oreChrome, oreSulfur, oreMercury, oreBauxite, oreCertusQuartz, oreWolfram;
	//public static Block EnderMinningWell, ExtendedEnderMinningWell;
	public static Block ItemProxy, EnderPlayerSensor, CommandExecutor, Camera, researchTable, rubberWood, rubberLeaves, rubberSapling;
	public static Block skyStone, blockTreetap;

	@SidedProxy(clientSide = Configs.CLIENT_PROXY_CLASS, serverSide = Configs.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	@Instance(modid)
	public static CoreInit modInstance;

	@EventHandler
	public static void construction(FMLConstructionEvent event){
		log.info("Tom's Mod Version: "+Configs.version);
		FluidRegistry.enableUniversalBucket();
		modids.add(modid);
	}

	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent){
		//System.out.println("Start Pre Initialization");
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		isPreInit = true;
		isCCLoaded = Loader.isModLoaded(Configs.COMPUTERCRAFT);
		//isCCLoaded = true;
		isPneumaticCraftLoaded = Loader.isModLoaded(Configs.PNEUMATICCRAFT);
		String configPathRaw = PreEvent.getSuggestedConfigurationFile().getAbsolutePath().replace("|", "_");
		String configPath = configPathRaw.substring(0, configPathRaw.length()-9)+File.separator;
		Config.init(new File(configPath));
		configFolder = configPath;
		//log.info(configPath);
		/**Items*/
		ItemRawCircuit = new Item()/*.setTextureName("minecraft:RawCirc")*/.setUnlocalizedName("ItemRawCircuit").setCreativeTab(tabTomsModItems);
		ItemElectronicParts = new ItemElectronicParts().setUnlocalizedName("ItemElectronicParts").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:itemElectronicParts")*/;
		ItemCircuitBasic = new ItemCircuitBasic().setUnlocalizedName("ItemCircuitBasic").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:basicCircBoard")*/;
		Tablet = new Tablet().setUnlocalizedName("tablet").setCreativeTab(tabTomsModItems);
		//itemGpuCable = new itemGpuCable().setUnlocalizedName("itemGpuCable").setCreativeTab(tabTomsModItems);
		//ingotChrome = new ingotChrome().setUnlocalizedName("itemCromeIngot").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:ingotChrome")*/;
		//ingotRedAlloy = new ingotRedAlloy().setUnlocalizedName("ingotRedAlloy").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:redAlloyIngot")*/;
		//ingotUranium = new ingotUranium().setUnlocalizedName("ingotUranium").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:ingotUran")*/;
		//cableCopper = new cableCopper().setUnlocalizedName("cableCopper").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:cableCopper")*/;
		//cableGold = new cableGold().setUnlocalizedName("cableGold").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:cableGold")*/;
		//cableRedAlloy = new cableRedAlloy().setUnlocalizedName("cableRedAlloy").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:cableRedAlloy")*/;
		//cableElectrum = new cableElectrum().setUnlocalizedName("cableElectrum").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:cableElectrum")*/;
		//cableSilver = new cableSilver().setUnlocalizedName("cableSilver").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:cableSilver")*/;
		ItemCircuitAdvanced = new ItemCircuitAdvanced().setUnlocalizedName("ItemCircuitAdvanced").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:advCircBoard")*/;
		ItemAdvancedElectronicParts = new ItemAdvancedElectronicParts().setUnlocalizedName("ItemAdvancedElectronicParts").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:itemElectronicPartsAdvanced")*/;
		//fusionModule = new fusionModule().setUnlocalizedName("fusionModule").setCreativeTab(tabTomsModItems);
		//itemPowerModule = new Item().setUnlocalizedName("itemPowerModule").setCreativeTab(tabTomsModItems);
		itemPump = new Item().setUnlocalizedName("itemPump").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:itemPump")*/;
		uraniumRod = new UraniumRod().setUnlocalizedName("uraniumRod").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:uranCellFull")*/;
		dUraniumRod = new Item().setUnlocalizedName("dUraniumRod").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:uranCellDepleted")*/;
		//dustIron = new dustIron().setUnlocalizedName("dustIron").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:dustIron")*/;
		//dustIronTiny = new dustIronTiny().setUnlocalizedName("dustIronTiny").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:dustTinyIron")*/;
		uraniumRodEmpty = new Item().setUnlocalizedName("uranRodEmpty")/*.setTextureName("minecraft:uranCellEmpty")*/.setCreativeTab(tabTomsModItems);
		//chargedRedstone = new ChargedRedstone().setUnlocalizedName("chargedRedstone").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:tm/chargedRedstone")*/;
		//chargedGlowstone = new ChargedGlowstone().setUnlocalizedName("chargedGlowstone").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:tm/chargedGlowstone")*/;
		//chargedEnderpearl = new ChargedEnderpearl().setUnlocalizedName("chargedEnder").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:tm/chargedEnderPearl")*/;
		//bigRedstone = new BigRedstone().setUnlocalizedName("bigRedstone").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:big_redstone_dust")*/;
		//bigGlowstone = new BigGlowstone().setUnlocalizedName("bigGlowstone").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:big_glowstone_dust")*/;
		//bigEnderpearl = new BigEnderpearl().setUnlocalizedName("bigEnderPearl").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:bigEnder")*/;
		//dustTitanium = new Item().setUnlocalizedName("titaniumDust").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:dustTitanium")*/;
		//dustChrome = new Item().setUnlocalizedName("chromeDust").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:dustChrome")*/;
		//plateChrome = new Item().setUnlocalizedName("chromePlate").setCreativeTab(tabTomsModMaterials)/*.setTextureName("minecraft:plateChrome")*/;
		memoryCard = new Item().setUnlocalizedName("memoryCard").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:memoryCard")*/;
		linker = new Linker().setUnlocalizedName("linker").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:linker")*/;
		Battery = new ItemBattery().setUnlocalizedName("Battery").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:battery")*/;
		Display = new Item().setUnlocalizedName("Display").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tm/display")*/;
		//plateTitanium = new Item().setUnlocalizedName("plateTitanium").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:titaniumPlate")*/;
		//dustUranium = new Item().setUnlocalizedName("dustUranium").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:dustUranium")*/;
		flintAxeHead = new Item().setUnlocalizedName("flintAxeHead").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:flintAxeHead")*/;
		connectionBoxModem = new Item().setUnlocalizedName("cBoxModem").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tm/modem2")*/.setMaxStackSize(1);
		wrenchA = new Item().setUnlocalizedName("wrenchA").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tm/wrench")*/.setMaxStackSize(1);
		entityTracker = new ItemEntityTracker().setUnlocalizedName("itemEntityTracker").setCreativeTab(tabTomsModItems).setMaxStackSize(1);
		holotape = new Holotape().setUnlocalizedName("holotape").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tm/holotape")*/.setMaxStackSize(1);
		magCard = new MagCard().setUnlocalizedName("magCard").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tm/magCard")*/.setMaxStackSize(1);
		electricalMagCard = new ElectricalMagCard().setUnlocalizedName("eMagCard").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tm/eMagCard")*/.setMaxStackSize(1);
		rsDoor = new RsDoor().setUnlocalizedName("rsDoor").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tm/door")*/.setMaxStackSize(4);
		plate = new ResourceItem(Type.PLATE);
		ingot = new ResourceItem(Type.INGOT);
		dust = new ResourceItem(Type.DUST);
		dustTiny = new ResourceItem(Type.DUST_TINY);
		cable = new ResourceItem(Type.CABLE);
		nugget = new ResourceItem(Type.NUGGET);
		wrench = new ItemWrench().setUnlocalizedName("wrench").setCreativeTab(tabTomsModWeaponsAndTools).setMaxStackSize(1);
		gem = new ResourceItem(Type.GEM);
		coil = new ResourceItem(Type.COIL);
		emptyWireCoil = new Item().setUnlocalizedName("emptyCoil").setCreativeTab(tabTomsModMaterials);
		crushedOre = new ResourceItem(Type.CRUSHED_ORE);
		crushedOreN = new ResourceItem(Type.CRUSHED_ORE_NETHER);
		crushedOreE = new ResourceItem(Type.CRUSHED_ORE_END);
		researchPod = new ResourceItem(Type.RESEARCH_ITEM);
		craftingMaterial = new CraftingItem().setCreativeTab(tabTomsModMaterials);
		bigNoteBook = new ItemBigNoteBook().setUnlocalizedName("bigNoteBook").setCreativeTab(tabTomsModItems).setMaxStackSize(1);
		magnifyingGlass = new ItemMGlass().setUnlocalizedName("mGlass").setCreativeTab(tabTomsModItems).setMaxStackSize(1);
		noteBook = new Item().setUnlocalizedName("noteBook").setCreativeTab(tabTomsModItems).setMaxStackSize(1);
		configurator = new Configurator().setCreativeTab(tabTomsModWeaponsAndTools).setMaxStackSize(1).setUnlocalizedName("tm.configurator");
		hammer = new Hammer().setUnlocalizedName("tm.hammer");
		shard = new ResourceItem(Type.SHARD);
		clump = new ResourceItem(Type.CLUMP);
		mortarAndPestle = new MortarAndPestle().setUnlocalizedName("tm.mortarAndPestle");
		wireCutters = new WireCutters().setUnlocalizedName("tm.wireCutters");
		treeTap = new ItemTreeTap().setCreativeTab(tabTomsModBlocks).setUnlocalizedName("tm.itemTreeTap").setMaxStackSize(4);
		//dirtyDust = new ResourceItem(Type.DIRTY_DUST);
		/**Blocks*/
		MachineFrameBasic = new Block(Material.IRON).setHardness(2F).setResistance(10F).setUnlocalizedName("MachineFrameBasic").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:BasicMachineFrame")*/;
		MachineFrameSteel = new Block(Material.IRON).setHardness(4F).setResistance(20F).setUnlocalizedName("MachineFrameSteel").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:SteelMachineFrame")*/;
		MachineFrameChrome = new Block(Material.IRON).setHardness(4F).setResistance(10F).setUnlocalizedName("MachineFrameChrome").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:ChromeMachineFrame")*/;
		MachineFrameTitanium = new Block(Material.IRON).setHardness(5F).setResistance(20F).setUnlocalizedName("MachineFrameTitanium").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:TitaniumMachineFrame")*/;
		MachineFrameBronze = new Block(Material.IRON).setHardness(2F).setResistance(10F).setUnlocalizedName("MachineFrameBronze").setCreativeTab(tabTomsModBlocks);
		/**TileEntities*/
		//EnergySensor = new EnergySensor().setUnlocalizedName("energySensor").setCreativeTab(tabTomsModBlocks);
		Antenna = new Antenna().setUnlocalizedName("antenna").setCreativeTab(tabTomsModBlocks);
		ItemProxy = new ItemProxy().setUnlocalizedName("ItemProxy").setCreativeTab(tabTomsModBlocks);
		blockRsDoor = new BlockRsDoor().setUnlocalizedName("brsDoor");
		researchTable = new ResearchTable().setCreativeTab(tabTomsModBlocks).setUnlocalizedName("resTable");
		rubberWood = new BlockRubberWood().setUnlocalizedName("tm.rubberWood").setCreativeTab(tabTomsModBlocks);
		oreCertusQuartz = new BlockCertusOre().setCreativeTab(tabTomsModMaterials).setUnlocalizedName("tm.oreCertusQuartz");
		oreCyanite = new Block(Material.ROCK).setCreativeTab(tabTomsModMaterials).setUnlocalizedName("tm.oreCyanite").setHardness(50.0F).setResistance(2000.0F);
		materialBlock1 = new MaterialBlock(TMResource.ALUMINUM, TMResource.advAlloyMK1,    TMResource.advAlloyMK2, TMResource.BLUE_METAL,
				TMResource.BRONZE,   TMResource.CERTUS_QUARTZ,  TMResource.CHROME,      TMResource.COPPER,
				TMResource.ELECTRUM, TMResource.ENDERIUM,       TMResource.FLUIX,       TMResource.GREENIUM,
				TMResource.LEAD,     TMResource.LITHIUM,        TMResource.NICKEL,      TMResource.PLATINUM).setUnlocalizedName("materialStorage");
		materialBlock2 = new MaterialBlock(TMResource.QUARTZ, TMResource.RED_DIAMOND, TMResource.SILVER, TMResource.STEEL, TMResource.TIN,
				TMResource.TITANIUM, TMResource.URANIUM, TMResource.ZINC, TMResource.WOLFRAM, TMResource.TUNGSTENSTEEL).setUnlocalizedName("materialStorage2");
		rubberLeaves = new RubberLeaves().setCreativeTab(tabTomsModBlocks).setUnlocalizedName("tm.rubberLeaves");
		skyStone = new Block(Material.ROCK).setUnlocalizedName("tm.skyStone").setCreativeTab(tabTomsModMaterials).setHardness(5.3F).setResistance(20.55F);
		rubberSapling = new RubberSapling().setCreativeTab(tabTomsModBlocks).setUnlocalizedName("tm.rubberSapling");
		blockTreetap = new BlockTreeTap().setUnlocalizedName("tm.blockTreeTap");
		//Ores
		oreBlueMetal =  new BlockOre(60, OVERWORLD, 5, TMResource.BLUE_METAL).setUnlocalizedName("oreBlueMetal");
		oreCopper =     new BlockOre(70, OVERWORLD, 9, TMResource.COPPER).setUnlocalizedName("oreCopper");
		oreTin =        new BlockOre(60, OVERWORLD, 8, TMResource.TIN).setUnlocalizedName("oreTin");
		oreNickel =     new BlockOre(50, OVERWORLD, 6, TMResource.NICKEL).setUnlocalizedName("oreNickel");
		oreTitanium =   new BlockOre(30, OVERWORLD, 2, TMResource.TITANIUM).setUnlocalizedName("oreTitanium");
		oreUranium =    new BlockOre(35, OVERWORLD, 3, TMResource.URANIUM).setUnlocalizedName("oreUranium");
		oreLithium =    new BlockOre(50, OVERWORLD, 4, TMResource.LITHIUM).setUnlocalizedName("oreLithium");
		oreRedDiamond = new BlockOre(40, OVERWORLD, 1, TMResource.RED_DIAMOND.getStackNormal(Type.GEM), TMResource.RED_DIAMOND).setUnlocalizedName("oreRedDiamond");
		oreQuartz =     new BlockOre(40, OVERWORLD, 1, TMResource.QUARTZ.getStackNormal(Type.GEM), TMResource.QUARTZ).setUnlocalizedName("oreQuartz");
		orePlatinum =   new BlockOre(10, OVERWORLD, 1, TMResource.PLATINUM).setUnlocalizedName("orePlatinum");
		oreSilver =     new BlockOre(40, OVERWORLD, 3, TMResource.SILVER).setUnlocalizedName("oreSilver");
		oreLead =       new BlockOre(35, OVERWORLD, 2, TMResource.LEAD).setUnlocalizedName("oreLead");
		oreZinc =       new BlockOre(65, OVERWORLD, 5, TMResource.ZINC).setUnlocalizedName("oreZinc");
		oreChrome =     new BlockOre(40, OVERWORLD, 3, TMResource.CHROME).setUnlocalizedName("oreChrome");
		oreSulfur =     new BlockOre(65, OVERWORLD, 6, TMResource.SULFUR.getStackNormal(Type.DUST, 2), TMResource.SULFUR).setUnlocalizedName("oreSulfur");
		oreMercury =    new BlockOre(30, OVERWORLD, 1, TMResource.MERCURY).setUnlocalizedName("oreMercury");
		oreBauxite =    new BlockOre(55, OVERWORLD, 6, TMResource.ALUMINUM).setUnlocalizedName("oreBauxite");
		oreWolfram =    new BlockOre(40, OVERWORLD, 3, TMResource.WOLFRAM).setUnlocalizedName("oreTungstate");
		//ComputerRegulator = new ComputerRegulator().setUnlocalizedName("computerRegulator").setCreativeTab(tabTomsModBlocks);
		//ExtendedEnderMinningWell = new ExtendedEnderMinningWell().setBlockName("ExtendedEnderMinningWell").setCreativeTab(tabTomsModBlocks);
		/*AntBase = new AntBase().setBlockName("AntBase").setCreativeTab(tabTomsModBlocks);
		AntMid = new AntMid().setBlockName("AntMid").setCreativeTab(tabTomsModBlocks);
		AntTop = new AntTop().setBlockName("AntTop").setCreativeTab(tabTomsModBlocks);*/
		/**Fluids*/
		plasma = new Fluid("tomsmodPlasma",new ResourceLocation("tomsmodcore:blocks/tomsmodplasma_still"),new ResourceLocation("tomsmodcore:blocks/tomsmodplasma_flow")).setTemperature(10000).setLuminosity(10).setViscosity(1100).setRarity(EnumRarity.UNCOMMON);
		//ePlasma = new Fluid("tomsmodEPlasma").setTemperature(10000).setLuminosity(12).setDensity(4000);
		Deuterium = new Fluid("tomsmodDeuterium",new ResourceLocation("tomsmodcore:blocks/tomsmoddeuterium_still"),new ResourceLocation("tomsmodcore:blocks/tomsmoddeuterium_flow")).setGaseous(true).setDensity(-400).setViscosity(600);
		Tritium = new Fluid("tomsmodTritium",new ResourceLocation("tomsmodcore:blocks/tomsmodtritium_still"),new ResourceLocation("tomsmodcore:blocks/tomsmodtritium_flow")).setGaseous(true).setDensity(-400).setViscosity(600);
		//hCoolant = new Fluid("tomsmodCoolantHot").setTemperature(500);
		//coolant = new Fluid("tomsmodCoolant");
		steam = new Fluid("steam",new ResourceLocation("tomsmodcore:blocks/steam_still"),new ResourceLocation("tomsmodcore:blocks/steam_flow")).setTemperature(500).setGaseous(true).setDensity(-200).setViscosity(800);
		fusionFuel = new Fluid("tomsmodFusionFuel".toLowerCase(),new ResourceLocation("tomsmodcore:blocks/tomsmodfusionfuel_still"),new ResourceLocation("tomsmodcore:blocks/tomsmodfusionfuel_flow"));
		Hydrogen = new Fluid("hydrogen",new ResourceLocation("tomsmodcore:blocks/hydrogen_still"),new ResourceLocation("tomsmodcore:blocks/hydrogen_flow")).setGaseous(true).setDensity(-500).setViscosity(500);
		nuclearWaste = new Fluid("tmNuclearWaste".toLowerCase(),new ResourceLocation("tomsmodcore:blocks/tmnuclearwaste_still"),new ResourceLocation("tomsmodcore:blocks/tmnuclearwaste_flow")).setLuminosity(7).setTemperature(400).setViscosity(600);
		oil = new Fluid("oil", new ResourceLocation("tomsmodcore:blocks/oil_still"),new ResourceLocation("tomsmodcore:blocks/oil_flow")).setViscosity(5000).setDensity(5000);
		sulfuricAcid = new Fluid("tmSulfuricAcid".toLowerCase(), new ResourceLocation("tomsmodcore:blocks/sulfurAcid_still"),new ResourceLocation("tomsmodcore:blocks/sulfurAcid_flow"));
		sulfurDioxide = new Fluid("tmSulfurDioxide".toLowerCase(), new ResourceLocation("tomsmodcore:blocks/sulfurDioxide_still"),new ResourceLocation("tomsmodcore:blocks/sulfurDioxide_flow")).setGaseous(true).setDensity(-500).setViscosity(500);
		sulfurTrioxide = new Fluid("tmSulfurTrioxide".toLowerCase(), new ResourceLocation("tomsmodcore:blocks/sulfurTrioxide_still"),new ResourceLocation("tomsmodcore:blocks/sulfurTrioxide_flow")).setGaseous(true).setDensity(-500).setViscosity(500);
		chlorine = new Fluid("chlorine", new ResourceLocation("tomsmodcore:blocks/chlorine_still"),new ResourceLocation("tomsmodcore:blocks/chlorine_flow")).setGaseous(true).setDensity(-600).setViscosity(400);
		hydrogenChlorine = new Fluid("hydrogenChlorine".toLowerCase(), new ResourceLocation("tomsmodcore:blocks/Hchlorine_still"),new ResourceLocation("tomsmodcore:blocks/Hchlorine_flow")).setGaseous(true).setDensity(-500).setViscosity(500);
		creosoteOil = new Fluid("creosote", new ResourceLocation("tomsmodcore:blocks/creosote_still"),new ResourceLocation("tomsmodcore:blocks/creosote_flow"));
		Oxygen = new Fluid("oxygen", new ResourceLocation("tomsmodcore:blocks/oxygen_still"),new ResourceLocation("tomsmodcore:blocks/oxygen_flow")).setGaseous(true);
		/**Fluid Registry*/
		///*
		fluids.add(plasma);
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
		fluids.add(sulfurDioxide);
		fluids.add(sulfurTrioxide);
		fluids.add(chlorine);
		fluids.add(hydrogenChlorine);
		fluids.add(creosoteOil);
		fluids.add(Oxygen);
		//*/
		/*registerFluid(plasma);
		registerFluid(ePlasma);
		registerFluid(Deuterium);
		registerFluid(Tritium);
		registerFluid(coolant);
		registerFluid(fusionFuel);
		registerFluid(hCoolant);
		registerFluid(steam);//*/
		/**Init Fluid Blocks and Buckets*/
		//for(Fluid fluid : fluids) {
		//registerFluid(fluid);
		//}
		//FluidRegistry.registerFluid(Deuterium);
		///**Get Registered Fluids*/
		//System.out.println(FluidRegistry.getFluid(plasma.getName()) == null);
		/**/
		//Monitor = new Monitor().setBlockName("monitor").setCreativeTab(tabTomsModBlocks);
		//
		//GpuCable = new GpuCable().setBlockName("gpuCable");
		/**Registry*/
		/**Items*/
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
		addItemToGameRegistry(craftingMaterial, craftingMaterial.getUnlocalizedName().substring(5));
		addItemToGameRegistry(shard, shard.getUnlocalizedName().substring(5));
		addItemToGameRegistry(clump, clump.getUnlocalizedName().substring(5));
		//addItemToGameRegistry(dirtyDust, dirtyDust.getUnlocalizedName().substring(5));
		//registerItem(ItemRawCircuit, ItemRawCircuit.getUnlocalizedName().substring(5));
		//registerItem(ItemElectronicParts, ItemElectronicParts.getUnlocalizedName().substring(5));
		//registerItem(ItemCircuitBasic, ItemCircuitBasic.getUnlocalizedName().substring(5));
		//registerItem(ingotChrome, ingotChrome.getUnlocalizedName().substring(5));
		//registerItem(ingotRedAlloy, ingotRedAlloy.getUnlocalizedName().substring(5));
		//registerItem(ingotUranium, ingotUranium.getUnlocalizedName().substring(5));
		/*registerItem(cableCopper, cableCopper.getUnlocalizedName().substring(5));
		registerItem(cableGold, cableGold.getUnlocalizedName().substring(5));
		registerItem(cableRedAlloy, cableRedAlloy.getUnlocalizedName().substring(5));
		registerItem(cableElectrum, cableElectrum.getUnlocalizedName().substring(5));
		registerItem(cableSilver, cableSilver.getUnlocalizedName().substring(5));*/
		//registerItem(ItemCircuitAdvanced, ItemCircuitAdvanced.getUnlocalizedName().substring(5));
		//registerItem(ItemAdvancedElectronicParts, ItemAdvancedElectronicParts.getUnlocalizedName().substring(5));
		//registerItem(fusionModule, fusionModule.getUnlocalizedName().substring(5));
		//registerItem(itemPowerModule, itemPowerModule.getUnlocalizedName().substring(5));
		registerItem(itemPump, itemPump.getUnlocalizedName().substring(5));
		registerItem(uraniumRod, uraniumRod.getUnlocalizedName().substring(5));
		registerItem(dUraniumRod, dUraniumRod.getUnlocalizedName().substring(5));
		registerItem(uraniumRodEmpty, uraniumRodEmpty.getUnlocalizedName().substring(5));
		//registerItem(dustIron, dustIron.getUnlocalizedName().substring(5));
		//registerItem(dustIronTiny, dustIronTiny.getUnlocalizedName().substring(5));
		/*registerItem(chargedRedstone, chargedRedstone.getUnlocalizedName().substring(5));
		registerItem(chargedGlowstone, chargedGlowstone.getUnlocalizedName().substring(5));
		registerItem(chargedEnderpearl, chargedEnderpearl.getUnlocalizedName().substring(5));
		registerItem(bigRedstone, bigRedstone.getUnlocalizedName().substring(5));
		registerItem(bigGlowstone, bigGlowstone.getUnlocalizedName().substring(5));
		registerItem(bigEnderpearl, bigEnderpearl.getUnlocalizedName().substring(5));
		//registerItem(dustTitanium, dustTitanium.getUnlocalizedName().substring(5));*/
		//registerItem(dustChrome, dustChrome.getUnlocalizedName().substring(5));
		//registerItem(plateChrome, plateChrome.getUnlocalizedName().substring(5));
		registerItem(memoryCard, memoryCard.getUnlocalizedName().substring(5));
		registerItem(linker, linker.getUnlocalizedName().substring(5));
		registerItem(Battery, Battery.getUnlocalizedName().substring(5));
		registerItem(Display, Display.getUnlocalizedName().substring(5));
		//registerItem(plateTitanium, plateTitanium.getUnlocalizedName().substring(5));
		//registerItem(dustUranium, dustUranium.getUnlocalizedName().substring(5));
		registerItem(rsDoor, rsDoor.getUnlocalizedName().substring(5));
		registerItem(wrench, wrench.getUnlocalizedName().substring(5));
		registerItem(emptyWireCoil, emptyWireCoil.getUnlocalizedName().substring(5));
		registerItem(bigNoteBook, bigNoteBook.getUnlocalizedName().substring(5));
		registerItem(magnifyingGlass, magnifyingGlass.getUnlocalizedName().substring(5));
		registerItem(noteBook, noteBook.getUnlocalizedName().substring(5));
		registerItem(flintAxeHead, flintAxeHead.getUnlocalizedName().substring(5));
		registerItem(configurator, configurator.getUnlocalizedName().substring(5));
		addItemToGameRegistry(hammer, hammer.getUnlocalizedName().substring(5));
		registerItem(mortarAndPestle, mortarAndPestle.getUnlocalizedName().substring(5));
		addItemToGameRegistry(wireCutters, wireCutters.getUnlocalizedName().substring(5));
		registerItem(treeTap, treeTap.getUnlocalizedName().substring(5));
		//registerItem(ironRod, ironRod.getUnlocalizedName().substring(5));
		/**Blocks*/
		registerBlock(MachineFrameBasic, MachineFrameBasic.getUnlocalizedName().substring(5));
		registerBlock(MachineFrameSteel, MachineFrameSteel.getUnlocalizedName().substring(5));
		registerBlock(MachineFrameChrome, MachineFrameChrome.getUnlocalizedName().substring(5));
		registerBlock(MachineFrameTitanium, MachineFrameTitanium.getUnlocalizedName().substring(5));
		registerBlock(MachineFrameBronze, MachineFrameBronze.getUnlocalizedName().substring(5));
		registerBlock(ItemProxy, ItemProxy.getUnlocalizedName().substring(5));
		addOnlyBlockToGameRegisty(blockRsDoor, blockRsDoor.getUnlocalizedName().substring(5));
		registerBlock(oreBlueMetal, oreBlueMetal.getUnlocalizedName().substring(5));
		registerBlock(oreCopper, oreCopper.getUnlocalizedName().substring(5));
		registerBlock(oreTin, oreTin.getUnlocalizedName().substring(5));
		registerBlock(oreNickel, oreNickel.getUnlocalizedName().substring(5));
		registerBlock(oreTitanium, oreTitanium.getUnlocalizedName().substring(5));
		registerBlock(oreRedDiamond, oreRedDiamond.getUnlocalizedName().substring(5));
		registerBlock(orePlatinum, orePlatinum.getUnlocalizedName().substring(5));
		registerBlock(oreQuartz, oreQuartz.getUnlocalizedName().substring(5));
		registerBlock(oreSilver, oreSilver.getUnlocalizedName().substring(5));
		registerBlock(oreLead, oreLead.getUnlocalizedName().substring(5));
		registerBlock(oreUranium, oreUranium.getUnlocalizedName().substring(5));
		registerBlock(oreLithium, oreLithium.getUnlocalizedName().substring(5));
		registerBlock(oreZinc, oreZinc.getUnlocalizedName().substring(5));
		registerBlock(researchTable, researchTable.getUnlocalizedName().substring(5));
		registerBlock(oreChrome, oreChrome.getUnlocalizedName().substring(5));
		registerBlock(oreSulfur, oreSulfur.getUnlocalizedName().substring(5));
		registerBlock(oreMercury, oreMercury.getUnlocalizedName().substring(5));
		registerBlock(oreBauxite, oreBauxite.getUnlocalizedName().substring(5));
		registerBlock(rubberWood, rubberWood.getUnlocalizedName().substring(5));
		registerBlock(oreCertusQuartz, oreCertusQuartz.getUnlocalizedName().substring(5));
		registerBlock(oreCyanite, oreCyanite.getUnlocalizedName().substring(5));
		registerBlock(materialBlock1, materialBlock1.itemBlock);
		registerBlock(materialBlock2, materialBlock2.itemBlock);
		registerBlock(rubberLeaves, rubberLeaves.getUnlocalizedName().substring(5));
		registerBlock(skyStone, skyStone.getUnlocalizedName().substring(5));
		registerBlock(rubberSapling, rubberSapling.getUnlocalizedName().substring(5));
		addOnlyBlockToGameRegisty(blockTreetap, blockTreetap.getUnlocalizedName().substring(5));
		registerBlock(oreWolfram, oreWolfram.getUnlocalizedName().substring(5));
		/*registerBlock(AntBase, AntBase.getUnlocalizedName().substring(5));
		registerBlock(AntTop, AntTop.getUnlocalizedName().substring(5));
		registerBlock(AntMid, AntMid.getUnlocalizedName().substring(5));*/
		/**TileEntities*/
		GameRegistry.registerTileEntity(TileEntityItemProxy.class, Configs.Modid + "ItemProxy");
		GameRegistry.registerTileEntity(TileEntityRSDoor.class, Configs.Modid + "rsDoor");
		GameRegistry.registerTileEntity(TileEntityResearchTable.class, Configs.Modid + "researchTable");
		GameRegistry.registerTileEntity(TileEntityTreeTap.class, Configs.Modid + "treetap");
		/**Integration*/
		log.info("Loading integration items...");
		if(isCCLoaded){
			GPU = new GPU().setUnlocalizedName("gpu").setCreativeTab(tabTomsModBlocks);
			Monitor = new Monitor().setUnlocalizedName("monitor").setCreativeTab(tabTomsModBlocks);
			enderMemory = new EnderMemory().setUnlocalizedName("enderMemory").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:EnderMemory3")*/;
			//CCProxy = new CCProxy().setUnlocalizedName("ccProxy").setCreativeTab(tabTomsModBlocks);
			WirelessPeripheral = new WirelessPeripheral().setUnlocalizedName("wirelessPeripheral").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:tm/TabContSideAct")*/;
			EnderPlayerSensor = new EnderPlayerSensor().setUnlocalizedName("EnderPlayerSensor").setCreativeTab(tabTomsModBlocks);
			holotapeWriter = new HolotapeWriter().setUnlocalizedName("holotapeWriter").setCreativeTab(tabTomsModBlocks);
			holotapeReader = new HolotapeReader().setUnlocalizedName("holotapeReader").setCreativeTab(tabTomsModBlocks);
			MagCardDevice = new MagCardDevice().setUnlocalizedName("magCardDevice").setCreativeTab(tabTomsModBlocks);
			MagCardReader = new MagCardReader().setUnlocalizedName("magCardReader").setCreativeTab(tabTomsModBlocks);
			RedstonePort = new RedstonePort().setUnlocalizedName("rsPort").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:rsPort")*/;
			registerItem(holotape, holotape.getUnlocalizedName().substring(5));
			registerItem(magCard, magCard.getUnlocalizedName().substring(5));
			registerBlock(GPU, GPU.getUnlocalizedName().substring(5));
			registerBlock(Monitor, Monitor.getUnlocalizedName().substring(5));
			registerBlock(WirelessPeripheral, WirelessPeripheral.getUnlocalizedName().substring(5));
			registerBlock(EnderPlayerSensor, EnderPlayerSensor.getUnlocalizedName().substring(5));
			//registerBlock(CCProxy, CCProxy.getUnlocalizedName().substring(5));
			registerBlock(enderMemory, enderMemory.getUnlocalizedName().substring(5));
			registerBlock(holotapeWriter, holotapeWriter.getUnlocalizedName().substring(5));
			registerBlock(holotapeReader, holotapeReader.getUnlocalizedName().substring(5));
			registerBlock(MagCardDevice, MagCardDevice.getUnlocalizedName().substring(5));
			registerBlock(MagCardReader, MagCardReader.getUnlocalizedName().substring(5));
			registerBlock(RedstonePort, RedstonePort.getUnlocalizedName().substring(5));
			//registerBlock(ComputerRegulator, ComputerRegulator.getUnlocalizedName().substring(5));
			GameRegistry.registerTileEntity(TileEntityEnderSensor.class, Configs.Modid + "EnderSensor");
			GameRegistry.registerTileEntity(TileEntityMonitor.class, Configs.Modid + ":mBlack");
			GameRegistry.registerTileEntity(TileEntityGPU.class, Configs.Modid + "GPU");
			GameRegistry.registerTileEntity(TileEntityWirelessPeripheral.class, Configs.Modid + "WirelessPeripheral");
			//GameRegistry.registerTileEntity(TileEntityCCProxy.class, Configs.Modid+"ccProxy");
			GameRegistry.registerTileEntity(TileEntityEnderMemory.class, Configs.Modid+"enderMemory");
			GameRegistry.registerTileEntity(TileEntityHolotapeWriter.class, Configs.Modid+"holotapeWriter");
			GameRegistry.registerTileEntity(TileEntityHolotapeReader.class, Configs.Modid+"holotapeReader");
			GameRegistry.registerTileEntity(TileEntityMagCardDevice.class, Configs.Modid+"MagCardDevice");
			GameRegistry.registerTileEntity(TileEntityMagCardReader.class, Configs.Modid+"MagCardReader");
			GameRegistry.registerTileEntity(TileEntityRedstonePort.class, Configs.Modid+"RsPort");
			//GameRegistry.registerTileEntity(TileEntityComputerRegulator.class, Configs.Modid+"ComputerRegulator");
			addRecipe(new ItemStack(enderMemory, 1), new Object[]{"BRB","RCR","BRB",'B',
					TMResource.BLUE_METAL.getOreDictName(Type.INGOT), 'R',Items.REDSTONE, 'C', CraftingMaterial.CHARGED_ENDER.getStack()});
			/*GameRegistry.addRecipe(new ItemStack(CCProxy, 1), new Object[]{"IBI","IRI","IBI",'I',Items.iron_ingot,
				'B',TMResource.BLUE_METAL.getStack(Type.INGOT),'R',Items.redstone});*/
			addRecipe(new ItemStack(WirelessPeripheral, 1), new Object[]{"IEI","ERE","IEI",'E',
					CraftingMaterial.CHARGED_ENDER.getStack(),'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'I', "ingotIron"});
			addRecipe(new ItemStack(holotape, 1), new Object[]{"IPI","IBI","RRR",'I',"ingotIron",'B',
					TMResource.BLUE_METAL.getOreDictName(Type.INGOT),'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'P',Items.PAPER});
			addRecipe(new ItemStack(holotapeWriter, 1), new Object[]{"III","BRB","IBI",'I',"ingotIron",'B',
					TMResource.BLUE_METAL.getOreDictName(Type.INGOT),'R',CraftingMaterial.CHARGED_REDSTONE.getStack()});
			addRecipe(new ItemStack(holotapeReader, 1), new Object[]{"III","IRI","IBI",'I',"ingotIron",'B',
					TMResource.BLUE_METAL.getOreDictName(Type.INGOT),'R',CraftingMaterial.CHARGED_REDSTONE.getStack()});
			addRecipe(new ItemStack(magCard, 1), new Object[]{"IR","PP",'I',"ingotIron",'P',
					Items.PAPER,'R',CraftingMaterial.CHARGED_REDSTONE.getStack()});
			addRecipe(new ItemStack(MagCardDevice, 1), new Object[]{"III","RIR","IBI",'I',"ingotIron",
					'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'B',TMResource.BLUE_METAL.getOreDictName(Type.INGOT)});
			addRecipe(new ItemStack(MagCardReader, 1), new Object[]{"RI","RI","BI",'I',"ingotIron",
					'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'B',new ItemStack(plate)});
			if(Config.enableAdventureItems){
				ControllerBox = new ControllerBox().setUnlocalizedName("ControllerBox").setCreativeTab(tabTomsModBlocks);
				TabletHouse = new Item().setUnlocalizedName("TabletHouse").setCreativeTab(tabTomsModItems)/*.setTextureName("minecraft:tablet/TabletOff")*/;
				trProcessor = new TrProcessor().setUnlocalizedName("trProcessor").setCreativeTab(tabTomsModItems).setMaxStackSize(1)/*.setTextureName("minecraft:chip")*/;
				linkedChipset = new LinkedChipset().setUnlocalizedName("linkedChipset").setCreativeTab(tabTomsModItems).setMaxStackSize(1)/*.setTextureName("minecraft:chip")*/;
				connectionModem = new ConnectionModem().setUnlocalizedName("connectionModem").setCreativeTab(tabTomsModItems).setMaxStackSize(1)/*.setTextureName("minecraft:tm/modem")*/;
				Camera = new Camera().setUnlocalizedName("Camera").setCreativeTab(tabTomsModBlocks);
				Jammer = new Jammer().setUnlocalizedName("Jammer").setCreativeTab(tabTomsModBlocks);
				AntennaController = new AntennaController().setUnlocalizedName("AntennaController").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:tm/TabContSideAct")*/;
				TabletCrafter = new TabletCrafter().setUnlocalizedName("TabletCrafter").setCreativeTab(tabTomsModBlocks);
				TabletAccessPoint = new TabletAccessPoint().setUnlocalizedName("tabletAccessPoint").setCreativeTab(tabTomsModBlocks);
				TabletController = new TabletController().setUnlocalizedName("tabletController").setCreativeTab(tabTomsModBlocks);
				registerItem(Tablet, Tablet.getUnlocalizedName().substring(5));
				registerItem(TabletHouse, TabletHouse.getUnlocalizedName().substring(5));
				registerItem(connectionBoxModem, connectionBoxModem.getUnlocalizedName().substring(5));
				registerItem(trProcessor, trProcessor.getUnlocalizedName().substring(5));
				registerItem(linkedChipset, linkedChipset.getUnlocalizedName().substring(5));
				registerItem(connectionModem, connectionModem.getUnlocalizedName().substring(5));
				registerItem(wrenchA, wrenchA.getUnlocalizedName().substring(5));
				registerItem(electricalMagCard, electricalMagCard.getUnlocalizedName().substring(5));
				registerBlock(TabletAccessPoint, TabletAccessPoint.getUnlocalizedName().substring(5));
				registerBlock(Antenna, Antenna.getUnlocalizedName().substring(5));
				registerBlock(TabletController, TabletController.getUnlocalizedName().substring(5));
				registerBlock(AntennaController, AntennaController.getUnlocalizedName().substring(5));
				registerBlock(ControllerBox, ControllerBox.getUnlocalizedName().substring(5));
				registerBlock(TabletCrafter, TabletCrafter.getUnlocalizedName().substring(5));
				registerBlock(Camera, Camera.getUnlocalizedName().substring(5));
				registerBlock(Jammer, Jammer.getUnlocalizedName().substring(5));
				GameRegistry.registerTileEntity(TileEntityTabletController.class, Configs.Modid + "TabletController");
				GameRegistry.registerTileEntity(TileEntityAntennaController.class, Configs.Modid + "AntennaCont");
				GameRegistry.registerTileEntity(TileEntityTabletAccessPoint.class, Configs.Modid + "TabletAccessPoint");
				GameRegistry.registerTileEntity(TileEntityAntenna.class, Configs.Modid + "Antenna");
				GameRegistry.registerTileEntity(TileEntityControllerBox.class, Configs.Modid + "ControllerBox");
				GameRegistry.registerTileEntity(TileEntityTabletCrafter.class, Configs.Modid + "TabletCrafter");
				GameRegistry.registerTileEntity(TileEntityCamera.class, Configs.Modid + "Camera");
				GameRegistry.registerTileEntity(TileEntityJammer.class, Configs.Modid + "Jammer");
				addRecipe(new ItemStack(TabletHouse, 1), new Object[]{"IDI","IGI","IBI",'I',
						"ingotIron",'D',new ItemStack(Items.DYE,1,0),'G',"paneGlassColorless",'B',Blocks.STONE_BUTTON});
				isAdventureItemsLoaded = true;
			}else{
				log.warn("Adventure items disabled");
			}
			if(Config.enableCommandExecutor){
				CommandExecutor = new CommandExecutor().setUnlocalizedName("CommandExecutor").setCreativeTab(tabTomsModBlocks)/*.setBlockTextureName("minecraft:command_block")*/.setBlockUnbreakable().setResistance(18000000F).setHardness(-1F);
				registerBlock(CommandExecutor, CommandExecutor.getUnlocalizedName().substring(5));
				GameRegistry.registerTileEntity(TileEntityCommandExecutor.class, Configs.Modid + "CommandExecutor");
			}
		}
		isMapEnabled = Config.enableMiniMap;
		if(isMapEnabled){
			log.info("Mini Map Enabled. Init minimap");
			addItemToGameRegistry(entityTracker, entityTracker.getUnlocalizedName().substring(5));
			addRecipe(new ItemStack(entityTracker, 1), new Object[]{"BGB","RPR","BSB",'G',TMResource.GREENIUM.getOreDictName(Type.INGOT),'B',
					new ItemStack(plate),'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'S',"blockPlatinum",'P', "paneGlassColorless"});
			log.info("Minimap loaded");
		}
		/*GameRegistry.registerTileEntity(TileEntityAntTop.class, Configs.Modid + "AntTop");
		GameRegistry.registerTileEntity(TileEntityAntMid.class, Configs.Modid + "AntMid");
		GameRegistry.registerTileEntity(TileEntityAntBase.class, Configs.Modid + "AntBase");*/
		/**Other*/
		//StartupCommon.preInitCommon();
		proxy.preInit();
		NetworkInit.init();
		GlobalFields.MBFrames.add(MultiblockPartList.AdvCasing);
		GlobalFields.MBFrames.add(MultiblockPartList.Casing);
		GlobalFields.MBFrames.add(MultiblockPartList.EnergyCellCasing);
		WorldGen.init();
		TMResource.loadFluids(false);
		GameRegistry.registerFuelHandler(FuelHandler.INSTANCE);
		//registerEntity(EntityCamera.class, Configs.Modid+":Camera",false);
		//FMLCommonHandler.instance().bus().register(new Config());
		proxy.registerKeyBindings();
		FMLInterModComms.sendMessage("Waila", "register", "com.tom.thirdparty.waila.Waila.onWailaCall");
		/*if(isMapEnabled){
		}*/
		Config.save();
		isPreInit = false;
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in "+time+" milliseconds");
	}
	@SideOnly(Side.CLIENT)
	public static void registerItemRenders(){
		log.info("Loading Renderers");
		long tM = System.currentTimeMillis();
		CustomModelLoader.clearExceptions();
		OBJLoader.INSTANCE.addDomain("tmobj");
		for(Item item : itemList){
			registerRender(item);
		}
		for(IIconRegisterRequired reg : customIconRegisterRequired){
			reg.registerIcons();
		}
		log.info("Loading Material Renderers");
		for(Type t : Type.VALUES){
			for(TMResource r : TMResource.VALUES){
				if(r.isValid(t))registerRender(t.getItem(), r.ordinal(),"tomsmodcore:resources/"+t.getName()+"_"+r.getName());
			}
		}
		for(CraftingMaterial t : CraftingMaterial.VALUES){
			registerRender(craftingMaterial, t.ordinal(),"tomsmodcore:resources/crafting/"+t.getName());
		}
		EnergyInit.registerRenders();
		if(isMapEnabled){
			if(Config.advEntityTrackerTexture){
				registerRender(entityTracker, 0, "tomsmodcore:radarOff");
				registerRender(entityTracker, 1, "tomsmodcore:radarActive");
				registerRender(entityTracker, 2, "tomsmodcore:radarJammed");
			}else{
				registerRender(entityTracker, 0, "tomsmodcore:radarLowOff");
				registerRender(entityTracker, 1, "tomsmodcore:radarLowActive");
				registerRender(entityTracker, 2, "tomsmodcore:radarLowJammed");
			}
		}
		TransportInit.registerRenders();
		DefenseInit.registerRenders();
		StorageInit.registerRenders();
		List<ItemStack> stackList = new ArrayList<ItemStack>();
		TMResource.addHammersToList(stackList, hammer);
		TMResource.addCuttersToList(stackList, wireCutters);
		for(ItemStack s : stackList)registerRender(s, "tomsmodcore:resources/"+s.getUnlocalizedName().substring(5));
		FactoryInit.registerRenders();
		//registerRender(Item.getItemFromBlock(blockTreetap), 0, "tomsmodcore:" + treeTap.getUnlocalizedName().substring(5));
		processRenderers();
		long time = System.currentTimeMillis() - tM;
		log.info("Loaded Renderers in "+time+" milliseconds");
	}
	@SideOnly(Side.CLIENT)
	private static void processRenderers() {
		log.info("Start processing " + modelList.size() + " models");
		ProgressBar bar = ProgressManager.push("Loading Renderers", modelList.size());
		for(Entry<Item, Entry<ModelResourceLocation, Integer>> e : modelList){
			addRenderToRegistry(e.getKey(), e.getValue().getValue(), e.getValue().getKey(), bar);
		}
		log.info("Loaded " + modelList.size() + " models.");
		ProgressManager.pop(bar);
	}

	@EventHandler
	public static void load(FMLInitializationEvent event){
		log.info("Start Initialization");
		long tM = System.currentTimeMillis();
		isInit = true;
		proxy.init();
		log.info("Registering world generator...");
		GameRegistry.registerWorldGenerator(WorldGen.instance, 1);
		initializeFluidBlocksAndBuckets();
		OreDict.init();
		plasma = FluidRegistry.getFluid("tomsmodplasma");
		//ePlasma = FluidRegistry.getFluid("tomsmodeplasma");
		Deuterium = FluidRegistry.getFluid("tomsmoddeuterium");
		Tritium = FluidRegistry.getFluid("tomsmodtritium");
		//hCoolant = FluidRegistry.getFluid("tomsmodcoolanthot");
		//coolant = FluidRegistry.getFluid("tomsmodcoolant");
		//System.out.println(FluidRegistry.getFluid("hydrogen"));
		//Controllers.init();
		/*if(!(FluidRegistry.isFluidRegistered("steam"))){

		}*/
		Hydrogen = FluidRegistry.getFluid("hydrogen");
		steam = FluidRegistry.getFluid("steam");
		fusionFuel = FluidRegistry.getFluid("tomsmodfusionfuel");
		nuclearWaste = FluidRegistry.getFluid("tmnuclearwaste");
		oil = FluidRegistry.getFluid("oil");

		sulfurDioxide = FluidRegistry.getFluid("tmsulfurdioxide");
		sulfuricAcid = FluidRegistry.getFluid("tmsulfuricacid");
		sulfurTrioxide = FluidRegistry.getFluid("tmsulfurtrioxide");
		hydrogenChlorine = FluidRegistry.getFluid("hydrogenchlorine");
		chlorine = FluidRegistry.getFluid("chlorine");
		creosoteOil = FluidRegistry.getFluid("creosote");
		Oxygen = FluidRegistry.getFluid("oxygen");
		/*if(!(FluidRegistry.isFluidRegistered("steam"))){

		}*/
		TMResource.loadFluids(true);
		if(Config.enableGrassDrops){
			log.info("Adding Grass Drops...");
			MinecraftForge.addGrassSeed(new ItemStack(Items.FLINT), 15);
			MinecraftForge.addGrassSeed(new ItemStack(Items.STICK), 15);
		}
		EnergyType.init();
		if(isCCLoaded)initComputerCraft();
		/*if(isPneumaticCraftLoaded){
			registerBlock(MultiblockPressurePort, MultiblockPressurePort.getUnlocalizedName().substring(5));
			registerBlock(MultiblockHeatPort, MultiblockHeatPort.getUnlocalizedName().substring(5));
			GameRegistry.registerTileEntity(TileEntityMBPressurePort.class, Configs.Modid+"mbPressurePort");
			GameRegistry.registerTileEntity(TileEntityMBHeatPort.class, Configs.Modid+"mbHeatPort");
			GameRegistry.addRecipe(new ItemStack(MultiblockCompressor, 1), new Object[]{"MCM","IBI","MRM",'R',chargedRedstone,'M',MultiblockCase,'B',Blocks.iron_block,'I',Items.iron_ingot,'C',plateChrome});
		}*/
		log.info("Loading Multiparts");
		ProgressBar bar = ProgressManager.push("Loading Multiparts", multipartList.size());
		for(Entry<Entry<String, String>,Class<? extends IMultipart>> e : multipartList){
			String info = "Loading: "+e.getKey().getValue()+":"+e.getKey().getKey();
			log.info(info);
			bar.step(info);
			MultipartRegistry.registerPart(e.getValue(), e.getKey().getValue()+":"+e.getKey().getKey());
			ignoredLocations.add(e.getKey().getKey());
		}
		ProgressManager.pop(bar);
		/*log.info("Registering Transparent Blocks");
		GlobalFields.glassBlocks.put(Blocks.GLASS,0.5F);
		GlobalFields.glassBlocks.put(Blocks.STAINED_GLASS,0.4F);
		GlobalFields.glassBlocks.put(Blocks.GLASS_PANE,0.5F);
		GlobalFields.glassBlocks.put(Blocks.STAINED_GLASS_PANE,0.4F);
		GlobalFields.glassBlocks.put(Blocks.TRIPWIRE_HOOK,0.9F);
		GlobalFields.glassBlocks.put(Blocks.OAK_FENCE_GATE,0.1F);
		GlobalFields.glassBlocks.put(Blocks.BIRCH_FENCE_GATE,0.1F);
		GlobalFields.glassBlocks.put(Blocks.SPRUCE_FENCE_GATE,0.1F);
		GlobalFields.glassBlocks.put(Blocks.JUNGLE_FENCE_GATE,0.1F);
		GlobalFields.glassBlocks.put(Blocks.DARK_OAK_FENCE_GATE,0.1F);
		GlobalFields.glassBlocks.put(Blocks.ACACIA_FENCE_GATE,0.1F);
		GlobalFields.glassBlocks.put(Blocks.OAK_FENCE,0.1F);
		GlobalFields.glassBlocks.put(Blocks.BIRCH_FENCE,0.1F);
		GlobalFields.glassBlocks.put(Blocks.SPRUCE_FENCE,0.1F);
		GlobalFields.glassBlocks.put(Blocks.JUNGLE_FENCE,0.1F);
		GlobalFields.glassBlocks.put(Blocks.DARK_OAK_FENCE,0.1F);
		GlobalFields.glassBlocks.put(Blocks.ACACIA_FENCE_GATE,0.1F);
		GlobalFields.glassBlocks.put(Blocks.LADDER,0.7F);
		GlobalFields.glassBlocks.put(Blocks.LEVER,0.9F);
		GlobalFields.glassBlocks.put(Blocks.STANDING_SIGN,0.9F);
		GlobalFields.glassBlocks.put(Blocks.STONE_BUTTON,0.9F);
		GlobalFields.glassBlocks.put(Blocks.WOODEN_BUTTON,0.9F);
		GlobalFields.glassBlocks.put(Blocks.TORCH,1F);
		GlobalFields.glassBlocks.put(Blocks.TALLGRASS,0.9F);
		GlobalFields.glassBlocks.put(Blocks.WALL_SIGN,0.9F);
		GlobalFields.glassBlocks.put(Blocks.WEB,0.3F);
		GlobalFields.glassBlocks.put(Blocks.LEAVES,0.01F);
		GlobalFields.glassBlocks.put(Blocks.LEAVES2,0.01F);
		GlobalFields.glassBlocks.put(Blocks.VINE,0.9F);
		GlobalFields.glassBlocks.put(Blocks.GLOWSTONE,0.8F);
		GlobalFields.glassBlocks.put(Blocks.LIT_REDSTONE_LAMP,0.5F);
		GlobalFields.glassBlocks.put(Blocks.LIT_FURNACE,0.1F);
		GlobalFields.glassBlocks.put(Blocks.LIT_PUMPKIN,0.2F);
		GlobalFields.glassBlocks.put(Blocks.LIT_REDSTONE_ORE,0.09F);
		GlobalFields.glassBlocks.put(Blocks.REDSTONE_TORCH,0.91F);
		GlobalFields.glassBlocks.put(Blocks.ICE,0.05F);
		GlobalFields.glassBlocks.put(Blocks.UNLIT_REDSTONE_TORCH,0.9F);
		GlobalFields.glassBlocks.put(Blocks.LAVA,0.9F);
		GlobalFields.glassBlocks.put(Blocks.FLOWING_LAVA,0.9F);
		GlobalFields.glassBlocks.put(Blocks.WATER,0.01F);
		GlobalFields.glassBlocks.put(Blocks.FLOWING_WATER,0.01F);
		GlobalFields.glassBlocks.put(Blocks.AIR,1F);*/
		DefenseInit.registerPlaceables();
		//MaterialOverride.override();
		MinecraftForge.EVENT_BUS.register(com.tom.handler.EventHandler.instance);
		log.info("Loading Vanilla Material Blocks");
		TMResource.DIAMOND.storageBlock = new EmptyEntry<Block, Integer>(Blocks.DIAMOND_BLOCK, 0);
		TMResource.IRON.storageBlock = new EmptyEntry<Block, Integer>(Blocks.IRON_BLOCK, 0);
		TMResource.GOLD.storageBlock = new EmptyEntry<Block, Integer>(Blocks.GOLD_BLOCK, 0);
		TMResource.COAL.storageBlock = new EmptyEntry<Block, Integer>(Blocks.COAL_BLOCK, 0);
		TMResource.GLOWSTONE.storageBlock = new EmptyEntry<Block, Integer>(Blocks.GLOWSTONE, 0);
		TMResource.LAPIS.storageBlock = new EmptyEntry<Block, Integer>(Blocks.LAPIS_BLOCK, 0);
		TMResource.NETHER_QUARTZ.storageBlock = new EmptyEntry<Block, Integer>(Blocks.QUARTZ_BLOCK, 0);
		TMResource.OBSIDIAN.storageBlock = new EmptyEntry<Block, Integer>(Blocks.OBSIDIAN, 0);
		TMResource.REDSTONE.storageBlock = new EmptyEntry<Block, Integer>(Blocks.REDSTONE_BLOCK, 0);
		log.info("Loading Recipes");
		CraftingRecipes.init();
		FurnaceRecipes.init();
		ElectrolyzerRecipes.init();
		MultiblockCrafterRecipes.init();
		CentifugeRecipes.init();
		ResearchLoader.init();
		AdvancedCraftingRecipes.init();
		TMResource.loadRecipes();
		MachineCraftingHandler.loadRecipes();
		proxy.registerRenders();
		AchievementHandler.init();
		//Type.DUST.setItem(cable);
		//loadThaumcraft();
		//System.out.println(plasma);
		Config.printWarnings();
		isInit = false;
		long time = System.currentTimeMillis() - tM;
		log.info("Initialization took in "+time+" milliseconds");
	}

	@EventHandler
	public static void PostLoad(FMLPostInitializationEvent PostEvent){
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
		ModContainer mc = Loader.instance().activeModContainer();
		versionCheckResult = ForgeVersion.getResult(mc);
		if(versionCheckResult.status == Status.OUTDATED){
			TMLogger.warn("[VersionChecker]: ****************************************");
			TMLogger.warn("[VersionChecker]: * Tom's Mod is OUTDATED!!");
			TMLogger.warn("[VersionChecker]: * Current version: " + Configs.version);
			TMLogger.warn("[VersionChecker]: * Online version: " + versionCheckResult.target.toString());
			TMLogger.warn("[VersionChecker]: ****************************************");
		}else if(versionCheckResult.status == Status.AHEAD){
			TMLogger.warn("[VersionChecker]: ??? status == AHEAD ???");
			TMLogger.warn("[VersionChecker]: ?? Current version: " + Configs.version);
		}else if(versionCheckResult.status == Status.PENDING || versionCheckResult.status == Status.FAILED){
			TMLogger.warn("[VersionChecker]: Tom's Mod version checking failed.");
		}else{
			TMLogger.info("[VersionChecker]: Tom's Mod is up to date");
		}
		/*if(isMapEnabled){
		}*/
		//TMLogger.bigCatching(new RuntimeException(), "Test Exception");
		Config.printWarnings();
		long time = System.currentTimeMillis() - tM;
		log.info("Post Initialization took in "+time+" milliseconds");
	}
	@EventHandler
	public static void onServerStart(FMLServerStartingEvent event) {
		log.info("Server Start");
		proxy.serverStart();
		TomsModUtils.setServer(event.getServer());
		if(isMapEnabled){
			event.registerServerCommand(new CommandWaypoint(false));
			event.registerServerCommand(new CommandWaypoint(true));
			Minimap.init(new File(TomsModUtils.getSavedFile(),Reference.worldDirConfigName));
		}
		GlobalFields.EnderMemoryObj = new Object[][][][]{{TomsModUtils.fillObjectSimple(65536)},TomsModUtils.fillObject(65536)};
		//ResearchHandler.load(event.getServer());
		if(isCCLoaded)GlobalFields.EnderMemoryIComputerAccess.clear();
		WorldHandler.onServerStart(new File(TomsModUtils.getSavedFile(),"chunkData"));
		PlayerHandler.onServerStart(new File(TomsModUtils.getSavedFile(),"playerData"));
		Config.printWarnings();
		/*Side side = event.getSide();
		if(side == Side.SERVER){
			log.info("Loading Server Handler");
			MinecraftServer s = event.getServer();
			if(isMapEnabled){
				log.info("Loading Minimap Server");
				String f = s.getFile("a").getAbsolutePath();
				String path = f.substring(0, f.length()-1);
				String mainFolderPath = path + "tmMap" + File.separator;
				File folder = new File(mainFolderPath);
				folder.mkdirs();
				mapFolder = folder;
				log.info(mainFolderPath);
			}
			isSingle = false;
			log.info("Loading Completed");
		}else if(side == Side.CLIENT){
			log.info("Loading Handlers");
			if(isMapEnabled){
				log.info("Loading Minimap");
				//Minecraft mc = Minecraft.getMinecraft();
				MinecraftServer s = event.getServer();
				if(s != null){
					String f = s.getFile("a").getAbsolutePath();
					String path = f.substring(0, f.length()-1);
					boolean isSinglePlayer = s.isSinglePlayer();
					if(isSinglePlayer){
						String mainFolderPath = path + "saves"+File.separator+s.getFolderName()+File.separator+"tmMap" + File.separator;
						File folder = new File(mainFolderPath);
						folder.mkdirs();
						mapFolder = folder;
						//log.info(mainFolderPath);
					}
				}
				/*if(mc != null){
					String f = mc.mcDataDir.getAbsolutePath()+File.separator+"tmMap"+File.separator;
					File folder = new File(f);
					folder.mkdirs();
					log.info(f);
					if(!mc.isSingleplayer()){
						ServerData serverData = mc.func_147104_D();
						File m = new File(folder,"main.tmmap");
						if(!m.exists()){
							try {
								m.createNewFile();
							} catch (IOException e) {
								log.error("IOException occurred while creating the minimap main file");
								e.printStackTrace();
							}
						}
						try {
							BufferedReader fileRead = new BufferedReader(new FileReader(m));
							PrintWriter file = new PrintWriter(m);
							int i = 0;
							boolean found = false;
							String si = "0";
							for(String line=fileRead.readLine(); line !=null ; line=fileRead.readLine()){
								found = line.contains("#{"+serverData.serverIP+"&");
								if(found){
									String l1 = line.substring(3 + serverData.serverIP.length());
									String l2 = l1.substring(0, l1.length()-2);
									si = l2;
									break;
								}
								i = i + 1;
							}
							if(!found) si = ""+i;
							File dir = new File(folder,"data"+si+File.separator);
							if(!found){
								file.println("#{"+serverData.serverIP+"&"+si+"};");
								dir.mkdirs();
							}
							file.close();
							fileRead.close();
						} catch (FileNotFoundException e) {
							log.error("FileNotFoundException occurred while writing the minimap main file");
							e.printStackTrace();
						} catch (IOException e) {
							log.error("IOException occurred while writing the minimap main file");
							e.printStackTrace();
						}
					}
				}
				//boolean isSinglePlayer = mc.isSingleplayer();
				//if(!isSinglePlayer){
					/*String f = s.getFile("a").getAbsolutePath();
					String path = f.substring(0, f.length()-1);
					String mainFolderPath = path + (isSinglePlayer ? "saves"+File.separator+s.getFolderName()+File.separator+"tmMap" : "tmMap") + File.separator;
					File folder = new File(mainFolderPath);
					folder.mkdirs();*/
		//log.info("Server Mode");
		//log.info("Dir"+mc.mcDataDir.getAbsolutePath());
		//}
		//isSingle = true;
		//}
		//}
		log.info("Loading Completed");
	}
	///*
	@EventHandler
	public static void onServerStop(FMLServerStoppingEvent event) {
		log.info("Stopping the Server");
		PlayerHandler.cleanup();
		if(isMapEnabled)Minimap.close();
		WorldHandler.stopServer();
		TomsModUtils.setServer(null);
		log.info("Server Stopped");
	}
	private static void initializeFluidBlocksAndBuckets(){
		log.info("Loading Fluids...");
		for(Fluid fluid : fluids) {
			if(!FluidRegistry.isFluidRegistered(fluid.getName())){
				FluidRegistry.registerFluid(fluid);
				if(fluid.getName().equals("oil") && fluid.getBlock() == null){
					Block blockOil = new BlockOil();
					//fluid.setBlock(oilBlock);
					registerBlock(blockOil, blockOil.getUnlocalizedName().substring(5));
				}
			}
			fluid = FluidRegistry.getFluid(fluid.getName());
			//Block fluidBlock = fluid.getBlock();
			log.info("Loading Fluid: " + fluid.getUnlocalizedName());
			//System.out.println(fluid.getIcon());
			fluidList.put(fluid.getName(), fluid);
			ignoredLocations.add(fluid.getName());
			/*if(fluidBlock == null) {
				fluidBlock = new BlockFluidTomsMod(fluid);
				registerBlock(fluidBlock);
				fluid.setBlock(fluidBlock);
			}
			fluidToBlockMap.put(fluid, fluidBlock);
			//fluid.setIcons(fluidIcons.get(fluidBlock)[1], fluidIcons.get(fluidBlock)[0]);
			//Item fluidBucket = new ItemFluidContainer(0, 1000).setContainerItem(Items.bucket).setCreativeTab(tabTomsModBuckets).setUnlocalizedName(fluid.getName() + "Bucket");
			//registerItem(fluidBucket);
			//fluidBlockToBucketMap.put(fluidBlock, fluidBucket);
			//registerBlock(fluidBlock, fluidBlock.getUnlocalizedName().substring(5));
			//fluidToBlockMap.put(fluid, fluidBlock);
			/*final Fluid fluidF = fluid;
			Item fluidBucket = new ItemBucket(fluidBlock){
				@Override
				@SideOnly(Side.CLIENT)
				public void getSubItems(Item item, CreativeTabs creativeTab, List<ItemStack> items){
					if(FluidRegistry.isFluidDefault(fluidF)){
						super.getSubItems(item, creativeTab, items);
					}
				}
			}.setContainerItem(Items.bucket).setCreativeTab(tabTomsModBuckets).setUnlocalizedName(fluid.getName() + "Bucket");

			addItemToGameRegistry(fluidBucket, fluidBucket.getUnlocalizedName().substring(5));
			proxy.registerItemRender(fluidBucket, 0, fluidBucket.getUnlocalizedName().substring(5));

			fluidBlockToBucketMap.put(fluidBlock, fluidBucket);*/
			FluidRegistry.addBucketForFluid(fluid);

			//net.minecraftforge.fluids.FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(fluidBucket), new ItemStack(Items.bucket));
		}
	}
	public static void registerBlock(Block block) {
		registerBlock(block, block.getUnlocalizedName().substring(5));
	}
	public static class BlockFluidTomsMod extends BlockFluidClassic{

		public BlockFluidTomsMod(Fluid fluid, Material material){
			super(fluid, material);
			setUnlocalizedName(fluid.getName());
		}

		public BlockFluidTomsMod(Fluid fluid){
			this(fluid, Material.WATER);
		}
	}
	//*/
	/*@SuppressWarnings("unused")
	private static void registerFluid(Fluid fluid){
		FluidRegistry.registerFluid(fluid);
        fluid = FluidRegistry.getFluid(fluid.getName());
        /*Block fluidBlock = fluid.getBlock();
        if(fluidBlock == null) {
            fluidBlock = getBlockForFluid(fluid);
            registerBlock(fluidBlock);
            fluid.setBlock(fluidBlock);
        }
        fluidToBlockMap.put(fluid, fluidBlock);

        Item fluidBucket = new ItemBucket(fluidBlock){
            @SuppressWarnings("rawtypes")
			@Override
            public void addInformation(ItemStack p_77624_1_, net.minecraft.entity.player.EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_){
                super.addInformation(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);

            };
        }.setContainerItem(Items.bucket).setCreativeTab(tabTomsModBuckets).setTextureName("minecraft:" + fluid.getName() + "Bucket").setUnlocalizedName(fluid.getName() + "Bucket");

        registerItem(fluidBucket);

        fluidBlockToBucketMap.put(fluidBlock, fluidBucket);

        FluidContainerRegistry.registerFluidContainer(new FluidStack(fluid, 1000), new ItemStack(fluidBucket), new ItemStack(Items.bucket));
	}
	/*private static Block getBlockForFluid(final Fluid fluid){
        return new BlockFluidClassic(fluid, Material.water){
            private IIcon flowingIcon, stillIcon;

            @Override
            public void registerBlockIcons(IIconRegister register){
                flowingIcon = register.registerIcon("minecraft:" + fluid.getName() + "_flow");
                stillIcon = register.registerIcon("minecraft:" + fluid.getName() + "_still");
                //fluidIcons.put(fluid, new IIcon[]{flowingIcon, stillIcon});
                fluid.setIcons(stillIcon, flowingIcon);
            }

           @Override
           @SideOnly(Side.CLIENT)
            public IIcon getIcon(int side, int meta){
                return side != 0 && side != 1 ? flowingIcon : stillIcon;
            }
        }.setBlockName(fluid.getName());
    }*/
	public static void registerBlock(Block block, String name){
		//log.info("Registering block: "+name);
		addBlockToGameRegistry(block, name);
		Item item = Item.getItemFromBlock(block);
		if(block instanceof IIconRegisterRequired){
			customIconRegisterRequired.add((IIconRegisterRequired) block);
		}else{
			blockList.add(block);
			if(item instanceof IIconRegisterRequired){
				customIconRegisterRequired.add((IIconRegisterRequired) item);
			}else itemList.add(item);
		}
	}
	public static void registerBlock(Block block, ItemBlock itemBlock){
		addBlockToGameRegistry(block, block.getUnlocalizedName().substring(5), itemBlock);
		if(block instanceof IIconRegisterRequired){
			customIconRegisterRequired.add((IIconRegisterRequired) block);
		}else{
			blockList.add(block);
			if(itemBlock instanceof IIconRegisterRequired){
				customIconRegisterRequired.add((IIconRegisterRequired) itemBlock);
			}else itemList.add(itemBlock);
		}
	}

	public static void registerItem(Item item){
		registerItem(item, item.getUnlocalizedName().substring(5));
	}

	public static void registerItem(Item item, String registerName){
		//log.info("Registering item: "+registerName);
		if(item instanceof IIconRegisterRequired){
			customIconRegisterRequired.add((IIconRegisterRequired) item);
		}else itemList.add(item);
		addItemToGameRegistry(item, registerName);
	}//*/
	public static void addItemToGameRegistry(Item item, String name){
		addItemToGameRegistry(item, name, true);
	}
	public static void addBlockToGameRegistry(Block block, String name){
		if(block instanceof ICustomItemBlock)
			addBlockToGameRegistry(block, name, ((ICustomItemBlock) block).createItemBlock());
		else
			addBlockToGameRegistry(block, name, new ItemBlock(block));
	}
	public static void addBlockToGameRegistry(Block block, String name, ItemBlock itemBlock){
		addOnlyBlockToGameRegisty(block, name, false);
		addItemToGameRegistry(itemBlock, name, false);
		if(block instanceof IRegisterRequired)
			((IRegisterRequired)block).register();
		if(itemBlock instanceof IRegisterRequired)
			((IRegisterRequired)itemBlock).register();
	}
	public static void addOnlyBlockToGameRegisty(Block block, String name){
		addOnlyBlockToGameRegisty(block, name, true);
	}
	private static void addOnlyBlockToGameRegisty(Block block, String name, boolean callRegister){
		GameRegistry.register(block.getRegistryName() == null ? block.setRegistryName(name) : block);
		if(callRegister && block instanceof IRegisterRequired)
			((IRegisterRequired)block).register();
	}
	private static void addItemToGameRegistry(Item item, String name, boolean callRegister){
		GameRegistry.register(item.getRegistryName() == null ? item.setRegistryName(name) : item);
		if(callRegister && item instanceof IRegisterRequired)
			((IRegisterRequired)item).register();
	}
	/*public static CreativeTabs tabTomsModBuckets = new CreativeTabs("tabTomsModBuckets"){

		@Override
		public Item getTabIconItem() {
			return new ItemStack(Items.bucket).getItem();
		}

	};*/
	public static CreativeTabs tabTomsModBlocks = new CreativeTabs("tabTomsModBlocks"){

		@Override
		public Item getTabIconItem() {
			return new ItemStack(ItemProxy).getItem();
		}

	};
	public static CreativeTabs tabTomsModItems = new CreativeTabs("tabTomsModItems"){

		@Override
		public Item getTabIconItem() {
			return new ItemStack(magnifyingGlass).getItem();
		}

	};
	public static CreativeTabs tabTomsModMaterials = new CreativeTabs("tabTomsModMaterials"){

		@Override
		public Item getTabIconItem() {return Items.APPLE;}
		@Override
		public ItemStack getIconItemStack(){
			return TMResource.BLUE_METAL.getStackNormal(Type.INGOT);
		}


	};
	/*private static void loadThaumcraft(){

	}*/
	/*public static Item getBucket(Fluid fluid){
		return fluidBlockToBucketMap.get(fluidToBlockMap.get(fluid));
	}*/
	public static void registerFluid(Fluid fluid, String fluidName) {

		if (!FluidRegistry.isFluidRegistered(fluidName)) {
			FluidRegistry.registerFluid(fluid);
		}
		fluid = FluidRegistry.getFluid(fluidName);
		fluidList.put(fluidName, fluid);
	}
	public static Fluid getFluid(String name){
		return fluidList.get(name);
	}
	@Optional.Method(modid = Configs.COMPUTERCRAFT)
	private static void initComputerCraft(){
		log.info("Init ComputerCraft Handler");
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) enderMemory);
		//ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) CCProxy);
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) GPU);
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) Monitor);
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) WirelessPeripheral);
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) EnderPlayerSensor);
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) holotapeReader);
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) holotapeWriter);
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) RedstonePort);
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) DefenseInit.forceCapacitor);
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) EnergyInit.MK1Storage);
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) EnergyInit.MK2Storage);
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) EnergyInit.MK3Storage);
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) EnergyInit.batteryBox);
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) MagCardDevice);
		//ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) ComputerRegulator);
		//ComputerRegulatorClass.init();
		if(Config.enableAdventureItems){
			ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) TabletController);
			ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) AntennaController);
		}
		if(Config.enableCommandExecutor) ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) CommandExecutor);
		ComputerCraftAPI.registerPeripheralProvider(MultipartProvider.INSTANCE);
		log.info("ComputerCraft Handler Loaded");
	}
	@EventHandler
	public static void onIMCMessages(IMCEvent event) {
		log.info("Receiving IMC");
		for(IMCMessage message : event.getMessages()) {
			try{
				IMCHandler.receive(message);
			}catch(Exception e){
				log.error("CRITICAL EXCEPTION occurred while handling IMC. Ignoring the current IMC message!");
				log.error(e.toString());
				e.printStackTrace();
				log.error("Message sent by: " + message.getSender());
			}
		}
	}
	/*@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void registerEntity(Class entityClass, String name, boolean hasAI)
	{
		int entityID = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(entityClass, name, entityID);
		EntityRegistry.registerModEntity(entityClass, name, entityID, modInstance, 64, 1, hasAI);
	}*/
	@SideOnly(Side.CLIENT)
	public static void registerRender(Item item){
		registerRender(item, 0);
	}
	@SideOnly(Side.CLIENT)
	public static void registerRender(Item item, int meta){
		//ModelLoader.setCustomModelResourceLocation(item, meta, getItemResourceLocation(item));
		String type = getNameForItem(item).replace("|", "");
		//Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, getItemResourceLocation(item));
		//ModelBakery.addVariantName(item, type);
		registerRender(item, meta, type);
	}
	@SideOnly(Side.CLIENT)
	public static void registerRender(Item item, int meta, String name){
		modelList.add(new EmptyEntry<Item, Entry<ModelResourceLocation, Integer>>(item, new EmptyEntry<ModelResourceLocation, Integer>(new ModelResourceLocation(name,"inventory"), meta)));
		//ModelBakery.addVariantName(item, name);
	}
	@SideOnly(Side.CLIENT)
	public static void registerRender(ItemStack stack, String name){
		registerRender(stack.getItem(), stack.getMetadata(), name);
	}
	@SideOnly(Side.CLIENT)
	private static void addRenderToRegistry(Item item, int meta, ModelResourceLocation loc, ProgressBar bar){
		String toString = loc.toString()+":"+meta;
		log.info("Loading: "+toString);
		bar.step(toString);
		ModelLoader.setCustomModelResourceLocation(item, meta, loc);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, loc);
		//ignoredLocations.add(loc.getResourceDomain()+":"+loc.getResourcePath());
	}
	public static String getNameForItem(Item item) {
		Object obj = Item.REGISTRY.getNameForObject(item);
		if (obj == null) {
			return null;
		}
		return obj.toString();
	}

	public static String getNameForBlock(Block block) {
		Object obj = Block.REGISTRY.getNameForObject(block);
		if (obj == null) {
			return null;
		}
		return obj.toString();
	}
	@SideOnly(Side.CLIENT)
	public static ModelResourceLocation getItemResourceLocation(Item item) {
		String type = getNameForItem(item).replace("|", "");
		//if(item == wrenchA) type = type+"A";
		//type = type.toLowerCase(Locale.ROOT);
		//log.info(type);
		return new ModelResourceLocation(type, "inventory");
	}
	@SideOnly(Side.CLIENT)
	public static ModelResourceLocation getBlockResourceLocation(Block block) {
		String type = getNameForBlock(block).replace("|", "");
		//type = type.toLowerCase(Locale.ROOT);
		//log.info(type);
		return new ModelResourceLocation(type);
	}
	public static void registerMultipart(MultipartItem item, Class<? extends IMultipart> c, String name, String modid){
		log.info("Adding Multipart to Registry. Multipart: "+name);
		registerItem(item, name);
		multipartList.add(new EmptyEntry<Entry<String, String>, Class<? extends IMultipart>>(new EmptyEntry<String, String>(name, modid), c));
	}
	public static void registerMultipart(MultipartItem item, Class<? extends IMultipart> c, String modid){
		registerMultipart(item, c, item.getUnlocalizedName().substring(5), modid);
	}
	/*
	 *
	 *         TMResource
	 *
	 */
	public static boolean isInit(){
		return isPreInit || isInit;
	}
	public static CreativeTabs tabTomsModWeaponsAndTools = new CreativeTabs("tabTomsModWeaponsAndTools"){

		@Override
		public Item getTabIconItem() {return CoreInit.wrench;}
	};
	public static boolean isWrench(ItemStack stack, EntityPlayer player){
		return stack != null && stack.getItem() != null && stack.getItem() instanceof IWrench && ((IWrench)stack.getItem()).isWrench(stack, player);
	}
	public static CheckResult getVersionCheckResult() {
		return versionCheckResult;
	}
	/*public static class ItemBase extends Item{
		public Item setTextureName(String t){
			proxy.registerItemRender(this, t);
			return this;
		}
	}*/
	/*private static void initFluids(){
		FluidList.put(Deuterium, 0xFFFF00);
		FluidList.put(Tritium, 0xFF0000);
		FluidList.put(coolant, 0x00BFFF);
		FluidList.put(ePlasma, 0x9ACD32);
		FluidList.put(fusionFuel, 0xBA55D3);
		FluidList.put(hCoolant, 0xFF0000);
		FluidList.put(plasma, 0xFFD700);
		FluidList.put(steam, 0x696969);
	}
	public static Map<String,Integer> getFluidMap(){
		return FluidList.map;
	}//*/

	/**
     Minecraft.getMinecraft().getBlockRenderDispatcher().renderBlockBrightness(IBlockState, brightness)
	 */
}
