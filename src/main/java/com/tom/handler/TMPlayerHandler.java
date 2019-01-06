package com.tom.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.common.FMLCommonHandler;

import com.tom.api.block.IGridPowerGenerator;
import com.tom.api.grid.StorageNetworkGrid.ControllMode;
import com.tom.api.item.IWirelessDevice;
import com.tom.api.tileentity.IAccessPoint;
import com.tom.core.research.ResearchHandler;
import com.tom.lib.api.IValidationChecker;
import com.tom.lib.api.tileentity.ITMPeripheral.IComputer;
import com.tom.lib.handler.IPlayerHandler;
import com.tom.lib.handler.PlayerHandler;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNetworkConnection;
import com.tom.network.messages.MessageProfiler;
import com.tom.storage.handler.ICraftable;
import com.tom.util.TomsModUtils;

import com.tom.core.item.TabletHandler;

public class TMPlayerHandler implements IPlayerHandler {
	private static final String ID = "tmhandler";
	private String name;
	private ICraftable.CraftableSorting itemSorting = ICraftable.CraftableSorting.AMOUNT;
	private boolean sortingDir = false;
	private int searchBoxType;
	private static final String SORTING_DIR = "sortingDirection", ITEM_SORTING = "itemSorting",
			SEARCH_BOX_TYPE = "searchBoxType", TERM_MODE = "termMode";
	public boolean profiling;
	public String debugProfilerName = "root";
	public int key;
	public ControllMode controllMode = ControllMode.AE;
	public boolean tallTerminal, wideTerminal;
	public TabletHandler tabletHandler;
	public List<IComputer> EnderMemoryIComputerAccess = new ArrayList<>();
	public Map<Integer, Object> EnderMemoryPrivate = new HashMap<>();
	public Set<IGridPowerGenerator> gridPowerGenerators = new HashSet<>();
	public long gridPower, gridPowerMax;
	public boolean underpowered;
	private boolean underpower;
	public static void register(){
		PlayerHandler.registerHandler(ID, TMPlayerHandler::new);
	}
	public TMPlayerHandler(String name) {
		this.name = name;
		this.tabletHandler = new TabletHandler(name);
	}

	@Override
	public void load(NBTTagCompound tag) {
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

	@Override
	public void save(NBTTagCompound tag) {
		ResearchHandler.save(name, tag);
		tag.setBoolean(SORTING_DIR, sortingDir);
		tag.setInteger(ITEM_SORTING, itemSorting.ordinal());
		tag.setInteger(SEARCH_BOX_TYPE, searchBoxType);
		int termMode = controllMode.ordinal();
		termMode = TomsModUtils.setBit(termMode, 4, wideTerminal);
		termMode = TomsModUtils.setBit(termMode, 5, tallTerminal);
		tag.setByte(TERM_MODE, (byte) termMode);
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

	public int getSearchBoxType() {
		return searchBoxType;
	}

	public void setSearchBoxType(int searchBoxType) {
		this.searchBoxType = searchBoxType;
	}
	private boolean profile = false, profiled;

	@Override
	public void updatePre(EntityPlayerMP player) {
		profile = false;
		FMLCommonHandler.instance().getMinecraftServerInstance().profiler.startSection("[Tom's Mod] Update Player Handler");
		if (profiling) {
			FMLCommonHandler.instance().getMinecraftServerInstance().enableProfiling();
			profile = true;
			profiled = true;
		}
		if(player.world.getTotalWorldTime() % 5 == 0){
			boolean found = false;
			for(int i = 0;i<9;i++){
				if(player.inventory.getStackInSlot(i).getItem() instanceof IWirelessDevice){
					found = true;
				}
			}
			if(found){
				List<IAccessPoint> p = TMWorldHandler.getAccessPoints(player.world, player.posX, player.posY, player.posZ);
				List<IAccessPoint>[] changes = TomsModUtils.findDifference(tabletHandler.antenna, p, true);
				if(!changes[0].isEmpty() || !changes[1].isEmpty()){
					NetworkHandler.sendTo(new MessageNetworkConnection(changes), player);
				}
			}
		}
		FMLCommonHandler.instance().getMinecraftServerInstance().profiler.endSection();
		if (!profile && profiled) {
			FMLCommonHandler.instance().getMinecraftServerInstance().profiler.profilingEnabled = false;
			profiled = false;
		}
	}
	public boolean checkAndUseGridPower(int power){
		if(gridPower > gridPowerMax){
			underpower = true;
			return false;
		}
		gridPower += power;
		return true;
	}

	@Override
	public void updateOffPre() {
		if(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getTotalWorldTime() % 5 == 0){
			gridPowerMax = IValidationChecker.removeAllInvalidCollectValid(gridPowerGenerators, Collectors.summingLong(IGridPowerGenerator::getMaxPowerGen));
		}
	}
	@Override
	public void updateOffPost() {
		underpowered = underpower;
		underpower = false;
	}
	@Override
	public void updatePost(EntityPlayerMP player) {
		if (profiling) {
			if (ret) {
				debugProfilerName = updateDebugProfilerName(debugProfilerName, 0);
				ret = false;
			} else if (next != null) {
				debugProfilerName = updateDebugProfilerName(debugProfilerName, next);
				next = null;
			}
			NetworkHandler.sendTo(new MessageProfiler(FMLCommonHandler.instance().getMinecraftServerInstance().profiler.getProfilingData(debugProfilerName), debugProfilerName), player);
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

	public static List<TabletHandler> getTablets(BlockPos controller) {
		return PlayerHandler.handlers().stream().map(e -> (TMPlayerHandler) e.getHandler(ID)).filter(e -> e != null).
				map(TMPlayerHandler::getTabletHandler).filter(t -> t.maches(controller)).collect(Collectors.toList());
	}

	public TabletHandler getTabletHandler() {
		return tabletHandler;
	}

	@Override
	public String getID() {
		return ID;
	}

	public static TMPlayerHandler getPlayerHandlerForName(String name){
		if(name == null)return null;
		PlayerHandler pl = PlayerHandler.getPlayerHandlerForName(name);
		return pl == null ? null : (TMPlayerHandler) pl.getHandler(ID);
	}
	public static TMPlayerHandler getPlayerHandler(EntityPlayer player) {
		PlayerHandler pl = PlayerHandler.getPlayerHandler(player);
		return pl == null ? null : (TMPlayerHandler) pl.getHandler(ID);
	}
}
