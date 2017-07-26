package com.tom.core.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.item.ItemCraftingTool;
import com.tom.core.TMResource;

public class WireCutters extends ItemCraftingTool {

	@Override
	public int getDurability(ItemStack stack) {
		return TMResource.getDurability(stack.getMetadata());
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "_" + TMResource.get(stack.getMetadata()).getName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		tooltip.add(I18n.format("tomsMod.tooltip.tier") + ": " + TMResource.get(stack.getMetadata()).getToolTier());
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		TMResource.addCuttersToList(subItems, itemIn);
	}
}
