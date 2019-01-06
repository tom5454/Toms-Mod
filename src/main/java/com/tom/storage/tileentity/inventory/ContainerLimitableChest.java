package com.tom.storage.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.tom.storage.tileentity.TileEntityLimitableChest;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerLimitableChest extends ContainerTomsMod {
	private static final int numRows = 3;
	private TileEntityLimitableChest te;

	public ContainerLimitableChest(InventoryPlayer inventory, TileEntityLimitableChest tileEntity) {
		this.te = tileEntity;
		for (int j = 0;j < numRows;++j) {
			for (int k = 0;k < 9;++k) {
				if (!(j == 2 && k == 8))
					this.addSlotToContainer(new Slot(te, k + j * 9, 8 + k * 18, 18 + j * 18));
			}
		}
		syncHandler.registerInventoryFieldShort(tileEntity, 0);
		syncHandler.setReceiver(te);
		this.addPlayerSlots(inventory, 8, 85);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return te.isUsableByPlayer(player);
	}

	/**
	 * Take a stack from the specified inventory slot.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < numRows * 9) {
				if (!this.mergeItemStack(itemstack1, numRows * 9, this.inventorySlots.size(), true)) { return ItemStack.EMPTY; }
			} else if (!this.mergeItemStack(itemstack1, 0, numRows * 9, false)) { return ItemStack.EMPTY; }

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

}
