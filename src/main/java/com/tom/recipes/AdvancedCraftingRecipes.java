package com.tom.recipes;

import static com.tom.api.energy.EnergyType.LV;
import static com.tom.api.energy.EnergyType.MV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.energy.EnergyType;
import com.tom.api.research.IResearch;
import com.tom.api.research.IScanningInformation;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.core.research.handler.ResearchLoader;
import com.tom.energy.EnergyInit;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.BlockMachineBase;
import com.tom.factory.tileentity.TileEntityMachineBase;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.CraftingLevel;
import com.tom.storage.StorageInit;

public class AdvancedCraftingRecipes {//CraftingRecipes
	public static Map<BlockMachineBase, Block> machines = new HashMap<BlockMachineBase, Block>();
	public static void init(){
		int brickAmount = Config.enableHardMode ? 2 : 4;
		AdvancedCraftingHandler.addRecipe(CraftingMaterial.ACID_PAPER.getStackNormal(2), 100, getResearchList(ResearchLoader.researchAcids), new ItemStack(Items.BUCKET), CraftingLevel.BASIC, new Object[]{"W D", "SPP", "RPP", 'W', Items.WATER_BUCKET, 'D', new ItemStack(Items.DYE,1,15), 'S', Items.SPIDER_EYE, 'P', Items.PAPER, 'R', Items.ROTTEN_FLESH});
		AdvancedCraftingHandler.addRecipe(CraftingMaterial.FLINT_HAMMER_HEAD.getStackNormal(), 100, getResearchList(ResearchLoader.hammer), null, CraftingLevel.BASIC, new Object[]{"FFF", "FIF", " F ", 'F', Items.FLINT, 'I', "ingotIron"});
		AdvancedCraftingHandler.addRecipe(CraftingMaterial.FLINT_HAMMER_HEAD.getStackNormal(), 100, getResearchList(ResearchLoader.hammer), null, CraftingLevel.BASIC, new Object[]{"FFF", "FIF", " F ", 'F', Items.FLINT, 'I', "ingotCopper"});
		AdvancedCraftingHandler.addRecipe(CraftingMaterial.FLINT_HAMMER_HEAD.getStackNormal(), 100, getResearchList(ResearchLoader.hammer), null, CraftingLevel.BASIC, new Object[]{"FFF", "FIF", " F ", 'F', Items.FLINT, 'I', "ingotTin"});
		AdvancedCraftingHandler.addShapelessRecipe(new ItemStack(CoreInit.mortarAndPestle), 200, getResearchList(ResearchLoader.mortar), null, CraftingLevel.BASIC, new Object[]{CraftingMaterial.STONE_BOWL.getStack(), Items.FLINT});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.basicBoiler), 300, getResearchList(ResearchLoader.basicBoiler), null, CraftingLevel.BASIC, new Object[]{"PPP", "PGP", "BFB", 'F', Blocks.FURNACE, 'P', TMResource.BRONZE.getStackName(Type.PLATE), 'B', Blocks.BRICK_BLOCK, 'G', "blockGlass"});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.advBoiler), 500, getResearchList(ResearchLoader.steelBoiler), null, CraftingLevel.BASIC, new Object[]{"PPP", "PGP", "BFB", 'F', Blocks.FURNACE, 'P', TMResource.STEEL.getStackName(Type.PLATE), 'B', Blocks.BRICK_BLOCK, 'G', FactoryInit.basicBoiler});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.steamCrusher), 300, getResearchList(ResearchLoader.basicBronzeMachines), null, CraftingLevel.BASIC, new Object[]{"DFD", "-C-", "P-P", 'D', TMResource.DIAMOND.getStackName(Type.GEM), 'P', "blockPiston", 'C', CoreInit.MachineFrameBronze, 'F', Items.FLINT, '-', CraftingMaterial.BRONZE_PIPE.getStack()});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.steamFurnace), 300, getResearchList(ResearchLoader.basicBronzeMachines), null, CraftingLevel.BASIC, new Object[]{"PPP", "PFP", "B-B", 'B', Blocks.BRICK_BLOCK, 'P', TMResource.BRONZE.getStackName(Type.PLATE), 'F', Blocks.FURNACE, '-', CraftingMaterial.BRONZE_PIPE.getStack()});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.steamAlloySmelter), 350, getResearchList(ResearchLoader.bronzeAlloySmelter), null, CraftingLevel.BASIC, new Object[]{"PPP", "FCF", "B-B", 'B', Blocks.BRICK_BLOCK, 'P', TMResource.BRONZE.getStackName(Type.PLATE), 'F', Blocks.FURNACE, 'C', CoreInit.MachineFrameBronze, '-', CraftingMaterial.BRONZE_PIPE.getStack()});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.steamPlateBlender), 350, getResearchList(ResearchLoader.bronzePlateBlender), null, CraftingLevel.BASIC, new Object[]{"PSP", "-C-", "BBB", 'B', Blocks.BRICK_BLOCK, 'P', TMResource.BRONZE.getStackName(Type.PLATE), 'S', TMResource.BRONZE.getBlockOreDictName(), 'C', CoreInit.MachineFrameBronze, '-', CraftingMaterial.BRONZE_PIPE.getStack()});
		AdvancedCraftingHandler.addShapelessRecipe(CraftingMaterial.REFINED_CLAY.getStackNormal(4), 50, getResearchList(ResearchLoader.refinedBricks), null, CraftingLevel.BASIC, new Object[]{Items.CLAY_BALL, Items.CLAY_BALL, CraftingMaterial.NETHERRACK_DUST.getStack(), Blocks.SAND, Blocks.SAND, TMResource.COAL.getStackName(Type.DUST), TMResource.IRON.getStackName(Type.DUST), TMResource.OBSIDIAN.getStackName(Type.DUST), Blocks.GRAVEL});
		AdvancedCraftingHandler.addRecipe(CraftingMaterial.BASIC_CIRCUIT.getStackNormal(), 500, getResearchList(ResearchLoader.soldering), null, CraftingLevel.SOLDERING_STATION, new Object[]{" C ", "RPR", "-R-", 'C', CraftingMaterial.BASIC_CIRCUIT_COMPONENT.getStack(), 'R', Items.REDSTONE, 'P', CraftingMaterial.BASIC_CIRCUIT_PLATE.getStack(), '-', TMResource.COPPER.getStackName(Type.CABLE)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.steamSolderingStation), 450, getResearchList(ResearchLoader.soldering), null, CraftingLevel.BASIC, new Object[]{"PSP", "-C-", "B-B", 'B', Blocks.BRICK_BLOCK, 'P', TMResource.BRONZE.getStackName(Type.PLATE), 'C', CoreInit.MachineFrameBronze, '-', CraftingMaterial.BRONZE_PIPE.getStack(), 'S', TMResource.STEEL.getStackName(Type.PLATE)});
		AdvancedCraftingHandler.addRecipe(CraftingMaterial.RAW_SILICON.getStackNormal(), 60, getResearchList(ResearchLoader.rubberProcessing), null, CraftingLevel.BASIC, new Object[]{"SSS", "SCS", "SSS", 'C', "blockCoal", 'S', Blocks.SAND});
		AdvancedCraftingHandler.addRecipe(CraftingMaterial.BASIC_CIRCUIT_COMPONENT.getStackNormal(), 600, getResearchList(ResearchLoader.soldering), null, CraftingLevel.BASIC, new Object[]{"RSR", "-P-", "RBR", 'R', Items.REDSTONE, 'S', CraftingMaterial.SILICON_PLATE.getStack(), '-', TMResource.COPPER.getStackName(Type.CABLE), 'P', TMResource.IRON.getStackName(Type.PLATE), 'B', CraftingMaterial.RUBBER.getStack()});
		AdvancedCraftingHandler.addRecipe(CraftingMaterial.RAW_CICRUIT_BOARD.getStackNormal(2), 600, getResearchList(ResearchLoader.soldering), null, CraftingLevel.BASIC, new Object[]{"CCC", "GGG", "RRR", 'C', TMResource.COPPER.getStackName(Type.PLATE), 'G', CraftingMaterial.GLASS_MESH.getStack(), 'R', CraftingMaterial.RUBBER.getStack()});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.cokeOvenWall, brickAmount), 600, getResearchList(ResearchLoader.cokeOven), null, CraftingLevel.BASIC, new Object[]{"BSB", "SRS", "BSB", 'S', Blocks.SAND, 'B', Items.BRICK, 'R', CraftingMaterial.REFINED_BRICK.getStack()});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.blastFurnaceWall, brickAmount), 600, getResearchList(ResearchLoader.blastFurnace), null, CraftingLevel.BASIC, new Object[]{"BSB", "SRS", "BSB", 'S', Blocks.SOUL_SAND, 'B', Blocks.NETHER_BRICK, 'R', CraftingMaterial.REFINED_BRICK.getStack()});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.cokeOven), 600, getResearchList(ResearchLoader.cokeOven), null, CraftingLevel.BASIC, new Object[]{"BSB", "SRS", "BSB", 'S', CraftingMaterial.REFINED_BRICK.getStack(), 'B', FactoryInit.cokeOvenWall, 'R', Blocks.SAND});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.blastFurnace), 600, getResearchList(ResearchLoader.blastFurnace), null, CraftingLevel.BASIC, new Object[]{"BSB", "SRS", "BSB", 'S', CraftingMaterial.REFINED_BRICK.getStack(), 'B', FactoryInit.blastFurnaceWall, 'R', Blocks.SOUL_SAND});
		AdvancedCraftingHandler.addRecipe(new ItemStack(EnergyInit.multimeter), 800, getResearchList(ResearchLoader.multimeter), null, CraftingLevel.SOLDERING_STATION, new Object[]{"IGI", "-C-", "IPI", 'C', Items.COMPASS, '-', TMResource.COPPER.getStackName(Type.CABLE), 'I', TMResource.IRON.getStackName(Type.PLATE), 'P', CraftingMaterial.BASIC_CIRCUIT.getStack(), 'G', "paneGlass"});
		AdvancedCraftingHandler.addRecipe(new ItemStack(EnergyInit.lvCable, 3), 850, getResearchList(ResearchLoader.lvCable), null, CraftingLevel.BASIC, new Object[]{"R-R", "R-R", "R-R", 'R', CraftingMaterial.RUBBER.getStack(), '-', TMResource.COPPER.getStackName(Type.CABLE)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(EnergyInit.lvCable, 3), 850, getResearchList(ResearchLoader.lvCable), null, CraftingLevel.BASIC, new Object[]{"RRR", "---", "RRR", 'R', CraftingMaterial.RUBBER.getStack(), '-', TMResource.COPPER.getStackName(Type.CABLE)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(EnergyInit.batteryBox), 900, getResearchList(ResearchLoader.batteryBox), null, CraftingLevel.BASIC, new Object[]{"W-W", "BBB", "WWW", 'W', "plankWood", '-', TMResource.COPPER.getStackName(Type.CABLE), 'B', CoreInit.Battery});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.crusher, 1, getMeta(LV)), 1000, getResearchList(ResearchLoader.basicLvMachines), null, CraftingLevel.BASIC, new Object[]{"SHS", "ECE", "SES", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'E', CraftingMaterial.BASIC_CIRCUIT.getStack(), 'C', CoreInit.MachineFrameBasic, 'H', CraftingMaterial.WOLFRAMIUM_GRINDER.getStack()});
		AdvancedCraftingHandler.addRecipe(CraftingMaterial.WOLFRAMIUM_GRINDER.getStackNormal(), 1000, getResearchList(ResearchLoader.basicLvMachines), null, CraftingLevel.BASIC, new Object[]{"WSW", "SBS", "WSW", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'W', TMResource.WOLFRAM.getStackName(Type.PLATE), 'B', TMResource.STEEL.getBlockOreDictName()});
		//AdvancedCraftingHandler.addRecipe(CraftingMaterial.DIAMOND_GRINDER.getStackNormal(), 1000, getResearchList(ResearchLoader.basicLvMachines), null, CraftingLevel.BASIC, new Object[]{"WSW", "SBS", "WSW", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'W', TMResource.WOLFRAM.getStackName(Type.PLATE), 'B', TMResource.STEEL.getBlockOreDictName()});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.electricFurnace, 1, getMeta(LV)), 1000, getResearchList(ResearchLoader.basicLvMachines), null, CraftingLevel.BASIC, new Object[]{"IEI", "RFR", "ICI", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'E', CraftingMaterial.BASIC_CIRCUIT.getStack(), 'C', CoreInit.MachineFrameBasic, 'R', Items.REDSTONE, 'F', FactoryInit.advSteamFurnace, 'I', TMResource.IRON.getStackName(Type.PLATE)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.wireMill, 1, getMeta(LV)), 1000, getResearchList(ResearchLoader.wireProcessing), null, CraftingLevel.BASIC, new Object[]{"IDI", "ECE", "IPI", 'P', "blockPiston", 'E', CraftingMaterial.BASIC_CIRCUIT.getStack(), 'C', CoreInit.MachineFrameBasic, 'D', Items.DIAMOND, 'I', TMResource.IRON.getStackName(Type.PLATE)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(CoreInit.emptyWireCoil, 8), 100, getResearchList(ResearchLoader.wireProcessing), null, CraftingLevel.BASIC, new Object[]{"III", " I ", "III", 'I', TMResource.IRON.getStackName(Type.PLATE)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(CoreInit.emptyWireCoil, 6), 110, getResearchList(ResearchLoader.wireProcessing), null, CraftingLevel.BASIC, new Object[]{"III", " I ", "III", 'I', TMResource.COPPER.getStackName(Type.PLATE)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(CoreInit.emptyWireCoil, 6), 110, getResearchList(ResearchLoader.wireProcessing), null, CraftingLevel.BASIC, new Object[]{"III", " I ", "III", 'I', TMResource.TIN.getStackName(Type.PLATE)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(CoreInit.emptyWireCoil, 8), 110, getResearchList(ResearchLoader.wireProcessing), null, CraftingLevel.BASIC, new Object[]{"III", " I ", "III", 'I', TMResource.BRONZE.getStackName(Type.PLATE)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.coilerPlant, 1, getMeta(LV)), 1100, getResearchList(ResearchLoader.wireProcessing), null, CraftingLevel.BASIC, new Object[]{"IWI", "ECE", "IPI", 'P', "blockPiston", 'E', CraftingMaterial.BASIC_CIRCUIT.getStack(), 'C', CoreInit.MachineFrameBasic, 'I', TMResource.IRON.getStackName(Type.PLATE), 'W', CoreInit.emptyWireCoil});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.extruderModule, 1, 0), 1100, getResearchList(ResearchLoader.basicLvMachines), null, CraftingLevel.BASIC, new Object[]{"WDW", "SBS", "SWS", 'W', TMResource.WOLFRAM.getStackName(Type.PLATE), 'S', TMResource.STEEL.getStackName(Type.PLATE), 'D', Items.DIAMOND, 'B', CraftingMaterial.UPGRADE_FRAME.getStack()});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.speedUpgrade), 1800, getResearchList(ResearchLoader.speedUpgrade), null, CraftingLevel.BASIC, new Object[]{"RBR", "BFB", "RCR", 'R', Blocks.REDSTONE_BLOCK, 'F', CraftingMaterial.UPGRADE_FRAME.getStack(), 'B', TMResource.BLUE_METAL.getStackName(Type.PLATE), 'C', TMResource.ELECTRUM.getStackName(Type.COIL)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.plateBlendingMachine, 1, getMeta(LV)), 1000, getResearchList(ResearchLoader.lvPlateBlender), null, CraftingLevel.BASIC, new Object[]{"PEP", "ICI", "IEI", 'P', "blockPiston", 'E', CraftingMaterial.BASIC_CIRCUIT.getStack(), 'C', CoreInit.MachineFrameBasic, 'I', TMResource.IRON.getStackName(Type.PLATE)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(CoreInit.Battery), 300, getResearchList(ResearchLoader.batteryBox), null, CraftingLevel.BASIC, new Object[]{" - ", "TBT", "TRT", '-', TMResource.COPPER.getStackName(Type.CABLE), 'T', TMResource.TIN.getStackName(Type.PLATE), 'B', TMResource.BLUE_METAL.getStackName(Type.INGOT), 'R', Items.REDSTONE});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.alloySmelter, 1, getMeta(LV)), 1000, getResearchList(ResearchLoader.lvAlloySmelter), null, CraftingLevel.BASIC, new Object[]{"IHI", "EFE", "IPI", 'P', "blockPiston", 'E', CraftingMaterial.BASIC_CIRCUIT.getStack(), 'F', new ItemStack(FactoryInit.electricFurnace, 1, getMeta(LV)), 'I', TMResource.IRON.getStackName(Type.PLATE), 'H', CraftingMaterial.CUPRONICKEL_HEATING_COIL.getStack()});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.waterCollector), 700, getResearchList(ResearchLoader.waterCollector), null, CraftingLevel.BASIC, new Object[]{"BIB", "IWI", "BRB", 'B', TMResource.BRONZE.getStackName(Type.PLATE), 'W', Items.WATER_BUCKET, 'I', Blocks.IRON_BARS, 'R', Items.REDSTONE});
		AdvancedCraftingHandler.addRecipe(new ItemStack(EnergyInit.steamTurbine), 1000, getResearchList(ResearchLoader.basicTurbine), null, CraftingLevel.BASIC, new Object[]{"-E-", "TCT", "G_G", 'E', CraftingMaterial.BASIC_CIRCUIT.getStack(), '-', CraftingMaterial.BRONZE_PIPE.getStack(), 'C', CoreInit.MachineFrameBasic, '_', TMResource.TIN.getStackName(Type.CABLE), 'T', CraftingMaterial.TIN_TURBINE.getStack(), 'G', CraftingMaterial.GENERATOR_COMPONENT.getStack()});
		AdvancedCraftingHandler.addRecipe(CraftingMaterial.GENERATOR_COMPONENT.getStackNormal(), 500, getResearchList(ResearchLoader.basicTurbine), null, CraftingLevel.BASIC, new Object[]{"TCI", "CRC", "ICT", 'T', TMResource.TIN.getStackName(Type.CABLE), 'C', TMResource.COPPER.getStackName(Type.CABLE), 'R', Items.REDSTONE, 'I', CraftingMaterial.IRON_ROD.getStack()});
		if(Config.addUnbreakableElytraRecipe){
			ItemStack elytraStack = new ItemStack(Items.ELYTRA);
			NBTTagCompound elytraTag = new NBTTagCompound();
			elytraTag.setBoolean("Unbreakable", true);
			elytraStack.setTagCompound(elytraTag);
			AdvancedCraftingHandler.addRecipe(elytraStack, 20000, getResearchList(ResearchLoader.basicLvMachines), null, CraftingLevel.BASIC, new Object[]{"DND", "WEW", "F F", 'D', Blocks.DIAMOND_BLOCK, 'N', Items.NETHER_STAR, 'W', TMResource.TUNGSTENSTEEL.getStackName(Type.PLATE), 'E', Items.ELYTRA, 'F', Items.FEATHER});
		}
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.solderingStation), 1100, getResearchList(ResearchLoader.eSolderingStation), null, CraftingLevel.BASIC, new Object[]{"IHI", "RCR", "IEI", 'I', TMResource.IRON.getStackName(Type.PLATE), 'H', CraftingMaterial.CUPRONICKEL_HEATING_COIL.getStack(), 'R', Items.REDSTONE, 'C', CoreInit.MachineFrameBasic, 'E', CraftingMaterial.BASIC_CIRCUIT.getStack()});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.pump, 1, getMeta(LV)), 1000, getResearchList(ResearchLoader.pump), null, CraftingLevel.BASIC, new Object[]{"ITI", "BCB", "IPI", 'I', TMResource.IRON.getStackName(Type.PLATE), 'C', CoreInit.MachineFrameBasic, 'B', Items.BUCKET, 'T', StorageInit.tankBasic, 'P', CoreInit.itemPump});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.fluidTransposer, 1, getMeta(LV)), 1100, getResearchList(ResearchLoader.fluidTransposer), null, CraftingLevel.BASIC, new Object[]{"IEI", "TCB", "IPI", 'I', TMResource.IRON.getStackName(Type.PLATE), 'C', CoreInit.MachineFrameBasic, 'B', Items.BUCKET, 'T', StorageInit.tankBasic, 'P', CoreInit.itemPump, 'E', CraftingMaterial.BASIC_CIRCUIT.getStack()});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.fluidTransposer, 1, getMeta(LV)), 1100, getResearchList(ResearchLoader.fluidTransposer), null, CraftingLevel.BASIC, new Object[]{"IEI", "BCT", "IPI", 'I', TMResource.IRON.getStackName(Type.PLATE), 'C', CoreInit.MachineFrameBasic, 'B', Items.BUCKET, 'T', StorageInit.tankBasic, 'P', CoreInit.itemPump, 'E', CraftingMaterial.BASIC_CIRCUIT.getStack()});
		AdvancedCraftingHandler.addRecipe(new ItemStack(StorageInit.tankAdv), 300, getResearchList(ResearchLoader.advancedTank), null, CraftingLevel.BASIC, new Object[]{"SGS", "GTG", "SGS", 'T', StorageInit.tankBasic, 'G', "blockGlassColorless", 'S', TMResource.STEEL.getStackName(Type.PLATE)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(FactoryInit.industrialBlastFurnace, 1, getMeta(LV)), 1200, getResearchList(ResearchLoader.industrialBlastFurnace), null, CraftingLevel.BASIC, new Object[]{"EHE", "HCH", "FHF", 'E', CraftingMaterial.BASIC_CIRCUIT.getStack(), 'H', CraftingMaterial.CUPRONICKEL_HEATING_COIL.getStack(), 'C', CoreInit.MachineFrameSteel, 'F', new ItemStack(FactoryInit.electricFurnace, 1, getMeta(LV))});
		AdvancedCraftingHandler.addRecipe(CraftingMaterial.SOLAR_PANEL_MK1.getStackNormal(2), 600, getResearchList(ResearchLoader.solarPanel), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"PPP", "BSB", "RLR", 'P', "paneGlassColorless", 'B', "dyeBlue", 'S', CraftingMaterial.SILICON_PLATE.getStack(), 'R', Items.REDSTONE, 'L', TMResource.LEAD.getStackName(Type.NUGGET)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(EnergyInit.solarPanel, 2), 1200, getResearchList(ResearchLoader.solarPanel), null, CraftingLevel.BASIC, new Object[]{"SSS", "RER", "P-P", 'S', CraftingMaterial.SOLAR_PANEL_MK1.getStackNormal(), 'R', Items.REDSTONE, 'E', CraftingMaterial.BASIC_CIRCUIT.getStack(), 'P', TMResource.STEEL.getStackName(Type.PLATE), '-', TMResource.COPPER.getStackName(Type.CABLE)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(EnergyInit.transformerLMV), 1500, getResearchList(ResearchLoader.mvTransformer), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"CIC", "-F_", "EIE", '-', TMResource.COPPER.getStackName(Type.CABLE), '_', TMResource.ELECTRUM.getStackName(Type.CABLE), 'I', Items.IRON_INGOT, 'F', CoreInit.MachineFrameSteel, 'C', TMResource.COPPER.getStackName(Type.COIL), 'E', TMResource.ELECTRUM.getStackName(Type.COIL)});
		AdvancedCraftingHandler.addRecipe(new ItemStack(EnergyInit.mvCable, 2), 1200, getResearchList(ResearchLoader.mvCable), null, CraftingLevel.BASIC, new Object[]{"RRR", "---", "RRR", 'R', CraftingMaterial.RUBBER.getStack(), '-', TMResource.ELECTRUM.getStackName(Type.CABLE)});
		Set<Entry<BlockMachineBase, Block>> mSet = machines.entrySet();
		for(Entry<BlockMachineBase, Block> m : mSet){
			AdvancedCraftingHandler.addRecipe(new ItemStack(m.getKey(), 1, getMeta(MV)), 1500, getResearchList(ResearchLoader.mvMachines), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"STS", "FM-", "SES", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'T', EnergyInit.transformerLMV, 'F', m.getValue(), 'M', new ItemStack(m.getKey(), 1, getMeta(LV)), '-', TMResource.ELECTRUM.getStackName(Type.CABLE), 'E', CraftingMaterial.NORMAL_CIRCUIT.getStack()});
			AdvancedCraftingHandler.addRecipe(new ItemStack(m.getKey(), 1, getMeta(MV)), 1500, getResearchList(ResearchLoader.mvMachines), null, CraftingLevel.E_SOLDERING_STATION, new Object[]{"STS", "-MF", "SES", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'T', EnergyInit.transformerLMV, 'F', m.getValue(), 'M', new ItemStack(m.getKey(), 1, getMeta(LV)), '-', TMResource.ELECTRUM.getStackName(Type.CABLE), 'E', CraftingMaterial.NORMAL_CIRCUIT.getStack()});
		}
		AdvancedCraftingHandler.addRecipe(CraftingMaterial.NORMAL_CIRCUIT_COMPONENT.getStackNormal(), 1200, getResearchList(ResearchLoader.mvCircuit), null, CraftingLevel.BASIC, new Object[]{"-E-", "SSS", "R-R", 'R', Items.REDSTONE, '-', TMResource.ELECTRUM.getStackName(Type.CABLE), 'E', CraftingMaterial.BASIC_CIRCUIT.getStack(), 'S', CraftingMaterial.SILICON_PLATE.getStack()});
		AdvancedCraftingHandler.addRecipe(CraftingMaterial.NORMAL_CIRCUIT.getStackNormal(), 1200, getResearchList(ResearchLoader.mvCircuit), null, CraftingLevel.SOLDERING_STATION, new Object[]{"RCR", "RPR", "-R-", 'R', Items.REDSTONE, '-', TMResource.ELECTRUM.getStackName(Type.CABLE), 'P', CraftingMaterial.BASIC_CIRCUIT_PLATE.getStack(), 'C', CraftingMaterial.NORMAL_CIRCUIT_COMPONENT.getStack()});
	}
	public static List<IResearch> getResearchList(IResearch... in){
		List<IResearch> list = new ArrayList<IResearch>();
		if(in != null){
			for(IResearch a : in){
				list.add(a);
			}
		}
		return list;
	}
	public static List<IScanningInformation> getScanningList(IScanningInformation... in){
		List<IScanningInformation> list = new ArrayList<IScanningInformation>();
		if(in != null){
			for(IScanningInformation a : in){
				list.add(a);
			}
		}
		return list;
	}
	private static int getMeta(EnergyType type){
		return TileEntityMachineBase.getMetaFromEnergyType(type);
	}
}
