package com.tom.core.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.tom.api.inventory.ISlotClickListener;
import com.tom.api.inventory.ItemIgnoreList;
import com.tom.api.inventory.SlotLocked;
import com.tom.api.inventory.SlotPhantom;
import com.tom.apis.TomsModUtils;

public abstract class ContainerTomsMod extends Container {
	public static final int phantomSlotChange = 4;

	protected void addPlayerSlots(InventoryPlayer playerInventory, int x, int y) {
		for (int i = 0;i < 3;++i) {
			for (int j = 0;j < 9;++j) {
				addSlotToContainer(new SlotPlayer(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18, this));
			}
		}

		for (int i = 0;i < 9;++i) {
			addSlotToContainer(new SlotPlayer(playerInventory, i, x + i * 18, y + 58, this));
		}
	}

	protected void addPlayerSlotsExceptHeldItem(InventoryPlayer playerInventory, int x, int y) {
		for (int i = 0;i < 3;++i) {
			for (int j = 0;j < 9;++j) {
				addSlotToContainer(new SlotPlayer(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18, this));
			}
		}
		for (int i = 0;i < 9;++i) {
			if (playerInventory.currentItem == i)
				addSlotToContainer(new SlotLocked(playerInventory, i, x + i * 18, y + 58));
			else
				addSlotToContainer(new SlotPlayer(playerInventory, i, x + i * 18, y + 58, this));
		}
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer playerIn) {
		Slot slot = slotId > -1 && slotId < inventorySlots.size() ? inventorySlots.get(slotId) : null;
		if (slot instanceof SlotPhantom) {
			ItemStack s = TomsModUtils.copyItemStack(playerIn.inventory.getItemStack());
			if (!s.isEmpty()) {
				if (!ItemIgnoreList.accepts(s))
					s.setTagCompound(null);
				slot.putStack(dragType == 1 ? s.splitStack(1) : s);
			} else if (dragType != 1)
				slot.putStack(ItemStack.EMPTY);
			else if (dragType == 1) {
				if (!slot.getStack().isEmpty()) {
					int c = 1;
					if (clickTypeIn == ClickType.PICKUP_ALL)
						c = -1;
					else if (clickTypeIn == ClickType.QUICK_CRAFT)
						c = -phantomSlotChange;
					else if (clickTypeIn == ClickType.CLONE)
						c = phantomSlotChange;
					if (slot.getStack().getMaxStackSize() >= slot.getStack().getCount() + c && slot.getStack().getCount() + c > 0) {
						slot.getStack().grow(c);
					}
				}
			}
			return playerIn.inventory.getItemStack();
		} else if (slot instanceof ISlotClickListener) {
			((ISlotClickListener) slot).slotClick(slotId, dragType, clickTypeIn, playerIn);
			return super.slotClick(slotId, dragType, clickTypeIn, playerIn);
		} else
			return super.slotClick(slotId, dragType, clickTypeIn, playerIn);
	}

	public boolean showPlayerSlots() {
		return true;
	}

	public static class SlotPlayer extends Slot {
		private ContainerTomsMod container;

		public SlotPlayer(IInventory inventoryIn, int index, int xPosition, int yPosition, ContainerTomsMod container) {
			super(inventoryIn, index, xPosition, yPosition);
			this.container = container;
		}

		public boolean canRender() {
			return container.showPlayerSlots();
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return ItemStack.EMPTY;
	}
}
