package com.tom.api.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface INBTPacketReceiver {
	void receiveNBTPacket(EntityPlayer from, NBTTagCompound message);
}
