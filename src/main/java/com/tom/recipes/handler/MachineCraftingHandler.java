package com.tom.recipes.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import com.tom.apis.EmptyEntry;
import com.tom.apis.RecipeData;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.defense.DefenseInit;
import com.tom.factory.FactoryInit;

public class MachineCraftingHandler {
	public static void loadRecipes() {
		addCrusherRecipe(new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.BLAZE_POWDER, 5));
		addCrusherRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.SAND, 1));
		addCrusherRecipe(new ItemStack(Blocks.GRAVEL), new ItemStack(Items.FLINT, 1));
		addCrusherRecipe(new ItemStack(Blocks.STONE), new ItemStack(Blocks.GRAVEL, 1));
		addCrusherRecipe(new ItemStack(Items.REEDS), new ItemStack(Items.SUGAR, 2));
		addAlloySmelterRecipe(TMResource.COPPER.getStackNormal(Type.INGOT, 3), TMResource.TIN.getStackNormal(Type.INGOT), TMResource.BRONZE.getStackNormal(Type.INGOT, 4));
		addAlloySmelterRecipe(TMResource.GOLD.getStackNormal(Type.INGOT), TMResource.SILVER.getStackNormal(Type.INGOT), TMResource.ELECTRUM.getStackNormal(Type.INGOT, 2));
		addCrusherRecipe(new ItemStack(Blocks.NETHERRACK), CraftingMaterial.NETHERRACK_DUST.getStackNormal());
		addAlloySmelterRecipe(TMResource.LEAD.getStackNormal(Type.INGOT), TMResource.TIN.getStackNormal(Type.INGOT), CraftingMaterial.SOLDERING_ALLOY.getStackNormal(2));
		addPlateBlenderRecipe(CraftingMaterial.SILICON.getStackNormal(), CraftingMaterial.SILICON_PLATE.getStackNormal(), 1);
		addCrusherRecipe(new ItemStack(Blocks.GLASS), CraftingMaterial.GLASS_DUST.getStackNormal());
		addCokeOvenRecipe(new ItemStack(Items.COAL), new ItemStack(FactoryInit.coalCoke), 500, 1800);
		addCokeOvenRecipe(new ItemStack(Blocks.COAL_BLOCK), new ItemStack(FactoryInit.blockCoalCoke), 5000, 15500);
		addCokeOvenRecipe(new ItemStack(Blocks.LOG), new ItemStack(Items.COAL, 1, 1), 250, 1600);
		addBlastFurnaceRecipe(new ItemStack(Items.IRON_INGOT), ItemStack.EMPTY, TMResource.STEEL.getStackNormal(Type.INGOT), 2000, 0);
		addBlastFurnaceRecipe(TMResource.IRON.getBlockStackNormal(1), ItemStack.EMPTY, TMResource.STEEL.getBlockStackNormal(1), 16000, 0);
		addCrusherRecipe(TMResource.WOLFRAM.getStackNormal(Type.CRUSHED_ORE), CraftingMaterial.TUNGSTATE_DUST.getStackNormal());
		addBlastFurnaceRecipe(CraftingMaterial.TUNGSTATE_DUST.getStackNormal(), ItemStack.EMPTY, CraftingMaterial.HOT_WOLFRAM_INGOT.getStackNormal(), 6400, 0);
		addAlloySmelterRecipe(new ItemStack(Blocks.REDSTONE_BLOCK), TMResource.ELECTRUM.getStackNormal(Type.INGOT, 4), TMResource.REDSTONE.getStackNormal(Type.INGOT, 4));
		addAlloySmelterRecipe(TMResource.COPPER.getStackNormal(Type.INGOT), TMResource.NICKEL.getStackNormal(Type.INGOT), CraftingMaterial.CUPRONICKEL_INGOT.getStackNormal(2));
		addWireMillRecipe(CraftingMaterial.CUPRONICKEL_INGOT.getStackNormal(4), CraftingMaterial.CUPRONICKEL_HEATING_COIL.getStackNormal(), 1);
		addBlastFurnaceRecipe(CraftingMaterial.TUNGSTATE_DUST.getStackNormal(), TMResource.COAL.getStackNormal(Type.DUST_TINY, 3), CraftingMaterial.HOT_WOLFRAM_INGOT.getStackNormal(), 6000, 2200);
		addBlastFurnaceRecipe(new ItemStack(Items.IRON_INGOT), TMResource.COAL.getStackNormal(Type.DUST, 2), TMResource.STEEL.getStackNormal(Type.INGOT), 2800, 1020);
		addBlastFurnaceRecipe(TMResource.IRON.getBlockStackNormal(1), TMResource.COAL.getStackNormal(Type.DUST, 18), TMResource.STEEL.getBlockStackNormal(1), 25000, 1020);
		addBlastFurnaceRecipe(CraftingMaterial.ENDERIUM_BASE.getStackNormal(), new ItemStack(Items.ENDER_PEARL, 4), TMResource.ENDERIUM.getStackNormal(Type.INGOT), 5000, 2000);
		addBlastFurnaceRecipe(TMResource.WOLFRAM.getStackNormal(Type.DUST), TMResource.STEEL.getStackNormal(Type.INGOT), TMResource.TUNGSTENSTEEL.getStackNormal(Type.INGOT, 2), 15000, 3000);
		addBlastFurnaceRecipe(TMResource.TITANIUM.getStackNormal(Type.DUST), ItemStack.EMPTY, TMResource.TITANIUM.getStackNormal(Type.INGOT), 4000, 1500);
		addBlastFurnaceRecipe(TMResource.CHROME.getStackNormal(Type.DUST), ItemStack.EMPTY, TMResource.CHROME.getStackNormal(Type.INGOT), 4000, 1700);
		addBlastFurnaceRecipe(TMResource.WOLFRAM.getStackNormal(Type.DUST), ItemStack.EMPTY, CraftingMaterial.HOT_WOLFRAM_INGOT.getStackNormal(), 4000, 2000);
		addBlastFurnaceRecipe(TMResource.TUNGSTENSTEEL.getStackNormal(Type.DUST), ItemStack.EMPTY, CraftingMaterial.HOT_TUNGSTENSTEEL_INGOT.getStackNormal(), 4000, 3000);
		addBlastFurnaceRecipe(CraftingMaterial.BAUXITE_DUST.getStackNormal(), TMResource.MERCURY.getStackNormal(Type.GEM), TMResource.ALUMINUM.getStackNormal(Type.INGOT), 5000, 1700);
		addBlastFurnaceRecipe(TMResource.MERCURY.getStackNormal(Type.CRUSHED_ORE), TMResource.COAL.getStackNormal(Type.DUST, 4), TMResource.MERCURY.getStackNormal(Type.GEM), 5000, 1700);
		addAlloySmelterRecipe(TMResource.OBSIDIAN.getStackNormal(Type.DUST, 2), TMResource.LEAD.getStackNormal(Type.DUST), new ItemStack(CoreInit.hardenedGlass));
		addAlloySmelterRecipe(new ItemStack(CoreInit.hardenedGlass, 2), TMResource.ENDERIUM.getStackNormal(Type.DUST), new ItemStack(CoreInit.hardenedGlass, 2, 1));
		addWireMillRecipe(TMResource.STEEL.getStackNormal(Type.PLATE, 3), new ItemStack(CoreInit.steelFence, 2), 2);
		addCrusherRecipe(new ItemStack(DefenseInit.oreMonazit, 1, 0), new ItemStack(DefenseInit.crushedMonazit, 2, 0));
		addCrusherRecipe(new ItemStack(DefenseInit.oreMonazit, 1, 1), new ItemStack(DefenseInit.crushedMonazit, 2, 1));
		addPlateBlenderRecipe(TMResource.TIN.getStackNormal(Type.PLATE), CraftingMaterial.TIN_CAN.getStackNormal(), 1);
		addCrusherRecipe(TMResource.ALUMINUM.getStackNormal(Type.CRUSHED_ORE), CraftingMaterial.BAUXITE_DUST.getStackNormal());
		addBlastFurnaceRecipe(TMResource.ALUMINUM.getStackNormal(Type.DUST, 2), TMResource.COAL.getStackNormal(Type.DUST), TMResource.ALUMINUM.getStackNormal(Type.INGOT, 2), 5000, 1700);
		addBlastFurnaceRecipe(TMResource.ALUMINUM.getStackNormal(Type.DUST), ItemStack.EMPTY, TMResource.ALUMINUM.getStackNormal(Type.INGOT), 5000, 0);
		addPlateBlenderRecipe(CraftingMaterial.ELECTRICAL_STEEL_INGOT.getStackNormal(), CraftingMaterial.ELECTRICAL_STEEL_PLATE.getStackNormal(), 1);
		addAlloySmelterRecipe(TMResource.STEEL.getStackNormal(Type.INGOT, 4), CraftingMaterial.RAW_ELECTRICAL_STEEL_DUST.getStackNormal(), CraftingMaterial.ELECTRICAL_STEEL_INGOT.getStackNormal(4));
		addAlloySmelterRecipe(TMResource.COPPER.getStackNormal(Type.INGOT, 3), TMResource.ZINC.getStackNormal(Type.INGOT), TMResource.BRASS.getStackNormal(Type.INGOT, 4));
		addWireMillRecipe(new ItemStack(Blocks.OBSIDIAN), CraftingMaterial.OBSIDIAN_ROD.getStackNormal(2), 5);
		addCrusherRecipe(CraftingMaterial.IMPURE_FLUIX.getStackNormal(), CraftingMaterial.IMPURE_FLUIX_DUST.getStackNormal());
		addLaserEngraverRecipe(1, new ItemStack(Items.REDSTONE), CraftingMaterial.BLUEPRINT_BASIC_CHIPSET.getStackNormal(), CraftingMaterial.BASIC_CHIPSET.getStackNormal(), 600, 0);
		addLaserEngraverRecipe(3, TMResource.GOLD.getStackNormal(Type.DUST), CraftingMaterial.BLUEPRINT_ADVANCED_CHIPSET.getStackNormal(), CraftingMaterial.ADVANCED_CHIPSET.getStackNormal(), 1200, 1);
		addLaserEngraverRecipe(5, TMResource.FLUIX.getStackNormal(Type.DUST), CraftingMaterial.BLUEPRINT_FLUIX_CHIPSET.getStackNormal(), CraftingMaterial.FLUIX_CHIPSET.getStackNormal(), 1700, 1);
		addLaserEngraverRecipe(8, TMResource.PLATINUM.getStackNormal(Type.DUST), CraftingMaterial.BLUEPRINT_QUANTUM_CHIPSET.getStackNormal(), CraftingMaterial.QUANTUM_CHIPSET.getStackNormal(), 2500, 2);
		addLaserEngraverRecipe(1, new ItemStack(Items.GOLD_NUGGET, 2), CraftingMaterial.BLUEPRINT_LOGIC_PROCESSOR.getStackNormal(), CraftingMaterial.LOGIC_PROCESSOR.getStackNormal(4), 800, 1);
		addCrusherRecipe(CraftingMaterial.CRUSHED_OBSIDIAN.getStackNormal(), TMResource.OBSIDIAN.getStackNormal(Type.DUST));
		addCrusherRecipe(new ItemStack(Blocks.OBSIDIAN), CraftingMaterial.CRUSHED_OBSIDIAN.getStackNormal());
		addHammerRecipe(Blocks.OBSIDIAN.getDefaultState(), 2, CraftingMaterial.CRUSHED_OBSIDIAN.getStackNormal());
		addHammerRecipe(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE), 2, new ItemStack(Blocks.GRAVEL));
		addHammerRecipe(Blocks.GRAVEL.getDefaultState(), 1, new ItemStackWRng(new ItemStack(Blocks.SAND)), new ItemStackWRng(new ItemStack(Items.FLINT), 15));
		addHammerRecipe(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE), 2, new ItemStack(Blocks.GRAVEL));
		addHammerRecipe(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE), 2, new ItemStack(Blocks.GRAVEL));
		addHammerRecipe(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE), 2, new ItemStack(Blocks.GRAVEL));
		addHammerRecipe(Blocks.COBBLESTONE.getDefaultState(), 2, new ItemStack(Blocks.GRAVEL));
		addElectrolyzerRecipe(new FluidStack(FluidRegistry.WATER, 300), 200, new FluidStack(CoreInit.Hydrogen.get(), 200), new FluidStack(CoreInit.Oxygen.get(), 100));
		addCentrifugeRecipe(new FluidStack(CoreInit.Hydrogen.get(), 5), 5, new FluidStack(CoreInit.Deuterium.get(), 5));
		addCentrifugeRecipe(new FluidStack(CoreInit.Deuterium.get(), 5), 10, new FluidStack(CoreInit.Tritium.get(), 5));
	}

	private static final ItemStack COIL_EMPTY = new ItemStack(CoreInit.emptyWireCoil);
	private static final Map<ItemStackChecker, ItemStackChecker> crusherRecipes = new HashMap<>();
	private static final Map<ItemStackChecker, ItemStackChecker> plateBlenderRecipes = new HashMap<>();
	private static final Map<ItemStackChecker, ItemStackChecker> wireMillRecipes = new HashMap<>();
	private static final Map<ItemStackChecker, ItemStackChecker> coilerPlantRecipes = new HashMap<>();
	private static final Map<ItemStackChecker, ItemStackChecker> alloySmelterRecipes = new HashMap<>();
	private static final Map<ItemStackChecker, ItemStackChecker> cokeOvenRecipes = new HashMap<>();
	private static final Map<ItemStackChecker, ItemStackChecker> blastFurnaceRecipes = new HashMap<>();
	private static final Map<ItemStackChecker, ItemStackChecker> fluidTransposerRecipes = new HashMap<>();
	private static final Map<ItemStackChecker, ItemStackChecker> laserEngraverRecipes = new HashMap<>();
	private static final Map<ItemStackChecker, ItemStackChecker> electrolyzerRecipes = new HashMap<>();
	private static final Map<ItemStackChecker, ItemStackChecker> centrifugeRecipes = new HashMap<>();
	private static final Map<IBlockState, Entry<Integer, List<ItemStackWRng>>> hammerRecipes = new HashMap<>();
	public static final List<ItemStack> blueprints = new ArrayList<>();

	public static void addCrusherRecipe(ItemStack input, ItemStack output) {
		ItemStackChecker c = new ItemStackChecker(input);
		boolean found = false;
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : crusherRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				found = true;
		}
		if (!found)
			crusherRecipes.put(new ItemStackChecker(input), new ItemStackChecker(output).setExtra(input.getCount()));
	}

	public static void addPlateBlenderRecipe(ItemStack input, ItemStack output, int tier) {
		ItemStackChecker c = new ItemStackChecker(input);
		boolean found = false;
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : plateBlenderRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				found = true;
		}
		if (!found)
			plateBlenderRecipes.put(new ItemStackChecker(input), new ItemStackChecker(output).setExtra(input.getCount()).setExtra2(tier));
	}

	public static void addWireMillRecipe(ItemStack input, ItemStack output, int tier) {
		ItemStackChecker c = new ItemStackChecker(input);
		boolean found = false;
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : wireMillRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				found = true;
		}
		if (!found)
			wireMillRecipes.put(new ItemStackChecker(input), new ItemStackChecker(output).setExtra(input.getCount()).setExtra2(tier));
	}

	public static void addCoilerPlantRecipe(ItemStack input, ItemStack output) {
		ItemStackChecker c = new ItemStackChecker(input);
		boolean found = false;
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : coilerPlantRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				found = true;
		}
		if (!found)
			coilerPlantRecipes.put(new ItemStackChecker(input), new ItemStackChecker(output).setExtra(input.getCount()).setExtra2(output.getCount()));
	}

	public static ItemStackChecker getCrusherOutput(ItemStack stack) {
		if (stack.isEmpty())
			return null;
		ItemStackChecker c = new ItemStackChecker(stack);
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : crusherRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				return recipe.getValue();
		}
		return null;
	}

	public static ItemStackChecker getPlateBlenderOutput(ItemStack stack, int lvl) {
		if (stack.isEmpty())
			return null;
		ItemStackChecker c = new ItemStackChecker(stack);
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : plateBlenderRecipes.entrySet()) {
			if (recipe.getKey().equals(c)) {
				if (lvl != -1 && recipe.getValue().extra2 > lvl)
					return null;
				else
					return recipe.getValue();
			}
		}
		return null;
	}

	public static ItemStackChecker getWireMillOutput(ItemStack stack, int lvl) {
		if (stack.isEmpty())
			return null;
		ItemStackChecker c = new ItemStackChecker(stack);
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : wireMillRecipes.entrySet()) {
			if (recipe.getKey().equals(c)) {
				if (lvl != -1 && recipe.getValue().extra2 > lvl)
					return null;
				else
					return recipe.getValue();
			}
		}
		return null;
	}

	public static ItemStackChecker getCoilerOutput(ItemStack stack) {
		if (stack.isEmpty())
			return null;
		ItemStackChecker c = new ItemStackChecker(stack);
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : coilerPlantRecipes.entrySet()) {
			if (recipe.getKey().equals(c)) { return recipe.getValue(); }
		}
		return null;
	}

	public static class ItemStackChecker {
		private final ItemStack stack, extraStack, extraStack2;
		private int extra, extra2, extra3, extra4, heat;
		private FluidStack fluid = null, fluid2 = null;
		private boolean mode = false;

		public ItemStackChecker() {
			this.stack = ItemStack.EMPTY;
			this.extraStack = ItemStack.EMPTY;
			this.extraStack2 = ItemStack.EMPTY;
		}

		public ItemStackChecker(ItemStack stack) {
			this.stack = stack;
			this.extraStack = ItemStack.EMPTY;
			this.extraStack2 = ItemStack.EMPTY;
		}

		public ItemStackChecker(ItemStack stack, ItemStack extra) {
			this.stack = stack;
			this.extraStack = extra;
			this.extraStack2 = ItemStack.EMPTY;
		}

		public ItemStackChecker(ItemStack stack, ItemStack extra, ItemStack extra2) {
			this.stack = stack;
			this.extraStack = extra;
			this.extraStack2 = extra2;
		}

		public ItemStack getStack() {
			return stack.copy();
		}

		public ItemStackChecker setExtra(int extra) {
			this.extra = extra;
			return this;
		}

		public ItemStackChecker setExtra2(int extra) {
			this.extra2 = extra;
			return this;
		}

		public ItemStackChecker setExtra3(int extra) {
			this.extra3 = extra;
			return this;
		}

		public ItemStackChecker setHeat(int extra) {
			this.heat = extra;
			return this;
		}

		public ItemStack getExtraStack() {
			return extraStack;
		}

		public ItemStack getExtraStack2() {
			return extraStack2;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof ItemStackChecker))
				return false;
			ItemStackChecker other = (ItemStackChecker) obj;
			return TomsModUtils.areItemStacksEqualOreDict(stack, other.stack, true, true, false, true) && mode == other.mode && heat <= other.heat && TomsModUtils.areFluidStacksEqual2(fluid, other.fluid) && other.stack.getCount() >= stack.getCount() && ((extraStack.isEmpty() && other.extraStack.isEmpty()) || TomsModUtils.areItemStacksEqualOreDict(extraStack, other.extraStack, true, true, false, true) && other.extraStack.getCount() >= extraStack.getCount()) && TomsModUtils.areFluidStacksEqual2(fluid2, other.fluid2) && ((extraStack2.isEmpty() && other.extraStack2.isEmpty()) || TomsModUtils.areItemStacksEqualOreDict(extraStack2, other.extraStack2, true, true, false, true) && other.extraStack2.getCount() >= extraStack2.getCount());
		}

		public int getExtra() {
			return extra;
		}

		public int getExtra2() {
			return extra2;
		}

		public int getExtra3() {
			return extra3;
		}

		public int getExtra4() {
			return extra4;
		}

		public FluidStack getExtraF() {
			return fluid;
		}

		public ItemStackChecker setExtraF(FluidStack extra) {
			this.fluid = extra;
			return this;
		}

		public FluidStack getExtraF2() {
			return fluid2;
		}

		public ItemStackChecker setExtraF2(FluidStack extra) {
			this.fluid2 = extra;
			return this;
		}

		public boolean getMode() {
			return mode;
		}

		public ItemStackChecker setMode(boolean mode) {
			this.mode = mode;
			return this;
		}

		public ItemStackChecker createSwappedExtra() {
			ItemStackChecker n = new ItemStackChecker(stack, extraStack, extraStack2);
			n.setExtra2(extra);
			n.setExtra(extra2);
			return n.setExtra3(extra3).setExtra4(extra4).setExtraF(fluid).setExtraF2(fluid2).setMode(mode).setHeat(heat);
		}

		public int getHeat() {
			return heat;
		}

		public static ItemStackChecker load(NBTTagCompound tag) {
			ItemStackChecker c = new ItemStackChecker(new ItemStack(tag.getCompoundTag("main")), new ItemStack(tag.getCompoundTag("extraS")), new ItemStack(tag.getCompoundTag("extraS2")));
			c.extra = tag.getInteger("extra");
			c.extra2 = tag.getInteger("extra2");
			c.extra3 = tag.getInteger("extra3");
			c.heat = tag.getInteger("heat");
			c.mode = tag.getBoolean("mode");
			c.extra4 = tag.getInteger("extra4");
			c.fluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid"));
			c.fluid2 = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid2"));
			return c;
		}

		public NBTTagCompound writeTo(NBTTagCompound tag) {
			tag.setTag("main", stack.writeToNBT(new NBTTagCompound()));
			if (!extraStack.isEmpty())
				tag.setTag("extraS", extraStack.writeToNBT(new NBTTagCompound()));
			if (!extraStack2.isEmpty())
				tag.setTag("extraS2", extraStack2.writeToNBT(new NBTTagCompound()));
			if (extra != 0)
				tag.setInteger("extra", extra);
			if (extra2 != 0)
				tag.setInteger("extra2", extra2);
			if (extra3 != 0)
				tag.setInteger("extra3", extra3);
			if (extra4 != 0)
				tag.setInteger("extra4", extra4);
			if (heat != 0)
				tag.setInteger("heat", heat);
			if (mode)
				tag.setBoolean("mode", mode);
			if (fluid != null)
				tag.setTag("fluid", fluid.writeToNBT(new NBTTagCompound()));
			if (fluid2 != null)
				tag.setTag("fluid2", fluid2.writeToNBT(new NBTTagCompound()));
			return tag;
		}

		public NBTTagCompound writeToNew() {
			return writeTo(new NBTTagCompound());
		}

		public ItemStackChecker setExtra4(int extra4) {
			this.extra4 = extra4;
			return this;
		}
	}

	public static List<RecipeData> getCrusherRecipes() {
		List<RecipeData> ret = new ArrayList<>();
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : crusherRecipes.entrySet()) {
			ret.add(new RecipeData(recipe.getKey().stack, recipe.getValue().stack));
		}
		return ret;
	}

	public static List<RecipeData> getPlateBlenderRecipes() {
		List<RecipeData> ret = new ArrayList<>();
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : plateBlenderRecipes.entrySet()) {
			ret.add(new RecipeData(recipe.getValue().extra2, recipe.getKey().stack, recipe.getValue().stack));
		}
		return ret;
	}

	public static List<RecipeData> getWireMillRecipes() {
		List<RecipeData> ret = new ArrayList<>();
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : wireMillRecipes.entrySet()) {
			ret.add(new RecipeData(recipe.getValue().extra2, recipe.getKey().stack, recipe.getValue().stack));
		}
		return ret;
	}

	public static List<RecipeData> getCoilerRecipes() {
		List<RecipeData> ret = new ArrayList<>();
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : coilerPlantRecipes.entrySet()) {
			ret.add(new RecipeData(recipe.getKey().stack, COIL_EMPTY, recipe.getValue().stack));
		}
		return ret;
	}

	public static ItemStack getFurnaceRecipe(ItemStack in) {
		if (in == null)
			return ItemStack.EMPTY;
		ItemStack stack = in.copy();
		stack.setCount(1);
		ItemStack s = FurnaceRecipes.instance().getSmeltingResult(stack);
		if (!s.isEmpty())
			s = s.copy();
		return s;
	}

	public static List<RecipeData> getAlloySmelterRecipes() {
		List<RecipeData> ret = new ArrayList<>();
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : alloySmelterRecipes.entrySet()) {
			ret.add(new RecipeData(recipe.getKey().stack, recipe.getKey().extraStack, recipe.getValue().stack));
		}
		return ret;
	}

	public static ItemStackChecker getAlloySmelterOutput(ItemStack stack1, ItemStack stack2) {
		if (stack1.isEmpty() || stack2.isEmpty())
			return null;
		ItemStackChecker c = new ItemStackChecker(stack1, stack2);
		ItemStackChecker cs = new ItemStackChecker(stack2, stack1);
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : alloySmelterRecipes.entrySet()) {
			if (recipe.getKey().equals(c)) {
				return recipe.getValue();
			} else if (recipe.getKey().equals(cs)) { return recipe.getValue().createSwappedExtra(); }
		}
		return null;
	}

	public static void addAlloySmelterRecipe(ItemStack input1, ItemStack input2, ItemStack output) {
		ItemStackChecker c = new ItemStackChecker(input1, input2);
		boolean found = false;
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : alloySmelterRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				found = true;
		}
		if (!found)
			alloySmelterRecipes.put(new ItemStackChecker(input1, input2), new ItemStackChecker(output).setExtra(input1.getCount()).setExtra2(input2.getCount()));
	}

	public static void addCokeOvenRecipe(ItemStack input1, ItemStack output, int creosote, int time) {
		ItemStackChecker c = new ItemStackChecker(input1).setExtra2(creosote);
		boolean found = false;
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : cokeOvenRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				found = true;
		}
		if (!found)
			cokeOvenRecipes.put(new ItemStackChecker(input1), new ItemStackChecker(output).setExtra(input1.getCount()).setExtra2(creosote).setExtra3(time));
	}

	public static ItemStackChecker getCokeOvenOutput(ItemStack stack) {
		if (stack.isEmpty())
			return null;
		ItemStackChecker c = new ItemStackChecker(stack);
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : cokeOvenRecipes.entrySet()) {
			if (recipe.getKey().equals(c)) { return recipe.getValue(); }
		}
		return null;
	}

	public static void addBlastFurnaceRecipe(ItemStack input1, ItemStack input2, ItemStack output, int time, int heat) {
		ItemStackChecker c = new ItemStackChecker(input1, input2).setHeat(heat);
		boolean found = false;
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : blastFurnaceRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				found = true;
		}
		if (!found)
			blastFurnaceRecipes.put(new ItemStackChecker(input1, input2).setHeat(heat), new ItemStackChecker(output).setExtra(input1 != null ? input1.getCount() : 0).setExtra2(input2 != null ? input2.getCount() : 0).setExtra3(time));
	}

	public static ItemStackChecker getBlastFurnaceOutput(ItemStack stack1, ItemStack stack2, int heat) {
		if (heat == 0 && stack1 == null)
			return null;
		if (heat > 0 && (stack1 == null && stack2 == null))
			return null;
		ItemStackChecker c = new ItemStackChecker(stack1, stack2).setHeat(heat);
		ItemStackChecker cs = new ItemStackChecker(stack2, stack1).setHeat(heat);
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : blastFurnaceRecipes.entrySet()) {
			if (recipe.getKey().equals(c)) {
				return recipe.getValue();
			} else if (recipe.getKey().equals(cs)) { return recipe.getValue().createSwappedExtra(); }
		}
		return null;
	}

	public static void addFluidTransposerRecipe(ItemStack input1, ItemStack output, FluidStack fluid, boolean isExtract) {
		ItemStackChecker c = new ItemStackChecker(input1);
		boolean found = false;
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : fluidTransposerRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				found = true;
		}
		if (!found)
			fluidTransposerRecipes.put(new ItemStackChecker(input1).setExtraF(fluid).setMode(isExtract), new ItemStackChecker(output).setExtra(fluid.amount));
	}

	public static ItemStackChecker getFluidTransposerOutput(ItemStack stack, FluidTank tank, boolean isExtract) {
		if (stack.isEmpty())
			return null;
		ItemStackChecker c = new ItemStackChecker(stack).setExtraF(tank.getFluid()).setMode(isExtract);
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : fluidTransposerRecipes.entrySet()) {
			if (recipe.getKey().equals(c)) { return recipe.getValue(); }
		}
		if (FluidUtil.getFluidHandler(stack.copy()) != null) {
			ItemStack container = stack.copy();
			IFluidHandlerItem h = FluidUtil.getFluidHandler(container);
			if (isExtract) {
				if (tank.getFluidAmount() != 10000) {
					if (tank.getFluid() != null) {
						FluidStack drained = h.drain(10000 - tank.getFluid().amount, false);
						if (drained != null && drained.amount > 0) {
							FluidStack stackOut = h.drain(10000 - tank.getFluid().amount, true);
							c = new ItemStackChecker(h.getContainer());
							c.setExtra(1);
							c.setExtraF(stackOut);
							return c;
						}
					} else {
						FluidStack drained = h.drain(10000, false);
						if (drained != null && drained.amount > 0) {
							FluidStack stackOut = h.drain(10000, true);
							c = new ItemStackChecker(h.getContainer());
							c.setExtraF(stackOut);
							c.setExtra(1);
							return c;
						}
					}
				}
			} else {
				int filled = h.fill(tank.getFluid(), false);
				if (filled > 0) {
					int stackOut = h.fill(tank.getFluid(), true);
					c = new ItemStackChecker(h.getContainer());
					c.setExtra(stackOut);
					return c;
				}
			}
		}
		return null;
	}

	public static List<RecipeData> getBlastFurnaceRecipes() {
		List<RecipeData> ret = new ArrayList<>();
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : blastFurnaceRecipes.entrySet()) {
			RecipeData d = new RecipeData(recipe.getKey().stack, recipe.getKey().extraStack, recipe.getValue().stack, recipe.getKey().getMode());
			d.processTime = recipe.getValue().getExtra3();
			d.energy = recipe.getKey().getHeat();
			ret.add(d);
		}
		return ret;
	}

	public static void addLaserEngraverRecipe(ItemStack silicon, ItemStack input, ItemStack blueprint, ItemStack output, int time, int minlvl) {
		ItemStackChecker c = new ItemStackChecker(silicon, input, blueprint);
		boolean found = false;
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : laserEngraverRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				found = true;
		}
		if (!found) {
			laserEngraverRecipes.put(new ItemStackChecker(silicon, input, blueprint), new ItemStackChecker(output).setExtra(silicon.getCount()).setExtra2(input.getCount()).setExtra3(time).setExtra4(minlvl));
			if (!blueprints.stream().anyMatch(blueprint::isItemEqual)) {
				blueprints.add(blueprint);
			}
		}
	}

	public static ItemStackChecker getLaserEngraverOutput(ItemStack silicon, ItemStack stack2, ItemStack blueprint) {
		if (silicon.isEmpty() && stack2.isEmpty() && blueprint.isEmpty())
			return null;
		ItemStackChecker c = new ItemStackChecker(silicon, stack2, blueprint);
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : laserEngraverRecipes.entrySet()) {
			if (recipe.getKey().equals(c)) { return recipe.getValue(); }
		}
		return null;
	}

	public static void addLaserEngraverRecipe(int silicon, ItemStack input, ItemStack blueprint, ItemStack output, int time, int minlvl) {
		addLaserEngraverRecipe(CraftingMaterial.SILICON_PLATE.getStackNormal(silicon), input, blueprint, output, time, minlvl);
	}

	public static void addHammerRecipe(IBlockState state, int lvl, ItemStack... stack) {
		addHammerRecipe(state, lvl, Arrays.stream(stack).filter(s -> s != null && !s.isEmpty()).map(ItemStackWRng::new).collect(Collectors.toList()));
	}

	public static void addHammerRecipe(IBlockState state, int lvl, ItemStackWRng... stack) {
		addHammerRecipe(state, lvl, Arrays.asList(stack));
	}

	public static void addHammerRecipe(IBlockState state, int lvl, List<ItemStackWRng> stack) {
		if (state != null && stack.size() > 0 && !hammerRecipes.containsKey(state)) {
			hammerRecipes.put(state, new EmptyEntry<>(lvl, stack.stream().filter(d -> d != null && !d.stack.isEmpty()).collect(Collectors.toList())));
		}
	}

	public static List<ItemStack> getHammerResult(int lvl, IBlockState state, Random rng) {
		Entry<Integer, List<ItemStackWRng>> v = hammerRecipes.get(state);
		return v != null ? v.getKey() <= lvl ? v.getValue().stream().filter(r -> r.check(rng)).map(ItemStackWRng::getStack).collect(Collectors.toList()) : null : null;
	}

	public static class ItemStackWRng {
		public ItemStack stack;
		public int chance;

		public boolean check(Random r) {
			return r.nextInt(100) <= chance;
		}

		public ItemStack getStack() {
			return stack.copy();
		}

		public ItemStackWRng(ItemStack stack, int chance) {
			this.stack = stack;
			this.chance = chance;
		}

		public ItemStackWRng(ItemStack stack) {
			this.stack = stack;
			this.chance = 100;
		}
	}

	public static void addElectrolyzerRecipe(FluidStack in, int energy, FluidStack out1, FluidStack out2) {
		ItemStackChecker c = new ItemStackChecker().setExtraF(in);
		boolean found = false;
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : electrolyzerRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				found = true;
		}
		if (!found)
			electrolyzerRecipes.put(new ItemStackChecker().setExtraF(in), new ItemStackChecker().setExtraF(out1).setExtraF2(out2).setExtra(energy).setExtra2(in.amount));
	}

	public static ItemStackChecker getElectrolyzerOutput(FluidStack in) {
		if (in != null)
			return null;
		ItemStackChecker c = new ItemStackChecker().setExtraF(in);
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : electrolyzerRecipes.entrySet()) {
			if (recipe.getKey().equals(c)) { return recipe.getValue(); }
		}
		return null;
	}

	public static void addCentrifugeRecipe(FluidStack in, int time, FluidStack out1) {
		ItemStackChecker c = new ItemStackChecker().setExtraF(in);
		boolean found = false;
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : centrifugeRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				found = true;
		}
		if (!found)
			centrifugeRecipes.put(new ItemStackChecker().setExtraF(in), new ItemStackChecker().setExtraF(out1).setExtra(time).setExtra2(in.amount).setMode(true));
	}

	public static void addCentrifugeRecipe(ItemStack in, int time, FluidStack out1) {
		ItemStackChecker c = new ItemStackChecker(in);
		boolean found = false;
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : centrifugeRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				found = true;
		}
		if (!found)
			centrifugeRecipes.put(new ItemStackChecker(in), new ItemStackChecker().setExtraF(out1).setExtra(time).setExtra2(in.getCount()));
	}

	public static void addCentrifugeRecipe(ItemStack in, int time, ItemStack out1, ItemStack out2, ItemStack out3, FluidStack out) {
		ItemStackChecker c = new ItemStackChecker(in);
		boolean found = false;
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : centrifugeRecipes.entrySet()) {
			if (recipe.getKey().equals(c))
				found = true;
		}
		if (!found)
			centrifugeRecipes.put(new ItemStackChecker(in), new ItemStackChecker(out1, out2, out3).setExtraF(out).setExtra(time).setExtra2(in.getCount()));
	}

	public static ItemStackChecker getCentrifugeOutput(ItemStack stack, FluidStack in) {
		if (in != null)
			return null;
		ItemStackChecker c = new ItemStackChecker().setExtraF(in);
		for (Entry<ItemStackChecker, ItemStackChecker> recipe : centrifugeRecipes.entrySet()) {
			if (recipe.getKey().equals(c)) { return recipe.getValue(); }
		}
		return null;
	}
}
