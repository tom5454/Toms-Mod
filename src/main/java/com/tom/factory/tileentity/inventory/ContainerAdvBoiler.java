package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.util.math.MathHelper;

import com.tom.factory.tileentity.TileEntityAdvBoiler;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerAdvBoiler extends ContainerTomsMod {
	private TileEntityAdvBoiler te;

	public ContainerAdvBoiler(InventoryPlayer playerInv, TileEntityAdvBoiler te) {
		this.te = te;
		syncHandler.setReceiver(te);
		syncHandler.registerTank(0, te.getTankWater());
		syncHandler.registerTank(1, te.getTankSteam());
		syncHandler.registerShort(0, () -> MathHelper.floor(te.getHeat()), data -> te.clientHeat = data);
		syncHandler.registerShort(1, () -> MathHelper.floor(((double) te.getBurnTime()) / te.getMaxBurnTime() * 100), te::setBurnTime);
		addSlotToContainer(new SlotFurnaceFuel(te.inv, 0, 43, 64));
		addPlayerSlots(playerInv, 8, 94);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return TomsModUtils.isUsable(te.getPos(), playerIn, te.getWorld(), te);
	}
}
