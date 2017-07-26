package com.tom.thirdparty.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import com.tom.apis.TMLogger;
import com.tom.core.CoreInit;
import com.tom.factory.FactoryInit;
import com.tom.factory.tileentity.gui.GuiAdvBoiler;
import com.tom.factory.tileentity.gui.GuiAdvElectricFurnace;
import com.tom.factory.tileentity.gui.GuiAdvSteamFurnace;
import com.tom.factory.tileentity.gui.GuiAlloySmelter;
import com.tom.factory.tileentity.gui.GuiBasicBoiler;
import com.tom.factory.tileentity.gui.GuiBlastFurnace;
import com.tom.factory.tileentity.gui.GuiCoiler;
import com.tom.factory.tileentity.gui.GuiCrusher;
import com.tom.factory.tileentity.gui.GuiElectricFurnace;
import com.tom.factory.tileentity.gui.GuiIndustrialBlastFurnace;
import com.tom.factory.tileentity.gui.GuiMixer;
import com.tom.factory.tileentity.gui.GuiPlasticProcessor;
import com.tom.factory.tileentity.gui.GuiPlateBlendingMachine;
import com.tom.factory.tileentity.gui.GuiSolderingStation;
import com.tom.factory.tileentity.gui.GuiSteamAlloySmelter;
import com.tom.factory.tileentity.gui.GuiSteamCrusher;
import com.tom.factory.tileentity.gui.GuiSteamFurnace;
import com.tom.factory.tileentity.gui.GuiSteamMixer;
import com.tom.factory.tileentity.gui.GuiSteamPlateBlender;
import com.tom.factory.tileentity.gui.GuiSteamRubberProcessor;
import com.tom.factory.tileentity.gui.GuiSteamSolderingStation;
import com.tom.factory.tileentity.gui.GuiWireMill;
import com.tom.factory.tileentity.inventory.ContainerAdvBoiler;
import com.tom.factory.tileentity.inventory.ContainerAdvElectricFurnace;
import com.tom.factory.tileentity.inventory.ContainerAdvSteamFurnace;
import com.tom.factory.tileentity.inventory.ContainerAlloySmelter;
import com.tom.factory.tileentity.inventory.ContainerBasicBoiler;
import com.tom.factory.tileentity.inventory.ContainerCoiler;
import com.tom.factory.tileentity.inventory.ContainerCrusher;
import com.tom.factory.tileentity.inventory.ContainerElectricFurnace;
import com.tom.factory.tileentity.inventory.ContainerPlateBlendingMachine;
import com.tom.factory.tileentity.inventory.ContainerSolderingStation;
import com.tom.factory.tileentity.inventory.ContainerSteamAlloySmelter;
import com.tom.factory.tileentity.inventory.ContainerSteamCrusher;
import com.tom.factory.tileentity.inventory.ContainerSteamFurnace;
import com.tom.factory.tileentity.inventory.ContainerSteamPlateBlender;
import com.tom.factory.tileentity.inventory.ContainerSteamSolderingStation;
import com.tom.factory.tileentity.inventory.ContainerWireMill;
import com.tom.recipes.CraftingRecipes;
import com.tom.recipes.WrenchShapelessCraftingRecipe;
import com.tom.storage.StorageInit;

import com.tom.core.tileentity.gui.GuiResearchTable;
import com.tom.core.tileentity.inventory.ContainerResearchTable;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;

@JEIPlugin
public class JEIHandler extends BlankModPlugin {
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

	@Override
	public void register(@Nonnull IModRegistry registry) {
		TMLogger.info("Loading JEI Handler...");
		long time = System.currentTimeMillis();
		jeiHelper = registry.getJeiHelpers();
		registry.addRecipes(CustomCraftingRecipeCategory.get(), JEIConstants.CUSTOM_CRAFTING_ID);
		registry.addRecipeClickArea(GuiResearchTable.class, 167, 71, 23, 15, JEIConstants.CUSTOM_CRAFTING_ID);
		registry.addRecipes(CrusherRecipeCategory.get(), JEIConstants.CRUSHER_ID);
		registry.addRecipeClickArea(GuiCrusher.class, 65, 34, 52, 17, JEIConstants.CRUSHER_ID);
		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCrusher.class, JEIConstants.CRUSHER_ID, 0, 1, 3, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerResearchTable.class, JEIConstants.CUSTOM_CRAFTING_ID, 7, 9, 19, 36);
		registry.addRecipes(WireMillRecipeCategory.get(), JEIConstants.WIREMILL_ID);
		registry.addRecipeClickArea(GuiWireMill.class, 75, 35, 28, 17, JEIConstants.WIREMILL_ID);
		registry.addRecipes(PlateBlenderRecipeCategory.get(), JEIConstants.PLATE_BLENDER_ID);
		registry.addRecipeClickArea(GuiPlateBlendingMachine.class, 65, 34, 52, 17, JEIConstants.PLATE_BLENDER_ID);
		registry.addRecipes(AlloySmelterRecipeCategory.get(), JEIConstants.ALLOY_SMELTER_ID);
		registry.addRecipes(BlastFurnaceRecipeCategory.get(), JEIConstants.BLAST_FURNACE_ID);
		registry.addRecipes(CoilerRecipeCategory.get(), JEIConstants.COILER);
		registry.addRecipes(MixerRecipeCategory.get(mixerRecipeCategory), JEIConstants.MIXER);
		registry.addRecipes(PlasticRecipeCategory.get(), JEIConstants.PLASTIC);
		registry.addRecipes(RubberBoilerRecipeCategory.get(), JEIConstants.RUBBER_BOILER);
		registry.addRecipes(RubberProcessorRecipeCategory.get(), JEIConstants.RUBBER_PROCESSOR);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerWireMill.class, JEIConstants.WIREMILL_ID, 0, 1, 4, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerPlateBlendingMachine.class, JEIConstants.PLATE_BLENDER_ID, 0, 1, 4, 36);
		registry.addRecipeClickArea(GuiSteamCrusher.class, 65, 34, 52, 17, JEIConstants.CRUSHER_ID);
		PatternTerminalJEITransferHandler.loadPetternTerminalTransferHandler(recipeTransferRegistry);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerSteamCrusher.class, JEIConstants.CRUSHER_ID, 0, 1, 3, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerSteamFurnace.class, VanillaRecipeCategoryUid.SMELTING, 0, 1, 2, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerAdvSteamFurnace.class, VanillaRecipeCategoryUid.SMELTING, 0, 1, 2, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerBasicBoiler.class, VanillaRecipeCategoryUid.FUEL, 0, 1, 1, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerAdvBoiler.class, VanillaRecipeCategoryUid.FUEL, 0, 1, 1, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerElectricFurnace.class, VanillaRecipeCategoryUid.SMELTING, 0, 1, 3, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerAdvElectricFurnace.class, VanillaRecipeCategoryUid.SMELTING, 0, 1, 3, 36);
		registry.addRecipeClickArea(GuiSteamFurnace.class, 65, 34, 52, 17, VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeClickArea(GuiAdvSteamFurnace.class, 65, 34, 52, 17, VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeClickArea(GuiElectricFurnace.class, 65, 34, 52, 17, VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeClickArea(GuiAdvElectricFurnace.class, 65, 34, 52, 17, VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeClickArea(GuiSteamPlateBlender.class, 65, 34, 52, 17, JEIConstants.PLATE_BLENDER_ID);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerSteamPlateBlender.class, JEIConstants.PLATE_BLENDER_ID, 0, 1, 2, 36);
		registry.addRecipeClickArea(GuiBasicBoiler.class, 43, 46, 15, 15, VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeClickArea(GuiAdvBoiler.class, 43, 46, 15, 15, VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.advElectricFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.electricFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.steamFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.advSteamFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.crusher), JEIConstants.CRUSHER_ID);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.steamCrusher), JEIConstants.CRUSHER_ID);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.plateBlendingMachine), JEIConstants.PLATE_BLENDER_ID);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.steamPlateBlender), JEIConstants.PLATE_BLENDER_ID);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.wireMill), JEIConstants.WIREMILL_ID);
		registry.addRecipeCatalyst(new ItemStack(StorageInit.assembler), VanillaRecipeCategoryUid.CRAFTING, JEIConstants.CUSTOM_CRAFTING_ID);
		registry.addRecipeCatalyst(new ItemStack(CoreInit.researchTable), JEIConstants.CUSTOM_CRAFTING_ID);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerAlloySmelter.class, JEIConstants.ALLOY_SMELTER_ID, 0, 2, 4, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerSteamAlloySmelter.class, JEIConstants.ALLOY_SMELTER_ID, 0, 2, 3, 36);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.alloySmelter), JEIConstants.ALLOY_SMELTER_ID);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.steamAlloySmelter), JEIConstants.ALLOY_SMELTER_ID);
		registry.addRecipeClickArea(GuiAlloySmelter.class, 65, 34, 52, 17, JEIConstants.ALLOY_SMELTER_ID);
		registry.addRecipeClickArea(GuiSteamAlloySmelter.class, 65, 34, 52, 17, JEIConstants.ALLOY_SMELTER_ID);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.basicBoiler), VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.advBoiler), VanillaRecipeCategoryUid.FUEL);
		// registry.addRecipeCatalyst(new ItemStack(EnergyInit.Generator),
		// VanillaRecipeCategoryUid.FUEL);
		CraftingTerminalTransferHandler.registerClickAreas(registry);
		CraftingTerminalTransferHandler.registerTransferHandlers(recipeTransferRegistry);
		registry.addRecipeCatalyst(new ItemStack(StorageInit.craftingTerminal), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(StorageInit.partCraftingTerminal), VanillaRecipeCategoryUid.CRAFTING);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerSteamSolderingStation.class, JEIConstants.CUSTOM_CRAFTING_ID, 0, 9, 12, 36);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.steamSolderingStation), JEIConstants.CUSTOM_CRAFTING_ID);
		registry.addRecipeClickArea(GuiSteamSolderingStation.class, 65, 34, 52, 17, JEIConstants.CUSTOM_CRAFTING_ID);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerSolderingStation.class, JEIConstants.CUSTOM_CRAFTING_ID, 0, 9, 13, 36);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.solderingStation), JEIConstants.CUSTOM_CRAFTING_ID);
		registry.addRecipeClickArea(GuiSolderingStation.class, 79, 46, 52, 17, JEIConstants.CUSTOM_CRAFTING_ID);
		registry.addRecipeClickArea(GuiBlastFurnace.class, 65, 34, 52, 17, JEIConstants.BLAST_FURNACE_ID);
		registry.addRecipeClickArea(GuiIndustrialBlastFurnace.class, 60, 45, 52, 17, JEIConstants.BLAST_FURNACE_ID);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.blastFurnace), JEIConstants.BLAST_FURNACE_ID);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.industrialBlastFurnace), JEIConstants.BLAST_FURNACE_ID);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.coilerPlant), JEIConstants.COILER);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCoiler.class, JEIConstants.COILER, 0, 2, 4, 36);
		registry.addRecipeClickArea(GuiCoiler.class, 65, 34, 52, 17, JEIConstants.COILER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.steamMixer), JEIConstants.MIXER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.mixer), JEIConstants.MIXER);
		registry.addRecipeClickArea(GuiSteamMixer.class, 65, 15, 52, 17, JEIConstants.MIXER);
		registry.addRecipeClickArea(GuiMixer.class, 65, 15, 52, 17, JEIConstants.MIXER);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.plasticProcessor), JEIConstants.PLASTIC);
		registry.addRecipeClickArea(GuiPlasticProcessor.class, 118, 19, 31, 17, JEIConstants.PLASTIC);
		registry.addAdvancedGuiHandlers(new ConfiguratorGuiHandler());
		registry.addAdvancedGuiHandlers(new GuiTankHandler());
		registry.addAdvancedGuiHandlers(new GuiTerminalHandler());
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.rubberProcessor), JEIConstants.RUBBER_PROCESSOR);
		registry.addRecipeClickArea(GuiSteamRubberProcessor.class, 65, 34, 52, 17, JEIConstants.RUBBER_PROCESSOR);
		registry.addRecipeCatalyst(new ItemStack(FactoryInit.rubberBoiler), JEIConstants.RUBBER_BOILER);
		registry.addRecipes(CraftingRecipes.customRecipes, VanillaRecipeCategoryUid.CRAFTING);
		registry.handleRecipes(WrenchShapelessCraftingRecipe.class, new IRecipeWrapperFactory<WrenchShapelessCraftingRecipe>() {
			@Override
			public IRecipeWrapper getRecipeWrapper(WrenchShapelessCraftingRecipe recipe) {
				return new WrenchShapelessRecipeWrapper(jeiHelper, recipe);
			}
		}, VanillaRecipeCategoryUid.CRAFTING);
		// jeiHelper.getIngredientBlacklist().addIngredientToBlacklist(new
		// ItemStack(CoreInit.modelledItem));
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
		jeiHelper = registry.getJeiHelpers();
		registry.addRecipeCategories(new IRecipeCategory[]{new CustomCraftingRecipeCategory(), new CrusherRecipeCategory(), new WireMillRecipeCategory(), new PlateBlenderRecipeCategory(), new AlloySmelterRecipeCategory(), new BlastFurnaceRecipeCategory(), new CoilerRecipeCategory(), mixerRecipeCategory = new MixerRecipeCategory(), new PlasticRecipeCategory(), new RubberBoilerRecipeCategory(), new RubberProcessorRecipeCategory(),});
	}
}
