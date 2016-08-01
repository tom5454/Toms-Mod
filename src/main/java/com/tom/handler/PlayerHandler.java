package com.tom.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.config.Configuration;

import com.tom.core.research.handler.ResearchHandler;
import com.tom.storage.multipart.StorageNetworkGrid.StorageItemStackSorting;

public class PlayerHandler {
	private static Map<String,PlayerHandler> handlerMap = new HashMap<String, PlayerHandler>();
	private static File mainFolder;
	private String name;
	private StorageItemStackSorting itemSorting = StorageItemStackSorting.AMOUNT;
	private boolean sortingDir = false;
	private int searchBoxType;
	private static final String CATRGORY = "main", SORTING_DIR = "sortingDirection", ITEM_SORTING = "itemSorting",
			PLAYER_LIST = "playerList", SEARCH_BOX_TYPE = "searchBoxType";
	private static File mainCfg;
	public static Logger log = LogManager.getLogger("Tom's Mod Player Handler");
	private static boolean allowSave = false;
	public PlayerHandler(String name) {
		this.name = name;
	}
	public static void onServerStart(File file){
		log.info("Loading Player Handler...");
		mainFolder = file;
		mainCfg = new File(mainFolder,"main.tmcfg");
		Configuration mainCfg = new Configuration(PlayerHandler.mainCfg);
		String[] playerList = mainCfg.get(CATRGORY, PLAYER_LIST, new String[]{}).getStringList();
		Arrays.sort(playerList);
		for(String s : playerList){
			loadPlayer(s);
		}
		mainCfg.save();
		allowSave = true;
		/*NBTTagCompound testTag = new NBTTagCompound();
		testTag.setInteger("a", 8888);
		try {
			CompressedStreamTools.write(testTag, new File(mainFolder,"test.dat"));
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	public static void loadPlayer(String name){
		log.info("Loading player: "+name);
		if(handlerMap.containsKey(name))
			handlerMap.remove(name);
		handlerMap.put(name, new PlayerHandler(name));
		File cfg = new File(mainFolder,"player_"+name+".dat");
		try{
			NBTTagCompound tag = CompressedStreamTools.read(cfg);
			getPlayerHandlerForName(name).readFromFile(tag,name);
		}catch(Exception e){}
	}
	private void readFromFile(NBTTagCompound tag, String name) {
		this.name = name;
		ResearchHandler.load(name, tag);
		itemSorting = StorageItemStackSorting.get(tag.getInteger(ITEM_SORTING));
		sortingDir = tag.getBoolean(SORTING_DIR);
		searchBoxType = tag.getInteger(SEARCH_BOX_TYPE);
	}
	private static void save(boolean log){
		if(log)PlayerHandler.log.info("Saving Player Handler...");
		/*Configuration cfg = new Configuration(new File(mainFolder,"dim_"+dim+".tmcfg"));
		try{
			getWorldHandlerForDim(dim).writeToFile(cfg);
		}catch(Exception e){}
		cfg.save();*/
		List<String> nameList = new ArrayList<String>();
		for(Entry<String, PlayerHandler> e : handlerMap.entrySet()){
			File cfg = new File(mainFolder,"player_"+e.getKey()+".dat");
			NBTTagCompound tag = new NBTTagCompound();
			try{
				getPlayerHandlerForName(e.getKey()).writeToFile(tag);
				CompressedStreamTools.write(tag, cfg);
			}catch(Exception ex){PlayerHandler.log.catching(ex);}
			nameList.add(e.getKey());
		}
		String[] out = nameList.toArray(new String[]{});
		Configuration mainCfg = new Configuration(PlayerHandler.mainCfg);
		mainCfg.get(CATRGORY, PLAYER_LIST, new String[]{}).set(out);
		mainCfg.save();
	}
	private void writeToFile(NBTTagCompound tag) {
		ResearchHandler.save(name, tag);
		tag.setBoolean(SORTING_DIR, sortingDir);
		tag.setInteger(ITEM_SORTING, itemSorting.ordinal());
		tag.setInteger(SEARCH_BOX_TYPE, searchBoxType);
	}
	public static PlayerHandler getPlayerHandlerForName(String name){
		return handlerMap.get(name);
	}
	public static void addPlayer(String name){
		if(!handlerMap.containsKey(name)){
			log.info("Adding new Player to handler...");
			log.info("Name: "+name);
			handlerMap.put(name, new PlayerHandler(name));
		}
	}
	public int getItemSortingMode(){
		return getItemSortingMode(itemSorting, sortingDir);
	}
	public void setItemSorting(int mode){
		itemSorting = getSortingType(mode);
		sortingDir = getSortingDir(mode);
	}
	public static StorageItemStackSorting getSortingType(int mode){
		return StorageItemStackSorting.get(mode);
	}
	public static boolean getSortingDir(int mode){
		return mode >= StorageItemStackSorting.VALUES.length;
	}
	public StorageItemStackSorting getSortingType(){
		return itemSorting;
	}
	public boolean getSortingDir(){
		return sortingDir;
	}
	public static int getItemSortingMode(StorageItemStackSorting itemSorting, boolean sortingDir){
		return itemSorting.ordinal() + (sortingDir ? StorageItemStackSorting.VALUES.length : 0);
	}
	public static void cleanup() {
		log.info("Cleaning up Player Handler");
		save(true);
		handlerMap.clear();
		allowSave = false;
	}
	public static void save(){
		if(allowSave)save(false);
		else log.info("Player Handler already saved, ignore event.");
	}
	public int getSearchBoxType() {
		return searchBoxType;
	}
	public void setSearchBoxType(int searchBoxType) {
		this.searchBoxType = searchBoxType;
	}
}
