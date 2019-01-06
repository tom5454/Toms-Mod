package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;

import com.tom.api.inventory.SlotOutput;
import com.tom.factory.tileentity.TileEntityCokeOven;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerCokeOven extends ContainerTomsMod {
	private TileEntityCokeOven te;

	public ContainerCokeOven(InventoryPlayer playerInv, TileEntityCokeOven te) {
		addSlotToContainer(new Slot(te, 0, 28, 41));
		addSlotToContainer(new SlotOutput(te, 1, 87, 41));
		addPlayerSlots(playerInv, 8, 94);
		syncHandler.registerTank(0, te.getTank());
		syncHandler.registerShort(0, () -> te.getField(1) > 0 ? MathHelper.floor(((double) te.getField(0)) / te.getField(1) * 100) : 0, i -> te.setField(0, i));
		this.te = te;
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

}
