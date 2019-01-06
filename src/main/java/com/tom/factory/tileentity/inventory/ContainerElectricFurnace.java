package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;

import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntityElectricFurnace;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerElectricFurnace extends ContainerTomsMod {
	private TileEntityElectricFurnace te;

	public ContainerElectricFurnace(InventoryPlayer playerInv, TileEntityElectricFurnace te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 44, 35));
		addSlotToContainer(new SlotFurnaceOutput(playerInv.player, te, 1, 130, 36));
		addSlotToContainer(new SlotSpeedUpgrade(te, 2, 152, 63, 24));
		addPlayerSlots(playerInv, 8, 84);
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerInventoryFieldShort(te, 1);
		syncHandler.registerInt(0, te::getClientEnergyStored, i -> te.clientEnergy = i);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}
}