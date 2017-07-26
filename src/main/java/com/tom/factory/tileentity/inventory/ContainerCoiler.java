package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotOutput;
import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.core.CoreInit;
import com.tom.factory.tileentity.TileEntityCoilerPlant;
import com.tom.network.messages.MessageProgress;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerCoiler extends ContainerTomsMod {
	private TileEntityCoilerPlant te;
	private int lastEnergy, lastProgress;
	private int lastMaxProgress;

	public ContainerCoiler(InventoryPlayer playerInv, TileEntityCoilerPlant te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 44, 35));
		addSlotToContainer(new SlotOutput(te, 1, 130, 36));
		addSlotToContainer(new SlotSpeedUpgrade(te, 2, 152, 63, 24));
		addSlotToContainer(new SlotEmptyCoil(te, 3, 44, 54));
		addPlayerSlots(playerInv, 8, 84);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener crafter : listeners) {
			MessageProgress msg = new MessageProgress(crafter);
			if (lastEnergy != te.getClientEnergyStored())
				msg.add(0, te.getClientEnergyStored());
			// crafter.sendProgressBarUpdate(this, 0,
			// te.getClientEnergyStored());
			if (lastProgress != te.getField(0))
				crafter.sendWindowProperty(this, 1, te.getField(0));
			if (lastMaxProgress != te.getField(1))
				crafter.sendWindowProperty(this, 2, te.getField(1));
			msg.send();
		}
		lastEnergy = te.getClientEnergyStored();
		lastProgress = te.getField(0);
		lastMaxProgress = te.getField(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0)
			te.clientEnergy = data;
		else if (id == 1)
			te.setField(0, data);
		else if (id == 2)
			te.setField(1, data);
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