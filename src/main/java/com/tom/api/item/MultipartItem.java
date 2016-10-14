package com.tom.api.item;

import net.minecraft.creativetab.CreativeTabs;

import mcmultipart.item.ItemMultiPart;

public abstract class MultipartItem extends ItemMultiPart {
	@Override
	public MultipartItem setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}
	@Override
	public MultipartItem setUnlocalizedName(String unlocalizedName) {
		super.setUnlocalizedName(unlocalizedName);
		return this;
	}
}
