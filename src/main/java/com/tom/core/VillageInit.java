package com.tom.core;

import static com.tom.core.CoreInit.professionScientist;

import java.util.Random;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.gen.structure.MapGenStructureIO;

import net.minecraftforge.fml.common.registry.VillagerRegistry;

import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.lib.Configs;
import com.tom.worldgen.VillageHouseScientist;

public class VillageInit {
	public static void init() {
		if (CoreInit.isInit()) {
			CoreInit.log.info("Loading Villager Handler");
			VillagerRegistry.instance().registerVillageCreationHandler(new VillageHouseScientist.VillageManager());
			professionScientist = new VillagerRegistry.VillagerProfession(CoreInit.modid + ":vScientist", "tomsmod:textures/models/villager_scientist.png", "textures/entity/zombie_villager/zombie_librarian.png");
			VillagerRegistry.instance().register(professionScientist);
			MapGenStructureIO.registerStructureComponent(VillageHouseScientist.class, Configs.Modid + ":vh_scientist");
			VillagerRegistry.VillagerCareer career1 = new VillagerRegistry.VillagerCareer(professionScientist, Configs.Modid + ":vScientist1");
			career1.addTrade(1, new CircuitRecipe(CraftingMaterial.ELECTRIC_MOTOR, 0.25F, 3));
			career1.addTrade(2, new CircuitRecipe(CraftingMaterial.BASIC_CIRCUIT, 1), new CircuitRecipe(CraftingMaterial.NORMAL_CIRCUIT, 2), new CircuitRecipe(CraftingMaterial.ADVANCED_CIRCUIT, 5));
			career1.addTrade(3, new EliteBlueprintRecipe());
			career1.addTrade(4, new CircuitRecipe(CraftingMaterial.ELITE_CIRCUIT, 12));
			career1.addTrade(5, new AdvFluixReactorBlueprintRecipe());
			career1.addTrade(6, new CircuitRecipe(CraftingMaterial.QUANTUM_CIRCUIT, 35));
		} else {
			CoreInit.log.fatal("Somebody tries to corrupt the villager handler!");
			throw new RuntimeException("Somebody tries to corrupt the villager handler!");
		}
	}

	public static class EliteBlueprintRecipe implements EntityVillager.ITradeList {

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, random.nextInt(5) + 8), CraftingMaterial.BASIC_FLUIX_REACTOR_BLUEPRINT.getStackNormal(), CraftingMaterial.BLUEPRINT_ELITE_CIRCUIT.getStackNormal(), 0, 1));
		}

	}

	public static class CircuitRecipe implements EntityVillager.ITradeList {
		private final CraftingMaterial circuit;
		private final float emerald;
		private final int max;

		public CircuitRecipe(CraftingMaterial circuit, float emerald) {
			this(circuit, emerald, 16);
		}

		public CircuitRecipe(CraftingMaterial circuit, float emerald, int max) {
			this.circuit = circuit;
			this.emerald = emerald;
			this.max = max;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			recipeList.add(new MerchantRecipe(circuit.getStackNormal(random.nextInt(4) + 3), ItemStack.EMPTY, new ItemStack(Items.EMERALD, MathHelper.ceil((random.nextInt(2) + 1) * emerald)), 0, MathHelper.ceil(max / 4D)));
			recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, MathHelper.ceil((random.nextInt(2) + 1) * emerald)), ItemStack.EMPTY, circuit.getStackNormal(random.nextInt(2) + 1), 0, max));
		}
	}

	public static class AdvFluixReactorBlueprintRecipe implements EntityVillager.ITradeList {

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, random.nextInt(10) + 32), TMResource.PLATINUM.getStackNormal(Type.INGOT, 55 + random.nextInt(8)), CraftingMaterial.ADVANCED_FLUIX_REACTOR_BLUEPRINT.getStackNormal(), 0, 1));
		}

	}
}
