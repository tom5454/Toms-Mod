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
import com.tom.defense.tileentity.TileEntityForceCapacitor;
import com.tom.network.messages.MessageProgress;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerForceCapacitor extends ContainerTomsMod {
	private int rangeLast = -1;
	private int linkedLast = -1;
	private int powerLast = -1, lastRS = -1;
	private TileEntityForceCapacitor te;
	public ContainerForceCapacitor(InventoryPlayer inv,
			TileEntityForceCapacitor te) {
		this.addSlotToContainer(new SlotRangeUpgrade(te,1,152,28));
		this.addSlotToContainer(new SlotSecurityCard(te, 0, 152, 46));
		this.addPlayerSlots(inv, 8, 94);
		this.te = te;
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return null;
	}
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for(IContainerListener crafter : listeners) {
			MessageProgress msg = new MessageProgress(crafter);
			if(te.getField(0) != linkedLast){
				crafter.sendProgressBarUpdate(this, 0, te.getField(0));
			}
			if(te.getField(1) != rangeLast){
				crafter.sendProgressBarUpdate(this, 1, te.getField(1));
			}
			if(te.getField(2) != powerLast){
				//crafter.sendProgressBarUpdate(this, 2, te.getField(2));
				msg.add(2, te.getField(2));
			}
			if(te.rsMode.ordinal() != lastRS){
				crafter.sendProgressBarUpdate(this, 3, te.rsMode.ordinal());
			}
			msg.send();
		}
		lastRS = te.rsMode.ordinal();
		powerLast = te.getField(2);
		rangeLast = te.getField(1);
		linkedLast = te.getField(0);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value){
		if(id == 3){
			te.rsMode = ForceDeviceControlType.get(value);
		}else
			te.setField(id, value);
	}
	public static class SlotRangeUpgrade extends Slot{

		public SlotRangeUpgrade(IInventory inventoryIn, int index,
				int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}
		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() == DefenseInit.rangeUpgrade;
		}
	}
}
