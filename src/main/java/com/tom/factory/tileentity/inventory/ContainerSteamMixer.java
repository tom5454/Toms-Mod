package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;

import com.tom.factory.tileentity.TileEntitySteamMixer;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSteamMixer extends ContainerTomsMod {
	private TileEntitySteamMixer te;

	public ContainerSteamMixer(InventoryPlayer inv, TileEntitySteamMixer te) {
		int x = 73, y = 45;
		addSlotToContainer(new Slot(te, 0, x, y));
		addSlotToContainer(new Slot(te, 1, x + 18, y));
		addSlotToContainer(new Slot(te, 2, x, y + 18));
		addSlotToContainer(new Slot(te, 3, x + 18, y + 18));
		addPlayerSlots(inv, 8, 94);
		this.te = te;
		syncHandler.registerTank(0, te.getTankIn());
		syncHandler.registerTank(1, te.getTankOut());
		syncHandler.registerShort(0, () -> MathHelper.floor(((double) te.getField(0)) / TileEntitySteamMixer.MAX_PROCESS_TIME * 100), i -> te.clientProgress = i);
		syncHandler.registerBoolean(1, te::canRun, te::setClCanRun);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return TomsModUtils.isUsable(te.getPos(), playerIn, te.getWorld(), te);
	}
}
