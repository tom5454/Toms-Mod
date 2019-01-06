package com.tom.defense.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

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

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerDefenseStation extends ContainerTomsMod {
	private TileEntityDefenseStation te;
	private String lastName = "";

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
		for (int i = 0;i < 4;i++) {
			for (int j = 0;j < 4;j++) {
				addSlotToContainer(new Slot(te, 9 + j + i * 4, 174 + j * 18, 134 + i * 18));
			}
		}
		for (int i = 0;i < 3;i++) {
			for (int j = 0;j < 7;j++) {
				addSlotToContainer(new SlotPhantom(te, 25 + j + i * 7, 120 + j * 18, 76 + i * 18));
			}
		}
		this.addPlayerSlots(playerInv, 8, 134);
		this.te = te;
		syncHandler.registerInventoryFieldInt(te, 0);
		syncHandler.registerInventoryFieldInt(te, 1);
		syncHandler.registerEnum(0, () -> te.rsMode , e -> te.rsMode = e, ForceDeviceControlType.VALUES);
		syncHandler.registerEnum(1, () -> te.config, e -> te.config = e, DefenseStationConfig.VALUES);
		syncHandler.registerBoolean(2, te::isWhiteList, te::setWhiteList);
		syncHandler.registerBoolean(3, te::useMeta, te::setUseMeta);
		syncHandler.registerBoolean(4, te::useMod, te::setUseMod);
		syncHandler.registerBoolean(5, te::useNBT, te::setUseNBT);
		syncHandler.registerBoolean(5, te::isPlayerKill, te::setPlayerKill);
		syncHandler.registerString(2, () -> te.customName, e -> {});
		syncHandler.setReceiver(te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener crafter : listeners) {
			if (!te.customName.equals(lastName)) {
				NetworkHandler.sendTo(new MessageNBT(te.getCustomNameMessage()), (EntityPlayerMP) crafter);
			}
		}
		lastName = te.customName;
	}

	public static class SlotUpgradeWidth extends Slot {

		public SlotUpgradeWidth(IInventory inv, int index, int xPosition, int yPosition) {
			super(inv, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() == DefenseInit.rangeWidthUpgrade;
		}
	}

	public static class SlotUpgradeHeight extends Slot {

		public SlotUpgradeHeight(IInventory inv, int index, int xPosition, int yPosition) {
			super(inv, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() == DefenseInit.rangeHeightUpgrade;
		}
	}
}
