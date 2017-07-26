package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotOutput;
import com.tom.factory.tileentity.TileEntityIndustrialBlastFurnace;
import com.tom.network.messages.MessageProgress;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerIndustrialBlastFurnace extends ContainerTomsMod {

	private TileEntityIndustrialBlastFurnace te;
	private int lastEnergy, lastProgress;

	public ContainerIndustrialBlastFurnace(InventoryPlayer playerInv, TileEntityIndustrialBlastFurnace te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 36, 36));
		addSlotToContainer(new Slot(te, 1, 36, 54));
		addSlotToContainer(new SlotOutput(te, 2, 123, 46));
		addPlayerSlots(playerInv, 8, 94);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int progress = te.getField(0) > 0 ? MathHelper.floor((1 - (((float) te.getField(0)) / te.maxProgress)) * 100) : 0;
		for (IContainerListener crafter : listeners) {
			MessageProgress msg = new MessageProgress(crafter);
			if (lastEnergy != te.getClientEnergyStored())
				msg.add(0, te.getClientEnergyStored());
			// crafter.sendProgressBarUpdate(this, 0,
			// te.getClientEnergyStored());
			if (lastProgress != progress)
				crafter.sendWindowProperty(this, 1, progress);
			msg.send();
		}
		lastEnergy = te.getClientEnergyStored();
		lastProgress = progress;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0)
			te.clientEnergy = data;
		else if (id == 1)
			te.setField(0, data);
	}
}
