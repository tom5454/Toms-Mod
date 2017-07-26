package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotOutput;
import com.tom.factory.tileentity.TileEntityBlastFurnace;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerBlastFurnace extends ContainerTomsMod {
	private TileEntityBlastFurnace te;
	private int burnLast, progressLast;

	public ContainerBlastFurnace(InventoryPlayer playerInv, TileEntityBlastFurnace te) {
		this.te = te;
		addSlotToContainer(new Slot(te, 0, 56, 17));
		addSlotToContainer(new SlotOutput(te, 1, 116, 35));
		addSlotToContainer(new Slot(te, 2, 56, 53));
		addPlayerSlots(playerInv, 8, 84);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int progress = te.getField(1) > 0 ? MathHelper.floor(((float) te.getField(0)) / te.getField(1) * 100) : 0;
		int burnTime = te.getField(3) > 0 ? MathHelper.floor(((float) te.getField(2)) / te.getField(3) * 100) : 0;
		for (IContainerListener crafter : listeners) {
			if (burnTime != burnLast)
				crafter.sendWindowProperty(this, 0, burnTime);
			if (progress != progressLast)
				crafter.sendWindowProperty(this, 1, progress);
		}
		burnLast = burnTime;
		progressLast = progress;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		te.setField(id, data);
	}
}
