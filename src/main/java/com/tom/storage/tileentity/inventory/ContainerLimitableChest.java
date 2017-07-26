package com.tom.storage.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.storage.tileentity.TileEntityLimitableChest;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerLimitableChest extends ContainerTomsMod {
	private static final int numRows = 3;
	private TileEntityLimitableChest te;
	private int selectedLast = -1;

	public ContainerLimitableChest(InventoryPlayer inventory, TileEntityLimitableChest tileEntity) {
		this.te = tileEntity;
		/*int x = 8;
		int y = 18;
		addSlotToContainer(new Slot(te,0,x,y));
		addSlotToContainer(new Slot(te,1,x,y+18));
		addSlotToContainer(new Slot(te,2,x,y+36));
		addSlotToContainer(new Slot(te,3,x+18,y));
		addSlotToContainer(new Slot(te,4,x+18,y+18));
		addSlotToContainer(new Slot(te,5,x+18,y+36));
		x = x + 36;
		addSlotToContainer(new Slot(te,7,x,y));
		addSlotToContainer(new Slot(te,8,x,y+18));
		addSlotToContainer(new Slot(te,9,x,y+36));
		addSlotToContainer(new Slot(te,10,x+18,y));
		addSlotToContainer(new Slot(te,11,x+18,y+18));
		addSlotToContainer(new Slot(te,12,x+18,y+36));
		x = x + 36;
		addSlotToContainer(new Slot(te,13,x,y));
		addSlotToContainer(new Slot(te,14,x,y+18));
		addSlotToContainer(new Slot(te,15,x,y+36));
		addSlotToContainer(new Slot(te,16,x+18,y));
		addSlotToContainer(new Slot(te,17,x+18,y+18));
		addSlotToContainer(new Slot(te,18,x+18,y+36));
		x = x + 36;
		addSlotToContainer(new Slot(te,19,x,y));
		addSlotToContainer(new Slot(te,20,x,y+18));
		addSlotToContainer(new Slot(te,21,x,y+36));
		addSlotToContainer(new Slot(te,22,x+18,y));
		addSlotToContainer(new Slot(te,23,x+18,y+18));
		addSlotToContainer(new Slot(te,24,x+18,y+36));
		x = x + 36;
		addSlotToContainer(new Slot(te,25,x,y));
		//addSlotToContainer(new Slot(te,26,x,y+18));
		addSlotToContainer(new Slot(te,6,x,y+36));*/
		for (int j = 0;j < numRows;++j) {
			for (int k = 0;k < 9;++k) {
				if (!(j == 2 && k == 8))
					this.addSlotToContainer(new Slot(te, k + j * 9, 8 + k * 18, 18 + j * 18));
			}
		}

		this.addPlayerSlots(inventory, 8, 85);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		boolean ret = this.te.isUsableByPlayer(player);
		return ret;
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

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (te.getField(0) != selectedLast) {
			for (IContainerListener crafter : listeners) {
				crafter.sendWindowProperty(this, 0, te.getField(0));
			}
			this.selectedLast = te.getField(0);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0) {
			te.setField(0, data);
		}
	}

}
