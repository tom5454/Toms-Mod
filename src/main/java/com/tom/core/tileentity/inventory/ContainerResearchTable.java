package com.tom.core.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.core.research.handler.ResearchHandler;
import com.tom.network.messages.MessageProgress;

import com.tom.core.tileentity.TileEntityResearchTable;

public class ContainerResearchTable extends ContainerTomsMod {
	public ContainerResearchTable(InventoryPlayer inventory, TileEntityResearchTable tileEntity) {
		this.te = tileEntity;
		int x = 6;
		int y = 62;
		this.addSlotToContainer(new Slot(te, 0, x, y-19));
		this.addSlotToContainer(new Slot(te, 1, x, y+18));
		this.addSlotToContainer(new Slot(te, 2, x, y-56));
		int x2 = x + 106;
		int y2 = y - 55;
		this.addSlotToContainer(new Slot(te, 3, x2, y2));
		this.addSlotToContainer(new Slot(te, 4, x2+18, y2));
		this.addSlotToContainer(new Slot(te, 5, x2, y2+18));
		this.addSlotToContainer(new Slot(te, 6, x2+18, y2+18));
		int x3 = x + 106;
		int y3 = y - 10;
		this.addSlotToContainer(new Slot(te, 7, x3, y3));
		this.addSlotToContainer(new Slot(te, 8, x3+18, y3));
		this.addSlotToContainer(new Slot(te, 9, x3+36, y3));
		this.addSlotToContainer(new Slot(te, 10, x3, y3+18));
		this.addSlotToContainer(new Slot(te, 11, x3+18, y3+18));
		this.addSlotToContainer(new Slot(te, 12, x3+36, y3+18));
		this.addSlotToContainer(new Slot(te, 13, x3, y3+36));
		this.addSlotToContainer(new Slot(te, 14, x3+18, y3+36));
		this.addSlotToContainer(new Slot(te, 15, x3+36, y3+36));

		this.addSlotToContainer(new Slot(te, 16, x+190, y+8));
		this.addSlotToContainer(new Slot(te, 17, x, y-38));
		this.addSlotToContainer(new Slot(te, 18, x+190, y+32));
		this.addPlayerSlots(inventory, 30, 137);
	}
	/**
	 * 0: Big Note Book, 1:Note Book, 2:Ink, 3-6: Research Components, 7-15:Crafting in, 16:Crafting out,
	 * 17:Paper
	 * */
	private TileEntityResearchTable te;
	//private int lastInkLevel = 0;
	//private boolean firstLoad = true;
	//private int lastResearchProgerss  = 0;
	//private int lastTotalResearchProgerss  = 0;
	//private int lastCraftingProgress  = 0;
	//private int lastTotalCraftingProgerss  = 0;
	private int field_178152_f = -1;
	private int field_178154_h = -1;
	private int field_178155_i = -1;
	private int field_178153_g = -1;
	private int field_178156_j = -1;
	private int field_178157_k = -2;
	private int field_178158_l = -1;
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		boolean ret = this.te.isUseableByPlayer(player);
		return ret;
	}
	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex){
		return null;
	}
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		//boolean sendData = false;
		/*if(this.firstLoad){
        	sendData = true;
        	this.firstLoad = false;
        }*/
		//sendData = sendData || lastInkLevel != te.getField(0) || lastCraftingProgress != te.getField(1) || lastTotalCraftingProgerss != te.getField(2) || lastResearchProgerss != te.getField(3) || lastTotalResearchProgerss != te.getField(4);
		int field_0 = ResearchHandler.getId(te.currentResearch);
		int field_1 = te.craftingError;
		for (int i = 0; i < this.listeners.size(); ++i)
		{
			IContainerListener icrafting = this.listeners.get(i);
			MessageProgress msg = new MessageProgress(icrafting);

			if (this.field_178152_f != this.te.getField(2))
			{
				//icrafting.sendProgressBarUpdate(this, 2, this.te.getField(2));
				msg.add(2, this.te.getField(2));
			}

			if (this.field_178154_h != this.te.getField(0))
			{
				icrafting.sendProgressBarUpdate(this, 0, this.te.getField(0));
			}

			if (this.field_178155_i != this.te.getField(1))
			{
				//icrafting.sendProgressBarUpdate(this, 1, this.te.getField(1));
				msg.add(1, this.te.getField(1));
			}

			if (this.field_178153_g != this.te.getField(3))
			{
				//icrafting.sendProgressBarUpdate(this, 3, this.te.getField(3));
				msg.add(3, this.te.getField(3));
			}
			if (this.field_178156_j != this.te.getField(4))
			{
				//icrafting.sendProgressBarUpdate(this, 4, this.te.getField(4));
				msg.add(4, this.te.getField(4));
			}

			if(this.field_178157_k != field_0)
				icrafting.sendProgressBarUpdate(this, 5, field_0);
			if(this.field_178158_l != field_1)
				icrafting.sendProgressBarUpdate(this, 6, field_1);
			msg.send();
		}

		this.field_178152_f = this.te.getField(2);
		this.field_178154_h = this.te.getField(0);
		this.field_178155_i = this.te.getField(1);
		this.field_178153_g = this.te.getField(3);
		this.field_178156_j = this.te.getField(4);
		this.field_178157_k = field_0;
		this.field_178158_l = field_1;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
	{
		if(id == 5){
			this.te.currentResearch = ResearchHandler.getResearchByID(data);
		}else if(id == 6){
			this.te.craftingError = data;
		}else this.te.setField(id, data);
	}
}
