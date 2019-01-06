package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;

import com.tom.api.inventory.SlotOutput;
import com.tom.factory.tileentity.TileEntityIndustrialBlastFurnace;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerIndustrialBlastFurnace extends ContainerTomsMod {
	private TileEntityIndustrialBlastFurnace te;

	public ContainerIndustrialBlastFurnace(InventoryPlayer playerInv, TileEntityIndustrialBlastFurnace te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 36, 36));
		addSlotToContainer(new Slot(te, 1, 36, 54));
		addSlotToContainer(new SlotOutput(te, 2, 123, 46));
		addPlayerSlots(playerInv, 8, 94);
		syncHandler.registerInt(0, te::getClientEnergyStored, i -> te.clientEnergy = i);
		syncHandler.registerShort(0, () -> te.getField(0) > 0 ? MathHelper.floor((1 - (((float) te.getField(0)) / te.maxProgress)) * 100) : 0, i -> te.setField(0, i));
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}
}
