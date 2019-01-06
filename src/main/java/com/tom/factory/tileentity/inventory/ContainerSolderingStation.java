package com.tom.factory.tileentity.inventory;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import com.tom.api.inventory.ITooltipSlot;
import com.tom.api.inventory.SlotLocked;
import com.tom.api.inventory.SlotOutput;
import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntitySolderingStation;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSolderingStation extends ContainerTomsMod {
	private TileEntitySolderingStation te;
	public static final int MAX_PROGRESS = 500;

	public ContainerSolderingStation(InventoryPlayer playerInv, TileEntitySolderingStation te) {
		this.te = te;
		for (int i = 0;i < 3;++i) {
			for (int j = 0;j < 3;++j) {
				this.addSlotToContainer(new Slot(te, j + i * 3, 24 + j * 18, 28 + i * 18));
			}
		}
		addSlotToContainer(new SlotOutput(te, 9, 141, 46));
		this.addSlotToContainer(new Slot(te, 10, 154, 6));
		this.addSlotToContainer(new Slot(te, 11, 129, 6));
		this.addSlotToContainer(new SlotSpeedUpgrade(te, 12, 152, 74, te.getMaxSpeedUpgradeCount() / 2));
		this.addSlotToContainer(new SlotOutputPreview(te, 13, 93, 28));
		addPlayerSlots(playerInv, 8, 94);
		syncHandler.registerInventoryFieldInt(te, 3);
		syncHandler.registerInventoryFieldShort(te, 2);
		syncHandler.registerShort(0, () -> te.getField(1) > 0 ? MathHelper.floor((1 - (((float) te.getField(0)) / te.getField(1))) * MAX_PROGRESS) : 0, i -> te.setField(0, i));
		syncHandler.registerShort(1, () -> te.craftingError, i -> te.craftingError = i);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	public static class SlotOutputPreview extends SlotLocked implements ITooltipSlot {
		private TileEntitySolderingStation te;

		public SlotOutputPreview(TileEntitySolderingStation te, int index, int xPosition, int yPosition) {
			super(te, index, xPosition, yPosition);
			this.te = te;
		}

		@Override
		public boolean showTooltip() {
			return true;
		}

		@Override
		public void getTooltip(List<String> tooltip) {
			tooltip.add("");
			tooltip.add(TextFormatting.YELLOW + "" + TextFormatting.ITALIC + I18n.format("tomsMod.clickToCraft"));
		}

		@Override
		public boolean canTakeStack(EntityPlayer playerIn) {
			if (!te.getWorld().isRemote)
				te.buttonPressed();
			return false;
		}
	}
}
