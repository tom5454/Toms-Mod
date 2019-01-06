package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.MathHelper;

import com.tom.factory.tileentity.TileEntityFluidBoiler;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerFluidBoiler extends ContainerTomsMod {
	private TileEntityFluidBoiler te;

	public ContainerFluidBoiler(InventoryPlayer playerInv, TileEntityFluidBoiler te) {
		this.te = te;
		addPlayerSlots(playerInv, 8, 94);
		syncHandler.registerTank(0, te.getTankWater());
		syncHandler.registerTank(1, te.getTankSteam());
		syncHandler.registerTank(2, te.getTankFuel());
		syncHandler.registerShort(0, () -> MathHelper.floor(te.getHeat()), i -> te.clientHeat = i);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return TomsModUtils.isUsable(te.getPos(), playerIn, te.getWorld(), te);
	}
}
