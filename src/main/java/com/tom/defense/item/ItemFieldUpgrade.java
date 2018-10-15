package com.tom.defense.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;

public class ItemFieldUpgrade extends Item implements IModelRegisterRequired {
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab))
			for (int i = 0;i < UpgradeType.VALUES.length;i++)
				subItems.add(new ItemStack(this, 1, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "_" + UpgradeType.get(stack.getItemDamage()).getName();
	}

	public static enum UpgradeType {
		ZAPPER("zapper"), FUSION("fusion"), BREAK_BLOCK("breakBlock"), SPONGE("sponge"),;
		private final String name;
		public static final UpgradeType[] VALUES = values();

		private UpgradeType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static UpgradeType get(int index) {
			return VALUES[index % VALUES.length];
		}
	}

	@Override
	public void registerModels() {
		CoreInit.registerRender(this, 0, "tomsmoddefense:projectorUpgrade_" + UpgradeType.get(0).getName());
		CoreInit.registerRender(this, 1, "tomsmoddefense:projectorUpgrade_" + UpgradeType.get(1).getName());
		CoreInit.registerRender(this, 2, "tomsmoddefense:projectorUpgrade_" + UpgradeType.get(2).getName());
		CoreInit.registerRender(this, 3, "tomsmoddefense:projectorUpgrade_" + UpgradeType.get(3).getName());
	}
}
