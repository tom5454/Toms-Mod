package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.MathHelper;

import com.tom.factory.tileentity.TileEntityAdvFluidBoiler;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerAdvFluidBoiler extends ContainerTomsMod {
	private TileEntityAdvFluidBoiler te;

	public ContainerAdvFluidBoiler(InventoryPlayer playerInv, TileEntityAdvFluidBoiler te) {
		this.te = te;
		addPlayerSlots(playerInv, 8, 94);
		syncHandler.registerTank(0, te.getTankWater());
		syncHandler.registerTank(1, te.getTankSteam());
		syncHandler.registerTank(2, te.getTankFuel());
		syncHandler.registerShort(0, () -> MathHelper.floor(te.getHeat()), data -> te.clientHeat = data);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return TomsModUtils.isUsable(te.getPos(), playerIn, te.getWorld(), te);
	}

}
