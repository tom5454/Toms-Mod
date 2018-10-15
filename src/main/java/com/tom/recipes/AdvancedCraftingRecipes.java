package com.tom.recipes;

import static com.tom.api.energy.EnergyType.*;
import static com.tom.recipes.handler.AdvancedCraftingHandler.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.tom.api.energy.EnergyType;
import com.tom.api.research.IScanningInformation;
import com.tom.api.research.Research;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.core.research.ResearchLoader;
import com.tom.energy.EnergyInit;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.BlockComponents.ComponentVariants;
import com.tom.factory.block.BlockMachineBase;
import com.tom.factory.tileentity.TileEntityMachineBase;
import com.tom.recipes.handler.AdvancedCraftingHandler.CraftingLevel;
import com.tom.storage.StorageInit;
import com.tom.transport.TransportInit;

public class AdvancedCraftingRecipes {// CraftingRecipes OreDict
	public static Map<BlockMachineBase, Entry<Block, Block>> machines = new HashMap<>();
	public static final List<Research> EMPTY = Collections.emptyList();
	public static final VirtualStack BASIC_CIRCUIT_COMPONENT = new VirtualStack(CoreInit.circuitComponent, 1, 0, "\"id\":\"basic\"");
	public static final VirtualStack BASIC_CIRCUIT = new VirtualStack(CoreInit.circuit, 1, 0, "\"id\":\"basicecirc\"");
	public static final VirtualStack NORMAL_CIRCUIT = new VirtualStack(CoreInit.circuit, 1, 1, "id:\"normalecirc\"");
	public static final VirtualStack ADVANCED_CIRCUIT = new VirtualStack(CoreInit.circuit, 1, 2, "id:\"advecirc\"");
	public static final VirtualStack NORMAL_CIRCUIT_COMPONENT = new VirtualStack(CoreInit.circuitComponent, 1, 1, "\"id\":\"normal\"");
	public static final VirtualStack ADVANCED_CIRCUIT_PLATE = new VirtualStack(CoreInit.circuitPanel, 1, 1, null);
	public static final VirtualStack ADVANCED_CIRCUIT_COMPONENT = new VirtualStack(CoreInit.circuitComponent, 1, 2, "\"id\":\"adv\"");
	public static final VirtualStack BASIC_CIRCUIT_PLATE = new VirtualStack(CoreInit.circuitPanel, 1, 0, null);
	public static final VirtualStack UNASSEMBLED_BASIC_CIRCUIT_PANEL = new VirtualStack(CoreInit.circuitUnassembled, 1, 0, "id:\"basicecirc\"");
	public static final VirtualStack UNASSEMBLED_NORMAL_CIRCUIT_PANEL = new VirtualStack(CoreInit.circuitUnassembled, 1, 0, "id:\"normalecirc\"");
	public static final VirtualStack UNASSEMBLED_ADVANCED_CIRCUIT_PANEL = new VirtualStack(CoreInit.circuitUnassembled, 1, 1, "id:\"advecirc\"");
	public static final VirtualStack RAW_BASIC_CIRCUIT_PANEL = new VirtualStack(CoreInit.circuitRaw, 1, 0, "id:\"basicecirc\"");
	public static final VirtualStack RAW_NORMAL_CIRCUIT_PANEL = new VirtualStack(CoreInit.circuitRaw, 1, 0, "id:\"normalecirc\"");
	public static final VirtualStack RAW_ADV_CIRCUIT_PANEL = new VirtualStack(CoreInit.circuitRaw, 1, 1, "id:\"advecirc\"");
	public static final VirtualStack PHOTOACTIVE_BASIC_CIRCUIT_PLATE = new VirtualStack(CoreInit.circuitPanelP, 1, 0, null);
	public static final VirtualStack PHOTOACTIVE_ADVANCED_CIRCUIT_PLATE = new VirtualStack(CoreInit.circuitPanelP, 1, 1, null);
	public static final VirtualStack BLUEPRINT_BASIC_CIRCUIT = new VirtualStack(CoreInit.blueprint, 1, 0, "id:\"basiccirc\"");
	public static final VirtualStack BLUEPRINT_NORMAL_CIRCUIT = new VirtualStack(CoreInit.blueprint, 1, 0, "id:\"normalcirc\"");
	public static final VirtualStack BLUEPRINT_ADVANCED_CIRCUIT = new VirtualStack(CoreInit.blueprint, 1, 0, "id:\"advcirc\"");
	public static final VirtualStack BASIC_CHIPSET = new VirtualStack(CoreInit.chipset, 1, 0, null);
	public static final VirtualStack ADVANCED_CHIPSET = new VirtualStack(CoreInit.chipset, 1, 1, null);

	public static void init() {
		int a2o4 = /*Config.enableHardRecipes ? 2 : */4;
		int a3o6 = /*Config.enableHardRecipes ? 3 : */6;
		int a1o2 = /*Config.enableHardRecipes ? 1 : */2;
		addRecipe(CraftingMaterial.ACID_PAPER.getStackNormal(2), 100, EMPTY, new ItemStack(Items.BUCKET), CraftingLevel.BASIC_WOODEN, new Object[]{"W D", "SPP", "RPP", 'W', Items.WATER_BUCKET, 'D', new ItemStack(Items.DYE, 1, 15), 'S', Items.SPIDER_EYE, 'P', Items.PAPER, 'R', Items.ROTTEN_FLESH});
		// addRecipe(CraftingMaterial.FLINT_HAMMER_HEAD.getStackNormal(), 100,
		// getResearchList(ResearchLoader.hammer), null,
		// CraftingLevel.BASIC_WOODEN, new Object[]{"FFF", "FIF", " F ", 'F',
		// "itemFlint", 'I', "ingotIron"});
		addRecipe(CraftingMaterial.FLINT_HAMMER_HEAD.getStackNormal(), 100, getResearchList(ResearchLoader.hammer), null, CraftingLevel.BASIC_WOODEN, new Object[]{"FFF", "FIF", " F ", 'F', "itemFlint", 'I', "ingotCopper"});
		addRecipe(CraftingMaterial.FLINT_HAMMER_HEAD.getStackNormal(), 100, getResearchList(ResearchLoader.hammer), null, CraftingLevel.BASIC_WOODEN, new Object[]{"FFF", "FIF", " F ", 'F', "itemFlint", 'I', "ingotTin"});
		addShapelessRecipe(new ItemStack(CoreInit.mortarAndPestle), 200, getResearchList(ResearchLoader.mortar), null, CraftingLevel.BASIC_WOODEN, new Object[]{CraftingMaterial.STONE_BOWL.getStack(), "itemFlint"});
		addRecipe(new ItemStack(FactoryInit.basicBoiler), 300, getResearchList(ResearchLoader.basicBoiler), null, CraftingLevel.BASIC_WOODEN, new Object[]{"PPP", "PGP", "BFB", 'F', Blocks.FURNACE, 'P', TMResource.BRONZE.getStackName(Type.PLATE), 'B', Blocks.BRICK_BLOCK, 'G', "blockGlass"});
		addRecipe(new ItemStack(FactoryInit.advBoiler), 500, getResearchList(ResearchLoader.steelBoiler), null, CraftingLevel.BRONZE, new Object[]{"PbP", "PGP", "BFB", 'F', Blocks.FURNACE, 'P', TMResource.STEEL.getStackName(Type.PLATE), 'B', Blocks.BRICK_BLOCK, 'G', FactoryInit.basicBoiler, 'b', FactoryInit.steelBoiler});
		addRecipe(new ItemStack(FactoryInit.steamCrusher), 300, getResearchList(ResearchLoader.basicBronzeMachines), null, CraftingLevel.BASIC_WOODEN, new Object[]{"DFD", "-C-", "P-P", 'D', TMResource.DIAMOND.getStackName(Type.GEM), 'P', "blockPiston", 'C', CoreInit.MachineFrameBronze, 'F', "itemFlint", '-', CraftingMaterial.BRONZE_PIPE.getStack()});
		addRecipe(new ItemStack(FactoryInit.steamFurnace), 300, getResearchList(ResearchLoader.basicBronzeMachines), null, CraftingLevel.BASIC_WOODEN, new Object[]{"PPP", "PFP", "B-B", 'B', Blocks.BRICK_BLOCK, 'P', TMResource.BRONZE.getStackName(Type.PLATE), 'F', Blocks.FURNACE, '-', CraftingMaterial.BRONZE_PIPE.getStack()});
		addRecipe(new ItemStack(FactoryInit.steamAlloySmelter), 350, getResearchList(ResearchLoader.bronzeAlloySmelter), null, CraftingLevel.BASIC_WOODEN, new Object[]{"PPP", "FCF", "B-B", 'B', Blocks.BRICK_BLOCK, 'P', TMResource.BRONZE.getStackName(Type.PLATE), 'F', Blocks.FURNACE, 'C', CoreInit.MachineFrameBronze, '-', CraftingMaterial.BRONZE_PIPE.getStack()});
		addRecipe(new ItemStack(FactoryInit.steamPlateBlender), 350, getResearchList(ResearchLoader.bronzePlateBlender), null, CraftingLevel.BASIC_WOODEN, new Object[]{"PSP", "-C-", "BBB", 'B', Blocks.BRICK_BLOCK, 'P', TMResource.BRONZE.getStackName(Type.PLATE), 'S', TMResource.BRONZE.getBlockOreDictName(), 'C', CoreInit.MachineFrameBronze, '-', CraftingMaterial.BRONZE_PIPE.getStack()});
		addShapelessRecipe(CraftingMaterial.REFINED_CLAY.getStackNormal(4), 50, getResearchList(ResearchLoader.refinedBricks), null, CraftingLevel.BRONZE, new Object[]{Items.CLAY_BALL, Items.CLAY_BALL, CraftingMaterial.NETHERRACK_DUST.getStack(), Blocks.SAND, Blocks.SAND, TMResource.COAL.getStackName(Type.DUST), TMResource.IRON.getStackName(Type.DUST), TMResource.OBSIDIAN.getStackName(Type.DUST), Blocks.GRAVEL});
		addRecipe(BASIC_CIRCUIT.get(), 500, getResearchList(ResearchLoader.soldering), null, CraftingLevel.SOLDERING_STATION, new Object[]{" C ", "RPR", "-R-", 'C', BASIC_CIRCUIT_COMPONENT.get(), 'R', Items.REDSTONE, 'P', UNASSEMBLED_BASIC_CIRCUIT_PANEL.get(), '-', TMResource.COPPER.getStackName(Type.CABLE)});
		addRecipe(new ItemStack(FactoryInit.steamSolderingStation), 450, getResearchList(ResearchLoader.soldering), null, CraftingLevel.BRONZE, new Object[]{"PSP", "-C-", "B-B", 'B', Blocks.BRICK_BLOCK, 'P', TMResource.BRONZE.getStackName(Type.PLATE), 'C', CoreInit.MachineFrameBronze, '-', CraftingMaterial.BRONZE_PIPE.getStack(), 'S', TMResource.STEEL.getStackName(Type.PLATE)});
		addRecipe(CraftingMaterial.RAW_SILICON.getStackNormal(), 60, getResearchList(ResearchLoader.rubberProcessing), null, CraftingLevel.BRONZE, new Object[]{"SSS", "SCS", "SSS", 'C', "blockCoal", 'S', Blocks.SAND});
		addRecipe(BASIC_CIRCUIT_COMPONENT.get(), 600, getResearchList(ResearchLoader.soldering), null, CraftingLevel.BRONZE, new Object[]{"RSR", "-P-", "RBR", 'R', TMResource.REDSTONE.getStackName(Type.PLATE), 'S', CraftingMaterial.SILICON_PLATE.getStack(), '-', TMResource.COPPER.getStackName(Type.CABLE), 'P', TMResource.IRON.getStackName(Type.PLATE), 'B', CraftingMaterial.RUBBER.getStack()});
		addRecipe(CraftingMaterial.RAW_CICRUIT_BOARD.getStackNormal(2), 100, getResearchList(ResearchLoader.soldering), null, CraftingLevel.BRONZE, new Object[]{"CCC", "GGG", "RRR", 'C', TMResource.COPPER.getStackName(Type.PLATE), 'G', CraftingMaterial.GLASS_MESH.getStack(), 'R', CraftingMaterial.RUBBER.getStack()});
		addRecipe(new ItemStack(FactoryInit.cokeOvenWall, a2o4), 600, getResearchList(ResearchLoader.cokeOven), null, CraftingLevel.BRONZE, new Object[]{"BSB", "SRS", "BSB", 'S', Blocks.SAND, 'B', Items.BRICK, 'R', CraftingMaterial.REFINED_BRICK.getStack()});
		addRecipe(new ItemStack(FactoryInit.blastFurnaceWall, a2o4), 600, getResearchList(ResearchLoader.blastFurnace), null, CraftingLevel.BRONZE, new Object[]{"BSB", "SRS", "BSB", 'S', Blocks.SOUL_SAND, 'B', Blocks.NETHER_BRICK, 'R', CraftingMaterial.REFINED_BRICK.getStack()});
		addRecipe(new ItemStack(FactoryInit.cokeOven), 600, getResearchList(ResearchLoader.cokeOven), null, CraftingLevel.BRONZE, new Object[]{"BSB", "SRS", "BSB", 'S', CraftingMaterial.REFINED_BRICK.getStack(), 'B', FactoryInit.cokeOvenWall, 'R', Blocks.SAND});
		addRecipe(new ItemStack(FactoryInit.blastFurnace), 600, getResearchList(ResearchLoader.blastFurnace), null, CraftingLevel.BRONZE, new Object[]{"BSB", "SRS", "BSB", 'S', CraftingMaterial.REFINED_BRICK.getStack(), 'B', FactoryInit.blastFurnaceWall, 'R', Blocks.SOUL_SAND});
		addRecipe(new ItemStack(EnergyInit.multimeter), 800, getResearchList(ResearchLoader.multimeter), null, CraftingLevel.SOLDERING_STATION, new Object[]{"IGI", "-C-", "IPI", 'C', Items.COMPASS, '-', TMResource.COPPER.getStackName(Type.CABLE), 'I', TMResource.IRON.getStackName(Type.PLATE), 'P', BASIC_CIRCUIT.get(), 'G', "paneGlass"});
		addRecipe(new ItemStack(EnergyInit.lvCable, 3), 850, getResearchList(ResearchLoader.lvCable), null, CraftingLevel.BRONZE, new Object[]{"R-R", "R-R", "R-R", 'R', CraftingMaterial.RUBBER.getStack(), '-', TMResource.COPPER.getStackName(Type.CABLE)});
		addRecipe(new ItemStack(EnergyInit.lvCable, 3), 850, getResearchList(ResearchLoader.lvCable), null, CraftingLevel.BRONZE, new Object[]{"RRR", "---", "RRR", 'R', CraftingMaterial.RUBBER.getStack(), '-', TMResource.COPPER.getStackName(Type.CABLE)});
		addRecipe(new ItemStack(EnergyInit.batteryBox), 900, getResearchList(ResearchLoader.batteryBox), null, CraftingLevel.BRONZE, new Object[]{"W-W", "BBB", "WWW", 'W', "plankWood", '-', TMResource.COPPER.getStackName(Type.CABLE), 'B', EnergyInit.battery});
		addRecipe(new ItemStack(FactoryInit.crusher, 1, getMeta(LV)), 1000, getResearchList(ResearchLoader.basicLvMachines), null, CraftingLevel.BRONZE, new Object[]{"SHS", "ECE", "SES", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'E', BASIC_CIRCUIT.get(), 'C', CoreInit.MachineFrameBasic, 'H', CraftingMaterial.WOLFRAMIUM_GRINDER.getStack()});
		addRecipe(CraftingMaterial.WOLFRAMIUM_GRINDER.getStackNormal(), 1000, getResearchList(ResearchLoader.basicLvMachines), null, CraftingLevel.BRONZE, new Object[]{"WSW", "SBS", "WSW", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'W', TMResource.WOLFRAM.getStackName(Type.PLATE), 'B', TMResource.STEEL.getBlockOreDictName()});
		//addRecipe(CraftingMaterial.DIAMOND_GRINDER.getStackNormal(), 1000, getResearchList(ResearchLoader.basicLvMachines), null, CraftingLevel.BASIC, new Object[]{"WSW", "SBS", "WSW", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'W', TMResource.WOLFRAM.getStackName(Type.PLATE), 'B', TMResource.STEEL.getBlockOreDictName()});
		addRecipe(new ItemStack(FactoryInit.electricFurnace, 1, getMeta(LV)), 1000, getResearchList(ResearchLoader.basicLvMachines), null, CraftingLevel.BRONZE, new Object[]{"IEI", "RFR", "ICI", 'E', BASIC_CIRCUIT.get(), 'C', CoreInit.MachineFrameBasic, 'R', Items.REDSTONE, 'F', FactoryInit.advSteamFurnace, 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack()});
		addRecipe(new ItemStack(FactoryInit.wireMill, 1, getMeta(LV)), 1000, getResearchList(ResearchLoader.wireProcessing), null, CraftingLevel.BRONZE, new Object[]{"IDI", "ECE", "IPI", 'P', "blockPiston", 'E', BASIC_CIRCUIT.get(), 'C', CoreInit.MachineFrameBasic, 'D', Items.DIAMOND, 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack()});
		addRecipe(new ItemStack(CoreInit.emptyWireCoil, 8), 100, getResearchList(ResearchLoader.wireProcessing), null, CraftingLevel.BRONZE, new Object[]{"III", " I ", "III", 'I', TMResource.IRON.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(CoreInit.emptyWireCoil, 6), 110, getResearchList(ResearchLoader.wireProcessing), null, CraftingLevel.BRONZE, new Object[]{"III", " I ", "III", 'I', TMResource.COPPER.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(CoreInit.emptyWireCoil, 6), 110, getResearchList(ResearchLoader.wireProcessing), null, CraftingLevel.BRONZE, new Object[]{"III", " I ", "III", 'I', TMResource.TIN.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(CoreInit.emptyWireCoil, 8), 110, getResearchList(ResearchLoader.wireProcessing), null, CraftingLevel.BRONZE, new Object[]{"III", " I ", "III", 'I', TMResource.BRONZE.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(FactoryInit.coilerPlant, 1, getMeta(LV)), 1100, getResearchList(ResearchLoader.wireProcessing), null, CraftingLevel.BRONZE, new Object[]{"IWI", "ECE", "IPI", 'P', "blockPiston", 'E', BASIC_CIRCUIT.get(), 'C', CoreInit.MachineFrameBasic, 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'W', CoreInit.emptyWireCoil});
		addRecipe(new ItemStack(FactoryInit.extruderModule, 1, 0), 1000, getResearchList(ResearchLoader.basicLvMachines), null, CraftingLevel.BRONZE, new Object[]{"WDW", "SBS", "SWS", 'W', TMResource.STEEL.getStackName(Type.PLATE), 'S', TMResource.BRONZE.getStackName(Type.PLATE), 'D', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'B', CraftingMaterial.UPGRADE_FRAME.getStack()});
		addRecipe(new ItemStack(FactoryInit.extruderModule, 1, 1), 1500, getResearchList(ResearchLoader.advancedExtruder), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"WDW", "SBS", "SWS", 'W', TMResource.TITANIUM.getStackName(Type.PLATE), 'S', TMResource.STEEL.getStackName(Type.PLATE), 'D', Items.DIAMOND, 'B', CraftingMaterial.UPGRADE_FRAME.getStack()});
		addRecipe(new ItemStack(FactoryInit.speedUpgrade), 1800, getResearchList(ResearchLoader.speedUpgrade), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"RBR", "BFB", "RCR", 'R', Blocks.REDSTONE_BLOCK, 'F', CraftingMaterial.UPGRADE_FRAME.getStack(), 'B', TMResource.BLUE_METAL.getStackName(Type.PLATE), 'C', TMResource.ELECTRUM.getStackName(Type.COIL)});
		addRecipe(new ItemStack(FactoryInit.plateBlendingMachine, 1, getMeta(LV)), 1000, getResearchList(ResearchLoader.lvPlateBlender), null, CraftingLevel.BRONZE, new Object[]{"PEP", "ICI", "IEI", 'P', "blockPiston", 'E', BASIC_CIRCUIT.get(), 'C', CoreInit.MachineFrameBasic, 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack()});
		addRecipe(new ItemStack(EnergyInit.battery), 300, getResearchList(ResearchLoader.batteryBox), null, CraftingLevel.BRONZE, new Object[]{" - ", "TBT", "TRT", '-', TMResource.COPPER.getStackName(Type.CABLE), 'T', TMResource.TIN.getStackName(Type.PLATE), 'B', TMResource.BLUE_METAL.getStackName(Type.INGOT), 'R', Items.REDSTONE});
		addRecipe(new ItemStack(FactoryInit.alloySmelter, 1, getMeta(LV)), 1000, getResearchList(ResearchLoader.lvAlloySmelter), null, CraftingLevel.BRONZE, new Object[]{"IHI", "EFE", "IPI", 'P', "blockPiston", 'E', BASIC_CIRCUIT.get(), 'F', new ItemStack(FactoryInit.electricFurnace, 1, getMeta(LV)), 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'H', CraftingMaterial.CUPRONICKEL_HEATING_COIL.getStack()});
		addRecipe(new ItemStack(FactoryInit.waterCollector), 700, getResearchList(ResearchLoader.waterCollector), null, CraftingLevel.BASIC_WOODEN, new Object[]{"BIB", "IWI", "BRB", 'B', TMResource.BRONZE.getStackName(Type.PLATE), 'W', Items.WATER_BUCKET, 'I', Blocks.IRON_BARS, 'R', Items.REDSTONE});
		addRecipe(new ItemStack(EnergyInit.steamTurbine), 1000, getResearchList(ResearchLoader.basicTurbine), null, CraftingLevel.BRONZE, new Object[]{"-E-", "TCT", "G_G", 'E', BASIC_CIRCUIT.get(), '-', CraftingMaterial.BRONZE_PIPE.getStack(), 'C', CoreInit.MachineFrameBasic, '_', TMResource.TIN.getStackName(Type.CABLE), 'T', CraftingMaterial.TIN_TURBINE.getStack(), 'G', CraftingMaterial.GENERATOR_COMPONENT.getStack()});
		addRecipe(CraftingMaterial.GENERATOR_COMPONENT.getStackNormal(), 500, getResearchList(ResearchLoader.basicTurbine), null, CraftingLevel.BRONZE, new Object[]{"TCI", "CRC", "ICT", 'T', TMResource.TIN.getStackName(Type.CABLE), 'C', TMResource.COPPER.getStackName(Type.CABLE), 'R', Items.REDSTONE, 'I', CraftingMaterial.IRON_ROD.getStack()});
		/*if (Config.addUnbreakableElytraRecipe) {
			ItemStack elytraStack = new ItemStack(Items.ELYTRA);
			NBTTagCompound elytraTag = new NBTTagCompound();
			elytraTag.setBoolean("Unbreakable", true);
			elytraStack.setTagCompound(elytraTag);
			addRecipe(elytraStack, 20000, getResearchList(ResearchLoader.hvMachines), null, CraftingLevel.HV_ELECTRICAL, new Object[]{"DND", "WEW", "F F", 'D', Blocks.DIAMOND_BLOCK, 'N', Items.NETHER_STAR, 'W', TMResource.TUNGSTENSTEEL.getStackName(Type.PLATE), 'E', Items.ELYTRA, 'F', Items.FEATHER});
		}*/
		addRecipe(new ItemStack(FactoryInit.solderingStation, 1, getMeta(LV)), 1100, getResearchList(ResearchLoader.eSolderingStation), null, CraftingLevel.BRONZE, new Object[]{"IHI", "RCR", "IEI", 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'H', CraftingMaterial.CUPRONICKEL_HEATING_COIL.getStack(), 'R', Items.REDSTONE, 'C', CoreInit.MachineFrameBasic, 'E', BASIC_CIRCUIT.get()});
		addRecipe(new ItemStack(FactoryInit.pump, 1, getMeta(LV)), 1000, getResearchList(ResearchLoader.pump), null, CraftingLevel.BRONZE, new Object[]{"ITI", "BCB", "IPI", 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'C', CoreInit.MachineFrameBasic, 'B', Items.BUCKET, 'T', StorageInit.tankBasic, 'P', CraftingMaterial.PUMP.getStackNormal()});
		addRecipe(new ItemStack(FactoryInit.fluidTransposer, 1, getMeta(LV)), 1100, getResearchList(ResearchLoader.fluidTransposer), null, CraftingLevel.BRONZE, new Object[]{"IEI", "TCB", "IPI", 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'C', CoreInit.MachineFrameBasic, 'B', Items.BUCKET, 'T', StorageInit.tankBasic, 'P', CraftingMaterial.PUMP.getStackNormal(), 'E', BASIC_CIRCUIT.get()});
		addRecipe(new ItemStack(FactoryInit.fluidTransposer, 1, getMeta(LV)), 1100, getResearchList(ResearchLoader.fluidTransposer), null, CraftingLevel.BRONZE, new Object[]{"IEI", "BCT", "IPI", 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'C', CoreInit.MachineFrameBasic, 'B', Items.BUCKET, 'T', StorageInit.tankBasic, 'P', CraftingMaterial.PUMP.getStackNormal(), 'E', BASIC_CIRCUIT.get()});
		addRecipe(new ItemStack(StorageInit.tankAdv), 300, getResearchList(ResearchLoader.advancedTank), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"SGS", "GTG", "SGS", 'T', StorageInit.tankBasic, 'G', "blockGlassColorless", 'S', TMResource.STEEL.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(FactoryInit.industrialBlastFurnace, 1, getMeta(LV)), 1200, getResearchList(ResearchLoader.industrialBlastFurnace), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"EHE", "HCH", "FHF", 'E', BASIC_CIRCUIT.get(), 'H', CraftingMaterial.CUPRONICKEL_HEATING_COIL.getStack(), 'C', CoreInit.MachineFrameSteel, 'F', new ItemStack(FactoryInit.electricFurnace, 1, getMeta(LV))});
		addRecipe(CraftingMaterial.SOLAR_PANEL_MK1.getStackNormal(2), 600, getResearchList(ResearchLoader.solarPanel), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"PPP", "BSB", "RLR", 'P', "paneGlassColorless", 'B', "dyeBlue", 'S', CraftingMaterial.SILICON_PLATE.getStack(), 'R', Items.REDSTONE, 'L', TMResource.LEAD.getStackName(Type.NUGGET)});
		addRecipe(new ItemStack(EnergyInit.solarPanel, 2), 1200, getResearchList(ResearchLoader.solarPanel), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"SSS", "RER", "P-P", 'S', CraftingMaterial.SOLAR_PANEL_MK1.getStackNormal(), 'R', Items.REDSTONE, 'E', BASIC_CIRCUIT.get(), 'P', TMResource.STEEL.getStackName(Type.PLATE), '-', TMResource.COPPER.getStackName(Type.CABLE)});
		addRecipe(new ItemStack(EnergyInit.transformerLMV), 1500, getResearchList(ResearchLoader.mvTransformer), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"CIC", "-F_", "EIE", '-', TMResource.COPPER.getStackName(Type.CABLE), '_', TMResource.ELECTRUM.getStackName(Type.CABLE), 'I', Items.IRON_INGOT, 'F', CoreInit.MachineFrameSteel, 'C', TMResource.COPPER.getStackName(Type.COIL), 'E', TMResource.ELECTRUM.getStackName(Type.COIL)});
		addRecipe(new ItemStack(EnergyInit.mvCable, 2), 1200, getResearchList(ResearchLoader.mvCable), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"RRR", "---", "RRR", 'R', CraftingMaterial.RUBBER.getStack(), '-', TMResource.ELECTRUM.getStackName(Type.CABLE)});
		for (Entry<BlockMachineBase, Entry<Block, Block>> m : machines.entrySet()) {
			if (m.getValue().getKey() != null) {
				addRecipe(new ItemStack(m.getKey(), 1, getMeta(MV)), 1500, getResearchList(ResearchLoader.mvMachines), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"STS", "FM-", "SES", 'S', TMResource.TITANIUM.getStackName(Type.PLATE), 'T', EnergyInit.transformerLMV, 'F', m.getValue().getKey(), 'M', new ItemStack(m.getKey(), 1, getMeta(LV)), '-', TMResource.ELECTRUM.getStackName(Type.CABLE), 'E', NORMAL_CIRCUIT.get()});
				addRecipe(new ItemStack(m.getKey(), 1, getMeta(MV)), 1500, getResearchList(ResearchLoader.mvMachines), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"STS", "-MF", "SES", 'S', TMResource.TITANIUM.getStackName(Type.PLATE), 'T', EnergyInit.transformerLMV, 'F', m.getValue().getKey(), 'M', new ItemStack(m.getKey(), 1, getMeta(LV)), '-', TMResource.ELECTRUM.getStackName(Type.CABLE), 'E', NORMAL_CIRCUIT.get()});
			}
			if (m.getValue().getValue() != null) {
				addRecipe(new ItemStack(m.getKey(), 1, getMeta(HV)), 2800, getResearchList(ResearchLoader.hvMachines), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"STS", "FM-", "SES", 'S', TMResource.CHROME.getStackName(Type.PLATE), 'T', EnergyInit.transformerMHV, 'F', m.getValue().getValue(), 'M', new ItemStack(m.getKey(), 1, getMeta(MV)), '-', TMResource.ENDERIUM.getStackName(Type.CABLE), 'E', ADVANCED_CIRCUIT.get()});
				addRecipe(new ItemStack(m.getKey(), 1, getMeta(HV)), 2800, getResearchList(ResearchLoader.hvMachines), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"STS", "-MF", "SES", 'S', TMResource.CHROME.getStackName(Type.PLATE), 'T', EnergyInit.transformerMHV, 'F', m.getValue().getValue(), 'M', new ItemStack(m.getKey(), 1, getMeta(MV)), '-', TMResource.ENDERIUM.getStackName(Type.CABLE), 'E', ADVANCED_CIRCUIT.get()});
			}
		}
		addRecipe(NORMAL_CIRCUIT_COMPONENT.get(), 1200, getResearchList(ResearchLoader.mvCircuit), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"-E-", "SSS", "R-R", 'R', TMResource.REDSTONE.getStackName(Type.PLATE), '-', TMResource.ELECTRUM.getStackName(Type.CABLE), 'E', BASIC_CIRCUIT.get(), 'S', CraftingMaterial.SILICON_PLATE.getStack()});
		addRecipe(NORMAL_CIRCUIT.get(), 1200, getResearchList(ResearchLoader.mvCircuit), null, CraftingLevel.SOLDERING_STATION, new Object[]{"RCR", "RPR", "-R-", 'R', Items.REDSTONE, '-', TMResource.ELECTRUM.getStackName(Type.CABLE), 'P', UNASSEMBLED_NORMAL_CIRCUIT_PANEL.get(), 'C', NORMAL_CIRCUIT_COMPONENT.get()});
		addRecipe(new ItemStack(FactoryInit.refinery), 1600, getResearchList(ResearchLoader.refinery), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"SAS", "GCG", "SFS", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'A', TMResource.ALUMINUM.getStackName(Type.PLATE), 'G', "blockGlass", 'C', CoreInit.MachineFrameSteel, 'F', FactoryInit.advBoiler});
		addRecipe(new ItemStack(FactoryInit.MultiblockCase, a3o6), 1800, getResearchList(ResearchLoader.multiblockComponents), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"APA", "SCS", "AEA", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'A', TMResource.ALUMINUM.getStackName(Type.PLATE), 'C', CoreInit.MachineFrameAluminum, 'P', TMResource.CHROME.getStackName(Type.PLATE), 'E', NORMAL_CIRCUIT.get()});
		addRecipe(new ItemStack(FactoryInit.MultiblockEnergyPort), 1800, getResearchList(ResearchLoader.multiblockComponents), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"MRM", "R-R", "MRM", 'R', CraftingMaterial.CHARGED_REDSTONE.getStack(), 'M', FactoryInit.MultiblockCase, '-', TMResource.ELECTRUM.getStackName(Type.CABLE)});
		addRecipe(new ItemStack(FactoryInit.MultiblockFluidHatch), 1800, getResearchList(ResearchLoader.multiblockComponents), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"MPM", "IBI", 'P', "blockPiston", 'M', FactoryInit.MultiblockCase, 'B', Items.BUCKET, 'I', "ingotIron"});
		addRecipe(new ItemStack(FactoryInit.MultiblockHatch), 1800, getResearchList(ResearchLoader.multiblockComponents), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"MPM", "ICI", 'P', "blockPiston", 'M', FactoryInit.MultiblockCase, 'C', "chest", 'I', "ingotIron"});
		addRecipe(CraftingMaterial.ELECTRIC_MOTOR.getStackNormal(), 1200, getResearchList(ResearchLoader.mvMachines), new ItemStack(CoreInit.emptyWireCoil, 4), CraftingLevel.E_SOLDERING_STATION, new Object[]{"TCI", "CRC", "ICT", 'T', TMResource.COPPER.getStackName(Type.CABLE), 'C', TMResource.ELECTRUM.getStackName(Type.COIL), 'R', Items.REDSTONE, 'I', CraftingMaterial.IRON_ROD.getStack()});
		addRecipe(new ItemStack(FactoryInit.Electrolyzer), 2000, getResearchList(ResearchLoader.electrolyzer), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"MBM", "CPC", "MRM", 'R', CraftingMaterial.CHARGED_REDSTONE.getStack(), 'M', FactoryInit.MultiblockCase, 'C', Blocks.COAL_BLOCK, 'P', CraftingMaterial.PUMP.getStackNormal(), 'B', FactoryInit.steelBoiler});
		addRecipe(new ItemStack(FactoryInit.Centrifuge), 2000, getResearchList(ResearchLoader.centrifuge), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"MBM", "IPI", "MRM", 'R', CraftingMaterial.CHARGED_REDSTONE.getStack(), 'M', FactoryInit.MultiblockCase, 'I', TMResource.ALUMINUM.getBlockOreDictName(), 'P', CraftingMaterial.PUMP.getStackNormal(), 'B', FactoryInit.steelBoiler});
		addRecipe(new ItemStack(EnergyInit.transformerMHV), 2500, getResearchList(ResearchLoader.hvTransformer), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"EIE", "-F_", "CIP", 'E', TMResource.ELECTRUM.getStackName(Type.COIL), 'C', TMResource.IRON.getStackName(Type.COIL), 'F', CoreInit.MachineFrameTitanium, 'P', CraftingMaterial.PLASTIC_SHEET.getStack(), 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), '-', TMResource.ELECTRUM.getStackName(Type.CABLE), '_', TMResource.IRON.getStackName(Type.CABLE)});
		addRecipe(new ItemStack(EnergyInit.transformerMHV), 2500, getResearchList(ResearchLoader.hvTransformer), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"EIE", "-F_", "PIC", 'E', TMResource.ELECTRUM.getStackName(Type.COIL), 'C', TMResource.IRON.getStackName(Type.COIL), 'F', CoreInit.MachineFrameTitanium, 'P', CraftingMaterial.PLASTIC_SHEET.getStack(), 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), '-', TMResource.ELECTRUM.getStackName(Type.CABLE), '_', TMResource.IRON.getStackName(Type.CABLE)});
		addRecipe(new ItemStack(EnergyInit.hvCable, 2), 1800, getResearchList(ResearchLoader.hvCable), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"R-R", "rCr", "R-R", 'R', CraftingMaterial.RUBBER.getStack(), '-', TMResource.ENDERIUM.getStackName(Type.CABLE), 'r', Items.REDSTONE, 'C', EnergyInit.mvCable});
		addRecipe(ADVANCED_CIRCUIT_PLATE.get(), 800, getResearchList(ResearchLoader.advancedCircuit), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"_C_", "RGR", "_P_", 'R', CraftingMaterial.RUBBER.getStack(), 'C', TMResource.COPPER.getStackName(Type.PLATE), '_', BASIC_CIRCUIT_PLATE.get(), 'G', CraftingMaterial.GLASS_MESH.getStack(), 'P', CraftingMaterial.PLASTIC_SHEET.getStack()});
		addRecipe(ADVANCED_CIRCUIT_COMPONENT.get(), 1600, getResearchList(ResearchLoader.advancedCircuit), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"CNC", "SRS", "GEG", 'C', TMResource.ELECTRUM.getStackName(Type.COIL), 'S', BASIC_CHIPSET.getStackNormal(1), 'R', TMResource.REDSTONE.getStackName(Type.PLATE), 'G', TMResource.GREENIUM.getStackName(Type.NUGGET), 'E', TMResource.ENDERIUM.getStackName(Type.COIL), 'N', NORMAL_CIRCUIT.get()});
		addRecipe(ADVANCED_CIRCUIT.get(), 1800, getResearchList(ResearchLoader.advancedCircuit), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"PCP", "-_-", "RER", 'R', Items.REDSTONE, 'P', CraftingMaterial.PLASTIC_SHEET.getStack(), 'C', ADVANCED_CIRCUIT_COMPONENT.get(), '-', TMResource.ELECTRUM.getStackName(Type.CABLE), '_', UNASSEMBLED_ADVANCED_CIRCUIT_PANEL.get(), 'E', TMResource.ENDERIUM.getStackName(Type.CABLE)});
		addRecipe(new ItemStack(FactoryInit.AdvancedMultiblockCasing, a1o2), 2200, getResearchList(ResearchLoader.advancedMultiblockParts), null, CraftingLevel.MV_ELECTRICAL, new Object[]{"TtT", "CEC", "TMT", 'T', TMResource.TUNGSTENSTEEL.getStackName(Type.PLATE), 't', TMResource.TITANIUM.getStackName(Type.PLATE), 'C', TMResource.CHROME.getStackName(Type.PLATE), 'E', ADVANCED_CIRCUIT.get(), 'M', FactoryInit.MultiblockCase});
		addRecipe(new ItemStack(FactoryInit.MultiblockFuelRod), 2500, getResearchList(ResearchLoader.advancedMultiblockParts), null, CraftingLevel.MV_ELECTRICAL, new Object[]{"MPM", "CIC", "MPM", 'M', FactoryInit.AdvancedMultiblockCasing, 'C', TMResource.LEAD.getStackName(Type.PLATE), 'I', Blocks.REDSTONE_BLOCK, 'P', TMResource.CHROME.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(StorageInit.tankElite), 3000, getResearchList(ResearchLoader.eliteTank), null, CraftingLevel.MV_ELECTRICAL, new Object[]{"PGP", "GTG", "PGP", 'P', TMResource.TITANIUM.getStackName(Type.PLATE), 'G', "glassHardened", 'T', StorageInit.tankAdv});
		addRecipe(new ItemStack(StorageInit.tankUltimate), 4000, getResearchList(ResearchLoader.ultimateTank), null, CraftingLevel.MV_ELECTRICAL, new Object[]{"PGP", "GTG", "PGP", 'P', TMResource.ENDERIUM.getStackName(Type.PLATE), 'G', "glassEnder", 'T', StorageInit.tankElite});
		addShapelessRecipe(new ItemStack(StorageInit.limitableChest), 200, getResearchList(ResearchLoader.limitableChest), null, CraftingLevel.BASIC_WOODEN, new Object[]{"chest", Blocks.TRAPDOOR, Blocks.LEVER, TMResource.BRONZE.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(EnergyInit.steamTurbineMK2), 1500, getResearchList(ResearchLoader.mvTurbine), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"STS", "EFE", "SRS", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'T', EnergyInit.steamTurbine, 'E', TMResource.ELECTRUM.getStackName(Type.COIL), 'F', CoreInit.MachineFrameSteel, 'R', Items.REDSTONE});
		addRecipe(new ItemStack(EnergyInit.charger, 1, getMeta(LV)), 1500, getResearchList(ResearchLoader.charger), null, CraftingLevel.BRONZE, new Object[]{"IBI", "-C-", "IRI", 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'B', EnergyInit.batteryBox, '-', TMResource.ELECTRUM.getStackName(Type.CABLE), 'C', CoreInit.MachineFrameBasic, 'R', Items.REDSTONE});
		addRecipe(new ItemStack(CoreInit.configurator), 1700, getResearchList(ResearchLoader.configurator), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"IGI", "RDR", "IBI", 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'B', EnergyInit.battery, 'D', CraftingMaterial.DISPLAY.getStackNormal(), 'R', Items.REDSTONE, 'G', "paneGlass"});
		addRecipe(new ItemStack(FactoryInit.geothermalBoiler), 1500, getResearchList(ResearchLoader.geoThermalBoiler), null, CraftingLevel.BRONZE, new Object[]{"SBS", "-A-", "BOB", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'B', CraftingMaterial.REFINED_BRICK.getStack(), 'A', FactoryInit.advBoiler, '-', CraftingMaterial.STEEL_PIPE.getStack(), 'O', Blocks.OBSIDIAN});
		addRecipe(new ItemStack(EnergyInit.geothermalGenerator), 1800, getResearchList(ResearchLoader.geoThermalGenerator), null, CraftingLevel.BRONZE, new Object[]{"SES", "BGB", "CAC", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'B', CraftingMaterial.REFINED_BRICK.getStack(), 'A', FactoryInit.geothermalBoiler, 'E', BASIC_CIRCUIT.get(), 'C', CraftingMaterial.GENERATOR_COMPONENT.getStack(), 'G', "glassHardened"});
		addRecipe(new ItemStack(FactoryInit.fluidBolier), 700, getResearchList(ResearchLoader.liquidFueledBoiler), null, CraftingLevel.BASIC_WOODEN, new Object[]{"PRP", "PCP", "BbB", 'P', TMResource.BRONZE.getStackName(Type.PLATE), 'R', CraftingMaterial.REFINED_BRICK.getStack(), 'C', CoreInit.MachineFrameBronze, 'B', Blocks.BRICK_BLOCK, 'b', FactoryInit.basicBoiler});
		addRecipe(new ItemStack(FactoryInit.advFluidBoiler), 1200, getResearchList(ResearchLoader.liquidFueledSteelBoiler), null, CraftingLevel.BRONZE, new Object[]{"PRP", "PSP", "BbB", 'P', TMResource.STEEL.getStackName(Type.PLATE), 'R', CraftingMaterial.REFINED_BRICK.getStack(), 'S', FactoryInit.advBoiler, 'B', Blocks.BRICK_BLOCK, 'b', FactoryInit.fluidBolier});
		addRecipe(new ItemStack(EnergyInit.fluidGenerator), 1800, getResearchList(ResearchLoader.liquidFueledGenerator), null, CraftingLevel.BRONZE, new Object[]{"PIP", "GCB", "PEP", 'P', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'I', "blockIron", 'G', CraftingMaterial.GENERATOR_COMPONENT.getStack(), 'B', FactoryInit.advFluidBoiler, 'C', CoreInit.MachineFrameBasic, 'E', BASIC_CIRCUIT.get()});
		addRecipe(new ItemStack(EnergyInit.fluidGenerator), 1800, getResearchList(ResearchLoader.liquidFueledGenerator), null, CraftingLevel.BRONZE, new Object[]{"PIP", "BCG", "PEP", 'P', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'I', "blockIron", 'G', CraftingMaterial.GENERATOR_COMPONENT.getStack(), 'B', FactoryInit.advFluidBoiler, 'C', CoreInit.MachineFrameBasic, 'E', BASIC_CIRCUIT.get()});
		addShapelessRecipe(RAW_BASIC_CIRCUIT_PANEL.get(), /*Config.enableHardRecipes ? 500 : */300, getResearchList(ResearchLoader.soldering), CoreInit.circuitDrawingPen.getDamaged(), CraftingLevel.BRONZE, new Object[]{BASIC_CIRCUIT_PLATE.get(), new ItemStack(CoreInit.circuitDrawingPen, 1, 0)});
		addRecipe(CraftingMaterial.UV_LAMP.getStackNormal(), 1000, getResearchList(ResearchLoader.uvLightbox), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"GGG", "GBG", " C ", 'G', "glassPaneHardened", 'B', TMResource.BLUE_METAL.getStackName(Type.NUGGET), 'C', TMResource.COPPER.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(FactoryInit.uvLightbox, 1, getMeta(LV)), 1000, getResearchList(ResearchLoader.uvLightbox), null, CraftingLevel.BRONZE, new Object[]{"ILI", "EFE", "III", 'I', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'L', CraftingMaterial.UV_LAMP.getStack(), 'E', BASIC_CIRCUIT.get(), 'F', CoreInit.MachineFrameBasic});
		addRecipe(new ItemStack(FactoryInit.advSteamFurnace), 800, getResearchList(ResearchLoader.steelFurnace), null, CraftingLevel.BRONZE, new Object[]{"PPP", "PGP", "BFB", 'F', Blocks.FURNACE, 'P', TMResource.STEEL.getStackName(Type.PLATE), 'B', Blocks.BRICK_BLOCK, 'G', FactoryInit.steamFurnace});
		addRecipe(new ItemStack(FactoryInit.steamMixer), 800, getResearchList(ResearchLoader.steamMixer), null, CraftingLevel.BRONZE, new Object[]{"BGB", "SCS", "bPb", 'B', TMResource.BRONZE.getStackName(Type.PLATE), 'G', "blockGlass", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'C', CoreInit.MachineFrameBronze, 'b', Blocks.BRICK_BLOCK, 'P', CraftingMaterial.BRONZE_PIPE.getStack()});
		addRecipe(new ItemStack(FactoryInit.mixer, 1, getMeta(LV)), 1500, getResearchList(ResearchLoader.mixer), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"PGP", "ECE", "PMP", 'P', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'G', "glassHardened", 'E', BASIC_CIRCUIT.get(), 'C', CoreInit.MachineFrameBasic, 'M', FactoryInit.steamMixer});
		addRecipe(new ItemStack(FactoryInit.plasticProcessor), 2000, getResearchList(ResearchLoader.plasticProcessing), null, CraftingLevel.MV_ELECTRICAL, new Object[]{"SAS", "ECE", "SMS", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'A', TMResource.ALUMINUM.getStackName(Type.PLATE), 'C', CoreInit.MachineFrameSteel, 'E', NORMAL_CIRCUIT.get(), 'M', new ItemStack(FactoryInit.mixer, 1, getMeta(MV))});
		addRecipe(new ItemStack(CoreInit.researchTableUpgrade, 1, 0), 500, getResearchList(ResearchLoader.basicBronzeMachines), null, CraftingLevel.BASIC_WOODEN, new Object[]{"BBB", "-P-", "S S", 'B', TMResource.BRONZE.getStackName(Type.PLATE), '-', CraftingMaterial.BRONZE_PIPE.getStack(), 'P', "plankWood", 'S', "stickWood"});
		addRecipe(new ItemStack(CoreInit.researchTableUpgrade, 1, 1), 1000, getResearchList(ResearchLoader.basicLvMachines), null, CraftingLevel.BRONZE, new Object[]{"BBB", "-P-", "S S", 'B', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), '-', EnergyInit.lvCable, 'P', BASIC_CIRCUIT.get(), 'S', "stickWood"});
		addRecipe(CraftingMaterial.LASER_MODULE.getStackNormal(), 1300, getResearchList(ResearchLoader.laserEngraver), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"PDP", "RDR", "PGP", 'P', TMResource.STEEL.getStackName(Type.PLATE), 'D', TMResource.RED_DIAMOND.getStackName(Type.GEM), 'R', Blocks.REDSTONE_BLOCK, 'G', "glassPaneHardened"});
		addRecipe(new ItemStack(FactoryInit.laserEngraver, 1, getMeta(MV)), 1300, getResearchList(ResearchLoader.laserEngraver), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"IMI", "CFC", "IOI", 'I', TMResource.TITANIUM.getStackName(Type.PLATE), 'M', CraftingMaterial.LASER_MODULE.getStack(), 'C', BASIC_CIRCUIT.get(), 'F', CoreInit.MachineFrameTitanium, 'O', "obsidian"});
		addRecipe(new ItemStack(FactoryInit.extruderModule, 1, 2), 2100, getResearchList(ResearchLoader.advancedExtruder2), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"WDW", "TBT", "TWT", 'W', TMResource.WOLFRAM.getStackName(Type.PLATE), 'T', TMResource.TITANIUM.getStackName(Type.PLATE), 'D', Items.DIAMOND, 'B', CraftingMaterial.UPGRADE_FRAME.getStack()});
		addRecipe(CraftingMaterial.UPGRADE_FRAME.getStackNormal(2), 600, getResearchList(ResearchLoader.machineUpgrades), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{" B ", "RIR", " G ", 'B', TMResource.BLUE_METAL.getStackName(Type.NUGGET), 'I', Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 'G', TMResource.GLOWSTONE.getStackName(Type.DUST_TINY), 'R', Items.REDSTONE});
		addRecipe(new ItemStack(FactoryInit.steelBoiler), 600, getResearchList(ResearchLoader.steelBoiler), null, CraftingLevel.BRONZE, new Object[]{"PP", "PP", 'P', new ItemStack(FactoryInit.components, 1, ComponentVariants.STEEL_SHEETS.ordinal())});
		addRecipe(new ItemStack(FactoryInit.rubberBoiler), 800, getResearchList(ResearchLoader.rubberProcessing), null, CraftingLevel.BRONZE, new Object[]{" - ", "-B-", "PPP", 'P', TMResource.STEEL.getStackName(Type.PLATE), '-', CraftingMaterial.STEEL_PIPE.getStack(), 'B', FactoryInit.steelBoiler});
		addShapelessRecipe(CraftingMaterial.VULCANIZING_AGENTS.getStackNormal(4), 30, getResearchList(ResearchLoader.rubberProcessing), null, CraftingLevel.BRONZE, new Object[]{TMResource.COAL.getStackName(Type.DUST), TMResource.SULFUR.getStackName(Type.DUST), "dyeWhite", TMResource.IRON.getStackName(Type.DUST)});
		addRecipe(new ItemStack(FactoryInit.electricalRubberProcessor, 1, getMeta(LV)), 1600, getResearchList(ResearchLoader.electricalRubberProcessing), null, CraftingLevel.BASIC_ELECTRICAL, new Object[]{"PBP", "CFC", "PSP", 'P', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'B', FactoryInit.rubberBoiler, 'F', FactoryInit.rubberProcessor, 'S', FactoryInit.electricFurnace, 'C', BASIC_CIRCUIT.get()});
		addRecipe(new ItemStack(FactoryInit.advBlastFurnace), 800, getResearchList(ResearchLoader.improvedBlastFurnace), null, CraftingLevel.BRONZE, new Object[]{"SBS", "bMb", "SBS", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'B', CraftingMaterial.REFINED_BRICK.getStack(), 'M', FactoryInit.blastFurnace, 'b', FactoryInit.blastFurnaceWall});
		addRecipe(new ItemStack(TransportInit.fluidDuct, 6), 30, getResearchList(ResearchLoader.fluidDucts), null, CraftingLevel.BASIC_WOODEN, new Object[]{"PGP", 'P', TMResource.IRON.getStackName(Type.PLATE), 'G', "blockGlass"});
		addRecipe(new ItemStack(TransportInit.fluidDuctOpaque, 6), 30, getResearchList(ResearchLoader.fluidDucts), null, CraftingLevel.BASIC_WOODEN, new Object[]{"PLP", 'P', TMResource.IRON.getStackName(Type.PLATE), 'L', TMResource.LEAD.getStackName(Type.PLATE)});
		addShapelessRecipe(new ItemStack(TransportInit.fluidDuct, 6), 20, getResearchList(ResearchLoader.fluidDucts), null, CraftingLevel.BASIC_WOODEN, new Object[]{TransportInit.fluidDuctOpaque, TransportInit.fluidDuctOpaque, TransportInit.fluidDuctOpaque, TransportInit.fluidDuctOpaque, TransportInit.fluidDuctOpaque, TransportInit.fluidDuctOpaque, "blockGlass"});
		addShapelessRecipe(new ItemStack(TransportInit.fluidDuctOpaque, 6), 20, getResearchList(ResearchLoader.fluidDucts), null, CraftingLevel.BASIC_WOODEN, new Object[]{TransportInit.fluidDuct, TransportInit.fluidDuct, TransportInit.fluidDuct, TransportInit.fluidDuct, TransportInit.fluidDuct, TransportInit.fluidDuct, TMResource.LEAD.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(TransportInit.conveyorBeltSlow, 12), 100, getResearchList(ResearchLoader.conveyorBelts), null, CraftingLevel.BASIC_WOODEN, new Object[]{"RRR", "CCC", 'C', TMResource.COPPER.getStackName(Type.PLATE), 'R', "itemRubber"});
		addRecipe(new ItemStack(TransportInit.conveyorBeltFast, 4), 100, getResearchList(ResearchLoader.conveyorBelts), null, CraftingLevel.BASIC_WOODEN, new Object[]{"RTR", "CTC", 'T', TMResource.TIN.getStackName(Type.PLATE), 'C', TMResource.COPPER.getStackName(Type.PLATE), 'R', "itemRubber"});
		addRecipe(new ItemStack(TransportInit.conveyorBeltOmnidirectionalSlow, 8), 100, getResearchList(ResearchLoader.conveyorBelts), null, CraftingLevel.BASIC_WOODEN, new Object[]{"CCC", "CSC", "CCC", 'C', TransportInit.conveyorBeltSlow, 'S', "slimeball"});
		addRecipe(new ItemStack(TransportInit.conveyorBeltOmnidirectionalFast, 8), 100, getResearchList(ResearchLoader.conveyorBelts), null, CraftingLevel.BASIC_WOODEN, new Object[]{"CCC", "CSC", "CCC", 'C', TransportInit.conveyorBeltFast, 'S', "slimeball"});
		addRecipe(new ItemStack(TransportInit.conveyorBeltSlope, 2, 0), 100, getResearchList(ResearchLoader.conveyorBelts), null, CraftingLevel.BASIC_WOODEN, new Object[]{"C  ", "PC ", "PPT", 'C', TransportInit.conveyorBeltFast, 'P', TMResource.COPPER.getStackName(Type.PLATE), 'T', TMResource.TIN.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(TransportInit.conveyorBeltSlope, 2, 1), 100, getResearchList(ResearchLoader.conveyorBelts), null, CraftingLevel.BASIC_WOODEN, new Object[]{"T  ", "PC ", "PPC", 'C', TransportInit.conveyorBeltFast, 'P', TMResource.COPPER.getStackName(Type.PLATE), 'T', TMResource.TIN.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(FactoryInit.rubberProcessor), 1000, getResearchList(ResearchLoader.rubberProcessing), null, CraftingLevel.BRONZE, new Object[]{"BSB", "_B_", "bFb", 'B', TMResource.BRONZE.getStackName(Type.PLATE), '_', TMResource.STEEL.getStackName(Type.PLATE), 'F', FactoryInit.advSteamFurnace, 'S', FactoryInit.steelBoiler, 'b', Blocks.BRICK_BLOCK});
		addRecipe(new ItemStack(EnergyInit.Generator), 1000, getResearchList(ResearchLoader.basicLvMachines), null, CraftingLevel.BRONZE, new Object[]{"P- ", "-B-", "_F_", 'P', CraftingMaterial.STEEL_PIPE.getStack(), '-', TMResource.STEEL.getStackName(Type.PLATE), 'B', FactoryInit.steelBoiler, '_', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 'F', Blocks.FURNACE});
		addRecipe(new ItemStack(TransportInit.openCrate), 200, getResearchList(ResearchLoader.limitableChest), null, CraftingLevel.BASIC_WOODEN, new Object[]{"PSP", "PCP", "PTP", 'C', "chest", 'T', Blocks.TRAPDOOR, 'P', "plankWood", 'S', "stickWood"});

		addRecipe(BLUEPRINT_BASIC_CIRCUIT.getStackNormal(1),     /*Config.enableHardRecipes ? 800  : */500, getResearchList(ResearchLoader.soldering), createChalk(0), CraftingLevel.BRONZE, new Object[]{"BBP", "BB ", 'B', CraftingMaterial.BLUEPRINT_PAPER.getStack(), 'P', CoreInit.chalk});
		addRecipe(BLUEPRINT_NORMAL_CIRCUIT.getStackNormal(1),    /*Config.enableHardRecipes ? 1500 : */1000, getResearchList(ResearchLoader.mvCircuit), createChalk(2), CraftingLevel.BRONZE, new Object[]{"BBP", "BbB", "BBB", 'B', CraftingMaterial.BLUEPRINT_PAPER.getStack(), 'P', CoreInit.chalk, 'b', CraftingMaterial.BIG_BLUEPRINT_PAPER.getStack()});
		addRecipe(BLUEPRINT_ADVANCED_CIRCUIT.getStackNormal(1),  /*Config.enableHardRecipes ? 2200 : */1800, getResearchList(ResearchLoader.advancedCircuit), createChalk(4), CraftingLevel.BASIC_ELECTRICAL, new Object[]{"BBP", "BBB", 'P', CoreInit.chalk, 'B', CraftingMaterial.BIG_BLUEPRINT_PAPER.getStack()});
		addRecipe(BLUEPRINT_BASIC_CIRCUIT.getStackNormal(2),    /*Config.enableHardRecipes ? 200  : */100, getResearchList(ResearchLoader.soldering), createChalk(0), CraftingLevel.BRONZE, new Object[]{"BBP", "BBb", 'P', CoreInit.chalk, 'B', CraftingMaterial.BLUEPRINT_PAPER.getStack(), 'b', BLUEPRINT_BASIC_CIRCUIT.getStackNormal(1)});
		addRecipe(BLUEPRINT_NORMAL_CIRCUIT.getStackNormal(2),   /*Config.enableHardRecipes ? 600  : */300, getResearchList(ResearchLoader.soldering), createChalk(2), CraftingLevel.BASIC_ELECTRICAL, new Object[]{"BBP", "aab", "B  ", 'P', CoreInit.chalk, 'B', CraftingMaterial.BLUEPRINT_PAPER.getStack(), 'b', BLUEPRINT_NORMAL_CIRCUIT.getStackNormal(1), 'a', CraftingMaterial.BIG_BLUEPRINT_PAPER.getStack()});
		addRecipe(BLUEPRINT_ADVANCED_CIRCUIT.getStackNormal(2), /*Config.enableHardRecipes ? 900  : */500, getResearchList(ResearchLoader.soldering), createChalk(3), CraftingLevel.MV_ELECTRICAL, new Object[]{"BBP", "BBb", "B  ", 'P', CoreInit.chalk, 'B', CraftingMaterial.BIG_BLUEPRINT_PAPER.getStack(), 'b', BLUEPRINT_ADVANCED_CIRCUIT.getStackNormal(1)});
		//addRecipe(BLUEPRINT_ELITE_CIRCUIT.getStackNormal(2),    /*Config.enableHardRecipes ? 1200 : */800, getResearchList(ResearchLoader.soldering), createChalk(6), CraftingLevel.HV_ELECTRICAL, new Object[]{"BBP", "BBb", "BBB", 'P', CoreInit.chalk, 'B', CraftingMaterial.BIG_BLUEPRINT_PAPER.getStack(), 'b', BLUEPRINT_ELITE_CIRCUIT.getStack()});
		/*addRecipe(CraftingMaterial.BLUEPRINT_BASIC_CHIPSET.getStackNormal(), Config.enableHardRecipes ? 1500 : 1000, getResearchList(ResearchLoader.basicChipset), createChalk(3), CraftingLevel.MV_ELECTRICAL, new Object[]{"PBB", "BbB", "BBB", 'B', CraftingMaterial.BLUEPRINT_PAPER.getStack(), 'P', CoreInit.chalk, 'b', CraftingMaterial.BIG_BLUEPRINT_PAPER.getStack()});
		addRecipe(CraftingMaterial.BLUEPRINT_ADVANCED_CHIPSET.getStackNormal(), Config.enableHardRecipes ? 3000 : 2000, getResearchList(ResearchLoader.advChipset), createChalk(6), CraftingLevel.HV_ELECTRICAL, new Object[]{"PBB", "BBB", "BBB", 'P', CoreInit.chalk, 'B', CraftingMaterial.BIG_BLUEPRINT_PAPER.getStack()});*/
	}

	public static List<Research> getResearchList(Research... in) {
		List<Research> list = new ArrayList<>();
		if (in != null) {
			for (Research a : in) {
				list.add(a);
			}
		}
		return list;
	}

	public static List<IScanningInformation> getScanningList(IScanningInformation... in) {
		List<IScanningInformation> list = new ArrayList<>();
		if (in != null) {
			for (IScanningInformation a : in) {
				list.add(a);
			}
		}
		return list;
	}

	private static int getMeta(EnergyType type) {
		return TileEntityMachineBase.getMetaFromEnergyType(type);
	}

	private static ItemStack createChalk(int dmg) {
		ItemStack is = new ItemStack(CoreInit.chalk);
		if (dmg > 0) {
			CoreInit.chalk.setItemDamage(is, dmg);
		}
		return is;
	}
}
