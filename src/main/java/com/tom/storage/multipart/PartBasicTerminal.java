package com.tom.storage.multipart;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.tom.storage.StorageInit;
import com.tom.storage.tileentity.gui.GuiBasicTerminal;
import com.tom.storage.tileentity.inventory.ContainerBasicTerminal;

public class PartBasicTerminal extends PartTerminal {

	@Override
	public Object getGui(EntityPlayer player) {
		return new GuiBasicTerminal(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player) {
		return new ContainerBasicTerminal(player.inventory, this);
	}

	@Override
	public ItemStack getStack() {
		return createStack(StorageInit.partTerminal);
	}
}
