package com.tom.energy;

import static com.tom.core.CoreInit.registerBlock;
import static com.tom.core.CoreInit.registerItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
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
import com.tom.energy.item.HVCable;
import com.tom.energy.item.LVCable;
import com.tom.energy.item.MVCable;
import com.tom.energy.item.Multimeter;
import com.tom.energy.item.PortableEnergyCell;
import com.tom.energy.item.PortableSolarPanel;
import com.tom.energy.multipart.PartHVCable;
import com.tom.energy.multipart.PartLVCable;
import com.tom.energy.multipart.PartMVCable;
import com.tom.lib.Configs;

import com.tom.core.item.ItemBattery;

import com.tom.energy.block.BatteryBox;
import com.tom.energy.block.BlockCharger;
import com.tom.energy.block.BlockEnergyStorage;
import com.tom.energy.block.BlockSolarPanel;
import com.tom.energy.block.CreativeCell;
import com.tom.energy.block.FusionCharger;
import com.tom.energy.block.FusionController;
import com.tom.energy.block.FusionCore;
import com.tom.energy.block.FusionFluidExtractor;
import com.tom.energy.block.FusionFluidInjector;
import com.tom.energy.block.FusionInjector;
import com.tom.energy.block.Generator;
import com.tom.energy.block.GeoGenerator;
import com.tom.energy.block.HVBattery;
import com.tom.energy.block.HVCapacitor;
import com.tom.energy.block.HVLaserReceiver;
import com.tom.energy.block.LVSteamTurbinbe;
import com.tom.energy.block.LargeBatteryBox;
import com.tom.energy.block.LiquidFueledGenerator;
import com.tom.energy.block.MK1Laser;
import com.tom.energy.block.MK2Laser;
import com.tom.energy.block.MK3Laser;
import com.tom.energy.block.MVBattery;
import com.tom.energy.block.MVLaserReceiver;
import com.tom.energy.block.MVSteamTurbinbe;
import com.tom.energy.block.TransformerLM;
import com.tom.energy.block.TransformerMH;
import com.tom.energy.block.WirelessCharger;

import com.tom.energy.tileentity.TileEntityBatteryBox;
import com.tom.energy.tileentity.TileEntityCharger;
import com.tom.energy.tileentity.TileEntityCreativeCell;
import com.tom.energy.tileentity.TileEntityEnergySensor;
import com.tom.energy.tileentity.TileEntityFusionCharger;
import com.tom.energy.tileentity.TileEntityFusionController;
import com.tom.energy.tileentity.TileEntityFusionFluidExtractor;
import com.tom.energy.tileentity.TileEntityFusionFluidInjector;
import com.tom.energy.tileentity.TileEntityFusionInjector;
import com.tom.energy.tileentity.TileEntityGenerator;
import com.tom.energy.tileentity.TileEntityGeoGenerator;
import com.tom.energy.tileentity.TileEntityHVBattery;
import com.tom.energy.tileentity.TileEntityHVCapacitor;
import com.tom.energy.tileentity.TileEntityHVReceiver;
import com.tom.energy.tileentity.TileEntityLVTurbine;
import com.tom.energy.tileentity.TileEntityLargeBatteryBox;
import com.tom.energy.tileentity.TileEntityLaserMK1;
import com.tom.energy.tileentity.TileEntityLaserMK2;
import com.tom.energy.tileentity.TileEntityLaserMK3;
import com.tom.energy.tileentity.TileEntityLiquidFueledGenerator;
import com.tom.energy.tileentity.TileEntityMVBattery;
import com.tom.energy.tileentity.TileEntityMVReceiver;
import com.tom.energy.tileentity.TileEntityMVTurbine;
import com.tom.energy.tileentity.TileEntitySolarPanel;
import com.tom.energy.tileentity.TileEntityTransformerLMV;
import com.tom.energy.tileentity.TileEntityTransformerMHV;
import com.tom.energy.tileentity.TileEntityWirelessCharger;

@Mod(modid = EnergyInit.modid, name = EnergyInit.modName, version = Configs.version, dependencies = Configs.coreDependencies)
public class EnergyInit {
	public static final String modid = Configs.ModidL + "|energy";
	public static final String modName = Configs.ModName + " Energy";
	public static final Logger log = LogManager.getLogger(modName);

	// Items
	public static Item multimeter, portableSolarPanel, portableEnergyCell, battery;

	// Multiparts
	public static Block hvCable;
	public static Block mvCable;
	public static Block lvCable;

	// Blocks
	public static Block Generator/*, MK1Storage, MK2Storage, MK3Storage, lavaGenerator*/;
	public static Block MK1Laser, MK2Laser, MK3Laser, CreativeCell;
	public static Block wirelessCharger, hvEnergyCell, solarPanel, steamTurbine, steamTurbineMK2, geothermalGenerator,
			fluidGenerator, mvStorageController, hvStorageController;
	public static Block FusionCore, FusionInjector, FusionCharger, FusionController, FusionFluidInjector,
			FusionFluidExtractor;
	public static Block EnergyCellFrame, EnergyCellSide, EnergyCellCore;
	public static Block transformerMHV, transformerLMV, /*transformerLaser, transformerHV, */charger, mvLaserReceiver,
			hvLaserReceiver;
	public static BlockEnergyStorage batteryBox, largeBatBox, mvBattery, mvCapacitor, hvCapacitor, hvBattery;

	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent) {
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		/** Items */
		multimeter = new Multimeter().setUnlocalizedName("multimeter").setCreativeTab(tabTomsModEnergy).setMaxStackSize(1);
		portableSolarPanel = new PortableSolarPanel().setUnlocalizedName("PortableSolarPanel").setCreativeTab(tabTomsModEnergy).setMaxStackSize(1);
		portableEnergyCell = new PortableEnergyCell().setUnlocalizedName("PortableEnergyCell").setCreativeTab(tabTomsModEnergy).setMaxStackSize(1);
		battery = new ItemBattery().setUnlocalizedName("Battery").setCreativeTab(tabTomsModEnergy);
		/** Blocks */
		FusionCore = new FusionCore().setUnlocalizedName("FusionCore").setCreativeTab(tabTomsModEnergy);
		/** TileEntities */
		Generator = new Generator().setUnlocalizedName("generator").setCreativeTab(tabTomsModEnergy);
		// MK1Storage = new
		// MK1Storage().setUnlocalizedName("mk1Storage").setCreativeTab(tabTomsModEnergy);
		MK1Laser = new MK1Laser().setUnlocalizedName("mk1Laser").setCreativeTab(tabTomsModEnergy);
		MK2Laser = new MK2Laser().setUnlocalizedName("mk2Laser").setCreativeTab(tabTomsModEnergy);
		MK3Laser = new MK3Laser().setUnlocalizedName("mk3Laser").setCreativeTab(tabTomsModEnergy);
		// MK2Storage = new
		// MK2Storage().setUnlocalizedName("mk2Storage").setCreativeTab(tabTomsModEnergy);
		// MK3Storage = new
		// MK3Storage().setUnlocalizedName("mk3Storage").setCreativeTab(tabTomsModEnergy);
		FusionInjector = new FusionInjector().setUnlocalizedName("FusionInjector").setCreativeTab(tabTomsModEnergy);
		FusionCharger = new FusionCharger().setUnlocalizedName("FusionCharger").setCreativeTab(tabTomsModEnergy);
		FusionController = new FusionController().setUnlocalizedName("FusionController").setCreativeTab(tabTomsModEnergy);
		FusionFluidInjector = new FusionFluidInjector().setUnlocalizedName("FusionFluidInjector").setCreativeTab(tabTomsModEnergy);
		FusionFluidExtractor = new FusionFluidExtractor().setUnlocalizedName("FusionFluidExtractor").setCreativeTab(tabTomsModEnergy);
		/*EnergyCellFrame = new EnergyCellFrame().setUnlocalizedName("mbEnergyCellFrame").setCreativeTab(tabTomsModEnergy);
		EnergyCellSide = new EnergyCellSide().setUnlocalizedName("mbEnergyCellSide").setCreativeTab(tabTomsModEnergy);
		EnergyCellCore = new EnergyCellCore().setUnlocalizedName("mbEnergyCellCore").setCreativeTab(tabTomsModEnergy);*/
		wirelessCharger = new WirelessCharger().setUnlocalizedName("wirelessCharger").setCreativeTab(tabTomsModEnergy);
		transformerMHV = new TransformerMH().setUnlocalizedName("transformerMHV").setCreativeTab(tabTomsModEnergy);
		transformerLMV = new TransformerLM().setUnlocalizedName("transformerLMV").setCreativeTab(tabTomsModEnergy);
		// lavaGenerator = new
		// LavaGenerator().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.lavaGenerator");
		CreativeCell = new CreativeCell().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.creativeEnergyCell").setBlockUnbreakable().setResistance(18000000F);
		solarPanel = new BlockSolarPanel().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.solarPanelBasic");
		// transformerLaser = new
		// TransformerLaser().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("transformerLaser");
		// transformerHV = new
		// TransformerHV().setUnlocalizedName("transformerHV").setCreativeTab(tabTomsModEnergy);
		steamTurbine = new LVSteamTurbinbe().setUnlocalizedName("tm.lvTurbine").setCreativeTab(tabTomsModEnergy);
		batteryBox = new BatteryBox().setUnlocalizedName("tm.batteryBox").setCreativeTab(tabTomsModEnergy);
		steamTurbineMK2 = new MVSteamTurbinbe().setUnlocalizedName("tm.mvTurbine").setCreativeTab(tabTomsModEnergy);
		charger = new BlockCharger().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.charger");
		geothermalGenerator = new GeoGenerator().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.geoGenerator");
		fluidGenerator = new LiquidFueledGenerator().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.liqFueledGen");
		mvLaserReceiver = new MVLaserReceiver().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.mvReceiver");
		hvLaserReceiver = new HVLaserReceiver().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.hvReceiver");
		largeBatBox = new LargeBatteryBox().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.largeBatteryBox");
		mvBattery = new MVBattery().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.mvBatteryBox");
		hvBattery = new HVBattery().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.hvBatteryBox");
		hvCapacitor = new HVCapacitor().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.hvCapacitor");
		/** Multiparts */
		lvCable = new LVCable().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("lvCable");
		mvCable = new MVCable().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("mvCable");
		hvCable = new HVCable().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("hvCable");
		/** Registry */
		/** Items */
		registerItem(multimeter);
		registerItem(portableSolarPanel);
		registerItem(portableEnergyCell);
		registerItem(battery);
		/** Blocks */
		registerBlock(Generator);
		// registerBlock(MK1Storage);
		// registerBlock(MK2Storage);
		// registerBlock(MK3Storage);
		registerBlock(MK1Laser);
		registerBlock(MK2Laser);
		registerBlock(MK3Laser);
		registerBlock(FusionInjector);
		registerBlock(FusionCharger);
		registerBlock(FusionController);
		registerBlock(FusionFluidInjector);
		registerBlock(FusionFluidExtractor);
		registerBlock(FusionCore);
		registerBlock(transformerMHV);
		registerBlock(transformerLMV);
		registerBlock(wirelessCharger);
		// registerBlock(lavaGenerator);
		registerBlock(CreativeCell);
		registerBlock(solarPanel);
		// registerBlock(transformerLaser);
		// registerBlock(transformerHV);
		registerBlock(steamTurbine);
		registerBlock(batteryBox);
		registerBlock(steamTurbineMK2);
		registerBlock(charger);
		registerBlock(geothermalGenerator);
		registerBlock(fluidGenerator);
		registerBlock(mvLaserReceiver);
		registerBlock(hvLaserReceiver);
		registerBlock(largeBatBox);
		registerBlock(mvBattery);
		registerBlock(hvCapacitor);
		registerBlock(hvBattery);
		// GameRegistry.registerBlock(transformer,
		// TransformerItemBlock.class,transformer.getUnlocalizedName().substring(5));
		// registerBlock(EnergyCellFrame,
		// EnergyCellFrame.getUnlocalizedName().substring(5));
		// registerBlock(EnergyCellSide,
		// EnergyCellSide.getUnlocalizedName().substring(5));
		// registerBlock(EnergyCellCore,
		// EnergyCellCore.getUnlocalizedName().substring(5));
		/** Multiparts */
		registerBlock(hvCable);
		registerBlock(mvCable);
		registerBlock(lvCable);
		/** TileEntities */
		GameRegistry.registerTileEntity(TileEntityGenerator.class, Configs.Modid + "generator");
		GameRegistry.registerTileEntity(TileEntityFusionInjector.class, Configs.Modid + "injector");
		GameRegistry.registerTileEntity(TileEntityFusionCharger.class, Configs.Modid + "Charger");
		GameRegistry.registerTileEntity(TileEntityFusionController.class, Configs.Modid + "FusionController");
		GameRegistry.registerTileEntity(TileEntityFusionFluidExtractor.class, Configs.Modid + "FusionFluidInjector");
		GameRegistry.registerTileEntity(TileEntityFusionFluidInjector.class, Configs.Modid + "FusionFluidExtractor");
		// GameRegistry.registerTileEntity(TileEntityEnergyCellFrame.class,
		// Configs.Modid+"mbEnergyCellFrame");
		// GameRegistry.registerTileEntity(TileEntityEnergyCellSide.class,
		// Configs.Modid+"mbEnergyCellSide");
		// GameRegistry.registerTileEntity(TileEntityEnergyCellCore.class,
		// Configs.Modid+"mbEnergyCellCore");
		GameRegistry.registerTileEntity(TileEntityEnergySensor.class, Configs.Modid + "EnergySensor");
		GameRegistry.registerTileEntity(TileEntityWirelessCharger.class, Configs.Modid + "WirelessCharger");
		// GameRegistry.registerTileEntity(TileEntityEnergyCellMK1.class,
		// Configs.Modid + "EnergyCell1");
		// GameRegistry.registerTileEntity(TileEntityEnergyCellMK2.class,
		// Configs.Modid + "EnergyCell2");
		// GameRegistry.registerTileEntity(TileEntityEnergyCellMK3.class,
		// Configs.Modid + "EnergyCell3");
		GameRegistry.registerTileEntity(TileEntityLaserMK1.class, Configs.Modid + "Laser1");
		GameRegistry.registerTileEntity(TileEntityLaserMK2.class, Configs.Modid + "Laser2");
		GameRegistry.registerTileEntity(TileEntityLaserMK3.class, Configs.Modid + "Laser3");
		GameRegistry.registerTileEntity(TileEntityTransformerMHV.class, Configs.Modid + "Transformer");
		GameRegistry.registerTileEntity(TileEntityTransformerLMV.class, Configs.Modid + "TransformerL");
		// GameRegistry.registerTileEntity(TileEntityLavaGenerator.class,
		// Configs.Modid + "lavaGenerator");
		GameRegistry.registerTileEntity(TileEntityCreativeCell.class, Configs.Modid + "creativeCell");
		GameRegistry.registerTileEntity(TileEntitySolarPanel.class, Configs.Modid + "solarPanel");
		// GameRegistry.registerTileEntity(TileEntityTransformerLaser.class,
		// Configs.Modid + "TransformerLaser");
		// GameRegistry.registerTileEntity(TileEntityTransformerHV.class,
		// Configs.Modid + "TransformerHV");
		GameRegistry.registerTileEntity(TileEntityLVTurbine.class, Configs.Modid + "lvTurbine");
		GameRegistry.registerTileEntity(TileEntityBatteryBox.class, Configs.Modid + "batteryBox");
		GameRegistry.registerTileEntity(TileEntityMVTurbine.class, Configs.Modid + "mvTurbine");
		GameRegistry.registerTileEntity(TileEntityCharger.class, Configs.Modid + "tmcharger");
		GameRegistry.registerTileEntity(TileEntityGeoGenerator.class, Configs.Modid + "geoGenerator");
		GameRegistry.registerTileEntity(TileEntityLiquidFueledGenerator.class, Configs.Modid + "liquidFueledGenerator");
		GameRegistry.registerTileEntity(TileEntityHVCapacitor.class, Configs.Modid + "hvCapacitor");
		GameRegistry.registerTileEntity(TileEntityMVReceiver.class, Configs.Modid + "mvReceiver");
		GameRegistry.registerTileEntity(TileEntityHVReceiver.class, Configs.Modid + "hvReceiver");
		GameRegistry.registerTileEntity(TileEntityMVBattery.class, Configs.Modid + "mvBattery");
		GameRegistry.registerTileEntity(TileEntityHVBattery.class, Configs.Modid + "hvBattery");
		GameRegistry.registerTileEntity(TileEntityLargeBatteryBox.class, Configs.Modid + "largeBatteryBox");
		/** Multiparts */
		GameRegistry.registerTileEntity(PartLVCable.class, Configs.Modid + ":part:lvCable");
		GameRegistry.registerTileEntity(PartMVCable.class, Configs.Modid + ":part:mvCable");
		GameRegistry.registerTileEntity(PartHVCable.class, Configs.Modid + ":part:hvCable");

		/*CoreInit.ignoredLocations.add("tomsmod|energy:lvcable");
		CoreInit.ignoredLocations.add("tomsmod|energy:mvcable");
		CoreInit.ignoredLocations.add("tomsmod|energy:hvcable");*/

		hadPreInit = true;
		CoreInit.tryLoadAfterPreInit(log);
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in " + time + " milliseconds");
	}

	public static CreativeTabs tabTomsModEnergy = new CreativeTabs("tabTomsModEnergy") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(EnergyInit.Generator);
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
