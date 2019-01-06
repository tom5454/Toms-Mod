package com.tom.api.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.items.IItemHandler;

import com.tom.api.grid.StorageNetworkGrid;
import com.tom.api.grid.StorageNetworkGrid.IPrioritized;
import com.tom.lib.api.IValidationChecker;
import com.tom.lib.api.grid.IGridAccess;
import com.tom.storage.handler.ICache;
import com.tom.storage.handler.ICraftable;
import com.tom.storage.handler.InventoryCache;
import com.tom.util.TomsModUtils;

public interface IStorageInventory extends IPrioritized, IGridAccess<StorageNetworkGrid>, IValidationChecker {
	<T extends ICraftable, C extends ICache<T>> List<T> getStacks(Class<C> cache);

	<T extends ICraftable> T pullStack(T stack, long max);

	<T extends ICraftable> T pushStack(T stack);

	<T extends ICraftable, C extends ICache<T>> List<T> getCraftableStacks(Class<C> cache);

	long getStorageValue();

	default void save() {
	}

	default void saveIfNeeded() {
	}
	void saveAndInvalidate();

	public static interface IUpdateable {
		void update(ItemStack stack, IItemHandler inv, int priority);
	}

	public static interface StoredItemStackFilter {
		boolean canInsert(ItemStack stack);

		boolean canExtract(ItemStack stack);

		boolean canView(ItemStack stack);
	}

	public static class FilteredStorageInventory implements IStorageInventory, IUpdateable {
		private final StorageNetworkGrid grid;
		public IItemHandler inventory;
		public int priority;
		public StoredItemStackFilter filter;
		public final EnumFacing side;

		public FilteredStorageInventory(IItemHandler inv, int priority, StoredItemStackFilter filter, EnumFacing side, StorageNetworkGrid grid) {
			this.inventory = inv;
			this.priority = priority;
			this.filter = filter;
			this.side = side;
			this.grid = grid;
		}

		/*@Override
		public String getName() {
			return inventory.getName();
		}

		@Override
		public int getSizeInventory() {
			return inventory.getSizeInventory();
		}

		@Override
		public boolean hasCustomName() {
			return inventory.hasCustomName();
		}

		@Override
		public ITextComponent getDisplayName() {
			return inventory.getDisplayName();
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			ItemStack stack = inventory.getStackInSlot(index);
			return filter.canInsert(stack) ? stack : null;
		}

		@Override
		public ItemStack decrStackSize(int index, int count) {
			return inventory.decrStackSize(index, count);
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			return inventory.removeStackFromSlot(index);
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			inventory.setInventorySlotContents(index, stack);
		}

		@Override
		public int getInventoryStackLimit() {
			return inventory.getInventoryStackLimit();
		}

		@Override
		public void markDirty() {
			inventory.markDirty();
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer player) {
			return inventory.isUseableByPlayer(player);
		}

		@Override
		public void openInventory(EntityPlayer player) {
			inventory.openInventory(player);
		}

		@Override
		public void closeInventory(EntityPlayer player) {
			inventory.closeInventory(player);
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return inventory.isItemValidForSlot(index, stack) && filter.canInsert(stack) && filter.canInsert(inventory.getStackInSlot(index)) && checkSide(index, stack);
		}

		private boolean checkSide(int slot, ItemStack stack) {
			if(inventory instanceof ISidedInventory){
				ISidedInventory sided = (ISidedInventory) inventory;
				int[] intArray = sided.getSlotsForFace(side);
				boolean found = false;
				for(int i : intArray){
					if(i == slot){
						found = true;
						break;
					}
				}
				if(found){
					return sided.canInsertItem(slot, stack, side);
				}
				return false;
			}else
				return true;
		}

		@Override
		public int getField(int id) {
			return inventory.getField(id);
		}

		@Override
		public void setField(int id, int value) {
			inventory.setField(id, value);
		}

		@Override
		public int getFieldCount() {
			return inventory.getFieldCount();
		}

		@Override
		public void clear() {
			inventory.clear();
		}

		@Override
		public boolean equals(Object arg0) {
			return inventory.equals(arg0);
		}*/

		@Override
		public void update(ItemStack stack, IItemHandler inv, int priority) {
			inventory = inv;
			this.priority = priority;
		}

		/*@Override
		public int getItemListSize() {
			int itemSize = 0;
			for(int i = 0;i<inventory.getSizeInventory();i++){
				if(inventory.getStackInSlot(i) != null)itemSize++;
			}
			return itemSize;
		}*/

		@SuppressWarnings("unchecked")
		@Override
		public <T extends ICraftable> T pullStack(T stackIn, long max) {
			if (stackIn instanceof StoredItemStack) {
				StoredItemStack stack = (StoredItemStack) stackIn;
				if (filter.canExtract(stack.getStack())) {
					StoredItemStack ret = null;
					/*if(inventory instanceof IStorageInventory){
					ISidedInventory isidedinventory = (ISidedInventory)inventory;
					int[] aint = isidedinventory.getSlotsForFace(side);
					for (int i = 0; i < aint.length && (ret == null || ret.getQuantity() < max); ++i){
						if(inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).isItemEqual(stack.getStack()) && ItemStack.areItemStackTagsEqual(stack.getStack(), inventory.getStackInSlot(i)) && canExtractItemFromSlot(inventory.getStackInSlot(i), i, side)){
							ItemStack s = inventory.extractItem(i, (int) Math.min(64, max), false);
							if(!s.isEmpty()){
								if(ret == null){
									ret = new StoredItemStack(s, s.getCount());
								}else{
									ret.removeQuantity(-s.getCount());
								}
							}
						}
					}
					}else{*/
					for (int i = 0;i < inventory.getSlots() && (ret == null || ret.getQuantity() < max);i++) {
						if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).isItemEqual(stack.getStack()) && ItemStack.areItemStackTagsEqual(stack.getStack(), inventory.getStackInSlot(i))) {
							ItemStack s = inventory.extractItem(i, (int) Math.min(64, max), false);
							if (s != null) {
								if (ret == null) {
									ret = new StoredItemStack(s, s.getCount());
								} else {
									ret.removeQuantity(-s.getCount());
								}
							}
						}
						// }
					}
					return (T) ret;
				}
			}
			return null;
		}

		/* *
		 * Can this hopper extract the specified item from the specified slot on the specified side?
		 * /
		private boolean canExtractItemFromSlot(ItemStack stack, int index, EnumFacing side)
		{
			return !(inventory instanceof ISidedInventory) || ((ISidedInventory)inventory).canExtractItem(index, stack, side);
		}*/
		@SuppressWarnings("unchecked")
		@Override
		public <T extends ICraftable> T pushStack(T stackIn) {
			if (stackIn instanceof StoredItemStack) {
				StoredItemStack stack = (StoredItemStack) stackIn;
				if (stack.hasQuantity() && filter.canInsert(stack.getStack())) {
					ItemStack sIn = stack.getStack().copy();
					int in = (int) Math.min(1024, stack.getQuantity());
					sIn.setCount(in);
					ItemStack s = TomsModUtils.putStackInInventoryAllSlots(inventory, sIn);
					if (s == null || s.getCount() < 1) {
						stack.removeQuantity(1024);
					} else {
						stack.removeQuantity(in - s.getCount());
					}
				}
				return (T) stack;
			}
			return stackIn;
		}

		@Override
		public int getPriority() {
			return priority;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends ICraftable, C extends ICache<T>> List<T> getStacks(Class<C> cache) {
			if (cache == InventoryCache.class) {
				List<StoredItemStack> ret = new ArrayList<>();
				for (int j = 0;j < inventory.getSlots();j++) {
					ItemStack stack = inventory.getStackInSlot(j);
					if (!stack.isEmpty() && filter.canView(stack)) {
						StoredItemStack storedStack = new StoredItemStack(stack, stack.getCount());
						boolean added = false;
						for (int k = 0;k < ret.size();k++) {
							if (ret.get(k).equals(storedStack)) {
								ret.get(k).add(storedStack);
								added = true;
								break;
							}
						}
						if (!added)
							ret.add(storedStack);
					}
				}
				return (List<T>) ret;
			}
			return null;
		}

		@Override
		public <T extends ICraftable, C extends ICache<T>> List<T> getCraftableStacks(Class<C> cache) {
			return null;
		}

		@Override
		public StorageNetworkGrid getGrid() {
			return grid;
		}

		@Override
		public long getStorageValue() {
			return inventory.getSlots() * 32;
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

	}

	public static class BasicFilter implements StoredItemStackFilter {
		private IFilteringInformation info;
		private IInventory filterInv;

		public BasicFilter(IInventory filterInv, IFilteringInformation info) {
			this.info = info;
			this.filterInv = filterInv;
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			Mode mode = info.getMode();
			if (mode == Mode.EXTRACT_ONLY || mode == Mode.VIEW_ONLY || mode == Mode.DISABLED)
				return false;
			boolean whitelist = info.isWhiteList();
			boolean allNull = true;
			for (int i = 0;i < filterInv.getSizeInventory();i++) {
				ItemStack valid = filterInv.getStackInSlot(i);
				if (!valid.isEmpty()) {
					allNull = false;
					if (TomsModUtils.areItemStacksEqual(stack, valid, info.useMeta(), info.useNBT(), info.useMod())) {
						if (whitelist)
							return true;
						else
							return false;
					}
				}
			}
			if (allNull)
				return true;
			return !whitelist;
		}

		public static interface IFilteringInformation {
			boolean isWhiteList();

			boolean useMeta();

			boolean useNBT();

			boolean useMod();

			boolean canViewAll();

			Mode getMode();
		}

		public static enum Mode {
			IO, INSERT_ONLY, EXTRACT_ONLY, VIEW_ONLY, DISABLED;
			public static final Mode[] VALUES = values();
		}

		@Override
		public boolean canExtract(ItemStack stack) {
			Mode mode = info.getMode();
			if (mode == Mode.INSERT_ONLY || mode == Mode.VIEW_ONLY || mode == Mode.DISABLED)
				return false;
			boolean allNull = true;
			boolean whitelist = info.isWhiteList();
			for (int i = 0;i < filterInv.getSizeInventory();i++) {
				ItemStack valid = filterInv.getStackInSlot(i);
				if (!valid.isEmpty()) {
					allNull = false;
					if (TomsModUtils.areItemStacksEqual(stack, valid, info.useMeta(), info.useNBT(), info.useMod())) {
						if (whitelist)
							return true;
						else
							return false;
					}
				}
			}
			if (allNull)
				return true;
			return !whitelist;
		}

		@Override
		public boolean canView(ItemStack stack) {
			Mode mode = info.getMode();
			boolean canViewAll = info.canViewAll();
			if ((mode == Mode.INSERT_ONLY || canViewAll) || mode == Mode.DISABLED)
				return false;
			if (canViewAll)
				return true;
			boolean allNull = true;
			boolean whitelist = info.isWhiteList();
			for (int i = 0;i < filterInv.getSizeInventory();i++) {
				ItemStack valid = filterInv.getStackInSlot(i);
				if (!valid.isEmpty()) {
					allNull = false;
					if (TomsModUtils.areItemStacksEqual(stack, valid, info.useMeta(), info.useNBT(), info.useMod())) {
						if (whitelist)
							return true;
						else
							return false;
					}
				}
			}
			if (allNull)
				return true;
			return !whitelist;
		}
	}
}
