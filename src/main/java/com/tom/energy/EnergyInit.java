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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.item.MultipartItem;
import com.tom.core.CoreInit;
import com.tom.energy.item.HVCable;
import com.tom.energy.item.LVCable;
import com.tom.energy.item.MVCable;
import com.tom.energy.item.Multimeter;
import com.tom.energy.item.PortableEnergyCell;
import com.tom.energy.item.PortableSolarPanel;
import com.tom.energy.item.WirelessChargerItemBlock;
import com.tom.energy.multipart.PartHVCable;
import com.tom.energy.multipart.PartLVCable;
import com.tom.energy.multipart.PartMVCable;
import com.tom.lib.Configs;

import com.tom.energy.block.BatteryBox;
import com.tom.energy.block.BlockSolarPanel;
import com.tom.energy.block.CreativeCell;
import com.tom.energy.block.EnergyCellCore;
import com.tom.energy.block.EnergyCellFrame;
import com.tom.energy.block.EnergyCellSide;
import com.tom.energy.block.FusionCharger;
import com.tom.energy.block.FusionController;
import com.tom.energy.block.FusionCore;
import com.tom.energy.block.FusionFluidExtractor;
import com.tom.energy.block.FusionFluidInjector;
import com.tom.energy.block.FusionInjector;
import com.tom.energy.block.Generator;
import com.tom.energy.block.LVSteamTurbinbe;
import com.tom.energy.block.LavaGenerator;
import com.tom.energy.block.MK1Laser;
import com.tom.energy.block.MK1Storage;
import com.tom.energy.block.MK2Laser;
import com.tom.energy.block.MK2Storage;
import com.tom.energy.block.MK3Laser;
import com.tom.energy.block.MK3Storage;
import com.tom.energy.block.TransformerHV;
import com.tom.energy.block.TransformerLM;
import com.tom.energy.block.TransformerLaser;
import com.tom.energy.block.TransformerMH;
import com.tom.energy.block.WirelessCharger;

import com.tom.energy.tileentity.TileEntityBatteryBox;
import com.tom.energy.tileentity.TileEntityCreativeCell;
import com.tom.energy.tileentity.TileEntityEnergyCellMK1;
import com.tom.energy.tileentity.TileEntityEnergyCellMK2;
import com.tom.energy.tileentity.TileEntityEnergyCellMK3;
import com.tom.energy.tileentity.TileEntityEnergySensor;
import com.tom.energy.tileentity.TileEntityFusionCharger;
import com.tom.energy.tileentity.TileEntityFusionController;
import com.tom.energy.tileentity.TileEntityFusionFluidExtractor;
import com.tom.energy.tileentity.TileEntityFusionFluidInjector;
import com.tom.energy.tileentity.TileEntityFusionInjector;
import com.tom.energy.tileentity.TileEntityGenerator;
import com.tom.energy.tileentity.TileEntityLVTurbine;
import com.tom.energy.tileentity.TileEntityLaserMK1;
import com.tom.energy.tileentity.TileEntityLaserMK2;
import com.tom.energy.tileentity.TileEntityLaserMK3;
import com.tom.energy.tileentity.TileEntityLavaGenerator;
import com.tom.energy.tileentity.TileEntitySolarPanel;
import com.tom.energy.tileentity.TileEntityTransformerHV;
import com.tom.energy.tileentity.TileEntityTransformerLMV;
import com.tom.energy.tileentity.TileEntityTransformerLaser;
import com.tom.energy.tileentity.TileEntityTransformerMHV;
import com.tom.energy.tileentity.TileEntityWirelessCharger;

@Mod(modid = EnergyInit.modid,name = "Tom's Mod Energy",version = Configs.version, dependencies = Configs.coreDependencies)
public class EnergyInit {
	public static final String modid = Configs.Modid + "|Energy";
	public static Logger log = LogManager.getLogger(modid);

	//Items
	public static Item multimeter, portableSolarPanel, portableEnergyCell;

	//Multiparts
	public static MultipartItem hvCable;
	public static MultipartItem mvCable;
	public static MultipartItem lvCable;

	//Blocks
	public static Block Generator, MK1Storage, MK1Laser, wirelessCharger, MK2Laser, MK3Laser, MK2Storage, MK3Storage, lavaGenerator, CreativeCell, hvEnergyCell, solarPanel, steamTurbine, batteryBox;
	public static Block FusionCore, FusionInjector, FusionCharger, FusionController, FusionFluidInjector, FusionFluidExtractor;
	public static Block EnergyCellFrame, EnergyCellSide, EnergyCellCore;
	public static Block transformerMHV, transformerLMV, transformerLaser, transformerHV;

	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent){
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		FusionCore = new FusionCore().setUnlocalizedName("FusionCore").setCreativeTab(tabTomsModEnergy)/*.setBlockTextureName("minecraft:FusionCore2")*/;
		Generator = new Generator().setUnlocalizedName("generator").setCreativeTab(tabTomsModEnergy);
		MK1Storage = new MK1Storage().setUnlocalizedName("mk1Storage").setCreativeTab(tabTomsModEnergy);
		MK1Laser = new MK1Laser().setUnlocalizedName("mk1Laser").setCreativeTab(tabTomsModEnergy);
		MK2Laser = new MK2Laser().setUnlocalizedName("mk2Laser").setCreativeTab(tabTomsModEnergy);
		MK3Laser = new MK3Laser().setUnlocalizedName("mk3Laser").setCreativeTab(tabTomsModEnergy);
		MK2Storage = new MK2Storage().setUnlocalizedName("mk2Storage").setCreativeTab(tabTomsModEnergy);
		MK3Storage = new MK3Storage().setUnlocalizedName("mk3Storage").setCreativeTab(tabTomsModEnergy);
		FusionInjector = new FusionInjector().setUnlocalizedName("FusionInjector").setCreativeTab(tabTomsModEnergy);
		FusionCharger = new FusionCharger().setUnlocalizedName("FusionCharger").setCreativeTab(tabTomsModEnergy)/*.setBlockTextureName("minecraft:fusionCase")*/;
		FusionController = new FusionController().setUnlocalizedName("FusionController").setCreativeTab(tabTomsModEnergy);
		FusionFluidInjector = new FusionFluidInjector().setUnlocalizedName("FusionFluidInjector").setCreativeTab(tabTomsModEnergy);
		FusionFluidExtractor = new FusionFluidExtractor().setUnlocalizedName("FusionFluidExtractor").setCreativeTab(tabTomsModEnergy);
		EnergyCellFrame = new EnergyCellFrame().setUnlocalizedName("mbEnergyCellFrame").setCreativeTab(tabTomsModEnergy);
		EnergyCellSide = new EnergyCellSide().setUnlocalizedName("mbEnergyCellSide").setCreativeTab(tabTomsModEnergy);
		EnergyCellCore = new EnergyCellCore().setUnlocalizedName("mbEnergyCellCore").setCreativeTab(tabTomsModEnergy);
		wirelessCharger = new WirelessCharger().setUnlocalizedName("wirelessCharger").setCreativeTab(tabTomsModEnergy);
		multimeter = new Multimeter().setUnlocalizedName("multimeter").setCreativeTab(tabTomsModEnergy).setMaxStackSize(1);
		hvCable = new HVCable().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("hvCable");
		transformerMHV = new TransformerMH().setUnlocalizedName("transformerMHV").setCreativeTab(tabTomsModEnergy);
		transformerLMV = new TransformerLM().setUnlocalizedName("transformerLMV").setCreativeTab(tabTomsModEnergy);
		lvCable = new LVCable().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("lvCable");
		lavaGenerator = new LavaGenerator().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.lavaGenerator");
		CreativeCell = new CreativeCell().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.creativeEnergyCell");
		solarPanel = new BlockSolarPanel().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("tm.solarPanelBasic");
		portableSolarPanel = new PortableSolarPanel().setUnlocalizedName("PortableSolarPanel").setCreativeTab(tabTomsModEnergy)/*.setTextureName("minecraft:tm/solar")*/.setMaxStackSize(1);
		portableEnergyCell = new PortableEnergyCell().setUnlocalizedName("PortableEnergyCell").setCreativeTab(tabTomsModEnergy)/*.setTextureName("minecraft:tm/portableEnergyCell")*/.setMaxStackSize(1);
		mvCable = new MVCable().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("mvCable");
		transformerLaser = new TransformerLaser().setCreativeTab(tabTomsModEnergy).setUnlocalizedName("transformerLaser");
		transformerHV = new TransformerHV().setUnlocalizedName("transformerHV").setCreativeTab(tabTomsModEnergy);
		steamTurbine = new LVSteamTurbinbe().setUnlocalizedName("tm.lvTurbine").setCreativeTab(tabTomsModEnergy);
		batteryBox = new BatteryBox().setUnlocalizedName("tm.batteryBox").setCreativeTab(tabTomsModEnergy);
		registerItem(multimeter, multimeter.getUnlocalizedName().substring(5));
		registerBlock(Generator, Generator.getUnlocalizedName().substring(5));
		registerBlock(MK1Storage, MK1Storage.getUnlocalizedName().substring(5));
		registerBlock(MK2Storage, MK2Storage.getUnlocalizedName().substring(5));
		registerBlock(MK3Storage, MK3Storage.getUnlocalizedName().substring(5));
		registerBlock(MK1Laser, MK1Laser.getUnlocalizedName().substring(5));
		registerBlock(MK2Laser, MK2Laser.getUnlocalizedName().substring(5));
		registerBlock(MK3Laser, MK3Laser.getUnlocalizedName().substring(5));
		registerBlock(FusionInjector, FusionInjector.getUnlocalizedName().substring(5));
		registerBlock(FusionCharger, FusionCharger.getUnlocalizedName().substring(5));
		registerBlock(FusionController, FusionController.getUnlocalizedName().substring(5));
		registerBlock(FusionFluidInjector, FusionFluidInjector.getUnlocalizedName().substring(5));
		registerBlock(FusionFluidExtractor, FusionFluidExtractor.getUnlocalizedName().substring(5));
		registerBlock(FusionCore, FusionCore.getUnlocalizedName().substring(5));
		//GameRegistry.registerBlock(transformer, TransformerItemBlock.class,transformer.getUnlocalizedName().substring(5));
		registerBlock(transformerMHV, transformerMHV.getUnlocalizedName().substring(5));
		registerBlock(transformerLMV, transformerLMV.getUnlocalizedName().substring(5));
		//registerBlock(EnergyCellFrame, EnergyCellFrame.getUnlocalizedName().substring(5));
		//registerBlock(EnergyCellSide, EnergyCellSide.getUnlocalizedName().substring(5));
		//registerBlock(EnergyCellCore, EnergyCellCore.getUnlocalizedName().substring(5));
		CoreInit.addBlockToGameRegistry(wirelessCharger, wirelessCharger.getUnlocalizedName().substring(5), new WirelessChargerItemBlock());
		registerBlock(lavaGenerator, lavaGenerator.getUnlocalizedName().substring(5));
		registerBlock(CreativeCell, CreativeCell.getUnlocalizedName().substring(5));
		registerBlock(solarPanel, solarPanel.getUnlocalizedName().substring(5));
		registerItem(portableSolarPanel, portableSolarPanel.getUnlocalizedName().substring(5));
		registerItem(portableEnergyCell, portableEnergyCell.getUnlocalizedName().substring(5));
		registerBlock(transformerLaser, transformerLaser.getUnlocalizedName().substring(5));
		registerBlock(transformerHV, transformerHV.getUnlocalizedName().substring(5));
		registerBlock(steamTurbine, steamTurbine.getUnlocalizedName().substring(5));
		registerBlock(batteryBox, batteryBox.getUnlocalizedName().substring(5));
		GameRegistry.registerTileEntity(TileEntityGenerator.class, Configs.Modid + "generator");
		GameRegistry.registerTileEntity(TileEntityFusionInjector.class, Configs.Modid+"injector");
		GameRegistry.registerTileEntity(TileEntityFusionCharger.class, Configs.Modid+"Charger");
		GameRegistry.registerTileEntity(TileEntityFusionController.class, Configs.Modid+"FusionController");
		GameRegistry.registerTileEntity(TileEntityFusionFluidExtractor.class, Configs.Modid+"FusionFluidInjector");
		GameRegistry.registerTileEntity(TileEntityFusionFluidInjector.class, Configs.Modid+"FusionFluidExtractor");
		//GameRegistry.registerTileEntity(TileEntityEnergyCellFrame.class, Configs.Modid+"mbEnergyCellFrame");
		//GameRegistry.registerTileEntity(TileEntityEnergyCellSide.class, Configs.Modid+"mbEnergyCellSide");
		//GameRegistry.registerTileEntity(TileEntityEnergyCellCore.class, Configs.Modid+"mbEnergyCellCore");
		GameRegistry.registerTileEntity(TileEntityEnergySensor.class, Configs.Modid+"EnergySensor");
		GameRegistry.registerTileEntity(TileEntityWirelessCharger.class, Configs.Modid + "WirelessCharger");
		GameRegistry.registerTileEntity(TileEntityEnergyCellMK1.class, Configs.Modid + "EnergyCell1");
		GameRegistry.registerTileEntity(TileEntityEnergyCellMK2.class, Configs.Modid + "EnergyCell2");
		GameRegistry.registerTileEntity(TileEntityEnergyCellMK3.class, Configs.Modid + "EnergyCell3");
		GameRegistry.registerTileEntity(TileEntityLaserMK1.class, Configs.Modid + "Laser1");
		GameRegistry.registerTileEntity(TileEntityLaserMK2.class, Configs.Modid + "Laser2");
		GameRegistry.registerTileEntity(TileEntityLaserMK3.class, Configs.Modid + "Laser3");
		GameRegistry.registerTileEntity(TileEntityTransformerMHV.class, Configs.Modid + "Transformer");
		GameRegistry.registerTileEntity(TileEntityTransformerLMV.class, Configs.Modid + "TransformerL");
		CoreInit.registerMultipart(hvCable, PartHVCable.class, "cableHv", "tomsmodenergy");
		CoreInit.registerMultipart(lvCable, PartLVCable.class, "cableLv", "tomsmodenergy");
		GameRegistry.registerTileEntity(TileEntityLavaGenerator.class, Configs.Modid + "lavaGenerator");
		GameRegistry.registerTileEntity(TileEntityCreativeCell.class, Configs.Modid + "creativeCell");
		GameRegistry.registerTileEntity(TileEntitySolarPanel.class, Configs.Modid + "solarPanel");
		CoreInit.registerMultipart(mvCable, PartMVCable.class, "cableMv", "tomsmodenergy");
		GameRegistry.registerTileEntity(TileEntityTransformerLaser.class, Configs.Modid + "TransformerLaser");
		GameRegistry.registerTileEntity(TileEntityTransformerHV.class, Configs.Modid + "TransformerHV");
		GameRegistry.registerTileEntity(TileEntityLVTurbine.class, Configs.Modid + "lvTurbine");
		GameRegistry.registerTileEntity(TileEntityBatteryBox.class, Configs.Modid + "batteryBox");
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in "+time+" milliseconds");
	}
	public static CreativeTabs tabTomsModEnergy = new CreativeTabs("tabTomsModEnergy"){

		@Override
		public Item getTabIconItem() {
			return new ItemStack(EnergyInit.Generator).getItem();
		}

	};
	@SideOnly(Side.CLIENT)
	public static void registerRenders(){
		log.info("Loading Renderers");
		/*for(int i = 0;i<12;i++){
			CoreInit.registerRender(Item.getItemFromBlock(Generator), i, "tomsmodenergy:generator");
		}
		for(int i = 0;i<14;i++){
			CoreInit.registerRender(Item.getItemFromBlock(FusionController), i, "tomsmodenergy:FusionController");
		}
		CoreInit.registerRender(Item.getItemFromBlock(FusionInjector), 0, "tomsmodenergy:FusionInjector");
		CoreInit.registerRender(Item.getItemFromBlock(FusionInjector), 1, "tomsmodenergy:FusionInjector");
		for(int i = 0;i<6;i++){
			CoreInit.registerRender(Item.getItemFromBlock(MK1Laser), i, "tomsmodenergy:mk1Laser");
			CoreInit.registerRender(Item.getItemFromBlock(MK2Laser), i, "tomsmodenergy:mk2Laser");
			CoreInit.registerRender(Item.getItemFromBlock(MK3Laser), i, "tomsmodenergy:mk3Laser");
			CoreInit.registerRender(Item.getItemFromBlock(FusionFluidExtractor), i, "tomsmodenergy:FusionFluidExtractor");
		}*/
		CoreInit.registerRender(Item.getItemFromBlock(wirelessCharger), 0, "tomsmodenergy:wirelessCharger");
		CoreInit.registerRender(Item.getItemFromBlock(wirelessCharger), 1, "tomsmodenergy:wirelessCharger");
		/*for(int i = 0;i<TransformerType.values().length;i++){
			CoreInit.registerRender(Item.getItemFromBlock(transformer), i);
		}*/
	}
	@EventHandler
	public static void construction(FMLConstructionEvent event){
		CoreInit.modids.add(modid);
	}
}
