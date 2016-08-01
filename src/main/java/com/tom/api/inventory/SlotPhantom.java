package com.tom.api.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPhantom extends Slot {
	public int maxStackSize;
	public SlotPhantom(IInventory inv, int slotIndex, int posX, int posY) {
		this(inv, slotIndex, posX, posY, 1);
	}
	public SlotPhantom(IInventory inv, int slotIndex, int posX, int posY, int maxStackSize) {
		super(inv, slotIndex, posX, posY);
		this.maxStackSize = maxStackSize;
	}
	@Override
	public int getSlotStackLimit(){
		return 1;
	}
	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer){
		return false;
	}
	@Override
	public boolean isItemValid(ItemStack stack) {
		return true;
	}
}
