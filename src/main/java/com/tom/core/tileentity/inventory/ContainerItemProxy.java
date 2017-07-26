package com.tom.core.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotPhantom;

import com.tom.core.tileentity.TileEntityItemProxy;

public class ContainerItemProxy extends ContainerTomsMod {
	private TileEntityItemProxy te;
	private boolean lastMode = false;
	private boolean lastItemMode = false;
	private boolean lastLock = false;
	private boolean lastNBT = false;
	private boolean isFirstLoad = true;

	public ContainerItemProxy(InventoryPlayer inventory, TileEntityItemProxy tileEntity) {
		this.te = tileEntity;
		int x = 8;
		int y = 19;
		addSlotToContainer(new SlotPhantom(te, 0, x, y));
		addSlotToContainer(new SlotPhantom(te, 1, x, y + 18));
		addSlotToContainer(new SlotPhantom(te, 2, x, y + 36));
		addSlotToContainer(new SlotPhantom(te, 3, x + 18, y));
		addSlotToContainer(new SlotPhantom(te, 4, x + 18, y + 18));
		addSlotToContainer(new SlotPhantom(te, 5, x + 18, y + 36));
		x = x + 36;
		addSlotToContainer(new SlotPhantom(te, 7, x, y));
		addSlotToContainer(new SlotPhantom(te, 8, x, y + 18));
		addSlotToContainer(new SlotPhantom(te, 9, x, y + 36));
		addSlotToContainer(new SlotPhantom(te, 10, x + 18, y));
		addSlotToContainer(new SlotPhantom(te, 11, x + 18, y + 18));
		addSlotToContainer(new SlotPhantom(te, 12, x + 18, y + 36));
		x = x + 36;
		addSlotToContainer(new SlotPhantom(te, 13, x, y));
		addSlotToContainer(new SlotPhantom(te, 14, x, y + 18));
		addSlotToContainer(new SlotPhantom(te, 15, x, y + 36));
		addSlotToContainer(new SlotPhantom(te, 16, x + 18, y));
		addSlotToContainer(new SlotPhantom(te, 17, x + 18, y + 18));
		addSlotToContainer(new SlotPhantom(te, 18, x + 18, y + 36));
		x = x + 36;
		addSlotToContainer(new SlotPhantom(te, 19, x, y));
		addSlotToContainer(new SlotPhantom(te, 20, x, y + 18));
		addSlotToContainer(new SlotPhantom(te, 21, x, y + 36));
		addSlotToContainer(new SlotPhantom(te, 22, x + 18, y));
		addSlotToContainer(new SlotPhantom(te, 23, x + 18, y + 18));
		addSlotToContainer(new SlotPhantom(te, 24, x + 18, y + 36));
		x = x + 36;
		addSlotToContainer(new SlotPhantom(te, 25, x, y));
		addSlotToContainer(new SlotPhantom(te, 26, x, y + 18));
		addSlotToContainer(new SlotPhantom(te, 6, x, y + 36));
		int x2 = x - 54;
		y = y + 54;
		this.addSlotToContainer(new Slot(te, 27, x2, y));
		this.addSlotToContainer(new Slot(te, 28, x2 + 18, y));
		this.addSlotToContainer(new Slot(te, 29, x2 + 36, y));
		this.addSlotToContainer(new Slot(te, 30, x2 + 54, y));
		/*for(int i = 0; i < 3; ++i) {
		    for(int j = 0; j < 9; ++j) {
		        addSlotToContainer(new SlotPhantom(te, j + i * 9 + 9, x + j * 18, y + i * 18,1));
		    }
		}
		 */

		this.addPlayerSlots(inventory, 8, 94);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		boolean ret = this.te.isUsableByPlayer(player);
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value) {
		super.updateProgressBar(id, value);
		if (id == 0) {
			te.mode = value == 1;
		} else if (id == 1) {
			te.isItemMode = value == 1;
		} else if (id == 2) {
			te.isLocked = value == 1;
		} else if (id == 3) {
			te.useNBT = value == 1;
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		boolean sendData = false;
		if (this.isFirstLoad) {
			sendData = true;
			this.isFirstLoad = false;
		}
		sendData = sendData || lastMode != te.mode || lastItemMode != te.isItemMode || lastLock != te.isLocked || lastNBT != te.useNBT;
		if (sendData)
			for (IContainerListener crafter : listeners) {
				crafter.sendWindowProperty(this, 0, te.mode ? 1 : 0);
				crafter.sendWindowProperty(this, 1, te.isItemMode ? 1 : 0);
				crafter.sendWindowProperty(this, 2, te.isLocked ? 1 : 0);
				crafter.sendWindowProperty(this, 3, te.useNBT ? 1 : 0);
			}
		lastMode = te.mode;
		lastItemMode = te.isItemMode;
		lastLock = te.isLocked;
		lastNBT = te.useNBT;
	}
}
