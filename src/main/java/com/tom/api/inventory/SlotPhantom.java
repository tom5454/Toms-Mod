package com.tom.api.inventory;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class SlotPhantom extends Slot implements ITooltipSlot {
	public int maxStackSize;

	public SlotPhantom(IInventory inv, int slotIndex, int posX, int posY) {
		this(inv, slotIndex, posX, posY, 1);
	}

	public SlotPhantom(IInventory inv, int slotIndex, int posX, int posY, int maxStackSize) {
		super(inv, slotIndex, posX, posY);
		this.maxStackSize = maxStackSize;
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return false;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return true;
	}

	@Override
	public boolean showTooltip() {
		return true;
	}

	@Override
	public void getTooltip(List<String> lines) {
		lines.add(I18n.format("tomsmod.gui.phantomS.shift"));
		lines.add(I18n.format("tomsmod.gui.plantomS.ctrl", ContainerTomsMod.phantomSlotChange));
	}
}
