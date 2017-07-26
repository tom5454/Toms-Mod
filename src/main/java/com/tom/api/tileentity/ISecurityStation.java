package com.tom.api.tileentity;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

public interface ISecurityStation {
	boolean canPlayerAccess(AccessType type, EntityPlayer player);

	boolean canPlayerAccess(AccessType stayInArea, UUID ownerId);
}
