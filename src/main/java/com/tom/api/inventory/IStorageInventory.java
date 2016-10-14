package com.tom.api.inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;

import com.tom.apis.TomsModUtils;
import com.tom.storage.multipart.StorageNetworkGrid.IPrioritized;

public interface IStorageInventory {
	List<StoredItemStack> getStacks();
	ItemStack pullStack(StoredItemStack stack, int max);
	ItemStack pushStack(ItemStack stack);
	List<ItemStack> getCraftableStacks();
	public static interface IStorageInv extends IInventory, IPrioritized{
		@Override
		int getPriority();
		void update(ItemStack stack, IInventory inv, int priority);
		int getItemListSize();
	}
	public static class StorageInventory implements IStorageInv{
		public IInventory inventory;
		public int priority;

		public StorageInventory(IInventory inv, int priority) {
			this.inventory = inv;
			this.priority = priority;
		}

		@Override
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
			return inventory.getStackInSlot(index);
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
			return inventory.isItemValidForSlot(index, stack);
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
		public int getPriority() {
			return priority;
		}
		@Override
		public boolean equals(Object arg0) {
			return inventory.equals(arg0);
		}

		@Override
		public void update(ItemStack stack, IInventory inv, int priority) {
			inventory = inv;
			this.priority = priority;
		}

		@Override
		public int getItemListSize() {
			int itemSize = 0;
			for(int i = 0;i<inventory.getSizeInventory();i++){
				if(inventory.getStackInSlot(i) != null)itemSize++;
			}
			return itemSize;
		}
	}
	public static class StorageCellInventory implements IStorageInv{
		public ItemStack cellStack;
		public int priority, invSize;
		private ItemStack[] stack;
		private boolean readDone = false;
		public StorageCellInventory(ItemStack cellStack, int priority, int invSize) {
			this.cellStack = cellStack;
			this.priority = priority;
			if(!cellStack.hasTagCompound())cellStack.setTagCompound(new NBTTagCompound());
			this.invSize = invSize;
			stack = new ItemStack[this.getSizeInventory()];
			readFromNBT(cellStack.getTagCompound());
		}

		@Override
		public int getSizeInventory() {
			return invSize;
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			if(!readDone){
				readFromNBT(cellStack.getTagCompound());
				readDone = true;
			}
			return stack[index];
		}

		@Override
		public ItemStack decrStackSize(int slot, int par2) {
			readFromNBT(cellStack.getTagCompound());
			if (this.stack[slot] != null) {
				ItemStack itemstack;
				if (this.stack[slot].stackSize <= par2) {
					itemstack = this.stack[slot];
					this.stack[slot] = null;
					writeToNBT(cellStack.getTagCompound());
					return itemstack;
				} else {
					itemstack = this.stack[slot].splitStack(par2);

					if (this.stack[slot].stackSize == 0) {
						this.stack[slot] = null;
					}
					writeToNBT(cellStack.getTagCompound());
					return itemstack;
				}
			} else {
				return null;
			}
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			ItemStack is = stack[index];
			stack[index] = null;
			writeToNBT(cellStack.getTagCompound());
			return is;
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			this.stack[index] = stack;
			writeToNBT(cellStack.getTagCompound());
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public void markDirty() {
			writeToNBT(cellStack.getTagCompound());
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer player) {
			return false;
		}

		@Override
		public void openInventory(EntityPlayer player) {

		}

		@Override
		public void closeInventory(EntityPlayer player) {

		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return true;
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
		public void clear() {
			stack = new ItemStack[this.getSizeInventory()];
			writeToNBT(cellStack.getTagCompound());
		}

		@Override
		public String getName() {
			return null;
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
		public int getPriority() {
			return priority;
		}

		@Override
		public void update(ItemStack stack, IInventory inv, int priority) {
			cellStack = stack;
			this.priority = priority;
		}
		public void writeToNBT(NBTTagCompound compound){
			NBTTagList list = new NBTTagList();
			for(int i = 0;i<stack.length;i++){
				if(stack[i] != null){
					NBTTagCompound tag = new NBTTagCompound();
					stack[i].writeToNBT(tag);
					tag.setInteger("Slot", i);
					list.appendTag(tag);
				}
			}
			compound.setTag("inventory", list);
		}
		public void readFromNBT(NBTTagCompound compound){
			stack = new ItemStack[this.getSizeInventory()];
			NBTTagList list = compound.getTagList("inventory", 10);
			for (int i = 0; i < list.tagCount(); ++i)
			{
				NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
				int j = nbttagcompound.getInteger("Slot");

				if (j >= 0 && j < this.stack.length)
				{
					this.stack[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
				}
			}
		}

		@Override
		public int getItemListSize() {
			readFromNBT(cellStack.getTagCompound());
			int itemSize = 0;
			for(int i = 0;i<stack.length;i++){
				if(stack[i] != null){
					itemSize++;
				}
			}
			return itemSize;
		}
	}
	public static interface StoredItemStackFilter{
		boolean canInsert(ItemStack stack);
	}
	public static class FilteredStorageInventory implements IStorageInv{
		public IInventory inventory;
		public int priority;
		public StoredItemStackFilter filter;
		public final EnumFacing side;

		public FilteredStorageInventory(IInventory inv, int priority, StoredItemStackFilter filter, EnumFacing side) {
			this.inventory = inv;
			this.priority = priority;
			this.filter = filter;
			this.side = side;
		}

		@Override
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
		public int getPriority() {
			return priority;
		}
		@Override
		public boolean equals(Object arg0) {
			return inventory.equals(arg0);
		}

		@Override
		public void update(ItemStack stack, IInventory inv, int priority) {
			inventory = inv;
			this.priority = priority;
		}

		@Override
		public int getItemListSize() {
			int itemSize = 0;
			for(int i = 0;i<inventory.getSizeInventory();i++){
				if(inventory.getStackInSlot(i) != null)itemSize++;
			}
			return itemSize;
		}
	}
	public static class BasicFilter implements StoredItemStackFilter{
		private IFilteringInformation info;
		private IInventory filterInv;
		public BasicFilter(IInventory filterInv, IFilteringInformation info) {
			this.info = info;
			this.filterInv = filterInv;
		}
		@Override
		public boolean canInsert(ItemStack stack) {
			boolean allNull = true;
			for(int i = 0;i<filterInv.getSizeInventory();i++){
				ItemStack valid = filterInv.getStackInSlot(i);
				if(valid != null){
					allNull = false;
					if(TomsModUtils.areItemStacksEqual(stack, valid, info.useMeta(), info.useNBT(), info.useMod())){
						return true;
					}
				}
			}
			if(allNull)return true;
			return false;
		}
		public static interface IFilteringInformation{
			boolean useMeta();
			boolean useNBT();
			boolean useMod();
		}
	}
}
