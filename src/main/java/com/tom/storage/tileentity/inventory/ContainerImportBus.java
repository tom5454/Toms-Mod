package com.tom.storage.tileentity.inventory;

import com.tom.api.inventory.SlotPhantom;
import com.tom.core.tileentity.inventory.ContainerTomsMod;
import com.tom.storage.StorageInit;
import com.tom.storage.multipart.PartImportBus;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerImportBus extends ContainerTomsMod {
	private PartImportBus part;
	public ContainerImportBus(PartImportBus bus, InventoryPlayer playerInv) {
		int i = 0, x = 57, y = 22;
		part = bus;
		addSlotToContainer(new SlotPhantom(bus.filterInv, i, x, y));
		addSlotToContainer(new SlotPhantom(bus.filterInv, i+1, x+18, y));
		addSlotToContainer(new SlotPhantom(bus.filterInv, i+2, x+36, y));
		i+=3;y+=18;
		addSlotToContainer(new SlotPhantom(bus.filterInv, i, x, y));
		addSlotToContainer(new SlotPhantom(bus.filterInv, i+1, x+18, y));
		addSlotToContainer(new SlotPhantom(bus.filterInv, i+2, x+36, y));
		i+=3;y+=18;
		addSlotToContainer(new SlotPhantom(bus.filterInv, i, x, y));
		addSlotToContainer(new SlotPhantom(bus.filterInv, i+1, x+18, y));
		addSlotToContainer(new SlotPhantom(bus.filterInv, i+2, x+36, y));
		addSlotToContainer(new SlotSpeedCard(bus.upgradeInv, 0, 140, 36, 4));
		addPlayerSlots(playerInv, 8, 94);
	}
	private boolean lastWhiteList;
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return null;
	}
	public static class SlotSpeedCard extends Slot{
		private final int maxSize;
		public SlotSpeedCard(IInventory inventoryIn, int index, int xPosition,
				int yPosition, int maxStackSize) {
			super(inventoryIn, index, xPosition, yPosition);
			maxSize = maxStackSize;
		}
		@Override
		public int getSlotStackLimit() {
			return maxSize;
		}
		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() == StorageInit.speedCard;
		}
	}
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for(IContainerListener crafter : listeners){
			if(lastWhiteList != part.isWhiteList()){
				crafter.sendProgressBarUpdate(this, 0, part.isWhiteList() ? 1 : 0);
			}
		}
		lastWhiteList = part.isWhiteList();
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value){
		if(id == 0){
			part.setWhiteList(value == 1);
		}
	}
}
