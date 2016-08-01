package com.tom.api.tileentity;

import net.minecraft.entity.player.EntityPlayer;

public interface IGuiTile {
	public void buttonPressed(EntityPlayer player, int id, int extra);
}
