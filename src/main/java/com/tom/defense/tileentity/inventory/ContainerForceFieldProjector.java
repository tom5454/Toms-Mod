package com.tom.defense.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.tileentity.IConfigurable.IConfigurationOption.SlotSecurityCard;
import com.tom.defense.DefenseInit;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.tileentity.TileEntityForceFieldProjector;
import com.tom.defense.tileentity.inventory.ContainerSecurityStation.SlotPowerLinkCard;
import com.tom.network.messages.MessageProgress;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerForceFieldProjector extends ContainerTomsMod{
	private TileEntityForceFieldProjector te;
	private int powerLast = -1, lastRS = -1, lastDrained, lastX, lastY, lastZ;
	public ContainerForceFieldProjector(InventoryPlayer playerInv, TileEntityForceFieldProjector te) {
		this.te = te;
		addSlotToContainer(new SlotSecurityCard(te, 0, 151, 67));
		addSlotToContainer(new SlotPowerLinkCard(te, 1, 9, 67));
		addSlotToContainer(new SlotProjectorLens(2, 96, 39));
		addSlotToContainer(new SlotEfficiencyUpgrade(te, 3, 96, 67));
		this.addPlayerSlots(playerInv, 8, 94);
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUseableByPlayer(playerIn);
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return null;
	}
	public class SlotProjectorLens extends Slot{
		public SlotProjectorLens(int index,
				int xPosition, int yPosition) {
			super(te, index, xPosition, yPosition);
		}
		@Override
		public boolean isItemValid(ItemStack stack) {
			return !(te.getField(1) > 0) && stack != null && stack.getItem() == DefenseInit.projectorLens;
		}
		@Override
		public boolean canTakeStack(EntityPlayer playerIn) {
			return !(te.getField(1) > 0);
		}
	}
	public static class SlotEfficiencyUpgrade extends Slot{
		public SlotEfficiencyUpgrade(IInventory inv, int index,
				int xPosition, int yPosition) {
			super(inv, index, xPosition, yPosition);
		}
		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() == DefenseInit.efficiencyUpgrade;
		}
		@Override
		public int getSlotStackLimit() {
			return 4;
		}
	}
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for(IContainerListener crafter : listeners) {
			MessageProgress msg = new MessageProgress(crafter);
			if(te.getField(0) != powerLast){
				//crafter.sendProgressBarUpdate(this, 0, te.getField(0));
				msg.add(0, te.getField(0));
			}
			if(te.getField(1) != lastDrained){
				//crafter.sendProgressBarUpdate(this, 1, te.getField(1));
				msg.add(1, te.getField(1));
			}
			if(te.getField(2) != lastX){
				crafter.sendProgressBarUpdate(this, 2, te.getField(2));
			}
			if(te.getField(3) != lastY){
				crafter.sendProgressBarUpdate(this, 3, te.getField(3));
			}
			if(te.getField(4) != lastZ){
				crafter.sendProgressBarUpdate(this, 4, te.getField(4));
			}
			if(te.rsMode.ordinal() != lastRS){
				crafter.sendProgressBarUpdate(this, 5, te.rsMode.ordinal());
			}
			msg.send();
		}
		lastRS = te.rsMode.ordinal();
		lastZ = te.getField(4);
		lastY = te.getField(3);
		lastX = te.getField(2);
		lastDrained = te.getField(1);
		powerLast = te.getField(0);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value){
		if(id == 5){
			te.rsMode = ForceDeviceControlType.get(value);
		}else
			te.setField(id, value);
	}
}
