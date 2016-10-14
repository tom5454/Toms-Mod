package com.tom.weaponsAndTools;

import static com.tom.core.CoreInit.registerItem;

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
import com.tom.core.IMod;
import com.tom.lib.Configs;
import com.tom.weaponsAndTools.item.FlintAxe;
import com.tom.weaponsAndTools.item.PortableComparator;

@Mod(modid = ToolsInit.modid,name = ToolsInit.modName,version = Configs.version, dependencies = Configs.coreDependencies)
public class ToolsInit {
	public static final String modid = Configs.ModidL + "|weaponsandtools";
	public static final String modName = Configs.ModName + " Weapons And Tools";
	public static final Logger log = LogManager.getLogger(modName);
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
		hadPreInit = true;
		CoreInit.tryLoadAfterPreInit(log);
		long time = System.currentTimeMillis() - tM;
		log.info("Pre Initialization took in "+time+" milliseconds");
	}
	private static boolean hadPreInit = false;
	@EventHandler
	public static void construction(FMLConstructionEvent event){
		CoreInit.modids.add(new IMod(){
			@Override
			public String getModID() {
				return modid;
			}

			@Override
			public boolean hadPreInit() {
				return hadPreInit;
			}
		});
	}
}
