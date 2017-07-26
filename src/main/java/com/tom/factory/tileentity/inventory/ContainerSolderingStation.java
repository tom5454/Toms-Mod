package com.tom.factory.tileentity.inventory;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.ITooltipSlot;
import com.tom.api.inventory.SlotLocked;
import com.tom.api.inventory.SlotOutput;
import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntitySolderingStation;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSolderingStation extends ContainerTomsMod {
	private TileEntitySolderingStation te;
	private int lastProgress = -1;
	private int lastEnergy = -1;
	private int lastSolderingAlloy = -1;
	private int craftingErrorLast = -1;
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
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int progress = te.getField(1) > 0 ? MathHelper.floor((1 - (((float) te.getField(0)) / te.getField(1))) * MAX_PROGRESS) : 0;
		for (IContainerListener crafter : listeners) {
			if (progress != lastProgress) {
				crafter.sendWindowProperty(this, 0, progress);
			}
			if (te.getField(2) != lastSolderingAlloy) {
				crafter.sendWindowProperty(this, 1, te.getField(2));
			}
			if (te.craftingError != craftingErrorLast) {
				crafter.sendWindowProperty(this, 2, te.craftingError);
			}
			if (te.getField(3) != lastEnergy) {
				crafter.sendWindowProperty(this, 3, te.getField(3));
			}
		}
		lastProgress = progress;
		lastSolderingAlloy = te.getField(2);
		craftingErrorLast = te.craftingError;
		lastEnergy = te.getField(3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0) {
			te.setField(0, data);
		} else if (id == 1) {
			te.setField(2, data);
		} else if (id == 2) {
			te.craftingError = data;
		} else if (id == 3) {
			te.setField(3, data);
		}
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
