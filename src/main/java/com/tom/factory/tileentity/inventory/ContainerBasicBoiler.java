package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.util.math.MathHelper;

import com.tom.factory.tileentity.TileEntityBasicBoiler;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerBasicBoiler extends ContainerTomsMod {
	private TileEntityBasicBoiler te;

	public ContainerBasicBoiler(InventoryPlayer playerInv, TileEntityBasicBoiler te) {
		this.te = te;
		addSlotToContainer(new SlotFurnaceFuel(te.inv, 0, 43, 64));
		addPlayerSlots(playerInv, 8, 94);
		syncHandler.registerTank(0, te.getTankWater());
		syncHandler.registerTank(1, te.getTankSteam());
		syncHandler.registerShort(0, () -> MathHelper.floor(te.getHeat()), d -> te.clientHeat = d);
		syncHandler.registerShort(1, () -> MathHelper.floor(((double) te.getBurnTime()) / te.getMaxBurnTime() * 100), te::setBurnTime);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return TomsModUtils.isUsable(te.getPos(), playerIn, te.getWorld(), te);
	}
}
