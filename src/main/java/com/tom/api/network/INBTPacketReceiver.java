package com.tom.api.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface INBTPacketReceiver {
	void receiveNBTPacket(NBTTagCompound message);

	public static interface IANBTPacketReceiver {
		void receiveNBTPacket(NBTTagCompound message, EntityPlayer from);
	}
}
