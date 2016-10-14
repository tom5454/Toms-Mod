package com.tom.thirdparty.jei;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import com.tom.apis.Function.BiFunction;
import com.tom.apis.TMLogger;
import com.tom.core.CoreInit;
import com.tom.energy.EnergyInit;
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
import com.tom.factory.tileentity.gui.GuiPlateBlendingMachine;
import com.tom.factory.tileentity.gui.GuiSolderingStation;
import com.tom.factory.tileentity.gui.GuiSteamAlloySmelter;
import com.tom.factory.tileentity.gui.GuiSteamCrusher;
import com.tom.factory.tileentity.gui.GuiSteamFurnace;
import com.tom.factory.tileentity.gui.GuiSteamPlateBlender;
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
import com.tom.storage.StorageInit;
import com.tom.thirdparty.jei.AlloySmelterRecipeCategory.AlloySmelterHandler;
import com.tom.thirdparty.jei.BlastFurnaceRecipeCategory.BlastFurnaceHandler;
import com.tom.thirdparty.jei.CoilerRecipeCategory.CoilerHandler;
import com.tom.thirdparty.jei.CrusherRecipeCategory.CrusherHandler;
import com.tom.thirdparty.jei.CustomCraftingRecipeCategory.CustomCraftingHandler;
import com.tom.thirdparty.jei.PlateBlenderRecipeCategory.PlateBlenderHandler;
import com.tom.thirdparty.jei.WireMillRecipeCategory.WireMillHandler;

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
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;

@JEIPlugin
public class JEIHandler extends BlankModPlugin{
	private static final BiFunction<IRecipeLayout, Boolean, ItemStack[]> directTransfer = new BiFunction<IRecipeLayout, Boolean, ItemStack[]>() {

		@Override
		public ItemStack[] apply(IRecipeLayout t, Boolean u) {
			List<ItemStack> inputs = new ArrayList<ItemStack>();
			List<ItemStack> outputs = new ArrayList<ItemStack>();
			IGuiItemStackGroup itemStackGroup = t.getItemStacks();
			for (IGuiIngredient<ItemStack> ingredient : itemStackGroup.getGuiIngredients().values()) {
				if (ingredient.isInput()) {
					if(!ingredient.getAllIngredients().isEmpty() && ingredient.getAllIngredients().get(0) != null){
						inputs.add(ingredient.getAllIngredients().get(0));
					}else{
						inputs.add(null);
					}
				}else{
					if(!ingredient.getAllIngredients().isEmpty() && ingredient.getAllIngredients().get(0) != null){
						outputs.add(ingredient.getAllIngredients().get(0));
					}else{
						outputs.add(null);
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
	@Override
	public void register(@Nonnull IModRegistry registry)
	{
		TMLogger.info("Loading JEI Handler...");
		jeiHelper = registry.getJeiHelpers();
		registry.addRecipeCategories(new CustomCraftingRecipeCategory());
		registry.addRecipeHandlers(new CustomCraftingHandler());
		registry.addRecipes(CustomCraftingRecipeCategory.get());
		registry.addRecipeClickArea(GuiResearchTable.class, 167, 71, 23, 15, JEIConstants.CUSTOM_CRAFTING_ID);
		registry.addRecipeCategories(new CrusherRecipeCategory());
		registry.addRecipeHandlers(new CrusherHandler());
		registry.addRecipes(CrusherRecipeCategory.get());
		registry.addRecipeClickArea(GuiCrusher.class, 65, 34, 52, 17, JEIConstants.CRUSHER_ID);
		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCrusher.class, JEIConstants.CRUSHER_ID, 0, 1, 3, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerResearchTable.class, JEIConstants.CUSTOM_CRAFTING_ID, 7, 9, 19, 36);
		PatternTerminalJEITransferHandler.clearUids();
		registry.addRecipeCategories(new WireMillRecipeCategory());
		registry.addRecipeHandlers(new WireMillHandler());
		registry.addRecipes(WireMillRecipeCategory.get());
		registry.addRecipeClickArea(GuiWireMill.class, 75, 35, 28, 17, JEIConstants.WIREMILL_ID);
		registry.addRecipeCategories(new PlateBlenderRecipeCategory());
		registry.addRecipeHandlers(new PlateBlenderHandler());
		registry.addRecipes(PlateBlenderRecipeCategory.get());
		registry.addRecipeClickArea(GuiPlateBlendingMachine.class, 65, 34, 52, 17, JEIConstants.PLATE_BLENDER_ID);
		registry.addRecipeCategories(new AlloySmelterRecipeCategory());
		registry.addRecipeHandlers(new AlloySmelterHandler());
		registry.addRecipes(AlloySmelterRecipeCategory.get());
		registry.addRecipeCategories(new BlastFurnaceRecipeCategory());
		registry.addRecipeHandlers(new BlastFurnaceHandler());
		registry.addRecipes(BlastFurnaceRecipeCategory.get());
		registry.addRecipeCategories(new CoilerRecipeCategory());
		registry.addRecipeHandlers(new CoilerHandler());
		registry.addRecipes(CoilerRecipeCategory.get());
		PatternTerminalJEITransferHandler.loadPetternTerminalTransferHandler(recipeTransferRegistry, VanillaRecipeCategoryUid.CRAFTING, directTransfer, true);
		PatternTerminalJEITransferHandler.loadPetternTerminalTransferHandler(recipeTransferRegistry, JEIConstants.CUSTOM_CRAFTING_ID, directTransfer);
		PatternTerminalJEITransferHandler.loadPetternTerminalTransferHandler(recipeTransferRegistry, VanillaRecipeCategoryUid.SMELTING, directTransfer);
		PatternTerminalJEITransferHandler.loadPetternTerminalTransferHandler(recipeTransferRegistry, JEIConstants.CRUSHER_ID, directTransfer);
		PatternTerminalJEITransferHandler.loadPetternTerminalTransferHandler(recipeTransferRegistry, JEIConstants.PLATE_BLENDER_ID, directTransfer);
		PatternTerminalJEITransferHandler.loadPetternTerminalTransferHandler(recipeTransferRegistry, JEIConstants.WIREMILL_ID, directTransfer);
		PatternTerminalJEITransferHandler.loadPetternTerminalTransferHandler(recipeTransferRegistry, JEIConstants.ALLOY_SMELTER_ID, directTransfer);
		PatternTerminalJEITransferHandler.loadPetternTerminalTransferHandler(recipeTransferRegistry, JEIConstants.BLAST_FURNACE_ID, directTransfer);
		PatternTerminalJEITransferHandler.loadPetternTerminalTransferHandler(recipeTransferRegistry, JEIConstants.COILER, directTransfer);
		PatternTerminalJEITransferHandler.loadPetternTerminalTransferHandler(recipeTransferRegistry, VanillaRecipeCategoryUid.BREWING, directTransfer);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerWireMill.class, JEIConstants.WIREMILL_ID, 0, 1, 4, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerPlateBlendingMachine.class, JEIConstants.PLATE_BLENDER_ID, 0, 1, 4, 36);
		PatternTerminalJEITransferHandler.loadClickAreas(registry);
		registry.addRecipeClickArea(GuiSteamCrusher.class, 65, 34, 52, 17, JEIConstants.CRUSHER_ID);
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
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.advElectricFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.electricFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.steamFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.advSteamFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.crusher), JEIConstants.CRUSHER_ID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.steamCrusher), JEIConstants.CRUSHER_ID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.plateBlendingMachine), JEIConstants.PLATE_BLENDER_ID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.steamPlateBlender), JEIConstants.PLATE_BLENDER_ID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.wireMill), JEIConstants.WIREMILL_ID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(StorageInit.assembler), VanillaRecipeCategoryUid.CRAFTING, JEIConstants.CUSTOM_CRAFTING_ID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(CoreInit.researchTable), JEIConstants.CUSTOM_CRAFTING_ID);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerAlloySmelter.class, JEIConstants.ALLOY_SMELTER_ID, 0, 2, 4, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerSteamAlloySmelter.class, JEIConstants.ALLOY_SMELTER_ID, 0, 2, 3, 36);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.alloySmelter), JEIConstants.ALLOY_SMELTER_ID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.steamAlloySmelter), JEIConstants.ALLOY_SMELTER_ID);
		registry.addRecipeClickArea(GuiAlloySmelter.class, 65, 34, 52, 17, JEIConstants.ALLOY_SMELTER_ID);
		registry.addRecipeClickArea(GuiSteamAlloySmelter.class, 65, 34, 52, 17, JEIConstants.ALLOY_SMELTER_ID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.basicBoiler), VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.advBoiler), VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeCategoryCraftingItem(new ItemStack(EnergyInit.Generator), VanillaRecipeCategoryUid.FUEL);
		CraftingTerminalTransferHandler.registerClickAreas(registry);
		CraftingTerminalTransferHandler.registerTransferHandlers(recipeTransferRegistry);
		registry.addRecipeCategoryCraftingItem(new ItemStack(StorageInit.craftingTerminal), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(StorageInit.partCraftingTerminal), VanillaRecipeCategoryUid.CRAFTING);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerSteamSolderingStation.class, JEIConstants.CUSTOM_CRAFTING_ID, 0, 9, 12, 36);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.steamSolderingStation), JEIConstants.CUSTOM_CRAFTING_ID);
		registry.addRecipeClickArea(GuiSteamSolderingStation.class, 65, 34, 52, 17, JEIConstants.CUSTOM_CRAFTING_ID);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerSolderingStation.class, JEIConstants.CUSTOM_CRAFTING_ID, 0, 9, 13, 36);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.solderingStation), JEIConstants.CUSTOM_CRAFTING_ID);
		registry.addRecipeClickArea(GuiSolderingStation.class, 79, 46, 52, 17, JEIConstants.CUSTOM_CRAFTING_ID);
		registry.addRecipeClickArea(GuiBlastFurnace.class, 65, 34, 52, 17, JEIConstants.BLAST_FURNACE_ID);
		registry.addRecipeClickArea(GuiIndustrialBlastFurnace.class, 60, 45, 52, 17, JEIConstants.BLAST_FURNACE_ID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.blastFurnace), JEIConstants.BLAST_FURNACE_ID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.industrialBlastFurnace), JEIConstants.BLAST_FURNACE_ID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(FactoryInit.coilerPlant), JEIConstants.COILER);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCoiler.class, JEIConstants.COILER, 0, 2, 4, 36);
		registry.addRecipeClickArea(GuiCoiler.class, 65, 34, 52, 17, JEIConstants.COILER);
		TMLogger.info("JEI Handler: Load Complete.");
	}
	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
		JEIHandler.jeiRuntime = jeiRuntime;
	}
	public static void setJeiSearchText(String text){
		if(jeiRuntime != null){
			if(jeiRuntime.getItemListOverlay() != null){
				jeiRuntime.getItemListOverlay().setFilterText(text);
				try{
					Class<?> c = Class.forName("mezz.jei.gui.ItemListOverlay");
					Method m = c.getDeclaredMethod("updateLayout");
					m.setAccessible(true);
					m.invoke(jeiRuntime.getItemListOverlay());
				}catch(Exception e){
					//CoreInit.log.debug(e);
				}
			}
		}
	}
}
