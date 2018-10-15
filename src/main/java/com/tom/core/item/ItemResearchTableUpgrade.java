package com.tom.core.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;

import com.tom.core.tileentity.TileEntityResearchTable.ResearchTableType;

public class ItemResearchTableUpgrade extends Item implements IModelRegisterRequired {
	public ItemResearchTableUpgrade() {
		setHasSubtypes(true);
	}

	@Override
	public void registerModels() {
		CoreInit.registerRender(this, 0, "tomsmodcore:researchTableUpgrade_bronze");
		CoreInit.registerRender(this, 1, "tomsmodcore:researchTableUpgrade_electrical");
		CoreInit.registerRender(this, 2, "tomsmodcore:researchTableUpgrade_mv");
		CoreInit.registerRender(this, 3, "tomsmodcore:researchTableUpgrade_hv");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)){
			subItems.add(new ItemStack(this, 1, 0));
			subItems.add(new ItemStack(this, 1, 1));
			subItems.add(new ItemStack(this, 1, 2));
			subItems.add(new ItemStack(this, 1, 3));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		ResearchTableType t = ResearchTableType.getFromItem(stack.getMetadata());
		return super.getUnlocalizedName(stack) + (t != null ? "_" + t.getName() : "");
	}
}
