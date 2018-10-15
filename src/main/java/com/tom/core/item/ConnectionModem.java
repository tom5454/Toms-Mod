package com.tom.core.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConnectionModem extends Item {
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("tier")) {
			tooltip.add(I18n.format("tomsMod.tooltip.tier") + ": " + stack.getTagCompound().getInteger("tier"));
		} else {
			tooltip.add(I18n.format("tomsMod.tooltip.tier") + ": 0");
		}
	}
}
