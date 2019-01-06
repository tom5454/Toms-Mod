package com.tom.core;

import static com.tom.core.CoreInit.professionScientist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.gen.structure.MapGenStructureIO;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.tom.api.recipes.RecipeHelper;
import com.tom.api.recipes.RecipeHelper.ItemStackWCount;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.core.TMResource.Type;
import com.tom.lib.Configs;
import com.tom.lib.utils.ReflectionUtils;
import com.tom.recipes.OreDict;
import com.tom.util.TomsModUtils;
import com.tom.worldgen.VillageHouseScientist;

import com.tom.core.item.ItemBlueprint;
import com.tom.core.item.ItemCircuit;

public class VillageHandler {
	private static VillagerRegistry.VillagerCareer career_scientist;
	private static List<Trade> trades = new ArrayList<>();
	@SuppressWarnings("unchecked")
	public static void init() {
		if (CoreInit.isInit()) {
			CoreInit.log.info("Loading Villager Handler");
			VillagerRegistry.instance().registerVillageCreationHandler(new VillageHouseScientist.VillageManager());
			professionScientist = new VillagerRegistry.VillagerProfession(CoreInit.modid + ":vScientist", "tomsmod:textures/models/villager_scientist.png", "textures/entity/zombie_villager/zombie_librarian.png");
			ForgeRegistries.VILLAGER_PROFESSIONS.register(professionScientist);
			MapGenStructureIO.registerStructureComponent(VillageHouseScientist.class, Configs.Modid + ":vh_scientist");
			career_scientist = new VillagerRegistry.VillagerCareer(professionScientist, Configs.Modid + ":vScientist1");
			if(RecipeHelper.genJson()){
				CoreInit.initRunnables.add(() -> {
					List<Object> trades = new ArrayList<>();
					addTrade(1, trades, new MaterialRecipe(CraftingMaterial.ELECTRIC_MOTOR, 0.25F, 3));
					addTrade(2, trades, new CircuitRecipe("basicecirc", 1), new CircuitRecipe("normalecirc", 2), new CircuitRecipe("advecirc", 5));
					addTrade(3, trades, new EliteBlueprintRecipe());
					addTrade(4, trades, new CircuitRecipe("eliteecirc", 12));
					addTrade(5, trades, new AdvFluixReactorBlueprintRecipe());
					//addTrade(6, new MaterialRecipe(CraftingMaterial.QUANTUM_CIRCUIT, 35));
					RecipeHelper.writeTrade(trades, "machine_recipes/scientist_trades.json");
				});
			}
			CoreInit.addReloadableTask(() -> {
				ReflectionUtils.trySetFinalField(VillagerCareer.class, List.class, career_scientist, new ArrayList<>(), CoreInit.log, "Couldn't clear trades list");
				load();
			});
		} else {
			CoreInit.log.fatal("Somebody tries to corrupt the villager handler!");
			throw new RuntimeException("Somebody tries to corrupt the villager handler!");
		}
	}

	@SuppressWarnings("unchecked")
	private static void addTrade(int lvl, List<Object> trades, Supplier<Map<String, Object>[]>... circuitRecipe) {
		Stream.of(circuitRecipe).map(Supplier::get).map(Stream::of).map(s -> s.map(m -> {m.put("level", lvl);return m;})).map(s -> s.collect(Collectors.toList())).forEach(trades::addAll);
	}

	private static void load(){
		trades.clear();
		JsonContext ctx = new JsonContext("tomsmodcore");
		TomsModUtils.parseJson("machine_recipes", "scientist_trades", TomsModUtils.gson, map -> {
			JsonArray trades = map.get("trades").getAsJsonArray();
			for (Iterator<JsonElement> iterator = trades.iterator();iterator.hasNext();) {
				try{
					JsonObject m = iterator.next().getAsJsonObject();
					int lvl = m.get("level").getAsInt();
					Trade trade = new Trade();
					loadStack(trade.buy1l, m, "in", ctx);
					loadStack(trade.buy2l, m, "in2", ctx);
					loadStack(trade.selll, m, "sell", ctx);
					int max = -1;
					if(m.has("max")){
						max = m.get("max").getAsInt();
						trade.max = max;
					}
					int rngM_in = -1, rngM_in2 = -1, rngM_out = -1;
					if(m.has("random_in_max")){
						rngM_in = m.get("random_in_max").getAsInt();
						trade.in_rng = rngM_in;
					}
					if(m.has("random_in2_max")){
						rngM_in2 = m.get("random_in2_max").getAsInt();
						trade.in2_rng = rngM_in2;
					}
					if(m.has("random_sell_max")){
						rngM_out = m.get("random_sell_max").getAsInt();
						trade.out_rng = rngM_out;
					}
					VillageHandler.trades.add(trade);
					final int fmax = max, frin = rngM_in, frin2 = rngM_in2, frout = rngM_out;
					if(trade.buy2l.isEmpty())trade.buy2l.add(ItemStack.EMPTY);
					for (Iterator<ItemStack> iterator2 = trade.buy1l.iterator();iterator2.hasNext();) {
						ItemStack fin = iterator2.next();
						for (Iterator<ItemStack> iterator3 = trade.buy2l.iterator();iterator3.hasNext();) {
							ItemStack fin2 = iterator3.next();
							for (Iterator<ItemStack> iterator4 = trade.selll.iterator();iterator4.hasNext();) {
								ItemStack fout = iterator4.next();
								career_scientist.addTrade(lvl, new ITradeList(){

									@Override
									public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
										int inc = fin.getCount();
										if(frin > 0){
											float d = (frin - inc) * random.nextFloat();
											inc = Math.round(inc + d);
										}
										int inc2 = fin2.getCount();
										if(frin2 > 0){
											float d = (frin2 - inc2) * random.nextFloat();
											inc2 = Math.round(inc2 + d);
										}
										int outc = fout.getCount();
										if(frout > 0){
											float d = (frout - outc) * random.nextFloat();
											outc = Math.round(outc + d);
										}
										ItemStack buy1 = fin.copy();
										ItemStack buy2 = fin2.copy();
										ItemStack sell = fout.copy();
										if(!buy1.isEmpty())buy1.setCount(inc);
										if(!buy2.isEmpty())buy2.setCount(inc2);
										if(!sell.isEmpty())sell.setCount(outc);
										recipeList.add(new MerchantRecipe(buy1, buy2, sell, 0, fmax < 0 ? 16 : fmax));
									}
								});
							}
						}
					}
				}catch(Exception e){
					CoreInit.log.error("Error parsing villager trade", e);
				}
			}
		});
	}

	private static void loadStack(List<ItemStack> list, JsonObject m, String name, JsonContext ctx){
		JsonElement e = m.get(name);
		if(e != null){
			if(e.isJsonObject()){
				JsonObject obj = e.getAsJsonObject();
				if(obj.has("nbt")){
					list.add(CraftingHelper.getItemStack(obj, ctx));
				}else{
					for(ItemStack i : CraftingHelper.getIngredient(obj, ctx).getMatchingStacks()){
						list.add(i);
					}
				}
			}else{
				String oid = m.get(name).getAsString();
				int c = m.get(name + "_count").getAsInt();
				OreDict.stream(oid, c).forEach(list::add);
			}
		}
	}

	public static class EliteBlueprintRecipe implements Supplier<Map<String, Object>[]> {

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, Object>[] get() {
			Map<String, Object> map = new HashMap<>();
			map.put("sell", new ItemStackWCount(ItemBlueprint.getBlueprintFor("elitecirctemplate")));
			map.put("in", new ItemStackWCount(Items.EMERALD, 8));
			map.put("in2", new ItemStackWCount(ItemBlueprint.getBlueprintFor("basicfluixreactor")));
			map.put("random_in_max", 16);
			map.put("max", 1);
			return new Map[]{map};
		}

	}

	public static class CircuitRecipe implements Supplier<Map<String, Object>[]> {
		private final String circuit;
		private final float emerald;
		private final int max;

		public CircuitRecipe(String circuit, float emerald) {
			this(circuit, emerald, 16);
		}

		public CircuitRecipe(String circuit, float emerald, int max) {
			this.circuit = circuit;
			this.emerald = emerald;
			this.max = max;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, Object>[] get() {
			Map<String, Object> map = new HashMap<>();
			map.put("in", ItemCircuit.serialize(circuit, "a", 3));
			map.put("random_in_max", 7);
			map.put("sell", new ItemStackWCount(Items.EMERALD, MathHelper.ceil(emerald)));
			map.put("random_sell_max", MathHelper.ceil(4 * emerald));
			map.put("max", max / 4);
			Map<String, Object> map2 = new HashMap<>();
			map2.put("sell", ItemCircuit.serialize(circuit, "a", 1));
			map2.put("random_sell_max", 3);
			map2.put("in", new ItemStackWCount(Items.EMERALD, MathHelper.ceil(emerald * 2)));
			map2.put("random_in_max", MathHelper.ceil(6 * emerald));
			map2.put("max", max);
			return new Map[]{map, map2};
		}
	}
	public static class MaterialRecipe implements Supplier<Map<String, Object>[]> {
		private final CraftingMaterial circuit;
		private final float emerald;
		private final int max;

		public MaterialRecipe(CraftingMaterial circuit, float emerald) {
			this(circuit, emerald, 16);
		}

		public MaterialRecipe(CraftingMaterial circuit, float emerald, int max) {
			this.circuit = circuit;
			this.emerald = emerald;
			this.max = max;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, Object>[] get() {
			Map<String, Object> map = new HashMap<>();
			map.put("in", new ItemStackWCount(circuit.getStackNormal(3)));
			map.put("random_in_max", 7);
			map.put("sell", new ItemStackWCount(Items.EMERALD, MathHelper.ceil(emerald)));
			map.put("random_sell_max", MathHelper.ceil(4 * emerald));
			map.put("max", max / 4);
			Map<String, Object> map2 = new HashMap<>();
			map2.put("sell", new ItemStackWCount(circuit.getStackNormal()));
			map2.put("random_sell_max", 3);
			map2.put("in", new ItemStackWCount(Items.EMERALD, MathHelper.ceil(emerald * 2)));
			map2.put("random_in_max", MathHelper.ceil(6 * emerald));
			map2.put("max", max);
			return new Map[]{map, map2};
		}
	}

	public static class AdvFluixReactorBlueprintRecipe implements Supplier<Map<String, Object>[]> {

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, Object>[] get() {
			Map<String, Object> map = new HashMap<>();
			map.put("sell", new ItemStackWCount(ItemBlueprint.getBlueprintFor("advfluixreactor")));
			map.put("in", new ItemStackWCount(Items.EMERALD, 32));
			map.put("random_in_max", 48);
			map.put("max", 1);
			map.put("in2", TMResource.PLATINUM.getStackName(Type.INGOT));
			map.put("in2_count", 55);
			map.put("random_in2_max", 64);
			return new Map[]{map};
		}

	}
	public static class Trade {
		public List<ItemStack> buy1l = new ArrayList<>();
		public List<ItemStack> buy2l = new ArrayList<>();
		public List<ItemStack> selll = new ArrayList<>();
		public int max = 16;
		public int in_rng = 0, in2_rng = 0, out_rng = 0;
	}
	public static List<Trade> getTrades() {
		return trades;
	}
}
