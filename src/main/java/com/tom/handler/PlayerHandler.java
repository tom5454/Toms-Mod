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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import com.tom.apis.TomsModUtils;
import com.tom.core.research.ResearchHandler;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageProfiler;
import com.tom.storage.handler.ICraftable;
import com.tom.storage.handler.StorageNetworkGrid.ControllMode;

public class PlayerHandler {
	private static Map<String, PlayerHandler> handlerMap = new HashMap<>();
	private static File mainFolder;
	private String name;
	private ICraftable.CraftableSorting itemSorting = ICraftable.CraftableSorting.AMOUNT;
	private boolean sortingDir = false;
	private int searchBoxType;
	private static final String CATRGORY = "main", SORTING_DIR = "sortingDirection", ITEM_SORTING = "itemSorting",
			PLAYER_LIST = "playerList", SEARCH_BOX_TYPE = "searchBoxType", TERM_MODE = "termMode";
	private static List<PlayerHandler> online;
	private static File mainCfg;
	public static Logger log = LogManager.getLogger("Tom's Mod Player Handler");
	private static boolean allowSave = false;
	public boolean profiling;
	public String debugProfilerName = "root";
	public int key;
	public ControllMode controllMode = ControllMode.AE;
	public boolean tallTerminal, wideTerminal;

	public PlayerHandler(String name) {
		this.name = name;
	}

	public static void onServerStart(File file) {
		log.info("Loading Player Handler...");
		mainFolder = file;
		online = new ArrayList<>();
		mainCfg = new File(mainFolder, "main.tmcfg");
		Configuration mainCfg = new Configuration(PlayerHandler.mainCfg);
		String[] playerList = mainCfg.get(CATRGORY, PLAYER_LIST, new String[]{}).getStringList();
		Arrays.sort(playerList);
		for (String s : playerList) {
			loadPlayer(s);
		}
		mainCfg.save();
		allowSave = true;
	}

	public static void loadPlayer(String name) {
		log.info("Loading player: " + name);
		if (handlerMap.containsKey(name))
			handlerMap.remove(name);
		handlerMap.put(name, new PlayerHandler(name));
		File cfg = new File(mainFolder, "player_" + name + ".dat");
		try {
			NBTTagCompound tag = CompressedStreamTools.read(cfg);
			getPlayerHandlerForName(name).readFromFile(tag, name);
		} catch (Exception e) {
		}
	}

	private void readFromFile(NBTTagCompound tag, String name) {
		this.name = name;
		ResearchHandler.load(name, tag);
		itemSorting = ICraftable.CraftableSorting.get(tag.getInteger(ITEM_SORTING));
		sortingDir = tag.getBoolean(SORTING_DIR);
		searchBoxType = tag.getInteger(SEARCH_BOX_TYPE);
		byte termMode = tag.getByte(TERM_MODE);
		int ctrl = termMode % 0x10;
		controllMode = ControllMode.VALUES[ctrl];
		wideTerminal = TomsModUtils.getBit(termMode, 4);
		tallTerminal = TomsModUtils.getBit(termMode, 5);
	}

	private static void save(boolean log) {
		if (log)
			PlayerHandler.log.info("Saving Player Handler...");
		List<String> nameList = new ArrayList<>();
		for (Entry<String, PlayerHandler> e : handlerMap.entrySet()) {
			File cfg = new File(mainFolder, "player_" + e.getKey() + ".dat");
			NBTTagCompound tag = new NBTTagCompound();
			try {
				getPlayerHandlerForName(e.getKey()).writeToFile(tag);
				CompressedStreamTools.write(tag, cfg);
			} catch (Exception ex) {
				PlayerHandler.log.catching(ex);
			}
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
		int termMode = controllMode.ordinal();
		termMode = TomsModUtils.setBit(termMode, 4, wideTerminal);
		termMode = TomsModUtils.setBit(termMode, 5, tallTerminal);
		tag.setByte(TERM_MODE, (byte) termMode);
	}

	public static PlayerHandler getPlayerHandlerForName(String name) {
		return handlerMap.get(name);
	}

	public static PlayerHandler addOrGetPlayer(EntityPlayer player) {
		String name = player.getName();
		if (!handlerMap.containsKey(name)) {
			log.info("Adding new Player to handler...");
			log.info("Name: " + name);
			PlayerHandler h = new PlayerHandler(name);
			handlerMap.put(name, h);
		}
		return handlerMap.get(name);
	}

	public int getItemSortingMode() {
		return getItemSortingMode(itemSorting, sortingDir);
	}

	public void setItemSorting(int mode) {
		itemSorting = getSortingType(mode);
		sortingDir = getSortingDir(mode);
	}

	public static ICraftable.CraftableSorting getSortingType(int mode) {
		return ICraftable.CraftableSorting.get(mode);
	}

	public static boolean getSortingDir(int mode) {
		return mode >= ICraftable.CraftableSorting.VALUES.length;
	}

	public ICraftable.CraftableSorting getSortingType() {
		return itemSorting;
	}

	public boolean getSortingDir() {
		return sortingDir;
	}

	public static int getItemSortingMode(ICraftable.CraftableSorting itemSorting, boolean sortingDir) {
		return itemSorting.ordinal() + (sortingDir ? ICraftable.CraftableSorting.VALUES.length : 0);
	}

	public static void cleanup() {
		log.info("Cleaning up Player Handler");
		save(true);
		handlerMap.clear();
		allowSave = false;
	}

	public static void save() {
		if (allowSave)
			save(false);
		else
			log.info("Player Handler already saved, ignore event.");
	}

	public int getSearchBoxType() {
		return searchBoxType;
	}

	public void setSearchBoxType(int searchBoxType) {
		this.searchBoxType = searchBoxType;
	}

	public static void update(Phase phase) {
		switch (phase) {
		case END:
			online.forEach(p -> p.update1());
			break;
		case START:
			profile = false;
			online.forEach(p -> p.update0());
			if (!profile && profiled) {
				FMLCommonHandler.instance().getMinecraftServerInstance().profiler.profilingEnabled = false;
				profiled = false;
			}
			break;
		default:
			break;
		}
	}

	private static boolean profile = false, profiled;

	private void update0() {
		if (profiling) {
			FMLCommonHandler.instance().getMinecraftServerInstance().enableProfiling();
			profile = true;
			profiled = true;
		}
	}

	private void update1() {
		if (profiling) {
			if (ret) {
				debugProfilerName = updateDebugProfilerName(debugProfilerName, 0);
				ret = false;
			} else if (next != null) {
				debugProfilerName = updateDebugProfilerName(debugProfilerName, next);
				next = null;
			}
			NetworkHandler.sendTo(new MessageProfiler(FMLCommonHandler.instance().getMinecraftServerInstance().profiler.getProfilingData(debugProfilerName), debugProfilerName), getPlayer());
		}
	}

	private String updateDebugProfilerName(String in, String next) {
		List<Profiler.Result> list = FMLCommonHandler.instance().getMinecraftServerInstance().profiler.getProfilingData(in);

		if (!list.isEmpty()) {
			if (list.stream().anyMatch(r -> r.profilerName.equals(next)) && !"unspecified".equals(next)) {
				if (!in.isEmpty()) {
					in = in + ".";
				}

				in = in + next;
			}
		}
		return in;
	}

	public EntityPlayerMP getPlayer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(name);
	}

	public static void playerLogIn(EntityPlayer player) {
		online.add(addOrGetPlayer(player));
		log.info("'" + player.getName() + "' logged in");
	}

	public static void playerLogOut(EntityPlayer player) {
		online.remove(getPlayerHandler(player));
		log.info("'" + player.getName() + "' logged out");
	}

	public static PlayerHandler getPlayerHandler(EntityPlayer player) {
		return getPlayerHandlerForName(player.getName());
	}

	public static String updateDebugProfilerName(String in, int keyCount) {
		List<Profiler.Result> list = FMLCommonHandler.instance().getMinecraftServerInstance().profiler.getProfilingData(in);

		if (!list.isEmpty()) {
			Profiler.Result profiler$result = list.remove(0);

			if (keyCount == 0) {
				if (!profiler$result.profilerName.isEmpty()) {
					int i = in.lastIndexOf(46);

					if (i >= 0) {
						in = in.substring(0, i);
					}
				}
			} else {
				--keyCount;

				if (keyCount < list.size() && !"unspecified".equals(list.get(keyCount).profilerName)) {
					if (!in.isEmpty()) {
						in = in + ".";
					}

					in = in + list.get(keyCount).profilerName;
				}
			}
		}
		return in;
	}

	private String next;
	private boolean ret;

	public void setProfiler(String s) {
		if (s == null) {
			ret = true;
		} else {
			next = s;
		}
	}
}
