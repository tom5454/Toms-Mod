package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;

import com.tom.api.inventory.SlotOutput;
import com.tom.factory.tileentity.TileEntityRubberBoiler;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerRubberBoiler extends ContainerTomsMod {
	private TileEntityRubberBoiler te;

	public ContainerRubberBoiler(InventoryPlayer playerInv, TileEntityRubberBoiler te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 38, 65));
		addSlotToContainer(new SlotOutput(te, 1, 114, 65));
		addPlayerSlots(playerInv, 8, 94);
		syncHandler.registerTank(0, te.getTankIn());
		syncHandler.registerTank(1, te.getTankOut());
		syncHandler.registerShort(0, () -> MathHelper.floor(te.getHeat()), i -> te.clientHeat = i);
		syncHandler.registerShort(1, () -> MathHelper.floor(((double) te.getProgress()) / TileEntityRubberBoiler.MAX_PROGRESS * 100), te::setClientProgress);
		syncHandler.registerShort(2, () -> te.maxHeat, i -> te.maxHeat = i);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}
}
