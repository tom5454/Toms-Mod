package com.tom.recipes.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.tom.api.recipes.RecipeHelper;
import com.tom.api.research.Research;
import com.tom.config.Config;
import com.tom.core.research.ResearchHandler;
import com.tom.util.RecipeData;
import com.tom.util.TomsModUtils;

public class AdvancedCraftingHandler {
	private static List<RecipeData> recipeList = new ArrayList<>();
	public static final Logger log = LogManager.getLogger("Tom's Mod] [JSON Recipe loader");
	//private static List<RecipeData> recipeListExtra = new ArrayList<>();
	private static final InventoryCrafting craftingInv = new InventoryCrafting(new Container() {

		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			return false;
		}

		@Override
		public void onCraftMatrixChanged(net.minecraft.inventory.IInventory inventoryIn) {
		};
	}, 3, 3);
	public static final String ROOT = "machine_recipes/research_table";

	/*public static void addRecipe(ItemStack out, int craftingTime, ItemStack[] input, List<IResearch> requiredResearches, boolean shaped, ItemStack extra, CraftingLevel level){
		if(out != null){
			if(input.length != 9){
				ItemStack[] newArray = new ItemStack[9];
				for(int i = 0;i<input.length;i++){
					newArray[i] = input[i];
				}
				input = newArray;
			}
			recipeList.add(new RecipeData(out, craftingTime, input, requiredResearches, shaped, extra, level));
		}
	}*/
	/*public static void addRecipe(ItemStack out, int craftingTime, ItemStack[] input, List<IResearch> requiredResearches, boolean shaped, CraftingLevel level){
		addRecipe(out, craftingTime, input, requiredResearches, shaped, null, level);
	}
	public static void addRecipe(ItemStack out, int craftingTime, ItemStack[] input, List<IResearch> requiredResearches, ItemStack extra, CraftingLevel level){
		addRecipe(out, craftingTime, input, requiredResearches, true, extra, level);
	}
	public static void addRecipe(ItemStack out, int craftingTime, ItemStack[] input, List<IResearch> requiredResearches, CraftingLevel level){
		addRecipe(out, craftingTime, input, requiredResearches, true, null, level);
	}*/
	public static void addRecipe(IRecipe recipe, int craftingTime, List<Research> requiredResearches, ItemStack extra, CraftingLevel level, Integer extraD, String rid) {
		if (recipe != null) {
			if (Config.enableResearchSystem) {
				if (extra == null)
					extra = ItemStack.EMPTY;
				if (extraD != null) {
					RecipeData r = new RecipeData(recipe, craftingTime, requiredResearches, extra, level, extraD, rid);
					recipeList.add(r);
					//recipeListExtra.add(r);
				} else {
					recipeList.add(new RecipeData(recipe, craftingTime, requiredResearches, extra, level, 0, rid));
				}
			} else {
				RecipeHelper.register(recipe);
			}
		}
	}

	public static ReturnData craft(ItemStack[] input, List<Research> researchList, CraftingLevel level, World world) {
		for (int i = 0;i < recipeList.size();i++) {
			RecipeData data = recipeList.get(i);
			/*ItemStack[] array = {data.itemstack1, data.itemstack2, data.itemstack3, data.itemstack4, data.itemstack5, data.itemstack6, data.itemstack7, data.itemstack8, data.itemstack9};
			boolean equals = true;
			List<ItemStackComparator> inL = new ArrayList<ItemStackComparator>();
			for(int k = 0;k<9;k++){
				if(inA[k] != null) inL.add(new ItemStackComparator(inA[k]));
			}
			if(inL.isEmpty())return null;
			for(int j = 0;j<9;j++){
				if(data.shaped){
					ItemStack r = array[j];
					ItemStack in = inA[j];
					equals = equals && ((in == null && r == null) || TomsModUtils.areItemStacksEqualOreDict(in, r, true, false, false, true));
				}else{
					if(inL.isEmpty())break;
					if(array[j] != null){
						ItemStackComparator r = new ItemStackComparator(array[j]);
						if(inL.contains(r))inL.remove(r);
					}
				}
			}
			if(!data.shaped)equals = inL.isEmpty();*/
			for (int j = 0;j < input.length && j < 9;j++) {
				craftingInv.setInventorySlotContents(j, input[j] != null ? input[j] : ItemStack.EMPTY);
			}
			if (data.recipe.matches(craftingInv, world)) {
				if (data.requiredResearches == null || researchList == null || researchList.containsAll(data.requiredResearches))
					return new ReturnData(TomsModUtils.copyItemStack(data.recipe.getCraftingResult(craftingInv)), data.processTime, TomsModUtils.copyItemStack(data.itemstack10), true, level == null || data.level.isEqual(level));
				else
					return new ReturnData(TomsModUtils.copyItemStack(data.recipe.getCraftingResult(craftingInv)), data.processTime, TomsModUtils.copyItemStack(data.itemstack10), false, level == null || data.level.isEqual(level));

			}
		}
		return null;
	}

	public static class ReturnData {
		private final ItemStack returnStack;
		private final ItemStack extraStack;
		private final int time;
		private final boolean hasAllResearches, rightLevel;

		public ReturnData(ItemStack returnStack, int time, ItemStack extra, boolean hasAllResearches, boolean rightLevel) {
			this.returnStack = returnStack;
			this.time = time;
			this.extraStack = extra;
			this.hasAllResearches = hasAllResearches;
			this.rightLevel = rightLevel;
		}

		public ItemStack getReturnStack() {
			return returnStack;
		}

		public int getTime() {
			return time;
		}

		public ItemStack getExtraStack() {
			return extraStack;
		}

		public boolean hasAllResearches() {
			return hasAllResearches;
		}

		public boolean isRightLevel() {
			return rightLevel;
		}
	}

	public static List<RecipeData> getRecipes() {
		return recipeList;
	}

	public static void addRecipe(ItemStack out, int craftingTime, List<Research> requiredResearches, ItemStack extra, CraftingLevel level, Object... recipe) {
		//addRecipe(out, craftingTime, requiredResearches, extra, true, level, recipe);
		RecipeHelper.writeAdvCrafting(out, craftingTime, requiredResearches == null ? new ArrayList<>() : requiredResearches, extra, level, recipe);
	}

	public static void addShapelessRecipe(ItemStack out, int craftingTime, List<Research> requiredResearches, ItemStack extra, CraftingLevel level, Object... recipe) {
		//addRecipe(out, craftingTime, requiredResearches, extra, false, level, recipe);
		RecipeHelper.writeAdvCraftingShapeless(out, craftingTime, requiredResearches == null ? new ArrayList<>() : requiredResearches, extra, level, recipe);
	}

	public static void addRecipe(ItemStack result, int craftingTime, List<Research> requiredResearches, ItemStack extra, boolean shaped, CraftingLevel level, Object... recipe) {
		Integer extraD = null;
		if (recipe[0] instanceof Integer) {
			extraD = (Integer) recipe[0];
			Object[] r = new Object[recipe.length - 1];
			System.arraycopy(recipe, 1, r, 0, r.length);
			recipe = r;
		}
		/*ItemStack output = result.copy();
		int width = 3;
		int height = 3;
		String shape = "";
		int idx = 0;
		if(shaped){
			if (recipe[idx] instanceof String[])
			{
				String[] parts = ((String[])recipe[idx++]);

				for (String s : parts)
				{
					width = s.length();
					shape += s;
				}

				height = parts.length;
			}
			else
			{
				height = 0;
				while (recipe[idx] instanceof String)
				{
					String s = (String)recipe[idx++];
					shape += s;
					width = s.length();
					height++;
				}
			}
			if (width * height != shape.length())
			{
				String ret = "Invalid shaped recipe: ";
				for (Object tmp :  recipe)
				{
					ret += tmp + ", ";
				}
				ret += output;
				throw new RuntimeException(ret);
			}
		}
		HashMap<Character, ItemStack> itemMap = new HashMap<Character, ItemStack>();
		try{
			for (; idx < recipe.length; idx += (shaped ? 2 : 1))
			{
				Character chr = shaped ? (Character)recipe[idx] : (char)idx;
				Object in = recipe[shaped ? idx + 1 : idx];

				if (in instanceof ItemStack)
				{
					itemMap.put(chr, ((ItemStack)in).copy());
				}
				else if (in instanceof Item)
				{
					itemMap.put(chr, new ItemStack((Item)in));
				}
				else if (in instanceof Block)
				{
					itemMap.put(chr, new ItemStack((Block)in, 1, OreDictionary.WILDCARD_VALUE));
				}
				else if (in instanceof String)
				{
					itemMap.put(chr, OreDictionary.getOres((String)in).get(0));
				}
				else
				{
					String ret = "Invalid shaped recipe: ";
					for (Object tmp :  recipe)
					{
						ret += tmp + ", ";
					}
					ret += output;
					throw new RuntimeException(ret);
				}
			}
			ItemStack[] input = new ItemStack[width * height];
			int x = 0;
			if(shaped){
				for (char chr : shape.toCharArray())
				{
					input[x++] = itemMap.get(chr);
				}
			}else{
				for (Entry<Character, ItemStack> chr : itemMap.entrySet())
				{
					input[x++] = chr.getValue();
				}
			}
			addRecipe(output, craftingTime, input, requiredResearches, shaped, extra, level);
		}catch(Throwable e){
			TMLogger.bigCatching(e, "Error occurred while adding a recipe.");
		}*/
		addRecipe(shaped ? new ShapedOreRecipe(new ResourceLocation("tomsmod:advshaped"), result, recipe) : new ShapelessOreRecipe(new ResourceLocation("tomsmod:advshapeless"), result, recipe), craftingTime, requiredResearches == null ? Collections.emptyList() : requiredResearches, extra, level, extraD, "internal");
	}

	public static enum CraftingLevel {
		BASIC_WOODEN("basic_wooden", "tomsMod.craftingLevel.basic", 0, 0), BRONZE("bronze", "tomsMod.craftingLevel.bronze", 0, 1),
		BASIC_ELECTRICAL("electrical", "tomsMod.craftingLevel.electrical", 0, 2), MV_ELECTRICAL("mv", "tomsMod.craftingLevel.mv", 0, 3),
		HV_ELECTRICAL("hv", "tomsMod.craftingLevel.hv", 0, 4), SOLDERING_STATION("soldering", "tomsMod.craftingLevel.soldering", 1, 0),
		E_SOLDERING_STATION("e_soldering", "tomsMod.craftingLevel.esoldering", 1, 1),;
		private final String unloc_name, name;
		private static final CraftingLevel[] VALUES = values();
		private final int type, lvl;

		private CraftingLevel(String regname, String unloc_name, int type, int lvl) {
			this.unloc_name = unloc_name;
			this.name = regname;
			this.type = type;
			this.lvl = lvl;
		}

		public String getUnlocName() {
			return unloc_name;
		}

		public String getName() {
			return name;
		}

		public boolean isAdvanced() {
			return this != BASIC_WOODEN;
		}

		public boolean isEqual(CraftingLevel other) {
			return type == other.type && lvl <= other.lvl;
		}
		public boolean nameEquals(String name){
			return this.name.equalsIgnoreCase(name);
		}
		public static CraftingLevel getLevelForName(String name){
			for (int i = 0;i < VALUES.length;i++) {
				CraftingLevel craftingLevel = VALUES[i];
				if(craftingLevel.nameEquals(name))return craftingLevel;
			}
			return CraftingLevel.BASIC_WOODEN;
		}
	}

	/*public static class ShapedRecipe extends ShapedOreRecipe {
		public ShapedRecipe(ResourceLocation group, Block result, Object... recipe) {
			this(group, new ItemStack(result), recipe);
		}

		public ShapedRecipe(ResourceLocation group, Item result, Object... recipe) {
			this(group, new ItemStack(result), recipe);
		}

		public ShapedRecipe(ResourceLocation group, ItemStack result, Object[] recipe) {
			super(group, result, recipe);
			for (int i = 0;i < input.size();i++) {
				Ingredient obj = input.get(i);
				if (obj instanceof ItemStack && ((ItemStack) obj).getItem() instanceof ItemCraftingTool) {
					ItemCraftingTool item = (ItemCraftingTool) ((ItemStack) obj).getItem();
					List<ItemStack> list = new ArrayList<>();
					item.addToList(list);
					input[i] = list;
				}
			}
		}
	}

	public static class ShapelessRecipe extends ShapelessOreRecipe {
		public ShapelessRecipe(Block result, Object... recipe) {
			this(new ItemStack(result), recipe);
		}

		public ShapelessRecipe(Item result, Object... recipe) {
			this(new ItemStack(result), recipe);
		}

		public ShapelessRecipe(ItemStack result, Object[] recipe) {
			super(result, recipe);
			for (int i = 0;i < input.size();i++) {
				Object obj = input.get(i);
				if (obj instanceof ItemStack && ((ItemStack) obj).getItem() instanceof ItemCraftingTool) {
					ItemCraftingTool item = (ItemCraftingTool) ((ItemStack) obj).getItem();
					List<ItemStack> list = new ArrayList<>();
					item.addToList(list);
					input.set(i, list);
				}
			}
		}

	}*/
	public static void loadRecipes(){
		recipeList.clear();
		//recipeListExtra.clear();
		JsonContext ctx = new JsonContext("tomsmodcore");
		TomsModUtils.walkResources(ROOT, TomsModUtils.gson, ctx, (o, s) -> parseRecipeJson(o, ctx, s));
	}

	private static void parseRecipeJson(JsonObject obj, JsonContext ctx, String rid){
		if(obj.size() == 0)return;
		boolean c = true;
		ItemStack sec = ItemStack.EMPTY;
		if(obj.has("secondary")){
			sec = CraftingHelper.getItemStack(obj.get("secondary").getAsJsonObject(), ctx);
		}
		if(obj.has("condition")){
			JsonObject cond = obj.get("condition").getAsJsonObject();
			c = CraftingHelper.getCondition(cond, ctx).getAsBoolean();
		}
		if(c){
			IRecipe r = CraftingHelper.getRecipe(obj.get("recipe").getAsJsonObject(), ctx);
			JsonArray researches = obj.getAsJsonArray("researches");
			List<String> rs = new ArrayList<>();
			researches.forEach(e -> rs.add(e.getAsString()));
			int time = obj.get("crafting_time").getAsInt();
			Integer extra = null;
			if(obj.has("extra_data")){
				extra = obj.get("extra_data").getAsInt();
			}
			JsonElement lvlb = obj.get("level");
			CraftingLevel lvl = CraftingLevel.BASIC_WOODEN;
			if(lvlb != null){
				String id = lvlb.getAsString();
				lvl = CraftingLevel.getLevelForName(id);
			}
			addRecipe(r, time, rs.stream().map(s -> {
				Research res = ResearchHandler.getResearchByName(s);
				if(res == null)log.error("Missing research with id: " + s);
				return res;
			}).filter(e -> e != null).collect(Collectors.toList()), sec, lvl, extra, rid);
		}
	}
}
