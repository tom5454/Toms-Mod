package com.tom.api.tileentity;

import net.minecraft.entity.player.EntityPlayer;

public interface ISecurityStation {
	boolean canPlayerAccess(AccessType type, EntityPlayer player);
}
