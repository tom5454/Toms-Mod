package com.tom.energy.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotOutput;
import com.tom.network.messages.MessageProgress;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

import com.tom.energy.tileentity.TileEntityCharger;

public class ContainerCharger extends ContainerTomsMod {

	private TileEntityCharger te;
	private int lastEnergy, lastProgress;
	public ContainerCharger(InventoryPlayer playerInv, TileEntityCharger te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 44, 35));
		addSlotToContainer(new SlotOutput(te, 1, 130, 36));
		addPlayerSlots(playerInv, 8, 84);
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUseableByPlayer(playerIn);
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return null;
	}
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for(IContainerListener crafter : listeners){
			MessageProgress msg = new MessageProgress(crafter);
			if(lastEnergy != te.getClientEnergyStored())msg.add(0, te.getClientEnergyStored());
			//crafter.sendProgressBarUpdate(this, 0, te.getClientEnergyStored());
			if(lastProgress != te.getField(0))crafter.sendProgressBarUpdate(this, 1, te.getField(0));
			msg.send();
		}
		lastEnergy = te.getClientEnergyStored();
		lastProgress = te.getField(0);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
	{
		if(id == 0)te.clientEnergy = data;
		else if(id == 1)te.setField(0, data);
	}

}
