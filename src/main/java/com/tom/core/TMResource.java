package com.tom.core;

import static com.tom.api.recipes.RecipeHelper.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.tom.config.Config;
import com.tom.lib.utils.EmptyEntry;
import com.tom.recipes.OreDict;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackWRng;
import com.tom.util.ItemStackHelper;
import com.tom.util.TMLogger;
import com.tom.util.TomsModUtils;
import com.tom.worldgen.WorldGen.OreGenEntry;

import com.tom.core.block.MaterialSlab;

import com.tom.core.item.ItemCircuit;
import com.tom.core.item.ItemCircuit.CircuitType;
import com.tom.core.item.ItemCircuitComponent;
import com.tom.core.item.ItemCircuitComponent.CircuitComponentType;
import com.tom.core.item.ResourceItem;


public enum TMResource implements IStringSerializable {
	//@formatter:off
	// ENUM_NAME(    "registryName", isGem, isAlloy, canSmeltDust,  "oreOreDictName", crusherAmount, addSmetingFromOre, tool strength, tool durability, materialLevel, wiremill output, harvestLvl, hardness, resistance, crusherLvl),
	CHROME(                "chrome", false,   false,        false,       "oreChrome",             2,              true,             0,               0,             3,               0,          3,        6,         50,          3),
	TITANIUM(            "titanium", false,   false,        false,     "oreTitanium",             2,              true,             3,            1024,             3,               3,          3,        6,         50,          3),
	COPPER(                "copper", false,   false,         true,       "oreCopper",             2,              true,             1,             128,             1,               2,          0,        3,          5,          0),
	TIN(                      "tin", false,   false,         true,          "oreTin",             2,              true,             0,               0,             1,               2,          0,        3,          5,          0),
	NICKEL(                "nickel", false,   false,         true,       "oreNickel",             2,              true,             2,             256,             2,               3,          1,        3,          6,          0),
	PLATINUM(            "platinum", false,   false,         true,     "orePlatinum",             3,              true,             2,            2048,             2,               4,          2,        4,         10,          1),
	advAlloyMK1(      "advAlloyMK1", false,    true,        false,              null,             0,             false,             5,            1024,             4,               0,          0,        0,          0,          3),
	advAlloyMK2(      "advAlloyMK2", false,    true,        false,              null,             0,             false,             6,            2048,             5,               0,          0,        0,          0,          3),
	URANIUM(              "uranium", false,   false,        false,      "oreUranium",             4,             false,             0,               0,             0,               0,          2,        4,         10,          1),
	BLUE_METAL(         "blueMetal", false,   false,         true,    "oreBlueMetal",             2,              true,             2,             512,             2,               0,          2,        4,          6,          1),
	GREENIUM(            "greenium", false,    true,        false,              null,             0,             false,             0,               0,             3,               0,          0,        0,          0,          2),
	IRON(                    "iron", false,   false,         true,         "oreIron",             2,             false,             2,             128,             2,               2,          0,        0,          0,          0),
	GOLD(                    "gold", false,   false,         true,         "oreGold",             2,             false,             0,               0,             2,               2,          0,        0,          0,          1),
	RED_DIAMOND(       "redDiamond",  true,   false,        false,   "oreRedDiamond",             2,              true,             0,               0,             0,               0,          2,        5,         10,          2),
	SILVER(                "silver", false,   false,         true,       "oreSilver",             2,              true,             0,               0,             2,               3,          2,        4,         10,          1),
	LAPIS(                  "lapis",  true,   false,        false,        "oreLapis",             5,             false,             0,               0,             0,               0,          0,        0,          0,          0),
	LEAD(                    "lead", false,   false,         true,         "oreLead",             2,              true,             0,               0,             3,               0,          2,        4,          8,          0),
	OBSIDIAN(            "obsidian",  true,   false,        false,        "obsidian",             2,             false,             3,             128,             0,               0,          0,        0,          0,          1),
	NETHER_QUARTZ(         "quartz",  true,   false,        false,       "oreQuartz",             2,             false,             0,               0,             0,               0,          0,        0,          0,          1),
	BRASS(                  "brass", false,    true,         true,              null,             2,             false,             0,               0,             1,               0,          0,        0,          0,          0),
	REDSTONE(            "redstone", false,   false,        false,     "oreRedstone",             5,             false,             0,               0,             1,               3,          0,        0,          0,          0),
	DIAMOND(              "diamond",  true,   false,        false,      "oreDiamond",             2,             false,             3,            2048,             0,               0,          0,        0,          0,          1),
	GLOWSTONE(          "glowstone",  true,   false,        false,              null,             0,             false,             0,               0,             0,               0,          0,        0,          0,          0),
	SULFUR(                "sulfur",  true,   false,        false,       "oreSulfur",             3,             false,             0,               0,             0,               0,          0,        3,          5,          0),
	ZINC(                    "zinc", false,   false,         true,         "oreZinc",             2,              true,             0,               0,             1,               2,          0,        3,          5,          0),
	ENDERIUM(            "enderium", false,    true,        false,     "oreEnderium",             4,             false,             3,            1024,             2,               3,          3,        5,         16,          2),
	STEEL(                  "steel", false,    true,        false,              null,             0,             false,             3,             512,             2,               0,          0,        0,          0,          1),
	ELECTRUM(            "electrum", false,    true,         true,              null,             0,             false,             0,               0,             2,               3,          0,        0,          0,          1),
	BRONZE(                "bronze", false,    true,         true,              null,             0,             false,             2,             256,             1,               0,          0,        0,          0,          0),
	ALUMINUM(            "aluminum", false,   false,        false,      "oreBauxite",             4,             false,             2,             256,             1,               2,          2,        4,          8,          0),
	MERCURY(              "mercury",  true,   false,        false,      "oreMercury",             3,              true,             0,               0,             0,               0,          1,        3,          6,          0),
	COAL(                    "coal",  true,   false,        false,         "oreCoal",             2,             false,             0,              56,             0,               0,          0,        0,          0,          0),
	SKY_QUARTZ(         "skyQuartz",  true,   false,        false,    "oreSkyQuartz",             3,              true,             0,               0,             0,               0,          3,        8,         50,          3),
	FLUIX(                  "fluix",  true,    true,        false,              null,             0,             false,             0,               0,             0,               2,          0,        0,          0,          3),
	WOLFRAM(              "wolfram", false,   false,        false,    "oreTungstate",             3,             false,             4,            1248,             3,               3,          3,        6,         50,          2),
	TUNGSTENSTEEL(  "tungstensteel", false,    true,        false,              null,             0,             false,             5,            1520,             4,               0,          0,        0,          0,          2),
	//QUARTZ(                "quartz",  true,   false,        false, "oreQuartzNormal",             2,              true,             0,               0,             0,              0),
	//LITHIUM(              "lithium", false,   false,         true,      "oreLithium",             2,              true,             0,               0,             1,              2),
	//@formatter:on
	;
	private final String name;
	private final boolean isGem;
	private final boolean isAlloy, canSmeltDust, addSmeltingFromOre/*, hasSlurry*/;
	// private Fluid fluid, fluidC;
	private final String ore;
	protected Entry<Block, Integer> storageBlock;
	protected MaterialSlab slabBlock;
	private final int crusherAmount, toolStrength, durability, materialLevel, wireMillOutput, harvestLvl, crusherHardness;
	private final float hardness, resistance;
	// private BlockOre ore;
	public static final TMResource[] VALUES = values();

	private TMResource(String name, boolean isGem, boolean isAlloy, boolean canSmeltDust, String ore, int crushAmount, boolean addSmeltingFromOre, int toolStrength, int durability, int materialLevel, int wireMillOutput, int harvestLvl, float hardness, float resistance, int crusherHardness) {
		this.name = name;
		this.isGem = isGem;
		this.isAlloy = isAlloy;
		this.canSmeltDust = canSmeltDust;
		this.ore = ore;
		this.addSmeltingFromOre = addSmeltingFromOre;
		/*if(addOreSlurry){
			String oreDictName = getOreDictName();
			String sN = "tomsmodOreSlurry" + oreDictName;
			String sC = "tomsmodCleanOreSlurry" + oreDictName;
			this.fluid = new Fluid(sN, new ResourceLocation("tomsmodcore:blocks/ore_still"), new ResourceLocation("tomsmodcore:blocks/ore_flow"));
			this.fluidC = new Fluid(sC, new ResourceLocation("tomsmodcore:blocks/oreClean_still"), new ResourceLocation("tomsmodcore:blocks/oreClean_flow"));
		}
		hasSlurry = addOreSlurry;*/
		this.crusherAmount = crushAmount;
		this.toolStrength = toolStrength;
		this.materialLevel = materialLevel;
		this.durability = durability;
		this.wireMillOutput = wireMillOutput;
		this.hardness = hardness;
		this.harvestLvl = harvestLvl;
		this.resistance = resistance;
		this.crusherHardness = crusherHardness;
		// log.info(name);
	}

	public List<ItemStack> getStackOreDict(Type type) {
		return getStackNormal(type) != null ? getStackOreDict(type, 1) : new ArrayList<>();
	}

	public ItemStack getStackNormal(Type type) {
		return getStackNormal(type, 1);
	}

	public ItemStack getStackNormal(Type type, int a) {
		if (type == Type.INGOT) {
			if (this == IRON) {
				return new ItemStack(Items.IRON_INGOT, a);
			} else if (this == GOLD) { return new ItemStack(Items.GOLD_INGOT, a); }
		} else if (type == Type.NUGGET) {
			if (this == GOLD) {
				return new ItemStack(Items.GOLD_NUGGET, a);
			} else if (this == IRON) { return new ItemStack(Items.IRON_NUGGET, a); }
		} else if (type == Type.GEM) {
			if (this == DIAMOND) {
				return new ItemStack(Items.DIAMOND, a);
			} else if (this == LAPIS) {
				return new ItemStack(Items.DYE, a, EnumDyeColor.BLUE.getDyeDamage());
			} else if (this == NETHER_QUARTZ) {
				return new ItemStack(Items.QUARTZ, a);
			} else if (this == COAL) {
				return new ItemStack(Items.COAL, a);
			} else if (this == OBSIDIAN) { return new ItemStack(Blocks.OBSIDIAN, a); }
		} else if (type == Type.DUST) {
			if (this == REDSTONE) {
				return new ItemStack(Items.REDSTONE, a);
			} else if (this == GLOWSTONE) { return new ItemStack(Items.GLOWSTONE_DUST, a); }
		}
		return isValid(type) ? new ItemStack(type.item, a, ordinal()) : ItemStack.EMPTY;
	}

	public List<ItemStack> getStackOreDict(Type type, int a) {
		List<ItemStack> sList = OreDictionary.getOres(getOreDictName(type));
		List<ItemStack> ret = new ArrayList<>();
		if (this == ALUMINUM) {
			List<ItemStack> sList2 = OreDictionary.getOres(type.name + "Aluminium");
			for (ItemStack s : sList2) {
				if (!s.isEmpty()) {
					ItemStack c = s.copy();
					c.setCount(c.getCount() * a);
					ret.add(c);
				}
			}
		}
		for (ItemStack s : sList) {
			if (!s.isEmpty()) {
				ItemStack c = s.copy();
				c.setCount(c.getCount() * a);
				ret.add(c);
			}
		}
		return ret;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getLength() {
		return VALUES.length;
	}

	public boolean isVanila() {
		return this == IRON || this == GOLD || this == LAPIS || this == OBSIDIAN || this == NETHER_QUARTZ || this == DIAMOND ||
				this == GLOWSTONE || this == COAL || this == REDSTONE;
	}

	public boolean isGem() {
		return isGem;
	}

	public boolean isAlloy() {
		return isAlloy;
	}

	public boolean isValid(Type type) {
		switch (type) {
		case INGOT:
			return (!isVanila() && (!isGem())) || this == REDSTONE;
		case PLATE:
			return this != URANIUM && this != GLOWSTONE && this != COAL && this != SKY_QUARTZ && this != FLUIX && this != SULFUR &&
			this != MERCURY;
		case CABLE:
		case COIL:
			return (this != TUNGSTENSTEEL && this != URANIUM && this != BRONZE && this != advAlloyMK1 && this != advAlloyMK2 &&
			this != CHROME && this != STEEL && this != BLUE_METAL && this != GREENIUM && (!isGem()) && this != OBSIDIAN &&
			this != LEAD && this != TITANIUM && this != BRASS) || this == FLUIX;
		case NUGGET:
			return this != advAlloyMK1 && this != advAlloyMK2 && this != GOLD && this != IRON && (!isGem()) && this != OBSIDIAN &&
			this != REDSTONE;
		case DUST:
			return this != advAlloyMK1 && this != REDSTONE && this != advAlloyMK2 && this != GLOWSTONE && this != MERCURY &&
			this != STEEL;
		case GEM:
			return isGem && (!isVanila() || this == IRON || this == GOLD) && this != SULFUR;
		case DUST_TINY:
			return this != advAlloyMK1 && this != advAlloyMK2 && this != MERCURY && this != STEEL && this != BRONZE &&
			this != BRASS;
		case CRUSHED_ORE:
			return !isAlloy && this != OBSIDIAN && this != GLOWSTONE;
		case CRUSHED_ORE_NETHER:
			return !isAlloy && this != OBSIDIAN && this != GLOWSTONE && this != LEAD && this != NETHER_QUARTZ && this != MERCURY &&
			this != SKY_QUARTZ && this != WOLFRAM;
		case CRUSHED_ORE_END:
			return this == WOLFRAM || this == PLATINUM || this == GOLD || this == TITANIUM || this == DIAMOND || this == NICKEL ||
			this == ENDERIUM || this == SILVER;
		case RESEARCH_ITEM:
			return isResearchItem();
		default:
			return true;
		}
		//return type == Type.INGOT ? (!isVanila() && (!isGem())) || this == REDSTONE : (type == Type.PLATE ? this != URANIUM &&
		//		this != GLOWSTONE && this != COAL && this != SKY_QUARTZ && this != FLUIX && this != SULFUR && this != MERCURY :
		//			(type == Type.CABLE || type == Type.COIL ? (this != TUNGSTENSTEEL && this != URANIUM && this != BRONZE &&
		//			this != advAlloyMK1 && this != advAlloyMK2 && this != CHROME && this != STEEL && this != BLUE_METAL &&
		//			this != GREENIUM && (!isGem()) && this != OBSIDIAN && this != LEAD && this != TITANIUM && this != BRASS) ||
		//					this == FLUIX : (type == Type.NUGGET ? this != advAlloyMK1 && this != advAlloyMK2 && this != GOLD &&
		//					this != IRON && (!isGem()) && this != OBSIDIAN && this != REDSTONE : (type == Type.DUST ?
		//							this != advAlloyMK1 && this != REDSTONE && this != advAlloyMK2 && this != GLOWSTONE &&
		//							this != MERCURY && this != STEEL : (type == Type.GEM ? (isGem /*|| !isAlloy ||
		//							this == ENDERIUM*/) && (!isVanila() || this == IRON || this == GOLD) && this != SULFUR :
		//								(type == Type.DUST_TINY ? this != advAlloyMK1 && this != advAlloyMK2 && this != MERCURY &&
		//								this != STEEL && this != BRONZE && this != BRASS : (type == Type.CRUSHED_ORE ? !isAlloy &&
		//										this != OBSIDIAN && this != GLOWSTONE : (type == Type.CRUSHED_ORE_NETHER ? !isAlloy
		//												&& this != OBSIDIAN && this != GLOWSTONE/* && this != QUARTZ*/ &&
		//												this != LEAD && this != NETHER_QUARTZ && this != MERCURY &&
		//												this != SKY_QUARTZ && this != WOLFRAM : (type == Type.CRUSHED_ORE_END ?
		//														this == WOLFRAM || this == PLATINUM || this == GOLD ||
		//														this == TITANIUM || this == DIAMOND || this == NICKEL ||
		//														this == ENDERIUM || this == SILVER : (type == Type.RESEARCH_ITEM ?
		//																this.isResearchItem() : /*(type == Type.CLUMP ||
		//																type == Type.SHARD ? (!isAlloy && !isGem && this != REDSTONE &&
		//																this != OBSIDIAN) || this == MERCURY || this == ENDERIUM :*/
		//																	(true)))))))))));
	}

	public boolean isResearchItem() {
		return TomsModUtils.or(this == IRON, this == GOLD, this == TITANIUM, this == COPPER, this == TIN, this == NICKEL, this == PLATINUM, this == BLUE_METAL, this == GREENIUM, this == SILVER, this == REDSTONE, this == DIAMOND, this == NETHER_QUARTZ, this == RED_DIAMOND, this == ZINC, this == ELECTRUM, this == ENDERIUM, this == ALUMINUM, this == TUNGSTENSTEEL);
	}

	public String getOreDictName() {
		if (this != advAlloyMK1 && this != advAlloyMK2) {
			String f = this.name.substring(0, 1);
			f = f.toUpperCase();
			return f + this.name.substring(1);
		} else {
			return "AdvA" + (this == advAlloyMK1 ? "1" : "2");
		}
	}

	public String getOreDictName(Type type) {
		if (type == Type.GEM) {
			if (this == OBSIDIAN || this == COAL)
				return getOreDictName();
		}
		if (this == REDSTONE && (type == Type.PLATE || type == Type.CABLE || type == Type.COIL || type == Type.INGOT)) { return type.name + "RedAlloy"; }
		boolean isCrystal = !isAlloy && !isGem && (!isVanila() || this == IRON || this == GOLD) && this != SULFUR;
		return (type == Type.GEM && isCrystal ? "crystal" : type.name) + getOreDictName();
	}

	/*public BlockOre getOre(){
		return ore;
	}
	public void setOre(BlockOre ore){
		if(isPreInit && (!isAlloy)){
			this.ore = ore;
		}else{
			log.error("Somebody tries to corrupt the material registry!");
			throw new RuntimeException("Somebody tries to corrupt the material registry!");
		}
	}*/
	public static void loadOreDict() {
		if (CoreInit.isInit()) {
			CoreInit.log.info("Loading Material Ore Dictionary Entries");
			for (TMResource r : VALUES) {
				for (Type t : Type.VALUES) {
					if (r.isValid(t)) {
						String name = r.getOreDictName(t);
						// log.info("OreDict Name: "+name);
						OreDict.registerOre(name, r.getStackNormal(t));
					}
				}
				if (r.storageBlock != null) {
					OreDict.registerOre("block" + r.getOreDictName(), r.getBlockStackNormal());
				}
				if (r.slabBlock != null) {
					OreDict.registerOre("slab" + r.getOreDictName(), r.getSlabStackNormal());
				}
				if (r.toolStrength >= 0 && r.durability > 0) {
					int s = r.toolStrength;
					ItemStack stack = new ItemStack(CoreInit.hammer, 1, r.ordinal());
					OreDict.registerOre("itemHammer", stack);
					while (s >= 0) {
						OreDict.registerOre("itemHammer_lvl" + s, stack);
						s--;
					}
				}
			}
			CraftingMaterial.loadOreDict();
			for (Type t : Type.VALUES) {
				if (ALUMINUM.isValid(t))
					OreDict.registerOre(t.name + "Aluminium", ALUMINUM.getStackNormal(t));
			}
			OreDict.registerOre("blockAluminium", ALUMINUM.getBlockStackNormal(1));
			List<ItemStack> stacks = new ArrayList<>();
			addCuttersToList(stacks, CoreInit.wireCutters);
			for (ItemStack stack : stacks) {
				int s = VALUES[stack.getMetadata()].toolStrength;
				while (s >= 0) {
					OreDict.registerOre("itemCutter_lvl" + s, stack);
					s--;
				}
			}
			OreDict.registerOre(OBSIDIAN.getOreDictName(Type.GEM), OBSIDIAN.getStackNormal(Type.GEM));
		} else {
			CoreInit.log.fatal("Somebody tries to corrupt the material registry!");
			throw new RuntimeException("Somebody tries to corrupt the material registry!");
		}
	}

	protected static void loadRecipes() {
		if (CoreInit.isInit()) {
			CoreInit.log.info("Loading Material Crafting Recipes");
			/*for(Type t : Type.VALUES){
				for(TMResource r : VALUES){
					if(r.isValid(t)){
						String name = r.getOreDictName(t);
						log.info("OreDict Name: "+name);
						OreDictionary.registerOre(name, r.getStackNormal(t));
					}
				}
			}*/
			for (TMResource r : VALUES) {
				CoreInit.log.info("Loading Recipes for " + r.name);
				if (r.isValid(Type.INGOT) || (r == IRON || r == GOLD)) {
					if (r.canSmeltDust) {
						if (r.isValid(Type.DUST))
							addSmelting(r.getStackOreDict(Type.DUST), r.getStackNormal(Type.INGOT), 0.3F);
						if (r.isValid(Type.CRUSHED_ORE))
							addSmelting(r.getStackOreDict(Type.CRUSHED_ORE), r.getStackNormal(Type.INGOT), 0.5F);
						if (r.isValid(Type.CRUSHED_ORE_NETHER))
							addSmelting(r.getStackOreDict(Type.CRUSHED_ORE_NETHER), r.getStackNormal(Type.INGOT), 0.7F);
						if (r.isValid(Type.CRUSHED_ORE_END))
							addSmelting(r.getStackOreDict(Type.CRUSHED_ORE_END), r.getStackNormal(Type.INGOT), 0.7F);
					}
					if (r.isValid(Type.DUST)) {
						for (ItemStack stack : r.getStackOreDict(Type.INGOT))
							MachineCraftingHandler.genCrusherRecipeCh(stack, r.getStackNormal(Type.DUST, 1), r.crusherHardness);
					}
					if (r.addSmeltingFromOre && r.ore != null) {
						List<ItemStack> ores = OreDictionary.getOres(r.ore);
						ItemStack ingot = r.getStackNormal(Type.INGOT);
						addSmelting(ores, ingot, 0.4F);
					}
					if(Config.enableHammerOreMining){
						for(List<OreGenEntry> es : CoreInit.oreList.values()){
							for (OreGenEntry e : es) {
								if(e.name.equalsIgnoreCase(r.ore) && e.block.test(Blocks.STONE.getDefaultState())){
									float c = (e.veinSize * e.maxAmount * (e.ySize / 5f)) / 200f;
									ItemStack is = r.getStackNormal(Type.CRUSHED_ORE);
									MachineCraftingHandler.addHammerRecipe(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE), 2, new ItemStackWRng(is, c));
									MachineCraftingHandler.addHammerRecipe(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE), 2, new ItemStackWRng(is, c));
									MachineCraftingHandler.addHammerRecipe(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE), 2, new ItemStackWRng(is, c));
									MachineCraftingHandler.addHammerRecipe(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE), 2, new ItemStackWRng(is, c));
								}
								if(e.name == r.ore && e.block.test(Blocks.NETHERRACK.getDefaultState())){
									float c = (e.veinSize * e.maxAmount * (e.ySize / 5f)) / 20f;
									ItemStack is = r.getStackNormal(Type.CRUSHED_ORE_NETHER);
									MachineCraftingHandler.addHammerRecipe(Blocks.NETHERRACK.getDefaultState(), 2, new ItemStackWRng(is, c));
								}
								if(e.name == r.ore && e.block.test(Blocks.END_STONE.getDefaultState())){
									float c = (e.veinSize * e.maxAmount * (e.ySize / 5f)) / 20f;
									ItemStack is = r.getStackNormal(Type.CRUSHED_ORE_END);
									MachineCraftingHandler.addHammerRecipe(Blocks.END_STONE.getDefaultState(), 2, new ItemStackWRng(is, c));
								}
							}
						}
					}
					if (r.isValid(Type.NUGGET) || r == GOLD || r == IRON) {
						ItemStack ingot = r.getStackNormal(Type.INGOT);
						// List<ItemStack> nuggetL =
						// r.getStackOreDict(Type.NUGGET);
						if (ingot != null)
							addRecipe(ingot, new Object[]{"NNN", "NNN", "NNN", 'N', r.getOreDictName(Type.NUGGET)});
						// List<ItemStack> ingotL =
						// r.getStackOreDict(Type.INGOT);
						ItemStack nugget = r.getStackNormal(Type.NUGGET, 9);
						if (nugget != null)
							addShapelessRecipe(nugget, r.getOreDictName(Type.INGOT));
						if (r.isValid(Type.DUST_TINY)) {
							List<ItemStack> list = r.getStackOreDict(Type.DUST_TINY);
							addSmelting(list, r.getStackNormal(Type.NUGGET), 0.1F);
						}
					}
					if (!(r == IRON || r == GOLD || r == REDSTONE) && r.getBlockStackNormal(1) != null) {
						addRecipe(r.getBlockStackNormal(1), new Object[]{"III", "III", "III", 'I', r.getOreDictName(Type.INGOT)});
						addShapelessRecipe(r.getStackNormal(Type.INGOT, 9), new Object[]{r.getBlockOreDictName()});
					}
					if (r.materialLevel > 0) {
						boolean hasPlate = r.isValid(Type.PLATE);
						if (hasPlate && r.isValid(Type.INGOT)) {
							addPlateRecipe(r.getStackNormal(Type.PLATE), r.materialLevel, r.getOreDictName(Type.INGOT));
							MachineCraftingHandler.genPlateBenderRecipeCh(r.getStackNormal(Type.INGOT), r.getStackNormal(Type.PLATE), r.materialLevel);
							if (r.toolStrength > 0 && r.durability > 0) {
								addRecipe(new ItemStack(CoreInit.hammer, 1, r.ordinal()), new Object[]{"PPP", "PSP", "HS ", 'P', r.getOreDictName(Type.PLATE), 'H', "itemHammer_lvl" + r.materialLevel, 'S', Items.STICK});
							}
						}else if(hasPlate && r.isGem){

						}
						if (r.isValid(Type.CABLE) && r.wireMillOutput > 0) {
							MachineCraftingHandler.genWireMillRecipeCh(r.getStackNormal(Type.INGOT), r.getStackNormal(Type.CABLE, r.wireMillOutput), r.materialLevel);
							if (hasPlate) {
								MachineCraftingHandler.genWireMillRecipeCh(r.getStackNormal(Type.PLATE), r.getStackNormal(Type.CABLE, r.wireMillOutput + 1), r.materialLevel + 1);
								addRecipe(r.getStackNormal(Type.CABLE, 2), new Object[]{"CP", 'C', "itemCutter_lvl" + r.materialLevel, 'P', r.getStackName(Type.PLATE)});
							}
							if (r.isValid(Type.COIL)) {
								MachineCraftingHandler.addCoilerPlantRecipe(r.getStackNormal(Type.CABLE, 8), r.getStackNormal(Type.COIL));
							}
						}
						if (r.materialLevel == 1 && r.isValid(Type.DUST) && r.isValid(Type.INGOT)) {
							addShapelessRecipe(r.getStackNormal(Type.DUST), new Object[]{r.getOreDictName(Type.INGOT), "itemMortar"});
						}
						if (r.materialLevel == 1 && r.isValid(Type.DUST) && r.isValid(Type.GEM)) {
							addShapelessRecipe(r.getStackNormal(Type.DUST), new Object[]{r.getOreDictName(Type.GEM), "itemMortar"});
						}
					}
				}
				if (r.isGem) {
					if (!r.isVanila() && r.getBlockStackNormal(1) != null && r != REDSTONE && r != MERCURY && r != SULFUR) {
						addRecipe(r.getBlockStackNormal(1), new Object[]{"GGG", "GGG", "GGG", 'G', r.getOreDictName(Type.GEM)});
						addShapelessRecipe(r.getStackNormal(Type.GEM, 9), new Object[]{r.getBlockOreDictName()});
					}
					if (r.isValid(Type.DUST) && r != OBSIDIAN)
						for (ItemStack stack : r.getStackOreDict(Type.GEM))
							MachineCraftingHandler.genCrusherRecipeCh(stack, r.getStackNormal(Type.DUST, 1), r.crusherHardness);
				}
				if (r.isValid(Type.DUST_TINY)) {
					if (r.isValid(Type.DUST) || r == REDSTONE || r == GLOWSTONE) {
						ItemStack dust = r.getStackNormal(Type.DUST);
						if (dust != null)
							addRecipe(dust, new Object[]{"TTT", "TTT", "TTT", 'T', r.getOreDictName(Type.DUST_TINY)});
						ItemStack dustTiny = r.getStackNormal(Type.DUST_TINY, 9);
						if (dust != null && dustTiny != null)
							addShapelessRecipe(dustTiny, r.getOreDictName(Type.DUST));
					}
					if (r.isValid(Type.GEM) && !r.isGem) {
						List<ItemStack> stackL = r.getStackOreDict(Type.GEM);
						if (!stackL.isEmpty()) {
							for (ItemStack stack : stackL)
								MachineCraftingHandler.genCrusherRecipeCh(stack, r.getStackNormal(Type.DUST_TINY, 5), r.crusherHardness);
						}
					}
				}
				if (r.storageBlock != null && r.slabBlock != null) {
					addRecipe(r.getSlabStackNormal(6), new Object[]{"GGG", 'G', r.getBlockOreDictName()});
					addShapelessRecipe(r.getBlockStackNormal(), new Object[]{r.getSlabOreDictName(), r.getSlabOreDictName()});
				}
			}
			addHammerRecipe(CraftingMaterial.COPPER_HAMMER_HEAD.getStackNormal(), COPPER);
			{
				List<ItemStack> stackL = OreDictionary.getOres("oreAluminum");
				if (!stackL.isEmpty()) {
					for (ItemStack stack : stackL)
						MachineCraftingHandler.genCrusherRecipeCh(stack, ALUMINUM.getStackNormal(Type.CRUSHED_ORE, ALUMINUM.crusherAmount), 0);
				}
			}
			{
				List<ItemStack> stackL = OreDictionary.getOres("oreAluminium");
				if (!stackL.isEmpty()) {
					for (ItemStack stack : stackL)
						MachineCraftingHandler.genCrusherRecipeCh(stack, ALUMINUM.getStackNormal(Type.CRUSHED_ORE, ALUMINUM.crusherAmount), 0);
				}
			}
			MachineCraftingHandler.addCoilerPlantRecipe(REDSTONE.getStackNormal(Type.CABLE, 8), REDSTONE.getStackNormal(Type.COIL));
			MachineCraftingHandler.addCoilerPlantRecipe(FLUIX.getStackNormal(Type.CABLE, 8), FLUIX.getStackNormal(Type.COIL));
			addSmelting(CraftingMaterial.RAW_MERCURY.getStackOreDict(), MERCURY.getStackNormal(Type.GEM), 0.4F);
			List<ItemStack> cutters = new ArrayList<>();
			addCuttersToList(cutters, CoreInit.wireCutters);
			for (ItemStack s : cutters) {
				TMResource r = VALUES[s.getMetadata()];
				addRecipe(s, new Object[]{"PHP", " P ", "S S", 'P', r.getOreDictName(Type.PLATE), 'H', "itemHammer_lvl" + r.materialLevel, 'S', "rodIron"});
			}
			addRecipe(COAL.getStackNormal(Type.DUST), new Object[]{"MI", 'M', "itemMortar", 'I', COAL.getStackName(Type.GEM)});
			MachineCraftingHandler.genCrusherRecipeCh(new ItemStack(Items.COAL), TMResource.COAL.getStackNormal(Type.DUST), 0);
			addRecipe(MERCURY.getBlockStackNormal(1), new Object[]{"GGG", "GGG", "GGG", 'G', REDSTONE.getOreDictName(Type.INGOT)});
			addShapelessRecipe(REDSTONE.getStackNormal(Type.INGOT, 9), new Object[]{MERCURY.getBlockOreDictName()});
			addRecipe(SULFUR.getBlockStackNormal(1), new Object[]{"GGG", "GGG", "GGG", 'G', SULFUR.getOreDictName(Type.DUST)});
			addShapelessRecipe(SULFUR.getStackNormal(Type.DUST, 9), new Object[]{SULFUR.getBlockOreDictName()});
			MachineCraftingHandler.genCrusherRecipeCh(new ItemStack(Blocks.IRON_ORE), IRON.getStackNormal(Type.CRUSHED_ORE, IRON.crusherAmount), 0);
			MachineCraftingHandler.genCrusherRecipeCh(new ItemStack(Blocks.GOLD_ORE), GOLD.getStackNormal(Type.CRUSHED_ORE, GOLD.crusherAmount), 0);
			MachineCraftingHandler.genCrusherRecipeCh(new ItemStack(Blocks.LAPIS_ORE), LAPIS.getStackNormal(Type.CRUSHED_ORE, LAPIS.crusherAmount), 0);
			MachineCraftingHandler.genCrusherRecipeCh(new ItemStack(Blocks.REDSTONE_ORE), REDSTONE.getStackNormal(Type.CRUSHED_ORE, REDSTONE.crusherAmount), 0);
			MachineCraftingHandler.genCrusherRecipeCh(new ItemStack(Blocks.COAL_ORE), COAL.getStackNormal(Type.CRUSHED_ORE, COAL.crusherAmount), 0);
			MachineCraftingHandler.genCrusherRecipeCh(new ItemStack(Blocks.DIAMOND_ORE), DIAMOND.getStackNormal(Type.CRUSHED_ORE, DIAMOND.crusherAmount), 1);
			MachineCraftingHandler.genCrusherRecipeCh(new ItemStack(Blocks.QUARTZ_ORE), NETHER_QUARTZ.getStackNormal(Type.CRUSHED_ORE, NETHER_QUARTZ.crusherAmount), 1);
			MachineCraftingHandler.genCrusherRecipeCh(new ItemStack(CoreInit.oreEnderium), ENDERIUM.getStackNormal(Type.CRUSHED_ORE_END, 2), 2);
			MachineCraftingHandler.genCrusherRecipeCh(new ItemStack(CoreInit.oreSkyQuartz), SKY_QUARTZ.getStackNormal(Type.CRUSHED_ORE, 2), 2);
		} else {
			CoreInit.log.fatal("Somebody tries to corrupt the material registry!");
			throw new RuntimeException("Somebody tries to corrupt the material registry!");
		}
	}

	private static void addHammerRecipe(ItemStack stack, TMResource type) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("damage", MathHelper.floor(type.durability * 0.05D));
		addShapelessRecipe(ItemStackHelper.newItemStack(CoreInit.hammer, 1, type.ordinal(), tag), new Object[]{stack, Items.STICK, Items.STICK, Items.STICK, Items.FLINT});
	}

	/*protected static void loadFluids(boolean post){
		if(CoreInit.isInit()){
			if(post){
				CoreInit.log.info("Post Loading Fluids for materials.");
				for(TMResource r : VALUES){
					if(r.hasSlurry && r.fluid != null && r.fluidC != null){
						r.fluid = FluidRegistry.getFluid(r.fluid.getName());
						r.fluidC = FluidRegistry.getFluid(r.fluidC.getName());
					}
				}
			}else{
				for(TMResource r : VALUES){
					if(r.hasSlurry && r.fluid != null && r.fluidC != null){
						CoreInit.log.info("Loading Fluids for material " + r.name);
						CoreInit.fluids.add(r.fluid);
						CoreInit.fluids.add(r.fluidC);
					}
				}
			}
		}else{
			CoreInit.log.fatal("Somebody tries to corrupt the material registry!");
			throw new RuntimeException("Somebody tries to corrupt the material registry!");
		}
	}*/
	public void setBlock(Block block, int meta) {
		if (CoreInit.isInit()) {
			storageBlock = new EmptyEntry<>(block, meta);
		} else {
			CoreInit.log.fatal("Somebody tries to corrupt the material registry!");
			throw new RuntimeException("Somebody tries to corrupt the material registry!");
		}
	}

	public void setSlab(MaterialSlab block) {
		if (CoreInit.isInit()) {
			slabBlock = block;
		} else {
			CoreInit.log.fatal("Somebody tries to corrupt the material registry!");
			throw new RuntimeException("Somebody tries to corrupt the material registry!");
		}
	}

	public ItemStack getBlockStackNormal(int amount) {
		return storageBlock != null ? new ItemStack(storageBlock.getKey(), amount, storageBlock.getValue()) : ItemStack.EMPTY;
	}

	public ItemStack getBlockStackNormal() {
		return getBlockStackNormal(1);
	}

	public List<ItemStack> getBlockStackOreDict(int a) {
		List<ItemStack> sList = OreDictionary.getOres(getBlockOreDictName());
		List<ItemStack> ret = new ArrayList<>();
		if (this == ALUMINUM) {
			List<ItemStack> sList2 = OreDictionary.getOres("blockAluminium");
			for (ItemStack s : sList2) {
				if (!s.isEmpty()) {
					ItemStack c = s.copy();
					c.setCount(c.getCount() * a);
					ret.add(c);
				}
			}
		}
		for (ItemStack s : sList) {
			if (!s.isEmpty()) {
				ItemStack c = s.copy();
				c.setCount(c.getCount() * a);
				ret.add(c);
			}
		}
		return ret;
	}

	public String getBlockOreDictName() {
		return "block" + getOreDictName();
	}

	public static void addHammersToList(List<ItemStack> stack, Item hammerItem) {
		for (TMResource r : VALUES) {
			if (r.toolStrength >= 0 && r.durability > 0)
				stack.add(new ItemStack(hammerItem, 1, r.ordinal()));
		}
	}

	public static int getDurability(int damage) {
		/*if (Config.enableHardRecipes) {
			return MathHelper.ceil(get(damage).durability * 0.8D);
		} else*/
		return get(damage).durability;
	}

	public static TMResource get(int index) {
		return VALUES[MathHelper.abs(index % VALUES.length)];
	}

	public ItemStack getHammerStack(int a) {
		return new ItemStack(CoreInit.hammer, a, ordinal());
	}

	public static enum Type {
		PLATE("plate"), INGOT("ingot"), DUST("dust"), DUST_TINY("dustTiny"), CABLE("cable"), NUGGET("nugget"),
		GEM("gem"), COIL("coil"), CRUSHED_ORE("crushed"), CRUSHED_ORE_NETHER("crushedN"), CRUSHED_ORE_END("crushedE"),
		RESEARCH_ITEM("research"),
		// SHARD("shard"), CLUMP("clump"),
		;
		private ResourceItem item = null;

		private Type(String name) {
			this.name = name;
		}

		public static final Type[] VALUES = values();
		private String name;

		public String getName() {
			return name;
		}

		public Item getItem() {
			return item;
		}

		public void setItem(ResourceItem item) {
			if (CoreInit.isInit()) {
				this.item = item;
			} else {
				CoreInit.log.fatal("Somebody tries to corrupt the material registry!");
				throw new RuntimeException("Somebody tries to corrupt the material registry!");
			}
		}
	}
	public static Map<String, Integer> metaMap = new HashMap<>();
	public static enum CraftingMaterial {
		ACID_PAPER("acidP", false, null),
		CHARGED_REDSTONE("chargedRedstone", false, null),
		CHARGED_GLOWSTONE("chargedGlowstone", false, null),
		CHARGED_ENDER("chargedEnder", false, null),
		BIG_REDSTONE("bigRedstone", false, null),
		BIG_GLOWSTONE("bigGlowstone", false, null),
		BIG_ENDER_PEARL("bigEnderPearl", false, null),
		IRON_ROD("rodIron", true, null),
		BAUXITE_DUST("dustBauxite", true, null),
		CRUSHED_OBSIDIAN("crushedObsidian", false, null),
		HOT_COPPER("hotCopper", false, new CoolingHandler(COPPER.getStackNormal(Type.INGOT))),
		OBSIDIAN_ROD("rodObsidian", true, null),
		COPPER_HAMMER_HEAD("copperHammerHead", false, null),
		ELECTRICAL_STEEL_INGOT("ingotESteel", true, null),
		ROSIN("rosin", false, null),
		HOT_COPPER_HAMMER_HEAD("hotCopperHammerHead", false, new CoolingHandler(COPPER_HAMMER_HEAD.getStackNormal())),
		ELECTRICAL_STEEL_PLATE("plateESteel", true, null),
		DISPLAY("display", false, null),
		RAW_MERCURY("rawMercury", true, null),
		BASIC_CARD("basicCard", false, null),
		UPGRADE_FRAME("upgradeFrame", false, null),
		STONE_BOWL("stoneBowl", false, null),
		NETHERRACK_DUST("dustNetherrack", true, null),
		BOTTLE_OF_RESIN("bottleRubber", false, new LaterSpecifiedHandler()),
		REFINED_CLAY("refinedClay", false, null),
		REFINED_BRICK("brickRefined", true, null),
		FLINT_HAMMER_HEAD("flintHammerHead", false, null),
		BRONZE_PIPE("pipeBronze", true, null),
		SOLDERING_ALLOY("ingotSolderingAlloy", true, null),
		RUBBER("itemRubber", true, null),
		RAW_SILICON("rawSilicon", false, null),
		SILICON("itemSilicon", true, null),
		SILICON_PLATE("plateSilicon", true, null),
		GLASS_DUST("dustGlass", true, null),
		GLASS_FIBER("fiberGlass", true, null),
		GLASS_MESH("meshGlass", true, null),
		RAW_CICRUIT_BOARD("rawCircBoard", false, null),
		WOLFRAMIUM_GRINDER("grinderWolfram", "componentCrusher", null),
		TUNGSTATE_DUST("dustTungstate", false, null),
		HOT_WOLFRAM_INGOT("ingotHotTungsten", true, new CoolingHandler(WOLFRAM.getStackNormal(Type.INGOT))),
		HOT_TUNGSTENSTEEL_INGOT("ingotHotTungstensteel", true, new CoolingHandler(TUNGSTENSTEEL.getStackNormal(Type.INGOT))),
		ENDERIUM_BASE("enderiumBase", false, null),
		CUPRONICKEL_INGOT("ingotCupronickel", true, null),
		CUPRONICKEL_HEATING_COIL("heatingCoilCupronickel", false, null),
		TIN_TURBINE("tinTurbine", false, null),
		GENERATOR_COMPONENT("generator", false, null),
		SOLAR_PANEL_MK1("solar1", false, null),
		STEEL_PIPE("pipeSteel", true, null),
		ELECTRIC_MOTOR("electricMotor", false, null),
		PLASTIC_SHEET("sheetPlastic", true, null),
		BOTTLE_OF_CONCENTRATED_RESIN("bottleConcentratedResin", false, new LaterSpecifiedHandler()),
		COPPER_MESH("meshCopper", true, null),
		URANIUM235("ingotUranium235", true, null),
		URANIUM235_NUGGET("nuggetUranium235", true, null),
		VULCANIZING_AGENTS("vulcanizingAgents", false, null),
		STONE_DUST("dustStone", true, null),
		STEEL_ROD("rodSteel", true, null),
		PUMP("pump", false, null),
		BLUEPRINT_PAPER("blueprintPaper", false, null),
		BIG_BLUEPRINT_PAPER("blueprintPaperBig", false, null),
		IMPURE_FLUIX("gemFluixImpure", true, null),
		IMPURE_FLUIX_DUST("dustFluixImpure", true, null),
		RAW_ELECTRICAL_STEEL_DUST("rawESteelDust", false, null),
		HEATCONDUCTINGPASTE_CAN("heatPasteCan", false, null),
		LASER_MODULE("moduleLaser", true, null),
		RAW_CHALK("rawChalk", false, null),
		TIN_CAN("canTin", true, new LaterSpecifiedHandler()),
		UV_LAMP("uvLamp", false, null),
		PHOTOACTIVE_CAN("photoactiveCan", false, null),
		MIXED_METAL_INGOT("advAlloyBase", false, null),
		ELECTRON_TUBE("tubeElectron", true, null),
		NIXIE_TUBE("tubeNixie", true, null),
		BRONZE_GRINDER("grinderBronze", true, null),
		STEEL_GRINDER("grinderSteel", true, null),
		DIAMOND_GRINDER("grinderDiamond", true, null),
		REDSTONE_CRYSTAL("crystalRedstone", true, null),
		TIN_CASING("casingTin", true, null),
		STEEL_CASING("casingSteel", true, null),
		TITANIUM_CASING("casingTitanium", true, null),
		;
		public static final CraftingMaterial[] VALUES = values();
		private final String name, oreDictName;
		private final ItemHandler itemHandler;
		private int maxStackSize = 64, maxDamage = 0;
		private String info;
		public static boolean inited;

		private CraftingMaterial(String name, boolean hasOreDict, ItemHandler itemHandler) {
			this(name, hasOreDict ? name : null, itemHandler);
		}

		private CraftingMaterial(String name, String oreDictName, ItemHandler itemHandler) {
			this.name = name;
			this.itemHandler = itemHandler;
			this.oreDictName = oreDictName;
			metaMap.put(name, ordinal());
		}

		@Override
		public String toString() {
			return name;
		}

		public List<ItemStack> getStackOreDict() {
			return this.getStackOreDict(1);
		}

		public List<ItemStack> getStackOreDict(int amount) {
			if (oreDictName != null) {
				List<ItemStack> sList = OreDictionary.getOres(oreDictName);
				List<ItemStack> ret = new ArrayList<>();
				for (ItemStack s : sList) {
					if (s != null) {
						ItemStack c = s.copy();
						c.setCount(c.getCount() * amount);
						ret.add(c);
					}
				}
				return ret;
			} else
				return TomsModUtils.getItemStackList(getStackNormal(amount));
		}

		public String getName() {
			return name;
		}

		public ItemStack getStackNormal() {
			return getStackNormal(1);
		}

		public ItemStack getStackNormal(int amount) {
			return new ItemStack(CoreInit.craftingMaterial, amount, ordinal());
		}

		private static void loadOreDict() {
			if (CoreInit.isInit()) {
				CoreInit.log.info("Loading Crafting Material Ore Dictionary Entries");
				for (CraftingMaterial m : VALUES) {
					if (m.oreDictName != null) {
						OreDict.registerOre(m.oreDictName, m.getStackNormal());
					}
				}
			} else {
				CoreInit.log.fatal("Somebody tries to corrupt the material registry!");
				throw new RuntimeException("Somebody tries to corrupt the material registry!");
			}
		}

		public Object getStack(int amount) {
			return oreDictName != null ? oreDictName : getStackNormal(amount);
		}

		public Object getStack() {
			return getStack(1);
		}

		public static ItemStack tick(World world, BlockPos pos, EntityItem ent, ItemStack stack) {
			if (!world.isRemote && stack.getItemDamage() <= VALUES.length && stack.getItemDamage() >= 0) {
				CraftingMaterial m = VALUES[stack.getItemDamage()];
				if (m.itemHandler != null) { return m.itemHandler.apply(world, pos, ent, stack); }
			}
			return stack;
		}

		public static class ItemHandler {
			public ItemStack apply(World world, BlockPos pos, EntityItem item, ItemStack stack) {
				return stack;
			}

			public double getDurabilityBar(ItemStack stack) {
				return 0;
			}

			public boolean hasDurabilityBar(ItemStack stack) {
				return false;
			}

			public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
				return null;
			}

			public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
				return null;
			}
		}

		public static class CoolingHandler extends ItemHandler {
			private final ItemStack stack;

			public CoolingHandler(ItemStack stack) {
				this.stack = stack;
			}

			@Override
			public ItemStack apply(World world, BlockPos pos, EntityItem item, ItemStack stack) {
				Block block = world.getBlockState(pos).getBlock();
				if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
					world.setBlockToAir(pos);
					ItemStack s = this.stack.copy();
					s.setCount(s.getCount() * stack.getCount());
					return s;
				}
				return stack;
			}
		}

		public static class AcidingHandler extends ItemHandler {
			private final ItemStack stack;
			private final int time;

			public AcidingHandler(ItemStack stack, int time) {
				this.stack = stack;
				this.time = time;
			}

			@Override
			public ItemStack apply(World world, BlockPos pos, EntityItem item, ItemStack stack) {
				item.setNoDespawn();
				Block block = world.getBlockState(pos).getBlock();
				if (block == CoreInit.ironChloride.getBlock()) {
					if (!stack.hasTagCompound())
						stack.setTagCompound(new NBTTagCompound());
					if (stack.getTagCompound().getInteger("acidTimer") >= time) {
						ItemStack s = this.stack.copy();
						s.setCount(s.getCount() * stack.getCount());
						return s;
					} else {
						stack.getTagCompound().setInteger("acidTimer", stack.getTagCompound().getInteger("acidTimer") + 1);
					}
				}
				return stack;
			}

			@Override
			public double getDurabilityBar(ItemStack stack) {
				return stack.hasTagCompound() ? 1 - (stack.getTagCompound().getInteger("acidTimer") / ((double) time)) : 0;
			}

			@Override
			public boolean hasDurabilityBar(ItemStack stack) {
				return stack.hasTagCompound() ? stack.getTagCompound().getInteger("acidTimer") > 0 : false;
			}
		}

		public static class NoDespawnHandler extends ItemHandler {
			@Override
			public ItemStack apply(World world, BlockPos pos, EntityItem item, ItemStack stack) {
				item.setNoDespawn();
				return stack;
			}
		}

		public static class LaterSpecifiedHandler extends ItemHandler {
			private ItemHandler handler;

			@Override
			public ItemStack apply(World world, BlockPos pos, EntityItem item, ItemStack stack) {
				return handler != null ? handler.apply(world, pos, item, stack) : stack;
			}

			@Override
			public double getDurabilityBar(ItemStack stack) {
				return handler != null ? handler.getDurabilityBar(stack) : 0;
			}

			@Override
			public boolean hasDurabilityBar(ItemStack stack) {
				return handler != null ? handler.hasDurabilityBar(stack) : false;
			}

			protected void setHandler(ItemHandler h) {
				if (handler != null)
					throw new IllegalStateException("Handler already set for this instance!!");
				if (h == null)
					throw new IllegalArgumentException("NULL argument");
				this.handler = h;
			}

			@Override
			public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
				return handler != null ? handler.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ, hand) : null;
			}

			@Override
			public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
				return handler != null ? handler.initCapabilities(stack, nbt) : null;
			}
		}

		public static class FillingHandler extends ItemHandler {
			private final Map<FluidStack, ItemStack> fluidToItem;

			public FillingHandler(FluidStack toExtract, ItemStack filled) {
				this(ImmutableMap.<FluidStack, ItemStack>of(toExtract, filled));
			}

			public FillingHandler(Fluid toExtract, int amount, ItemStack filled) {
				this(ImmutableMap.<FluidStack, ItemStack>of(new FluidStack(toExtract, amount), filled));
			}

			public FillingHandler(Map<FluidStack, ItemStack> fluidToItem) {
				this.fluidToItem = ImmutableMap.<FluidStack, ItemStack>copyOf(fluidToItem);
			}

			@Override
			public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
				if (world.isRemote)
					return null;
				IFluidHandler h = FluidUtil.getFluidHandler(world, pos, side);
				if (h != null) {
					for (Entry<FluidStack, ItemStack> e : fluidToItem.entrySet()) {
						FluidStack drain = h.drain(new FluidStack(e.getKey(), e.getKey().amount), false);
						if (e.getKey().isFluidStackIdentical(drain)) {
							h.drain(drain, true);
							ItemStack s = e.getValue().copy();
							stack.shrink(1);
							player.setHeldItem(hand, stack);
							if (!player.inventory.addItemStackToInventory(s)) {
								InventoryHelper.spawnItemStack(world, player.posX, player.posY, player.posZ, s);
							}
							player.inventoryContainer.detectAndSendChanges();
							return EnumActionResult.SUCCESS;
						}
					}
				}
				return null;
			}

		}

		public static class CapabilityHandler extends ItemHandler {
			@SuppressWarnings("rawtypes")
			private final Map<Capability, BiFunction<ItemStack, NBTTagCompound, ?>> capMap;

			@SuppressWarnings("rawtypes")
			public CapabilityHandler(Map<Capability, BiFunction<ItemStack, NBTTagCompound, ?>> capMap) {
				this.capMap = capMap;
			}

			public <T> CapabilityHandler(Capability<T> cap, BiFunction<ItemStack, NBTTagCompound, T> v) {
				this(Collections.singletonMap(cap, v));
			}

			@Override
			public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
				return new CapabilityProvider(stack, nbt, capMap);
			}

			public static class CapabilityProvider implements ICapabilityProvider {
				@SuppressWarnings("rawtypes")
				private final Map<Capability, ?> capMap;

				@SuppressWarnings("rawtypes")
				public CapabilityProvider(ItemStack stack, NBTTagCompound nbt, Map<Capability, BiFunction<ItemStack, NBTTagCompound, ?>> capMap) {
					this.capMap = capMap.entrySet().stream().map(e -> Pair.<Capability, Object>of(e.getKey(), e.getValue().apply(stack, nbt))).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
				}

				@Override
				public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
					return capMap.containsKey(capability);
				}

				@SuppressWarnings("unchecked")
				@Override
				public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
					return (T) capMap.get(capability);
				}
			}
		}

		public int getMaxStackSize() {
			return maxStackSize;
		}

		public int getMaxDamage() {
			return maxDamage;
		}

		protected void setMaxStackSize(int maxStackSize) {
			this.maxStackSize = maxStackSize;
		}

		protected void setMaxDamage(int maxDamage) {
			this.maxDamage = maxDamage;
		}

		protected void setUpdateHandler(ItemHandler handler) {
			try {
				if (handler instanceof LaterSpecifiedHandler)
					throw new IllegalArgumentException("Handler cannot be a LaterSpecifiedHandler!!");
				((LaterSpecifiedHandler) itemHandler).setHandler(handler);
			} catch (Exception e) {
				CoreInit.log.fatal("Somebody tries to corrupt the material registry!");
				throw new RuntimeException("Somebody tries to corrupt the material registry!", e);
			}
		}

		protected void setInfo(String info) {
			this.info = info;
		}

		protected static void init() {
			if (CoreInit.isInit()) {
				CoreInit.log.info("Loading custom stack sizes for Crafting Materials.");
				ACID_PAPER.setMaxStackSize(16);
				HOT_COPPER.setMaxStackSize(16);
				HOT_COPPER_HAMMER_HEAD.setMaxStackSize(1);
				COPPER_HAMMER_HEAD.setMaxStackSize(16);
				UPGRADE_FRAME.setMaxStackSize(16);
				FLINT_HAMMER_HEAD.setMaxStackSize(16);
				WOLFRAMIUM_GRINDER.setMaxStackSize(1);
				HOT_WOLFRAM_INGOT.setMaxStackSize(16);
				HOT_TUNGSTENSTEEL_INGOT.setMaxStackSize(16);
				RAW_CHALK.setMaxStackSize(4);
				Map<FluidStack, ItemStack> fluidMap = new HashMap<>();
				fluidMap.put(new FluidStack(CoreInit.photoactiveLiquid.get(), 1000), CraftingMaterial.PHOTOACTIVE_CAN.getStackNormal());
				fluidMap.put(new FluidStack(CoreInit.heatConductingPaste.get(), 1000), CraftingMaterial.HEATCONDUCTINGPASTE_CAN.getStackNormal());
				TIN_CAN.setUpdateHandler(new FillingHandler(fluidMap));
				BOTTLE_OF_RESIN.setUpdateHandler(new CapabilityHandler(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (stack, nbt) -> new FluidHandlerItemStack.SwapEmpty(stack, new ItemStack(Items.GLASS_BOTTLE), 1000) {
					FluidStack fluid = new FluidStack(CoreInit.resin.get(), 1000);

					@Override
					public boolean canFillFluidType(FluidStack fluid) {
						return fluid.getFluid() == CoreInit.resin.get();
					}

					@Override
					public boolean canDrainFluidType(FluidStack fluid) {
						return fluid.getFluid() == CoreInit.resin.get();
					}

					@Override
					@Nullable
					public FluidStack getFluid() {
						return fluid;
					}

					@Override
					protected void setContainerToEmpty() {
						container = emptyContainer;
					}
				}));
				CRUSHED_OBSIDIAN.setInfo("tomsMod.tooltip.crushedObsidian");
				BOTTLE_OF_CONCENTRATED_RESIN.setUpdateHandler(new CapabilityHandler(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, (stack, nbt) -> new FluidHandlerItemStack.SwapEmpty(stack, new ItemStack(Items.GLASS_BOTTLE), 1000) {
					FluidStack fluid = new FluidStack(CoreInit.concentratedResin.get(), 1000);

					@Override
					public boolean canFillFluidType(FluidStack fluid) {
						return fluid.getFluid() == CoreInit.concentratedResin.get();
					}

					@Override
					public boolean canDrainFluidType(FluidStack fluid) {
						return fluid.getFluid() == CoreInit.concentratedResin.get();
					}

					@Override
					@Nullable
					public FluidStack getFluid() {
						return fluid;
					}

					@Override
					protected void setContainerToEmpty() {
						container = emptyContainer;
					}
				}));
			} else {
				CoreInit.log.fatal("Somebody tries to corrupt the material registry!");
				throw new RuntimeException("Somebody tries to corrupt the material registry!");
			}
		}

		public static CraftingMaterial get(int i) {
			return VALUES[MathHelper.abs(i % VALUES.length)];
		}

		public static boolean equals(Item item) {
			return item == CoreInit.craftingMaterial;
		}

		public boolean equals(ItemStack item) {
			return !item.isEmpty() && item.getItem() == CoreInit.craftingMaterial && item.getMetadata() == ordinal();
		}

		public boolean hasDurabilityBar(ItemStack stack) {
			return itemHandler != null ? itemHandler.hasDurabilityBar(stack) : false;
		}

		public double getDurabilityBar(ItemStack stack) {
			return itemHandler != null ? itemHandler.getDurabilityBar(stack) : 0;
		}

		public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
			EnumActionResult r = itemHandler != null ? itemHandler.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ, hand) : null;
			return r == null ? EnumActionResult.PASS : r;
		}

		@SideOnly(Side.CLIENT)
		public void getTooltip(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
			if (info != null) {
				String[] infoS = info.split("\\n");
				for (int i = 0;i < infoS.length;i++) {
					tooltip.add(I18n.format(infoS[i]));
				}
			}
		}

		public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
			return itemHandler != null ? itemHandler.initCapabilities(stack, nbt) : null;
		}
	}

	public String getStackName(Type type) {
		return getOreDictName(type);
	}

	public int getToolTier() {
		return toolStrength;
	}

	public static void addCuttersToList(List<ItemStack> subItems, Item itemIn) {
		subItems.add(new ItemStack(itemIn, 1, BRONZE.ordinal()));
		subItems.add(new ItemStack(itemIn, 1, IRON.ordinal()));
		subItems.add(new ItemStack(itemIn, 1, TITANIUM.ordinal()));
		subItems.add(new ItemStack(itemIn, 1, advAlloyMK1.ordinal()));
		subItems.add(new ItemStack(itemIn, 1, advAlloyMK2.ordinal()));
		subItems.add(new ItemStack(itemIn, 1, ALUMINUM.ordinal()));
		subItems.add(new ItemStack(itemIn, 1, STEEL.ordinal()));
		subItems.add(new ItemStack(itemIn, 1, ENDERIUM.ordinal()));
		subItems.add(new ItemStack(itemIn, 1, TUNGSTENSTEEL.ordinal()));
	}

	public int getMaterialLevel() {
		return materialLevel;
	}

	public IBlockState getSlab(SlabState state) {
		switch (state) {
		case BOTTOM:
			return slabBlock.half.getStateForType(this).withProperty(BlockSlab.HALF, EnumBlockHalf.BOTTOM);
		case FULL:
			return slabBlock.full.getStateForType(this);
		case TOP:
			return slabBlock.half.getStateForType(this).withProperty(BlockSlab.HALF, EnumBlockHalf.TOP);
		default:
			break;
		}
		return null;
	}

	public static enum SlabState {
		BOTTOM, TOP, FULL
	}

	public ItemStack getSlabStackNormal(int a) {
		return slabBlock.getStackForType(this, a);
	}

	public ItemStack getSlabStackNormal() {
		return getSlabStackNormal(1);
	}

	public String getSlabOreDictName() {
		return "slab" + getOreDictName();
	}

	public List<ItemStack> getSlabStackOreDict(int a) {
		List<ItemStack> sList = OreDictionary.getOres(getSlabOreDictName());
		List<ItemStack> ret = new ArrayList<>();
		if (this == ALUMINUM) {
			List<ItemStack> sList2 = OreDictionary.getOres("slabAluminium");
			for (ItemStack s : sList2) {
				if (!s.isEmpty()) {
					ItemStack c = s.copy();
					c.setCount(c.getCount() * a);
					ret.add(c);
				}
			}
		}
		for (ItemStack s : sList) {
			if (!s.isEmpty()) {
				ItemStack c = s.copy();
				c.setCount(c.getCount() * a);
				ret.add(c);
			}
		}
		return ret;
	}

	@SuppressWarnings("deprecation")
	public IBlockState getBlockState() {
		return storageBlock != null ? storageBlock.getKey().getStateFromMeta(storageBlock.getValue()) : Blocks.IRON_BLOCK.getDefaultState();
	}

	public int getHarvestLevel() {
		return harvestLvl;
	}

	public float getOreHardness() {
		return hardness;
	}

	public float getOreResistance() {
		return resistance;
	}

	/*public static void main(String[] args) {//Plate JSON Generator
		new File(".", "plates").mkdirs();
		for (int i = 0; i < VALUES.length; i++) {
			TMResource tmResource = VALUES[i];
			try{
				PrintWriter w = new PrintWriter(new File(".", "plates/plate_"+tmResource.name.toLowerCase()+".json"));
				w.println("{");
				w.println("  \"parent\": \"tomsmodcore:item/platebase\",");
				w.println("  \"textures\": {");
				w.println("    \"block\": \"tomsmodcore:blocks/resources/block"+tmResource.name.toLowerCase()+"\"");
				w.println("  }");
				w.println("}");
				w.close();
			}catch(Exception e){}
		}
	}*/
	/*public static void main(String[] args) {//Nether & End Ore JSON Generator
		new File(".", "ores").mkdirs();
		new File(".", "ores/item").mkdirs();
		for (int i = 0; i < VALUES.length; i++) {
			TMResource tmResource = VALUES[i];
			try{
				if(tmResource.isValid(Type.CRUSHED_ORE))write("ore!", tmResource.name.toLowerCase());
				if(tmResource.isValid(Type.CRUSHED_ORE_NETHER))write("ore!nether", tmResource.name.toLowerCase());
				if(tmResource.isValid(Type.CRUSHED_ORE_END))write("ore!end", tmResource.name.toLowerCase());
			}catch(Exception e){}
		}
	}
	private static void write(String loc, String name) throws IOException {
		PrintWriter w = new PrintWriter(new File(".", "ores/" + loc.replace("!", name)+".json"));
		w.println("{");
		w.println("  \"parent\": \"block/cube_all\",");
		w.println("  \"textures\": {");
		w.println("    \"all\": \"tomsmodcore:blocks/" + loc.replace("!", name)+"\"");
		w.println("  }");
		w.println("}");
		w.close();
		w = new PrintWriter(new File(".", "ores/item/"+loc.replace("!", (loc.endsWith("!") ? name : name + "."))+".json"));
		w.println("{");
		w.println("  \"parent\": \"tomsmodcore:block/"+loc.replace("!", name)+"\"");
		w.println("}");
		w.close();
	}*/
	/*public static void main(String[] args) {//Nether & End Ore Texture Generator
		new File(".", "ores/texture/base").mkdirs();
		new File(".", "ores/texture/out").mkdirs();
		BufferedImage stone, netherrack, endstone;
		try{
			stone = ImageIO.read(new File(".", "ores/texture/base/stone.png"));
			netherrack = ImageIO.read(new File(".", "ores/texture/base/netherrack.png"));
			endstone = ImageIO.read(new File(".", "ores/texture/base/end_stone.png"));
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		for (int i = 0; i < VALUES.length; i++) {
			TMResource tmResource = VALUES[i];
			try{
				if(tmResource.isValid(Type.CRUSHED_ORE)){
					write(stone, netherrack, endstone, tmResource.name.toLowerCase(), tmResource.isValid(Type.CRUSHED_ORE_NETHER), tmResource.isValid(Type.CRUSHED_ORE_END));
				}
			}catch(Exception e){
				System.out.println(tmResource.getOreDictName());
				e.printStackTrace();
			}
		}
		try{
			write(stone, netherrack, endstone, "bauxite", true, false);
		}catch(Exception e){
			System.out.println("aluminium");
			e.printStackTrace();
		}
	}
	private static void write(BufferedImage stone, BufferedImage netherrack, BufferedImage endstone, String name, boolean n, boolean e) throws IOException{
		BufferedImage img = null;
		try{
			img = ImageIO.read(new File(".", "ores/texture/base/ore" + name + ".png"));
		}catch(Exception ex){
			img = ImageIO.read(new File(".", "ores/texture/base/" + name + "_ore.png"));
		}
		if(n || e){
			BufferedImage base = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
			for(int x = 0;x<img.getWidth();x++)
				for(int y = 0;y<img.getHeight();y++){
					int rgb = img.getRGB(x, y);
					if(rgb != stone.getRGB(x, y)){
						base.setRGB(x, y, rgb);
					}
				}
			if(n){
				BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g = out.createGraphics();
				g.drawImage(netherrack, 0, 0, null);
				g.drawImage(base, 0, 0, null);
				g.dispose();
				ImageIO.write(out, "PNG", new File(".", "ores/texture/out/ore" + name + "nether.png"));
			}
			if(e){
				BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g = out.createGraphics();
				g.drawImage(endstone, 0, 0, null);
				g.drawImage(base, 0, 0, null);
				g.dispose();
				ImageIO.write(out, "PNG", new File(".", "ores/texture/out/ore" + name + "end.png"));
			}
		}
	}*/

	/*public static void main(String[] args) {//Formatter
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(".", "src/main/java/" + TMResource.class.getName().replace('.', '/') + ".java")));
			System.out.println("opened file");
			String sLine = reader.readLine();
			boolean reading = false;
			List<List<String>> lines = new ArrayList<>();
			while(sLine != null){
				String line = sLine.trim();
				if(line.startsWith("//@formatter:")){
					if(line.contains("off")){
						reading = true;
						sLine = reader.readLine();
						continue;
					}
					else break;
				}
				if(reading){
					List<String> list = new ArrayList<>();
					lines.add(list);
					String[] s1 = line.split("[(]", 2);
					if(s1[0].startsWith("//") && !s1[0].startsWith("// "))s1[0] = s1[0].substring(2).trim();
					String[] s2 = s1[1].split(",");
					list.add(s1[0]);
					Arrays.stream(s2).map(String::trim).forEach(list::add);
				}
				sLine = reader.readLine();
			}
			reader.close();
			System.out.println("closed file");
			Map<Integer, List<String>> cols = new HashMap<>();
			for (int i = 0;i < lines.size();i++) {
				List<String> l = lines.get(i);
				for (int j = 0;j < l.size();j++) {
					String s = l.get(j);
					if(!cols.containsKey(j)){
						cols.put(j, new ArrayList<>());
					}
					cols.get(j).add(s);
				}
			}
			cols.forEach((id, l) -> System.out.println(id + " " + l));
			List<Integer> colLength = cols.entrySet().stream().map(Entry::getValue).map(Collection::stream).map(s -> s.mapToInt(String::length).map(i -> i+1)).map(IntStream::max).map(o -> o.orElse(0)).collect(Collectors.toList());
			System.out.println(colLength);
			for (int line = 0;line < lines.size();line++) {
				List<String> l = lines.get(line);
				StringBuilder b = new StringBuilder();
				for (int col = 0;col < l.size();col++) {
					String s = l.get(col);
					int toFill = colLength.get(col) - s.length();
					String fill = String.join("", Collections.nCopies(toFill, " "));
					String v = col == 0 ? s + "(" + fill : fill + s;
					b.append(v);
					if(col > 0 && col < l.size()-1)b.append(',');
				}
				String ret = b.toString();
				System.out.println(ret + ",");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	public void registerOre(int i, ItemStack itemStack) {
		TMLogger.info("Registering ore: " + itemStack.getUnlocalizedName());
		switch (i) {
		case 0:
			OreDict.registerOre("ore" + getOreDictName(), itemStack);
			if (crusherAmount > 0 && isValid(Type.CRUSHED_ORE)) {
				List<ItemStack> stackL = OreDictionary.getOres("ore" + getOreDictName());
				if (!stackL.isEmpty()) {
					for (ItemStack stack : stackL) {
						MachineCraftingHandler.genCrusherRecipeCh(stack, getStackNormal(Type.CRUSHED_ORE, crusherAmount), crusherHardness);
					}
				} else {
					Config.logWarn("Material ore" + getOreDictName() + " disappeared from OreDictionary!");
				}
			}
			break;
		case 1:
			OreDict.registerOre("oreEnd" + getOreDictName(), itemStack);
			if (crusherAmount > 0 && isValid(Type.CRUSHED_ORE_END)) {
				List<ItemStack> stackL = OreDictionary.getOres("oreEnd" + getOreDictName());
				if (!stackL.isEmpty()) {
					if (addSmeltingFromOre)
						addSmelting(stackL, getStackNormal(Type.INGOT), 0.5F);
					for (ItemStack stack : stackL) {
						MachineCraftingHandler.genCrusherRecipeCh(stack, getStackNormal(Type.CRUSHED_ORE_END, crusherAmount), crusherHardness);
					}
				} else {
					Config.logWarn("Material oreEnd" + getOreDictName() + " disappeared from OreDictionary!");
				}
			}
			break;
		case -1:
			OreDict.registerOre("oreNether" + getOreDictName(), itemStack);
			if (crusherAmount > 0 && isValid(Type.CRUSHED_ORE_NETHER)) {
				List<ItemStack> stackL = OreDictionary.getOres("oreNether" + getOreDictName());
				if (!stackL.isEmpty()) {
					if (addSmeltingFromOre)
						addSmelting(stackL, getStackNormal(Type.INGOT), 0.5F);
					for (ItemStack stack : stackL) {
						MachineCraftingHandler.genCrusherRecipeCh(stack, getStackNormal(Type.CRUSHED_ORE_NETHER, crusherAmount), crusherHardness);
					}
				} else {
					Config.logWarn("Material oreNether" + getOreDictName() + " disappeared from OreDictionary!");
				}
			}
			break;
		default:
			break;
		}
	}

	public static void addPlateRecipe(ItemStack plate, int hammer, Object material) {
		if (Config.easyPlates)
			addShapelessRecipe(plate, new Object[]{"itemHammer_lvl" + hammer, material});
		else
			addRecipe(plate, new Object[]{"H", "I", "I", 'I', material, 'H', "itemHammer_lvl" + hammer});
	}
	private static abstract class condition_factory implements IConditionFactory {
		public condition_factory() {
			System.out.println("Constructing Condition Factory: " + getClass());
		}
	}
	private static abstract class ingredient_factory implements IIngredientFactory {
		public ingredient_factory() {
			System.out.println("Constructing Ingredient Factory: " + getClass());
		}
	}
	public static class tomsmod_circuit extends ingredient_factory {

		@Override
		public Ingredient parse(JsonContext context, JsonObject json) {
			String id = JsonUtils.getString(json, "id");
			CircuitType ct = ItemCircuit.circuitTypes.get(id);
			if(ct == null){
				throw new IllegalArgumentException("Invalid circuit id: " + id);
			}
			String type = JsonUtils.getString(json, "ctype");
			JsonElement countE = json.get("count");
			int count = countE != null ? countE.getAsInt() : 1;
			switch (type) {
			case "ua":
				return Ingredient.fromStacks(ct.createStack(CoreInit.circuitUnassembled, count));
			case "raw":
				return Ingredient.fromStacks(ct.createStack(CoreInit.circuitRaw, count));
			case "a":
				return Ingredient.fromStacks(ct.createCStack(CoreInit.circuit, count));
			default:
				throw new IllegalArgumentException("Invalid circuit type, (ua, raw, a) are vaild");
			}
		}
	}
	public static class tomsmod_circuitcomp extends ingredient_factory {

		@Override
		public Ingredient parse(JsonContext context, JsonObject json) {
			String id = JsonUtils.getString(json, "id");
			CircuitComponentType ct = ItemCircuitComponent.componentTypes.get(id);
			if(ct == null){
				throw new IllegalArgumentException("Invalid circuit component id " + id);
			}
			JsonElement countE = json.get("count");
			int count = countE != null ? countE.getAsInt() : 1;
			return Ingredient.fromStacks(ct.createStack(CoreInit.circuitComponent, count));
		}
	}
	public static class tomsmod_circuitpanel extends ingredient_factory {

		@Override
		public Ingredient parse(JsonContext context, JsonObject json) {
			String id = JsonUtils.getString(json, "id");
			Integer ct = ItemCircuit.panelNames.get(id);
			if(ct == null){
				throw new IllegalArgumentException("Invalid circuit id: " + id);
			}
			JsonElement phe = json.get("photoactive");
			boolean ph = phe == null ? false : phe.getAsBoolean();
			JsonElement countE = json.get("count");
			int count = countE != null ? countE.getAsInt() : 1;
			return Ingredient.fromStacks(new ItemStack(ph ? CoreInit.circuitPanelP : CoreInit.circuitPanel, count, ct));
		}
	}
	public static class tomsmod_material extends ingredient_factory {

		@Override
		public Ingredient parse(JsonContext context, JsonObject json) {
			String id = JsonUtils.getString(json, "id");
			Integer ct = metaMap.get(id);
			if(ct == null){
				throw new IllegalArgumentException("Invalid Material id: " + id);
			}
			JsonElement countE = json.get("count");
			int count = countE != null ? countE.getAsInt() : 1;
			return Ingredient.fromStacks(new ItemStack(CoreInit.craftingMaterial, count, ct));
		}
	}
	public static class tomsmod_chipset extends ingredient_factory {

		@Override
		public Ingredient parse(JsonContext context, JsonObject json) {
			int meta = json.get("meta").getAsInt();
			String type = JsonUtils.getString(json, "ctype");
			JsonElement countE = json.get("count");
			int count = countE != null ? countE.getAsInt() : 1;
			switch(type){
			case "base":
				return Ingredient.fromStacks(new ItemStack(CoreInit.chipsetBase, count, meta));
			case "chipset":
				return Ingredient.fromStacks(new ItemStack(CoreInit.chipset, count, meta));
			default:
				throw new IllegalArgumentException("Invalid Chipset Type: " + type);
			}
		}
	}
	public static class tomsmod_config extends condition_factory {

		@Override
		public BooleanSupplier parse(JsonContext context, JsonObject json) {
			String id = JsonUtils.getString(json, "id");
			return () -> Config.enabledBools.contains(id);
		}

	}
}