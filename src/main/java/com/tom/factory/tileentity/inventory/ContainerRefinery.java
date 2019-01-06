package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.util.math.MathHelper;

import com.tom.factory.tileentity.TileEntityRefinery;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerRefinery extends ContainerTomsMod {
	private TileEntityRefinery te;

	public ContainerRefinery(InventoryPlayer playerInv, TileEntityRefinery te) {
		this.te = te;
		addSlotToContainer(new SlotFurnaceFuel(te, 0, 49, 63));
		addSlotToContainer(new SlotFurnaceFuel(te, 1, 30, 27));
		addSlotToContainer(new SlotFurnaceFuel(te, 2, 30, 45));
		addSlotToContainer(new SlotFurnaceFuel(te, 3, 30, 63));
		addPlayerSlots(playerInv, 8, 94);
		syncHandler.registerTank(0, te.getTankIn());
		syncHandler.registerTank(1, te.getTankOut1());
		syncHandler.registerTank(2, te.getTankOut2());
		syncHandler.registerTank(3, te.getTankOut3());
		syncHandler.registerShort(0, () -> MathHelper.floor(te.getHeat()), i -> te.clientHeat = i);
		syncHandler.registerShort(1, () -> MathHelper.floor(((double) te.getBurnTime()) / te.getMaxBurnTime() * 100), te::setBurnTime);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}
}
