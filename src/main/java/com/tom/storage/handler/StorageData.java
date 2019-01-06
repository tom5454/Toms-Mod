package com.tom.storage.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import com.tom.api.grid.StorageNetworkGrid;
import com.tom.api.grid.StorageNetworkGrid.IControllerTile;
import com.tom.api.grid.StorageNetworkGrid.ICraftingController;
import com.tom.api.grid.StorageNetworkGrid.IDevice;
import com.tom.api.grid.StorageNetworkGrid.IGridEnergyStorage;
import com.tom.api.grid.StorageNetworkGrid.IGridInputListener;
import com.tom.api.grid.StorageNetworkGrid.IPowerDrain;
import com.tom.api.inventory.IStorageInventory;
import com.tom.lib.api.IValidationChecker;
import com.tom.lib.api.energy.IEnergyStorage;
import com.tom.lib.api.grid.IGridDevice;
import com.tom.util.Storage;
import com.tom.util.TMLogger;
import com.tom.util.TomsModUtils;

public class StorageData implements IEnergyStorage {
	public List<IStorageInventory> inventories = new ArrayList<>();
	protected PowerCache powerCache;
	protected List<AutoCraftingHandler.ICraftingHandler<?>> craftingHandlerList = new ArrayList<>();
	// protected Map<Location, AutoCraftingHandler.ICraftingHandler<?>>
	// craftingHandlerMap = new HashMap<>();
	protected List<ICraftingController> craftingControllerList = new ArrayList<>();
	protected List<IGridInputListener> inputListeners = new ArrayList<>();
	public List<StorageNetworkGrid> grids = new ArrayList<>();
	protected Map<String, Storage<StorageData>> networks = new HashMap<>();
	// private StorageSystemProperties properties = new
	// DefaultStorageSystemProperties();
	protected boolean isVPN = false, isRealVPN = false;

	// private int bootTime = 20;
	public StorageData() {
		powerCache = new PowerCache();
		cache = CacheRegistry.createNetworkCache(this);
	}

	public Storage<StorageData> getVPN(String name) {
		return !isRealVPN ? networks.get(name) : null;
	}

	public Storage<StorageData> createPowerWrappedVPN(String nameIn, PowerCache power) {
		String name = "." + nameIn;
		if (networks.containsKey(name)) {
			return networks.get(name);
		} else {
			StorageData d = new StorageData();
			d.isVPN = true;
			Storage<StorageData> s = new Storage<>(d);
			networks.put(name, s);
			return s;
		}
	}

	public IStorageInventory storageInv = new IStorageInventory() {

		@Override
		public <T extends ICraftable> T pushStack(T stack) {
			if (stack == null || !stack.hasQuantity())
				return null;
			if (hasEnergy() && extractEnergy(0.1D, false) == 0.1D) {
				for (int i = 0;i < craftingControllerList.size();i++) {
					if (craftingControllerList.get(i) != null) {
						stack = craftingControllerList.get(i).onStackInput(stack);
					} else {
						TMLogger.bigWarn("Crafting Controller List Contains a ~~NULL~~ instance!!! This SHOULDN'T BE POSSIBLE!");
					}
					if (stack == null)
						return null;
				}
				for (int i = 0;i < inputListeners.size();i++) {
					if (inputListeners.get(i) != null) {
						stack = inputListeners.get(i).onStackInput(stack);
					} else {
						TMLogger.bigWarn("Input Listener List Contains a ~~NULL~~ instance!!! This SHOULDN'T BE POSSIBLE!");
					}
					if (stack == null)
						return null;
				}
				return pushStack0(stack);
			} else
				return stack;
		}

		private <T extends ICraftable> T pushStack0(T stack) {
			for (int i = 0;i < inventories.size();i++) {
				stack = inventories.get(i).pushStack(stack);
				if (stack == null || !stack.hasQuantity())
					return null;
			}
			return stack;
		}

		@Override
		public <T extends ICraftable> T pullStack(T stack, long max) {
			if (hasEnergy()) {
				if (stack == null)
					return null;
				T retStack = null;
				for (int i = 0;i < inventories.size();i++) {
					List<T> stacks = inventories.get(i).getStacks(CacheRegistry.getCacheClassFor(stack));
					for (int j = 0;j < stacks.size();j++) {
						if (stack.isEqual(stacks.get(j))) {
							// System.out.println("pull");
							if (retStack == null) {
								long maxExtractable = Math.min(Math.min(stack.getQuantity(), max), stack.getMaxStackSize());
								long stackSizeMultipier = 64 / stack.getMaxStackSize();
								double energyExtracted = extractEnergy((stackSizeMultipier / 10D) * maxExtractable, true);
								double d1 = energyExtracted / (stackSizeMultipier / 10D);
								maxExtractable = Math.min(maxExtractable, MathHelper.floor(d1));
								if (maxExtractable == 0)
									return null;
								extractEnergy(energyExtracted, false);
								retStack = inventories.get(i).pullStack(stack, maxExtractable);
							} else {
								long maxExtractable = Math.min(Math.min(stack.getQuantity(), max), stack.getMaxStackSize()) - retStack.getQuantity();
								long stackSizeMultipier = 64 / stack.getMaxStackSize();
								double energyExtracted = extractEnergy((stackSizeMultipier / 10D) * maxExtractable, true);
								double d1 = energyExtracted / (stackSizeMultipier / 10D);
								maxExtractable = Math.min(maxExtractable, MathHelper.floor(d1));
								if (maxExtractable == 0)
									return null;
								extractEnergy(energyExtracted, false);
								T c = inventories.get(i).pullStack(stack, maxExtractable);
								if (c != null)
									retStack.add(c);
							}
							if (retStack != null && retStack.getQuantity() == Math.min(Math.min(stack.getQuantity(), max), stack.getMaxStackSize()))
								return retStack;
						}
					}
				}
				return retStack;
			} else
				return null;
		}

		@Override
		public int getPriority() {
			return 0;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends ICraftable, C extends ICache<T>> List<T> getStacks(Class<C> cache) {
			List<T> list = new ArrayList<>();
			if (hasEnergy()) {
				for (int i = 0;i < inventories.size();i++) {
					IStorageInventory inv = inventories.get(i);
					List<T> stacks = inv.getStacks(cache);
					for (int j = 0;j < stacks.size();j++) {
						T stack = stacks.get(j);
						if (stack != null) {
							stack = (T) stack.copy();
							boolean added = false;
							for (int k = 0;k < list.size();k++) {
								if (list.get(k).equals(stack)) {
									list.get(k).add(stack);
									added = true;
									break;
								}
							}
							if (!added)
								list.add(stack);
						}
					}
				}
			}
			// list.sort(comparator);
			return list;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends ICraftable, C extends ICache<T>> List<T> getCraftableStacks(Class<C> cache) {
			List<T> list = new ArrayList<>();
			if (hasEnergy()) {
				for (int i = 0;i < craftingHandlerList.size();i++) {
					AutoCraftingHandler.ICraftingHandler<?> handler = craftingHandlerList.get(i);
					if (handler.getCraftableCacheClass() == cache) {
						AutoCraftingHandler.ICraftingHandler<T> handler2 = (AutoCraftingHandler.ICraftingHandler<T>) handler;
						List<AutoCraftingHandler.ICraftingRecipe<T>> recipes = handler2.getRecipes();
						for (int j = 0;j < recipes.size();j++) {
							List<T> outputStacks = recipes.get(j).getOutputs();
							for (int k = 0;k < outputStacks.size();k++) {
								T ss = outputStacks.get(k);
								if (ss != null) {
									boolean found = false;
									ss = (T) ss.copy();
									for (int l = 0;l < list.size();l++) {
										T stack = list.get(l);
										if (stack.isEqual(ss)) {
											found = true;
											break;
										}
									}
									if (!found)
										list.add((T) ss.copy());
								}
							}
						}
					}
				}
			}
			return list;
		}

		@Override
		public StorageNetworkGrid getGrid() {
			return null;
		}

		@Override
		public long getStorageValue() {
			return inventories.stream().mapToLong(IStorageInventory::getStorageValue).sum();
		}
		boolean valid = true;
		@Override
		public void saveAndInvalidate() {
			save();
			valid = false;
		}

		@Override
		public boolean isValid() {
			return valid;
		}
	};
	private NetworkCache cache;
	public List<IGridDevice<?>> devices = new ArrayList<>();
	public List<IPowerDrain> powerDrain = new ArrayList<>();
	public List<IDevice> channelDevices = new ArrayList<>();
	public List<IControllerTile> controllers = new ArrayList<>();
	public NetworkState networkState = NetworkState.ACTIVE;

	public void writeToNBT(NBTTagCompound tag) {
		powerCache.writeToNBT(tag);
	}

	public void readFromNBT(NBTTagCompound tag) {
		powerCache.readFromNBT(tag);
	}

	public void addInventory(IStorageInventory inventory) {
		if (!inventories.contains(inventory)) {
			inventories.add(inventory);
			Collections.sort(inventories, com.tom.api.grid.StorageNetworkGrid.PRIORITY_COMP);
		}
	}

	/*@Override
	public int getSizeInventory() {
		int size = 0;
		for(int i = 0;i<inventories.size();i++){
			size += inventories.get(i).getSizeInventory();
		}
		return size;
	}
	@Override
	public ItemStack getStackInSlot(int index) {
		int currentS = 0;
		for(int i = 0;i<inventories.size();i++){
			if(currentS <= index && (currentS + inventories.get(i).getSizeInventory()) > index){
				return inventories.get(i).getStackInSlot(index - currentS);
			}else{
				currentS += inventories.get(i).getSizeInventory();
			}
		}
		return null;
	}
	@Override
	public ItemStack decrStackSize(int index, int count) {
		int currentS = 0;
		for(int i = 0;i<inventories.size();i++){
			if(currentS <= index && (currentS + inventories.get(i).getSizeInventory()) > index){
				return inventories.get(i).decrStackSize(index - currentS, count);
			}else{
				currentS += inventories.get(i).getSizeInventory();
			}
		}
		return null;
	}
	@Override
	public ItemStack removeStackFromSlot(int index) {
		int currentS = 0;
		for(int i = 0;i<inventories.size();i++){
			if(currentS <= index && (currentS + inventories.get(i).getSizeInventory()) > index){
				return inventories.get(i).removeStackFromSlot(index - currentS);
			}else{
				currentS += inventories.get(i).getSizeInventory();
			}
		}
		return null;
	}
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		int currentS = 0;
		for(int i = 0;i<inventories.size();i++){
			if(currentS <= index && (currentS + inventories.get(i).getSizeInventory()) > index){
				inventories.get(i).setInventorySlotContents(index - currentS, stack);
				return;
			}else{
				currentS += inventories.get(i).getSizeInventory();
			}
		}
	}
	@Override
	public void markDirty() {
		for(int i = 0;i<inventories.size();i++){
			inventories.get(i).markDirty();
		}
	}
	@Override
	public void openInventory(EntityPlayer player) {
		for(int i = 0;i<inventories.size();i++){
			inventories.get(i).openInventory(player);
		}
	}
	@Override
	public void closeInventory(EntityPlayer player) {
		for(int i = 0;i<inventories.size();i++){
			inventories.get(i).closeInventory(player);
		}
	}
	@Override
	public void clear() {
		for(int i = 0;i<inventories.size();i++){
			inventories.get(i).clear();
		}
	}
	@Override
	public String getName() {
		return "";
	}
	@Override
	public boolean hasCustomName() {
		return false;
	}
	@Override
	public ITextComponent getDisplayName() {
		return null;
	}
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		int currentS = 0;
		for(int i = 0;i<inventories.size();i++){
			if(currentS <= index && (currentS + inventories.get(i).getSizeInventory()) > index){
				return inventories.get(i).isItemValidForSlot(i, stack);
			}else{
				currentS += inventories.get(i).getSizeInventory();
			}
		}
		return false;
	}
	@Override
	public int getField(int id) {
		return 0;
	}
	@Override
	public void setField(int id, int value) {

	}
	@Override
	public int getFieldCount() {
		return 0;
	}*/
	public void removeInventory(IStorageInventory inventory) {
		if (inventories.contains(inventory)) {
			inventories.remove(inventory);
			Collections.sort(inventories, com.tom.api.grid.StorageNetworkGrid.PRIORITY_COMP);
		}
	}

	public void addCraftingHandler(AutoCraftingHandler.ICraftingHandler<? extends ICraftable> data) {
		if (!craftingHandlerList.contains(data)) {
			craftingHandlerList.add(data);
			// craftingHandlerMap.put(new Location(data.getPos2(),
			// data.getDim(), data.getExtraData()), data);
			Collections.sort(craftingHandlerList, com.tom.api.grid.StorageNetworkGrid.PRIORITY_COMP);
		}
	}

	public void removeCraftingHandler(AutoCraftingHandler.ICraftingHandler<? extends ICraftable> data) {
		if (craftingHandlerList.contains(data)) {
			craftingHandlerList.remove(data);
			// craftingHandlerMap.remove(new Location(data.getPos2(),
			// data.getDim(), data.getExtraData()));
			Collections.sort(craftingHandlerList, com.tom.api.grid.StorageNetworkGrid.PRIORITY_COMP);
		}
	}

	public AutoCraftingHandler.CalculatedCrafting calculateCrafting(ICraftable stackToCraft) throws AutoCraftingHandler.TooComplexCraftingException {
		List<AutoCraftingHandler.ICraftingRecipe<? extends ICraftable>> recipes = new ArrayList<>();
		for (int i = 0;i < craftingHandlerList.size();i++) {
			recipes.addAll(craftingHandlerList.get(i).getRecipes());
		}
		/*List<ItemStack> storedStacks = new ArrayList<ItemStack>();
		for(int i = 0;i<inventories.size();i++){
			IStorageInv inv = inventories.get(i);
			for(int j = 0;j<inv.getSizeInventory();j++){
				ItemStack stack = inv.getStackInSlot(j);
				if(stack != null){
					storedStacks.add(stack);
				}
			}
		}*/
		return AutoCraftingHandler.calculateCrafting(recipes, stackToCraft, cache.createStored());
	}

	public void addCraftingController(ICraftingController data) {
		if (!craftingControllerList.contains(data)) {
			craftingControllerList.add(data);
		}
		// addInputListener(data);
	}

	public void removeCraftingController(ICraftingController data) {
		if (craftingControllerList.contains(data)) {
			craftingControllerList.remove(data);
		}
		// removeInputListener(data);
	}

	public void addInputListener(IGridInputListener data) {
		if (!inputListeners.contains(data)) {
			inputListeners.add(data);
		}
	}

	public void removeInputListener(ICraftingController data) {
		if (inputListeners.contains(data)) {
			inputListeners.remove(data);
		}
	}

	public void queueCrafting(ICraftable stackToCraft, EntityPlayer queuedBy, int cpuId) {
		try {
			AutoCraftingHandler.CalculatedCrafting crafting = calculateCrafting(stackToCraft);
			// List<StoredItemStack> storedStacks = storageInv.getStacks();
			boolean containsAll = true;
			List<ICraftable> missingStacks = new ArrayList<>();
			NetworkCache cache = this.cache.createStored();
			/*for(int i = 0;i<crafting.requiredStacks.size();i++){
			ItemStack stack = crafting.requiredStacks.get(i);
			StoredItemStack storedStack = new StoredItemStack(stack, stack.stackSize);
			if(!storedStacks.contains(new StoredItemStackComparator(storedStack))){
				containsAll = false;
				if(missingStacks.contains(storedStack)){
					for(int j = 0;j<missingStacks.size();j++){
						StoredItemStack s = missingStacks.get(j);
						if(s.equals(storedStack)){
							s.itemCount += storedStack.itemCount;
							break;
						}
					}
				}else{
					missingStacks.add(storedStack);
				}
			}
			}*/
			for (int i = 0;i < crafting.requiredStacks.size();i++) {
				ICraftable stack = crafting.requiredStacks.get(i);
				// StoredItemStack storedStack = new StoredItemStack(stack,
				// stack.stackSize);
				/*boolean stackFound = false;
				for(int j = 0;j<storedStacks.size();j++){
				StoredItemStack storedStack = storedStacks.get(j);
				if(TomsModUtils.areItemStacksEqualOreDict(stack, storedStack.stack, true, true, false, true)){
					stackFound = true;
					if(storedStack.itemCount < stack.stackSize){
						ItemStack copiedStack = stack.copy();
						int found = Math.min(storedStack.itemCount, stack.stackSize);
						storedStack.itemCount -= found;
						stack.stackSize = found;
						copiedStack.stackSize -= found;
						if(copiedStack.stackSize < 1){
							crafting.requiredStacks.remove(i);
							copiedStack = null;
						}
						if(storedStack.itemCount < 1){
							storedStacks.remove(j);
						}
						if(copiedStack != null){
							ItemStack s = copiedStack.copy();
							s.stackSize = 1;
							StoredItemStack storedS = new StoredItemStack(s, copiedStack.stackSize);
							if(missingStacks.contains(storedS)){
								for(int k = 0;k<missingStacks.size();k++){
									StoredItemStack mStack = missingStacks.get(k);
									if(mStack.equals(storedS)){
										mStack.itemCount += storedS.itemCount;
										break;
									}
								}
							}else{
								missingStacks.add(storedS);
							}
						}
					}else break;
				}
				}
				if(!stackFound){
				ItemStack s = stack.copy();
				s.stackSize = 1;
				StoredItemStack storedS = new StoredItemStack(s, stack.stackSize);
				if(missingStacks.contains(storedS)){
					for(int k = 0;k<missingStacks.size();k++){
						StoredItemStack mStack = missingStacks.get(k);
						if(mStack.equals(storedS)){
							mStack.itemCount += storedS.itemCount;
							break;
						}
					}
				}else{
					missingStacks.add(storedS);
				}
				crafting.requiredStacks.remove(stack);
				}*/
				stack.checkIfIngredientsAreAvailable(cache, missingStacks, crafting);
				/*if(!storedStacks.contains(new StoredItemStackComparator(storedStack))){
				//containsAll = false;
				if(missingStacks.contains(storedStack)){
					for(int j = 0;j<missingStacks.size();j++){
						StoredItemStack s = missingStacks.get(j);
						if(s.equals(storedStack)){
							s.itemCount += storedStack.itemCount;
							break;
						}
					}
				}else{
					missingStacks.add(storedStack);
				}
				crafting.requiredStacks.remove(i);
				}*/
			}
			if (crafting.recipesToCraft.isEmpty() || !containsAll || !missingStacks.isEmpty()) {
				if (queuedBy != null)
					TomsModUtils.sendNoSpamTranslate(queuedBy, new Style().setColor(TextFormatting.RED), "tomsMod.chat.craftFail", new TextComponentTranslation("tomsMod.missingItems"));
				return;
			}
			if (queuedBy != null)
				crafting.queuedBy = queuedBy.getName();
			for (int i = 0;i < craftingControllerList.size();i++) {
				ICraftingController cont = craftingControllerList.get(i);
				int maxMemory = cont.getMaxMemory();
				if (!cont.hasJob() && ((maxMemory >= crafting.memorySize && cont.getMaxOperations() >= crafting.operationCount) || maxMemory == -1)) {
					cont.queueCrafing(crafting);
					int secTime = MathHelper.ceil(crafting.time / 20D);
					ITextComponent c = crafting.mainStack.serializeTextComponent(TextFormatting.GREEN);
					if (queuedBy != null)
						TomsModUtils.sendChatTranslate(queuedBy, new Style().setColor(TextFormatting.GREEN), "tomsMod.chat.craftingStarted", c, secTime / 60, secTime % 60);
					return;
				}
			}
			if (queuedBy != null)
				TomsModUtils.sendNoSpamTranslate(queuedBy, new Style().setColor(TextFormatting.RED), "tomsMod.chat.craftFail", new TextComponentTranslation("tomsMod.notEnoughCPUsOrMemory"));
		} catch (Throwable e) {
			if (queuedBy != null)
				TomsModUtils.sendNoSpamTranslate(queuedBy, new Style().setColor(TextFormatting.RED), "tomsMod.chat.craftFail", new TextComponentTranslation(e.getMessage()));
		}
	}

	// public void update(){
	// if(bootTime > 0)bootTime--;
	// if(bootTime < 0)bootTime = 0;
	// }
	public AutoCraftingHandler.CompiledCalculatedCrafting compileCalculatedCrafting(AutoCraftingHandler.CalculatedCrafting crafting) {
		AutoCraftingHandler.CompiledCalculatedCrafting c = new AutoCraftingHandler.CompiledCalculatedCrafting();
		// NBTTagCompound tag = new NBTTagCompound();
		// NBTTagList list = new NBTTagList();
		for (int i = 0;i < craftingControllerList.size();i++) {
			ICraftingController cont = craftingControllerList.get(i);
			int maxMemory = cont.getMaxMemory();
			if (!cont.hasJob() && ((maxMemory >= crafting.memorySize && cont.getMaxOperations() >= crafting.operationCount) || maxMemory == -1)) {
				// list.appendTag(new NBTTagInt(i));
				short[] cpus = new short[c.cpus.length + 1];
				System.arraycopy(c.cpus, 0, cpus, 0, c.cpus.length);
				cpus[c.cpus.length] = (short) i;
				c.cpus = cpus;
			}
		}
		NetworkCache cache = this.cache.createStored();
		// tag.setTag("p", list);
		// List<StoredItemStack> storedStacks = storageInv.getStacks();
		// boolean containsAll = true;
		List<ICraftable> missingStacks = new ArrayList<>();
		for (int i = 0;i < crafting.requiredStacks.size();i++) {
			ICraftable stack = crafting.requiredStacks.get(i);
			// StoredItemStack storedStack = new StoredItemStack(stack,
			// stack.stackSize);
			stack.checkIfIngredientsAreAvailable(cache, missingStacks, crafting);
			/*if(!storedStacks.contains(new StoredItemStackComparator(storedStack))){
				//containsAll = false;
				if(missingStacks.contains(storedStack)){
					for(int j = 0;j<missingStacks.size();j++){
						StoredItemStack s = missingStacks.get(j);
						if(s.equals(storedStack)){
							s.itemCount += storedStack.itemCount;
							break;
						}
					}
				}else{
					missingStacks.add(storedStack);
				}
				crafting.requiredStacks.remove(i);
			}*/
		}
		crafting.writeToClientNBTPacket(c);
		// tag.setBoolean("ca", containsAll);
		// list = new NBTTagList();
		List<ICraftable> missingStacksO = new ArrayList<>();
		for (ICraftable s : missingStacks) {
			AutoCraftingHandler.addCraftableToList(s, missingStacksO);
		}
		for (ICraftable s : missingStacksO) {
			NBTTagCompound t = new NBTTagCompound();
			// s.writeToNBT(t);
			CacheRegistry.writeToNBT(s, t);
			// list.appendTag(t);
			c.missingStacks.add(t);
		}
		// tag.setTag("mi", list);
		return c;
	}

	public int getCpuID(ICraftingController c) {
		for (int i = 0;i < craftingControllerList.size();i++) {
			ICraftingController o = craftingControllerList.get(i);
			if (o == c) { return i; }
		}
		return -1;
	}
	private int counter;
	public void update() {
		counter++;
		boolean outOfPower = !networkState.isPowered();
		List<IPowerDrain> invalid = new ArrayList<>();
		boolean foundInvalid = false;
		for (int i = 0;i < powerDrain.size();i++) {
			IPowerDrain dr = powerDrain.get(i);
			if (!dr.isValid()) {
				invalid.add(dr);
				foundInvalid = true;
				continue;
			}
			if (outOfPower) {
				dr.setActive(NetworkState.OFF);
			} else {
				double drain = dr.getPowerDrained();
				double e = this.extractEnergy(drain, false);
				if (e == drain) {
					// powerDrain.get(i).setActive(networkState);
				} else {
					// powerDrain.get(i).setActive(NetworkState.OFF);
					outOfPower = true;
				}
			}
		}
		if (foundInvalid)
			powerDrain.removeAll(invalid);
		powerCache.setActive(!outOfPower);
		IValidationChecker.removeAllInvalid(inventories);
		if(counter % 5 == 0){
			IValidationChecker.removeAllInvalid(craftingControllerList);
			IValidationChecker.removeAllInvalid(craftingHandlerList);
			IValidationChecker.removeAllInvalid(devices);
			IValidationChecker.removeAllInvalid(channelDevices);
			IValidationChecker.removeAllInvalid(controllers);
			IValidationChecker.removeAllInvalid(inputListeners);
		}
	}

	public void invalidate(StorageNetworkGrid grid) {
		grids.remove(grid);
	}

	public NetworkState isActive() {
		return networkState;
	}

	public void setActive(NetworkState state) {
		this.networkState = state;
	}

	public void addEnergyStorage(IGridEnergyStorage storage) {
		powerCache.addEnergyStorage(storage);
	}

	public void removeEnergyStorage(IGridEnergyStorage storage) {
		powerCache.removeEnergyStorage(storage);
	}

	@Override
	public double receiveEnergy(double maxReceive, boolean simulate) {
		return powerCache.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public double extractEnergy(double maxExtract, boolean simulate) {
		return powerCache.extractEnergy(maxExtract, simulate);
	}

	@Override
	public double getEnergyStored() {
		return powerCache.getEnergyStored();
	}

	@Override
	public long getMaxEnergyStored() {
		return powerCache.getMaxEnergyStored();
	}

	@Override
	public boolean isFull() {
		return powerCache.isFull();
	}

	@Override
	public boolean hasEnergy() {
		return powerCache.hasEnergy();
	}

	@Override
	public double getMaxExtract() {
		return powerCache.getMaxExtract();
	}

	@Override
	public double getMaxReceive() {
		return powerCache.getMaxReceive();
	}

	public PowerCache getPowerCache() {
		return powerCache;
	}

	public void setPowerCache(PowerCache powerCache) {
		this.powerCache = powerCache;
	}

	public boolean isFullyActive() {
		return hasEnergy() && networkState.fullyActive();
	}

	public boolean showChannels() {
		return hasEnergy() && networkState.showChannels();
	}

	public BlockPos getSecurityStationPos() {
		return controllers.size() > 0 ? controllers.get(0).getSecurityStationPos() : null;
	}
	/*public ICraftingHandler<?> getCraftingHandler(BlockPos pos, int dim, int extra){
		return craftingHandlerMap.get(new Location(pos, dim, extra));
	}*/
}