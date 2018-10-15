package com.tom.factory.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.tom.api.block.ICustomItemBlock;

public class BlockCoalCoke extends Block implements ICustomItemBlock{

	public BlockCoalCoke() {
		super(Material.ROCK);
	}

	@Override
	public ItemBlock createItemBlock() {
		return new ItemBlock(this){
			@Override
			public int getItemBurnTime(ItemStack itemStack) {
				return 32000;
			}
		};
	}
}
