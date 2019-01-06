package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.MathHelper;

import com.tom.factory.tileentity.TileEntityGeoBoiler;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerGeoBoiler extends ContainerTomsMod {
	private TileEntityGeoBoiler te;

	public ContainerGeoBoiler(InventoryPlayer playerInv, TileEntityGeoBoiler te) {
		this.te = te;
		addPlayerSlots(playerInv, 8, 94);
		syncHandler.registerShort(0, () -> MathHelper.floor(te.getHeat()), i -> te.clientHeat = i);
		syncHandler.registerTank(0, te.getTankWater());
		syncHandler.registerTank(1, te.getTankSteam());
		syncHandler.registerTank(2, te.getTankLava());
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return TomsModUtils.isUsable(te.getPos(), playerIn, te.getWorld(), te);
	}
}
