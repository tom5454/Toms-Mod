package com.tom.api.network;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTPacketSender {
	void writeToNBTPacket(NBTTagCompound tag);
}
