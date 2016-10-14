package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntityElectricFurnaceAdv;
import com.tom.network.messages.MessageProgress;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerAdvElectricFurnace extends ContainerTomsMod {
	private TileEntityElectricFurnaceAdv te;
	private int lastEnergy, lastProgress;
	public ContainerAdvElectricFurnace(InventoryPlayer playerInv, TileEntityElectricFurnaceAdv te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 44, 35));
		addSlotToContainer(new SlotFurnaceOutput(playerInv.player, te, 1, 130, 36));
		addSlotToContainer(new SlotSpeedUpgrade(te, 2, 152, 63, 24));
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
			if(lastEnergy != te.getClientEnergyStored())//crafter.sendProgressBarUpdate(this, 0, te.getClientEnergyStored());
				msg.add(0, te.getClientEnergyStored());
			if(lastProgress != te.getField(0))crafter.sendProgressBarUpdate(this, 1, te.getField(0));
			//if(lastMaxProgress != te.getField(1))crafter.sendProgressBarUpdate(this, 2, te.getField(1));
			msg.send();
		}
		lastEnergy = te.getClientEnergyStored();
		lastProgress = te.getField(0);
		//lastMaxProgress = te.getField(1);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
	{
		if(id == 0)te.clientEnergy = data;
		else if(id == 1)te.setField(0, data);
		//else if(id == 2)te.setField(1, data);
	}
}