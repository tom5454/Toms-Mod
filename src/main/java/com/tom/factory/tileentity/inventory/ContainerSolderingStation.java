package com.tom.factory.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotOutput;
import com.tom.api.inventory.SlotSpeedUpgrade;
import com.tom.factory.tileentity.TileEntitySolderingStation;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerSolderingStation extends ContainerTomsMod {
	private TileEntitySolderingStation te;
	private int lastProgress = -1;
	private int lastEnergy = -1;
	private int lastSolderingAlloy = -1;
	private int craftingErrorLast = -1;
	public static final int MAX_PROGRESS = 500;
	public ContainerSolderingStation(InventoryPlayer playerInv, TileEntitySolderingStation te) {
		this.te = te;
		for (int i = 0; i < 3; ++i){
			for (int j = 0; j < 3; ++j){
				this.addSlotToContainer(new Slot(te, j + i * 3, 24 + j * 18, 28 + i * 18));
			}
		}
		addSlotToContainer(new SlotOutput(te, 9, 141, 46));
		this.addSlotToContainer(new Slot(te, 10, 154, 6));
		this.addSlotToContainer(new Slot(te, 11, 129, 6));
		this.addSlotToContainer(new SlotSpeedUpgrade(te, 12, 152, 74, 4));
		addPlayerSlots(playerInv, 8, 94);
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUseableByPlayer(playerIn);
	}
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int progress = te.getField(1) > 0 ? MathHelper.floor_double((1 - (((float)te.getField(0)) / te.getField(1))) * MAX_PROGRESS) : 0;
		for(IContainerListener crafter : listeners){
			if(progress != lastProgress){
				crafter.sendProgressBarUpdate(this, 0, progress);
			}
			if(te.getField(2) != lastSolderingAlloy){
				crafter.sendProgressBarUpdate(this, 1, te.getField(2));
			}
			if(te.craftingError != craftingErrorLast){
				crafter.sendProgressBarUpdate(this, 2, te.craftingError);
			}
			if(te.getField(3) != lastEnergy){
				crafter.sendProgressBarUpdate(this, 3, te.getField(3));
			}
		}
		lastProgress = progress;
		lastSolderingAlloy = te.getField(2);
		craftingErrorLast = te.craftingError;
		lastEnergy = te.getField(3);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if(id == 0){
			te.setField(0, data);
		}else if(id == 1){
			te.setField(2, data);
		}else if(id == 2){
			te.craftingError = data;
		}else if(id == 3){
			te.setField(3, data);
		}
	}
}
