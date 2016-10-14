package com.tom.core.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Holotape extends Item {
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List list, boolean isAdvanced)
	{
		super.addInformation(is, player, list, isAdvanced);
		if(is.getTagCompound() != null && is.getTagCompound().hasKey("name")){
			String name = is.getTagCompound().getString("name");
			list.add(name);
		}
	}
}
