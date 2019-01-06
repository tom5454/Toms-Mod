package com.tom.defense.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.tom.api.tileentity.IConfigurable.IConfigurationOption.SlotSecurityCard;
import com.tom.defense.DefenseInit;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.tileentity.TileEntityForceFieldProjector;
import com.tom.defense.tileentity.inventory.ContainerSecurityStation.SlotPowerLinkCard;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerForceFieldProjector extends ContainerTomsMod {
	private TileEntityForceFieldProjector te;

	public ContainerForceFieldProjector(InventoryPlayer playerInv, TileEntityForceFieldProjector te) {
		this.te = te;
		addSlotToContainer(new SlotSecurityCard(te, 0, 151, 67));
		addSlotToContainer(new SlotPowerLinkCard(te, 1, 9, 67));
		addSlotToContainer(new SlotProjectorLens(2, 96, 39));
		addSlotToContainer(new SlotEfficiencyUpgrade(te, 3, 96, 67));
		this.addPlayerSlots(playerInv, 8, 94);
		syncHandler.registerInventoryFieldInt(te, 0);
		syncHandler.registerInventoryFieldInt(te, 1);
		syncHandler.registerInventoryFieldShort(te, 2);
		syncHandler.registerInventoryFieldShort(te, 3);
		syncHandler.registerInventoryFieldShort(te, 4);
		syncHandler.registerEnum(5, () -> te.rsMode , e -> te.rsMode = e, ForceDeviceControlType.VALUES);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	public class SlotProjectorLens extends Slot {
		public SlotProjectorLens(int index, int xPosition, int yPosition) {
			super(te, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return !(te.getField(1) > 0) && stack != null && stack.getItem() == DefenseInit.projectorLens;
		}

		@Override
		public boolean canTakeStack(EntityPlayer playerIn) {
			return !(te.getField(1) > 0);
		}
	}

	public static class SlotEfficiencyUpgrade extends Slot {
		public SlotEfficiencyUpgrade(IInventory inv, int index, int xPosition, int yPosition) {
			super(inv, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() == DefenseInit.efficiencyUpgrade;
		}

		@Override
		public int getSlotStackLimit() {
			return 4;
		}
	}
}
