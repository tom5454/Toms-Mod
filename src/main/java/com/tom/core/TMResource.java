package com.tom.core;

import static com.tom.api.recipes.RecipeHelper.addRecipe;
import static com.tom.api.recipes.RecipeHelper.addShapelessRecipe;
import static com.tom.api.recipes.RecipeHelper.addSmelting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import com.tom.apis.EmptyEntry;
import com.tom.apis.Function.BiFunction;
import com.tom.apis.ItemStackHelper;
import com.tom.apis.TomsModUtils;
import com.tom.config.Config;
import com.tom.recipes.OreDict;
import com.tom.recipes.handler.MachineCraftingHandler;

import com.tom.core.item.ResourceItem;

public enum TMResource implements IStringSerializable{
	//ENUM_NAME(   "registryName", isGem, isAlloy, canSmeltDust,  "oreOreDictName", crusherAmount, addSmetingFromOre, addSlurry, tool strength, tool durability, materialLevel, wiremill output),
	CHROME(              "chrome", false,   false,        false,       "oreChrome",             2,              true,      true,             0,               0,             2,               0),
	TITANIUM(          "titanium", false,   false,        false,     "oreTitanium",             2,              true,      true,             3,            1024,             2,               3),
	COPPER(              "copper", false,   false,         true,       "oreCopper",             2,              true,      true,             1,             128,             1,               2),
	TIN(                    "tin", false,   false,         true,          "oreTin",             2,              true,      true,             1,             128,             1,               2),
	NICKEL(              "nickel", false,   false,         true,       "oreNickel",             2,              true,      true,             1,             256,             1,               3),
	PLATINUM(          "platinum", false,   false,         true,     "orePlatinum",             3,              true,      true,             2,            2048,             2,               4),
	advAlloyMK1(    "advAlloyMK1", false,    true,        false,              null,             0,             false,     false,             5,            1024,             4,               0),
	advAlloyMK2(    "advAlloyMK2", false,    true,        false,              null,             0,             false,     false,             6,            2048,             5,               0),
	URANIUM(            "uranium", false,   false,        false,      "oreUranium",             4,             false,      true,             0,               0,             0,               0),
	BLUE_METAL(       "blueMetal", false,   false,        false,    "oreBlueMetal",             2,              true,      true,             2,             512,             2,               0),
	GREENIUM(          "greenium", false,    true,        false,              null,             0,             false,     false,             0,               0,             3,               0),
	IRON(                  "iron", false,   false,         true,         "oreIron",             2,             false,      true,             1,             128,             1,               2),
	GOLD(                  "gold", false,   false,         true,         "oreGold",             2,             false,      true,             0,               0,             1,               2),
	RED_DIAMOND(     "redDiamond",  true,   false,        false,   "oreRedDiamond",             2,              true,     false,             0,               0,             0,               0),
	QUARTZ(              "quartz",  true,   false,        false, "oreQuartzNormal",             2,              true,     false,             0,               0,             0,               0),
	SILVER(              "silver", false,   false,         true,       "oreSilver",             2,              true,      true,             0,               0,             2,               3),
	LAPIS(                "lapis",  true,   false,        false,        "oreLapis",             5,             false,     false,             0,               0,             0,               0),
	LEAD(                  "lead", false,   false,         true,         "oreLead",             2,              true,      true,             0,               0,             3,               0),
	OBSIDIAN(          "obsidian",  true,   false,        false,        "obsidian",             2,             false,     false,             3,             128,             0,               0),
	NETHER_QUARTZ( "netherQuartz",  true,   false,        false,       "oreQuartz",             2,             false,     false,             0,               0,             0,               0),
	LITHIUM(            "lithium", false,   false,         true,      "oreLithium",             2,              true,      true,             0,               0,             1,               2),
	REDSTONE(          "redstone", false,   false,        false,     "oreRedstone",             5,             false,     false,             0,               0,             0,               3),
	DIAMOND(            "diamond",  true,   false,        false,      "oreDiamond",             2,             false,     false,             3,            2048,             0,               0),
	GLOWSTONE(        "glowstone",  true,   false,        false,              null,             0,             false,     false,             0,               0,             0,               0),
	SULFUR(              "sulfur",  true,   false,        false,       "oreSulfur",             3,             false,     false,             0,               0,             0,               0),
	ZINC(                  "zinc", false,   false,         true,         "oreZinc",             2,              true,      true,             0,               0,             1,               2),
	ENDERIUM(          "enderium", false,    true,        false,     "oreEnderium",             4,             false,      true,             3,            1024,             2,               3),
	STEEL(                "steel", false,    true,        false,              null,             0,             false,     false,             3,             512,             2,               0),
	ELECTRUM(          "electrum", false,    true,         true,              null,             0,             false,     false,             0,               0,             2,               3),
	BRONZE(              "bronze", false,    true,         true,              null,             0,             false,     false,             2,             256,             1,               0),
	ALUMINUM(          "aluminum", false,   false,         true,      "oreBauxite",             4,             false,      true,             2,             256,             1,               2),
	MERCURY(           "mercury",   true,   false,        false,      "oreMercury",             3,              true,      true,             0,               0,             0,               0),
	COAL(                 "coal",   true,   false,        false,         "oreCoal",             2,             false,     false,             0,              56,             0,               0),
	CERTUS_QUARTZ("certusQuartz",   true,   false,        false,       "oreCertus",             3,              true,     false,             0,               0,             0,               0),
	FLUIX(               "fluix",   true,    true,        false,              null,             0,             false,     false,             0,               0,             0,               2),
	WOLFRAM(           "wolfram",  false,   false,        false,    "oreTungstate",             3,             false,      true,             4,            1248,             3,               3),
	TUNGSTENSTEEL("tungstensteel", false,    true,        false,              null,             0,             false,     false,             5,            1520,             4,               0)
	;
	private final String name;
	private final boolean isGem;
	private final boolean isAlloy, canSmeltDust, addSmeltingFromOre, hasSlurry;
	private Fluid fluid, fluidC;
	private final String ore;
	protected Entry<Block, Integer> storageBlock;
	private final int crusherAmount, toolStrength, durability, materialLevel, wireMillOutput;
	//private BlockOre ore;
	public static final TMResource[] VALUES = values();

	public List<ItemStack> getStackOreDict(Type type) {
		return getStackNormal(type) != null ? getStackOreDict(type, 1) : new ArrayList<ItemStack>();
	}
	public ItemStack getStackNormal(Type type) {
		return getStackNormal(type, 1);
	}
	public ItemStack getStackNormal(Type type, int a) {
		if(type == Type.INGOT){
			if(this == IRON){
				return new ItemStack(Items.IRON_INGOT, a);
			}else if(this == GOLD){
				return new ItemStack(Items.GOLD_INGOT, a);
			}
		}else if(type == Type.NUGGET){
			if(this == GOLD){
				return new ItemStack(Items.GOLD_NUGGET, a);
			}
		}else if(type == Type.GEM){
			if(this == DIAMOND){
				return new ItemStack(Items.DIAMOND, a);
			}else if(this == LAPIS){
				return new ItemStack(Items.DYE, a, EnumDyeColor.BLUE.getDyeDamage());
			}else if(this == NETHER_QUARTZ){
				return new ItemStack(Items.QUARTZ, a);
			}else if(this == COAL){
				return new ItemStack(Items.COAL, a);
			}else if(this == OBSIDIAN){
				return new ItemStack(Blocks.OBSIDIAN, a);
			}
		}else if(type == Type.DUST){
			if(this == REDSTONE){
				return new ItemStack(Items.REDSTONE, a);
			}else if(this == GLOWSTONE){
				return new ItemStack(Items.GLOWSTONE_DUST, a);
			}
		}
		return isValid(type) ? new ItemStack(type.item,a,ordinal()) : null;
	}
	public List<ItemStack> getStackOreDict(Type type, int a) {
		List<ItemStack> sList = OreDictionary.getOres(getOreDictName(type));
		List<ItemStack> ret = new ArrayList<ItemStack>();
		if(this == ALUMINUM){
			List<ItemStack> sList2 = OreDictionary.getOres(type.name + "Aluminium");
			for(ItemStack s : sList2){
				if(s != null){
					ItemStack c = s.copy();
					c.stackSize = c.stackSize * a;
					ret.add(c);
				}
			}
		}
		for(ItemStack s : sList){
			if(s != null){
				ItemStack c = s.copy();
				c.stackSize = c.stackSize * a;
				ret.add(c);
			}
		}
		return ret;
	}
	private TMResource(String name, boolean isGem, boolean isAlloy, boolean canSmeltDust, String ore, int crushAmount, boolean addSmeltingFromOre, boolean addOreSlurry, int toolStrength, int durability, int materialLevel, int wireMillOutput){
		this.name = name;
		this.isGem = isGem;
		this.isAlloy = isAlloy;
		this.canSmeltDust = canSmeltDust;
		this.ore = ore;
		this.addSmeltingFromOre = addSmeltingFromOre;
		if(addOreSlurry){
			String oreDictName = getOreDictName();
			String sN = "tomsmodOreSlurry" + oreDictName;
			String sC = "tomsmodCleanOreSlurry" + oreDictName;
			this.fluid = new Fluid(sN, new ResourceLocation("tomsmodcore:blocks/ore_still"), new ResourceLocation("tomsmodcore:blocks/ore_flow"));
			this.fluidC = new Fluid(sC, new ResourceLocation("tomsmodcore:blocks/oreClean_still"), new ResourceLocation("tomsmodcore:blocks/oreClean_flow"));
		}
		hasSlurry = addOreSlurry;
		this.crusherAmount = crushAmount;
		this.toolStrength = toolStrength;
		this.materialLevel = materialLevel;
		this.durability = durability;
		this.wireMillOutput = wireMillOutput;
		//log.info(name);
	}
	@Override
	public String getName(){
		return name;
	}
	public int getLenth(){
		return VALUES.length;
	}
	public boolean isVanila(){
		return this == IRON || this == GOLD || this == LAPIS || this == OBSIDIAN ||
				this == NETHER_QUARTZ || this == DIAMOND || this == GLOWSTONE || this == COAL || this == REDSTONE;
	}
	public boolean isGem(){
		return isGem;
	}
	public boolean isAlloy(){
		return isAlloy;
	}
	public boolean isValid(Type type){
		return type == Type.INGOT ? (!isVanila() && (!isGem())) || this == REDSTONE : (type == Type.PLATE ? this != URANIUM && this != GLOWSTONE && this != COAL &&
				this != CERTUS_QUARTZ && this != FLUIX && this != SULFUR && this != MERCURY : (type == Type.CABLE || type == Type.COIL ?
						(this != TUNGSTENSTEEL && this != URANIUM && this != BRONZE && this != advAlloyMK1 && this != advAlloyMK2 && this != CHROME && this != STEEL &&
						this != BLUE_METAL && this != GREENIUM && (!isGem()) && this != OBSIDIAN && this != LEAD) || this == FLUIX :
							(type == Type.NUGGET ? this != advAlloyMK1 && this != advAlloyMK2 && this != GOLD && (!isGem()) && this != OBSIDIAN &&
							this != REDSTONE : (type == Type.DUST ? this != advAlloyMK1 && this != REDSTONE && this != advAlloyMK2 && this != GLOWSTONE &&
							this != MERCURY && this != STEEL : (type == Type.GEM ? (isGem || !isAlloy || this == ENDERIUM) && (!isVanila() || this == IRON || this == GOLD) && this != SULFUR :
								(type == Type.DUST_TINY ? this != advAlloyMK1 && this != advAlloyMK2 && this != MERCURY && this != STEEL && this != BRONZE :
									(type == Type.CRUSHED_ORE ? !isAlloy && this != OBSIDIAN && this != GLOWSTONE : (type == Type.CRUSHED_ORE_NETHER ? !isAlloy &&
											this != OBSIDIAN && this != GLOWSTONE && this != QUARTZ && this != LEAD && this != NETHER_QUARTZ && this != LITHIUM &&
											this != MERCURY && this != CERTUS_QUARTZ && this != WOLFRAM : (type == Type.CRUSHED_ORE_END ? this == WOLFRAM || this == PLATINUM || this == GOLD || this == TITANIUM || this == DIAMOND ||
											this == NICKEL || this == ENDERIUM : (type == Type.RESEARCH_ITEM ? this.isResearchItem() : (type == Type.CLUMP || type == Type.SHARD ?
													(!isAlloy && !isGem && this != REDSTONE && this != OBSIDIAN) || this == MERCURY || this == ENDERIUM : (true))))))))))));
	}
	public boolean isResearchItem(){
		return TomsModUtils.or(this == IRON,this == GOLD, this == TITANIUM, this == COPPER, this == TIN,
				this == NICKEL, this == PLATINUM, this == BLUE_METAL, this == GREENIUM, this == SILVER,
				this == LITHIUM, this == REDSTONE, this == DIAMOND, this == QUARTZ, this == NETHER_QUARTZ,
				this == RED_DIAMOND, this == ZINC, this == ELECTRUM, this == ENDERIUM, this == ALUMINUM,
				this == TUNGSTENSTEEL);
	}
	public String getOreDictName(){
		if(this != advAlloyMK1 && this != advAlloyMK2 && this != QUARTZ){
			String f = this.name.substring(0, 1);
			f = f.toUpperCase();
			return f + this.name.substring(1);
		}else if(this == QUARTZ){
			return "OuartzNormal";
		}else{
			return "AdvA" + (this == advAlloyMK1 ? "1" : "2");
		}
	}
	public String getOreDictName(Type type){
		if(type == Type.GEM){
			if(this == OBSIDIAN || this == COAL)return getOreDictName();
		}
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
	public static void loadOreDict(){
		if(CoreInit.isInit()){
			CoreInit.log.info("Loading Material Ore Dictionary Entries");
			for(TMResource r : VALUES){
				for(Type t : Type.VALUES){
					if(r.isValid(t)){
						String name = r.getOreDictName(t);
						//log.info("OreDict Name: "+name);
						OreDict.registerOre(name, r.getStackNormal(t));
					}
				}
				if(r.storageBlock != null){
					OreDict.registerOre("block"+r.getOreDictName(), r.getBlockStackNormal(1));
				}
				if(r.toolStrength >= 0 && r.durability > 0){
					int s = r.toolStrength;
					ItemStack stack = new ItemStack(CoreInit.hammer, 1, r.ordinal());
					OreDict.registerOre("itemHammer", stack);
					while(s >= 0){
						OreDict.registerOre("itemHammer_lvl"+s, stack);
						s--;
					}
				}
			}
			CraftingMaterial.loadOreDict();
			for(Type t : Type.VALUES){
				if(ALUMINUM.isValid(t))OreDict.registerOre(t.name + "Aluminium", ALUMINUM.getStackNormal(t));
			}
			OreDict.registerOre("blockAluminium", ALUMINUM.getBlockStackNormal(1));
			List<ItemStack> stacks = new ArrayList<ItemStack>();
			addCuttersToList(stacks, CoreInit.wireCutters);
			for(ItemStack stack : stacks){
				int s = VALUES[stack.getMetadata()].toolStrength;
				while(s >= 0){
					OreDict.registerOre("itemCutter_lvl"+s, stack);
					s--;
				}
			}
			OreDict.registerOre(OBSIDIAN.getOreDictName(Type.GEM), OBSIDIAN.getStackNormal(Type.GEM));
		}else{
			CoreInit.log.fatal("Somebody tries to corrupt the material registry!");
			throw new RuntimeException("Somebody tries to corrupt the material registry!");
		}
	}
	protected static void loadRecipes(){
		if(CoreInit.isInit()){
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
			for(TMResource r : VALUES){
				CoreInit.log.info("Loading Recipes for " + r.name);
				if(r.isValid(Type.INGOT) || (r == IRON || r == GOLD)){
					if(r.canSmeltDust){
						if(r.isValid(Type.DUST))addSmelting(r.getStackOreDict(Type.DUST), r.getStackNormal(Type.INGOT), 0.3F);
						if(r.isValid(Type.CRUSHED_ORE))addSmelting(r.getStackOreDict(Type.CRUSHED_ORE), r.getStackNormal(Type.INGOT), 0.5F);
						if(r.isValid(Type.CRUSHED_ORE_NETHER))addSmelting(r.getStackOreDict(Type.CRUSHED_ORE_NETHER), r.getStackNormal(Type.INGOT), 0.7F);
						if(r.isValid(Type.CRUSHED_ORE_END))addSmelting(r.getStackOreDict(Type.CRUSHED_ORE_END), r.getStackNormal(Type.INGOT), 0.7F);
					}
					if(r.isValid(Type.DUST)){
						for(ItemStack stack : r.getStackOreDict(Type.INGOT))MachineCraftingHandler.addCrusherRecipe(stack, r.getStackNormal(Type.DUST, 1));
					}
					if(r.addSmeltingFromOre && r.ore != null){
						List<ItemStack> ores = OreDictionary.getOres(r.ore);
						ItemStack ingot = r.getStackNormal(Type.INGOT);
						addSmelting(ores, ingot, 0.4F);
					}
					if(r.isValid(Type.NUGGET) || r == GOLD){
						ItemStack ingot = r.getStackNormal(Type.INGOT);
						//List<ItemStack> nuggetL = r.getStackOreDict(Type.NUGGET);
						if(ingot != null)addRecipe(ingot, new Object[]{"NNN","NNN","NNN",'N', r.getOreDictName(Type.NUGGET)});
						//List<ItemStack> ingotL = r.getStackOreDict(Type.INGOT);
						ItemStack nugget = r.getStackNormal(Type.NUGGET, 9);
						if(nugget != null)addShapelessRecipe(nugget, r.getOreDictName(Type.INGOT));
						if(r.isValid(Type.DUST_TINY)){
							List<ItemStack> list = r.getStackOreDict(Type.DUST_TINY);
							addSmelting(list, r.getStackNormal(Type.NUGGET), 0.1F);
						}
					}
					if(!(r == IRON || r == GOLD || r == REDSTONE) && r.getBlockStackNormal(1) != null){
						addRecipe(r.getBlockStackNormal(1), new Object[]{"III","III","III",'I', r.getOreDictName(Type.INGOT)});
						addShapelessRecipe(r.getStackNormal(Type.INGOT, 9), new Object[]{r.getBlockOreDictName()});
					}
					if(r.materialLevel > 0){
						boolean hasPlate = r.isValid(Type.PLATE);
						if(hasPlate){
							addRecipe(r.getStackNormal(Type.PLATE), new Object[]{"H","I","I",'I', r.getOreDictName(Type.INGOT), 'H', "itemHammer_lvl" + r.materialLevel});
							MachineCraftingHandler.addPlateBlenderRecipe(r.getStackNormal(Type.INGOT), r.getStackNormal(Type.PLATE), r.materialLevel);
							if(r.toolStrength > 0 && r.durability > 0){
								addRecipe(new ItemStack(CoreInit.hammer, 1, r.ordinal()), new Object[]{"PPP","HS "," S ",'P', r.getOreDictName(Type.PLATE), 'H', "itemHammer_lvl" + r.materialLevel, 'S', Items.STICK});
							}
						}
						if(r.isValid(Type.CABLE) && r.wireMillOutput > 0){
							MachineCraftingHandler.addWireMillRecipe(r.getStackNormal(Type.INGOT), r.getStackNormal(Type.CABLE, r.wireMillOutput), r.materialLevel);
							if(hasPlate){
								MachineCraftingHandler.addWireMillRecipe(r.getStackNormal(Type.PLATE), r.getStackNormal(Type.CABLE, r.wireMillOutput + 1), r.materialLevel + 1);
								addRecipe(r.getStackNormal(Type.CABLE, 2), new Object[]{"CP", 'C', "itemCutter_lvl" + r.materialLevel, 'P', r.getStackName(Type.PLATE)});
							}
							if(r.isValid(Type.COIL)){
								MachineCraftingHandler.addCoilerPlantRecipe(r.getStackNormal(Type.CABLE, 8), r.getStackNormal(Type.COIL));
							}
						}
						if(r.materialLevel == 1 && r.isValid(Type.DUST)){
							addShapelessRecipe(r.getStackNormal(Type.DUST), new Object[]{r.getOreDictName(Type.INGOT), "itemMortar"});
						}
					}
				}
				if(r.isGem){
					if(!r.isVanila() && r.getBlockStackNormal(1) != null && r != REDSTONE){
						addRecipe(r.getBlockStackNormal(1), new Object[]{"GGG","GGG","GGG",'G', r.getOreDictName(Type.GEM)});
						addShapelessRecipe(r.getStackNormal(Type.GEM, 9), new Object[]{r.getBlockOreDictName()});
					}
					if(r.isValid(Type.DUST))for(ItemStack stack : r.getStackOreDict(Type.GEM))MachineCraftingHandler.addCrusherRecipe(stack, r.getStackNormal(Type.DUST, 1));
				}
				if(r.isValid(Type.DUST_TINY)){
					if(r.isValid(Type.DUST) || r == REDSTONE || r == GLOWSTONE){
						ItemStack dust = r.getStackNormal(Type.DUST);
						if(dust != null)addRecipe(dust, new Object[]{"TTT","TTT","TTT",'T', r.getOreDictName(Type.DUST_TINY)});
						ItemStack dustTiny = r.getStackNormal(Type.DUST_TINY, 9);
						if(dust != null && dustTiny != null)addShapelessRecipe(dustTiny, r.getOreDictName(Type.DUST));
					}
					if(r.isValid(Type.SHARD)){
						List<ItemStack> stackL = r.getStackOreDict(Type.SHARD);
						if(!stackL.isEmpty()){
							for(ItemStack stack : stackL)MachineCraftingHandler.addCrusherRecipe(stack, r.getStackNormal(Type.DUST_TINY, 6));
						}
					}
					if(r.isValid(Type.CLUMP)){
						List<ItemStack> stackL = r.getStackOreDict(Type.CLUMP);
						if(!stackL.isEmpty()){
							for(ItemStack stack : stackL)MachineCraftingHandler.addCrusherRecipe(stack, r.getStackNormal(Type.DUST_TINY, 8));
						}
					}
					if(r.isValid(Type.GEM) && !r.isGem){
						List<ItemStack> stackL = r.getStackOreDict(Type.GEM);
						if(!stackL.isEmpty()){
							for(ItemStack stack : stackL)MachineCraftingHandler.addCrusherRecipe(stack, r.getStackNormal(Type.DUST_TINY, 5));
						}
					}
				}
				if(r.crusherAmount > 0 && r.isValid(Type.CRUSHED_ORE) && r.ore != null){
					List<ItemStack> stackL = OreDictionary.getOres(r.ore);
					if(!stackL.isEmpty()){
						for(ItemStack stack : stackL)MachineCraftingHandler.addCrusherRecipe(stack, r.getStackNormal(Type.CRUSHED_ORE, r.crusherAmount));
					}
				}
			}
			addHammerRecipe(CraftingMaterial.IRON_HAMMER_HEAD.getStackNormal(), IRON);
			addHammerRecipe(CraftingMaterial.COPPER_HAMMER_HEAD.getStackNormal(), COPPER);
			addHammerRecipe(CraftingMaterial.TIN_HAMMER_HEAD.getStackNormal(), TIN);
			{
				List<ItemStack> stackL = OreDictionary.getOres("oreAluminum");
				if(!stackL.isEmpty()){
					for(ItemStack stack : stackL)MachineCraftingHandler.addCrusherRecipe(stack, ALUMINUM.getStackNormal(Type.CRUSHED_ORE, ALUMINUM.crusherAmount));
				}
			}
			{
				List<ItemStack> stackL = OreDictionary.getOres("oreAluminium");
				if(!stackL.isEmpty()){
					for(ItemStack stack : stackL)MachineCraftingHandler.addCrusherRecipe(stack, ALUMINUM.getStackNormal(Type.CRUSHED_ORE, ALUMINUM.crusherAmount));
				}
			}
			MachineCraftingHandler.addCoilerPlantRecipe(REDSTONE.getStackNormal(Type.CABLE, 8), REDSTONE.getStackNormal(Type.COIL));
			MachineCraftingHandler.addCoilerPlantRecipe(FLUIX.getStackNormal(Type.CABLE, 8), FLUIX.getStackNormal(Type.COIL));
			addSmelting(CraftingMaterial.RAW_MERCURY.getStackOreDict(), MERCURY.getStackNormal(Type.GEM), 0.4F);
			List<ItemStack> cutters = new ArrayList<ItemStack>();
			addCuttersToList(cutters, CoreInit.wireCutters);
			for(ItemStack s : cutters){
				TMResource r = VALUES[s.getMetadata()];
				addRecipe(s, new Object[]{"PHP"," P ","S S",'P', r.getOreDictName(Type.PLATE), 'H', "itemHammer_lvl" + r.materialLevel, 'S', "rodIron"});
			}
			addRecipe(COAL.getStackNormal(Type.DUST), new Object[]{"MI", 'M', "itemMortar", 'I', COAL.getStackName(Type.GEM)});
			MachineCraftingHandler.addCrusherRecipe(new ItemStack(Items.COAL), TMResource.COAL.getStackNormal(Type.DUST));
		}else{
			CoreInit.log.fatal("Somebody tries to corrupt the material registry!");
			throw new RuntimeException("Somebody tries to corrupt the material registry!");
		}
	}
	private static void addHammerRecipe(ItemStack stack, TMResource type){
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("damage", MathHelper.floor_double(type.durability * 0.05D));
		addShapelessRecipe(ItemStackHelper.newItemStack(CoreInit.hammer, 1, type.ordinal(), tag), new Object[]{stack, Items.STICK, Items.STICK, Items.STICK, Items.FLINT});
	}
	protected static void loadFluids(boolean post){
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
	}
	public void setBlock(Block block, int meta){
		if(CoreInit.isInit()){
			storageBlock = new EmptyEntry<Block, Integer>(block, meta);
		}else{
			CoreInit.log.fatal("Somebody tries to corrupt the material registry!");
			throw new RuntimeException("Somebody tries to corrupt the material registry!");
		}
	}
	public ItemStack getBlockStackNormal(int amount){
		return storageBlock != null ? new ItemStack(storageBlock.getKey(), amount, storageBlock.getValue()) : null;
	}
	public List<ItemStack> getBlockStackOreDict(int a) {
		List<ItemStack> sList = OreDictionary.getOres(getBlockOreDictName());
		List<ItemStack> ret = new ArrayList<ItemStack>();
		if(this == ALUMINUM){
			List<ItemStack> sList2 = OreDictionary.getOres("blockAluminium");
			for(ItemStack s : sList2){
				if(s != null){
					ItemStack c = s.copy();
					c.stackSize = c.stackSize * a;
					ret.add(c);
				}
			}
		}
		for(ItemStack s : sList){
			if(s != null){
				ItemStack c = s.copy();
				c.stackSize = c.stackSize * a;
				ret.add(c);
			}
		}
		return ret;
	}

	public String getBlockOreDictName() {
		return "block" + getOreDictName();
	}

	public static void addHammersToList(List<ItemStack> stack, Item hammerItem){
		for(TMResource r : VALUES){
			if(r.toolStrength >= 0 && r.durability > 0)stack.add(new ItemStack(hammerItem, 1, r.ordinal()));
		}
	}

	public static int getDurability(int damage){
		if(Config.enableHardMode){
			return MathHelper.ceiling_double_int(get(damage).durability * 0.8D);
		}else
			return get(damage).durability;
	}

	public static TMResource get(int index){
		return VALUES[MathHelper.abs_int(index % VALUES.length)];
	}
	public ItemStack getHammerStack(int a){
		return new ItemStack(CoreInit.hammer, a, ordinal());
	}
	public static enum Type{
		PLATE("plate"),INGOT("ingot"),DUST("dust"),DUST_TINY("dustTiny"), CABLE("cable"),
		NUGGET("nugget"), GEM("gem"), COIL("coil"), CRUSHED_ORE("crushed"),
		CRUSHED_ORE_NETHER("crushedN"), CRUSHED_ORE_END("crushedE"), RESEARCH_ITEM("research"),
		SHARD("shard"), CLUMP("clump"),
		;
		private ResourceItem item = null;
		private Type(String name){
			this.name = name;
		}
		public static final Type[] VALUES = values();
		private String name;
		public String getName(){
			return name;
		}
		public Item getItem(){
			return item;
		}
		public void setItem(ResourceItem item){
			if(CoreInit.isInit()){
				this.item = item;
			}else{
				CoreInit.log.fatal("Somebody tries to corrupt the material registry!");
				throw new RuntimeException("Somebody tries to corrupt the material registry!");
			}
		}
	}
	public static enum CraftingMaterial{
		ACID_PAPER("acidP", false, null),
		CHARGED_REDSTONE("chargedRedstone", false, null),
		CHARGED_GLOWSTONE("chargedGlowstone", false, null),
		CHARGED_ENDER("chargedEnder", false, null),
		BIG_REDSTONE("bigRedstone", false, null),
		BIG_GLOWSTONE("bigGlowstone", false, null),
		BIG_ENDER_PEARL("bigEnderPearl", false, null),
		IRON_ROD("rodIron", true, null),
		BAUXITE_DUST("dustBauxite", true, null),
		HOT_IRON("hotIron", false, new CoolingHandler(IRON.getStackNormal(Type.INGOT))),
		HOT_COPPER("hotCopper", false, new CoolingHandler(COPPER.getStackNormal(Type.INGOT))),
		HOT_TIN("hotTin", false, new CoolingHandler(TIN.getStackNormal(Type.INGOT))),
		COPPER_HAMMER_HEAD("copperHammerHead", false, null),
		TIN_HAMMER_HEAD("tinHammerHead", false, null),
		IRON_HAMMER_HEAD("ironHammerHead", false, null),
		HOT_COPPER_HAMMER_HEAD("hotCopperHammerHead", false, new CoolingHandler(COPPER_HAMMER_HEAD.getStackNormal())),
		HOT_TIN_HAMMER_HEAD("hotTinHammerHead", false, new CoolingHandler(TIN_HAMMER_HEAD.getStackNormal())),
		HOT_IRON_HAMMER_HEAD("hotIronHammerHead", false, new CoolingHandler(IRON_HAMMER_HEAD.getStackNormal())),
		RAW_MERCURY("rawMercury", true, null),
		MERCURY_CRISTAL("crystalMercury", true, null),
		UPGRADE_FRAME("upgradeFrame", false, null),
		STONE_BOWL("stoneBowl", false, null),
		NETHERRACK_DUST("dustNetherrack", true, null),
		BOTTLE_OF_RUBBER("bottleRubber", false, null),
		REFINED_CLAY("refinedClay", false, null),
		REFINED_BRICK("brickRefined", true, null),
		FLINT_HAMMER_HEAD("flintHammerHead", false, null),
		BASIC_CIRCUIT("circuitBasic", true, null),
		NORMAL_CIRCUIT("circuitNormal", true, null),
		ADVANCED_CIRCUIT("circuitAdvanced", true, null),
		ELITE_CIRCUIT("circuitElite", true, null),
		BASIC_CIRCUIT_PLATE("basicCircuitPlate", false, null),
		ADVANCED_CIRCUIT_PLATE("advCircuitPlate", false, null),
		BASIC_CIRCUIT_COMPONENT("circuitComponentBasic", true, null),
		NORMAL_CIRCUIT_COMPONENT("circuitComponentNormal", true, null),
		ADVANCED_CIRCUIT_COMPONENT("circuitComponentAdvanced", true, null),
		ELITE_CIRCUIT_COMPONENT("circuitComponentElite", true, null),
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
		//DIAMOND_GRINDER("grinderDiamond", "componentCrusher", null),
		;
		public static final CraftingMaterial[] VALUES = values();
		private final String name, oreDictName;
		private final BiFunction<Entry<World, BlockPos>, Entry<EntityItem, ItemStack>, ItemStack> tickFunc;
		private CraftingMaterial(String name, boolean hasOreDict, BiFunction<Entry<World, BlockPos>, Entry<EntityItem, ItemStack>, ItemStack> worldTickF){
			this(name, hasOreDict ? name : null, worldTickF);
		}
		private CraftingMaterial(String name, String oreDictName, BiFunction<Entry<World, BlockPos>, Entry<EntityItem, ItemStack>, ItemStack> worldTickF){
			this.name = name;
			this.tickFunc = worldTickF;
			this.oreDictName = oreDictName;
		}
		@Override
		public String toString() {
			return name;
		}
		public List<ItemStack> getStackOreDict() {
			return this.getStackOreDict(1);
		}
		public List<ItemStack> getStackOreDict(int amount) {
			if(oreDictName != null){
				List<ItemStack> sList = OreDictionary.getOres(oreDictName);
				List<ItemStack> ret = new ArrayList<ItemStack>();
				for(ItemStack s : sList){
					if(s != null){
						ItemStack c = s.copy();
						c.stackSize = c.stackSize * amount;
						ret.add(c);
					}
				}
				return ret;
			}else return TomsModUtils.getItemStackList(getStackNormal(amount));
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
		private static void loadOreDict(){
			if(CoreInit.isInit()){
				CoreInit.log.info("Loading Crafting Material Ore Dictionary Entries");
				for(CraftingMaterial m : VALUES){
					if(m.oreDictName != null){
						OreDict.registerOre(m.oreDictName, m.getStackNormal());
					}
				}
			}else{
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
		public static ItemStack tick(World world, BlockPos pos, EntityItem ent, ItemStack stack){
			if(!world.isRemote && stack.getItemDamage() <= VALUES.length && stack.getItemDamage() >= 0){
				CraftingMaterial m = VALUES[stack.getItemDamage()];
				if(m.tickFunc != null){
					return m.tickFunc.apply(new EmptyEntry<World, BlockPos>(world, pos), new EmptyEntry<EntityItem, ItemStack>(ent, stack));
				}
			}
			return stack;
		}
		public static class CoolingHandler implements BiFunction<Entry<World, BlockPos>, Entry<EntityItem, ItemStack>, ItemStack>{
			private final ItemStack stack;
			public CoolingHandler(ItemStack stack) {
				this.stack = stack;
			}
			@Override
			public ItemStack apply(Entry<World, BlockPos> t, Entry<EntityItem, ItemStack> u) {
				Block block = t.getKey().getBlockState(t.getValue()).getBlock();
				if(block == Blocks.WATER || block == Blocks.FLOWING_WATER){
					t.getKey().setBlockToAir(t.getValue());
					ItemStack s = stack.copy();
					s.stackSize = s.stackSize * u.getValue().stackSize;
					return s;
				}
				return u.getValue();
			}

		}
	}
	/*@SideOnly(Side.CLIENT)
	public static class ResourceItemMesh implements ItemMeshDefinition{
		private Type type;
		public ResourceItemMesh(Type type){
			this.type = type;
		}
		@Override
		public ModelResourceLocation getModelLocation(ItemStack is) {
			return new ModelResourceLocation("tomsmodcore:resources/"+type.getName()+"_"+TMResource.VALUES[is.getMetadata()].getName(),"inventory");
		}

	}*/
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
}