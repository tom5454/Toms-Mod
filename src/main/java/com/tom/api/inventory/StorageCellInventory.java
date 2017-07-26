package com.tom.api.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import com.tom.api.inventory.IStorageInventory.IUpdateable;
import com.tom.apis.TMLogger;
import com.tom.apis.TomsModUtils;
import com.tom.storage.handler.CacheRegistry;
import com.tom.storage.handler.ICache;
import com.tom.storage.handler.ICraftable;
import com.tom.storage.handler.StorageNetworkGrid;

public class StorageCellInventory implements IStorageInventory, IUpdateable {
	public static class StoredCraftable {
		ICraftable c;
		int bytesUsed;

		public StoredCraftable(ICraftable c) {
			this.c = c;
			getBytes();
		}

		public void add(ICraftable s) {
			c.add(s);
			getBytes();
		}

		public int getBytes() {
			return bytesUsed = c.getBaseBytes() + c.getQuantityBytes();
		}
	}

	public ItemStack cellStack;
	public int priority, cellSize, bytesUsed, clientBytes;
	private boolean dirty = false;
	private List<StorageCellInventory.StoredCraftable> data = new ArrayList<>();
	private final StorageNetworkGrid grid;

	public StorageCellInventory(ItemStack cellStack, int priority, int invSize, StorageNetworkGrid grid) {
		this.cellStack = cellStack;
		this.priority = priority;
		if (!cellStack.hasTagCompound())
			cellStack.setTagCompound(new NBTTagCompound());
		this.cellSize = invSize;
		readFromNBT(cellStack.getTagCompound());
		this.grid = grid;
	}

	@SideOnly(Side.CLIENT)
	public StorageCellInventory(ItemStack cellStack, int invSize) {
		this.cellStack = cellStack;
		if (!cellStack.hasTagCompound())
			cellStack.setTagCompound(new NBTTagCompound());
		this.cellSize = invSize;
		clientBytes = cellStack.getTagCompound().getInteger("bytes");
		grid = null;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public void update(ItemStack stack, IItemHandler inv, int priority) {
		cellStack = stack;
		this.priority = priority;
		saveIfNeeded();
	}

	public void writeToNBT(NBTTagCompound compound) {
		/*NBTTagList list = new NBTTagList();
		for(int i = 0;i<stack.length;i++){
			if(stack[i] != null){
				NBTTagCompound tag = new NBTTagCompound();
				stack[i].writeToNBT(tag);
				tag.setInteger("Slot", i);
				list.appendTag(tag);
			}
		}
		compound.setTag("inventory", list);*/
		NBTTagList list = new NBTTagList();
		for (int i = 0;i < data.size();i++) {
			if (data.get(i) != null) {
				NBTTagCompound tag = new NBTTagCompound();
				CacheRegistry.writeToNBT(data.get(i).c, tag);
				list.appendTag(tag);
			}
		}
		compound.setTag("data", list);
		compound.setInteger("bytes", bytesUsed);
	}

	public void readFromNBT(NBTTagCompound compound) {
		data.clear();
		NBTTagList list = compound.getTagList("data", 10);
		for (int i = 0;i < list.tagCount();++i) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			ICraftable e = CacheRegistry.readFromNBT(tag);
			if (e != null)
				data.add(new StoredCraftable(e));
		}
		if (compound.hasKey("inventory")) {
			TMLogger.info("Found a disk with old data, converting...");
			NBTTagList list2 = compound.getTagList("inventory", 10);
			for (int i = 0;i < list2.tagCount();++i) {
				NBTTagCompound nbttagcompound = list2.getCompoundTagAt(i);
				ItemStack stack = TomsModUtils.loadItemStackFromNBT(nbttagcompound);
				if (stack != null) {
					StoredItemStack s = new StoredItemStack(stack, stack.getCount());
					boolean added = false;
					for (int j = 0;j < data.size();j++) {
						if (data.get(j).equals(s)) {
							data.get(j).add(s);
							added = true;
							break;
						}
					}
					if (!added) {
						data.add(new StoredCraftable(s));
					}
				}
			}
			compound.removeTag("inventory");
			writeToNBT(compound);
		}
		clientBytes = compound.getInteger("bytes");
		recalcBytes();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ICraftable> T pullStack(T stack, long max) {
		for (int i = 0;i < data.size();i++) {
			if (data.get(i).c.equals(stack)) {
				StorageCellInventory.StoredCraftable v = data.get(i);
				T ret = (T) v.c.copy();
				long q = Math.min(max, v.c.getQuantity());
				ret.setQuantity(q);
				v.c.removeQuantity(q);
				if (!v.c.hasQuantity()) {
					data.remove(i);
				} else {
					v.getBytes();
				}
				recalcBytes();
				dirty = true;
				return ret;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ICraftable> T pushStack(T stack) {
		if (stack == null || !stack.hasQuantity() || !ItemIgnoreList.accepts(stack))
			return stack;
		boolean added = false;
		T stackToPut = (T) stack.copy();
		for (int i = 0;i < data.size();i++) {
			StorageCellInventory.StoredCraftable v = data.get(i);
			if (v.c.equals(stackToPut)) {
				long max = v.c.getMaxQuantityForBytes(cellSize - bytesUsed);
				if (max < 1)
					return stack;
				max = Math.min(max, stack.getQuantity());
				added = true;
				stackToPut.setQuantity(max);
				stack.removeQuantity(max);
				v.add(stackToPut);
				v.getBytes();
				recalcBytes();
				dirty = true;
				break;
			}
		}
		if (!added && cellSize >= bytesUsed + stack.getBaseBytes() + 1) {
			long max = stack.getMaxQuantityForBytes(cellSize - bytesUsed);
			if (max < 1)
				return stack;
			max = Math.min(max, stack.getQuantity());
			stackToPut.setQuantity(max);
			stack.removeQuantity(max);
			data.add(new StoredCraftable(stackToPut));
			recalcBytes();
			dirty = true;
		}
		return stack.hasQuantity() ? stack : null;
	}

	public int getBytes() {
		return bytesUsed;
	}

	public int getClientBytes() {
		return clientBytes;
	}

	public void recalcBytes() {
		bytesUsed = data.stream().filter(v -> v != null).mapToInt(StoredCraftable::getBytes).sum();
	}

	public int getMaxBytes() {
		return cellSize;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ICraftable, C extends ICache<T>> List<T> getStacks(Class<C> cache) {
		List<T> ret = new ArrayList<>();
		for (int i = 0;i < data.size();i++) {
			if (data.get(i).c.getCacheClass() == cache) {
				ret.add((T) data.get(i).c);
			}
		}
		return ret;
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
	public void save() {
		writeToNBT(cellStack.getTagCompound());
		dirty = false;
	}

	@Override
	public void saveIfNeeded() {
		if (dirty)
			save();
	}

	@Override
	public long getStorageValue() {
		return cellSize;
	}
}