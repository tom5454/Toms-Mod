package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.network.datasync.DataSerializers;

import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntityElectricFurnaceAdv;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerAdvElectricFurnace extends ContainerTomsMod {
	private TileEntityElectricFurnaceAdv te;

	public ContainerAdvElectricFurnace(InventoryPlayer playerInv, TileEntityElectricFurnaceAdv te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 44, 35));
		addSlotToContainer(new SlotFurnaceOutput(playerInv.player, te, 1, 130, 36));
		addSlotToContainer(new SlotSpeedUpgrade(te, 2, 152, 63, 24));
		addPlayerSlots(playerInv, 8, 84);
		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.register(0, te::getClientEnergyStored, data -> te.clientEnergy = data, DataSerializers.VARINT);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

}