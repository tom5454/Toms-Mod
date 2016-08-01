package com.tom.api.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;

public interface ISlotClickListener {
	void slotClick(int slotId, int clickedButton, ClickType clickTypeIn, EntityPlayer playerIn);
}
