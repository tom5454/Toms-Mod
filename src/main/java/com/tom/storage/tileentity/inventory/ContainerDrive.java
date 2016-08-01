package com.tom.storage.tileentity.inventory;

import com.tom.api.item.IStorageCell;
import com.tom.core.tileentity.inventory.ContainerTomsMod;
import com.tom.storage.tileentity.TileEntityDrive;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDrive extends ContainerTomsMod {
	private TileEntityDrive te;
	public ContainerDrive(InventoryPlayer playerInv, TileEntityDrive te) {
		this.te = te;
		int i = 14;
		this.addSlotToContainer(new SlotStorageCell(te, 0, 71, i));
		this.addSlotToContainer(new SlotStorageCell(te, 1, 71+18, i));i+=18;
		this.addSlotToContainer(new SlotStorageCell(te, 2, 71, i));
		this.addSlotToContainer(new SlotStorageCell(te, 3, 71+18, i));i+=18;
		this.addSlotToContainer(new SlotStorageCell(te, 4, 71, i));
		this.addSlotToContainer(new SlotStorageCell(te, 5, 71+18, i));i+=18;
		this.addSlotToContainer(new SlotStorageCell(te, 6, 71, i));
		this.addSlotToContainer(new SlotStorageCell(te, 7, 71+18, i));i+=18;
		this.addSlotToContainer(new SlotStorageCell(te, 8, 71, i));
		this.addSlotToContainer(new SlotStorageCell(te, 9, 71+18, i));
		this.addPlayerSlots(playerInv, 8, 117);
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUseableByPlayer(playerIn);
	}
	public static class SlotStorageCell extends Slot{

		public SlotStorageCell(IInventory inventoryIn, int index, int xPosition,
				int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}
		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() instanceof IStorageCell && ((IStorageCell)stack.getItem()).isValid(stack);
		}
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		if(index < inventorySlots.size()){
			//if(inventorySlots.get(index) instanceof SlotStorageCell){
				if(inventorySlots.get(index).getStack() != null){
					Slot slot = inventorySlots.get(index);
					ItemStack itemstack1 = slot.getStack();
					if(itemstack1 != null){
						if (index < 10)
						{
							if (!this.mergeItemStack(itemstack1, 10, this.inventorySlots.size(), true))
							{
								return null;
							}
						}
						else if (!this.mergeItemStack(itemstack1, 0, 10, false))
						{
							return null;
						}

						if (itemstack1.stackSize == 0)
						{
							slot.putStack((ItemStack)null);
						}
						else
						{
							slot.onSlotChanged();
						}
					}
				}
			//}
		}
		return null;
	}
}
