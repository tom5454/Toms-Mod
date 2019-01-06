package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.tom.api.inventory.SlotOutput;
import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntityUVLightbox;

import com.tom.core.item.ItemBlueprint;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerUVLightbox extends ContainerTomsMod {
	private TileEntityUVLightbox te;

	public ContainerUVLightbox(InventoryPlayer playerInv, TileEntityUVLightbox te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 44, 35));
		addSlotToContainer(new SlotOutput(te, 1, 130, 36));
		addSlotToContainer(new SlotBlueprint(te, 2, 44, 16));
		addSlotToContainer(new SlotSpeedUpgrade(te, 3, 152, 63, 24));
		addPlayerSlots(playerInv, 8, 84);
		syncHandler.registerInt(0, te::getClientEnergyStored, i -> te.clientEnergy = i);
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerInventoryFieldShort(te, 1);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	public static class SlotBlueprint extends Slot {

		public SlotBlueprint(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack.getItem() instanceof ItemBlueprint && ((ItemBlueprint)stack.getItem()).isCircuit(stack);
		}
	}
}
