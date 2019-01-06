package com.tom.defense.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.tom.api.item.IIdentityCard;
import com.tom.api.item.IPowerLinkCard;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.tileentity.TileEntitySecurityStation;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSecurityStation extends ContainerTomsMod {
	private TileEntitySecurityStation te;

	public ContainerSecurityStation(InventoryPlayer inv, TileEntitySecurityStation te) {
		this.addSlotToContainer(new SlotIndentityCard(te, 0, 177, 28));
		this.addSlotToContainer(new SlotPowerLinkCard(te, 1, 145, 45));
		this.addSlotToContainer(new SlotIndentityCard(te, 2, 15, 25) {
			@Override
			public int getSlotStackLimit() {
				return 4;
			}
		});
		this.addSlotToContainer(new SlotIndentityCard(te, 3, 88, 97));
		this.addSlotToContainer(new SlotIndentityCard(te, true, 4, 146, 97));
		for (int i = 0;i < 8;i++) {
			int y = 57 + i * 18;
			int id = 5 + i * 4;
			this.addSlotToContainer(new SlotIndentityCard(te, id, 176, y));
			this.addSlotToContainer(new SlotIndentityCard(te, id + 1, 194, y));
			this.addSlotToContainer(new SlotIndentityCard(te, id + 2, 212, y));
			this.addSlotToContainer(new SlotIndentityCard(te, id + 3, 230, y));
		}
		this.addPlayerSlots(inv, 8, 129);
		this.te = te;
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerShort(1, te::getCompiledRightsFromEditingCard, i -> te.setField(1, i));
		syncHandler.registerEnum(2, () -> te.rsMode, e -> te.rsMode = e, ForceDeviceControlType.VALUES);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	public static class SlotIndentityCard extends Slot {
		private final boolean createSlot;

		public SlotIndentityCard(IInventory inventoryIn, boolean isCreatingSlot, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
			this.createSlot = isCreatingSlot;
		}

		public SlotIndentityCard(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			this(inventoryIn, false, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() instanceof IIdentityCard && (this.createSlot ? ((IIdentityCard) stack.getItem()).isEmpty(stack) || ((IIdentityCard) stack.getItem()).getUsername(stack) != null : ((IIdentityCard) stack.getItem()).getUsername(stack) != null);
		}

		@Override
		public int getSlotStackLimit() {
			return this.createSlot ? 4 : 1;
		}
	}

	public static class SlotPowerLinkCard extends Slot {
		public SlotPowerLinkCard(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() instanceof IPowerLinkCard && ((IPowerLinkCard) stack.getItem()).getMaster(stack) != null;
		}

		@Override
		public int getSlotStackLimit() {
			return 1;
		}
	}
}
