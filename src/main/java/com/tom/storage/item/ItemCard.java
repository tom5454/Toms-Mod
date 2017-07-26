package com.tom.storage.item;

import java.util.Locale;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.core.CoreInit;
import com.tom.storage.StorageInit;

public class ItemCard extends Item implements IModelRegisterRequired {
	public ItemCard() {
		setHasSubtypes(true);
	}

	public static enum CardType {
		SPEED, CRAFTING;
		public static final CardType[] VALUES = values();

		public boolean equal(ItemStack stack) {
			return !stack.isEmpty() && stack.getItem() == StorageInit.card && stack.getMetadata() == ordinal();
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + CardType.VALUES[stack.getMetadata() % CardType.VALUES.length].name().toLowerCase(Locale.ROOT);
	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye
	 * returns 16 items)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (CardType t : CardType.VALUES) {
			subItems.add(new ItemStack(itemIn, 1, t.ordinal()));
		}
	}

	@Override
	public void registerModels() {
		String type = CoreInit.getNameForItem(this).replace("|", "");
		for (int i = 0;i < CardType.VALUES.length;i++)
			CoreInit.registerRender(this, i, type + "." + CardType.VALUES[i].name().toLowerCase(Locale.ROOT));
	}
}
