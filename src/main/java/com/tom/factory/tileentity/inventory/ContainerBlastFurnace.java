package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;

import com.tom.api.inventory.SlotOutput;
import com.tom.factory.tileentity.TileEntityBlastFurnace;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerBlastFurnace extends ContainerTomsMod {
	private TileEntityBlastFurnace te;

	public ContainerBlastFurnace(InventoryPlayer playerInv, TileEntityBlastFurnace te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 56, 17));
		addSlotToContainer(new SlotOutput(te, 1, 116, 35));
		addSlotToContainer(new Slot(te, 2, 56, 53));
		addPlayerSlots(playerInv, 8, 84);
		syncHandler.registerShort(0, () -> te.getField(1) > 0 ? MathHelper.floor(((float) te.getField(0)) / te.getField(1) * 100) : 0, i -> te.setField(0, i));
		syncHandler.registerShort(1, () -> te.getField(3) > 0 ? MathHelper.floor(((float) te.getField(2)) / te.getField(3) * 100) : 0, i -> te.setField(1, i));
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}
}
