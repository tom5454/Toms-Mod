package com.tom.recipes;

import static com.tom.api.recipes.RecipeHelper.*;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.tom.api.recipes.RecipeHelper.Condition;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.defense.DefenseInit;
import com.tom.energy.EnergyInit;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.BlockComponents.ComponentVariants;
import com.tom.storage.StorageInit;
import com.tom.storage.multipart.block.StorageNetworkCable;
import com.tom.toolsAndCombat.ToolsInit;
import com.tom.transport.TransportInit;

public class CraftingRecipes {// OreDictionary AdvancedCraftingRecipes OreDict
	// ResearchLoader
	public static void init() {
		int machineFrameAmount = /*Config.enableHardRecipes ? 2 : */4;
		addRecipe(CraftingMaterial.BIG_REDSTONE.getStackNormal(), new Object[]{"RR", 'R', Items.REDSTONE});
		addRecipe(CraftingMaterial.BIG_GLOWSTONE.getStackNormal(), new Object[]{"GG", 'G', Items.GLOWSTONE_DUST});
		addRecipe(CraftingMaterial.BIG_ENDER_PEARL.getStackNormal(), new Object[]{"EE", 'E', Items.ENDER_PEARL});
		addRecipe(CraftingMaterial.BIG_REDSTONE.getStackNormal(), new Object[]{"R", "R", 'R', Items.REDSTONE});
		addRecipe(CraftingMaterial.BIG_GLOWSTONE.getStackNormal(), new Object[]{"G", "G", 'G', Items.GLOWSTONE_DUST});
		addRecipe(CraftingMaterial.BIG_ENDER_PEARL.getStackNormal(), new Object[]{"E", "E", 'E', Items.ENDER_PEARL});
		addRecipe(new ItemStack(CoreInit.MachineFrameChrome, machineFrameAmount), new Object[]{"CCC", "CHC", "CCC", 'C', TMResource.CHROME.getStackName(Type.PLATE), 'H', "itemHammer_lvl3"});
		addRecipe(new ItemStack(CoreInit.MachineFrameBronze, machineFrameAmount), new Object[]{"CBC", "BHB", "bBb", 'C', TMResource.BRONZE.getStackName(Type.PLATE), 'H', "itemHammer_lvl2", 'B', TMResource.BRASS.getStackName(Type.PLATE), 'b', Blocks.BRICK_BLOCK});
		addRecipe(new ItemStack(CoreInit.MachineFrameSteel, machineFrameAmount), new Object[]{"CCC", "CHC", "CCC", 'C', TMResource.STEEL.getStackName(Type.PLATE), 'H', "itemHammer_lvl2"});
		addRecipe(new ItemStack(CoreInit.MachineFrameBasic, machineFrameAmount), new Object[]{"CCC", "CHC", "CCC", 'C', CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStack(), 'H', "itemHammer_lvl2"});
		addRecipe(new ItemStack(CoreInit.MachineFrameTitanium, machineFrameAmount), new Object[]{"CCC", "CHC", "CCC", 'C', TMResource.TITANIUM.getStackName(Type.PLATE), 'H', "itemHammer_lvl4"});
		addRecipe(new ItemStack(CoreInit.MachineFrameAluminum, machineFrameAmount), new Object[]{"CCC", "CHC", "CCC", 'C', TMResource.ALUMINUM.getStackName(Type.PLATE), 'H', "itemHammer_lvl3"});
		addRecipe(new ItemStack(CoreInit.memoryCard, 1), new Object[]{"CCC", "IBI", "RRR", 'I', "ingotIron", 'C', TMResource.COPPER.getStackName(Type.CABLE), 'R', Items.REDSTONE, 'B', TMResource.BLUE_METAL.getStackName(Type.INGOT)});
		addRecipe(new ItemStack(CoreInit.linker, 1), new Object[]{"IRI", "IMI", 'I', "ingotIron", 'M', CoreInit.memoryCard, 'R', Items.REDSTONE});
		addRecipe(new ItemStack(EnergyInit.FusionFluidInjector, 1), new Object[]{"CBC", "CPC", "CBC", 'C', TMResource.CHROME.getStackName(Type.PLATE), 'B', TMResource.BLUE_METAL.getStackName(Type.INGOT), 'P', CraftingMaterial.PUMP.getStackNormal()});
		addRecipe(new ItemStack(EnergyInit.FusionFluidExtractor, 1), new Object[]{"CCC", "BPB", "CCC", 'C', TMResource.CHROME.getStackName(Type.PLATE), 'B', TMResource.BLUE_METAL.getStackName(Type.INGOT), 'P', CraftingMaterial.PUMP.getStackNormal()});
		addRecipe(new ItemStack(EnergyInit.FusionCore, 2), new Object[]{"CBC", "BTB", "CBC", 'C', TMResource.CHROME.getStackName(Type.PLATE), 'B', TMResource.BLUE_METAL.getStackName(Type.INGOT), 'T', TMResource.TITANIUM.getStackName(Type.PLATE)});
		addRecipe(CraftingMaterial.DISPLAY.getStackNormal(), new Object[]{"IGI", "IPI", "IRI", 'G', CraftingMaterial.CHARGED_GLOWSTONE.getStack(), 'P', "paneGlassColorless", 'I', "ingotIron", 'R', CraftingMaterial.CHARGED_REDSTONE.getStack()});
		addRecipe(CraftingMaterial.PUMP.getStackNormal(), new Object[]{" II", "IRI", "II ", 'I', "ingotIron", 'R', CraftingMaterial.CHARGED_REDSTONE.getStack()});
		addRecipe(new ItemStack(EnergyInit.FusionCharger, 1), new Object[]{"CRC", "CBC", "CRC", 'C', TMResource.CHROME.getStackName(Type.PLATE), 'R', CraftingMaterial.CHARGED_REDSTONE.getStack(), 'B', TMResource.BLUE_METAL.getStackName(Type.INGOT)});
		addRecipe(new ItemStack(EnergyInit.FusionInjector, 1), new Object[]{"CRC", "CRC", "CRC", 'C', TMResource.CHROME.getStackName(Type.PLATE), 'R', CraftingMaterial.CHARGED_REDSTONE.getStack()});
		addRecipe(new ItemStack(EnergyInit.FusionController, 1), new Object[]{"CGC", "CRC", "CCC", 'G', TMResource.advAlloyMK2.getStackName(Type.PLATE), 'R', CraftingMaterial.CHARGED_REDSTONE.getStack(), 'C', TMResource.CHROME.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(CoreInit.uraniumRodEmpty, 2), new Object[]{"III", "I I", " I ", 'I', "ingotIron"});
		addShapelessRecipe(new ItemStack(CoreInit.uraniumRod), new Object[]{CoreInit.uraniumRodEmpty, "ingotUranium"});
		addRecipe(new ItemStack(CoreInit.rsDoor, 1), new Object[]{"III", "IDI", "RRR", 'I', "ingotIron", 'D', Items.OAK_DOOR, 'R', CraftingMaterial.CHARGED_REDSTONE.getStack()});
		{
			Condition cond = Config.requireConfig("research");
			addShapelessRecipe(new ItemStack(CoreInit.bigNoteBook), cond, new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.PAPER), new ItemStack(Items.PAPER));
			addRecipe(new ItemStack(CoreInit.researchTable, 2), new Object[]{cond, "BSI", "WCW", "RPR", 'B', Items.GLASS_BOTTLE, 'S', "slabWood", 'I', "dyeBlack", 'W', "plankWood", 'C', Blocks.CRAFTING_TABLE, 'R', Items.STICK, 'P', Items.PAPER});
			addRecipe(new ItemStack(CoreInit.magnifyingGlass, 1), new Object[]{cond, " G", "I ", 'I', "stickWood", 'G', "paneGlassColorless"});
			addShapelessRecipe(new ItemStack(CoreInit.noteBook, 1), new Object[]{cond, Items.WRITABLE_BOOK});
		}
		addShapelessRecipe(new ItemStack(ToolsInit.portableComparator), new ItemStack(Items.COMPARATOR), new ItemStack(Items.REDSTONE), new ItemStack(Items.COMPASS));
		addShapelessRecipe(new ItemStack(DefenseInit.projectorFieldType, 1, 3), new ItemStack(DefenseInit.projectorFieldType, 1, 3));
		addRecipe(CraftingMaterial.IRON_ROD.getStackNormal(), new Object[]{"I", "I", "I", 'I', "ingotIron"});
		addRecipe(CraftingMaterial.IRON_ROD.getStackNormal(4), new Object[]{" I", "HI", " I", 'I', "plateIron", 'H', "itemHammer_lvl1"});
		addRecipe(CraftingMaterial.HOT_COPPER_HAMMER_HEAD.getStackNormal(), new Object[]{" H ", "III", "III", 'I', CraftingMaterial.HOT_COPPER.getStackNormal(), 'H', "itemHammer_lvl0"});
		addShapelessRecipe(TMResource.BRONZE.getStackNormal(Type.DUST, 4), new Object[]{TMResource.COPPER.getStackName(Type.DUST), TMResource.COPPER.getStackName(Type.DUST), TMResource.COPPER.getStackName(Type.DUST), TMResource.TIN.getStackName(Type.DUST)});
		addRecipe(CraftingMaterial.STONE_BOWL.getStackNormal(3), new Object[]{"S S", " S ", 'S', Blocks.STONE});
		addRecipe(new ItemStack(CoreInit.wrench), new Object[]{"S S", "BbB", " I ", 'B', "plateBronze", 'b', TMResource.BLUE_METAL.getStackName(Type.PLATE), 'S', "plateSteel", 'I', CraftingMaterial.IRON_ROD.getStack()});
		addShapelessRecipe(TMResource.ELECTRUM.getStackNormal(Type.DUST, 2), new Object[]{TMResource.GOLD.getStackName(Type.DUST), TMResource.SILVER.getStackName(Type.DUST)});
		addRecipe(new ItemStack(StorageInit.tankBasic), new Object[]{"OGO", "GGG", "OGO", 'O', TMResource.COPPER.getStackName(Type.PLATE), 'G', "paneGlassColorless"});
		addRecipe(new ItemStack(CoreInit.treeTap, 2), new Object[]{" _S", "WWW", "  W", 'W', "plankWood", '_', "slabWood", 'S', Items.STICK});
		addRecipe(TMResource.COAL.getHammerStack(1), new Object[]{" H", "FS", " S", 'S', Items.STICK, 'F', "itemFlint", 'H', CraftingMaterial.FLINT_HAMMER_HEAD.getStackNormal()});
		addRecipe(CraftingMaterial.BRONZE_PIPE.getStackNormal(6), new Object[]{"P P", "PHP", "P P", 'P', TMResource.BRONZE.getStackName(Type.PLATE), 'H', "itemHammer_lvl2"});
		addRecipe(new ItemStack(StorageInit.basicTerminal), new Object[]{"IGI", "TCG", "IGI", 'I', TMResource.IRON.getStackName(Type.PLATE), 'T', StorageInit.partTerminal, 'G', "blockGlassColorless", 'C', "storageNetworkCable"});
		addRecipe(new ItemStack(StorageInit.craftingTerminal), new Object[]{"IGI", "TCG", "IGI", 'I', TMResource.IRON.getStackName(Type.PLATE), 'T', StorageInit.partCraftingTerminal, 'G', "blockGlassColorless", 'C', "storageNetworkCable"});
		addRecipe(new ItemStack(StorageInit.patternTerminal), new Object[]{"IGI", "TCG", "IGI", 'I', TMResource.IRON.getStackName(Type.PLATE), 'T', StorageInit.partPatternTerminal, 'G', "blockGlassColorless", 'C', "storageNetworkCable"});
		addRecipe(new ItemStack(StorageInit.basicTerminal), new Object[]{"IGI", "TCG", "IGI", 'I', TMResource.ALUMINUM.getStackName(Type.PLATE), 'T', StorageInit.partTerminal, 'G', "blockGlassColorless", 'C', "storageNetworkCable"});
		addRecipe(new ItemStack(StorageInit.craftingTerminal), new Object[]{"IGI", "TCG", "IGI", 'I', TMResource.ALUMINUM.getStackName(Type.PLATE), 'T', StorageInit.partCraftingTerminal, 'G', "blockGlassColorless", 'C', "storageNetworkCable"});
		addRecipe(new ItemStack(StorageInit.patternTerminal), new Object[]{"IGI", "TCG", "IGI", 'I', TMResource.ALUMINUM.getStackName(Type.PLATE), 'T', StorageInit.partPatternTerminal, 'G', "blockGlassColorless", 'C', "storageNetworkCable"});
		addRecipe(new ItemStack(FactoryInit.blockCoalCoke), new Object[]{"CCC", "CCC", "CCC", 'C', FactoryInit.coalCoke});
		addShapelessRecipe(new ItemStack(FactoryInit.coalCoke, 9), new Object[]{FactoryInit.blockCoalCoke});
		addRecipe(CraftingMaterial.GLASS_FIBER.getStackNormal(2), new Object[]{"GG", "GG", 'G', CraftingMaterial.GLASS_DUST.getStack()});
		addRecipe(CraftingMaterial.GLASS_MESH.getStackNormal(), new Object[]{"GG", "GG", 'G', CraftingMaterial.GLASS_FIBER.getStack()});
		addShapelessRecipe(CraftingMaterial.GLASS_DUST.getStackNormal(), new Object[]{"blockGlass", "itemMortar"});
		addShapelessRecipe(CraftingMaterial.ENDERIUM_BASE.getStackNormal(4), new Object[]{"dustTin", "dustTin", "dustSilver", "dustPlatinum"});
		addRecipe(new ItemStack(Blocks.STICKY_PISTON), new Object[]{"R", "P", 'R', CraftingMaterial.BOTTLE_OF_RESIN.getStack(), 'P', Blocks.PISTON});
		addRecipe(CraftingMaterial.TIN_TURBINE.getStackNormal(), new Object[]{"PHP", "NIN", "PNP", 'P', TMResource.TIN.getStackNormal(Type.PLATE), 'H', "itemHammer_lvl2", 'I', TMResource.TIN.getStackName(Type.INGOT), 'N', TMResource.TIN.getStackName(Type.NUGGET)});
		addRecipe(new ItemStack(TransportInit.fluidServo, 2), new Object[]{"PGP", "NRN", 'P', TMResource.IRON.getStackNormal(Type.PLATE), 'G', "blockGlassColorless", 'R', Items.REDSTONE, 'N', TMResource.IRON.getStackName(Type.NUGGET)});
		addRecipe(CraftingMaterial.STEEL_PIPE.getStackNormal(6), new Object[]{"P P", "PHP", "P P", 'P', TMResource.STEEL.getStackName(Type.PLATE), 'H', "itemHammer_lvl2"});
		addRecipe(new ItemStack(FactoryInit.components, 2, ComponentVariants.ENGINEERING_BLOCK.ordinal()), new Object[]{"SAS", "-B-", "SAS", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'A', TMResource.ALUMINUM.getStackName(Type.PLATE), 'B', TMResource.BRONZE.getStackName(Type.PLATE), '-', CraftingMaterial.STEEL_PIPE.getStack()});
		addRecipe(new ItemStack(FactoryInit.components, 1, ComponentVariants.OUTPUT_HATCH.ordinal()), new Object[]{"S-S", "BPC", 'S', TMResource.STEEL.getStackName(Type.PLATE), '-', CraftingMaterial.STEEL_PIPE.getStack(), 'B', Items.BUCKET, 'C', "chest", 'P', "blockPiston"});
		addRecipe(new ItemStack(FactoryInit.components, 1, ComponentVariants.REFINERY_HEATER.ordinal()), new Object[]{"S-S", "A A", "SLS", 'S', TMResource.STEEL.getStackName(Type.PLATE), '-', CraftingMaterial.STEEL_PIPE.getStack(), 'A', TMResource.ALUMINUM.getStackName(Type.PLATE), 'L', Items.LAVA_BUCKET});
		addRecipe(CoreInit.circuitDrawingPen.getDamaged(), new Object[]{"  F", "NS ", "RN ", 'F', Items.FEATHER, 'S', Items.STICK, 'R', Items.REDSTONE, 'N', "nuggetIron"});
		addRecipe(CraftingMaterial.BLUEPRINT_PAPER.getStackNormal(4), new Object[]{"PPP", "PBP", "PPP", 'B', "dyeBlue", 'P', Items.PAPER});
		addRecipe(CraftingMaterial.BIG_BLUEPRINT_PAPER.getStackNormal(), new Object[]{"PPP", "PPP", "PPP", 'P', CraftingMaterial.BLUEPRINT_PAPER.getStack()});
		addRecipe(new ItemStack(CoreInit.acidResistantInkBottle), new Object[]{"IRI", "IBI", "IRI", 'I', "dyeBlack", 'R', Items.REDSTONE, 'B', Items.GLASS_BOTTLE});
		addShapelessRecipe(new ItemStack(CoreInit.circuitDrawingPen), new Object[]{CoreInit.acidResistantInkBottle, CoreInit.circuitDrawingPen.getDamaged()});
		addRecipe(CraftingMaterial.RAW_CHALK.getStackNormal(), new Object[]{" BB", "BCB", "CB ", 'C', Items.CLAY_BALL, 'B', "dyeWhite"});
		addRecipe(CraftingMaterial.TIN_CAN.getStackNormal(2), new Object[]{" T ", "T T", " T ", 'T', TMResource.TIN.getStackName(Type.PLATE)});
		addShapelessRecipe(AdvancedCraftingRecipes.PHOTOACTIVE_BASIC_CIRCUIT_PLATE.getStackNormal(3), new Object[]{AdvancedCraftingRecipes.BASIC_CIRCUIT_PLATE.get(), AdvancedCraftingRecipes.BASIC_CIRCUIT_PLATE.get(), AdvancedCraftingRecipes.BASIC_CIRCUIT_PLATE.get(), CoreInit.photoactiveMaterialCan});
		addShapelessRecipe(AdvancedCraftingRecipes.PHOTOACTIVE_ADVANCED_CIRCUIT_PLATE.getStackNormal(1), new Object[]{AdvancedCraftingRecipes.ADVANCED_CIRCUIT_PLATE.get(), CoreInit.photoactiveMaterialCan});
		addRecipe(new ItemStack(CoreInit.hardenedGlassPane, 16), new Object[]{"GGG", "GGG", 'G', "glassHardened"});
		addShapelessRecipe(new ItemStack(CoreInit.photoactiveMaterialCan), new Object[]{CraftingMaterial.PHOTOACTIVE_CAN.getStack()});
		((StorageNetworkCable) StorageInit.cable).loadRecipes();
		addShapelessRecipe(CraftingMaterial.RAW_ELECTRICAL_STEEL_DUST.getStackNormal(8), new Object[]{TMResource.COAL.getStackName(Type.DUST), TMResource.COAL.getStackName(Type.DUST), TMResource.COAL.getStackName(Type.DUST), TMResource.COAL.getStackName(Type.DUST), TMResource.IRON.getStackName(Type.DUST), TMResource.IRON.getStackName(Type.DUST), TMResource.NICKEL.getStackName(Type.DUST), TMResource.BLUE_METAL.getStackName(Type.DUST), CraftingMaterial.SILICON.getStack()});
		addShapelessRecipe(TMResource.BRASS.getStackNormal(Type.DUST, 4), new Object[]{TMResource.COPPER.getStackName(Type.DUST), TMResource.COPPER.getStackName(Type.DUST), TMResource.COPPER.getStackName(Type.DUST), TMResource.ZINC.getStackName(Type.DUST)});
		addRecipe(new ItemStack(CoreInit.flintBlock), new Object[]{"FFF", "FFF", "FFF", 'F', "itemFlint"});
		addShapelessRecipe(new ItemStack(Items.FLINT, 9), new Object[]{"blockFlint"});
		addRecipe(new ItemStack(FactoryInit.components, machineFrameAmount, ComponentVariants.MACHINE_BASE.ordinal()), new Object[]{"SSS", "SSS", 'S', TMResource.STEEL.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(FactoryInit.components, machineFrameAmount, ComponentVariants.STEEL_SCAFFOLDING.ordinal()), new Object[]{"SRS", "R R", "SRS", 'S', TMResource.STEEL.getStackName(Type.PLATE), 'R', "rodSteel"});
		addRecipe(CraftingMaterial.STEEL_ROD.getStackNormal(4), new Object[]{" I", "HI", " I", 'I', "plateSteel", 'H', "itemHammer_lvl1"});
		addRecipe(new ItemStack(FactoryInit.components, machineFrameAmount, ComponentVariants.IRON_SHEETS.ordinal()), new Object[]{"SS", "SS", 'S', TMResource.IRON.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(FactoryInit.components, machineFrameAmount, ComponentVariants.STEEL_SHEETS.ordinal()), new Object[]{"SS", "SS", 'S', TMResource.STEEL.getStackName(Type.PLATE)});
		addShapelessWrenchRecipe(new ItemStack(TransportInit.conveyorBeltSlope, 1, 1), new ItemStack(TransportInit.conveyorBeltSlope));
		addShapelessWrenchRecipe(new ItemStack(TransportInit.conveyorBeltSlope), new ItemStack(TransportInit.conveyorBeltSlope, 1, 1));
		addRecipe(new ItemStack(TransportInit.steamDuct, /*Config.enableHardRecipes ? 6 : */12), new Object[]{"B B", "PHP", "B B", 'B', TMResource.BRONZE.getStackName(Type.PLATE), 'P', TMResource.BRASS.getStackName(Type.PLATE), 'H', "itemHammer_lvl2"});
		addShapelessWrenchRecipe(new ItemStack(CoreInit.buildGuide), Items.BOOK);
		addRecipe(CraftingMaterial.URANIUM235.getStackNormal(), new Object[]{"NNN", "NNN", "NNN", 'N', CraftingMaterial.URANIUM235_NUGGET.getStack()});
		addShapelessRecipe(CraftingMaterial.URANIUM235_NUGGET.getStackNormal(9), new Object[]{CraftingMaterial.URANIUM235.getStack()});
		addRecipe(new ItemStack(FactoryInit.components, machineFrameAmount, ComponentVariants.ALU_SHEETS.ordinal()), new Object[]{"SS", "SS", 'S', TMResource.ALUMINUM.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(CoreInit.enderMemory, 1), new Object[]{"BRB", "RCR", "BRB", 'B', TMResource.BLUE_METAL.getOreDictName(Type.INGOT), 'R', Items.REDSTONE, 'C', CraftingMaterial.CHARGED_ENDER.getStack()});
		addRecipe(new ItemStack(CoreInit.WirelessPeripheral, 1), new Object[]{"IEI", "ERE", "IEI", 'E', CraftingMaterial.CHARGED_ENDER.getStack(), 'R', CraftingMaterial.CHARGED_REDSTONE.getStack(), 'I', "ingotIron"});
		addRecipe(new ItemStack(CoreInit.holotape, 1), new Object[]{"IPI", "IBI", "RRR", 'I', "ingotIron", 'B', TMResource.BLUE_METAL.getOreDictName(Type.INGOT), 'R', CraftingMaterial.CHARGED_REDSTONE.getStack(), 'P', Items.PAPER});
		addRecipe(new ItemStack(CoreInit.holotapeWriter, 1), new Object[]{"III", "BRB", "IBI", 'I', "ingotIron", 'B', TMResource.BLUE_METAL.getOreDictName(Type.INGOT), 'R', CraftingMaterial.CHARGED_REDSTONE.getStack()});
		addRecipe(new ItemStack(CoreInit.holotapeReader, 1), new Object[]{"III", "IRI", "IBI", 'I', "ingotIron", 'B', TMResource.BLUE_METAL.getOreDictName(Type.INGOT), 'R', CraftingMaterial.CHARGED_REDSTONE.getStack()});
		addRecipe(new ItemStack(CoreInit.magCard, 1), new Object[]{"IR", "PP", 'I', "ingotIron", 'P', Items.PAPER, 'R', CraftingMaterial.CHARGED_REDSTONE.getStack()});
		addRecipe(new ItemStack(CoreInit.MagCardDevice, 1), new Object[]{"III", "RIR", "IBI", 'I', "ingotIron", 'R', CraftingMaterial.CHARGED_REDSTONE.getStack(), 'B', TMResource.BLUE_METAL.getOreDictName(Type.INGOT)});
		addRecipe(new ItemStack(CoreInit.MagCardReader, 1), new Object[]{"RI", "RI", "BI", 'I', "ingotIron", 'R', CraftingMaterial.CHARGED_REDSTONE.getStack(), 'B', TMResource.BLUE_METAL.getStackName(Type.PLATE)});
		{
			Condition cond = Config.requireConfig("adventureItems");
			addRecipe(new ItemStack(CoreInit.TabletHouse, 1), new Object[]{cond, "IDI", "IGI", "IBI", 'I', "ingotIron", 'D', new ItemStack(Items.DYE, 1, 0), 'G', "paneGlassColorless", 'B', Blocks.STONE_BUTTON});
		}
		addRecipe(new ItemStack(CoreInit.entityTracker, 1), new Object[]{"BGB", "RPR", "BSB", 'G', TMResource.GREENIUM.getOreDictName(Type.INGOT), 'B', TMResource.BLUE_METAL.getStackName(Type.PLATE), 'R', CraftingMaterial.CHARGED_REDSTONE.getStack(), 'S', "blockPlatinum", 'P', "paneGlassColorless"});
		addRecipe(CraftingMaterial.COPPER_MESH.getStackNormal(), new Object[]{"WW", "WW", 'W', TMResource.COPPER.getStackName(Type.CABLE)});
		TMResource.addPlateRecipe(CraftingMaterial.TIN_CASING.getStackNormal(), 1, TMResource.TIN.getStackName(Type.PLATE));
	}

	//public static List<IRecipe> customRecipes = new ArrayList<>();

	private static void addShapelessWrenchRecipe(ItemStack stack, Object... recipe) {
		/*IRecipe r = new WrenchShapelessCraftingRecipe(stack, recipe);
		customRecipes.add(r);
		RecipeHelper.register(r);*/
		Object[] r = new Object[recipe.length+1];
		System.arraycopy(recipe, 0, r, 0, recipe.length);
		r[recipe.length] = "tomsmodwrench";
		addShapelessRecipe(stack, r);
	}

	public static void patchRecipes() {
		patchShapedRecipe("iron bars", new ItemStack(Blocks.IRON_BARS, 16), () -> new Object[][]{{"C H", "PPP", "PPP", 'C', "itemCutter_lvl2", 'H', "itemHammer_lvl2", 'P', TMResource.IRON.getStackName(Type.PLATE)}, {"H C", "PPP", "PPP", 'C', "itemCutter_lvl2", 'H', "itemHammer_lvl2", 'P', TMResource.IRON.getStackName(Type.PLATE)}});
		patchShapedRecipe("light weighted pressure plate", Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, () -> new Object[][]{{"GGH", 'G', TMResource.GOLD.getStackName(Type.PLATE), 'H', "itemHammer_lvl2"}});
		patchShapedRecipe("heavy weighted pressure plate", Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, () -> new Object[][]{{"GGH", 'G', TMResource.IRON.getStackName(Type.PLATE), 'H', "itemHammer_lvl2"}});
		patchShapedRecipe("iron door", new ItemStack(Items.IRON_DOOR, 2), () -> new Object[][]{{"PP ", "PPH", "PP ", 'P', TMResource.IRON.getStackName(Type.PLATE), 'H', "itemHammer_lvl2"}});
		patchShapedRecipe("iron trapdoor", Blocks.IRON_TRAPDOOR, () -> new Object[][]{{"H ", "PP", "PP", 'P', TMResource.IRON.getStackName(Type.PLATE), 'H', "itemHammer_lvl2"}});
	}
}
