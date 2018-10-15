package com.tom.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.storage.StorageInit;
import com.tom.storage.multipart.block.StorageNetworkCable;

public class OreDict {
	private static List<String> printedIDs = new ArrayList<>();

	public static void init() {
		TMResource.loadOreDict();
		registerOre("itemMortar", CoreInit.mortarAndPestle);
		registerOre("blockPiston", Blocks.PISTON);
		registerOre("blockPiston", Blocks.STICKY_PISTON);
		registerOre("glassHardened", new ItemStack(CoreInit.hardenedGlass, 1, 0));
		registerOre("glassEnder", new ItemStack(CoreInit.hardenedGlass, 1, 1));
		registerOre("glassPaneHardened", new ItemStack(CoreInit.hardenedGlassPane));
		((StorageNetworkCable) StorageInit.cable).loadOreDict();
		registerOre("blockFlint", new ItemStack(CoreInit.flintBlock));
		registerOre("itemFlint", new ItemStack(Items.FLINT));
		registerOre("logRubber", new ItemStack(CoreInit.rubberWood));
		registerOre("saplingRubber", new ItemStack(CoreInit.rubberSapling));
		registerOre("leavesRubber", new ItemStack(CoreInit.rubberLeaves));
		registerOre("tomsmodwrench", new ItemStack(CoreInit.wrench));
		registerOre("slimeball", CraftingMaterial.ROSIN.getStackNormal());
	}

	public static void registerOre(String name, Item ore) {
		registerOre(name, new ItemStack(ore));
	}

	public static void registerOre(String name, Block ore) {
		registerOre(name, new ItemStack(ore));
	}

	public static void registerOre(String name, ItemStack ore) {
		if (Config.logOredictNames && !printedIDs.contains(name)) {
			CoreInit.log.info("OreDict name: " + name);
			printedIDs.add(name);
		}
		OreDictionary.registerOre(name, ore);
	}

	public static List<ItemStack> getOres(List<String> ids) {
		List<ItemStack> stackList = new ArrayList<>();
		for (String id : ids) {
			List<ItemStack> oreStacks = OreDictionary.getOres(id);
			stackList.addAll(oreStacks);
		}
		return stackList;
	}

	public static boolean isOre(ItemStack itemStack, String oreID) {
		if (itemStack.isEmpty())
			return false;
		int id = OreDictionary.getOreID(oreID);
		int[] ids = OreDictionary.getOreIDs(itemStack);
		for (int i = 0;i < ids.length;i++) {
			if (ids[i] == id)
				return true;
		}
		return false;
	}

	public static Stream<ItemStack> stream(String oid) {
		return OreDictionary.getOres(oid).stream();
	}

	public static Stream<ItemStack> stream(String oid, int count) {
		return stream(oid).map(ItemStack::copy).map(i -> {i.setCount(count);return i;});
	}
}
