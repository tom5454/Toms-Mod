package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotOutput;
import com.tom.factory.tileentity.TileEntitySteamSolderingStation;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSteamSolderingStation extends ContainerTomsMod {
	private TileEntitySteamSolderingStation te;
	private int lastProgress = -1;
	private int lastSolderingAlloy = -1;
	private int craftingErrorLast = -1;
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
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int progress = te.getField(1) > 0 ? MathHelper.floor((1 - (((float) te.getField(0)) / te.getField(1))) * MAX_PROGRESS) : 0;
		for (IContainerListener crafter : listeners) {
			if (progress != lastProgress) {
				crafter.sendWindowProperty(this, 0, progress);
			}
			if (te.getField(2) != lastSolderingAlloy) {
				crafter.sendWindowProperty(this, 1, te.getField(2));
			}
			if (te.craftingError != craftingErrorLast) {
				crafter.sendWindowProperty(this, 2, te.craftingError);
			}
		}
		lastProgress = progress;
		lastSolderingAlloy = te.getField(2);
		craftingErrorLast = te.craftingError;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0) {
			te.setField(0, data);
		} else if (id == 1) {
			te.setField(2, data);
		} else if (id == 2) {
			te.craftingError = data;
		}
	}
}
