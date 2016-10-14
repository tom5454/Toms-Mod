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

import com.tom.core.CoreInit;
import com.tom.core.IMod;
import com.tom.factory.block.AdvBoiler;
import com.tom.factory.block.AdvancedFluidBoiler;
import com.tom.factory.block.AdvancedMultiblockCasing;
import com.tom.factory.block.AlloySmelter;
import com.tom.factory.block.BasicBoiler;
import com.tom.factory.block.BlockBlastFurnace;
import com.tom.factory.block.BlockCokeOven;
import com.tom.factory.block.BlockComponents;
import com.tom.factory.block.BlockCrusher;
import com.tom.factory.block.BlockGeoBoiler;
import com.tom.factory.block.BlockPump;
import com.tom.factory.block.BlockRefinery;
import com.tom.factory.block.BlockWaterCollector;
import com.tom.factory.block.BlockWireMill;
import com.tom.factory.block.Centrifuge;
import com.tom.factory.block.CoilerPlant;
import com.tom.factory.block.CoolantTower;
import com.tom.factory.block.ElectricFurnace;
import com.tom.factory.block.ElectricFurnaceAdv;
import com.tom.factory.block.Electrolyzer;
import com.tom.factory.block.FluidBoiler;
import com.tom.factory.block.FluidTransposer;
import com.tom.factory.block.FusionPreHeater;
import com.tom.factory.block.IndustrialBlastFurnace;
import com.tom.factory.block.MultiblockCase;
import com.tom.factory.block.MultiblockCompressor;
import com.tom.factory.block.MultiblockEnergyPort;
import com.tom.factory.block.MultiblockFluidHatch;
import com.tom.factory.block.MultiblockFuelRod;
import com.tom.factory.block.MultiblockHatch;
import com.tom.factory.block.MultiblockHeatPort;
import com.tom.factory.block.MultiblockPressurePort;
import com.tom.factory.block.PlateBlendingMachine;
import com.tom.factory.block.SolderingStation;
import com.tom.factory.block.SteamAlloySmelter;
import com.tom.factory.block.SteamCrusher;
import com.tom.factory.block.SteamFurnace;
import com.tom.factory.block.SteamFurnaceAdv;
import com.tom.factory.block.SteamPlateBlender;
import com.tom.factory.block.SteamSolderingStation;
import com.tom.factory.item.ExtruderModule;
import com.tom.factory.item.ItemCoalCoke;
import com.tom.factory.tileentity.TileEntityAdvBoiler;
import com.tom.factory.tileentity.TileEntityAdvFluidBoiler;
import com.tom.factory.tileentity.TileEntityAdvMBCasing;
import com.tom.factory.tileentity.TileEntityAlloySmelter;
import com.tom.factory.tileentity.TileEntityBasicBoiler;
import com.tom.factory.tileentity.TileEntityBlastFurnace;
import com.tom.factory.tileentity.TileEntityCentrifuge;
import com.tom.factory.tileentity.TileEntityCoilerPlant;
import com.tom.factory.tileentity.TileEntityCokeOven;
import com.tom.factory.tileentity.TileEntityCoolantTower;
import com.tom.factory.tileentity.TileEntityCrusher;
import com.tom.factory.tileentity.TileEntityElectricFurnace;
import com.tom.factory.tileentity.TileEntityElectricFurnaceAdv;
import com.tom.factory.tileentity.TileEntityElectrolyzer;
import com.tom.factory.tileentity.TileEntityFluidBoiler;
import com.tom.factory.tileentity.TileEntityFluidTransposer;
import com.tom.factory.tileentity.TileEntityFusionPreHeater;
import com.tom.factory.tileentity.TileEntityGeoBoiler;
import com.tom.factory.tileentity.TileEntityIndustrialBlastFurnace;
import com.tom.factory.tileentity.TileEntityMBCompressor;
import com.tom.factory.tileentity.TileEntityMBEnergyPort;
import com.tom.factory.tileentity.TileEntityMBFluidPort;
import com.tom.factory.tileentity.TileEntityMBFuelRod;
import com.tom.factory.tileentity.TileEntityMBHatch;
import com.tom.factory.tileentity.TileEntityPlateBlendingMachine;
import com.tom.factory.tileentity.TileEntityPump;
import com.tom.factory.tileentity.TileEntityRefinery;
import com.tom.factory.tileentity.TileEntitySolderingStation;
import com.tom.factory.tileentity.TileEntitySteamAlloySmelter;
import com.tom.factory.tileentity.TileEntitySteamCrusher;
import com.tom.factory.tileentity.TileEntitySteamFurnace;
import com.tom.factory.tileentity.TileEntitySteamFurnaceAdv;
import com.tom.factory.tileentity.TileEntitySteamPlateBlender;
import com.tom.factory.tileentity.TileEntitySteamSolderingStation;
import com.tom.factory.tileentity.TileEntityWaterCollector;
import com.tom.factory.tileentity.TileEntityWireMill;
import com.tom.handler.FuelHandler;
import com.tom.lib.Configs;

import com.tom.core.tileentity.TileEntityMultiblockCase;

@Mod(modid = FactoryInit.modid,name = FactoryInit.modName,version = Configs.version, dependencies = Configs.coreDependencies)
public class FactoryInit {
	public static final String modid = Configs.ModidL + "|factory";
	public static final String modName = Configs.ModName + " Factory";
	public static final Logger log = LogManager.getLogger(modName);

	public static Item speedUpgrade, extruderModule, coalCoke;

	public static Block MultiblockCase, MultiblockEnergyPort, MultiblockHatch, MultiblockHeatPort, MultiblockPressurePort, MultiblockFluidHatch, MultiblockFuelRod, MultiblockCompressor;
	public static Block Electrolyzer, Centrifuge, FusionPreHeater, CoolantTower, cokeOven, blastFurnace, industrialBlastFurnace, refinery;
	public static Block AdvancedMultiblockCasing, plateBlendingMachine, wireMill, crusher, basicBoiler, advBoiler, steamFurnace, advSteamFurnace, electricFurnace, steamCrusher, coilerPlant, waterCollector, steamPlateBlender;
	public static Block advElectricFurnace, steamSolderingStation, solderingStation, pump, fluidTransposer, plasticProcessor, geothermalBoiler, fluidBolier, advFluidBoiler, alloySmelter, steamAlloySmelter;
	public static Block blockCoalCoke, cokeOvenWall, blastFurnaceWall, components;
	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent){
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		MultiblockCase = new MultiblockCase().setUnlocalizedName("mbCase").setCreativeTab(tabTomsModFactory);
		MultiblockEnergyPort = new MultiblockEnergyPort().setUnlocalizedName("mbEnergyPort").setCreativeTab(tabTomsModFactory);
		MultiblockHatch = new MultiblockHatch().setUnlocalizedName("mbHatch").setCreativeTab(tabTomsModFactory);
		MultiblockFluidHatch = new MultiblockFluidHatch().setUnlocalizedName("mbFluidHatch").setCreativeTab(tabTomsModFactory);
		MultiblockHeatPort = new MultiblockHeatPort().setUnlocalizedName("mbHeatPort").setCreativeTab(tabTomsModFactory)/*.setBlockTextureName("minecraft:mbHeatPort")*/;
		MultiblockPressurePort = new MultiblockPressurePort().setCreativeTab(tabTomsModFactory).setUnlocalizedName("mbPressurePort");
		Electrolyzer = new Electrolyzer().setUnlocalizedName("Electrolyzer").setCreativeTab(tabTomsModFactory);
		Centrifuge = new Centrifuge().setUnlocalizedName("tm.centrifuge").setCreativeTab(tabTomsModFactory);
		MultiblockFuelRod = new MultiblockFuelRod().setUnlocalizedName("mbFuelRod").setCreativeTab(tabTomsModFactory);
		AdvancedMultiblockCasing = new AdvancedMultiblockCasing().setUnlocalizedName("mbAdvCasing").setCreativeTab(tabTomsModFactory);
		FusionPreHeater = new FusionPreHeater().setUnlocalizedName("fusionPreHeater").setCreativeTab(tabTomsModFactory);
		MultiblockCompressor = new MultiblockCompressor().setUnlocalizedName("mbCompressor").setCreativeTab(tabTomsModFactory)/*.setBlockTextureName("minecraft:mbComperssor")*/;
		CoolantTower = new CoolantTower().setUnlocalizedName("coolantTower").setCreativeTab(tabTomsModFactory);
		crusher = new BlockCrusher().setUnlocalizedName("tm.crusher").setCreativeTab(tabTomsModFactory);
		speedUpgrade = new Item().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.speedUpgrade").setMaxStackSize(24);
		plateBlendingMachine = new PlateBlendingMachine().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.plateBlendingMachine");
		extruderModule = new ExtruderModule().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.extruderModule").setMaxStackSize(1);
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
		steamAlloySmelter  = new SteamAlloySmelter().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.steam.alloySmelter");
		advElectricFurnace = new ElectricFurnaceAdv().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.advElectricFurnace");
		coalCoke = new ItemCoalCoke().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.coalCoke");
		blockCoalCoke = new Block(Material.ROCK).setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.cokeBlock").setHardness(5.0F).setResistance(10.0F);
		steamSolderingStation = new SteamSolderingStation().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.steamSolderingStation");
		cokeOvenWall = new Block(Material.ROCK).setHardness(5.0F).setResistance(20.0F).setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.cokeOvenWall");
		blastFurnaceWall = new Block(Material.ROCK).setHardness(6.0F).setResistance(40.0F).setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.blastFurnaceWall");
		cokeOven = new BlockCokeOven().setUnlocalizedName("tm.cokeOven").setCreativeTab(tabTomsModFactory);
		blastFurnace = new BlockBlastFurnace().setUnlocalizedName("tm.blastFurnace").setCreativeTab(tabTomsModFactory);
		solderingStation = new SolderingStation().setUnlocalizedName("tm.solderingStation").setCreativeTab(tabTomsModFactory);
		pump = new BlockPump().setUnlocalizedName("tm.blockPump").setCreativeTab(tabTomsModFactory);
		fluidTransposer = new FluidTransposer().setUnlocalizedName("tm.fluidTransposer").setCreativeTab(tabTomsModFactory);
		industrialBlastFurnace = new IndustrialBlastFurnace().setUnlocalizedName("tm.industrialBlastFurnace").setCreativeTab(tabTomsModFactory);
		refinery = new BlockRefinery().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.refinery");
		components = new BlockComponents().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.componentsBlock");
		geothermalBoiler = new BlockGeoBoiler().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.geoBoiler");
		fluidBolier = new FluidBoiler().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.fluidBoiler");
		advFluidBoiler = new AdvancedFluidBoiler().setCreativeTab(tabTomsModFactory).setUnlocalizedName("tm.advFluidBoiler");
		registerItem(speedUpgrade, speedUpgrade.getUnlocalizedName().substring(5));
		registerItem(extruderModule, extruderModule.getUnlocalizedName().substring(5));
		registerBlock(MultiblockCase, MultiblockCase.getUnlocalizedName().substring(5));
		registerBlock(MultiblockEnergyPort, MultiblockEnergyPort.getUnlocalizedName().substring(5));
		registerBlock(MultiblockHatch, MultiblockHatch.getUnlocalizedName().substring(5));
		registerBlock(MultiblockFluidHatch, MultiblockFluidHatch.getUnlocalizedName().substring(5));
		registerBlock(Electrolyzer, Electrolyzer.getUnlocalizedName().substring(5));
		registerBlock(Centrifuge, Centrifuge.getUnlocalizedName().substring(5).substring(3));
		registerBlock(MultiblockFuelRod, MultiblockFuelRod.getUnlocalizedName().substring(5));
		registerBlock(AdvancedMultiblockCasing, AdvancedMultiblockCasing.getUnlocalizedName().substring(5));
		//registerBlock(EnergySensor, EnergySensor.getUnlocalizedName().substring(5));
		registerBlock(FusionPreHeater, FusionPreHeater.getUnlocalizedName().substring(5));
		registerBlock(MultiblockCompressor, MultiblockCompressor.getUnlocalizedName().substring(5));
		registerBlock(CoolantTower, CoolantTower.getUnlocalizedName().substring(5));
		registerBlock(crusher, crusher.getUnlocalizedName().substring(5));
		registerBlock(plateBlendingMachine, plateBlendingMachine.getUnlocalizedName().substring(5));
		registerBlock(wireMill, wireMill.getUnlocalizedName().substring(5));
		registerBlock(coilerPlant, coilerPlant.getUnlocalizedName().substring(5));
		registerBlock(basicBoiler, basicBoiler.getUnlocalizedName().substring(5));
		registerBlock(advBoiler, advBoiler.getUnlocalizedName().substring(5));
		registerBlock(waterCollector, waterCollector.getUnlocalizedName().substring(5));
		registerBlock(steamCrusher, steamCrusher.getUnlocalizedName().substring(5));
		registerBlock(steamFurnace, steamFurnace.getUnlocalizedName().substring(5));
		registerBlock(steamPlateBlender, steamPlateBlender.getUnlocalizedName().substring(5));
		registerBlock(advSteamFurnace, advSteamFurnace.getUnlocalizedName().substring(5));
		registerBlock(electricFurnace, electricFurnace.getUnlocalizedName().substring(5));
		registerBlock(alloySmelter, alloySmelter.getUnlocalizedName().substring(5));
		registerBlock(steamAlloySmelter, steamAlloySmelter.getUnlocalizedName().substring(5));
		registerBlock(advElectricFurnace, advElectricFurnace.getUnlocalizedName().substring(5));
		registerItem(coalCoke, coalCoke.getUnlocalizedName().substring(5));
		registerBlock(blockCoalCoke, blockCoalCoke.getUnlocalizedName().substring(5));
		registerBlock(steamSolderingStation, steamSolderingStation.getUnlocalizedName().substring(5));
		registerBlock(cokeOvenWall, cokeOvenWall.getUnlocalizedName().substring(5));
		registerBlock(blastFurnaceWall, blastFurnaceWall.getUnlocalizedName().substring(5));
		registerBlock(cokeOven, cokeOven.getUnlocalizedName().substring(5));
		registerBlock(blastFurnace, blastFurnace.getUnlocalizedName().substring(5));
		registerBlock(solderingStation, solderingStation.getUnlocalizedName().substring(5));
		registerBlock(pump, pump.getUnlocalizedName().substring(5));
		registerBlock(fluidTransposer, fluidTransposer.getUnlocalizedName().substring(5));
		registerBlock(industrialBlastFurnace, industrialBlastFurnace.getUnlocalizedName().substring(5));
		registerBlock(refinery, refinery.getUnlocalizedName().substring(5));
		registerBlock(components, components.getUnlocalizedName().substring(5));
		registerBlock(geothermalBoiler, geothermalBoiler.getUnlocalizedName().substring(5));
		registerBlock(fluidBolier, fluidBolier.getUnlocalizedName().substring(5));
		registerBlock(advFluidBoiler, advFluidBoiler.getUnlocalizedName().substring(5));
		GameRegistry.registerTileEntity(TileEntityMultiblockCase.class, Configs.Modid+"MultiblockCase");
		GameRegistry.registerTileEntity(TileEntityMBEnergyPort.class, Configs.Modid+"mbEnergyPort");
		GameRegistry.registerTileEntity(TileEntityMBHatch.class, Configs.Modid+"mbHatch");
		GameRegistry.registerTileEntity(TileEntityMBFluidPort.class, Configs.Modid+"mbFluidPort");
		GameRegistry.registerTileEntity(TileEntityElectrolyzer.class, Configs.Modid+"mbContElectrolyzer");
		GameRegistry.registerTileEntity(TileEntityCentrifuge.class, Configs.Modid+"mbContCentrifuge");
		GameRegistry.registerTileEntity(TileEntityMBFuelRod.class, Configs.Modid+"mbFuelRod");
		GameRegistry.registerTileEntity(TileEntityAdvMBCasing.class, Configs.Modid+"mbAdvCasing");
		GameRegistry.registerTileEntity(TileEntityFusionPreHeater.class, Configs.Modid+"FusionPreHeater");
		GameRegistry.registerTileEntity(TileEntityMBCompressor.class, Configs.Modid+"mbCompressor");
		GameRegistry.registerTileEntity(TileEntityCoolantTower.class, Configs.Modid+"coolantTower");
		GameRegistry.registerTileEntity(TileEntityCrusher.class, Configs.Modid+"crusher");
		GameRegistry.registerTileEntity(TileEntityPlateBlendingMachine.class, Configs.Modid+"plateBlendingMachine");
		GameRegistry.registerTileEntity(TileEntityWireMill.class, Configs.Modid+"wireMill");
		GameRegistry.registerTileEntity(TileEntityCoilerPlant.class, Configs.Modid+"coilerPlant");
		GameRegistry.registerTileEntity(TileEntityBasicBoiler.class, Configs.Modid+"basicBoiler");
		GameRegistry.registerTileEntity(TileEntityAdvBoiler.class, Configs.Modid+"advBoiler");
		GameRegistry.registerTileEntity(TileEntityWaterCollector.class, Configs.Modid+"waterCollector");
		GameRegistry.registerTileEntity(TileEntitySteamCrusher.class, Configs.Modid+"steamCrusher");
		GameRegistry.registerTileEntity(TileEntitySteamFurnace.class, Configs.Modid+"steamFurnace");
		GameRegistry.registerTileEntity(TileEntitySteamFurnaceAdv.class, Configs.Modid+"steamFurnaceAdv");
		GameRegistry.registerTileEntity(TileEntitySteamPlateBlender.class, Configs.Modid+"steamPlateBlendingMachine");
		GameRegistry.registerTileEntity(TileEntitySteamAlloySmelter.class, Configs.Modid+"steamAlloySmelter");
		GameRegistry.registerTileEntity(TileEntityAlloySmelter.class, Configs.Modid+"alloySmelter");
		GameRegistry.registerTileEntity(TileEntityElectricFurnace.class, Configs.Modid+"electricFurnace");
		GameRegistry.registerTileEntity(TileEntityElectricFurnaceAdv.class, Configs.Modid+"advElectricFurnace");
		GameRegistry.registerTileEntity(TileEntitySteamSolderingStation.class, Configs.Modid+"steamSolderingStation");
		GameRegistry.registerTileEntity(TileEntityCokeOven.class, Configs.Modid+"cokeOven");
		GameRegistry.registerTileEntity(TileEntityBlastFurnace.class, Configs.Modid+"blastFurnace");
		GameRegistry.registerTileEntity(TileEntitySolderingStation.class, Configs.Modid+"solderingStation");
		GameRegistry.registerTileEntity(TileEntityPump.class, Configs.Modid+"pump");
		GameRegistry.registerTileEntity(TileEntityFluidTransposer.class, Configs.Modid+"fluidTransposer");
		GameRegistry.registerTileEntity(TileEntityIndustrialBlastFurnace.class, Configs.Modid+"industrialBlastFurnace");
		GameRegistry.registerTileEntity(TileEntityRefinery.class, Configs.Modid+"refinery");
		GameRegistry.registerTileEntity(TileEntityGeoBoiler.class, Configs.Modid+"geoBoiler");
		GameRegistry.registerTileEntity(TileEntityFluidBoiler.class, Configs.Modid+"fluidBoiler");
		GameRegistry.registerTileEntity(TileEntityAdvFluidBoiler.class, Configs.Modid+"advFluidBoiler");
		FuelHandler.registerExtraFuelHandler(new ItemStack(blockCoalCoke), 32000);
		hadPreInit = true;
		CoreInit.tryLoadAfterPreInit(log);
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in "+time+" milliseconds");
	}
	public static CreativeTabs tabTomsModFactory = new CreativeTabs("tabTomsModFactory"){

		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(Electrolyzer);
		}
	};
	private static boolean hadPreInit = false;
	@EventHandler
	public static void construction(FMLConstructionEvent event){
		CoreInit.modids.add(new IMod(){
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
