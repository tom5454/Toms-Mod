package com.tom.core.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConnectionModem extends Item {
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean isAdvanced)
	{
		super.addInformation(itemStack, player, list, isAdvanced);
		if (itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("tier")) {
			list.add(I18n.format("tomsMod.tooltip.tier")+": "+itemStack.getTagCompound().getInteger("tier"));
		}else{
			list.add(I18n.format("tomsMod.tooltip.tier")+": 0");
		}
	}
}
