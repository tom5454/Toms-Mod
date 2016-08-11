package com.tom.recipes;

import static com.tom.api.recipes.RecipeHelper.addRecipe;
import static com.tom.api.recipes.RecipeHelper.addShapelessRecipe;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.defense.DefenseInit;
import com.tom.energy.EnergyInit;
import com.tom.factory.FactoryInit;
import com.tom.storage.StorageInit;
import com.tom.transport.TransportInit;
import com.tom.weaponsAndTools.ToolsInit;

public class CraftingRecipes {//OreDictionary AdvancedCraftingRecipes
	public static void init(){
		int machineFrameAmount = Config.enableHardMode ? 2 : 4;
		addRecipe(CraftingMaterial.BIG_REDSTONE.getStackNormal(), new Object[]{"RR",'R',Items.REDSTONE});
		addRecipe(CraftingMaterial.BIG_GLOWSTONE.getStackNormal(), new Object[]{"GG",'G',Items.GLOWSTONE_DUST});
		addRecipe(CraftingMaterial.BIG_ENDER_PEARL.getStackNormal(), new Object[]{"EE",'E',Items.ENDER_PEARL});
		addRecipe(CraftingMaterial.BIG_REDSTONE.getStackNormal(), new Object[]{"R","R",'R',Items.REDSTONE});
		addRecipe(CraftingMaterial.BIG_GLOWSTONE.getStackNormal(), new Object[]{"G","G",'G',Items.GLOWSTONE_DUST});
		addRecipe(CraftingMaterial.BIG_ENDER_PEARL.getStackNormal(), new Object[]{"E","E",'E',Items.ENDER_PEARL});
		//addRecipe(TMResource.CHROME.getStackNormal(Type.PLATE), new Object[]{"CC","CC",'C',TMResource.CHROME.getStackName(Type.INGOT)});
		addRecipe(new ItemStack(CoreInit.MachineFrameChrome, machineFrameAmount), new Object[]{"CCC","CHC","CCC",'C',TMResource.CHROME.getStackName(Type.PLATE), 'H', "itemHammer_lvl3"});
		addRecipe(new ItemStack(CoreInit.MachineFrameBronze, machineFrameAmount), new Object[]{"CCC","CHC","CCC",'C',TMResource.BRONZE.getStackName(Type.PLATE), 'H', "itemHammer_lvl2"});
		addRecipe(new ItemStack(CoreInit.MachineFrameSteel, machineFrameAmount), new Object[]{"CCC","CHC","CCC",'C',TMResource.STEEL.getStackName(Type.PLATE), 'H', "itemHammer_lvl2"});
		addRecipe(new ItemStack(CoreInit.MachineFrameBasic, machineFrameAmount), new Object[]{"CCC","CHC","CCC",'C',TMResource.IRON.getStackName(Type.PLATE), 'H', "itemHammer_lvl2"});
		addRecipe(new ItemStack(CoreInit.MachineFrameTitanium, machineFrameAmount), new Object[]{"CCC","CHC","CCC",'C',TMResource.TITANIUM.getStackName(Type.PLATE), 'H', "itemHammer_lvl4"});
		//addRecipe(new ItemStack(CoreInit.Battery, 1), new Object[]{" C ","IBI","IRI",'I',"ingotIron",'C',TMResource.COPPER.getStackName(Type.CABLE),'R',Items.REDSTONE,'B',TMResource.BLUE_METAL.getStackName(Type.INGOT)});
		addRecipe(new ItemStack(CoreInit.memoryCard, 1), new Object[]{"CCC","IBI","RRR",'I',"ingotIron",'C',TMResource.COPPER.getStackName(Type.CABLE),'R',Items.REDSTONE,'B',TMResource.BLUE_METAL.getStackName(Type.INGOT)});
		addRecipe(new ItemStack(CoreInit.linker, 1), new Object[]{"IRI","IMI",'I',"ingotIron",'M',CoreInit.memoryCard,'R',Items.REDSTONE});
		//addRecipe(TMResource.COPPER.getStackNormal(Type.CABLE,3), new Object[]{"CCC",'C',TMResource.COPPER.getStackName(Type.INGOT)});
		addRecipe(new ItemStack(EnergyInit.FusionFluidInjector, 1), new Object[]{"CBC","CPC","CBC",'C',TMResource.CHROME.getStackName(Type.PLATE),'B',TMResource.BLUE_METAL.getStackName(Type.INGOT),'P',CoreInit.itemPump});
		addRecipe(new ItemStack(EnergyInit.FusionFluidExtractor, 1), new Object[]{"CCC","BPB","CCC",'C',TMResource.CHROME.getStackName(Type.PLATE),'B',TMResource.BLUE_METAL.getStackName(Type.INGOT),'P',CoreInit.itemPump});
		//addRecipe(TMResource.IRON.getStackNormal(Type.PLATE,2), new Object[]{"TT","TT",'T',"ingotIron"});
		//addRecipe(TMResource.TIN.getStackNormal(Type.PLATE,2), new Object[]{"TT","TT",'T',TMResource.TIN.getStackName(Type.INGOT)});
		addRecipe(new ItemStack(EnergyInit.FusionCore, 2), new Object[]{"CBC","BTB","CBC",'C',TMResource.CHROME.getStackName(Type.PLATE),'B',TMResource.BLUE_METAL.getStackName(Type.INGOT),'T',TMResource.TITANIUM.getStackName(Type.PLATE)});
		//GameRegistry.addRecipnew ShapedOreRecipe(e(new ItemStack(CoreInit.EnergySensor, 1), new Object[]{"IRI","IBI","IRI",'I',"ingotIron",'B',TMResource.BLUE_METAL.getStack(Type.INGOT),'R',Items.REDSTONE});
		addRecipe(new ItemStack(FactoryInit.MultiblockCase, 2), new Object[]{"IBI","BCB","IBI",'I',"ingotIron",'B',TMResource.BLUE_METAL.getStackName(Type.INGOT),'C',TMResource.CHROME.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(FactoryInit.AdvancedMultiblockCasing, 2), new Object[]{"TMT","MCM","TMT",'T',TMResource.TITANIUM.getStackName(Type.PLATE),'M',FactoryInit.MultiblockCase,'C',TMResource.CHROME.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(FactoryInit.Electrolyzer, 1), new Object[]{"MBM","CPC","MRM",'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'M',FactoryInit.MultiblockCase,'C',Blocks.COAL_BLOCK,'P',CoreInit.itemPump});
		addRecipe(new ItemStack(FactoryInit.Centrifuge, 1), new Object[]{"MBM","IPI","MRM",'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'M',FactoryInit.MultiblockCase,'I',Blocks.IRON_BLOCK,'P',CoreInit.itemPump});
		addRecipe(new ItemStack(FactoryInit.FusionPreHeater, 1), new Object[]{"MBM","TIT","MRM",'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'M',FactoryInit.AdvancedMultiblockCasing,'I',Blocks.IRON_BLOCK,'T',TMResource.TITANIUM.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(FactoryInit.CoolantTower, 1), new Object[]{"MBM","CPC","MRM",'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'M',FactoryInit.MultiblockCase,'C',TMResource.CHROME.getStackName(Type.PLATE),'P',CoreInit.itemPump});
		addRecipe(new ItemStack(FactoryInit.MultiblockEnergyPort, 1), new Object[]{"MRM","RRR","MRM",'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'M',FactoryInit.MultiblockCase});
		addRecipe(new ItemStack(FactoryInit.MultiblockHatch, 1), new Object[]{"MPM","ICI",'P',Blocks.PISTON,'M',FactoryInit.MultiblockCase,'C',Blocks.CHEST,'I',"ingotIron"});
		addRecipe(new ItemStack(FactoryInit.MultiblockFluidHatch, 1), new Object[]{"MPM","IBI",'P',Blocks.PISTON,'M',FactoryInit.MultiblockCase,'B',Items.BUCKET,'I',"ingotIron"});
		addRecipe(new ItemStack(FactoryInit.MultiblockFuelRod, 1), new Object[]{"MPM","CIC","MPM",'M',FactoryInit.AdvancedMultiblockCasing,'C',Blocks.COAL_BLOCK,'I',"ingotIron",'P',TMResource.CHROME.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(CoreInit.Display, 1), new Object[]{"IGI","IPI","IRI",'G',CraftingMaterial.CHARGED_GLOWSTONE.getStack(),'P',"paneGlassColorless",'I',"ingotIron",'R',CraftingMaterial.CHARGED_REDSTONE.getStack()});
		addRecipe(new ItemStack(CoreInit.itemPump, 1), new Object[]{" II","IRI","II ",'I',"ingotIron",'R',CraftingMaterial.CHARGED_REDSTONE.getStack()});
		addRecipe(new ItemStack(EnergyInit.FusionCharger, 1), new Object[]{"CRC","CBC","CRC",'C',TMResource.CHROME.getStackName(Type.PLATE),'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'B',TMResource.BLUE_METAL.getStackName(Type.INGOT)});
		addRecipe(new ItemStack(EnergyInit.FusionInjector, 1), new Object[]{"CRC","CRC","CRC",'C',TMResource.CHROME.getStackName(Type.PLATE),'R',CraftingMaterial.CHARGED_REDSTONE.getStack()});
		addRecipe(new ItemStack(EnergyInit.FusionController, 1), new Object[]{"CGC","CRC","CCC",'G',TMResource.advAlloyMK2.getStackName(Type.PLATE),'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'C',TMResource.CHROME.getStackName(Type.PLATE)});
		addRecipe(new ItemStack(CoreInit.uraniumRodEmpty, 2), new Object[]{"III","I I"," I ",'I',"ingotIron"});
		addShapelessRecipe(new ItemStack(CoreInit.uraniumRod), new Object[]{CoreInit.uraniumRodEmpty,"ingotUranium"});
		addRecipe(new ItemStack(CoreInit.ItemProxy, 1), new Object[]{"TGT","BRB","TET",'T',TMResource.TITANIUM.getStackName(Type.PLATE),'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'G',TMResource.GREENIUM.getStackName(Type.INGOT),'B',TMResource.BLUE_METAL.getStackName(Type.INGOT),'E',CraftingMaterial.CHARGED_ENDER.getStack()});
		/*GameRegistry.addRecipe(new ItemStack(mcreator_pItem.block, 2), new Object[]{"BBB","BGB","BBB",'B',TMResource.BLUE_METAL.getStack(Type.INGOT),'G',mcreator_bloodGlass.block});
		GameRegistry.addRecipe(new ItemStack(mcreator_w.block, 4), new Object[]{"IGI","GPG","IGI",'P',mcreator_pItem.block,'G',Items.gold_ingot,'I',"ingotIron"});
		GameRegistry.addRecipe(new ItemStack(mcreator_v.block, 4), new Object[]{"IBI","BPB","IBI",'P',mcreator_pItem.block,'B',Blocks.iron_bars,'I',"ingotIron"});
		if(!Loader.isModLoaded(Configs.AE2)){
			GameRegistry.addRecipe(new ItemStack(mcreator_p.block, 1), new Object[]{"CBC","GPG","CBC",'P',mcreator_pItem.block,'B',TMResource.BLUE_METAL.getStack(Type.INGOT),'C',Blocks.coal_block,'G',mcreator_bloodGlass.block});
			GameRegistry.addRecipe(new ItemStack(mcreator_pylon.block, 1), new Object[]{"CBC","GPG","CBC",'P',mcreator_p.block,'B',TMResource.BLUE_METAL.getStack(Type.INGOT),'C',Blocks.coal_block,'G',mcreator_bloodGlass.block});
			GameRegistry.addRecipe(new ItemStack(mcreator_pl.block, 1), new Object[]{"IBI","IPI","INI",'P',mcreator_pItem.block,'I',"ingotIron",'B',Blocks.hopper,'N',Blocks.REDSTONE_block});
			GameRegistry.addRecipe(new ItemStack(mcreator_pc.block, 4), new Object[]{"SPS","PGP","SPS",'P',mcreator_pItem.block,'G',mcreator_greeniumIngot.block,'S',Blocks.coal_block});
			GameRegistry.addRecipe(new ItemStack(mcreator_cu.block, 4), new Object[]{"SBS","BPB","IMI",'P',mcreator_pItem.block,'B',TMResource.BLUE_METAL.getStack(Type.INGOT),'S',Blocks.coal_block,'I',"ingotIron",'M',Blocks.crafting_table});
			GameRegistry.addRecipe(new ItemStack(mcreator_cr.block, 2), new Object[]{"INI","NPN","INI",'P',mcreator_pItem.block,'I',"ingotIron",'N',Blocks.hopper});
			GameRegistry.addRecipe(new ItemStack(mcreator_pm.block, 1), new Object[]{"SPS","PRP","SPS",'P',mcreator_pItem.block,'R',Blocks.REDSTONE_block,'S',Blocks.coal_block});
		}else{
			Block itemSkyStone = GameRegistry.findBlock(Configs.AE2, "tile.BlockSkyStone");
			Item itemBus = GameRegistry.findItem(Configs.AE2, "item.ItemMultiPart");
			Block itemInterface = GameRegistry.findBlock(Configs.AE2, "tile.BlockInterface");
			Block itemMolecularAss = GameRegistry.findBlock(Configs.AE2, "tile.BlockMolecularAssembler");
			ItemStack itemStackSkyStone = new ItemStack(itemSkyStone,1,1);
			ItemStack itemStackBus = new ItemStack(itemBus,1,240);
			ItemStack itemStackInterface = new ItemStack(itemInterface,1,0);
			ItemStack itemStackMolecularAss = new ItemStack(itemMolecularAss,1,0);
			GameRegistry.addRecipe(new ItemStack(mcreator_p.block, 1), new Object[]{"CBC","GPG","CBC",'P',mcreator_pItem.block,'B',TMResource.BLUE_METAL.getStack(Type.INGOT),'C',itemStackSkyStone,'G',mcreator_bloodGlass.block});
			GameRegistry.addRecipe(new ItemStack(mcreator_pylon.block, 1), new Object[]{"CBC","GPG","CBC",'P',mcreator_p.block,'B',TMResource.BLUE_METAL.getStack(Type.INGOT),'C',itemStackSkyStone,'G',mcreator_bloodGlass.block});
			GameRegistry.addRecipe(new ItemStack(mcreator_pl.block, 1), new Object[]{"IBI","IPI","INI",'P',mcreator_pItem.block,'I',"ingotIron",'B',itemStackBus,'N',itemStackInterface});
			GameRegistry.addRecipe(new ItemStack(mcreator_pc.block, 4), new Object[]{"SPS","PGP","SPS",'P',mcreator_pItem.block,'G',mcreator_greeniumIngot.block,'S',itemStackSkyStone});
			GameRegistry.addRecipe(new ItemStack(mcreator_cu.block, 4), new Object[]{"SBS","BPB","IMI",'P',mcreator_pItem.block,'B',TMResource.BLUE_METAL.getStack(Type.INGOT),'S',itemStackSkyStone,'I',"ingotIron",'M',itemStackMolecularAss});
			GameRegistry.addRecipe(new ItemStack(mcreator_cr.block, 2), new Object[]{"INI","NPN","INI",'P',mcreator_pItem.block,'I',"ingotIron",'N',itemStackInterface});
			GameRegistry.addRecipe(new ItemStack(mcreator_pm.block, 1), new Object[]{"SPS","PRP","SPS",'P',mcreator_pItem.block,'R',Blocks.REDSTONE_block,'S',itemStackSkyStone});
		}
		GameRegistry.addRecipe(new ItemStack(mcreator_multiblockCont.block, 1), new Object[]{"BLB","BGB","BRB",'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'B',TMResource.BLUE_METAL.getStack(Type.INGOT),'G',mcreator_greeniumIngot.block,'L',mcreator_bloodGlass.block});
		GameRegistry.addRecipe(new ItemStack(mcreator_wrench.block, 1), new Object[]{" RT"," IE","C  ",'R',CraftingMaterial.CHARGED_REDSTONE.getStack(),'T',TMResource.TITANIUM.getStack(Type.PLATE),'I',"ingotIron",'E',CoreInit.chargedEnderpearl,'C',TMResource.CHROME.getStack(Type.PLATE)});
		GameRegistry.addRecipe(new ItemStack(mcreator_cOreCrafted.block, 1), new Object[]{"GPG","PRP","TPT",'T',TMResource.TITANIUM.getStack(Type.PLATE),'P',mcreator_pItem.block,'G',mcreator_greeniumIngot.block,'R',CraftingMaterial.CHARGED_REDSTONE.getStack()});
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(mcreator_c.block, 1), new Object[]{"GBG","RSR","GBG",'G',mcreator_greeniumIngot.block,'B',TMResource.BLUE_METAL.getStack(Type.INGOT),'R',Blocks.REDSTONE_block,'S',"blockPlatinum"}));*/
		addRecipe(new ItemStack(CoreInit.rsDoor, 1), new Object[]{"III","IDI","RRR",'I',"ingotIron",'D',Items.OAK_DOOR,'R',CraftingMaterial.CHARGED_REDSTONE.getStack()});
		/*for(int i = 1;i<Configs.maxProcessorTier;i++){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("tier",i+1);
			NBTTagCompound tag2 = new NBTTagCompound();
			tag2.setInteger("tier",i);
			GameRegistry.addRecipe(new ItemStack(CoreInit.trProcessor, 1, tag), new Object[]{"RRR","RPR","RRR",'R',Items.REDSTONE,'P', new ItemStack(CoreInit.trProcessor, 1, tag2)});
		}*/
		addShapelessRecipe(new ItemStack(CoreInit.bigNoteBook), new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.PAPER), new ItemStack(Items.PAPER));
		addRecipe(new ItemStack(ToolsInit.flintAxe, 1), new Object[]{"FS"," S",'F',CoreInit.flintAxeHead,'S',Items.STICK});
		addRecipe(new ItemStack(CoreInit.flintAxeHead, 1), new Object[]{"FF"," F",'F',Items.FLINT});
		addShapelessRecipe(new ItemStack(ToolsInit.portableComparator), new ItemStack(Items.COMPARATOR), new ItemStack(Items.REDSTONE), new ItemStack(Items.COMPASS));
		addShapelessRecipe(new ItemStack(DefenseInit.projectorFieldType,1,3), new ItemStack(DefenseInit.projectorFieldType,1,3));
		addRecipe(new ItemStack(CoreInit.researchTable, 2), new Object[]{"BSI","WCW","RPR",'B', Items.GLASS_BOTTLE, 'S', "slabWood", 'I', "dyeBlack", 'W', "plankWood", 'C', Blocks.CRAFTING_TABLE, 'R', Items.STICK, 'P', Items.PAPER});
		addRecipe(CraftingMaterial.IRON_ROD.getStackNormal(), new Object[]{"I","I","I", 'I', "ingotIron"});
		addRecipe(CraftingMaterial.IRON_ROD.getStackNormal(4), new Object[]{" I","HI"," I", 'I', "plateIron", 'H', "itemHammer_lvl1"});
		addRecipe(new ItemStack(CoreInit.magnifyingGlass, 1), new Object[]{" G","I ", 'I', "rodIron", 'G', "paneGlassColorless"});
		addShapelessRecipe(new ItemStack(CoreInit.noteBook, 1), new Object[]{Items.WRITABLE_BOOK, Items.PAPER, "dyeBlack", Items.FEATHER, Items.PAPER, Items.STICK});
		addRecipe(CraftingMaterial.HOT_COPPER_HAMMER_HEAD.getStackNormal(), new Object[]{" H ","III","III",'I', CraftingMaterial.HOT_COPPER.getStackNormal(), 'H', "itemHammer_lvl0"});
		addRecipe(CraftingMaterial.HOT_TIN_HAMMER_HEAD.getStackNormal(), new Object[]{" H ","III","III",'I', CraftingMaterial.HOT_TIN.getStackNormal(), 'H', "itemHammer_lvl0"});
		addRecipe(CraftingMaterial.HOT_IRON_HAMMER_HEAD.getStackNormal(), new Object[]{" H ","III","III",'I', CraftingMaterial.HOT_IRON.getStackNormal(), 'H', "itemHammer_lvl0"});
		addShapelessRecipe(TMResource.BRONZE.getStackNormal(Type.DUST, 4), new Object[]{TMResource.COPPER.getStackName(Type.DUST), TMResource.COPPER.getStackName(Type.DUST), TMResource.COPPER.getStackName(Type.DUST), TMResource.TIN.getStackName(Type.DUST)});
		addRecipe(CraftingMaterial.STONE_BOWL.getStackNormal(3), new Object[]{"S S"," S ",'S', Blocks.STONE});
		//addRecipe(new ItemStack(TransportInit.itemDuct, 6), new Object[]{"TGT",'T', "ingotTin", 'G', "blockGlass"});
		addRecipe(new ItemStack(TransportInit.fluidDuct, 6), new Object[]{"IGI",'I', "plateIron", 'G', "blockGlassColorless"});
		//addRecipe(new ItemStack(TransportInit.servo, 2), new Object[]{"IGI", "RPR",'I', "ingotIron", 'G', "blockGlass", 'R', Items.REDSTONE, 'P', Items.paper});
		//addRecipe(new ItemStack(TransportInit.filter, 2), new Object[]{"IPI", "NGN",'I', "ingotIron", 'G', "blockGlass", 'N', "nuggetIron", 'P', Items.paper});
		addRecipe(new ItemStack(CoreInit.wrench), new Object[]{" RE", " BG", "S  ",'B', "ingotBronze", 'G', "nuggetGold", 'R', Blocks.REDSTONE_BLOCK, 'E', Items.ENDER_PEARL, 'S', Items.STICK});
		addShapelessRecipe(TMResource.ELECTRUM.getStackNormal(Type.DUST, 2), new Object[]{TMResource.GOLD.getStackName(Type.DUST), TMResource.SILVER.getStackName(Type.DUST)});
		addRecipe(new ItemStack(StorageInit.tankBasic), new Object[]{"OGO", "GGG", "OGO",'O', TMResource.COPPER.getStackName(Type.PLATE), 'G', "paneGlassColorless"});
		addRecipe(new ItemStack(CoreInit.treeTap, 2), new Object[]{" _S", "WWW", "  W", 'W', "plankWood", '_', "slabWood", 'S', Items.STICK});
		addRecipe(TMResource.COAL.getHammerStack(1), new Object[]{" H", "FS", " S", 'S', Items.STICK, 'F', Items.FLINT, 'H', CraftingMaterial.FLINT_HAMMER_HEAD.getStackNormal()});
		addRecipe(CraftingMaterial.BRONZE_PIPE.getStackNormal(6), new Object[]{"P P","PHP","P P",'P',TMResource.BRONZE.getStackName(Type.PLATE), 'H', "itemHammer_lvl2"});
		addRecipe(new ItemStack(StorageInit.basicTerminal), new Object[]{"IGI","TCG","IGI",'I',TMResource.IRON.getStackName(Type.PLATE), 'T', StorageInit.partTerminal, 'G', "blockGlassColorless", 'C', "storageNetworkCable"});
		addRecipe(new ItemStack(StorageInit.craftingTerminal), new Object[]{"IGI","TCG","IGI",'I',TMResource.IRON.getStackName(Type.PLATE), 'T', StorageInit.partCraftingTerminal, 'G', "blockGlassColorless", 'C', "storageNetworkCable"});
		addRecipe(new ItemStack(StorageInit.patternTerminal), new Object[]{"IGI","TCG","IGI",'I',TMResource.IRON.getStackName(Type.PLATE), 'T', StorageInit.partPatternTerminal, 'G', "blockGlassColorless", 'C', "storageNetworkCable"});
		addRecipe(new ItemStack(StorageInit.basicTerminal), new Object[]{"IGI","TCG","IGI",'I',TMResource.ALUMINUM.getStackName(Type.PLATE), 'T', StorageInit.partTerminal, 'G', "blockGlassColorless", 'C', "storageNetworkCable"});
		addRecipe(new ItemStack(StorageInit.craftingTerminal), new Object[]{"IGI","TCG","IGI",'I',TMResource.ALUMINUM.getStackName(Type.PLATE), 'T', StorageInit.partCraftingTerminal, 'G', "blockGlassColorless", 'C', "storageNetworkCable"});
		addRecipe(new ItemStack(StorageInit.patternTerminal), new Object[]{"IGI","TCG","IGI",'I',TMResource.ALUMINUM.getStackName(Type.PLATE), 'T', StorageInit.partPatternTerminal, 'G', "blockGlassColorless", 'C', "storageNetworkCable"});
		addRecipe(new ItemStack(FactoryInit.blockCoalCoke), new Object[]{"CCC", "CCC", "CCC", 'C', FactoryInit.coalCoke});
		addShapelessRecipe(new ItemStack(FactoryInit.coalCoke, 9), new Object[]{FactoryInit.blockCoalCoke});
		addRecipe(CraftingMaterial.GLASS_FIBER.getStackNormal(2), new Object[]{"GG", "GG", 'G', CraftingMaterial.GLASS_DUST.getStack()});
		addRecipe(CraftingMaterial.GLASS_MESH.getStackNormal(), new Object[]{"GG", "GG", 'G', CraftingMaterial.GLASS_FIBER.getStack()});
		addShapelessRecipe(CraftingMaterial.GLASS_DUST.getStackNormal(), new Object[]{"blockGlass", "itemMortar"});
		addRecipe(CraftingMaterial.UPGRADE_FRAME.getStackNormal(2), new Object[]{" B ", "RIR", " G ", 'B', TMResource.BLUE_METAL.getStackName(Type.NUGGET), 'I', Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 'G', TMResource.GLOWSTONE.getStackName(Type.DUST_TINY), 'R', Items.REDSTONE});
		addShapelessRecipe(CraftingMaterial.ENDERIUM_BASE.getStackNormal(4), new Object[]{"dustTin", "dustTin", "dustSilver", "dustPlatinum"});
		addRecipe(new ItemStack(Blocks.STICKY_PISTON), new Object[]{"R", "P", 'R', CraftingMaterial.BOTTLE_OF_RUBBER.getStack(), 'P', Blocks.PISTON});
		addRecipe(CraftingMaterial.TIN_TURBINE.getStackNormal(), new Object[]{"PHP", "NIN", "PNP", 'P', TMResource.TIN.getStackNormal(Type.PLATE), 'H', "itemHammer_lvl2", 'I', TMResource.TIN.getStackName(Type.INGOT), 'N', TMResource.TIN.getStackName(Type.NUGGET)});
		addRecipe(new ItemStack(TransportInit.fluidServo, 2), new Object[]{"PGP", "NRN", 'P', TMResource.IRON.getStackNormal(Type.PLATE), 'G', "blockGlassColorless", 'R', Items.REDSTONE, 'N', TMResource.IRON.getStackName(Type.NUGGET)});
	}
}
