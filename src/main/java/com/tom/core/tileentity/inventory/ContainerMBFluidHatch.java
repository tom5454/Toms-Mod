package com.tom.core.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.IFluidContainerItem;

import com.tom.factory.tileentity.TileEntityMBFluidPort;

@SuppressWarnings("deprecation")
public class ContainerMBFluidHatch extends ContainerTomsMod{
	private TileEntityMBFluidPort te;

	public ContainerMBFluidHatch(InventoryPlayer inventory, TileEntityMBFluidPort tileEntity) {
		this.te = tileEntity;
		int x = 109;
		int y = 56;
		this.addSlotToContainer(new Slot(te, 0, x, y));
		this.addSlotToContainer(new Slot(te, 1, x, y-43));

		this.addPlayerSlots(inventory, 8, 84);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		boolean ret = this.te.isUseableByPlayer(player);
		return ret;
	}
	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex){
		int slotA = 2;
		ItemStack itemstack = null;
		Slot slot = inventorySlots.get(slotIndex);

		if(slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			//From here change accordingly...
			if(slotIndex < slotA) {
				if(!mergeItemStack(itemstack1, slotA, slotA+36, true)) {
					return null;
				}
			} else {
				//Shift click single items only.
				if(itemstack1.stackSize == 1 && itemstack1.getItem() instanceof IFluidContainerItem && slotIndex == 0) {
					for(int i = 0; i < slotA; i++) {
						Slot shiftedInSlot = inventorySlots.get(i);
						if(!shiftedInSlot.getHasStack() && shiftedInSlot.isItemValid(itemstack1)) mergeItemStack(itemstack1, i, i + 1, false);
					}
				}
			}
			//...till here.

			if(itemstack1.stackSize == 0) {
				slot.putStack((ItemStack)null);
			} else {
				slot.onSlotChanged();
			}

			if(itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

}
