package com.tom.api.item;

import java.util.List;

import com.tom.api.tileentity.AccessType;

import net.minecraft.item.ItemStack;

public interface IIdentityCard {
	String getUsername(ItemStack stack);
	void setUsername(ItemStack stack, String name);
	boolean isEmpty(ItemStack stack);
	List<AccessType> getRights(ItemStack stack);
	void setRights(ItemStack stack, List<AccessType> rights);
}
