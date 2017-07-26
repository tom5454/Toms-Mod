package com.tom.api.item;

import net.minecraft.creativetab.CreativeTabs;

import com.tom.api.multipart.BlockMultipart;

import mcmultipart.api.item.ItemBlockMultipart;

public class MultipartItem extends ItemBlockMultipart {

	public MultipartItem(BlockMultipart block) {
		super(block, block);
	}

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
