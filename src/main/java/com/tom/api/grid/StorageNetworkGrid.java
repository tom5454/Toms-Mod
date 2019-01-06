package com.tom.api.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Function;

import com.tom.api.grid.StorageNetworkGrid.Data;
import com.tom.api.inventory.IStorageInventory;
import com.tom.api.inventory.StoredItemStack;
import com.tom.api.tileentity.ISecuredTileEntity;
import com.tom.lib.api.IValidationChecker;
import com.tom.lib.api.energy.IEnergyStorage;
import com.tom.lib.api.grid.GridBase;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.lib.api.grid.IGridUpdateListener;
import com.tom.storage.handler.AutoCraftingHandler;
import com.tom.storage.handler.ICraftable;
import com.tom.storage.handler.InventoryCache;
import com.tom.storage.handler.NetworkState;
import com.tom.storage.handler.StorageData;
import com.tom.util.TomsModUtils;

public class StorageNetworkGrid extends GridBase<Data, StorageNetworkGrid> {
	private static final Comparator<IPowerDrain> POWER_SORTER = (o1, o2) -> {
		Integer o1p = o1 instanceof IPowerDrain ? o1.getPriority() : -1;
		Integer o2p = o2 instanceof IPowerDrain ? o2.getPriority() : -1;
		return o1p.compareTo(o2p);
	};
	public static final Comparator<IPrioritized> PRIORITY_COMP = (in1, in2) -> {
		int priority1 = in1.getPriority();
		int priority2 = in2.getPriority();
		return priority1 < priority2 ? 1 : priority1 == priority2 ? 0 : -1;
	};
	public static final Object[] DUMMY = new Object[]{new Object()};
	private Data data = new Data();
	private List<IDevice> devices = new ArrayList<>();
	private List<IChannelLoadListener> updateListeners = new ArrayList<>();
	public StorageNetworkGrid.Channel channel = new StorageNetworkGrid.Channel();
	private NetworkState state = NetworkState.OFF;
	private boolean active = true;
	private int ch;
	private final boolean dense;
	public StorageNetworkGrid() {
		this(false);
	}
	public StorageNetworkGrid(boolean dense) {
		this.dense = dense;
	}
	public static class Data {
		public Supplier<StorageData> dataSupplier;
		public boolean local;
		public NBTTagCompound tag;
	}

	@Override
	public Data getData() {
		return data;
	}

	@Override
	public void updateGrid(World world, IGridDevice<StorageNetworkGrid> master) {
		if(data.local){
			StorageData d = data.dataSupplier.get();
			if(d != null)d.update();
		}
		if(world.getTotalWorldTime() % 5 == 0)loadParts();
	}

	@Override
	public void setData(Data data) {
		this.data = data;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		if(data.local){
			StorageData d = data.dataSupplier.get();
			if(d != null)d.writeToNBT(tag);
		}
	}
	@Override
	public void forceUpdateGrid(IBlockAccess world, IGridDevice<StorageNetworkGrid> thisT) {
		super.forceUpdateGrid(world, thisT);
		if(data.dataSupplier == null){
			data.local = true;
			StorageData d = new StorageData();
			if(data.tag != null)d.readFromNBT(data.tag);
			data.dataSupplier = () -> d;
		}else{
			data.local = false;
			data.tag = null;
		}
		loadParts();
	}
	private void loadChannels() {
		int size = devices.size();
		if (!active || ch > 3 || !getSData().showChannels()) {
			NetworkState s = getSData().hasEnergy() ? NetworkState.POWERED_ONLY : NetworkState.OFF;
			for (int i = 0;i < size;i++) {
				devices.get(i).setActive(s);
			}
			state = s;
			channel.setValue(0);
		} else {
			channel.setValue(size > 8 ? 10 : size);
			state = getSData().networkState == NetworkState.LOADING_CHANNELS ? NetworkState.LOADING_CHANNELS : size <= 8 && ch < 1 ? NetworkState.ACTIVE : (size <= 16 || ch == 1 ? NetworkState.CHANNEL_OVERLOAD_1x : (size <= 24 || ch == 2 ? NetworkState.CHANNEL_OVERLOAD_2x : (size <= 32 || ch == 3 ? NetworkState.CHANNEL_OVERLOAD_3x : NetworkState.POWERED_ONLY)));
			for (int i = 0;i < size;i++) {
				devices.get(i).setActive(state);
			}
		}
	}
	public StorageData getSData() {
		if(data.dataSupplier == null){
			data.local = true;
			StorageData d = new StorageData();
			if(data.tag != null)d.readFromNBT(data.tag);
			data.dataSupplier = () -> d;
			return d;
		}
		StorageData d = data.dataSupplier.get();
		return d;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}
	private void loadParts() {
		devices = new ArrayList<>();
		for (int i = 0;i < parts.size();i++) {
			IGridDevice<?> part = parts.get(i);
			boolean contains = getSData().devices.contains(part);
			if (!contains) {
				getSData().devices.add(part);
				if (part instanceof IPowerDrain) {
					getSData().powerDrain.add((IPowerDrain) part);
				}
			}
			if (part instanceof IDevice) {
				IDevice d = (IDevice) part;
				if (!contains)
					getSData().channelDevices.add(d);
				devices.add(d);
			}
		}
		Collections.sort(getSData().powerDrain, POWER_SORTER);
		for (int i = 0;i < updateListeners.size();i++) {
			updateListeners.get(i).onPartsUpdate();
		}
		loadChannels();
	}

	public IStorageInventory getInventory() {
		return getSData().storageInv;
	}
	public void startCrafting(ICraftable stackToCraft, Function<AutoCraftingHandler.CraftingCalculationResult, Void> apply) {
		new AutoCraftingHandler.CraftingCalculationThread(getSData(), stackToCraft, apply).start();
	}
	public boolean isPowered() {
		return getSData().isFullyActive();
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
			return ItemStack.EMPTY;
		StoredItemStack r = getInventory().pullStack(new StoredItemStack(stack, stack.getCount()), max);
		if (r != null) {
			ItemStack ret = r.getStack().copy();
			ret.setCount((int) r.getQuantity());
			return ret;
		} else
			return ItemStack.EMPTY;
	}
	public ItemStack pullStack(ItemStack stack) {
		return pullStack(stack, true, true, false);
	}

	public ItemStack pullStack(ItemStack stack, boolean checkMeta, boolean checkNBT, boolean checkMod) {
		ItemStack retStack = ItemStack.EMPTY;
		for (int i = 0;i < getSData().inventories.size();i++) {
			List<StoredItemStack> stacks = getSData().inventories.get(i).getStacks(InventoryCache.class);
			for (int j = 0;j < stacks.size();j++) {
				if (TomsModUtils.areItemStacksEqual(stack, stacks.get(j).getStack(), checkMeta, checkNBT, checkMod)) {
					if (retStack.isEmpty()) {
						ICraftable c = getSData().inventories.get(i).pullStack(new StoredItemStack(stack, stack.getCount()), stack.getCount());
						retStack = c != null && c instanceof StoredItemStack ? ((StoredItemStack) c).getActualStack() : ItemStack.EMPTY;
					} else {
						ICraftable c = getSData().inventories.get(i).pullStack(new StoredItemStack(stack, stack.getCount() - retStack.getCount()), stack.getCount() - retStack.getCount());
						retStack.grow((int) (c != null && c instanceof StoredItemStack ? ((StoredItemStack) c).getQuantity() : 0));
					}
					if (retStack.getCount() == stack.getCount())
						return retStack;
				}
			}
		}
		return retStack;
	}

	@Override
	public StorageNetworkGrid importFromNBT(NBTTagCompound tag) {
		data.tag = tag.copy();
		return this;
	}

	@Override
	public Object[] getExtra() {
		return dense ? DUMMY : null;
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
	public static enum SearchType {
		AUTO_DEF("search_auto", false, false, true),
		STANDARD("search_std", false, false, false),
		AUTO_KEEP("search_auto_keep", false, true, true),
		STANDARD_KEEP("search_std_keep", false, true, false),
		AUTO_SYNC("search_auto_sync", true, false, true),
		STANDARD_SYNC("search_std_sync", true, false, false),
		AUTO_KEEP_SYNC("search_auto_keep_sync", true, true, true),
		STANDARD_KEEP_SYNC("search_std_keep_sync", true, false, false),
		;
		public static final SearchType[] VALUES = values();
		public final String loc;
		public final boolean jei;
		public final boolean keep;
		public final boolean defSelected;

		private SearchType(String loc, boolean jei, boolean keep, boolean defSelected) {
			this.loc = loc;
			this.jei = jei;
			this.keep = keep;
			this.defSelected = defSelected;
		}


		public static SearchType get(int index) {
			return VALUES[MathHelper.abs(index % VALUES.length)];
		}
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
	public interface IChannelUpdateListener {
		void channelUpdate();
	}
	public static interface ICraftingController extends IGridUpdateListener, IValidationChecker {
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

	public static interface IGridInputListener extends IValidationChecker {
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
	public static interface IChannelSource {
		StorageData getData();

		void setData(StorageData data);

		World getWorld2();

		BlockPos getPos2();

		boolean isValid();

		void markDirty2();

		void updateData(boolean load);
	}
	public static interface IControllerTile extends ISecuredTileEntity, IChannelSource, IValidationChecker {
	}
	public static interface IAdvRouterTile extends ISecuredTileEntity {
		IControllerTile getController();
		void setController(IControllerTile controller);
		boolean isValid();
		BlockPos getPos2();
	}
}
