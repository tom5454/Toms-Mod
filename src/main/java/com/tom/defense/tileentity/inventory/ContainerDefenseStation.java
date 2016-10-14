package com.tom.defense.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.inventory.SlotPhantom;
import com.tom.api.tileentity.IConfigurable.IConfigurationOption.SlotSecurityCard;
import com.tom.defense.DefenseInit;
import com.tom.defense.ForceDeviceControlType;
import com.tom.defense.tileentity.TileEntityDefenseStation;
import com.tom.defense.tileentity.TileEntityDefenseStation.DefenseStationConfig;
import com.tom.defense.tileentity.inventory.ContainerForceFieldProjector.SlotEfficiencyUpgrade;
import com.tom.defense.tileentity.inventory.ContainerSecurityStation.SlotPowerLinkCard;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;
import com.tom.network.messages.MessageProgress;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerDefenseStation extends ContainerTomsMod {
	private TileEntityDefenseStation te;
	private int modeLast = -1;
	private int powerLast = -1, lastRS = -1;
	private String lastName = "";
	private boolean lastWhiteList = false;
	private boolean lastMeta = false;
	private boolean lastMod = false;
	private boolean lastNBT = false;
	public ContainerDefenseStation(InventoryPlayer playerInv, TileEntityDefenseStation te) {
		addSlotToContainer(new SlotSecurityCard(te, 0, 227, 9));
		addSlotToContainer(new SlotPowerLinkCard(te, 1, 8, 102));
		addSlotToContainer(new SlotEfficiencyUpgrade(te, 2, 96, 102));
		addSlotToContainer(new SlotUpgradeWidth(te, 3, 137, 10));
		addSlotToContainer(new SlotUpgradeHeight(te, 4, 155, 10));
		addSlotToContainer(new SlotUpgradeWidth(te, 5, 173, 10));
		addSlotToContainer(new SlotUpgradeWidth(te, 6, 137, 41));
		addSlotToContainer(new SlotUpgradeHeight(te, 7, 155, 41));
		addSlotToContainer(new SlotUpgradeWidth(te, 8, 173, 41));
		for(int i = 0;i<4;i++){
			for(int j = 0;j<4;j++){
				addSlotToContainer(new Slot(te, 9 + j + i * 4, 174 + j * 18, 134 + i * 18));
			}
		}
		for(int i = 0;i<3;i++){
			for(int j = 0;j<7;j++){
				addSlotToContainer(new SlotPhantom(te, 25 + j + i * 7, 120 + j * 18, 76 + i * 18));
			}
		}
		this.addPlayerSlots(playerInv, 8, 134);
		this.te = te;
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUseableByPlayer(playerIn);
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
			if(te.getField(0) != powerLast){
				//crafter.sendProgressBarUpdate(this, 0, te.getField(0));
				msg.add(0, te.getField(0));
			}
			if(te.rsMode.ordinal() != lastRS){
				crafter.sendProgressBarUpdate(this, 1, te.rsMode.ordinal());
			}
			if(te.config.ordinal() != modeLast){
				crafter.sendProgressBarUpdate(this, 2, te.config.ordinal());
			}
			if(!te.customName.equals(lastName)){
				NetworkHandler.sendTo(new MessageNBT(te.getCustomNameMessage()), (EntityPlayerMP) crafter);
			}
			boolean sendData = lastWhiteList != te.isWhiteList() || lastMeta != te.useMeta() || lastMod != te.useMod() || lastNBT != te.useNBT();
			if(sendData){
				crafter.sendProgressBarUpdate(this, 3, te.isWhiteList() ? 1 : 0);
				crafter.sendProgressBarUpdate(this, 4, te.useMeta() ? 1 : 0);
				crafter.sendProgressBarUpdate(this, 5, te.useMod() ? 1 : 0);
				crafter.sendProgressBarUpdate(this, 6, te.useNBT() ? 1 : 0);
			}
			msg.send();
		}
		powerLast = te.getField(0);
		lastRS = te.rsMode.ordinal();
		modeLast = te.config.ordinal();
		lastName = te.customName;
		lastWhiteList = te.isWhiteList();
		lastMeta = te.useMeta();
		lastMod = te.useMod();
		lastNBT = te.useNBT();
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value){
		if(id == 1){
			te.rsMode = ForceDeviceControlType.get(value);
		}else if(id == 2){
			te.config = DefenseStationConfig.get(value);
		}else if(id == 3) {
			te.setWhiteList(value == 1);
		}else if(id == 4) {
			te.setUseMeta(value == 1);
		}else if(id == 5) {
			te.setUseMod(value == 1);
		}else if(id == 6) {
			te.setUseNBT(value == 1);
		}else
			te.setField(id, value);
	}
	public static class SlotUpgradeWidth extends Slot{

		public SlotUpgradeWidth(IInventory inv, int index,
				int xPosition, int yPosition) {
			super(inv, index, xPosition, yPosition);
		}
		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() == DefenseInit.rangeWidthUpgrade;
		}
	}
	public static class SlotUpgradeHeight extends Slot{

		public SlotUpgradeHeight(IInventory inv, int index,
				int xPosition, int yPosition) {
			super(inv, index, xPosition, yPosition);
		}
		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() == DefenseInit.rangeHeightUpgrade;
		}
	}
}
