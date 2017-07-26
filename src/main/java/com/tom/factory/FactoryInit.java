package com.tom.factory;

import static com.tom.core.CoreInit.registerBlock;
import static com.tom.core.CoreInit.registerItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.tom.api.block.BlockMultiblockCasing;
import com.tom.core.CoreInit;
import com.tom.core.IMod;
import com.tom.factory.block.AdvBoiler;
import com.tom.factory.block.AdvancedFluidBoiler;
import com.tom.factory.block.AlloySmelter;
import com.tom.factory.block.BasicBoiler;
import com.tom.factory.block.BlockAdvBlastFurnace;
import com.tom.factory.block.BlockBlastFurnace;
import com.tom.factory.block.BlockCokeOven;
import com.tom.factory.block.BlockComponents;
import com.tom.factory.block.BlockCrusher;
import com.tom.factory.block.BlockGeoBoiler;
import com.tom.factory.block.BlockMixer;
import com.tom.factory.block.BlockPump;
import com.tom.factory.block.BlockRefinery;
import com.tom.factory.block.BlockSteelBoiler;
import com.tom.factory.block.BlockWaterCollector;
import com.tom.factory.block.BlockWireMill;
import com.tom.factory.block.Centrifuge;
import com.tom.factory.block.CoilerPlant;
import com.tom.factory.block.ElectricFurnace;
import com.tom.factory.block.ElectricFurnaceAdv;
import com.tom.factory.block.ElectricalRubberProcessor;
import com.tom.factory.block.Electrolyzer;
import com.tom.factory.block.FluidBoiler;
import com.tom.factory.block.FluidTransposer;
import com.tom.factory.block.FusionPreHeater;
import com.tom.factory.block.IndustrialBlastFurnace;
import com.tom.factory.block.LaserEngraver;
import com.tom.factory.block.MultiblockEnergyPort;
import com.tom.factory.block.MultiblockFluidHatch;
import com.tom.factory.block.MultiblockFuelRod;
import com.tom.factory.block.MultiblockHatch;
import com.tom.factory.block.PlasticProcessor;
import com.tom.factory.block.PlateBlendingMachine;
import com.tom.factory.block.RubberBoiler;
import com.tom.factory.block.RubberProcessor;
import com.tom.factory.block.SolderingStation;
import com.tom.factory.block.SteamAlloySmelter;
import com.tom.factory.block.SteamCrusher;
import com.tom.factory.block.SteamFurnace;
import com.tom.factory.block.SteamFurnaceAdv;
import com.tom.factory.block.SteamMixer;
import com.tom.factory.block.SteamPlateBlender;
import com.tom.factory.block.SteamSolderingStation;
import com.tom.factory.block.UVLightbox;
import com.tom.factory.item.ExtruderModule;
import com.tom.factory.item.ItemCoalCoke;
import com.tom.factory.tileentity.TileEntityAdvBlastFurnace;
import com.tom.factory.tileentity.TileEntityAdvBoiler;
import com.tom.factory.tileentity.TileEntityAdvFluidBoiler;
import com.tom.factory.tileentity.TileEntityAlloySmelter;
import com.tom.factory.tileentity.TileEntityBasicBoiler;
import com.tom.factory.tileentity.TileEntityBlastFurnace;
import com.tom.factory.tileentity.TileEntityCentrifuge;
import com.tom.factory.tileentity.TileEntityCoilerPlant;
import com.tom.factory.tileentity.TileEntityCokeOven;
import com.tom.factory.tileentity.TileEntityCrusher;
import com.tom.factory.tileentity.TileEntityElectricFurnace;
import com.tom.factory.tileentity.TileEntityElectricFurnaceAdv;
import com.tom.factory.tileentity.TileEntityElectricalRubberProcessor;
import com.tom.factory.tileentity.TileEntityElectrolyzer;
import com.tom.factory.tileentity.TileEntityFluidBoiler;
import com.tom.factory.tileentity.TileEntityFluidTransposer;
import com.tom.factory.tileentity.TileEntityFusionPreHeater;
import com.tom.factory.tileentity.TileEntityGeoBoiler;
import com.tom.factory.tileentity.TileEntityIndustrialBlastFurnace;
import com.tom.factory.tileentity.TileEntityLaserEngraver;
import com.tom.factory.tileentity.TileEntityMixer;
import com.tom.factory.tileentity.TileEntityMultiblockController;
import com.tom.factory.tileentity.TileEntityPlasticProcessor;
import com.tom.factory.tileentity.TileEntityPlateBlendingMachine;
import com.tom.factory.tileentity.TileEntityPump;
import com.tom.factory.tileentity.TileEntityRefinery;
import com.tom.factory.tileentity.TileEntityRubberBoiler;
import com.tom.factory.tileentity.TileEntitySolderingStation;
import com.tom.factory.tileentity.TileEntitySteamAlloySmelter;
import com.tom.factory.tileentity.TileEntitySteamCrusher;
import com.tom.factory.tileentity.TileEntitySteamFurnace;
import com.tom.factory.tileentity.TileEntitySteamFurnaceAdv;
import com.tom.factory.tileentity.TileEntitySteamMixer;
import com.tom.factory.tileentity.TileEntitySteamPlateBlender;
import com.tom.factory.tileentity.TileEntitySteamRubberProcessor;
import com.tom.factory.tileentity.TileEntitySteamSolderingStation;
import com.tom.factory.tileentity.TileEntityUVLightbox;
import com.tom.factory.tileentity.TileEntityWaterCollector;
import com.tom.factory.tileentity.TileEntityWireMill;
import com.tom.handler.FuelHandler;
import com.tom.lib.Configs;

@Mod(modid = FactoryInit.modid, name = FactoryInit.modName, version = Configs.version, dependencies = Configs.coreDependencies)
public class FactoryInit {
	public static final String modid = Configs.ModidL + "|factory";
	public static final String modName = Configs.ModName + " Factory";
	public static final Logger log = LogManager.getLogger(modName);

	public static Item speedUpgrade, extruderModule, coalCoke, rfModule;

	public static Block MultiblockCase, MultiblockEnergyPort, MultiblockHatch, MultiblockFluidHatch, MultiblockFuelRod;
	public static Block Electrolyzer, Centrifuge, FusionPreHeater, cokeOven, blastFurnace, advBlastFurnace,
			industrialBlastFurnace, refinery, plasticProcessor;
	public static Block AdvancedMultiblockCasing, plateBlendingMachine, wireMill, crusher, basicBoiler, advBoiler,
			steamFurnace, advSteamFurnace, electricFurnace, uvLightbox, laserEngraver, steamCrusher, coilerPlant,
			waterCollector, steamPlateBlender;
	public static Block advElectricFurnace, steamSolderingStation, solderingStation, pump, fluidTransposer,
			geothermalBoiler, fluidBolier, advFluidBoiler, alloySmelter, steamAlloySmelter, steamMixer, mixer,
			rubberBoiler, rubberProcessor, electricalRubberProcessor;
	public static Block blockCoalCoke, cokeOvenWall, blastFurnaceWall, components, steelBoiler;

	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent) {
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		/** Items */
		speedUpgrade = new Item().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.speedUpgrade").setMaxStackSize(24);
		extruderModule = new ExtruderModule().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.extruderModule").setMaxStackSize(1);
		coalCoke = new ItemCoalCoke().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.coalCoke");
		rfModule = new Item().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.rfmodule");
		/** Blocks */
		cokeOvenWall = new Block(Material.ROCK).setHardness(5.0F).setResistance(20.0F).setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.cokeOvenWall");
		blastFurnaceWall = new Block(Material.ROCK).setHardness(6.0F).setResistance(40.0F).setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.blastFurnaceWall");
		blockCoalCoke = new Block(Material.ROCK).setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.cokeBlock").setHardness(5.0F).setResistance(10.0F);
		components = new BlockComponents().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.componentsBlock").setHardness(5.0F).setResistance(10.0F);
		steelBoiler = new BlockSteelBoiler().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.steelBoiler");
		/** TileEntities */
		MultiblockCase = new BlockMultiblockCasing().setUnlocalizedName("mbCase").setCreativeTab(tabTomsModFactory);
		MultiblockEnergyPort = new MultiblockEnergyPort().setUnlocalizedName("mbEnergyPort").setCreativeTab(tabTomsModFactory);
		MultiblockHatch = new MultiblockHatch().setUnlocalizedName("mbHatch").setCreativeTab(tabTomsModFactory);
		MultiblockFluidHatch = new MultiblockFluidHatch().setUnlocalizedName("mbFluidHatch").setCreativeTab(tabTomsModFactory);
		Electrolyzer = new Electrolyzer().setUnlocalizedName("Electrolyzer").setCreativeTab(tabTomsModFactory);
		Centrifuge = new Centrifuge().setUnlocalizedName("tm.centrifuge").setCreativeTab(tabTomsModFactory);
		MultiblockFuelRod = new MultiblockFuelRod().setUnlocalizedName("mbFuelRod").setCreativeTab(tabTomsModFactory);
		AdvancedMultiblockCasing = new BlockMultiblockCasing().setUnlocalizedName("mbAdvCasing").setCreativeTab(tabTomsModFactory);
		FusionPreHeater = new FusionPreHeater().setUnlocalizedName("fusionPreHeater").setCreativeTab(tabTomsModFactory);
		crusher = new BlockCrusher().setUnlocalizedName("tm.crusher").setCreativeTab(tabTomsModFactory);
		plateBlendingMachine = new PlateBlendingMachine().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.plateBlendingMachine");
		wireMill = new BlockWireMill().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.wiremill");
		coilerPlant = new CoilerPlant().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.coilerPlant");
		basicBoiler = new BasicBoiler().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.basicBoiler");
		advBoiler = new AdvBoiler().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.advBoiler");
		waterCollector = new BlockWaterCollector().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.waterCollector");
		steamCrusher = new SteamCrusher().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.steam.crusher");
		steamFurnace = new SteamFurnace().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.steam.furnace");
		steamPlateBlender = new SteamPlateBlender().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.steam.plateBlendingMachine");
		advSteamFurnace = new SteamFurnaceAdv().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.steam.adv.furnace");
		electricFurnace = new ElectricFurnace().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.electricFurnace");
		alloySmelter = new AlloySmelter().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.alloySmelter");
		steamAlloySmelter = new SteamAlloySmelter().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.steam.alloySmelter");
		advElectricFurnace = new ElectricFurnaceAdv().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.advElectricFurnace");
		steamSolderingStation = new SteamSolderingStation().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.steamSolderingStation");
		cokeOven = new BlockCokeOven().setUnlocalizedName("tm.cokeOven").setCreativeTab(tabTomsModFactory);
		blastFurnace = new BlockBlastFurnace().setUnlocalizedName("tm.blastFurnace").setCreativeTab(tabTomsModFactory);
		solderingStation = new SolderingStation().setUnlocalizedName("tm.solderingStation").setCreativeTab(tabTomsModFactory);
		pump = new BlockPump().setUnlocalizedName("tm.blockPump").setCreativeTab(tabTomsModFactory);
		fluidTransposer = new FluidTransposer().setUnlocalizedName("tm.fluidTransposer").setCreativeTab(tabTomsModFactory);
		industrialBlastFurnace = new IndustrialBlastFurnace().setUnlocalizedName("tm.industrialBlastFurnace").setCreativeTab(tabTomsModFactory);
		refinery = new BlockRefinery().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.refinery");
		geothermalBoiler = new BlockGeoBoiler().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.geoBoiler");
		fluidBolier = new FluidBoiler().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.fluidBoiler");
		advFluidBoiler = new AdvancedFluidBoiler().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.advFluidBoiler");
		plasticProcessor = new PlasticProcessor().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.plasticProcessor");
		uvLightbox = new UVLightbox().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.uvBox");
		steamMixer = new SteamMixer().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.steamMixer");
		mixer = new BlockMixer().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.mixer");
		laserEngraver = new LaserEngraver().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.laserEngraver");
		rubberBoiler = new RubberBoiler().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.rubberBoiler");
		rubberProcessor = new RubberProcessor().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.rubberProcessor");
		electricalRubberProcessor = new ElectricalRubberProcessor().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.erubberProcessor");
		advBlastFurnace = new BlockAdvBlastFurnace().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.advBlastFurnace");
		/** Registry */
		/** Items */
		registerItem(coalCoke);
		registerItem(speedUpgrade);
		registerItem(extruderModule);
		registerItem(rfModule);
		/** Blocks */
		registerBlock(MultiblockCase);
		registerBlock(MultiblockEnergyPort);
		registerBlock(MultiblockHatch);
		registerBlock(MultiblockFluidHatch);
		registerBlock(Electrolyzer);
		registerBlock(Centrifuge, Centrifuge.getUnlocalizedName().substring(5).substring(3));
		registerBlock(MultiblockFuelRod);
		registerBlock(AdvancedMultiblockCasing);
		registerBlock(FusionPreHeater);
		registerBlock(crusher);
		registerBlock(plateBlendingMachine);
		registerBlock(wireMill);
		registerBlock(coilerPlant);
		registerBlock(basicBoiler);
		registerBlock(advBoiler);
		registerBlock(waterCollector);
		registerBlock(steamCrusher);
		registerBlock(steamFurnace);
		registerBlock(steamPlateBlender);
		registerBlock(advSteamFurnace);
		registerBlock(electricFurnace);
		registerBlock(alloySmelter);
		registerBlock(steamAlloySmelter);
		registerBlock(steelBoiler);
		registerBlock(advElectricFurnace);
		registerBlock(blockCoalCoke);
		registerBlock(steamSolderingStation);
		registerBlock(cokeOvenWall);
		registerBlock(blastFurnaceWall);
		registerBlock(cokeOven);
		registerBlock(blastFurnace);
		registerBlock(solderingStation);
		registerBlock(pump);
		registerBlock(fluidTransposer);
		registerBlock(industrialBlastFurnace);
		registerBlock(refinery);
		registerBlock(components);
		registerBlock(geothermalBoiler);
		registerBlock(fluidBolier);
		registerBlock(advFluidBoiler);
		registerBlock(plasticProcessor);
		registerBlock(uvLightbox);
		registerBlock(steamMixer);
		registerBlock(mixer);
		registerBlock(laserEngraver);
		registerBlock(rubberBoiler);
		registerBlock(rubberProcessor);
		registerBlock(electricalRubberProcessor);
		registerBlock(advBlastFurnace);
		/** TileEntities */
		GameRegistry.registerTileEntity(TileEntityElectrolyzer.class, Configs.Modid + "mbContElectrolyzer");
		GameRegistry.registerTileEntity(TileEntityCentrifuge.class, Configs.Modid + "mbContCentrifuge");
		GameRegistry.registerTileEntity(TileEntityFusionPreHeater.class, Configs.Modid + "FusionPreHeater");
		GameRegistry.registerTileEntity(TileEntityCrusher.class, Configs.Modid + "crusher");
		GameRegistry.registerTileEntity(TileEntityPlateBlendingMachine.class, Configs.Modid + "plateBlendingMachine");
		GameRegistry.registerTileEntity(TileEntityWireMill.class, Configs.Modid + "wireMill");
		GameRegistry.registerTileEntity(TileEntityCoilerPlant.class, Configs.Modid + "coilerPlant");
		GameRegistry.registerTileEntity(TileEntityBasicBoiler.class, Configs.Modid + "basicBoiler");
		GameRegistry.registerTileEntity(TileEntityAdvBoiler.class, Configs.Modid + "advBoiler");
		GameRegistry.registerTileEntity(TileEntityWaterCollector.class, Configs.Modid + "waterCollector");
		GameRegistry.registerTileEntity(TileEntitySteamCrusher.class, Configs.Modid + "steamCrusher");
		GameRegistry.registerTileEntity(TileEntitySteamFurnace.class, Configs.Modid + "steamFurnace");
		GameRegistry.registerTileEntity(TileEntitySteamFurnaceAdv.class, Configs.Modid + "steamFurnaceAdv");
		GameRegistry.registerTileEntity(TileEntitySteamPlateBlender.class, Configs.Modid + "steamPlateBlendingMachine");
		GameRegistry.registerTileEntity(TileEntitySteamAlloySmelter.class, Configs.Modid + "steamAlloySmelter");
		GameRegistry.registerTileEntity(TileEntityAlloySmelter.class, Configs.Modid + "alloySmelter");
		GameRegistry.registerTileEntity(TileEntityElectricFurnace.class, Configs.Modid + "electricFurnace");
		GameRegistry.registerTileEntity(TileEntityElectricFurnaceAdv.class, Configs.Modid + "advElectricFurnace");
		GameRegistry.registerTileEntity(TileEntitySteamSolderingStation.class, Configs.Modid + "steamSolderingStation");
		GameRegistry.registerTileEntity(TileEntityCokeOven.class, Configs.Modid + "cokeOven");
		GameRegistry.registerTileEntity(TileEntityBlastFurnace.class, Configs.Modid + "blastFurnace");
		GameRegistry.registerTileEntity(TileEntitySolderingStation.class, Configs.Modid + "solderingStation");
		GameRegistry.registerTileEntity(TileEntityPump.class, Configs.Modid + "pump");
		GameRegistry.registerTileEntity(TileEntityFluidTransposer.class, Configs.Modid + "fluidTransposer");
		GameRegistry.registerTileEntity(TileEntityIndustrialBlastFurnace.class, Configs.Modid + "industrialBlastFurnace");
		GameRegistry.registerTileEntity(TileEntityRefinery.class, Configs.Modid + "refinery");
		GameRegistry.registerTileEntity(TileEntityGeoBoiler.class, Configs.Modid + "geoBoiler");
		GameRegistry.registerTileEntity(TileEntityFluidBoiler.class, Configs.Modid + "fluidBoiler");
		GameRegistry.registerTileEntity(TileEntityAdvFluidBoiler.class, Configs.Modid + "advFluidBoiler");
		GameRegistry.registerTileEntity(TileEntityPlasticProcessor.class, Configs.Modid + "plasticProcessor");
		GameRegistry.registerTileEntity(TileEntityUVLightbox.class, Configs.Modid + "uvBox");
		GameRegistry.registerTileEntity(TileEntitySteamMixer.class, Configs.Modid + "steamMixer");
		GameRegistry.registerTileEntity(TileEntityMixer.class, Configs.Modid + "mixer");
		GameRegistry.registerTileEntity(TileEntityLaserEngraver.class, Configs.Modid + "laserEngraver");
		GameRegistry.registerTileEntity(TileEntityRubberBoiler.class, Configs.Modid + "rubberboiler");
		GameRegistry.registerTileEntity(TileEntitySteamRubberProcessor.class, Configs.Modid + "rubberprocessor");
		GameRegistry.registerTileEntity(TileEntityElectricalRubberProcessor.class, Configs.Modid + "erubberprocessor");
		GameRegistry.registerTileEntity(TileEntityAdvBlastFurnace.class, Configs.Modid + "advblastfurnace");
		FuelHandler.registerExtraFuelHandler(new ItemStack(blockCoalCoke), 32000);
		TileEntityMultiblockController.init();
		hadPreInit = true;
		CoreInit.tryLoadAfterPreInit(log);
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in " + time + " milliseconds");
	}

	public static CreativeTabs tabTomsModFactory = new CreativeTabs("tabTomsModFactory") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Electrolyzer);
		}
	};
	private static boolean hadPreInit = false;

	@EventHandler
	public static void construction(FMLConstructionEvent event) {
		CoreInit.modids.add(new IMod() {
			@Override
			public String getModID() {
				return modid;
			}

			@Override
			public boolean hadPreInit() {
				return hadPreInit;
			}
		});
	}
}
