package com.tom.core.tileentity.inventory;

import com.tom.factory.tileentity.TileEntityMBHatch;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMBHatch extends ContainerTomsMod{
	private TileEntityMBHatch te;

	public ContainerMBHatch(InventoryPlayer inventory, TileEntityMBHatch tileEntity) {
		this.te = tileEntity;
		int x = 72;
		int y = 22;
		this.addSlotToContainer(new Slot(te, 0, x, y));
		this.addSlotToContainer(new Slot(te, 1, x+18, y));
		this.addSlotToContainer(new Slot(te, 2, x, y+18));
		this.addSlotToContainer(new Slot(te, 3, x+18, y+18));
		
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
    	int slotA = 4;
        ItemStack itemstack = null;
        Slot slot = (Slot)inventorySlots.get(slotIndex);

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
                //if(itemstack1.stackSize == 1) {
                    for(int i = 0; i < slotA; i++) {
                        Slot shiftedInSlot = (Slot)inventorySlots.get(i);
                        if(!shiftedInSlot.getHasStack() && shiftedInSlot.isItemValid(itemstack1)) mergeItemStack(itemstack1, i, i + 1, false);
                    }
                //}
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
