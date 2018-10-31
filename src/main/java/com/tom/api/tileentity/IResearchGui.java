package com.tom.api.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IResearchGui {
	public ItemStack getIcon();
	public void openGui(EntityPlayer pl);
}
