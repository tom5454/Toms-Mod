package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotOutput;
import com.tom.factory.tileentity.TileEntitySteamCrusher;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSteamCrusher extends ContainerTomsMod {
	private TileEntitySteamCrusher te;
	private int lastProgress;
	private boolean lastCanRun;

	public ContainerSteamCrusher(InventoryPlayer playerInv, TileEntitySteamCrusher te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 44, 35));
		addSlotToContainer(new SlotOutput(te, 1, 126, 36));
		addSlotToContainer(new SlotOutput(te, 2, 144, 36));
		// addSlotToContainer(new SlotSpeedUpgrade(te, 2, 152, 63, 24));
		addPlayerSlots(playerInv, 8, 84);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		boolean canRun = te.canRun();
		for (IContainerListener crafter : listeners) {
			// if(lastEnergy !=
			// te.getClientEnergyStored())crafter.sendProgressBarUpdate(this, 0,
			// te.getClientEnergyStored());
			if (lastProgress != te.getField(0))
				crafter.sendWindowProperty(this, 0, te.getField(0));
			if(lastCanRun != canRun)
				crafter.sendWindowProperty(this, 1, canRun ? 1 : 0);
			// if(lastMaxProgress !=
			// te.getField(1))crafter.sendProgressBarUpdate(this, 2,
			// te.getField(1));
		}
		// lastEnergy = te.getClientEnergyStored();
		lastProgress = te.getField(0);
		// lastMaxProgress = te.getField(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		// if(id == 0)te.clientEnergy = data;
		if (id == 0)
			te.setField(0, data);
		else if(id == 1)te.clientCanRun = data != 0;
		// else if(id == 2)te.setField(1, data);
	}
}
