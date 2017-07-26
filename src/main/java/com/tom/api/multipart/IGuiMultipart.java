package com.tom.api.multipart;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import mcmultipart.api.slot.IPartSlot;

public interface IGuiMultipart {
	Object getGui(EntityPlayer player);

	Object getContainer(EntityPlayer player);

	void buttonPressed(EntityPlayer player, int id, int extra);

	BlockPos getPos2();

	IPartSlot getPosition();

	IBlockAccess getWorld2();
}
