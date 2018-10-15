package com.tom.storage.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Function;

import com.tom.api.energy.IEnergyStorage;
import com.tom.api.grid.GridBase;
import com.tom.api.grid.IDenseGridDevice;
import com.tom.api.inventory.IStorageInventory;
import com.tom.api.inventory.StoredItemStack;
import com.tom.api.multipart.IDuctModule;
import com.tom.api.multipart.PartDuct;
import com.tom.api.multipart.PartModule;
import com.tom.api.tileentity.ISecuredTileEntity;
import com.tom.lib.api.grid.BlockAccess;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.api.grid.IGridUpdateListener;
import com.tom.lib.api.grid.IMultigridDevice;
import com.tom.storage.multipart.PartStorageNetworkCable;
import com.tom.storage.multipart.block.StorageNetworkCable;
import com.tom.util.Storage;
import com.tom.util.TomsModUtils;

public class StorageNetworkGrid extends GridBase<StorageData, StorageNetworkGrid> {
	private static final PowerDrainSorter POWER_SORTER = new PowerDrainSorter();
	private StorageData data = new StorageData();
	private boolean denseGrid = false;
	private List<IDevice> devices = new ArrayList<>();
	private List<IRouterTile> denseC = new ArrayList<>();
	private List<IChannelLoadListener> updateListeners = new ArrayList<>();
	private BlockPos controllerPos;
	private int ch;
	private IController controller;
	private IRouter router;
	// private IAdvRouter advRouter;
	public Channel channel = new Channel();
	private String vpnID;
	private NetworkState state = NetworkState.OFF;
	private boolean active = true;

	@Override
	public StorageData getData() {
		if (vpnID != null) {
			Storage<StorageData> d = data.getVPN(vpnID);
			if (d == null)
				return data;
			else
				return d.get();
		} else
			return data;
	}

	protected void setDataRaw(StorageData data) {
		this.data = data;
	}

	public void setVpnID(String vpnID) {
		this.vpnID = vpnID;
	}

	public String getVpnID() {
		return vpnID;
	}

	@Override
	public StorageNetworkGrid importFromNBT(NBTTagCompound tag) {
		getData().readFromNBT(tag);
		return this;
	}

	@Override
	public void updateGrid(World world, IGridDevice<StorageNetworkGrid> master) {
		world.profiler.startSection("StorageNetworkGrid.updateGrid");
		if (getData().grids.size() == 1)
			getData().update();
		if (world.getTotalWorldTime() % 5 == 0) {
			List<IControllerTile> toRemove = new ArrayList<>();
			getData().controllers.forEach((c) -> {
				if (!c.isValid())
					toRemove.add(c);
			});
			if (!toRemove.isEmpty())
				getData().controllers.removeAll(toRemove);
		}
		// world.profiler.endSection();
		if (!getData().grids.contains(this)) {
			getData().grids.add(this);
		}
		// world.profiler.startSection("updateParts");
		if (world.getTotalWorldTime() % 40 == 0)
			loadParts();
		// world.profiler.endSection();
		// world.profiler.startSection("updateChannels");
		loadChannels();
		world.profiler.endSection();
	}

	private void loadChannels() {
		if (!denseGrid) {
			int size = devices.size();
			if (!active || ch > 3 || !getData().showChannels()) {
				NetworkState s = getData().hasEnergy() ? NetworkState.POWERED_ONLY : NetworkState.OFF;
				for (int i = 0;i < size;i++) {
					devices.get(i).setActive(s);
				}
				state = s;
				channel.setValue(0);
			} else {
				channel.setValue(size > 8 ? 10 : size);
				state = getData().networkState == NetworkState.LOADING_CHANNELS ? NetworkState.LOADING_CHANNELS : size <= 8 && ch < 1 ? NetworkState.ACTIVE : (size <= 16 || ch == 1 ? NetworkState.CHANNEL_OVERLOAD_1x : (size <= 24 || ch == 2 ? NetworkState.CHANNEL_OVERLOAD_2x : (size <= 32 || ch == 3 ? NetworkState.CHANNEL_OVERLOAD_3x : NetworkState.POWERED_ONLY)));
				for (int i = 0;i < size;i++) {
					devices.get(i).setActive(state);
				}
			}
		} else {
			ch = 0;
			List<StorageNetworkGrid> grids = new ArrayList<>();
			int channelI = 0;
			for (int i = 0;i < denseC.size();i++) {
				IRouterTile g = denseC.get(i);
				channelI += g.getChannelUsage();
				/*if(g.getData() != getData()){
					g.setData(getData());
					g.loadParts();
				}
				if(!grids.contains(g)){
					grids.add(g);
					ch += g.devices.size();
					g.setData(getData());
				}*/
			}
			channel.setValue(channelI > 8 ? 10 : channelI);
			if (!getData().showChannels()) {
				for (int i = 0;i < grids.size();i++) {
					StorageNetworkGrid g = grids.get(i);
					if (!g.denseGrid)
						g.ch = 4;
				}
			} else {
				for (int i = 0;i < grids.size();i++) {
					StorageNetworkGrid g = grids.get(i);
					if (!g.denseGrid)
						g.ch = /*(ch - 1) / 8*/ 0;
				}
			}
		}
	}

	@Override
	public void setData(StorageData data) {
		boolean n = data != this.getData();
		this.setDataRaw(data);
		if (n)
			loadParts();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		getData().writeToNBT(tag);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void forceUpdateGrid(IBlockAccess world, IGridDevice<StorageNetworkGrid> thisT) {
		// Profiler p = world instanceof World ? ((World)world).profiler : null;
		// if(p != null)p.startSection("forceUpdateGrid");
		// System.out.println("StorageNetworkGrid.forceUpdateGrid()");
		for (int i = 0;i < parts.size();i++)
			parts.get(i).invalidateGrid();
		// new Exception().printStackTrace();
		controllerPos = null;
		parts.clear();
		denseC.clear();
		updateListeners.clear();
		IController controllerOld = controller;
		IRouter routerOld = router;
		// IAdvRouter advRouterOld = advRouter;
		controller = null;
		router = null;
		// advRouter = null;
		channel = new Channel();
		List<IGridDevice<StorageNetworkGrid>> connectedStorages = new ArrayList<>();
		Stack<IGridDevice<StorageNetworkGrid>> traversingStorages = new Stack<>();
		IGridDevice<StorageNetworkGrid> masterOld = master;
		StorageData oldData = this.getData();
		master = thisT;
		boolean denseGridOld = denseGrid;
		denseGrid = false;
		if (master instanceof PartStorageNetworkCable) {
			PartStorageNetworkCable cable = (PartStorageNetworkCable) master;
			denseGrid = cable.type == StorageNetworkCable.CableType.DENSE;
		}
		List<IControllerTile> controllers = new ArrayList<>();
		List<IRouterTile> routers = new ArrayList<>();
		// List<IAdvRouterTile> advRouters = new ArrayList<>();
		int first = 0;
		traversingStorages.add(thisT);
		while (!traversingStorages.isEmpty()) {
			IGridDevice<StorageNetworkGrid> storage = traversingStorages.pop();
			if (storage != null && storage.isValid()) {
				if (storage.isMaster()) {
					master = storage;
				}
				if (!connectedStorages.contains(storage)) {
					connectedStorages.add(storage);
					if (storage instanceof IController) {
						IController ctrl = (IController) storage;
						if (ctrl.getController() != null && !controllers.contains(ctrl.getController())) {
							controllers.add(ctrl.getController());
						}
						if (controller == null) {
							controller = ctrl;
							controllerPos = ctrl.getPos2();
							connectedStorages.add(controller);
							setDataRaw(controller.getTile().getData());
						}
					}
					if (storage instanceof IRouter) {
						if (!routers.contains(((IRouter) storage).getTile())) {
							routers.add((IRouterTile) ((IRouter) storage).getTile());
						}
						if (router == null) {
							router = (IRouter) storage;
							connectedStorages.add(router);
							if (controllerPos == null)
								controllerPos = router.getPos2();
						}
					}
					/*if(storage instanceof IAdvRouter){
						if(!advRouters.contains(((IAdvRouter) storage).getTile())){
							advRouters.add((IAdvRouterTile) ((IAdvRouter) storage).getTile());
						}
						if(advRouter == null){
							advRouter = (IAdvRouter) storage;
							connectedStorages.add(advRouter);
						}
					}*/
					if (storage instanceof IDenseGridDevice) {
						denseGrid = denseGrid || ((IDenseGridDevice) storage).isDenseGridDevice();
					}
					if (storage instanceof PartDuct) {
						PartDuct<StorageNetworkGrid> duct = (PartDuct<StorageNetworkGrid>) storage;
						List<PartModule<StorageNetworkGrid>> modules = duct.getModules();
						if (modules != null && !modules.isEmpty()) {
							connectedStorages.addAll(modules);
						}
						if (duct instanceof PartStorageNetworkCable) {
							PartStorageNetworkCable cable = (PartStorageNetworkCable) duct;
							if (cable.type == StorageNetworkCable.CableType.SMART || cable.type == StorageNetworkCable.CableType.DENSE) {
								channel.listeners.add(cable);
							}
						}
					} else if (storage instanceof PartModule) {
						PartDuct<StorageNetworkGrid> baseDuct = ((PartModule<StorageNetworkGrid>) storage).getBaseDuct();
						if (baseDuct != null && !connectedStorages.contains(baseDuct) && !traversingStorages.contains(baseDuct)) {
							traversingStorages.add(baseDuct);
						}
					}
					for (BlockAccess d : storage.next()) {
						final PartDuct<?> ductN = getDuct(world, d.getPos(), d.getFacing().getOpposite());
						if (ductN != null) {
							if (ductN.isValidConnection(d.getFacing())) {
								if (!connectedStorages.contains(ductN) && ductN.getGrid().getClass() == getClass()) {
									final PartDuct<StorageNetworkGrid> duct = (PartDuct<StorageNetworkGrid>) ductN;
									if (duct instanceof PartStorageNetworkCable) {
										PartStorageNetworkCable cable = (PartStorageNetworkCable) duct;
										if (first == 1)
											denseGrid = denseGrid || cable.isDenseGridDevice();
										if (denseGrid) {
											if (cable.type == StorageNetworkCable.CableType.DENSE) {
												traversingStorages.add(duct);
											} else {
												/*addDenseC(duct);
													duct.getGrid().addDenseC(duct);
													denseConnections.add(duct);*/
											}
										} else {
											if (cable.type == StorageNetworkCable.CableType.DENSE) {
												/*duct.getGrid().addDenseC(duct);
													addDenseC(duct);*/
											} else {
												traversingStorages.add(duct);
											}
										}
									} else {
										if (denseGrid) {
											/*addDenseC(duct);
												duct.getGrid().addDenseC(duct);
												denseConnections.add(duct);*/
										} else {
											traversingStorages.add(duct);
										}
									}
								}
							} else {
								PartModule<?> moduleInWay = ductN.getModule(d.getFacing());
								if (moduleInWay != null && moduleInWay instanceof IMultigridDevice) {
									IMultigridDevice<?> multiModule = (IMultigridDevice<?>) moduleInWay;
									IGridDevice<?> other = multiModule.getOtherGridDevice();
									if (other != null && !connectedStorages.contains(other) && other.getGrid().getClass() == getClass()) {
										if (other.isValidConnection(multiModule.getSide())) {
											traversingStorages.add((IGridDevice<StorageNetworkGrid>) other);
											if (other instanceof PartModule) {
												PartDuct<StorageNetworkGrid> baseDuct = ((PartModule<StorageNetworkGrid>) other).getBaseDuct();
												if (baseDuct != null && !connectedStorages.contains(baseDuct) && !traversingStorages.contains(baseDuct)) {
													traversingStorages.add(baseDuct);
												}
											}
										}
									}
								}
							}
						} else {
							TileEntity te = world.getTileEntity(d.getPos());
							if (te instanceof IControllerTile) {
								if (!controllers.contains(te)) {
									controllers.add((IControllerTile) te);
								}
								if (controller == null) {
									controller = ((IControllerTile) te).getControllerOnSide(d.getFacing());
									if (controller != null) {
										connectedStorages.add(controller);
										setDataRaw(controller.getTile().getData());
									}
								}
							} else if (te instanceof IRouterTile) {
								if (!routers.contains(te)) {
									routers.add((IRouterTile) te);
								}
								IRouterTile r = (IRouterTile) te;
								if (router == null) {
									router = r.getRouterOnSide(d.getFacing());
									if (controllerPos == null)
										controllerPos = router.getPos2();
								}
								connectedStorages.add(router);
								addDenseC(r);
							} else if (te instanceof IAdvRouterTile) {
								IAdvRouterTile tile = (IAdvRouterTile) te;
								if (tile.getController() != null && !controllers.contains(tile.getController())) {
									controllers.add(tile.getController());
								}
								if (controller == null) {
									controller = tile.getRouterOnSide(d.getFacing());
									if (controller != null) {
										connectedStorages.add(controller);
										controllerPos = controller.getPos2();
										setDataRaw(controller.getTile().getData());
									}
								}
								/*if(!advRouters.contains(te)){
									advRouters.add((IAdvRouterTile) te);
								}
								if(advRouter == null){
									IAdvRouterTile r = (IAdvRouterTile) te;
									advRouter = r.getRouterOnSide(d.getFacing());
									connectedStorages.add(advRouter);
								}*/
							} else if (te instanceof IDuctModule<?>) {
								// Do nothing
							} else if (te instanceof IGridDevice && !connectedStorages.contains(te)) {
								try {
									final IGridDevice<StorageNetworkGrid> griddevice = (IGridDevice<StorageNetworkGrid>) te;
									if (griddevice.isValidConnection(d.getFacing()) && griddevice.getGrid().getClass() == getClass())
										if (!denseGrid) {
											traversingStorages.add((IGridDevice<StorageNetworkGrid>) te);
										} else {
											/*addDenseC(griddevice);
												griddevice.getGrid().addDenseC(griddevice);
												denseConnections.add(griddevice);*/
										}
								} catch (ClassCastException e) {
									// Do nothing
								}
							}
						}
					}
					first++;
				}
			}
		}
		// if(p != null)p.startSection(connectedStorages.size() + "");
		// if(p != null)p.startSection("dataTransfer");
		if (masterOld != null && (!masterOld.isValid() || master.getPos2().equals(masterOld.getPos2()))) {
			NBTTagCompound tag = masterOld.getGridData();
			if (tag != null)
				setData(importFromNBT(tag).getData());
		}
		if (denseGrid)
			denseGrid = controllerPos != null;
		// if(p != null)p.endStartSection("dataTransfer2");
		if (((oldData == getData() || (controllerOld != null) || routerOld != null) && (controller == null || router == null)/* && router == null/* && advRouter == null*/) ||
				/*((oldData == getData() || (routerOld != null)) && controller == null && router == null && advRouter == null) ||*/
				/*((oldData == getData() || (advRouterOld != null)) && controller == null && advRouter == null) || */(!denseGrid && denseGridOld))
			setDataRaw(new StorageData());
		if (!denseGrid && router != null && router != routerOld)
			setDataRaw(router.getGrid().getData());
		if (controller != null)
			setDataRaw(controller.getGrid().getData());
		// if(advRouter != null)setDataRaw(advRouter.getGrid().getData());
		if (denseGrid && router != null)
			router.getTile().setData(getData());
		for (int i = 0;i < routers.size();i++)
			controllers.addAll(routers.get(i).getData().controllers);
		for (int i = 0;i < controllers.size();i++)
			if (!getData().controllers.contains(controllers.get(i)))
				getData().controllers.add(controllers.get(i));
		if (denseGrid)
			for (int i = 0;i < routers.size();i++)
				routers.get(i).setData(getData());
		// if(p != null)p.endStartSection("updateListeners");
		master.setGrid(this);
		master.getGrid().onForceUpdateDone();
		List<IGridUpdateListener> listeners = new ArrayList<>();
		for (IGridDevice<StorageNetworkGrid> storage : connectedStorages) {
			// if(p != null)p.startSection("setMaster");
			storage.setMaster(master, connectedStorages.size());
			// if(p != null)p.endSection();
			if (storage instanceof IGridUpdateListener) {
				// if(p !=
				// null)p.startSection(storage.getClass().getSimpleName());
				IGridUpdateListener l = ((IGridUpdateListener) storage);
				l.onGridReload();
				listeners.add(l);
				// if(p != null)p.endSection();
			}
		}
		// if(p != null)p.endStartSection("postUpdateListeners");
		for (IGridUpdateListener l : listeners) {
			l.onGridPostReload();
			if (l instanceof IChannelLoadListener) {
				updateListeners.add((IChannelLoadListener) l);
			}
		}
		// if(p != null)p.endSection();
		this.parts.addAll(connectedStorages);
		// if(p != null)p.endSection();
		//
		loadParts();
		isInvalid = false;
		// if(p != null)p.endSection();
	}

	private boolean isInvalid;

	@Override
	public void invalidate() {
		if (isInvalid)
			return;
		for (int i = 0;i < parts.size();i++) {
			IGridDevice<StorageNetworkGrid> part = parts.get(i);
			if (part instanceof IPowerDrain) {
				getData().powerDrain.remove(part);
			}
			if (part instanceof IDevice) {
				getData().channelDevices.remove(part);
			}
		}
		getData().devices.removeAll(parts);
		getData().invalidate(this);
		isInvalid = true;
	}

	public int getChannel() {
		return devices.size();
	}

	public int getCh() {
		return ch;
	}

	public void loadParts() {
		devices = new ArrayList<>();
		for (int i = 0;i < parts.size();i++) {
			IGridDevice<StorageNetworkGrid> part = parts.get(i);
			boolean contains = getData().devices.contains(part);
			if (!contains) {
				getData().devices.add(part);
				if (part instanceof IPowerDrain) {
					getData().powerDrain.add((IPowerDrain) part);
				}
			}
			if (part instanceof IDevice) {
				IDevice d = (IDevice) part;
				if (!contains)
					getData().channelDevices.add(d);
				devices.add(d);
			}
		}
		Collections.sort(getData().powerDrain, POWER_SORTER);
		for (int i = 0;i < updateListeners.size();i++) {
			updateListeners.get(i).onPartsUpdate();
		}
		loadChannels();
	}

	public ItemStack pullStack(ItemStack stack) {
		return pullStack(stack, true, true, false);
	}

	public ItemStack pullStack(ItemStack stack, boolean checkMeta, boolean checkNBT, boolean checkMod) {
		ItemStack retStack = ItemStack.EMPTY;
		for (int i = 0;i < getData().inventories.size();i++) {
			List<StoredItemStack> stacks = getData().inventories.get(i).getStacks(InventoryCache.class);
			for (int j = 0;j < stacks.size();j++) {
				if (TomsModUtils.areItemStacksEqual(stack, stacks.get(j).getStack(), checkMeta, checkNBT, checkMod)) {
					if (retStack.isEmpty()) {
						ICraftable c = getData().inventories.get(i).pullStack(new StoredItemStack(stack, stack.getCount()), stack.getCount());
						retStack = c != null && c instanceof StoredItemStack ? ((StoredItemStack) c).getActualStack() : ItemStack.EMPTY;
					} else {
						ICraftable c = getData().inventories.get(i).pullStack(new StoredItemStack(stack, stack.getCount() - retStack.getCount()), stack.getCount() - retStack.getCount());
						retStack.grow((int) (c != null && c instanceof StoredItemStack ? ((StoredItemStack) c).getQuantity() : 0));
					}
					if (retStack.getCount() == stack.getCount())
						return retStack;
				}
			}
		}
		return retStack;
	}

	public IStorageInventory getInventory() {
		return getData().storageInv;
	}

	public void addDenseC(IRouterTile e) {
		if (!denseC.contains(e))
			denseC.add(e);
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public static interface IGridEnergyStorage extends IEnergyStorage, IPrioritized {
		@Override
		public double extractEnergy(double maxExtract, boolean simulate);

		@Override
		public double getEnergyStored();

		@Override
		public long getMaxEnergyStored();

		@Override
		public int getPriority();

		@Override
		public boolean hasEnergy();

		@Override
		public boolean isFull();

		@Override
		public double receiveEnergy(double maxReceive, boolean simulate);
	}

	public static interface IPrioritized {
		int getPriority();
	}

	public static class PriorityComparator implements Comparator<IPrioritized> {

		@Override
		public int compare(IPrioritized in1, IPrioritized in2) {
			int priority1 = in1.getPriority();
			int priority2 = in2.getPriority();
			return priority1 < priority2 ? 1 : priority1 == priority2 ? 0 : -1;
		}
	}

	public static class ItemStackComparator {
		ItemStack stack;
		boolean matchStackSize;

		public ItemStackComparator(ItemStack stack) {
			this.stack = stack;
			matchStackSize = true;
		}

		public ItemStackComparator(ItemStack stack, boolean matchStackSize) {
			this.stack = stack;
			this.matchStackSize = matchStackSize;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (!(other instanceof ItemStack))
				return false;
			ItemStack o = (ItemStack) other;
			return TomsModUtils.areItemStacksEqualOreDict(stack, o, true, true, false, true) && (stack.getCount() <= o.getCount() || !matchStackSize);
		}
	}

	public static interface ICraftingController extends IGridUpdateListener {
		boolean hasJob();

		int getMaxOperations();

		int getMaxMemory();

		void queueCrafing(AutoCraftingHandler.CalculatedCrafting crafting);

		<T extends ICraftable> T onStackInput(T stack);
	}

	public static interface IStorageTerminalGui {
		void openCraftingReport(ItemStack stack, int amount, boolean show);

		void sendCrafting(int cpuId, boolean doCraft);

		GuiScreen getScreen();

		void drawAsBackground(float partialTicks);

		boolean isTall();

		boolean isWide();

		void sendDisplayMode(boolean wide, boolean tall);

		void setDisplayMode(boolean wide, boolean tall);
	}

	public static interface ICraftingRecipeContainer {
		void setRecipe(ItemStack to, AutoCraftingHandler.ISavedCraftingRecipe recipe);

		AutoCraftingHandler.SavedCraftingRecipe getRecipe(ItemStack from);
	}

	public static interface IPowerDrain extends IGridDevice<StorageNetworkGrid> {
		double getPowerDrained();

		void setActive(NetworkState state);

		int getPriority();
	}

	public static interface IDevice extends IGridDevice<StorageNetworkGrid>, IPowerDrain {
		int getProcessingPower();

		int getMemoryUsage();
	}

	public static interface IGridInputListener {
		<T extends ICraftable> T onStackInput(T stack);
	}

	public static interface ICraftingPatternListener {
		boolean pushRecipe(ICraftable[] recipe, boolean doPush);

		double receiveEnergy(double maxReceive, boolean simulate);
	}

	@SideOnly(Side.CLIENT)
	public static interface ICraftingReportScreen {
		void renderItemInGui(ItemStack stack, int x, int y, int mouseX, int mouseY, int color, String... extraInfo);

		void drawHoveringText(List<String> hovering, int mouseX, int mouseY);

		float getZLevel();

		FontRenderer getFontRenderer();
	}

	public static class CraftingPatternProperties {
		public boolean storedOnly = false, useContainerItems = false;
		// public boolean timed = false;
		public int time = 1;

		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			tag.setInteger("time", time);
			tag.setBoolean("storedOnly", storedOnly);
			tag.setBoolean("useContainerItems", useContainerItems);
			return tag;
		}

		public static CraftingPatternProperties loadFromNBT(NBTTagCompound tag) {
			CraftingPatternProperties p = new CraftingPatternProperties();
			p.readFromNBT(tag);
			return p;
		}

		private void readFromNBT(NBTTagCompound tag) {
			time = tag.getInteger("time");
			storedOnly = tag.getBoolean("storedOnly");
			useContainerItems = tag.getBoolean("useContainerItems");
		}

		public CraftingPatternProperties copy() {
			return loadFromNBT(writeToNBT(new NBTTagCompound()));
		}
	}

	/*public static class CraftingReportSyncThread extends Thread{
		private final CompiledCalculatedCrafting c;
		private final EntityPlayerMP player;
		public CraftingReportSyncThread(CompiledCalculatedCrafting c, EntityPlayerMP player) {
			super("Tom's Mod Auto-Crafting Client Sync Thread");
			setDaemon(true);
			this.c = c;
			this.player = player;
		}
		@Override
		public void run() {
			TMLogger.info("Syncing Crafting Data...");
			sendCompiledCraftingTo(c, player);
			TMLogger.info("Crafting Data Sent.");
		}
	}*/
	public interface IChannelUpdateListener {
		void channelUpdate();
	}

	public static class PowerDrainSorter implements Comparator<IPowerDrain> {

		@Override
		public int compare(IPowerDrain o1, IPowerDrain o2) {
			Integer o1p = o1 instanceof IPowerDrain ? o1.getPriority() : -1;
			Integer o2p = o2 instanceof IPowerDrain ? o2.getPriority() : -1;
			return o1p.compareTo(o2p);
		}

	}

	public static interface IController extends IGridDevice<StorageNetworkGrid>, IPowerDrain {
		IChannelSource getTile();

		IControllerTile getController();
	}

	public static interface IRouter extends IGridDevice<StorageNetworkGrid>, IPowerDrain {
		IChannelSource getTile();
	}

	/*public static interface IAdvRouter extends IGridDevice<StorageNetworkGrid>, IPowerDrain{
		IChannelSource getTile();
	}*/
	public static interface IChannelSource {
		StorageData getData();

		void setData(StorageData data);

		World getWorld2();

		BlockPos getPos2();

		boolean isValid();

		void markDirty2();

		void updateData(boolean load);
	}

	public static interface IControllerTile extends IChannelSource, ISecuredTileEntity {
		IController getControllerOnSide(EnumFacing side);
	}

	public static interface IRouterTile extends IChannelSource, ISecuredTileEntity {
		IRouter getRouterOnSide(EnumFacing side);

		int getChannelUsage();

		IRouter[] getRouters();
	}

	public static interface IAdvRouterTile extends IChannelSource, ISecuredTileEntity {
		IController getRouterOnSide(EnumFacing side);

		IControllerTile getController();

		void setController(IControllerTile controller);
	}

	public static class Channel {
		public List<IChannelUpdateListener> listeners = new ArrayList<>();
		public byte channel = 0;

		public boolean setValue(int ch) {
			byte o = channel;
			channel = (byte) (ch % 11);
			if (o != channel) {
				for (int i = 0;i < listeners.size();i++) {
					listeners.get(i).channelUpdate();
				}
				return true;
			}
			return false;
		}

		public byte getValue() {
			return channel;
		}

		@Override
		public String toString() {
			return "ChannelCount:" + channel;
		}
	}

	public static interface IChannelLoadListener extends IGridUpdateListener {
		void onPartsUpdate();
	}

	public static enum ControllMode {
		AE, RS;
		public static final ControllMode[] VALUES = values();
	}

	public static class Location {
		private final int x, y, z, dim, extra;

		public Location(int x, int y, int z, int dim, int extra) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.dim = dim;
			this.extra = extra;
		}

		public Location(BlockPos pos, int dim, int extra) {
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			this.dim = dim;
			this.extra = extra;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + dim;
			result = prime * result + extra;
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + z;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Location other = (Location) obj;
			if (dim != other.dim)
				return false;
			if (extra != other.extra)
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			if (z != other.z)
				return false;
			return true;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getZ() {
			return z;
		}

		public int getDim() {
			return dim;
		}

		public int getExtra() {
			return extra;
		}

		public BlockPos getPos() {
			return new BlockPos(x, y, z);
		}
	}

	public void startCrafting(ICraftable stackToCraft, Function<AutoCraftingHandler.CraftingCalculationResult, Void> apply) {
		new AutoCraftingHandler.CraftingCalculationThread(getData(), stackToCraft, apply).start();
	}

	/*public static class StackToCraft{
		public ItemStack stack;
		public int craftAmount;
		@Override
		public boolean equals(Object obj) {
			if(obj == this)return true;
			if(!(obj instanceof StackToCraft))return false;
			return super.equals(obj);
		}
	}*/
	public boolean isPowered() {
		return getData().isFullyActive();
	}

	public <T extends ICraftable> T pushStack(T stack) {
		return getInventory().pushStack(stack);
	}

	public ItemStack pushStack(ItemStack stack) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		StoredItemStack r = getInventory().pushStack(new StoredItemStack(stack, stack.getCount()));
		if (r != null) {
			ItemStack ret = r.getStack().copy();
			ret.setCount((int) r.getQuantity());
			return ret != null ? ret : ItemStack.EMPTY;
		} else
			return ItemStack.EMPTY;
	}

	public ItemStack pullStack(ItemStack stack, int max) {
		if (stack.isEmpty() || max < 1)
			return null;
		StoredItemStack r = getInventory().pullStack(new StoredItemStack(stack, stack.getCount()), max);
		if (r != null) {
			ItemStack ret = r.getStack().copy();
			ret.setCount((int) r.getQuantity());
			return ret;
		} else
			return ItemStack.EMPTY;
	}

	public IController getController() {
		return controller;
	}

	public long getNetworkLatency() {
		return 0;
	}

	public boolean isDense() {
		return denseGrid && !denseC.isEmpty();
	}

	public List<IRouterTile> getDenseC() {
		return denseC;
	}

	public BlockPos getControllerPos() {
		return controllerPos;
	}

	public static String serializeIP(int x, int y, int z, int dimension, int i) {
		byte l = (byte) MathHelper.clamp(i, 0, 15);
		if (x < 0)
			l |= 1 << 4;
		if (z < 0)
			l |= 1 << 5;
		if (dimension < 0)
			l |= 1 << 6;
		return Math.abs(x) + "." + y + "." + Math.abs(z) + "." + l + "." + dimension;
	}

	public static int[] deserializeIP(String ip) {
		String[] s = ip.split(".");
		if (s.length == 5) {
			try {
				int[] nums = Arrays.stream(s).mapToInt(Integer::parseInt).toArray();
				int l = nums[3];
				nums[3] = l & 0xF;
				if ((l & (1 << 4)) > 0)
					nums[0] = -nums[0];
				if ((l & (1 << 5)) > 0)
					nums[2] = -nums[2];
				if ((l & (1 << 6)) > 0)
					nums[4] = -nums[4];
				return nums;
			} catch (Exception e) {
				return new int[]{0, 0, 0, -1, 0};
			}
		}
		return new int[]{0, 0, 0, -1, 0};
	}
	/*public static void main(String[] args) {
		System.out.println("Test1");
		long[] diff = new long[1000];
		for(int j = 0;j<diff.length;j++){
			long time = System.currentTimeMillis();
			for(int i = 0;i<10000;i++){
				serializeIP(0, 0, 0, 0, 0);
			}
			diff[j] = System.currentTimeMillis() - time;
		}
		System.out.println(MathHelper.average(diff));
		String ip = serializeIP(0, 0, 0, 0, 0);
		System.out.println("Test2");
		diff = new long[diff.length];
		for(int j = 0;j<diff.length;j++){
			long time = System.currentTimeMillis();
			for(int i = 0;i<10000;i++){
				deserializeIP(ip);
			}
			diff[j] = System.currentTimeMillis() - time;
		}
		System.out.println(MathHelper.average(diff));
	}*/
}
