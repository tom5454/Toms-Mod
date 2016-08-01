package com.tom.defense.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemFieldUpgrade extends Item {
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab,
			List<ItemStack> subItems) {
		for(int i = 0;i<UpgradeType.VALUES.length;i++)
			subItems.add(new ItemStack(itemIn, 1, i));
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "_" + UpgradeType.get(stack.getItemDamage()).getName();
	}
	public static enum UpgradeType{
		ZAPPER("zapper"), FUSION("fusion"), BREAK_BLOCK("breakBlock"), SPONGE("sponge"),
		;
		private final String name;
		public static final UpgradeType[] VALUES = values();
		private UpgradeType(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public static UpgradeType get(int index){
			return VALUES[index % VALUES.length];
		}
	}
}
