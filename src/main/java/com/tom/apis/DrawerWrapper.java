package com.tom.apis;

import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandler;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;

public class DrawerWrapper implements IItemHandler {
	public IDrawer drawer;

	public DrawerWrapper(IDrawer drawer) {
		this.drawer = drawer;
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		ItemStack s = drawer.getStoredItemPrototype().copy();
		s.setCount(drawer.getStoredItemCount());
		return slot == 0 ? s : ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (slot == 0) {
			return insertItem(drawer, stack, simulate);
		} else
			return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot == 0) {
			return extractItem(drawer, amount, simulate);
		} else
			return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return slot == 0 ? drawer.getMaxCapacity(drawer.getStoredItemPrototype()) : 0;
	}

	public static ItemStack extractItem(IDrawer drawer, int amount, boolean simulate) {
		if (drawer.isEmpty()) {
			return ItemStack.EMPTY;
		} else if (drawer.canItemBeExtracted(drawer.getStoredItemPrototype())) {
			int ext = Math.min(drawer.getStoredItemCount(), amount);
			ItemStack s = drawer.getStoredItemPrototype().copy();
			if (!simulate)
				drawer.setStoredItemCount(drawer.getStoredItemCount() - ext);
			s.setCount(ext);
			return s;
		} else {
			return ItemStack.EMPTY;
		}
	}

	public static ItemStack insertItem(IDrawer drawer, ItemStack stack, boolean simulate) {
		if (drawer.canItemBeStored(stack)) {
			if (drawer.isEmpty()) {
				ItemStack s = stack.copy();
				s.setCount(1);
				drawer.setStoredItem(s, 0);
				int ins = Math.min(drawer.getRemainingCapacity(), stack.getCount());
				if (!simulate)
					drawer.setStoredItem(s, ins);
				if (simulate)
					drawer.setStoredItem(ItemStack.EMPTY, 0);
				if (ins != stack.getCount()) {
					s = stack.copy();
					s.setCount(s.getCount() - ins);
					return s;
				} else
					return ItemStack.EMPTY;
			} else {
				int ins = Math.min(drawer.getRemainingCapacity(), stack.getCount());
				if (!simulate)
					drawer.setStoredItemCount(drawer.getStoredItemCount() + ins);
				if (ins != stack.getCount()) {
					ItemStack s = stack.copy();
					s.setCount(s.getCount() - ins);
					return s;
				} else
					return ItemStack.EMPTY;
			}
		} else {
			return stack;
		}
	}
}
