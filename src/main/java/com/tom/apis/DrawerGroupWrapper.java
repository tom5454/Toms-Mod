package com.tom.apis;

import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandler;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;

public class DrawerGroupWrapper implements IItemHandler {
	public IDrawerGroup drawer;

	public DrawerGroupWrapper(IDrawerGroup drawer) {
		this.drawer = drawer;
	}

	@Override
	public int getSlots() {
		return drawer.getDrawerCount();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		IDrawer d = drawer.getDrawer(slot);
		ItemStack s = d.getStoredItemPrototype().copy();
		s.setCount(d.getStoredItemCount());
		return s;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return DrawerWrapper.insertItem(drawer.getDrawer(slot), stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return DrawerWrapper.extractItem(drawer.getDrawer(slot), amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		IDrawer d = drawer.getDrawer(slot);
		return d.getMaxCapacity(d.getStoredItemPrototype());
	}

}
