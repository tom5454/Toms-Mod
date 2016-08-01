package com.tom.api.multipart;

import mcmultipart.multipart.PartSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public interface IGuiMultipart {
	Object getGui(EntityPlayer player);
	Object getContainer(EntityPlayer player);
	void buttonPressed(EntityPlayer player, int id, int extra);
	BlockPos getPos2();
	PartSlot getPosition();
}
