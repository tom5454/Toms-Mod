package com.tom.core.tileentity.inventory;

import java.util.ArrayList;
import java.util.List;

import com.tom.api.tileentity.IConfigurable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerConfigurator extends ContainerTomsMod {
	public IConfigurable te;
	public ContainerConfigurator(InventoryPlayer playerInv, IConfigurable configurable) {
		this.te = configurable;
		List<Slot> slotList = new ArrayList<Slot>();
		te.getOption().addSlotsToList(te, slotList, 100-te.getOption().getWidth(), 70-te.getOption().getHeight());
		for(Slot s : slotList){
			addSlotToContainer(s);
		}
		this.addPlayerSlots(playerInv, 8, 94);
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return null;
	}
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		IInventory inv = te.getOption().getInventory();
		if(inv != null)inv.closeInventory(playerIn);
	}
}
