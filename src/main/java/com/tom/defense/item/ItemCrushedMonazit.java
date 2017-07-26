package com.tom.defense.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;

public class ItemCrushedMonazit extends Item implements IModelRegisterRequired {

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
		subItems.add(new ItemStack(itemIn, 1, 1));
	}

	@Override
	public void registerModels() {
		CoreInit.registerRender(this, 0, "tomsmoddefense:" + getUnlocalizedName(new ItemStack(this, 1, 0)).substring(5));
		CoreInit.registerRender(this, 1, "tomsmoddefense:" + getUnlocalizedName(new ItemStack(this, 1, 1)).substring(5));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return stack.getMetadata() > 0 ? super.getUnlocalizedName(stack) + ".end" : super.getUnlocalizedName(stack);
	}
}
