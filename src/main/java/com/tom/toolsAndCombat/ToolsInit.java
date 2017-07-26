package com.tom.toolsAndCombat;

import static com.tom.api.recipes.RecipeHelper.addRecipe;
import static com.tom.api.recipes.RecipeHelper.addShapelessRecipe;
import static com.tom.core.CoreInit.registerItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.tom.api.block.IMethod;
import com.tom.api.recipes.RecipeHelper;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.IMod;
import com.tom.core.TMResource;
import com.tom.lib.Configs;
import com.tom.toolsAndCombat.item.PortableComparator;

@Mod(modid = ToolsInit.modid, name = ToolsInit.modName, version = Configs.version, dependencies = Configs.coreDependencies)
public class ToolsInit {
	public static final String modid = Configs.ModidL + "|toolsandcombat";
	public static final String modName = Configs.ModName + " Tools And Combat";
	public static final Logger log = LogManager.getLogger(modName);
	public static CreativeTabs tabTomsModToolsAndCombat = CoreInit.tabTomsModToolsAndCombat;
	public static Item.ToolMaterial flintToolMaterial, copperToolMaterial, bronzeToolMaterial, steelToolMaterial;
	public static Item portableComparator, diamondHead;
	public static ToolGroup flint, copper, bronze, steel;
	private static int diamondHeadCounter;
	public static String[][] toolShape = new String[][]{{"MMM", "HS ", " S "}, {"MMH", "MS ", " S "}, {"HM ", " S ", " S "}, {"HMM", " S ", " S "}, {" M ", "HM ", " S "}};

	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent) {
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		if (!Config.toolsNeedHammer) {
			toolShape = new String[][]{{"MMM", " S ", " S "}, {"MM ", "MS ", " S "}, {" M ", " S ", " S "}, {" MM", " S ", " S "}, {" M ", " M ", " S "}};
		}
		if (Config.enableHardRecipes) {
			log.warn("Hard mode enabled");
			flintToolMaterial = EnumHelper.addToolMaterial("flintToolMaterial", 0, 150, 2F, 1F, 1);
			copperToolMaterial = EnumHelper.addToolMaterial("copperToolMaterial", 1, 300, 2.5F, 1.5F, 4);
			bronzeToolMaterial = EnumHelper.addToolMaterial("bronzeToolMaterial", 2, 512, 3.2F, 2.5F, 8);
			steelToolMaterial = EnumHelper.addToolMaterial("steelToolMaterial", 3, 800, 3.8F, 3F, 10);
		} else {
			flintToolMaterial = EnumHelper.addToolMaterial("flintToolMaterial", 0, 200, 3F, 1.5F, 2);
			copperToolMaterial = EnumHelper.addToolMaterial("copperToolMaterial", 1, 400, 3.5F, 1.8F, 6);
			bronzeToolMaterial = EnumHelper.addToolMaterial("bronzeToolMaterial", 2, 580, 4.2F, 2.5F, 12);
			steelToolMaterial = EnumHelper.addToolMaterial("steelToolMaterial", 3, 900, 4.8F, 3F, 15);
		}
		flint = new ToolGroup(flintToolMaterial, -3.2F, ToolGroup.defaultHead, "", 0).setUnlocalizedName("flint").setCreativeTab(tabTomsModToolsAndCombat);
		copper = new ToolGroup(copperToolMaterial, -2.6F, null, "plateCopper", TMResource.COPPER.getMaterialLevel()).setUnlocalizedName("copper").setCreativeTab(tabTomsModToolsAndCombat);
		bronze = new ToolGroup(bronzeToolMaterial, -2.2F, null, "plateBronze", TMResource.BRONZE.getMaterialLevel()).setUnlocalizedName("bronze").setCreativeTab(tabTomsModToolsAndCombat);
		steel = new ToolGroup(steelToolMaterial, -2.0F, null, "plateSteel", TMResource.STEEL.getMaterialLevel()).setUnlocalizedName("steel").setCreativeTab(tabTomsModToolsAndCombat);
		portableComparator = new PortableComparator().setCreativeTab(tabTomsModToolsAndCombat).setUnlocalizedName("tm.portableComparator").setMaxStackSize(1);
		flint.register();
		copper.register();
		bronze.register();
		steel.register();
		registerItem(portableComparator, portableComparator.getUnlocalizedName().substring(5));
		if (Config.changeDiamondToolsRecipe) {
			diamondHead = new ToolGroup.Head(ToolGroup.defaultHead).setCreativeTab(tabTomsModToolsAndCombat).setUnlocalizedName("tm.diamondHead").setMaxStackSize(1);
			CoreInit.addItemToGameRegistry(diamondHead, diamondHead.getUnlocalizedName().substring(5));
			CoreInit.proxy.runMethod((IMethod) diamondHead);
		}
		hadPreInit = true;
		CoreInit.tryLoadAfterPreInit(log);
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in " + time + " milliseconds");
	}

	@EventHandler
	public static void load(FMLInitializationEvent event) {
		log.info("Start Initialization");
		long tM = System.currentTimeMillis();
		copper.registerHammerRecipe();
		bronze.registerHammerRecipe();
		steel.registerHammerRecipe();
		if (Config.changeDiamondToolsRecipe) {
			log.info("Tweaking vanilla tool crafting recipes");
			Item[] itemsD = new Item[]{Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE, Items.DIAMOND_SWORD};
			Item[] itemsG = new Item[]{Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_HOE, Items.GOLDEN_SWORD};
			Item[] itemsI = new Item[]{Items.IRON_PICKAXE, Items.IRON_AXE, Items.IRON_SHOVEL, Items.IRON_HOE, Items.IRON_SWORD};
			List<Item> recipesToRemove = new ArrayList<>();
			recipesToRemove.addAll(Arrays.asList(itemsD));
			String[] diamondTRecipe;
			if (Config.diamondToolsNeedCraftingTable) {
				diamondTRecipe = new String[]{" F ", " S ", " S "};
			} else {
				diamondTRecipe = new String[]{"FS", "S "};
			}
			if (Config.diamondToolHeadsCraftable) {
				Stream.of(itemsD).forEach(i -> addRecipe(new ItemStack(diamondHead, 1, diamondHeadCounter), new Object[]{toolShape[diamondHeadCounter++], 'M', "gemDiamond"}));
				diamondHeadCounter = 0;
			}
			if (Config.hardToolRecipes) {
				recipesToRemove.addAll(Arrays.asList(itemsG));
				recipesToRemove.addAll(Arrays.asList(itemsI));
			}
			recipesToRemove.stream().map(ItemStack::new).forEach(RecipeHelper::removeAllRecipes);
			Stream.of(itemsD).forEach(i -> addRecipe(new ItemStack(i), new Object[]{diamondTRecipe, 'F', new ItemStack(diamondHead, 1, diamondHeadCounter++), 'S', Items.STICK}));
			diamondHeadCounter = 0;
			if (Config.hardToolRecipes) {
				addToolRecipes(itemsG, "plateGold", TMResource.GOLD.getMaterialLevel());
				addToolRecipes(itemsI, "plateIron", TMResource.IRON.getMaterialLevel());
			}
			log.info("Vanilla tool crafting recipes tweaked");
		}
		addRecipe(flint.axeHead(), new Object[]{"FF", " F", 'F', "itemFlint"});
		addRecipe(flint.pickHead(), new Object[]{"FF", "F ", 'F', "itemFlint"});
		addRecipe(flint.swordHead(), new Object[]{"F", "F", 'F', "itemFlint"});
		addRecipe(flint.hoeHead(), new Object[]{"FF", 'F', "itemFlint"});
		addShapelessRecipe(flint.shovelHead(), new Object[]{"itemFlint"});
		addShapelessRecipe(flint.pickHead(), new Object[]{flint.axeHead()});
		addShapelessRecipe(flint.axeHead(), new Object[]{flint.pickHead()});
		flint.registerSimpleRecipe();
		long time = System.currentTimeMillis() - tM;
		log.info("Initialization took in " + time + " milliseconds");
	}

	public static void addToolRecipes(Item[] items, String name, int hammerLvl) {
		Stream.of(items).forEach(i -> addRecipe(new ItemStack(i), new Object[]{toolShape[diamondHeadCounter++], 'M', name, 'S', Items.STICK, 'H', "itemHammer_lvl" + hammerLvl}));
		diamondHeadCounter = 0;
	}

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
