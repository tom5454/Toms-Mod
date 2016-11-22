package com.tom.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.storage.StorageInit;

public class OreDict {
	private static List<String> printedIDs = new ArrayList<String>();
	public static void init(){
		//CoreInit.log.info(CoreInit.oreUranium);
		registerOre("oreCopper", CoreInit.oreCopper);
		registerOre("oreTin", CoreInit.oreTin);
		registerOre("oreBlueMetal", CoreInit.oreBlueMetal);
		registerOre("oreLead", CoreInit.oreLead);
		registerOre("oreLithium", CoreInit.oreLithium);
		registerOre("oreNickel", CoreInit.oreNickel);
		registerOre("orePlatinum", CoreInit.orePlatinum);
		registerOre("oreRedDiamond", CoreInit.oreRedDiamond);
		registerOre("oreSilver", CoreInit.oreSilver);
		registerOre("oreTitanium", CoreInit.oreTitanium);
		registerOre("oreUranium", CoreInit.oreUranium);
		registerOre("oreChrome", CoreInit.oreChrome);
		registerOre("oreSulfur", CoreInit.oreSulfur);
		registerOre("oreMercury", CoreInit.oreMercury);
		registerOre("oreBauxite", CoreInit.oreBauxite);
		registerOre("oreAluminum", CoreInit.oreBauxite);
		registerOre("oreAluminium", CoreInit.oreBauxite);
		registerOre("oreZinc", CoreInit.oreZinc);
		registerOre("oreTungstate", CoreInit.oreWolfram);
		TMResource.loadOreDict();
		//registerOre("rodIron", CraftingMaterial.IRON_ROD.getStackNormal());
		//registerOre("obsidian", Blocks.obsidian);
		registerOre("itemMortar", CoreInit.mortarAndPestle);
		registerOre("blockPiston", Blocks.PISTON);
		registerOre("blockPiston", Blocks.STICKY_PISTON);
		registerOre("glassHardened", new ItemStack(CoreInit.hardenedGlass, 1, 0));
		registerOre("glassEnder", new ItemStack(CoreInit.hardenedGlass, 1, 1));
		registerOre("glassPaneHardened", new ItemStack(CoreInit.hardenedGlassPane));
		StorageInit.cable.loadOreDict();
		//registerOre("dustIron", Items.gunpowder);
		/*OreDictionary.registerOre("ingotCopper", TMResource.COPPER.getStack(Type.INGOT));
		OreDictionary.registerOre("ingotUranium", TMResource.URANIUM.getStack(Type.INGOT));
		OreDictionary.registerOre("ingotAdvA1", TMResource.advAlloyMK1.getStack(Type.INGOT));
		OreDictionary.registerOre("ingotAdvA2", TMResource.advAlloyMK2.getStack(Type.INGOT));
		OreDictionary.registerOre("ingotBlueMetal", TMResource.BLUE_METAL.getStack(Type.INGOT));
		OreDictionary.registerOre("ingotChrome", TMResource.CHROME.getStack(Type.INGOT));
		OreDictionary.registerOre("ingotGreenium", TMResource.GREENIUM.getStack(Type.INGOT));
		OreDictionary.registerOre("ingotLead", TMResource.LEAD.getStack(Type.INGOT));
		OreDictionary.registerOre("ingotLithium", TMResource.LITHIUM.getStack(Type.INGOT));
		OreDictionary.registerOre("ingotAdvA1", TMResource.TIN.getStack(Type.INGOT));*/
	}
	public static void registerOre(String name, Item      ore){ registerOre(name, new ItemStack(ore));  }
	public static void registerOre(String name, Block     ore){ registerOre(name, new ItemStack(ore));  }
	public static void registerOre(String name, ItemStack ore){
		if(Config.logOredictNames && !printedIDs.contains(name)){
			CoreInit.log.info("OreDict name: " + name);
			printedIDs.add(name);
		}
		OreDictionary.registerOre(name, ore);
	}
	public static List<ItemStack> getOres(List<String> ids){
		List<ItemStack> stackList = new ArrayList<ItemStack>();
		for(String id : ids){
			List<ItemStack> oreStacks = OreDictionary.getOres(id);
			stackList.addAll(oreStacks);
			/*else{
				for(int i = 0;i<oreStacks.size();i++){
					boolean found = false;
					for(int j = 0;j<stackList.size();j++){
						if(TomsModUtils.areItemStacksEqual(oreStacks.get(i), stackList.get(j), true, true, false))found = true;
					}
					if(!found)stackList.add(oreStacks.get(i));
				}
			}*/
		}
		return stackList;
	}
	public static boolean isOre(ItemStack itemStack, String oreID) {
		if(itemStack == null)return false;
		int id = OreDictionary.getOreID(oreID);
		int[] ids = OreDictionary.getOreIDs(itemStack);
		for(int i = 0;i<ids.length;i++){
			if(ids[i] == id)return true;
		}
		return false;
	}
}
