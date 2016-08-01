package com.tom.storage.multipart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.energy.IEnergyStorage;
import com.tom.api.grid.GridBase;
import com.tom.api.grid.GridEnergyStorage;
import com.tom.api.grid.IGridDevice;
import com.tom.api.grid.IGridUpdateListener;
import com.tom.api.inventory.IStorageInventory;
import com.tom.api.inventory.IStorageInventory.IStorageInv;
import com.tom.api.inventory.StoredItemStack;
import com.tom.api.tileentity.IGuiTile;
import com.tom.apis.EmptyEntry;
import com.tom.apis.Function;
import com.tom.apis.TMLogger;
import com.tom.apis.TomsModUtils;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageCraftingReportSync;
import com.tom.network.messages.MessageCraftingReportSync.MessageType;
import com.tom.network.messages.MessageNBT;
import com.tom.storage.multipart.StorageNetworkGrid.StorageData;

public class StorageNetworkGrid extends GridBase<StorageData, StorageNetworkGrid> {
	public static class StorageData implements IInventory, IEnergyStorage{
		private List<IStorageInv> inventories = new ArrayList<IStorageInv>();
		private List<IGridEnergyStorage> energyStorages;
		private GridEnergyStorage energy;
		private List<IStorageData> storageDataList = new ArrayList<IStorageData>();
		private List<ICraftingHandler<?>> craftingHandlerList = new ArrayList<ICraftingHandler<?>>();
		private List<ICraftingController> craftingControllerList = new ArrayList<ICraftingController>();
		private List<IGridInputListener> inputListeners = new ArrayList<IGridInputListener>();
		//private int bootTime = 20;
		public StorageData() {
			energy = new GridEnergyStorage(50, 0);
			energyStorages = new ArrayList<IGridEnergyStorage>();
			this.energyStorages.add(energy);
			cache = CacheRegistry.createNetworkCache(this);
		}
		private IStorageInventory storageInv = new IStorageInventory() {

			@Override
			public ItemStack pushStack(ItemStack stack) {
				if(stack == null || stack.stackSize < 1)return null;
				if(hasEnergy() && extractEnergy(0.1D, false) == 0.1D){
					for(int i = 0;i<craftingControllerList.size();i++){
						if(craftingControllerList.get(i) != null){
							stack = craftingControllerList.get(i).onStackInput(stack);
						}else{
							TMLogger.bigWarn("Crafting Controller List Contains a ~~NULL~~ instance!!! This SHOULDN'T BE POSSIBLE!");
						}
						if(stack == null)return null;
					}
					for(int i = 0;i<inputListeners.size();i++){
						if(inputListeners.get(i) != null){
							stack = inputListeners.get(i).onStackInput(stack);
						}else{
							TMLogger.bigWarn("Input Listener List Contains a ~~NULL~~ instance!!! This SHOULDN'T BE POSSIBLE!");
						}
						if(stack == null)return null;
					}
					return TileEntityHopper.putStackInInventoryAllSlots(StorageData.this, stack, null);
				}
				else return stack;
			}

			@Override
			public ItemStack pullStack(StoredItemStack stack, int max) {
				if(hasEnergy()){
					if(stack == null)return null;
					ItemStack retStack = null;
					for(int i = 0;i<getSizeInventory();i++){
						if(TomsModUtils.areItemStacksEqual(stack.stack, getStackInSlot(i), true, true, false)){
							//System.out.println("pull");
							if(retStack == null){
								int maxExtractable = Math.min(Math.min(stack.itemCount, max), stack.stack.getMaxStackSize());
								int stackSizeMultipier = 64 / stack.stack.getMaxStackSize();
								double energyExtracted = extractEnergy((stackSizeMultipier/10D) * maxExtractable, true);
								double d1 = energyExtracted / (stackSizeMultipier / 10D);
								maxExtractable = Math.min(maxExtractable, MathHelper.floor_double(d1));
								if(maxExtractable == 0)return null;
								extractEnergy(energyExtracted, false);
								retStack = decrStackSize(i, maxExtractable);
							}else{
								int maxExtractable = Math.min(Math.min(stack.itemCount, max), stack.stack.getMaxStackSize()) - retStack.stackSize;
								int stackSizeMultipier = 64 / stack.stack.getMaxStackSize();
								double energyExtracted = extractEnergy((stackSizeMultipier/10D) * maxExtractable, true);
								double d1 = energyExtracted / (stackSizeMultipier / 10D);
								maxExtractable = Math.min(maxExtractable, MathHelper.floor_double(d1));
								if(maxExtractable == 0)return null;
								extractEnergy(energyExtracted, false);
								retStack.stackSize += decrStackSize(i, maxExtractable).stackSize;
							}
							if(retStack != null && retStack.stackSize == Math.min(Math.min(stack.itemCount, max), stack.stack.getMaxStackSize()))
								return retStack;
						}
					}
					return retStack;
				}else return null;
			}

			@Override
			public List<StoredItemStack> getStacks() {
				List<StoredItemStack> list = new ArrayList<StoredItemStack>();
				if(hasEnergy()){
					for(int i = 0;i<inventories.size();i++){
						IStorageInv inv = inventories.get(i);
						for(int j = 0;j<inv.getSizeInventory();j++){
							ItemStack stack = inv.getStackInSlot(j);
							if(stack != null){
								StoredItemStack storedStack = new StoredItemStack(stack, stack.stackSize);
								boolean added = false;
								for(int k = 0;k<list.size();k++){
									if(list.get(k).equals(storedStack)){
										list.get(k).itemCount += storedStack.itemCount;
										added = true;
										break;
									}
								}
								if(!added)list.add(storedStack);
							}
						}
					}
				}
				//list.sort(comparator);
				return list;
			}

			@SuppressWarnings("unchecked")
			@Override
			public List<ItemStack> getCraftableStacks() {
				List<ItemStack> list = new ArrayList<ItemStack>();
				if(hasEnergy()){
					for(int i = 0;i<craftingHandlerList.size();i++){
						ICraftingHandler<?> handler = craftingHandlerList.get(i);
						if(handler.getCraftableClass() == StoredItemStack.class){
							ICraftingHandler<StoredItemStack> handler2 = (ICraftingHandler<StoredItemStack>) handler;
							List<ICraftingRecipe<StoredItemStack>> recipes = handler2.getRecipes();
							for(int j = 0;j<recipes.size();j++){
								List<StoredItemStack> outputStacks = recipes.get(j).getOutputs();
								for(int k = 0;k<outputStacks.size();k++){
									StoredItemStack ss = outputStacks.get(k);
									if(ss != null){
										ItemStack s = ss.stack;
										boolean found = false;
										for(int l = 0;l < list.size();l++){
											ItemStack stack = list.get(l);
											if(TomsModUtils.areItemStacksEqual(stack, s, true, true, false)){
												found = true;
												break;
											}
										}
										if(!found)list.add(s);
									}
								}
							}
						}
					}
				}
				return list;
			}
		};
		private NetworkCache cache;
		public void writeToNBT(NBTTagCompound tag){
			energy.writeToNBT(tag);
		}
		public void readFromNBT(NBTTagCompound tag){
			energy.readFromNBT(tag);
		}
		public void addInventory(IStorageInv inventory){
			if(!inventories.contains(inventory)){
				inventories.add(inventory);
				Collections.sort(inventories, new PriorityComparator());
			}
		}
		public void addEnergyStorage(IGridEnergyStorage storage){
			if(!energyStorages.contains(storage)){
				energyStorages.add(storage);
				Collections.sort(energyStorages, new PriorityComparator());
			}
		}
		@Override
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
		}
		@Override
		public double receiveEnergy(double maxReceive, boolean simulate) {
			double received = 0;
			for(int i = 0;i<energyStorages.size();i++){
				received += energyStorages.get(i).receiveEnergy(maxReceive - received, simulate);
				if(maxReceive == received)return received;
			}
			return received;
		}
		@Override
		public double extractEnergy(double maxExtract, boolean simulate) {
			double extracted = 0;
			for(int i = 0;i<energyStorages.size();i++){
				extracted += energyStorages.get(i).extractEnergy(maxExtract - extracted, simulate);
				if(maxExtract == extracted)return extracted;
			}
			return extracted;
		}
		@Override
		public double getEnergyStored() {
			double stored = 0;
			for(int i = 0;i<energyStorages.size();i++){
				stored += energyStorages.get(i).getEnergyStored();
			}
			return stored;
		}
		@Override
		public int getMaxEnergyStored() {
			int maxStored = 0;
			for(int i = 0;i<energyStorages.size();i++){
				maxStored += energyStorages.get(i).getMaxEnergyStored();
			}
			return maxStored;
		}
		@Override
		public boolean isFull() {
			double stored = 0;
			int maxStored = 0;
			for(int i = 0;i<energyStorages.size();i++){
				IEnergyStorage s = energyStorages.get(i);
				stored += s.getEnergyStored();
				maxStored += s.getMaxEnergyStored();
			}
			return stored == maxStored;
		}
		@Override
		public boolean hasEnergy() {
			return this.getEnergyStored() > 0;
		}
		public List<IGridEnergyStorage> getEnergyStorages(){
			return energyStorages;
		}
		public List<IStorageData> getStorageDataList(){
			return storageDataList;
		}
		public void addStorageData(IStorageData data){
			if(!storageDataList.contains(data)){
				storageDataList.add(data);
				IStorageInv i = data.getInventory();
				if(i != null)addInventory(i);
				ICraftingHandler<? extends ICraftable> c = data.getCraftingHandler();
				if(c != null)addCraftingHandler(c);
			}
		}
		public void removeStorageData(IStorageData data){
			if(storageDataList.contains(data)){
				storageDataList.remove(data);
				IStorageInv i = data.getInventory();
				if(i != null)removeInventory(i);
				ICraftingHandler<? extends ICraftable> c = data.getCraftingHandler();
				if(c != null)removeCraftingHandler(c);
			}
		}
		public void removeInventory(IStorageInv inventory){
			if(inventories.contains(inventory)){
				inventories.remove(inventory);
				Collections.sort(inventories, new PriorityComparator());
			}
		}
		public void addCraftingHandler(ICraftingHandler<? extends ICraftable> data){
			if(!craftingHandlerList.contains(data)){
				craftingHandlerList.add(data);
				Collections.sort(craftingHandlerList, new PriorityComparator());
			}
		}
		public void removeCraftingHandler(ICraftingHandler<? extends ICraftable> data){
			if(craftingHandlerList.contains(data)){
				craftingHandlerList.remove(data);
				Collections.sort(craftingHandlerList, new PriorityComparator());
			}
		}
		public void removeEnergyStorage(IGridEnergyStorage storage){
			if(energyStorages.contains(storage)){
				energyStorages.remove(storage);
				Collections.sort(energyStorages, new PriorityComparator());
			}
		}
		public CalculatedCrafting calculateCrafting(ICraftable stackToCraft, int amount) throws TooComplexCraftingException{
			List<ICraftingRecipe<? extends ICraftable>> recipes = new ArrayList<ICraftingRecipe<? extends ICraftable>>();
			for(int i = 0;i<craftingHandlerList.size();i++){
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
			return StorageNetworkGrid.calculateCrafting(recipes, stackToCraft, cache.createStored(), amount);
		}
		public void addCraftingController(ICraftingController data){
			if(!craftingControllerList.contains(data)){
				craftingControllerList.add(data);
			}
			//addInputListener(data);
		}
		public void removeCraftingController(ICraftingController data){
			if(craftingControllerList.contains(data)){
				craftingControllerList.remove(data);
			}
			//removeInputListener(data);
		}
		public void addInputListener(IGridInputListener data){
			if(!inputListeners.contains(data)){
				inputListeners.add(data);
			}
		}
		public void removeInputListener(ICraftingController data){
			if(inputListeners.contains(data)){
				inputListeners.remove(data);
			}
		}
		public void queueCrafting(ICraftable stackToCraft, int amount, EntityPlayer queuedBy, int cpuId){
			try {
				CalculatedCrafting crafting = calculateCrafting(stackToCraft, amount);
				//List<StoredItemStack> storedStacks = storageInv.getStacks();
				boolean containsAll = true;
				List<ICraftable> missingStacks = new ArrayList<ICraftable>();
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
				for(int i = 0;i<crafting.requiredStacks.size();i++){
					ICraftable stack = crafting.requiredStacks.get(i);
					//StoredItemStack storedStack = new StoredItemStack(stack, stack.stackSize);
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
				if(crafting.recipesToCraft.isEmpty() || !containsAll || !missingStacks.isEmpty()){
					if(queuedBy != null)TomsModUtils.sendNoSpamTranslate(queuedBy, new Style().setColor(TextFormatting.RED), "tomsMod.chat.craftFail", new TextComponentTranslation("tomsMod.missingItems"));
					return;
				}
				if(queuedBy != null)crafting.queuedBy = queuedBy.getName();
				for(int i = 0;i<craftingControllerList.size();i++){
					ICraftingController cont = craftingControllerList.get(i);
					int maxMemory = cont.getMaxMemory();
					if(!cont.hasJob() && ((maxMemory >= crafting.memorySize && cont.getMaxOperations() >= crafting.operationCount) || maxMemory == -1)){
						cont.queueCrafing(crafting);
						int secTime = MathHelper.ceiling_double_int(crafting.time / 20D);
						ITextComponent c = crafting.mainStack.serializeTextComponent(TextFormatting.GREEN);
						if(queuedBy != null)TomsModUtils.sendChatTranslate(queuedBy, new Style().setColor(TextFormatting.GREEN), "tomsMod.chat.craftingStarted", c, secTime / 60, secTime % 60);
						return;
					}
				}
				if(queuedBy != null)TomsModUtils.sendNoSpamTranslate(queuedBy, new Style().setColor(TextFormatting.RED), "tomsMod.chat.craftFail", new TextComponentTranslation("tomsMod.notEnoughCPUsOrMemory"));
			} catch (Throwable e) {
				if(queuedBy != null)TomsModUtils.sendNoSpamTranslate(queuedBy, new Style().setColor(TextFormatting.RED), "tomsMod.chat.craftFail", new TextComponentTranslation(e.getMessage()));
			}
		}
		//public void update(){
		//if(bootTime > 0)bootTime--;
		//if(bootTime < 0)bootTime = 0;
		//}
		public CompiledCalculatedCrafting compileCalculatedCrafting(CalculatedCrafting crafting){
			CompiledCalculatedCrafting c = new CompiledCalculatedCrafting();
			//NBTTagCompound tag = new NBTTagCompound();
			//NBTTagList list = new NBTTagList();
			for(int i = 0;i<craftingControllerList.size();i++){
				ICraftingController cont = craftingControllerList.get(i);
				int maxMemory = cont.getMaxMemory();
				if(!cont.hasJob() && ((maxMemory >= crafting.memorySize && cont.getMaxOperations() >= crafting.operationCount) || maxMemory == -1)){
					//list.appendTag(new NBTTagInt(i));
					short[] cpus = new short[c.cpus.length + 1];
					System.arraycopy(c.cpus, 0, cpus, 0, c.cpus.length);
					cpus[c.cpus.length] = (short) i;
					c.cpus = cpus;
				}
			}
			NetworkCache cache = this.cache.createStored();
			//tag.setTag("p", list);
			//List<StoredItemStack> storedStacks = storageInv.getStacks();
			//boolean containsAll = true;
			List<ICraftable> missingStacks = new ArrayList<ICraftable>();
			for(int i = 0;i<crafting.requiredStacks.size();i++){
				ICraftable stack = crafting.requiredStacks.get(i);
				//StoredItemStack storedStack = new StoredItemStack(stack, stack.stackSize);
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
			//tag.setBoolean("ca", containsAll);
			//list = new NBTTagList();
			for(ICraftable s : missingStacks){
				NBTTagCompound t = new NBTTagCompound();
				//s.writeToNBT(t);
				CacheRegistry.writeToNBT(s, t);
				//list.appendTag(t);
				c.missingStacks.add(t);
			}
			//tag.setTag("mi", list);
			return c;
		}
		public int getCpuID(ICraftingController c){
			for(int i = 0;i<craftingControllerList.size();i++){
				ICraftingController o = craftingControllerList.get(i);
				if(o == c){
					return i;
				}
			}
			return -1;
		}
	}
	private StorageData data = new StorageData();
	@Override
	public StorageData getData() {
		return data;
	}

	@Override
	public StorageNetworkGrid importFromNBT(NBTTagCompound tag) {
		data.readFromNBT(tag);
		return this;
	}

	@Override
	public void updateGrid(World world, IGridDevice<StorageNetworkGrid> master) {
		//data.update();
	}

	@Override
	public void setData(StorageData data) {
		this.data = data;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		data.writeToNBT(tag);
	}
	public ItemStack pullStack(ItemStack stack){
		return pullStack(stack, true, true, false);
	}
	public ItemStack pullStack(ItemStack stack, boolean checkMeta, boolean checkNBT, boolean checkMod){
		ItemStack retStack = null;
		for(int i = 0;i<data.getSizeInventory();i++){
			if(TomsModUtils.areItemStacksEqual(stack, data.getStackInSlot(i), checkMeta, checkNBT, checkMod)){
				if(retStack == null){
					retStack = data.decrStackSize(i, stack.stackSize);
				}else{
					retStack.stackSize += data.decrStackSize(i, stack.stackSize - retStack.stackSize).stackSize;
				}
				if(retStack.stackSize == stack.stackSize)return retStack;
			}
		}
		return retStack;
	}
	public IStorageInventory getInventory(){
		return data.storageInv;
	}
	public static class StorageItemStackComparatorAmount implements IReversableStorageItemStackComparator{
		public boolean reversed;
		public StorageItemStackComparatorAmount(boolean reversed) {
			this.reversed = reversed;
		}
		@Override
		public int compare(StoredItemStack in1, StoredItemStack in2) {
			int c = in2.itemCount > in1.itemCount ? 1 : (in1.itemCount == in2.itemCount ? in1.stack.getUnlocalizedName().compareTo(in2.stack.getUnlocalizedName()) : -1);
			return this.reversed ? -c : c;
		}
		@Override
		public void setReverse(boolean reverse) {
			this.reversed = reverse;
		}
		@Override
		public Comparator<StoredItemStack> getReversed() {
			return new StorageItemStackComparatorAmount(!reversed);
		}
	}
	public static class StorageItemStackComparatorName implements IReversableStorageItemStackComparator{
		public boolean reversed;
		public StorageItemStackComparatorName(boolean reversed) {
			this.reversed = reversed;
		}
		@Override
		public int compare(StoredItemStack in1, StoredItemStack in2) {
			int c = in1.stack.getDisplayName().compareTo(in2.stack.getDisplayName());
			return this.reversed ? -c : c;
		}
		@Override
		public void setReverse(boolean reverse) {
			this.reversed = reverse;
		}
		@Override
		public Comparator<StoredItemStack> getReversed() {
			return new StorageItemStackComparatorName(!reversed);
		}
	}
	public static class StorageItemStackComparatorMod implements IReversableStorageItemStackComparator{
		public boolean reversed;
		public StorageItemStackComparatorMod(boolean reversed) {
			this.reversed = reversed;
		}
		@Override
		public int compare(StoredItemStack in1, StoredItemStack in2) {
			String modname1 = in1.stack.getItem().delegate.name().getResourceDomain();
			String modname2 = in2.stack.getItem().delegate.name().getResourceDomain();
			int c2 = modname1.compareTo(modname2);
			int c = c2 == 0 ? in2.itemCount > in1.itemCount ? 1 : (in1.itemCount == in2.itemCount ? in1.stack.getUnlocalizedName().compareTo(in2.stack.getUnlocalizedName()) : -1) : c2;
			return this.reversed ? -c : c;
		}
		@Override
		public void setReverse(boolean reverse) {
			this.reversed = reverse;
		}
		@Override
		public Comparator<StoredItemStack> getReversed() {
			return new StorageItemStackComparatorMod(!reversed);
		}
	}
	public static enum StorageItemStackSorting{
		AMOUNT(new StorageItemStackComparatorAmount(false)),
		NAME(new StorageItemStackComparatorName(false)),
		MOD(new StorageItemStackComparatorMod(false)),
		;
		public static final StorageItemStackSorting[] VALUES = values();
		private final Comparator<StoredItemStack> comparator, comparatorRev;

		private StorageItemStackSorting(IReversableStorageItemStackComparator comparator) {
			this.comparator = comparator;
			this.comparatorRev = comparator.getReversed();
		}

		public Comparator<StoredItemStack> getComparator(boolean reversed) {
			return reversed ? comparatorRev : comparator;
		}
		public static StorageItemStackSorting get(int index){
			return VALUES[MathHelper.abs_int(index % VALUES.length)];
		}
	}
	private static interface IReversableStorageItemStackComparator extends Comparator<StoredItemStack>{
		void setReverse(boolean reverse);
		Comparator<StoredItemStack> getReversed();
	}
	public static interface IGridEnergyStorage extends IEnergyStorage, IPrioritized{
		@Override
		public double extractEnergy(double maxExtract, boolean simulate);
		@Override
		public double getEnergyStored();
		@Override
		public int getMaxEnergyStored();
		@Override
		public int getPriority();
		@Override
		public boolean hasEnergy();
		@Override
		public boolean isFull();
		@Override
		public double receiveEnergy(double maxReceive, boolean simulate);
	}
	/*public static class AutoUpdaterThread implements Runnable{
		private final Queue<Runnable> scheduledTasks = Queues.<Runnable>newArrayDeque();
		private static boolean started;
		private static Thread thread;
		private static AutoUpdaterThread theAutoUpdaterThread = new AutoUpdaterThread();
		@Override
		public void run() {

		}
		public static void start(){
			if(!started){
				started = true;
				thread = new Thread(theAutoUpdaterThread, "Tom's Mod Storage System Update Thread");
			}
		}
		public static AutoUpdaterThread getAutoUpdaterThread() {
			return theAutoUpdaterThread;
		}
	}*/
	public static interface IStorageData {
		IStorageInv getInventory();
		void update(ItemStack stack, IInventory inv, World world, int priority);
		ICraftingHandler<?> getCraftingHandler();
	}
	public static enum CellLight{
		GREEN, ORANGE, RED
	}
	public static interface ICraftingRecipe<T extends ICraftable>{
		boolean isStoredOnly();
		List<T> getOutputs();
		List<T> getInputs();
		int getTime();
		boolean execute(boolean doExecute);
		void writeToNBT(NBTTagCompound tag);
		//void addTime(int time);
		boolean useContainerItems();
	}
	public static interface ICraftingHandler<T extends ICraftable> extends IPrioritized, IGridUpdateListener{
		List<ICraftingRecipe<T>> getRecipes();
		boolean executeRecipe(ICraftingRecipe<T> recipe, boolean doExecute);
		Class<T> getCraftableClass();
	}
	public static interface IPrioritized{
		int getPriority();
	}
	public static class PriorityComparator implements Comparator<IPrioritized>{

		@Override
		public int compare(IPrioritized in1, IPrioritized in2) {
			int priority1 = in1.getPriority();
			int priority2 = in2.getPriority();
			return priority1 < priority2 ? 1 : priority1 == priority2 ? 0 : -1;
		}
	}
	public static class CalculatedCrafting{
		public Stack<RecipeToCraft> recipesToCraft;
		public int time;
		public int operationCount;
		public int memorySize;
		public List<ICraftable> requiredStacks;
		public List<ICraftable> secondaryOutList;
		public ICraftable mainStack;
		public String queuedBy;
		public int totalTime = 0;
		public List<ICraftable> pullStacks(StorageNetworkGrid grid){
			List<ICraftable> pulledList = new ArrayList<ICraftable>();
			for(int i = 0;i<requiredStacks.size();i++){
				/*ItemStack pulled = grid.pullStack(requiredStacks.get(i));
				if(pulled != null){
					pulledList.add(pulled);
				}*/
				requiredStacks.get(i).pullFromGrid(grid, pulledList);
			}
			return pulledList;
		}
		public void writeToClientNBTPacket(CompiledCalculatedCrafting c){
			c.time = time;
			//NBTTagList list = new NBTTagList();
			for(int i = 0;i<recipesToCraft.size();i++){
				NBTTagCompound t = new NBTTagCompound();
				recipesToCraft.get(i).writeToClientNBTPacket(t);
				//list.appendTag(t);
				c.recipes.add(t);
			}
			//tag.setTag("c", list);
			//list = new NBTTagList();
			for(int i = 0;i<requiredStacks.size();i++){
				ICraftable o = requiredStacks.get(i);
				NBTTagCompound t = new NBTTagCompound();
				/*int count = o.stackSize;
				o = o.copy();
				o.stackSize = 1;
				o.writeToNBT(t);
				t.removeTag("Count");
				t.setInteger("Count", count);*/
				if(o.hasQuantity()){
					CacheRegistry.writeToNBT(o, t);
					//list.appendTag(t);
					c.toPull.add(t);
				}
			}
			//tag.setTag("l", list);
			NBTTagCompound t = new NBTTagCompound();
			//mainStack.writeToNBT(t);
			CacheRegistry.writeToNBT(mainStack, t);
			//t.removeTag("Count");
			//t.setInteger("Count", mainStack.itemCount);
			/*tag.setTag("m", t);
			tag.setInteger("o", operationCount);
			tag.setInteger("s", memorySize);*/
			c.main = t;
			c.opertaions = operationCount;
			c.memory = memorySize;
		}
		public static CalculatedCrafting getCraftingFromClientNBT(NBTTagCompound tag){
			CalculatedCrafting c = new CalculatedCrafting();
			c.time = tag.getInteger("t");
			c.recipesToCraft = new Stack<RecipeToCraft>();
			c.requiredStacks = new ArrayList<ICraftable>();
			NBTTagList list = tag.getTagList("c", 10);
			for(int i = 0;i<list.tagCount();i++){
				NBTTagCompound t = list.getCompoundTagAt(i);
				c.recipesToCraft.add(RecipeToCraft.loadFromClientNBT(t));
			}
			list = tag.getTagList("l", 10);
			for(int i = 0;i<list.tagCount();i++){
				NBTTagCompound t = list.getCompoundTagAt(i);
				/*int count = t.getInteger("Count");
				t.removeTag("Count");
				t.setByte("Count", (byte) 1);
				ItemStack o = ItemStack.loadItemStackFromNBT(t);
				o.stackSize = count;*/
				c.requiredStacks.add(CacheRegistry.readFromNBT(t));
			}
			NBTTagCompound t = tag.getCompoundTag("m");
			//int amount = t.getInteger("Count");
			//t.removeTag("Count");
			//t.setByte("Count", (byte) 1);
			//ItemStack s = ItemStack.loadItemStackFromNBT(t);
			c.mainStack = CacheRegistry.readFromNBT(t);
			//c.mainStack = StoredItemStack.readFromNBT(t);
			c.operationCount = tag.getInteger("o");
			c.memorySize = tag.getInteger("s");
			return c;
		}
	}
	public static class RecipeToCraft{
		private ICraftingRecipe<?> recipe;
		public List<ICraftable> stackToCraftClient;
		//public int time = 0;
		public RecipeToCraft(ICraftingRecipe<?> r) {
			recipe = r;
		}
		@Override
		public boolean equals(Object other) {
			if(this == other)return true;
			if(!(other instanceof RecipeToCraft))return false;
			RecipeToCraft o = (RecipeToCraft) other;
			return recipe == o.recipe;
		}
		public boolean execute(boolean doExecute, List<ICraftable> stored){
			/*for(int i = 0;i<(executionTime - executed);i++){
				if(!recipe.execute()){
					executed += i;
					return false;
				}
			}
			executed = executionTime;*/
			List<ICraftable> inputs = getInputsIgnoreStacksize();
			//List<ItemStack> inputs2 = getInputsIgnoreStacksize();
			Map<Integer, Integer> ids = new HashMap<Integer, Integer>();
			for(int i = 0;i<stored.size();i++){
				ICraftable storedS = stored.get(i).copy();
				List<ICraftable> requiredToRemove = new ArrayList<ICraftable>();
				for(int j = 0;j<inputs.size();j++){
					ICraftable input = inputs.get(j);
					if(input.isEqual(storedS)){
						ids.put(i, input.handleSecondaryPull(storedS));
						if(!input.hasQuantity())requiredToRemove.add(input);
						break;
					}
				}
				for(int j = 0;j<requiredToRemove.size();j++){
					inputs.remove(requiredToRemove.get(j));
				}
			}
			if(inputs.isEmpty() && recipe.execute(doExecute)){
				if(doExecute){
					List<ICraftable> requiredToRemove = new ArrayList<ICraftable>();
					for(Entry<Integer, Integer> i : ids.entrySet()){
						stored.get(i.getKey()).removeQuantity(i.getValue());
						if(!stored.get(i.getKey()).hasQuantity())requiredToRemove.add(stored.get(i.getKey()));
					}
					for(int j = 0;j<requiredToRemove.size();j++){
						stored.remove(requiredToRemove.get(j));
					}
				}
				return true;
			}else
				return false;
		}
		/*@Override
		public List<ItemStack> getOutputs() {
			List<ItemStack> stackList = new ArrayList<ItemStack>();
			List<ItemStack> outputs = recipe.getOutputs();
			for(int j = 0;j<outputs.size();j++){
				ItemStack s = outputs.get(j).copy();
				int req = s.stackSize * executed;
				while(req > 0){
					int max = Math.min(s.getMaxStackSize(), req);
					req -= max;
					ItemStack s2 = s.copy();
					s2.stackSize = max;
					stackList.add(s2);
				}
			}
			return stackList;
		}
		@Override
		public List<ItemStack> getInputs() {
			List<ItemStack> stackList = new ArrayList<ItemStack>();
			List<ItemStack> inputs = recipe.getInputs();
			for(int j = 0;j<inputs.size();j++){
				ItemStack s = inputs.get(j).copy();
				int req = s.stackSize * executed;
				while(req > 0){
					int max = Math.min(s.getMaxStackSize(), req);
					req -= max;
					ItemStack s2 = s.copy();
					s2.stackSize = max;
					stackList.add(s2);
				}
			}
			return stackList;
		}*/
		public int getTime() {
			return recipe.getTime();
		}
		public List<ICraftable> getInputsIgnoreStacksize() {
			List<ICraftable> stackList = new ArrayList<ICraftable>();
			@SuppressWarnings("unchecked")
			List<ICraftable> inputs = (List<ICraftable>) recipe.getInputs();
			for(int i = 0;i<inputs.size();i++){
				ICraftable s = inputs.get(i).copy();
				boolean f = false;
				for(int j = 0;j<stackList.size();j++){
					if(s.isEqual(stackList.get(j))){
						/*int req = s.stackSize;
						ItemStack s2 = s.copy();
						s2.stackSize = req;
						stackList.add(s2);*/
						stackList.get(j).add(s);
						f = true;
					}
				}
				if(!f)stackList.add(s);
			}
			return stackList;
		}
		public List<ICraftable> getOutputsIgnoreStacksize() {
			List<ICraftable> stackList = new ArrayList<ICraftable>();
			@SuppressWarnings("unchecked")
			List<ICraftable> outputs = (List<ICraftable>) recipe.getOutputs();
			for(int i = 0;i<outputs.size();i++){
				ICraftable s = outputs.get(i).copy();
				boolean f = false;
				for(int j = 0;j<stackList.size();j++){
					if(s.isEqual(stackList.get(j))){
						/*int req = s.stackSize;
						ItemStack s2 = s.copy();
						s2.stackSize = req;
						stackList.add(s2);*/
						stackList.get(j).add(s);
						f = true;
					}
				}
				if(!f)stackList.add(s);
			}
			return stackList;
		}
		public void writeToClientNBTPacket(NBTTagCompound tag){
			NBTTagList list = new NBTTagList();
			List<ICraftable> outputs = getOutputsIgnoreStacksize();
			for(int i = 0;i<outputs.size();i++){
				ICraftable o = outputs.get(i);
				//int count = o.stackSize;
				//o = o.copy();
				//o.stackSize = 1;
				NBTTagCompound t = new NBTTagCompound();
				CacheRegistry.writeToNBT(o, t);
				//t.removeTag("Count");
				//t.setInteger("Count", count);
				list.appendTag(t);
			}
			tag.setTag("l", list);
		}
		public static RecipeToCraft loadFromClientNBT(NBTTagCompound tag){
			RecipeToCraft r = new RecipeToCraft(null);
			r.stackToCraftClient = new ArrayList<ICraftable>();
			NBTTagList list = tag.getTagList("l", 10);
			for(int i = 0;i<list.tagCount();i++){
				NBTTagCompound t = list.getCompoundTagAt(i);
				/*int count = t.getInteger("Count");
				t.removeTag("Count");
				t.setByte("Count", (byte) 1);
				ItemStack o = ItemStack.loadItemStackFromNBT(t);
				o.stackSize = count;*/
				r.stackToCraftClient.add(CacheRegistry.readFromNBT(t));
			}
			return r;
		}
		public void addRequiredStacks(List<ICraftable> requiredStacks){
			List<ICraftable> outputs = getOutputsIgnoreStacksize();
			for(int i = 0;i<outputs.size();i++){
				addCraftableToList(outputs.get(i), requiredStacks);
			}
		}
	}
	public static class StoredItemStackComparator{
		StoredItemStack stack;
		public StoredItemStackComparator(StoredItemStack stack) {
			this.stack = stack;
		}
		@Override
		public boolean equals(Object other) {
			if(this == other)return true;
			if(!(other instanceof StoredItemStack))return false;
			StoredItemStack o = (StoredItemStack) other;
			return TomsModUtils.areItemStacksEqual(stack.stack, o.stack, true, true, false) && stack.itemCount <= o.itemCount;
		}
	}
	public static class ItemStackComparator{
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
			if(this == other)return true;
			if(!(other instanceof ItemStack))return false;
			ItemStack o = (ItemStack) other;
			return TomsModUtils.areItemStacksEqualOreDict(stack, o, true, true, false, true) && (stack.stackSize <= o.stackSize || !matchStackSize);
		}
	}
	public static interface ICraftingController extends IGridUpdateListener{
		boolean hasJob();
		int getMaxOperations();
		int getMaxMemory();
		void queueCrafing(CalculatedCrafting crafting);
		ItemStack onStackInput(ItemStack stack);
	}
	public static interface IStorageTerminalGui{
		void openCraftingReport(ItemStack stack, int amount);
		void sendCrafting(int cpuId, boolean doCraft);
		GuiScreen getScreen();
	}
	public static class CalculatedClientCrafting{
		public List<ClientCraftingStack<?>> stacks;
		public ICraftable mainStack;
		public int amount, cpuId, operations, memory, time;
		public List<Integer> cpus;
		public boolean hasMissing;
	}
	public static class ClientCraftingStack<T extends ICraftable>{
		public int toCraft;
		public T mainStack;
		public int stored, missing;
		@SuppressWarnings("unchecked")
		public ClientCraftingStack<T> copy() {
			ClientCraftingStack<T> c = new ClientCraftingStack<T>();
			c.toCraft = toCraft;
			c.mainStack = (T) mainStack.copy();
			c.stored = stored;
			c.missing = missing;
			return c;
		}
		@SideOnly(Side.CLIENT)
		public void draw(int posX, int posY, int mouseX, int mouseY, ICraftingReportScreen screen, boolean isTooltip) {
			mainStack.drawEntry(this, posX, posY, mouseX, mouseY, screen, isTooltip);
		}
	}
	public static interface ISavedCraftingRecipe{
		boolean isStoredOnly();
		ICraftable[] getOutputs();
		ICraftable[] getInputs();
		int getTime();
		void setTime(int time);
		void setStoredOnly(boolean isStoredOnly);
		void setInputs(ICraftable[] stacks);
		void setOutputs(ICraftable[] stacks);
		void writeToNBT(NBTTagCompound tag);
		void readFromNBT(NBTTagCompound tag);
	}
	public static class SavedCraftingRecipe implements ISavedCraftingRecipe{
		private ICraftable[] inputs, outputs;
		private CraftingPatternProperties p;
		//private int[] lastTime;
		//private boolean timed = false;
		@Override
		public boolean isStoredOnly() {
			return p.storedOnly;
		}

		@Override
		public ICraftable[] getOutputs() {
			return outputs;
		}

		@Override
		public ICraftable[] getInputs() {
			return inputs;
		}

		@Override
		public int getTime() {
			return p.time;
		}

		@Override
		public void setTime(int time) {
			this.p.time = time;
		}

		@Override
		public void setStoredOnly(boolean isStoredOnly) {
			p.storedOnly = isStoredOnly;
		}

		@Override
		public void setInputs(ICraftable[] stacks) {
			inputs = stacks;
		}

		@Override
		public void setOutputs(ICraftable[] stacks) {
			outputs = stacks;
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			p.writeToNBT(tag);
			//tag.setIntArray("lastTime", lastTime);
			//tag.setBoolean("timed", timed);
			NBTTagList list = new NBTTagList();
			if(inputs != null && inputs.length > 0){
				for(int i = 0;i<inputs.length;i++){
					ICraftable s = inputs[i];
					if(s != null){
						NBTTagCompound t = new NBTTagCompound();
						CacheRegistry.writeToNBT(s,t);
						t.setInteger("Slot", i);
						list.appendTag(t);
					}
				}
			}
			tag.setTag("in", list);
			list = new NBTTagList();
			if(outputs != null && outputs.length > 0){
				for(int i = 0;i<outputs.length;i++){
					ICraftable s = outputs[i];
					if(s != null){
						NBTTagCompound t = new NBTTagCompound();
						CacheRegistry.writeToNBT(s,t);
						t.setInteger("Slot", i);
						list.appendTag(t);
					}
				}
			}
			tag.setTag("out", list);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			p = CraftingPatternProperties.loadFromNBT(tag);
			//lastTime = tag.getIntArray("lastTime");
			//timed = tag.getBoolean("timed");
			NBTTagList list = tag.getTagList("in", 10);
			inputs = new ICraftable[9];
			outputs = new ICraftable[3];
			for(int i = 0;i<list.tagCount();i++){
				NBTTagCompound t = list.getCompoundTagAt(i);
				int slot = t.getInteger("Slot");
				if(slot >= 0 && slot < 9){
					inputs[slot] = CacheRegistry.readFromNBT(t);
				}
			}
			list = tag.getTagList("out", 10);
			for(int i = 0;i<list.tagCount();i++){
				NBTTagCompound t = list.getCompoundTagAt(i);
				int slot = t.getInteger("Slot");
				if(slot >= 0 && slot < 3){
					outputs[slot] = CacheRegistry.readFromNBT(t);
				}
			}
		}
		public static SavedCraftingRecipe loadFromNBT(NBTTagCompound tag){
			SavedCraftingRecipe r = new SavedCraftingRecipe();
			r.readFromNBT(tag);
			return r;
		}
		public <T extends ICraftable> ICraftingRecipe<T> getRecipe(final ICraftingHandler<T> handler){
			return new ICraftingRecipe<T>() {

				@Override
				public boolean isStoredOnly() {
					return p.storedOnly;
				}

				@Override
				public int getTime() {
					//return timed ? time : TomsModUtils.average_int(lastTime);
					return p.time;
				}

				@SuppressWarnings("unchecked")
				@Override
				public List<T> getOutputs() {
					List<T> list = new ArrayList<T>();
					if(outputs != null){
						for(ICraftable a : outputs){
							if(a != null)
								list.add((T) a.copy());
						}
					}
					return list;
				}

				@SuppressWarnings("unchecked")
				@Override
				public List<T> getInputs() {
					List<T> list = new ArrayList<T>();
					if(inputs != null){
						for(ICraftable a : inputs){
							if(a != null)
								list.add((T) a.copy());
						}
					}
					return list;
				}

				@Override
				public boolean execute(boolean doExecute) {
					return handler.executeRecipe(this, doExecute);
				}

				@Override
				public void writeToNBT(NBTTagCompound tag) {
					SavedCraftingRecipe.this.writeToNBT(tag);
				}

				@Override
				public boolean useContainerItems() {
					return p.useContainerItems;
				}

				/*@Override
				public void addTime(int time) {
					lastTime = TomsModUtils.array_intAddLimit(lastTime, time, 20);
				}*/
			};
		}
		public static SavedCraftingRecipe createFromStacks(IInventory inv, IInventory resultInventory, CraftingPatternProperties properties, CraftableProperties[] props){
			SavedCraftingRecipe r = new SavedCraftingRecipe();
			if(inv.getSizeInventory() > 8 && resultInventory.getSizeInventory() > 2){
				r.inputs = new ICraftable[inv.getSizeInventory()];
				r.outputs = new ICraftable[resultInventory.getSizeInventory()];
				for(int i = 0;i<r.inputs.length;i++){
					r.inputs[i] = deserializeItemStack(inv.getStackInSlot(i), props[i]);
				}
				for(int i = 0;i<r.outputs.length;i++){
					r.outputs[i] = deserializeItemStack(resultInventory.getStackInSlot(i), props[inv.getSizeInventory() + i]);
				}
			}
			r.p = properties.copy();
			//r.timed = properties.timed;
			return r;
		}
		private static ICraftable deserializeItemStack(ItemStack stack, CraftableProperties properties){
			return stack != null ? new StoredItemStack(stack, stack.stackSize).setHasProperites(properties) : null;
		}
		@SuppressWarnings({ "rawtypes" })
		public boolean recipeEquals(ICraftingRecipe recipe){
			//List<ICraftable> stackList = recipe.getInputs();
			/*List<ICraftable> list = new ArrayList<ICraftable>();
			if(inputs != null){
				for(ICraftable a : inputs){
					if(a != null)
						list.add(a.copy());
				}
			}*/
			/*if(list.size() != stackList.size())return false;
			for(int i = 0;i<list.size();i++){
				ICraftable s1 = list.get(i);
				ICraftable s2 = stackList.get(i);
				if(!s1.isEqual(s2)){
					return false;
				}
			}*/
			/*if(inputs == null)return false;
			if(inputs.length != stackList.size())return false;
			for(int i = 0;i<stackList.size();i++){
				if(!(inputs[i] == stackList.get(i) || (inputs[i] != null && inputs[i].isEqual(stackList.get(i))))){
					return false;
				}
			}*/
			NBTTagCompound tag = new NBTTagCompound();
			recipe.writeToNBT(tag);
			NBTTagCompound tag2 = new NBTTagCompound();
			writeToNBT(tag2);
			return tag.equals(tag2);
		}
	}
	public static interface ICraftingRecipeContainer{
		void setRecipe(ItemStack to, ISavedCraftingRecipe recipe);
		SavedCraftingRecipe getRecipe(ItemStack from);
	}
	public static interface ITerminal extends IGridDevice<StorageNetworkGrid>, IGuiTile{
		int getTerminalMode();
	}
	public static interface IGridInputListener{
		ItemStack onStackInput(ItemStack stack);
	}
	public static interface ICraftingPatternListener{
		boolean pushRecipe(ICraftable[] recipe, boolean doPush);
		double receiveEnergy(double maxReceive, boolean simulate);
	}
	public static interface ICraftable{
		ICraftable copy();
		void removeQuantity(int value);
		void writeObjToNBT(NBTTagCompound t);
		void addValidRecipes(List<ICraftingRecipe<?>> recipes, List<ICraftingRecipe<?>> validRecipes);
		void pull(NetworkCache cache, List<ICraftable> requiredStacksToPull);
		boolean hasQuantity();
		boolean isEqual(ICraftable s);
		int handleSecondaryPull(ICraftable secondary);
		void setNoQuantity();
		void add(ICraftable other);
		RecipeReturnInformation useRecipe(ICraftingRecipe<?> r, NetworkCache cache, List<ICraftable> secondaryOutList, List<ICraftable> requiredStacksToPull, Stack<RecipeToCraft> recipesToCraft, Stack<ICraftable> toCraft);
		int getQuantity();
		void pullFromGrid(StorageNetworkGrid cache, List<ICraftable> pulledList);
		ITextComponent serializeTextComponent(TextFormatting color);
		void checkIfIngredientsAreAvailable(NetworkCache cache, List<ICraftable> missingStacks, CalculatedCrafting crafting);
		@SuppressWarnings("rawtypes")
		@SideOnly(Side.CLIENT)
		void drawEntry(ClientCraftingStack ccStack, int posX, int posY, int mouseX, int mouseY, ICraftingReportScreen screen, boolean isTooltip);
		ICraftable pushToGrid(StorageNetworkGrid grid);
		void setQuantity(int value);
		Class<? extends ICache<?>> getCacheClass();
		@SideOnly(Side.CLIENT)
		String serializeStringTooltip();
		boolean isFirst();
		void setFirst();
		CraftableProperties getProperties();
	}
	public static class CraftableProperties{
		public boolean useOreDict = true, useMeta = true, useNBT = false;
		public NBTTagCompound writeToNBT(NBTTagCompound tag){
			tag.setBoolean("nbt", useNBT);
			tag.setBoolean("meta", useMeta);
			tag.setBoolean("oreDict", useOreDict);
			return tag;
		}
		public void readFromNBT(NBTTagCompound tag){
			useMeta = tag.getBoolean("meta");
			useNBT = tag.getBoolean("nbt");
			useOreDict = tag.getBoolean("oreDict");
		}
	}
	public static class RecipeReturnInformation{
		public final boolean success;
		public final int time, operations, memoryUsage;
		public RecipeReturnInformation(boolean success, int time, int operations, int memoryUsage) {
			this.success = success;
			this.time = time;
			this.operations = operations;
			this.memoryUsage = memoryUsage;
		}
	}
	public static class NetworkCache{
		private Map<Class<? extends ICache<?>>, ICache<?>> cacheMap;
		private final StorageData data;
		@SuppressWarnings("unchecked")
		public <T extends ICraftable> ICache<T> getCache(Class<? extends ICache<T>> clazz){
			try{
				return (ICache<T>) cacheMap.get(clazz);
			}catch(ClassCastException e){
				return null;
			}
		}
		private NetworkCache(Map<Class<? extends ICache<?>>, ICache<?>> cacheMap, StorageData data) {
			this.cacheMap = cacheMap;
			this.data = data;
		}
		public StorageData getData() {
			return data;
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public NetworkCache createStored(){
			Map<Class<? extends ICache<?>>, ICache<?>> cacheMapOut = new HashMap<Class<? extends ICache<?>>, ICache<?>>();
			for(Entry<Class<? extends ICache<?>>, ICache<?>> i : cacheMap.entrySet()){
				cacheMapOut.put(i.getKey(), new CopiedCache(i.getValue()));
			}
			return new NetworkCache(cacheMapOut, data);
		}
	}
	public static class InventoryCache implements ICache<StoredItemStack>{
		private final StorageData data;
		public InventoryCache(StorageData data) {
			this.data = data;
		}
		@Override
		public List<StoredItemStack> getStored() {
			return data.storageInv.getStacks();
		}

		@Override
		public Class<StoredItemStack> getCraftableClass() {
			return StoredItemStack.class;
		}

		@Override
		public StoredItemStack readObjectFromNBT(NBTTagCompound tag) {
			return StoredItemStack.readFromNBT(tag);
		}

	}
	public static interface ICache<T extends ICraftable>{
		List<T> getStored();
		Class<T> getCraftableClass();
		T readObjectFromNBT(NBTTagCompound tag);
	}
	public static class CacheRegistry{
		private static final Map<Class<? extends ICache<?>>, Entry<Integer, Function<StorageData, ? extends ICache<?>>>> cacheMap = new HashMap<Class<? extends ICache<?>>, Entry<Integer, Function<StorageData, ? extends ICache<?>>>>();
		private static final String ID_NBT_NAME = "cid";

		public static <T extends ICache<?>> void registerCache(Class<T> clazz, Function<StorageData, T> constructor) {
			cacheMap.put(clazz, new EmptyEntry<Integer, Function<StorageData, ? extends ICache<?>>>(cacheMap.size(), constructor));
		}

		public static NetworkCache createNetworkCache(StorageData data){
			Map<Class<? extends ICache<?>>, ICache<?>> map = new HashMap<Class<? extends ICache<?>>, ICache<?>>();
			for(Entry<Class<? extends ICache<?>>, Entry<Integer, Function<StorageData, ? extends ICache<?>>>> i : cacheMap.entrySet()){
				//try {
				map.put(i.getKey(), i.getValue().getValue().apply(data));
				/*} catch (Exception e) {
					throw new RuntimeException("Execption occurred while constructing a Cache.", e);
				}*/
			}
			return new NetworkCache(map, data);
		}

		public static void writeToNBT(ICraftable c, NBTTagCompound tag){
			Entry<Integer, Function<StorageData, ? extends ICache<?>>> entry = cacheMap.get(c.getCacheClass());
			if(entry != null){
				int id = entry.getKey();
				c.writeObjToNBT(tag);
				tag.setInteger(ID_NBT_NAME, id);
			}
		}
		public static ICraftable readFromNBT(NBTTagCompound tag){
			int id = tag.getInteger(ID_NBT_NAME);
			for(Entry<Class<? extends ICache<?>>, Entry<Integer, Function<StorageData, ? extends ICache<?>>>> i : cacheMap.entrySet()){
				if(i.getValue().getKey().intValue() == id){
					//try {
					return i.getValue().getValue().apply(null).readObjectFromNBT(tag);
					/*} catch (Exception e) {
						throw new RuntimeException("Execption occurred while constructing a Cache.", e);
					}*/
				}
			}
			return null;
		}
		public static void init(){
			registerCache(InventoryCache.class, new Function<StorageData, InventoryCache>() {

				@Override
				public InventoryCache apply(StorageData t) {
					return new InventoryCache(t);
				}

			});
		}
	}
	@SideOnly(Side.CLIENT)
	public static interface ICraftingReportScreen{
		void renderItemInGui(ItemStack stack, int x, int y, int mouseX, int mouseY, int color, String... extraInfo);
		void drawHoveringText(List<String> hovering, int mouseX, int mouseY);
		float getZLevel();
		FontRenderer getFontRenderer();
	}
	public static class CopiedCache<T extends ICraftable> implements ICache<T>{
		private final ICache<T> original;
		private final List<T> storedList;
		private CopiedCache(ICache<T> original) {
			this.original = original;
			storedList = original.getStored();
		}
		@Override
		public List<T> getStored() {
			return storedList;
		}
		@Override
		public Class<T> getCraftableClass() {
			return original.getCraftableClass();
		}
		@Override
		public T readObjectFromNBT(NBTTagCompound tag) {
			return original.readObjectFromNBT(tag);
		}
	}
	public static class TooComplexCraftingException extends Exception{
		private static final long serialVersionUID = -947645277184685155L;
		public TooComplexCraftingException() {
			super("tomsMod.autoCraftingError");
		}
	}
	public static class CraftingPatternProperties{
		public boolean storedOnly = false, useContainerItems = false;
		//public boolean timed = false;
		public int time = 1;
		public NBTTagCompound writeToNBT(NBTTagCompound tag){
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
		public CraftingPatternProperties copy(){
			return loadFromNBT(writeToNBT(new NBTTagCompound()));
		}
	}
	public static class CompiledCalculatedCrafting{
		private NBTTagCompound main;
		private int opertaions;
		private int memory;
		private int time;
		private short[] cpus = new short[0];
		private List<NBTTagCompound> missingStacks = new ArrayList<NBTTagCompound>();
		private List<NBTTagCompound> recipes = new ArrayList<NBTTagCompound>();
		private List<NBTTagCompound> toPull = new ArrayList<NBTTagCompound>();
		public void sendTo(EntityPlayerMP player){
			sendCompiledCraftingTo(this, player);
		}
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
	public boolean isPowered(){
		return data.hasEnergy();
	}
	public boolean drainEnergy(double amount){
		return data.extractEnergy(amount, false) == amount;
	}
	public ItemStack pushStack(ItemStack stack){
		return getInventory().pushStack(stack);
	}
	private static final long maxCycles = 100000L;
	public static CalculatedCrafting calculateCrafting(List<ICraftingRecipe<?>> recipes, ICraftable stackToCraft, NetworkCache cache, int amount) throws TooComplexCraftingException{
		Stack<ICraftable> toCraft = new Stack<ICraftable>();
		//toCraft.add(stackToCraft.copy());
		ICraftable stackToCraft2 = stackToCraft.copy();
		stackToCraft2.setFirst();
		addCraftableToCraftList(stackToCraft2, toCraft);
		List<ICraftable> secondaryOutList = new ArrayList<ICraftable>();
		List<ICraftable> requiredStacksToPull = new ArrayList<ICraftable>();
		int time = 0;
		int operations = 0;
		int memoryUsage = 0;
		Stack<RecipeToCraft> recipesToCraft = new Stack<RecipeToCraft>();
		long cycles = 0L;
		while(!toCraft.isEmpty()){
			ICraftable cStack = toCraft.pop();
			List<ICraftingRecipe<?>> validRecipes = new ArrayList<ICraftingRecipe<?>>();
			cStack.addValidRecipes(recipes, validRecipes);
			if(!cStack.isFirst()){
				cStack.pull(cache, requiredStacksToPull);
			}
			cycles++;
			while(cStack.hasQuantity()){
				boolean skip = false;
				for(int i = 0;i<secondaryOutList.size();i++){
					ICraftable s = secondaryOutList.get(i);
					if(cStack.isEqual(s)){
						skip = true;
						/*int pulled = s.splitStack(Math.min(cStack.itemCount, s.stackSize)).stackSize;
						cStack.itemCount -= pulled;
						 */
						int pulled = cStack.handleSecondaryPull(s);
						memoryUsage += pulled;
						operations += 1;
						if(!s.hasQuantity()){
							secondaryOutList.remove(s);
						}
						break;
					}
				}
				cycles++;
				if(!skip){
					/*boolean found = false;
					int lastC = -1;
					int rID = 0;*/
					if(validRecipes.isEmpty()){
						//while(cStack.itemCount > 0){
						//int pulled = Math.min(cStack.itemCount, cStack.stack.getMaxStackSize());
						//cStack.itemCount -= pulled;
						ICraftable s = cStack.copy();
						//s.stackSize = cStack.itemCount;
						operations += 1;
						cStack.setNoQuantity();
						//requiredStacksToPull.add(s);
						addCraftableToList(s, requiredStacksToPull);
						break;
						//}
					}else{
						for(int i = 0;i<validRecipes.size();i++){
							ICraftingRecipe<?> r = validRecipes.get(i);
							RecipeReturnInformation info = cStack.useRecipe(r, cache, secondaryOutList, requiredStacksToPull, recipesToCraft, toCraft);
							if(info.success){
								time += info.time;
								operations += info.operations;
								memoryUsage += info.memoryUsage;
								break;
							}
						}
					}
					/*if(!found){
						if(rID == -1){
							ICraftingRecipe r = validRecipes.get(rID);
						}
					}*/
				}
				checkCycles(cycles);
			}
			checkCycles(cycles);
		}
		CalculatedCrafting c = new CalculatedCrafting();
		c.mainStack = stackToCraft;
		c.memorySize = memoryUsage;
		c.operationCount = operations;
		c.time = time;
		c.requiredStacks = requiredStacksToPull;
		c.recipesToCraft = recipesToCraft;
		c.secondaryOutList = secondaryOutList;
		return c;
	}
	private static void checkCycles(long cycles) throws TooComplexCraftingException{
		if(cycles > maxCycles)throw new TooComplexCraftingException();
	}
	public static void addStackToList(ItemStack stack, List<ItemStack> stackList){
		for(int i = 0;i<stackList.size();i++){
			ItemStack s = stackList.get(i);
			if(TomsModUtils.areItemStacksEqualOreDict(stack, s, true, true, false, true)){
				int spaceInStack = s.getMaxStackSize() - s.stackSize;
				if(spaceInStack > 0){
					s.stackSize += stack.splitStack(Math.min(spaceInStack, stack.stackSize)).stackSize;
					if(stack.stackSize < 1){
						return;
					}
				}
			}
		}
		if(stack.stackSize > 0){
			while(stack.stackSize > 0){
				ItemStack s = stack.copy();
				int spaceInStack = stack.getMaxStackSize();
				s.stackSize = stack.splitStack(Math.min(spaceInStack, stack.stackSize)).stackSize;
				stackList.add(s);
			}
		}
	}
	public static void addStackToListIgnoreStackSize(ItemStack stack, List<ItemStack> stackList){
		for(int i = 0;i<stackList.size();i++){
			ItemStack s = stackList.get(i);
			if(TomsModUtils.areItemStacksEqualOreDict(stack, s, true, true, false, true)){
				s.stackSize += stack.stackSize;
				return;
			}
		}
		/*if(stack.stackSize > 0){
			while(stack.stackSize > 0){
				ItemStack s = stack.copy();
				int spaceInStack = stack.getMaxStackSize();
				s.stackSize = stack.splitStack(Math.min(spaceInStack, stack.stackSize)).stackSize;

			}
		}*/
		stackList.add(stack.copy());
	}
	public static void addCraftableToList(ICraftable stack, List<ICraftable> stackList){
		for(int i = 0;i<stackList.size();i++){
			ICraftable s = stackList.get(i);
			if(stack.isEqual(s)){
				s.add(stack);
				return;
			}
		}
		/*if(stack.stackSize > 0){
			while(stack.stackSize > 0){
				ItemStack s = stack.copy();
				int spaceInStack = stack.getMaxStackSize();
				s.stackSize = stack.splitStack(Math.min(spaceInStack, stack.stackSize)).stackSize;

			}
		}*/
		stackList.add(stack.copy());
	}
	public static void addCraftableToCraftList(ICraftable stack, List<ICraftable> stackList){
		for(int i = 0;i<stack.getQuantity();i++){
			ICraftable c = stack.copy();
			c.setQuantity(1);
			stackList.add(c);
		}
	}
	public static CalculatedClientCrafting readCalculatedCraftingFromNBT(NBTTagCompound tag){
		CalculatedClientCrafting cc = new CalculatedClientCrafting();
		CalculatedCrafting c = CalculatedCrafting.getCraftingFromClientNBT(tag);
		List<ClientCraftingStack<?>> list = new ArrayList<ClientCraftingStack<?>>();
		for(RecipeToCraft s : c.recipesToCraft){
			for(int i = 0;i<s.stackToCraftClient.size();i++){
				addCraftableToList(s.stackToCraftClient.get(i), list, 0);
			}
		}
		for(ICraftable s : c.requiredStacks){
			if(s != null)addCraftableToList(s, list, 1);
		}
		cc.cpus = new ArrayList<Integer>();
		NBTTagList l = tag.getTagList("p", 3);
		for(int i = 0;i<l.tagCount();i++){
			cc.cpus.add(((NBTTagInt)l.get(i)).getInt());
		}
		l = tag.getTagList("mi", 10);
		boolean hasMissing = false;
		for(int i = 0;i<l.tagCount();i++){
			ICraftable s = CacheRegistry.readFromNBT(l.getCompoundTagAt(i));
			if(s != null){
				//s.stack.stackSize = s.itemCount;
				addCraftableToList(s, list, -1);
				hasMissing = true;
			}
		}
		cc.stacks = list;
		cc.mainStack = c.mainStack.copy();
		cc.amount = c.mainStack.getQuantity();
		cc.cpuId = -1;
		cc.time = c.time;
		cc.memory = c.memorySize;
		cc.operations = c.operationCount;
		cc.hasMissing = hasMissing;
		return cc;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void addCraftableToList(ICraftable stack, List<ClientCraftingStack<?>> stackList, int toStored){
		for(int i = 0;i<stackList.size();i++){
			ClientCraftingStack s = stackList.get(i);
			if(stack.isEqual(s.mainStack)){
				if(toStored == 1)s.stored += stack.getQuantity();
				else if(toStored == -1)s.missing += stack.getQuantity();
				else s.toCraft += stack.getQuantity();
				return;
			}
		}
		/*if(stack.stackSize > 0){
			while(stack.stackSize > 0){
				ItemStack s = stack.copy();
				int spaceInStack = stack.getMaxStackSize();
				s.stackSize = stack.splitStack(Math.min(spaceInStack, stack.stackSize)).stackSize;

			}
		}*/
		ClientCraftingStack s = new ClientCraftingStack();
		s.mainStack = stack.copy();
		if(toStored == 1)s.stored += stack.getQuantity();
		else if(toStored == -1)s.missing += stack.getQuantity();
		else s.toCraft += stack.getQuantity();
		stackList.add(s);
	}
	public static void addStackToListIgnoreStackSize(ItemStack stack, Stack<StoredItemStack> stackList){
		for(int i = 0;i<stackList.size();i++){
			StoredItemStack s = stackList.get(i);
			if(TomsModUtils.areItemStacksEqualOreDict(stack, s.stack, true, true, false, true)){
				s.itemCount += stack.stackSize;
				return;
			}
		}
		/*if(stack.stackSize > 0){
			while(stack.stackSize > 0){
				ItemStack s = stack.copy();
				int spaceInStack = stack.getMaxStackSize();
				s.stackSize = stack.splitStack(Math.min(spaceInStack, stack.stackSize)).stackSize;

			}
		}*/
		stackList.add(new StoredItemStack(stack, stack.stackSize));
	}
	public static void sendCompiledCraftingTo(CompiledCalculatedCrafting c, EntityPlayerMP player){
		NetworkHandler.sendTo(new MessageCraftingReportSync(c.main, c.opertaions, c.memory, c.time, c.cpus, c.missingStacks.size(), c.recipes.size(), c.toPull.size()), player);
		sendTagList(c.missingStacks, player, MessageType.MISSING);
		sendTagList(c.recipes, player, MessageType.RECIPE);
		sendTagList(c.toPull, player, MessageType.NORMAL);
	}
	private static void sendTagList(List<NBTTagCompound> tagList, EntityPlayerMP player, MessageType id){
		for(int i = 0;i<tagList.size();i++){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag("m", tagList.get(i));
			tag.setInteger("id", id.ordinal());
			tag.setBoolean("r", true);
			NetworkHandler.sendTo(new MessageNBT(tag), player);
		}
	}
}
