package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.tom.api.research.Research;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.research.ResearchHandler;
import com.tom.energy.EnergyInit;
import com.tom.factory.FactoryInit;
import com.tom.factory.tileentity.gui.GuiAdvBoiler;
import com.tom.factory.tileentity.gui.GuiAdvElectricFurnace;
import com.tom.factory.tileentity.gui.GuiAdvSteamFurnace;
import com.tom.factory.tileentity.gui.GuiAlloySmelter;
import com.tom.factory.tileentity.gui.GuiBasicBoiler;
import com.tom.factory.tileentity.gui.GuiBlastFurnace;
import com.tom.factory.tileentity.gui.GuiCoiler;
import com.tom.factory.tileentity.gui.GuiCokeOven;
import com.tom.factory.tileentity.gui.GuiCrusher;
import com.tom.factory.tileentity.gui.GuiElectricFurnace;
import com.tom.factory.tileentity.gui.GuiIndustrialBlastFurnace;
import com.tom.factory.tileentity.gui.GuiLaserEngraver;
import com.tom.factory.tileentity.gui.GuiMixer;
import com.tom.factory.tileentity.gui.GuiPlasticProcessor;
import com.tom.factory.tileentity.gui.GuiPlateBendingMachine;
import com.tom.factory.tileentity.gui.GuiSolderingStation;
import com.tom.factory.tileentity.gui.GuiSteamAlloySmelter;
import com.tom.factory.tileentity.gui.GuiSteamCrusher;
import com.tom.factory.tileentity.gui.GuiSteamFurnace;
import com.tom.factory.tileentity.gui.GuiSteamMixer;
import com.tom.factory.tileentity.gui.GuiSteamPlateBender;
import com.tom.factory.tileentity.gui.GuiSteamRubberProcessor;
import com.tom.factory.tileentity.gui.GuiSteamSolderingStation;
import com.tom.factory.tileentity.gui.GuiUVLightbox;
import com.tom.factory.tileentity.gui.GuiWireMill;
import com.tom.factory.tileentity.inventory.ContainerAdvBoiler;
import com.tom.factory.tileentity.inventory.ContainerAdvElectricFurnace;
import com.tom.factory.tileentity.inventory.ContainerAdvSteamFurnace;
import com.tom.factory.tileentity.inventory.ContainerAlloySmelter;
import com.tom.factory.tileentity.inventory.ContainerBasicBoiler;
import com.tom.factory.tileentity.inventory.ContainerCoiler;
import com.tom.factory.tileentity.inventory.ContainerCrusher;
import com.tom.factory.tileentity.inventory.ContainerElectricFurnace;
import com.tom.factory.tileentity.inventory.ContainerPlateBendingMachine;
import com.tom.factory.tileentity.inventory.ContainerSolderingStation;
import com.tom.factory.tileentity.inventory.ContainerSteamAlloySmelter;
import com.tom.factory.tileentity.inventory.ContainerSteamCrusher;
import com.tom.factory.tileentity.inventory.ContainerSteamFurnace;
import com.tom.factory.tileentity.inventory.ContainerSteamPlateBender;
import com.tom.factory.tileentity.inventory.ContainerSteamSolderingStation;
import com.tom.factory.tileentity.inventory.ContainerWireMill;
import com.tom.storage.StorageInit;
import com.tom.util.TMLogger;

import com.tom.core.item.ItemBlueprint;
import com.tom.core.item.ItemCircuit;

import com.tom.core.tileentity.gui.GuiResearchTable;
import com.tom.core.tileentity.inventory.ContainerResearchTable;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mezz.jei.config.Constants;

@JEIPlugin
public class JEIHandler implements IModPlugin {
	protected static final BiFunction<IRecipeLayout, Boolean, ItemStack[]> directTransfer = new BiFunction<IRecipeLayout, Boolean, ItemStack[]>() {

		@Override
		public ItemStack[] apply(IRecipeLayout t, Boolean u) {
			List<ItemStack> inputs = new ArrayList<>();
			List<ItemStack> outputs = new ArrayList<>();
			IGuiItemStackGroup itemStackGroup = t.getItemStacks();
			for (IGuiIngredient<ItemStack> ingredient : itemStackGroup.getGuiIngredients().values()) {
				if (ingredient.isInput()) {
					if (!ingredient.getAllIngredients().isEmpty() && ingredient.getAllIngredients().get(0) != null) {
						inputs.add(ingredient.getAllIngredients().get(0));
					} else {
						inputs.add(ItemStack.EMPTY);
					}
				} else {
					if (!ingredient.getAllIngredients().isEmpty() && ingredient.getAllIngredients().get(0) != null) {
						outputs.add(ingredient.getAllIngredients().get(0));
					} else {
						outputs.add(ItemStack.EMPTY);
					}
				}
			}
			return u ? outputs.toArray(new ItemStack[]{}) : inputs.toArray(new ItemStack[]{});
		}
	};

	public JEIHandler() {
		CoreInit.log.info("Loading JEI Plugin.");
	}

	public static IJeiHelpers jeiHelper;
	public static IJeiRuntime jeiRuntime;
	public static MixerRecipeCategory mixerRecipeCategory;
	public static ResearchRenderer researchRenderer;
	public static final IIngredientType<Research> RESEARCH = () -> Research.class;
	@Nonnull
	public static IDrawable tankOverlay;
	@Nonnull
	public static IDrawable tankBackground;

	@Override
	public void register(@Nonnull IModRegistry registry) {
		TMLogger.info("Loading JEI Handler...");
		long time = System.currentTimeMillis();
		jeiHelper = registry.getJeiHelpers();
		ResourceLocation backgroundTexture = Constants.RECIPE_BACKGROUND;
		tankBackground = jeiHelper.getGuiHelper().drawableBuilder(backgroundTexture, 220, 196, 18, 60)
				.addPadding(-1, -1, -1, -1)
				.build();
		tankOverlay = jeiHelper.getGuiHelper().drawableBuilder(backgroundTexture, 238, 196, 18, 60)
				.addPadding(-1, -1, -1, -1)
				.build();
		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();

		if(Config.enableResearchSystem){
			registry.addRecipes(CustomCraftingRecipeCategory.get(), JEIConstants.CUSTOM_CRAFTING);
			registry.addRecipes(ResearchCategory.get(), JEIConstants.RESEARCH);

			registry.addRecipeClickArea(GuiResearchTable.class, 167, 71, 23, 15, JEIConstants.CUSTOM_CRAFTING);
			registry.addRecipeClickArea(GuiSteamSolderingStation.class, 65, 34, 52, 17, JEIConstants.CUSTOM_CRAFTING);
			registry.addRecipeClickArea(GuiSolderingStation.class, 79, 46, 52, 17, JEIConstants.CUSTOM_CRAFTING);

			recipeTransferRegistry.addRecipeTransferHandler(ContainerResearchTable.class, JEIConstants.CUSTOM_CRAFTING, 7, 9, 19, 36);
			recipeTransferRegistry.addRecipeTransferHandler(ContainerSteamSolderingStation.class, JEIConstants.CUSTOM_CRAFTING, 0, 9, 12, 36);
			recipeTransferRegistry.addRecipeTransferHandler(ContainerSolderingStation.class, JEIConstants.CUSTOM_CRAFTING, 0, 9, 13, 36);

			registry.addRecipeCatalyst(new ItemStack(CoreInit.researchTable), JEIConstants.CUSTOM_CRAFTING);
			registry.addRecipeCatalyst(new ItemStack(StorageInit.assembler), VanillaRecipeCategoryUid.CRAFTING, JEIConstants.CUSTOM_CRAFTING);
			registry.addRecipeCatalyst(new ItemStack(FactoryInit.steamSolderingStation), JEIConstants.CUSTOM_CRAFTING);
			registry.addRecipeCatalyst(new ItemStack(FactoryInit.solderingStation), JEIConstants.CUSTOM_CRAFTING);
			registry.addRecipeCatalyst(new ItemStack(CoreInit.researchTable), JEIConstants.RESEARCH);
			registry.addRecipeCatalyst(new ItemStack(CoreInit.blueprint), JEIConstants.RESEARCH);
		}
		researchRenderer.renderer = registry.getIngredientRegistry().getIngredientRenderer(VanillaTypes.ITEM);

		registry.addRecipes(CrusherRecipeCategory.get(), JEIConstants.CRUSHER);
		registry.addRecipes(WireMillRecipeCategory.get(), JEIConstants.WIREMILL);
		registry.addRecipes(PlateBlenderRecipeCategory.get(), JEIConstants.PLATE_BENDER);
		registry.addRecipes(AlloySmelterRecipeCategory.get(), JEIConstants.ALLOY_SMELTER);
		registry.addRecipes(BlastFurnaceRecipeCategory.get(), JEIConstants.BLAST_FURNACE);
		registry.addRecipes(CoilerRecipeCategory.get(), JEIConstants.COILER);
		registry.addRecipes(MixerRecipeCategory.get(mixerRecipeCategory), JEIConstants.MIXER);
		registry.addRecipes(PlasticRecipeCategory.get(), JEIConstants.PLASTIC);
		registry.addRecipes(RubberBoilerRecipeCategory.get(), JEIConstants.RUBBER_BOILER);
		registry.addRecipes(RubberProcessorRecipeCategory.get(), JEIConstants.RUBBER_PROCESSOR);
		registry.addRecipes(UVBoxCategory.get(), JEIConstants.UV_BOX);
		registry.addRecipes(LaserEngraverCategory.get(), JEIConstants.LASER_ENGRAVER);
		registry.addRecipes(InWorldCraftingCategory.get(), JEIConstants.IN_WORLD);
		registry.addRecipes(CokeOvenCategory.get(), JEIConstants.COKE_OVEN);
		registry.addRecipes(ScientistRecipeCategory.get(), JEIConstants.SCIENTIST);

		registry.addRecipeClickArea(GuiSteamCrusher.class, 65, 34, 52, 17, JEIConstants.CRUSHER);
		registry.addRecipeClickArea(GuiCrusher.class, 65, 34, 52, 17, JEIConstants.CRUSHER);
		registry.addRecipeClickArea(GuiWireMill.class, 75, 35, 28, 17, JEIConstants.WIREMILL);
		registry.addRecipeClickArea(GuiPlateBendingMachine.class, 65, 34, 52, 17, JEIConstants.PLATE_BENDER);
		registry.addRecipeClickArea(GuiSteamFurnace.class, 65, 34, 52, 17, VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeClickArea(GuiAdvSteamFurnace.class, 65, 34, 52, 17, VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeClickArea(GuiElectricFurnace.class, 65, 34, 52, 17, VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeClickArea(GuiAdvElectricFurnace.class, 65, 34, 52, 17, VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeClickArea(GuiSteamPlateBender.class, 65, 34, 52, 17, JEIConstants.PLATE_BENDER);
		registry.addRecipeClickArea(GuiBasicBoiler.class, 43, 46, 15, 15, VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeClickArea(GuiAdvBoiler.class, 43, 46, 15, 15, VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeClickArea(GuiAlloySmelter.class, 65, 34, 52, 17, JEIConstants.ALLOY_SMELTER);
		registry.addRecipeClickArea(GuiSteamAlloySmelter.class, 65, 34, 52, 17, JEIConstants.ALLOY_SMELTER);
		CraftingTerminalTransferHandler.registerClickAreas(registry);
		registry.addRecipeClickArea(GuiBlastFurnace.class, 65, 34, 52, 17, JEIConstants.BLAST_FURNACE);
		registry.addRecipeClickArea(GuiIndustrialBlastFurnace.class, 60, 45, 52, 17, JEIConstants.BLAST_FURNACE);
		registry.addRecipeClickArea(GuiCoiler.class, 65, 34, 52, 17, JEIConstants.COILER);
		registry.addRecipeClickArea(GuiSteamMixer.class, 65, 15, 52, 17, JEIConstants.MIXER);
		registry.addRecipeClickArea(GuiMixer.class, 65, 15, 52, 17, JEIConstants.MIXER);
		registry.addRecipeClickArea(GuiPlasticProcessor.class, 118, 19, 31, 17, JEIConstants.PLASTIC);
		registry.addRecipeClickArea(GuiSteamRubberProcessor.class, 65, 34, 52, 17, JEIConstants.RUBBER_PROCESSOR);
		registry.addRecipeClickArea(GuiUVLightbox.class, 118, 19, 31, 17, JEIConstants.UV_BOX);
		registry.addRecipeClickArea(GuiLaserEngraver.class, 118, 19, 31, 17, JEIConstants.LASER_ENGRAVER);
		registry.addRecipeClickArea(GuiCokeOven.class, 118, 19, 31, 17, JEIConstants.COKE_OVEN);

		recipeTransferRegistry.addRecipeTransferHandler(ContainerCrusher.class, JEIConstants.CRUSHER, 0, 1, 3, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerWireMill.class, JEIConstants.WIREMILL, 0, 1, 4, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerPlateBendingMachine.class, JEIConstants.PLATE_BENDER, 0, 1, 4, 36);
		PatternTerminalJEITransferHandler.loadPetternTerminalTransferHandler(recipeTransferRegistry);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerSteamCrusher.class, JEIConstants.CRUSHER, 0, 1, 3, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerSteamFurnace.class, VanillaRecipeCategoryUid.SMELTING, 0, 1, 2, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerAdvSteamFurnace.class, VanillaRecipeCategoryUid.SMELTING, 0, 1, 2, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerBasicBoiler.class, VanillaRecipeCategoryUid.FUEL, 0, 1, 1, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerAdvBoiler.class, VanillaRecipeCategoryUid.FUEL, 0, 1, 1, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerElectricFurnace.class, VanillaRecipeCategoryUid.SMELTING, 0, 1, 3, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerAdvElectricFurnace.class, VanillaRecipeCategoryUid.SMELTING, 0, 1, 3, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerSteamPlateBender.class, JEIConstants.PLATE_BENDER, 0, 1, 2, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerAlloySmelter.class, JEIConstants.ALLOY_SMELTER, 0, 2, 4, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerSteamAlloySmelter.class, JEIConstants.ALLOY_SMELTER, 0, 2, 3, 36);
		CraftingTerminalTransferHandler.registerTransferHandlers(recipeTransferRegistry);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCoiler.class, JEIConstants.COILER, 0, 2, 4, 36);

		registry.addRecipeCatalyst(new ItemStack(FactoryInit.advElectricFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.electricFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.steamFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.advSteamFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.crusher), JEIConstants.CRUSHER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.steamCrusher), JEIConstants.CRUSHER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.advSteamCrusher), JEIConstants.CRUSHER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.plateBendingMachine), JEIConstants.PLATE_BENDER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.steamPlateBendeingMachine), JEIConstants.PLATE_BENDER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.wireMill), JEIConstants.WIREMILL);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.alloySmelter), JEIConstants.ALLOY_SMELTER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.steamAlloySmelter), JEIConstants.ALLOY_SMELTER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.basicBoiler), VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.advBoiler), VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeCatalyst(new ItemStack(EnergyInit.Generator), VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeCatalyst(new ItemStack(StorageInit.craftingTerminal), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(StorageInit.partCraftingTerminal), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.blastFurnace), JEIConstants.BLAST_FURNACE);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.industrialBlastFurnace), JEIConstants.BLAST_FURNACE);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.coilerPlant), JEIConstants.COILER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.steamMixer), JEIConstants.MIXER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.mixer), JEIConstants.MIXER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.plasticProcessor), JEIConstants.PLASTIC);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.rubberProcessor), JEIConstants.RUBBER_PROCESSOR);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.rubberBoiler), JEIConstants.RUBBER_BOILER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.uvLightbox), JEIConstants.UV_BOX);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.laserEngraver), JEIConstants.LASER_ENGRAVER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.cokeOven), JEIConstants.COKE_OVEN);

		registry.addAdvancedGuiHandlers(new ConfiguratorGuiHandler());
		registry.addAdvancedGuiHandlers(new GuiTankHandler());
		registry.addAdvancedGuiHandlers(new GuiTerminalHandler());

		long tM = System.currentTimeMillis() - time;
		TMLogger.info("JEI Handler: Load Complete in " + tM + " miliseconds.");
	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
		JEIHandler.jeiRuntime = jeiRuntime;
	}

	public static void setJeiSearchText(String text) {
		if (jeiRuntime != null) {
			if (jeiRuntime.getIngredientFilter() != null) {
				jeiRuntime.getIngredientFilter().setFilterText(text);
				/*try{
					Class<?> c = Class.forName("mezz.jei.gui.ItemListOverlay");
					Method m = c.getDeclaredMethod("updateLayout");
					m.setAccessible(true);
					m.invoke(jeiRuntime.getItemListOverlay());
				}catch(Exception e){
					//CoreInit.log.debug(e);
				}*/
			}
		}
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		TMLogger.info("Registering JEI Categories");
		jeiHelper = registry.getJeiHelpers();
		if(Config.enableResearchSystem)registry.addRecipeCategories(new CustomCraftingRecipeCategory(), new ResearchCategory());
		registry.addRecipeCategories(new IRecipeCategory[]{new CrusherRecipeCategory(), new WireMillRecipeCategory(),
				new PlateBlenderRecipeCategory(), new AlloySmelterRecipeCategory(), new BlastFurnaceRecipeCategory(),
				new CoilerRecipeCategory(), mixerRecipeCategory = new MixerRecipeCategory(), new PlasticRecipeCategory(),
				new RubberBoilerRecipeCategory(), new RubberProcessorRecipeCategory(), new LaserEngraverCategory(),
				new UVBoxCategory(), new InWorldCraftingCategory(), new CokeOvenCategory(), new ScientistRecipeCategory(),

		});
	}
	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		TMLogger.info("Registering JEI Item Subtypes");
		subtypeRegistry.registerSubtypeInterpreter(CoreInit.craftingMaterial, itemStack -> {
			return "tomsmodmaterial_" + CraftingMaterial.get(itemStack.getMetadata()).getName();
		});
		subtypeRegistry.registerSubtypeInterpreter(CoreInit.circuit, itemStack -> {
			return "tomsmodcircuit_" + ItemCircuit.getType(itemStack);
		});
		subtypeRegistry.registerSubtypeInterpreter(CoreInit.circuitRaw, itemStack -> {
			return "tomsmodcircuitraw_" + ItemCircuit.getType(itemStack);
		});
		subtypeRegistry.registerSubtypeInterpreter(CoreInit.circuitUnassembled, itemStack -> {
			return "tomsmodcircuitua_" + ItemCircuit.getType(itemStack);
		});
		subtypeRegistry.registerSubtypeInterpreter(CoreInit.blueprint, itemStack -> {
			return ItemBlueprint.isResearch(itemStack) ? ISubtypeInterpreter.NONE : "tmblueprint_" + itemStack.getTagCompound().getString("id");
		});
	}
	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
		TMLogger.info("Registering JEI Ingredients");
		registry.register(RESEARCH, ResearchHandler.getAllResearches(), new ResearchHelper(), researchRenderer = new ResearchRenderer());
	}
}
