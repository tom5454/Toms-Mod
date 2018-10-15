package com.tom.api.recipes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import com.tom.api.research.Research;
import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.recipes.ICustomJsonIngerdient;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.CraftingLevel;
import com.tom.util.CountingList;
import com.tom.util.DualOutputStream;
import com.tom.util.TomsModUtils;

public class RecipeHelper {
	public static class ItemStackWCount {
		private ItemStack stack;

		public ItemStackWCount(ItemStack stack) {
			this.stack = stack;
		}

		public ItemStackWCount(Item emerald, int i) {
			stack = new ItemStack(emerald, i);
		}

		public Item getItem() {
			return stack.getItem();
		}

		public int getCount() {
			return stack.getCount();
		}

		public int getMetadata() {
			return stack.getMetadata();
		}
	}
	public static List<Runnable> recipesToPatch = new ArrayList<>();
	public static final Logger log = LogManager.getLogger("Tom's Mod] [RecipePatcher");
	private static final String MOD_FOLDER = "assets/tomsmodcore";
	private static boolean scheduled = false, ran = false;
	private static int count;
	private static boolean genJson = false;
	private static File src, bin;
	private static CountingList<String> jsonNames;
	private static Gson gson;
	static {
		if(CoreInit.isDebugging){
			new JOptionPane();
			Thread t1 = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					JOptionPane.getRootFrame().dispose();

				}
			});
			t1.start();
			if(JOptionPane.showConfirmDialog(null, "Generate JSON recipes", "Recipe Generator", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
				File file = new File(".").getAbsoluteFile().getParentFile().getParentFile();
				genJson = true;
				src = new File(file, "src/main/resources/" + MOD_FOLDER);
				bin = new File(file, "bin/" + MOD_FOLDER);
				jsonNames = new CountingList<>();
				gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ItemStack.class, new JsonSerializer<ItemStack>() {

					@Override
					public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
						if(src.getItem() instanceof ICustomJsonIngerdient){
							return context.serialize(((ICustomJsonIngerdient)src.getItem()).serialize(src, false));
						}else{
							Map<String, Object> map = new HashMap<>();
							map.put("item", src.getItem().getRegistryName().toString());
							map.put("data", src.getMetadata());
							if(src.hasTagCompound())map.put("nbt", TomsModUtils.gson.fromJson(src.getTagCompound().toString(), Object.class));
							return context.serialize(map);
						}
					}
				}).registerTypeAdapter(Item.class, new JsonSerializer<Item>() {

					@Override
					public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
						Map<String, Object> map = new HashMap<>();
						map.put("item", src.getRegistryName().toString());
						return context.serialize(map);
					}
				}).registerTypeAdapter(ItemStackWCount.class, new JsonSerializer<ItemStackWCount>() {

					@Override
					public JsonElement serialize(ItemStackWCount src, Type typeOfSrc, JsonSerializationContext context) {
						/*if(src.getItem() instanceof ICustomJsonIngerdient){
							return context.serialize(((ICustomJsonIngerdient)src.getItem()).serialize(src.stack, true));
						}else{*/
						Map<String, Object> map = new HashMap<>();
						map.put("item", src.getItem().getRegistryName().toString());
						map.put("data", src.getMetadata());
						map.put("count", src.getCount());
						if(src.stack.hasTagCompound())map.put("nbt", TomsModUtils.gson.fromJson(src.stack.getTagCompound().toString(), Object.class));
						return context.serialize(map);
						//}
					}
				}).create();
			}
		}
	}

	public static void addOreDictSmelting(String name, ItemStack output, float xp) {
		List<ItemStack> cL = OreDictionary.getOres(name);
		if (cL != null && cL.size() > 0) {
			for (ItemStack c : cL) {
				GameRegistry.addSmelting(c, output, xp);
			}
		}
	}

	public static void addSmelting(List<ItemStack> input, ItemStack output, float xp) {
		if (input != null && input.size() > 0) {
			for (ItemStack c : input) {
				// CoreInit.log.info("Adding Recipe for " + c + " = " + output);
				if (c != null)
					GameRegistry.addSmelting(c, output, xp);
			}
		}
	}

	public static void addShapelessRecipe(ItemStack output, Object... inputs) {
		if(genJson){
			CoreInit.initRunnables.add(() -> {
				Map<String, Object> map = new HashMap<>();
				encodeShapeless(map, output, inputs);
				writeRecipe(map, output.getItem().delegate.name().getResourcePath());
			});
			//register(new ShapelessOreRecipe(new ResourceLocation("tomsmod:shapeless"), output, inputs));
		}
	}

	public static void addRecipe(ItemStack output, Object... inputs) {
		if(genJson){
			CoreInit.initRunnables.add(() -> {
				Map<String, Object> map = new HashMap<>();
				encodeShaped(map, output, inputs);
				String fName = writeRecipe(map, output.getItem().delegate.name().getResourcePath());
				String name = "tomsmodcore:" + fName;
				if(output.getItem() == TMResource.Type.PLATE.getItem()){
					map = new HashMap<>();
					map.put("parent", "minecraft:recipes/root");
					Map<String, Object> map2 = new HashMap<>();
					map.put("rewards", map2);
					List<Object> list = new ArrayList<>();
					map2.put("recipes", list);
					list.add(name);
					map2 = new HashMap<>();
					map.put("criteria", map2);
					Map<String, Object> map3 = new HashMap<>();
					map2.put("has_the_recipe", map3);
					map3.put("trigger", "minecraft:recipe_unlocked");
					Map<String, Object> map4 = new HashMap<>();
					map3.put("conditions", map4);
					map4.put("recipe", name);
					map3 = new HashMap<>();
					map2.put("has_the_item", map3);
					map3.put("trigger", "minecraft:inventory_changed");
					map4 = new HashMap<>();
					map3.put("conditions", map4);
					list = new ArrayList<>();
					map4.put("items", list);
					list.add(TMResource.get(output.getMetadata()).getStackNormal(TMResource.Type.INGOT));
					list = new ArrayList<>();
					map.put("requirements", list);
					List<Object> list2 = new ArrayList<>();
					list.add(list2);
					list2.add("has_the_recipe");
					list2.add("has_the_item");
					writeAdvancement(map, "recipes/resources", fName);
				}
			});
			//register(new ShapedOreRecipe(new ResourceLocation("tomsmod:shaped"), output, inputs));
		}
	}

	public static void removeAllRecipes(ItemStack stack) {
		/*List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		Iterator<IRecipe> itr = recipes.iterator();

		while (itr.hasNext()) {
			ItemStack is = itr.next().getRecipeOutput();
			if (ItemStack.areItemsEqual(stack, is))
				itr.remove();
		}*/
		//log.warn("Cannot removeAllRecipes for " + stack.getUnlocalizedName(), new Throwable());
	}

	public static void patchRecipe(ItemStack stack, boolean shaped, Supplier<Object[][]> recipe) {
		removeAllRecipes(stack);
		if (shaped)
			for (Object[] o : recipe.get())
				addRecipe(stack, o);
		else
			for (Object[] o : recipe.get())
				addShapelessRecipe(stack, o);
	}

	public static void patchRecipe(String name, boolean shaped, ItemStack stack, Supplier<Object[][]> recipe) {
		/*if (!CoreInit.hadPostPreInit() && Config.changeRecipe(name)) {
			registerRunner();
			if (ran)
				TMLogger.bigWarn("A mod trying to patch a recipe after the patcher has run!");
			else
				recipesToPatch.add(() -> {
					log.info("Patching " + name + " recipe");
					patchRecipe(stack, shaped, recipe);
				});
		}*/
	}
	private static void registerRunner(){
		if (!scheduled) {
			CoreInit.initRunnables.add(RecipeHelper::runRecipePatcher);
			scheduled = true;
		}
	}
	public static void patchShapedRecipe(String name, ItemStack stack, Supplier<Object[][]> recipe) {
		patchRecipe(name, true, stack, recipe);
	}

	public static void patchShapedRecipe(String name, Block block, Supplier<Object[][]> recipe) {
		patchRecipe(name, true, new ItemStack(block), recipe);
	}

	public static void patchShapedRecipe(String name, Item item, Supplier<Object[][]> recipe) {
		patchRecipe(name, true, new ItemStack(item), recipe);
	}

	public static void patchShapelessRecipe(String name, ItemStack stack, Supplier<Object[][]> recipe) {
		patchRecipe(name, false, stack, recipe);
	}

	public static void runRecipePatcher() {
		if (recipesToPatch.size() > 0) {
			log.info("Patching " + recipesToPatch.size() + " vanilla recipes...");
			recipesToPatch.forEach(Runnable::run);
			ran = true;
		}
	}
	public static <K extends IForgeRegistryEntry<K>> void register(IRecipe in){
		ModContainer mc = Loader.instance().activeModContainer();
		String prefix = mc == null || (mc instanceof InjectedModContainer && ((InjectedModContainer)mc).wrappedContainer instanceof FMLContainer) ? "minecraft" : mc.getModId().toLowerCase();
		in.setRegistryName(new ResourceLocation(prefix, "recipe_" + count++));
	}
	private static String writeRecipe(Map<String, Object> in, String name, String path){
		try{
			String fileName = getName(name) + ".json";
			writeJson(in, path + "/" + fileName);
			return fileName.substring(0, fileName.length()-5);
		}catch(Exception e){
			e.printStackTrace();
		}
		return name;
	}
	public static String writeRecipe(Map<String, Object> in, String name, String path, String postFix){
		try{
			String fileName = getName(name, postFix) + ".json";
			writeJson(in, path + "/" + fileName);
			return fileName.substring(0, fileName.length()-5);
		}catch(Exception e){
			e.printStackTrace();
		}
		return name;
	}
	public static String getName(String name){
		int id = jsonNames.addOrGetCount(name);
		return (id != 0 ? name + "_alt" + id : name);
	}
	public static String getName(String name, String postFix){
		int id = jsonNames.addOrGetCount(name + postFix);
		return (id != 0 ? name + "_alt" + id : name);
	}
	private static String writeRecipe(Map<String, Object> in, String name){
		return writeRecipe(in, name, "recipes");
	}
	private static String writeAdvRecipe(Map<String, Object> in, String name){
		return writeRecipe(in, name, AdvancedCraftingHandler.ROOT);
	}
	private static String writeAdvancement(Map<String, Object> in, String cat, String name){
		try{
			String fileName = getName(name, "_a") + ".json";
			writeJson(in, "advancements/" + cat + "/" + fileName);
			return fileName.substring(0, fileName.length()-5);
		}catch(Exception e){
			e.printStackTrace();
		}
		return name;
	}
	public static void writeJson(Map<String, Object> in, String name) throws IOException {
		log.info("Writing JSON, name: " + name + ", content: " + in.toString());
		PrintWriter w = new PrintWriter(new DualOutputStream(new FileOutputStream(mkdirs(new File(src, name))), new FileOutputStream(mkdirs(new File(bin, name)))));
		gson.toJson(in, w);
		w.close();
	}
	private static File mkdirs(File in){
		in.getParentFile().mkdirs();
		return in;
	}
	private static Integer parseShaped(Map<String, Object> map, Object[] recipe) {
		int width = 0, height = 0;
		List<String> pattern = new ArrayList<>();
		Map<String, Object> key = new HashMap<>();
		map.put("pattern", pattern);
		map.put("key", key);
		String shape = "";
		int idx = 0;
		Integer extra = null;

		if (recipe[idx] instanceof Boolean) {
			map.put("mirrored", recipe[idx]);
			if (recipe[idx+1] instanceof Object[])
				recipe = (Object[])recipe[idx+1];
			else
				idx = 1;
		}

		if (recipe[idx] instanceof Integer) {
			extra = (Integer) recipe[idx];
			if (recipe[idx+1] instanceof Object[])
				recipe = (Object[])recipe[idx+1];
			else
				idx++;
		}

		if (recipe[idx] instanceof String[]) {
			String[] parts = ((String[])recipe[idx++]);

			for (String s : parts)
			{
				width = s.length();
				shape += s;
				pattern.add(s);
			}

			height = parts.length;
		}
		else {
			while (recipe[idx] instanceof String) {
				String s = (String)recipe[idx++];
				shape += s;
				pattern.add(s);
				width = s.length();
				height++;
			}
		}

		if (width * height != shape.length() || shape.length() == 0) {
			String err = "Invalid shaped recipe: ";
			for (Object tmp :  recipe)
			{
				err += tmp + ", ";
			}
			throw new RuntimeException(err);
		}

		HashMap<Character, Ingredient> itemMap = Maps.newHashMap();
		itemMap.put(' ', Ingredient.EMPTY);

		for (; idx < recipe.length; idx += 2) {
			Character chr = (Character)recipe[idx];
			Object in = recipe[idx + 1];

			Ingredient ing = CraftingHelper.getIngredient(in);

			if (' ' == chr.charValue())
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

			if (ing != null) {
				itemMap.put(chr, ing);
				key.put(chr.toString(), serialize(in));
			}
			else {
				String err = "Invalid shaped ore recipe: ";
				for (Object tmp :  recipe)
				{
					err += tmp + ", ";
				}
				throw new RuntimeException(err);
			}
		}

		Set<Character> keys = Sets.newHashSet(itemMap.keySet());
		keys.remove(' ');

		for (char chr : shape.toCharArray()) {
			Ingredient ing = itemMap.get(chr);
			if (ing == null)
				throw new IllegalArgumentException("Pattern references symbol '" + chr + "' but it's not defined in the key");
			keys.remove(chr);
		}

		if (!keys.isEmpty())
			throw new IllegalArgumentException("Key defines symbols that aren't used in pattern: " + keys);

		return extra;
	}
	private static Object serialize(Object o){
		o = wrap(o);
		if(o instanceof ItemStack){
			return o;
		}else if(o instanceof String){
			Map<String, String> m = new HashMap<>();
			m.put("type", "forge:ore_dict");
			m.put("ore", o.toString());
			return m;
		}
		Config.logWarn("Invalid Ingredient " + o);
		return "~~INVALID~~";
	}
	private static Object wrap(Object o){
		if(o instanceof Item)return new ItemStack((Item) o);
		else if(o instanceof Block)return new ItemStack((Block) o);
		else return o;
	}
	public static void writeAdvCrafting(ItemStack out, int craftingTime, List<Research> requiredResearches, ItemStack extra, CraftingLevel level, Object... recipe){
		if(genJson){
			CoreInit.initRunnables.add(() -> {
				Map<String, Object> map = new HashMap<>();
				Map<String, Object> rmap = new HashMap<>();
				Integer extraD = encodeShaped(rmap, out, recipe);
				map.put("recipe", rmap);
				if(extra != null && !extra.isEmpty())map.put("secondary", new ItemStackWCount(extra));
				map.put("crafting_time", craftingTime);
				if(extraD != null)map.put("extra_data", extraD);
				map.put("level", level.getName());
				map.put("researches", requiredResearches.stream().filter(e -> e != null).filter(e -> {
					if(e.delegate == null || e.delegate.name() == null)log.error("Research delegate is null: " + e.getName());
					return e.delegate != null && e.delegate.name() != null;
				}).map(e -> e.delegate.name().toString()).collect(Collectors.toList()));
				writeAdvRecipe(map, out.getItem().delegate.name().getResourcePath());
			});
		}
	}
	public static void writeAdvCraftingShapeless(ItemStack out, int craftingTime, List<Research> requiredResearches, ItemStack extra, CraftingLevel level, Object... recipe){
		if(genJson){
			CoreInit.initRunnables.add(() -> {
				Map<String, Object> map = new HashMap<>();
				Map<String, Object> rmap = new HashMap<>();
				Integer extraD = encodeShapeless(rmap, out, recipe);
				map.put("recipe", rmap);
				if(extra != null && !extra.isEmpty())map.put("secondary", new ItemStackWCount(extra));
				map.put("crafting_time", craftingTime);
				if(extraD != null)map.put("extra_data", extraD);
				map.put("level", level.getName());
				map.put("researches", requiredResearches.stream().filter(e -> e != null).filter(e -> {
					if(e.delegate == null || e.delegate.name() == null)log.error("Research delegate is null: " + e.getName());
					return e.delegate != null && e.delegate.name() != null;
				}).map(e -> e.delegate.name().toString()).collect(Collectors.toList()));
				writeAdvRecipe(map, out.getItem().delegate.name().getResourcePath());
			});
		}
	}
	private static Integer encodeShaped(Map<String, Object> map, ItemStack output, Object... inputs){
		map.put("type", "forge:ore_shaped");
		//map.put("group", output.getItem().delegate.name().toString());
		Integer extraD = parseShaped(map, inputs);
		map.put("result", new ItemStackWCount(output));
		return extraD;
	}
	private static Integer encodeShapeless(Map<String, Object> map, ItemStack output, Object... inputs){
		map.put("type", "forge:ore_shapeless");
		Integer extraD = null;
		if (inputs[0] instanceof Integer) {
			extraD = (Integer) inputs[0];
			Object[] r = new Object[inputs.length - 1];
			System.arraycopy(inputs, 1, r, 0, r.length);
			inputs = r;
		}
		//map.put("group", output.getItem().delegate.name().toString());
		map.put("ingredients", Arrays.stream(inputs).map(RecipeHelper::serialize).toArray());
		map.put("result", new ItemStackWCount(output));
		return extraD;
	}

	public static void writeTrade(List<Object> trades, String name) {
		if(genJson){
			Map<String, Object> map = new HashMap<>();
			map.put("trades", trades);
			try {
				writeJson(map, name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static boolean genJson() {
		return genJson;
	}
	public static void writeFactories(){
		if(genJson()){
			CoreInit.initRunnables.add(() -> {
				Map<String, Object> map = new HashMap<>();
				Map<String, String> m = new HashMap<>();
				map.put("ingredients", m);
				m.put("circuit", "com.tom.core.TMResource$tomsmod_circuit");
				m.put("circuitcomp", "com.tom.core.TMResource$tomsmod_circuitcomp");
				m.put("circuitpanel", "com.tom.core.TMResource$tomsmod_circuitpanel");
				m.put("material", "com.tom.core.TMResource$tomsmod_material");
				m.put("chipset", "com.tom.core.TMResource$tomsmod_chipset");
				writeRecipe(map, "_factories", "recipes", "_f");
			});
		}
	}

	public static void askReload() {
		if(genJson()){
			new JOptionPane();
			Thread t1 = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					JOptionPane.getRootFrame().dispose();

				}
			});
			t1.start();
			if(JOptionPane.showConfirmDialog(null, "Recipe Generation Complete. Restart?", "Restart", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
				throw new RuntimeException("Game aborted");
			}
		}
	}
}
