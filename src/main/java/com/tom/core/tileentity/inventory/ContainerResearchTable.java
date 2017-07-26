package com.tom.core.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.core.research.ResearchHandler;
import com.tom.network.messages.MessageProgress;

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
	}

	/**
	 * 0: Big Note Book, 1:Note Book, 2:Ink, 3-6: Research Components,
	 * 7-15:Crafting in, 16:Crafting out, 17:Paper
	 */
	private TileEntityResearchTable te;
	private int lastTotalCraftingTime = -1;
	private int lastInk = -1;
	private int lastCraftingTime = -1;
	private int lastResearchProgress = -1;
	private int lastTotalResearchProgress = -1;
	private int lastResearch = -2;
	private int lastCraftingError = -1;
	private int lastCraftAll = -1;

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		boolean ret = this.te.isUsableByPlayer(player);
		return ret;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int researchID = ResearchHandler.getId(te.currentResearch);
		int craftingError = te.craftingError;
		int craftAll = te.craftAll ? 1 : 0;
		for (int i = 0;i < this.listeners.size();++i) {
			IContainerListener icrafting = this.listeners.get(i);
			MessageProgress msg = new MessageProgress(icrafting);

			if (this.lastTotalCraftingTime != this.te.getField(2)) {
				msg.add(2, this.te.getField(2));
			}

			if (this.lastInk != this.te.getField(0)) {
				icrafting.sendWindowProperty(this, 0, this.te.getField(0));
			}

			if (this.lastCraftingTime != this.te.getField(1)) {
				msg.add(1, this.te.getField(1));
			}

			if (this.lastResearchProgress != this.te.getField(3)) {
				msg.add(3, this.te.getField(3));
			}
			if (this.lastTotalResearchProgress != this.te.getField(4)) {
				msg.add(4, this.te.getField(4));
			}

			if (this.lastResearch != researchID) {
				icrafting.sendWindowProperty(this, 5, researchID);
				icrafting.sendWindowProperty(this, 51, ResearchHandler.isCompleted(te.getResearchHanler(), te.currentResearch) ? 1 : 0);
			}
			if (this.lastCraftingError != craftingError)
				icrafting.sendWindowProperty(this, 6, craftingError);
			if (this.lastCraftAll != craftAll)
				icrafting.sendWindowProperty(this, 7, craftAll);
			msg.send();
		}
		this.lastTotalCraftingTime = this.te.getField(2);
		this.lastInk = this.te.getField(0);
		this.lastCraftingTime = this.te.getField(1);
		this.lastResearchProgress = this.te.getField(3);
		this.lastTotalResearchProgress = this.te.getField(4);
		this.lastResearch = researchID;
		this.lastCraftingError = craftingError;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 5) {
			this.te.currentResearch = ResearchHandler.getResearchByID(data);
		} else if (id == 51) {
			this.te.completed = data == 1;
		} else if (id == 6) {
			this.te.craftingError = data;
		} else if (id == 7) {
			this.te.craftAll = data == 1;
		} else
			this.te.setField(id, data);
	}
}
