package com.tom.storage;

import static com.tom.core.CoreInit.addBlockToGameRegistry;
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
import com.tom.core.CoreInit;
import com.tom.core.IMod;
import com.tom.lib.Configs;
import com.tom.storage.block.AdvStorageSystemRouter;
import com.tom.storage.block.BasicTerminal;
import com.tom.storage.block.BlockAssembler;
import com.tom.storage.block.BlockCraftingTerminal;
import com.tom.storage.block.BlockImportBus;
import com.tom.storage.block.BlockInterface;
import com.tom.storage.block.BlockPatternTerminal;
import com.tom.storage.block.BlockStorageBus;
import com.tom.storage.block.BlockStorageNetworkController;
import com.tom.storage.block.BlockTankAdv;
import com.tom.storage.block.BlockTankBasic;
import com.tom.storage.block.BlockTankElite;
import com.tom.storage.block.BlockTankUltimate;
import com.tom.storage.block.CraftingController;
import com.tom.storage.block.Drive;
import com.tom.storage.block.EnergyAcceptor;
import com.tom.storage.block.LimitableChest;
import com.tom.storage.block.StorageSystemRouter;
import com.tom.storage.handler.CacheRegistry;
import com.tom.storage.item.EncodedPattern;
import com.tom.storage.item.ItemBoard;
import com.tom.storage.item.ItemCard;
import com.tom.storage.item.ItemChipset;
import com.tom.storage.item.ItemFan;
import com.tom.storage.item.ItemHeatSink;
import com.tom.storage.item.ItemPartBasicTerminal;
import com.tom.storage.item.ItemPartCraftingTerminal;
import com.tom.storage.item.ItemPartPatternTerminal;
import com.tom.storage.item.ItemPartTerminal;
import com.tom.storage.item.ItemProcessor;
import com.tom.storage.item.ItemRAM;
import com.tom.storage.item.ItemStorageCell;
import com.tom.storage.item.ItemTowerBoard;
import com.tom.storage.multipart.PartBasicTerminal;
import com.tom.storage.multipart.PartCraftingTerminal;
import com.tom.storage.multipart.PartExportBus;
import com.tom.storage.multipart.PartImportBus;
import com.tom.storage.multipart.PartPatternTerminal;
import com.tom.storage.multipart.PartStorageBus;
import com.tom.storage.multipart.PartStorageNetworkCable;
import com.tom.storage.multipart.block.BlockExportBus;
import com.tom.storage.multipart.block.StorageNetworkCable;
import com.tom.storage.multipart.block.StorageNetworkCable.CableColor;
import com.tom.storage.tileentity.TileEntityAdvRouter;
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
import com.tom.storage.tileentity.TileEntityRouter;
import com.tom.storage.tileentity.TileEntityStorageNetworkController;
import com.tom.storage.tileentity.TileEntityUltimateTank;

@Mod(modid = StorageInit.modid, name = StorageInit.modName, version = Configs.version, dependencies = Configs.coreDependencies)
public class StorageInit {
	public static final String modid = Configs.ModidL + "|storage";
	public static final String modName = Configs.ModName + " Storage";
	public static final Logger log = LogManager.getLogger(modName);
	public static Block exportBus, importBus, partInterface, storageBus, cable;
	public static Block limitableChest, assembler, tankBasic, tankAdv, tankElite, tankUltimate, quantumTank,
			storageController, router, advRouter, smallFluixReactor;
	public static BlockGridDevice drive, basicTerminal, energyAcceptor, blockInterface, craftingController,
			patternTerminal, craftingTerminal;
	public static ItemStorageCell itemStorageCell;
	public static Item card, craftingPattern, processor, ram, board, chipset, towerBoard, heatSink, fan;
	public static ItemPartTerminal partTerminal, partCraftingTerminal, partPatternTerminal;

	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent) {
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		/** Items */
		card = new ItemCard().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.storagecard");
		itemStorageCell = new ItemStorageCell().setCreativeTab(tabTomsModStorage);
		craftingPattern = new EncodedPattern().setUnlocalizedName("tm.craftingPattern").setCreativeTab(tabTomsModStorage);
		processor = new ItemProcessor().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.processor").setMaxStackSize(1);
		ram = new ItemRAM().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.ram").setMaxStackSize(4);
		board = new ItemBoard().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.mainboard").setMaxStackSize(1);
		towerBoard = new ItemTowerBoard().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.towerboard").setMaxStackSize(1);
		chipset = new ItemChipset().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.chipset");
		heatSink = new ItemHeatSink().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.heatsink");
		fan = new ItemFan().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.pfan");
		/** TileEntities */
		limitableChest = new LimitableChest().setUnlocalizedName("limitableChest").setCreativeTab(tabTomsModStorage);
		drive = new Drive().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tms.drive");
		basicTerminal = new BasicTerminal().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.basicTerminal.block");
		energyAcceptor = new EnergyAcceptor().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.energyAcceptor");
		cable = new StorageNetworkCable().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.cable");
		importBus = new BlockImportBus().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.importBus");
		exportBus = new BlockExportBus().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.exportBus");
		// partInterface = new
		// BlockPartInterface().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.partInterface");
		storageBus = new BlockStorageBus().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.storageBus");
		craftingController = new CraftingController().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.craftingController");
		blockInterface = new BlockInterface().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.interface");
		patternTerminal = new BlockPatternTerminal().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.patternTerminal.block");
		assembler = new BlockAssembler().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.assembler");
		tankBasic = new BlockTankBasic().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.tank.basic");
		tankAdv = new BlockTankAdv().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.tank.adv");
		tankElite = new BlockTankElite().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.tank.elite");
		tankUltimate = new BlockTankUltimate().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.tank.ultimate");
		craftingTerminal = new BlockCraftingTerminal().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.craftingTerminal.block");
		partTerminal = new ItemPartBasicTerminal().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.basicTerminal.part");
		partCraftingTerminal = new ItemPartCraftingTerminal().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.craftingTerminal.part");
		partPatternTerminal = new ItemPartPatternTerminal().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.patternTerminal.part");
		storageController = new BlockStorageNetworkController().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.storageController");
		router = new StorageSystemRouter().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.storageRouter");
		advRouter = new AdvStorageSystemRouter().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.advStorageRouter");
		// smallFluixReactor = new
		// SmallFluixReactorController().setCreativeTab(tabTomsModStorage).setUnlocalizedName("tm.smallfluixreactor");
		/** Registry */
		/** Items */
		registerItem(itemStorageCell);
		registerItem(card);
		registerItem(craftingPattern);
		registerItem(processor);
		registerItem(ram);
		registerItem(board);
		registerItem(towerBoard);
		registerItem(chipset);
		registerItem(heatSink);
		registerItem(fan);
		/** Blocks */
		registerBlock(limitableChest);
		addBlockToGameRegistry(basicTerminal, basicTerminal.getUnlocalizedName().substring(5));
		registerBlock(drive);
		registerBlock(energyAcceptor);
		registerBlock(craftingController);
		registerBlock(blockInterface);
		addBlockToGameRegistry(patternTerminal, patternTerminal.getUnlocalizedName().substring(5));
		registerBlock(assembler);
		registerBlock(tankBasic);
		registerBlock(tankAdv);
		registerBlock(tankElite);
		registerBlock(tankUltimate);
		addBlockToGameRegistry(craftingTerminal, craftingTerminal.getUnlocalizedName().substring(5));
		registerBlock(storageController);
		registerBlock(router);
		registerBlock(advRouter);
		/** Multiparts */
		registerBlock(cable);
		registerBlock(importBus);
		registerBlock(exportBus);
		// registerBlock(partInterface,
		// partInterface.getUnlocalizedName().substring(5));
		registerBlock(storageBus);
		addBlockToGameRegistry(partTerminal, partTerminal.getUnlocalizedName().substring(5));
		addBlockToGameRegistry(partCraftingTerminal, partCraftingTerminal.getUnlocalizedName().substring(5));
		addBlockToGameRegistry(partPatternTerminal, partPatternTerminal.getUnlocalizedName().substring(5));
		/** TileEntities */
		GameRegistry.registerTileEntity(TileEntityLimitableChest.class, Configs.Modid + ":limitableChest");
		GameRegistry.registerTileEntity(TileEntityBasicTerminal.class, Configs.Modid + ":terminal");
		GameRegistry.registerTileEntity(TileEntityDrive.class, Configs.Modid + ":drive");
		GameRegistry.registerTileEntity(TileEntityEnergyAcceptor.class, Configs.Modid + ":energyAcceptor");
		GameRegistry.registerTileEntity(TileEntityInterface.class, Configs.Modid + ":interface");
		GameRegistry.registerTileEntity(TileEntityCraftingController.class, Configs.Modid + ":craftingController");
		GameRegistry.registerTileEntity(TileEntityPatternTerminal.class, Configs.Modid + ":patternTerminal");
		GameRegistry.registerTileEntity(TileEntityAssembler.class, Configs.Modid + ":mAssembler");
		GameRegistry.registerTileEntity(TileEntityBasicTank.class, Configs.Modid + ":basicTank");
		GameRegistry.registerTileEntity(TileEntityAdvTank.class, Configs.Modid + ":advTank");
		GameRegistry.registerTileEntity(TileEntityEliteTank.class, Configs.Modid + ":eliteTank");
		GameRegistry.registerTileEntity(TileEntityUltimateTank.class, Configs.Modid + ":ultimateTank");
		GameRegistry.registerTileEntity(TileEntityCraftingTerminal.class, Configs.Modid + ":craftingTerminal");
		GameRegistry.registerTileEntity(TileEntityStorageNetworkController.class, Configs.Modid + ":storageController");
		GameRegistry.registerTileEntity(TileEntityRouter.class, Configs.Modid + ":storageRouter");
		GameRegistry.registerTileEntity(TileEntityAdvRouter.class, Configs.Modid + ":advStorageRouter");
		/** Multiparts */
		GameRegistry.registerTileEntity(PartStorageNetworkCable.class, Configs.Modid + ":part:storageNetworkCable");
		GameRegistry.registerTileEntity(PartExportBus.class, Configs.Modid + ":part:exportBus");
		GameRegistry.registerTileEntity(PartImportBus.class, Configs.Modid + ":part:importBus");
		GameRegistry.registerTileEntity(PartStorageBus.class, Configs.Modid + ":part:storageBus");
		GameRegistry.registerTileEntity(PartBasicTerminal.class, Configs.Modid + ":part:basicTerminal");
		GameRegistry.registerTileEntity(PartCraftingTerminal.class, Configs.Modid + ":part:craftingTerminal");
		GameRegistry.registerTileEntity(PartPatternTerminal.class, Configs.Modid + ":part:patternTerminal");
		CacheRegistry.init();
		hadPreInit = true;
		CoreInit.tryLoadAfterPreInit(log);
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in " + time + " milliseconds");
	}

	public static CreativeTabs tabTomsModStorage = new CreativeTabs("tabTomsModStorage") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(cable, 1, StorageNetworkCable.getMeta(StorageNetworkCable.CableType.NORMAL, CableColor.BLUE));
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
