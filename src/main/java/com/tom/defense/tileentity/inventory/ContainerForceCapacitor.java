package com.tom.defense.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.tom.api.tileentity.IConfigurable.IConfigurationOption.SlotSecurityCard;
import com.tom.defense.DefenseInit;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.tileentity.TileEntityForceCapacitor;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerForceCapacitor extends ContainerTomsMod {
	private TileEntityForceCapacitor te;

	public ContainerForceCapacitor(InventoryPlayer inv, TileEntityForceCapacitor te) {
		this.addSlotToContainer(new SlotRangeUpgrade(te, 1, 152, 28));
		this.addSlotToContainer(new SlotSecurityCard(te, 0, 152, 46));
		this.addPlayerSlots(inv, 8, 94);
		this.te = te;
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerInventoryFieldShort(te, 1);
		syncHandler.registerInventoryFieldInt(te, 2);
		syncHandler.registerEnum(2, () -> te.rsMode , e -> te.rsMode = e, ForceDeviceControlType.VALUES);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	public static class SlotRangeUpgrade extends Slot {

		public SlotRangeUpgrade(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() == DefenseInit.rangeUpgrade;
		}
	}
}
