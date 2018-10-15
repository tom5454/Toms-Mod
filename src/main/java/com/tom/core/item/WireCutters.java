package com.tom.core.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.item.ItemCraftingTool;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;

public class WireCutters extends ItemCraftingTool implements IModelRegisterRequired {

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
	public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		tooltip.add(I18n.format("tomsMod.tooltip.tier") + ": " + TMResource.get(stack.getMetadata()).getToolTier());
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab))TMResource.addCuttersToList(subItems, this);
	}

	@Override
	public void registerModels() {
		List<ItemStack> stackList = new ArrayList<>();
		TMResource.addCuttersToList(stackList, this);
		for (ItemStack s : stackList)
			CoreInit.registerRender(s, "tomsmodcore:resources/" + s.getUnlocalizedName().substring(5));
	}
}
