package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.tom.api.inventory.SlotOutput;
import com.tom.core.TMResource.CraftingMaterial;
import com.tom.factory.tileentity.TileEntitySteamRubberProcessor;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSteamRubberProcessor extends ContainerTomsMod {
	private TileEntitySteamRubberProcessor te;

	public ContainerSteamRubberProcessor(InventoryPlayer playerInv, TileEntitySteamRubberProcessor te) {
		this.te = te;
		addSlotToContainer(new SlotVulcanizing(te, 0, 44, 35));
		addSlotToContainer(new SlotOutput(te, 1, 130, 36));
		addPlayerSlots(playerInv, 8, 84);
		syncHandler.registerTank(0, te.getTankIn());
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerInventoryFieldShort(te, 1);
		syncHandler.registerBoolean(2, te::canRun, te::setClCanRun);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	public static class SlotVulcanizing extends Slot {

		public SlotVulcanizing(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return CraftingMaterial.VULCANIZING_AGENTS.equals(stack);
		}
	}
}
