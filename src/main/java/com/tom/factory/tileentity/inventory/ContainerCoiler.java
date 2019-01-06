package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.tom.api.inventory.SlotOutput;
import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.core.CoreInit;
import com.tom.factory.tileentity.TileEntityCoilerPlant;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerCoiler extends ContainerTomsMod {
	private TileEntityCoilerPlant te;

	public ContainerCoiler(InventoryPlayer playerInv, TileEntityCoilerPlant te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 44, 35));
		addSlotToContainer(new SlotOutput(te, 1, 130, 36));
		addSlotToContainer(new SlotSpeedUpgrade(te, 2, 152, 63, 24));
		addSlotToContainer(new SlotEmptyCoil(te, 3, 44, 54));
		addPlayerSlots(playerInv, 8, 84);
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerInventoryFieldShort(te, 1);
		syncHandler.registerInt(0, te::getClientEnergyStored, d -> te.clientEnergy = d);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	public static class SlotEmptyCoil extends Slot {

		public SlotEmptyCoil(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() == CoreInit.emptyWireCoil;
		}
	}
}