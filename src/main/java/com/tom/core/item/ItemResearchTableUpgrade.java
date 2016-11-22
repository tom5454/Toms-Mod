package com.tom.core.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.tom.api.block.IIconRegisterRequired;
import com.tom.core.CoreInit;

import com.tom.core.tileentity.TileEntityResearchTable.ResearchTableType;

public class ItemResearchTableUpgrade extends Item implements IIconRegisterRequired{
	public ItemResearchTableUpgrade() {
		setHasSubtypes(true);
	}

	@Override
	public void registerIcons() {
		CoreInit.registerRender(this, 0, "tomsmodcore:researchTableUpgrade_bronze");
		CoreInit.registerRender(this, 1, "tomsmodcore:researchTableUpgrade_electrical");
	}
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
		subItems.add(new ItemStack(itemIn, 1, 1));
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		ResearchTableType t = ResearchTableType.getFromItem(stack.getMetadata());
		return super.getUnlocalizedName(stack) + (t != null ? "_" + t.getName() : "");
	}
}
