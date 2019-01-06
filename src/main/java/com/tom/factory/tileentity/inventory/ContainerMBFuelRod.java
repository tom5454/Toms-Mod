package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.tom.api.item.IFuelRod;
import com.tom.factory.tileentity.TileEntityMultiblockController;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerMBFuelRod extends ContainerTomsMod {
	private TileEntityMultiblockController tileEntity;

	public ContainerMBFuelRod(InventoryPlayer inventory, TileEntityMultiblockController tileEntity) {
		IInventory inv = tileEntity.getInventory(10);
		this.tileEntity = tileEntity;
		if (inv != null) {
			int[] slots = tileEntity.getSlots(10);
			if (slots != null) {
				addSlotToContainer(new SlotFuelRod(inv, slots[0], 98, 40));
			}
		}
		addPlayerSlots(inventory, 8, 94);
		syncHandler.setReceiver(tileEntity);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return TomsModUtils.isUseable(playerIn, tileEntity);
	}

	public static class SlotFuelRod extends Slot {

		public SlotFuelRod(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public int getSlotStackLimit() {
			return 1;
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack.getItem() instanceof IFuelRod && ((IFuelRod) stack.getItem()).getHeat(stack) <= 20;
		}
	}

}
