package com.tom.weaponsAndTools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.tom.config.Config;
import com.tom.core.CoreInit;
import com.tom.lib.Configs;
import com.tom.weaponsAndTools.item.FlintAxe;
import com.tom.weaponsAndTools.item.PortableComparator;

@Mod(modid = ToolsInit.modid,name = "Tom's Mod Weapons And Tools",version = Configs.version, dependencies = Configs.coreDependencies)
public class ToolsInit {
	public static final String modid = Configs.Modid + "|WeaponsAndTools";
	public static Logger log = LogManager.getLogger(modid);
	public static CreativeTabs tabTomsModWeaponsAndTools = CoreInit.tabTomsModWeaponsAndTools;
	public static Item.ToolMaterial flintToolMaterial;
	public static Item flintAxe, portableComparator;
	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent){
		log.info("Start Pre Initialization");
		long tM = System.currentTimeMillis();
		if(Config.enableHardMode){
			log.warn("Hard mode enabled");
			flintToolMaterial = EnumHelper.addToolMaterial("flintToolMaterial", 1, 150, 1F, 0, 1);
		}else{
			flintToolMaterial = EnumHelper.addToolMaterial("flintToolMaterial", 1, 200, 1.5F, 0, 1);
		}
		flintAxe = new FlintAxe(flintToolMaterial, -3.2F).setUnlocalizedName("flintAxe")/*.setTextureName("minecraft:flintAxe")*/.setCreativeTab(tabTomsModWeaponsAndTools);
		portableComparator = new PortableComparator().setCreativeTab(tabTomsModWeaponsAndTools).setUnlocalizedName("tm.portableComparator").setMaxStackSize(1);
		registerItem(flintAxe, flintAxe.getUnlocalizedName().substring(5));
		registerItem(portableComparator, portableComparator.getUnlocalizedName().substring(5));
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in "+time+" milliseconds");
	}
	public static void registerItem(Item item, String registerName){
		CoreInit.itemList.add(item);
		CoreInit.addItemToGameRegistry(item, registerName);
	}
	@EventHandler
	public static void construction(FMLConstructionEvent event){
		CoreInit.modids.add(modid);
	}
}
