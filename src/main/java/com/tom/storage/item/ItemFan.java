package com.tom.storage.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.item.IFan;
import com.tom.core.CoreInit;

public class ItemFan extends Item implements IFan, IModelRegisterRequired {
	public ItemFan() {
		setHasSubtypes(true);
	}

	@Override
	public void registerModels() {
		for (FanType t : FanType.VALUES) {
			CoreInit.registerRender(this, t.ordinal(), "tomsmodstorage:fan" + t.getName());
		}
	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye
	 * returns 16 items)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab))
			for (FanType t : FanType.VALUES) {
				subItems.add(new ItemStack(this, 1, t.ordinal()));
			}
	}

	public static enum FanType implements IStringSerializable {
		SMALL(1, 0.5), LARGE(3, 0.9),;
		public static final FanType[] VALUES = values();

		private FanType(double powerUsage, double heat) {
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}

	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "_" + get(stack).getName();
	}

	private static FanType get(ItemStack s) {
		return FanType.VALUES[s.getMetadata() % FanType.VALUES.length];
	}
}
