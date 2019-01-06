package com.tom.core.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import com.tom.core.tileentity.TileEntityTabletCrafter;

public class ContainerTabletCrafter extends ContainerTomsMod {
	private TileEntityTabletCrafter te;

	public ContainerTabletCrafter(InventoryPlayer inventory, TileEntityTabletCrafter tileEntity) {
		this.te = tileEntity;
		int x = 72;
		int y = 22;
		this.addSlotToContainer(new Slot(te, 0, x - 10, y + 40));
		this.addSlotToContainer(new Slot(te, 1, x - 64, y + 16));
		this.addSlotToContainer(new Slot(te, 2, x - 64, y - 2));
		this.addSlotToContainer(new Slot(te, 3, x - 28, y + 16));
		this.addSlotToContainer(new Slot(te, 4, x - 28, y - 2));
		this.addSlotToContainer(new Slot(te, 5, x + 13, y - 2));
		this.addSlotToContainer(new Slot(te, 6, x + 71, y + 20));
		this.addSlotToContainer(new Slot(te, 7, x + 9, y + 40));
		this.addSlotToContainer(new Slot(te, 8, x - 46, y + 27));
		this.addSlotToContainer(new Slot(te, 9, x - 46, y - 9));
		this.addSlotToContainer(new Slot(te, 10, x - 46, y + 9));
		this.addSlotToContainer(new Slot(te, 11, x + 57, y - 12));

		this.addPlayerSlots(inventory, 8, 84);
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		boolean ret = this.te.isUsableByPlayer(player);
		return ret;
	}

}
