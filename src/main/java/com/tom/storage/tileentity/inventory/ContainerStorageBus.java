package com.tom.storage.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.IStorageInventory.BasicFilter.Mode;
import com.tom.api.inventory.SlotPhantom;
import com.tom.storage.multipart.PartStorageBus;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerStorageBus extends ContainerTomsMod {
	private PartStorageBus part;

	public ContainerStorageBus(PartStorageBus bus, InventoryPlayer inventory) {
		this.part = bus;
		for (int i = 0;i < 4;++i) {
			for (int j = 0;j < 9;++j) {
				addSlotToContainer(new SlotPhantom(bus.filterInv, j + i * 9, 8 + j * 18, 11 + i * 18));
			}
		}
		addPlayerSlots(inventory, 8, 94);
	}

	private boolean lastWhiteList, lastMeta, lastNBT, lastMod, lastViewAll, sent;
	private int lastMode;

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener crafter : listeners) {
			if (lastWhiteList != part.isWhiteList() || !sent) {
				crafter.sendWindowProperty(this, 0, part.isWhiteList() ? 1 : 0);
			}
			if (lastMeta != part.isCheckMeta() || !sent) {
				crafter.sendWindowProperty(this, 1, part.isCheckMeta() ? 1 : 0);
			}
			if (lastNBT != part.isCheckNBT() || !sent) {
				crafter.sendWindowProperty(this, 2, part.isCheckNBT() ? 1 : 0);
			}
			if (lastMod != part.isCheckMod() || !sent) {
				crafter.sendWindowProperty(this, 3, part.isCheckMod() ? 1 : 0);
			}
			if (lastViewAll != part.isCanViewAll() || !sent) {
				crafter.sendWindowProperty(this, 4, part.isCanViewAll() ? 1 : 0);
			}
			if (lastMode != part.getMode().ordinal() || !sent) {
				crafter.sendWindowProperty(this, 5, part.getMode().ordinal());
			}
		}
		lastWhiteList = part.isWhiteList();
		lastMeta = part.isCheckMeta();
		lastNBT = part.isCheckNBT();
		lastMod = part.isCheckMod();
		lastViewAll = part.isCanViewAll();
		lastMode = part.getMode().ordinal();
		sent = true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value) {
		if (id == 0) {
			part.setWhiteList(value == 1);
		} else if (id == 1) {
			part.setCheckMeta(value == 1);
		} else if (id == 2) {
			part.setCheckNBT(value == 1);
		} else if (id == 3) {
			part.setCheckMod(value == 1);
		} else if (id == 4) {
			part.setCanViewAll(value == 1);
		} else if (id == 5) {
			part.setMode(Mode.VALUES[value]);
		}
	}
}
