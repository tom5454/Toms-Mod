package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;

import com.tom.api.inventory.SlotOutput;
import com.tom.factory.tileentity.TileEntitySteamSolderingStation;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSteamSolderingStation extends ContainerTomsMod {
	private TileEntitySteamSolderingStation te;
	public static final int MAX_PROGRESS = 500;

	public ContainerSteamSolderingStation(InventoryPlayer playerInv, TileEntitySteamSolderingStation te) {
		this.te = te;
		for (int i = 0;i < 3;++i) {
			for (int j = 0;j < 3;++j) {
				this.addSlotToContainer(new Slot(te, j + i * 3, 10 + j * 18, 17 + i * 18));
			}
		}
		addSlotToContainer(new SlotOutput(te, 9, 130, 36));
		this.addSlotToContainer(new Slot(te, 10, 154, 6));
		this.addSlotToContainer(new Slot(te, 11, 74, 7));
		addPlayerSlots(playerInv, 8, 84);
		syncHandler.registerShort(0, () -> te.getField(1) > 0 ? MathHelper.floor((1 - (((float) te.getField(0)) / te.getField(1))) * MAX_PROGRESS) : 0, i -> te.setField(0, i));
		syncHandler.registerInventoryFieldShort(te, 2);
		syncHandler.registerShort(1, () -> te.craftingError, i -> te.craftingError = i);
		syncHandler.registerBoolean(3, te::canRun, te::setClCanRun);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}
}
