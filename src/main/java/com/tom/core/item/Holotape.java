package com.tom.core.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Holotape extends Item {
	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, World player, List list, ITooltipFlag isAdvanced) {
		if (is.getTagCompound() != null && is.getTagCompound().hasKey("name")) {
			String name = is.getTagCompound().getString("name");
			list.add(name);
		}
	}
}
