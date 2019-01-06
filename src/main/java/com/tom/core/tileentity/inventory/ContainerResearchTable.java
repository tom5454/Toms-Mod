package com.tom.core.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import com.tom.core.research.ResearchHandler;

import com.tom.core.tileentity.TileEntityResearchTable;

public class ContainerResearchTable extends ContainerTomsMod {
	public ContainerResearchTable(InventoryPlayer inventory, TileEntityResearchTable tileEntity) {
		this.te = tileEntity;
		int x = 6;
		int y = 62;
		this.addSlotToContainer(new Slot(te, 0, x, y - 19));
		this.addSlotToContainer(new Slot(te, 1, x, y + 18));
		this.addSlotToContainer(new Slot(te, 2, x, y - 56));
		int x2 = x + 106;
		int y2 = y - 55;
		this.addSlotToContainer(new Slot(te, 3, x2, y2));
		this.addSlotToContainer(new Slot(te, 4, x2 + 18, y2));
		this.addSlotToContainer(new Slot(te, 5, x2, y2 + 18));
		this.addSlotToContainer(new Slot(te, 6, x2 + 18, y2 + 18));
		int x3 = x + 106;
		int y3 = y - 10;
		this.addSlotToContainer(new Slot(te, 7, x3, y3));
		this.addSlotToContainer(new Slot(te, 8, x3 + 18, y3));
		this.addSlotToContainer(new Slot(te, 9, x3 + 36, y3));
		this.addSlotToContainer(new Slot(te, 10, x3, y3 + 18));
		this.addSlotToContainer(new Slot(te, 11, x3 + 18, y3 + 18));
		this.addSlotToContainer(new Slot(te, 12, x3 + 36, y3 + 18));
		this.addSlotToContainer(new Slot(te, 13, x3, y3 + 36));
		this.addSlotToContainer(new Slot(te, 14, x3 + 18, y3 + 36));
		this.addSlotToContainer(new Slot(te, 15, x3 + 36, y3 + 36));

		this.addSlotToContainer(new Slot(te, 16, x + 190, y + 8));
		this.addSlotToContainer(new Slot(te, 17, x, y - 38));
		this.addSlotToContainer(new Slot(te, 18, x + 190, y + 32));
		this.addPlayerSlots(inventory, 30, 137);

		syncHandler.registerInventoryFieldShort(te, 0);
		syncHandler.registerInventoryFieldInt(te, 2);
		syncHandler.registerInventoryFieldInt(te, 3);
		syncHandler.registerInventoryFieldInt(te, 4);
		syncHandler.registerShort(1, () -> te.craftingError, i -> te.craftingError = i);
		syncHandler.registerBoolean(2, () -> te.craftAll, b -> te.craftAll = b);
		syncHandler.registerBoolean(3, () -> ResearchHandler.isCompleted(te.getResearchHandler(), te.currentResearch), b -> te.completed = b);
		syncHandler.registerInt(1, () -> ResearchHandler.getId(te.currentResearch), id -> te.currentResearch = ResearchHandler.getResearchByID(id));
		syncHandler.setReceiver(te);
	}

	/**
	 * 0: Big Note Book, 1:Note Book, 2:Ink, 3-6: Research Components,
	 * 7-15:Crafting in, 16:Crafting out, 17:Paper, 18:Crafting Extra
	 */
	private TileEntityResearchTable te;

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		boolean ret = this.te.isUsableByPlayer(player);
		return ret;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		te.containerOpen();
	}
}
