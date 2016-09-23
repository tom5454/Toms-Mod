package com.tom.storage;

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

import com.tom.api.block.BlockGridDevice;
import com.tom.api.item.MultipartItem;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;
import com.tom.storage.block.BasicTerminal;
import com.tom.storage.block.BlockAssembler;
import com.tom.storage.block.BlockCraftingTerminal;
import com.tom.storage.block.BlockInterface;
import com.tom.storage.block.BlockPatternTerminal;
import com.tom.storage.block.BlockTankAdv;
import com.tom.storage.block.BlockTankBasic;
import com.tom.storage.block.BlockTankElite;
import com.tom.storage.block.BlockTankUltimate;
import com.tom.storage.block.CraftingController;
import com.tom.storage.block.Drive;
import com.tom.storage.block.EnergyAcceptor;
import com.tom.storage.block.LimitableChest;
import com.tom.storage.item.EncodedPattern;
import com.tom.storage.item.ExportBus;
import com.tom.storage.item.ImportBus;
import com.tom.storage.item.ItemPartCraftingTerminal;
import com.tom.storage.item.ItemPartInterface;
import com.tom.storage.item.ItemPartPatternTerminal;
import com.tom.storage.item.ItemPartTerminal;
import com.tom.storage.item.ItemStorageCell;
import com.tom.storage.item.StorageBus;
import com.tom.storage.item.StorageNetworkCable;
import com.tom.storage.multipart.PartExportBus;
import com.tom.storage.multipart.PartImportBus;
import com.tom.storage.multipart.PartStorageBus;
import com.tom.storage.multipart.PartStorageNetworkCable;
import com.tom.storage.multipart.StorageNetworkGrid.CacheRegistry;
import com.tom.storage.tileentity.TileEntityAdvTank;
import com.tom.storage.tileentity.TileEntityAssembler;
import com.tom.storage.tileentity.TileEntityBasicTank;
import com.tom.storage.tileentity.TileEntityBasicTerminal;
import com.tom.storage.tileentity.TileEntityCraftingController;
import com.tom.storage.tileentity.TileEntityCraftingTerminal;
import com.tom.storage.tileentity.TileEntityDrive;
import com.tom.storage.tileentity.TileEntityEliteTank;
import com.tom.storage.tileentity.TileEntityEnergyAcceptor;
import com.tom.storage.tileentity.TileEntityInterface;
import com.tom.storage.tileentity.TileEntityLimitableChest;
import com.tom.storage.tileentity.TileEntityPatternTerminal;
import com.tom.storage.tileentity.TileEntityUltimateTank;

@Mod(modid = StorageInit.modid,name = "Tom's Mod Storage",version = Configs.version, dependencies = Configs.coreDependencies)
public class StorageInit {
	public static final String modid = Configs.Modid + "|Storage";
	public static Logger log = LogManager.getLogger(modid);
	public static MultipartItem cable, exportBus, importBus, partInterface, storageBus;
	public static Block limitableChest, assembler, tankBasic, tankAdv, tankElite, tankUltimate, quantumTank;
	public static BlockGridDevice drive, basicTerminal, energyAcceptor, blockInterface, craftingController, patternTerminal, craftingTerminal;
	public static ItemStorageCell itemStorageCell;
	public static Item speedCard, craftingPattern, craftingCard;
	public static Item partTerminal, partCraftingTerminal, partPatternTerminal;
	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent){
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		limitableChest = new LimitableChest().setUnlocalizedName("limitableChest").setCreativeTab(tabTomsModStorage);
		cable = new StorageNetworkCable().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.cable");
		drive = new Drive().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tms.drive");
		basicTerminal = new BasicTerminal().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.basicTerminal.block");
		energyAcceptor = new EnergyAcceptor().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.energyAcceptor");
		itemStorageCell = new ItemStorageCell().setCreativeTab(tabTomsModStorage);
		importBus = new ImportBus().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.importBus");
		exportBus = new ExportBus().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.exportBus");
		partInterface = new ItemPartInterface().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.partInterface");
		storageBus = new StorageBus().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.storageBus");
		speedCard = new Item().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.speedCard");
		craftingController = new CraftingController().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.craftingController");
		//pattern = new Item().setUnlocalizedName("tm.emptyPattern").setCreativeTab(tabTomsModStorage);
		craftingPattern = new EncodedPattern().setUnlocalizedName("tm.craftingPattern").setCreativeTab(tabTomsModStorage);
		blockInterface = new BlockInterface().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.interface");
		patternTerminal = new BlockPatternTerminal().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.patternTerminal.block");
		craftingCard = new Item().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.craftingCard");
		assembler = new BlockAssembler().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.assembler");
		tankBasic = new BlockTankBasic().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.tank.basic");
		tankAdv = new BlockTankAdv().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.tank.adv");
		tankElite = new BlockTankElite().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.tank.elite");
		tankUltimate = new BlockTankUltimate().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.tank.ultimate");
		craftingTerminal = new BlockCraftingTerminal().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.craftingTerminal.block");
		partTerminal = new ItemPartTerminal().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.basicTerminal.part");
		partCraftingTerminal = new ItemPartCraftingTerminal().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.craftingTerminal.part");
		partPatternTerminal = new ItemPartPatternTerminal().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.patternTerminal.part");
		registerBlock(limitableChest, limitableChest.getUnlocalizedName().substring(5));
		registerBlock(basicTerminal, basicTerminal.getUnlocalizedName().substring(5));
		registerBlock(drive, drive.getUnlocalizedName().substring(5));
		registerBlock(energyAcceptor, energyAcceptor.getUnlocalizedName().substring(5));
		registerBlock(craftingController, craftingController.getUnlocalizedName().substring(5));
		registerBlock(blockInterface, blockInterface.getUnlocalizedName().substring(5));
		registerItem(itemStorageCell, itemStorageCell.getUnlocalizedName().substring(5));
		registerItem(speedCard, speedCard.getUnlocalizedName().substring(5));
		registerItem(craftingCard, craftingCard.getUnlocalizedName().substring(5));
		registerBlock(patternTerminal, patternTerminal.getUnlocalizedName().substring(5));
		registerBlock(assembler, assembler.getUnlocalizedName().substring(5));
		registerItem(craftingPattern, craftingPattern.getUnlocalizedName().substring(5));
		registerBlock(tankBasic, tankBasic.getUnlocalizedName().substring(5));
		registerBlock(tankAdv, tankAdv.getUnlocalizedName().substring(5));
		registerBlock(tankElite, tankElite.getUnlocalizedName().substring(5));
		registerBlock(tankUltimate, tankUltimate.getUnlocalizedName().substring(5));
		registerBlock(craftingTerminal, craftingTerminal.getUnlocalizedName().substring(5));
		//registerItem(pattern, pattern.getUnlocalizedName().substring(5));
		//registerItem(encodedPattern, encodedPattern.getUnlocalizedName().substring(5));
		GameRegistry.registerTileEntity(TileEntityLimitableChest.class, Configs.Modid+":limitableChest");
		GameRegistry.registerTileEntity(TileEntityBasicTerminal.class, Configs.Modid+":terminal");
		GameRegistry.registerTileEntity(TileEntityDrive.class, Configs.Modid+":drive");
		GameRegistry.registerTileEntity(TileEntityEnergyAcceptor.class, Configs.Modid+":energyAcceptor");
		GameRegistry.registerTileEntity(TileEntityInterface.class, Configs.Modid+":interface");
		GameRegistry.registerTileEntity(TileEntityCraftingController.class, Configs.Modid+":craftingController");
		GameRegistry.registerTileEntity(TileEntityPatternTerminal.class, Configs.Modid+":patternTerminal");
		GameRegistry.registerTileEntity(TileEntityAssembler.class, Configs.Modid+":mAssembler");
		GameRegistry.registerTileEntity(TileEntityBasicTank.class, Configs.Modid+":basicTank");
		GameRegistry.registerTileEntity(TileEntityAdvTank.class, Configs.Modid+":advTank");
		GameRegistry.registerTileEntity(TileEntityEliteTank.class, Configs.Modid+":eliteTank");
		GameRegistry.registerTileEntity(TileEntityUltimateTank.class, Configs.Modid+":ultimateTank");
		GameRegistry.registerTileEntity(TileEntityCraftingTerminal.class, Configs.Modid+":craftingTerminal");
		CoreInit.registerMultipart(cable, PartStorageNetworkCable.class, "tomsmodstorage");
		CoreInit.registerMultipart(importBus, PartImportBus.class, "tomsmodstorage");
		CoreInit.registerMultipart(storageBus, PartStorageBus.class, "tomsmodstorage");
		CoreInit.registerMultipart(exportBus, PartExportBus.class, "tomsmodstorage");
		registerItem(partTerminal, partTerminal.getUnlocalizedName().substring(5));
		registerItem(partCraftingTerminal, partCraftingTerminal.getUnlocalizedName().substring(5));
		registerItem(partPatternTerminal, partPatternTerminal.getUnlocalizedName().substring(5));
		CacheRegistry.init();
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in "+time+" milliseconds");
	}
	public static CreativeTabs tabTomsModStorage = new CreativeTabs("tabTomsModStorage"){

		@Override
		public Item getTabIconItem() {
			return new ItemStack(cable).getItem();
		}

	};
	@EventHandler
	public static void construction(FMLConstructionEvent event){
		CoreInit.modids.add(modid);
	}
}
