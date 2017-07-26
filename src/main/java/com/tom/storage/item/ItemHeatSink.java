package com.tom.storage.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tom.api.block.IModelRegisterRequired;
import com.tom.api.item.IHeatSink;
import com.tom.core.CoreInit;

public class ItemHeatSink extends Item implements IModelRegisterRequired, IHeatSink {
	public ItemHeatSink() {
		setHasSubtypes(true);
	}

	@Override
	public void registerModels() {
		for (HeatSinkType t : HeatSinkType.VALUES) {
			CoreInit.registerRender(this, t.ordinal(), "tomsmodstorage:heatsink" + t.getName());
		}
	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye
	 * returns 16 items)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (HeatSinkType t : HeatSinkType.VALUES) {
			subItems.add(new ItemStack(this, 1, t.ordinal()));
		}
	}

	public static enum HeatSinkType implements IStringSerializable {
		SMALL(10, 30), LARGE(50, 100),;
		public static final HeatSinkType[] VALUES = values();
		private final double passiveHeat, activeHeat;

		private HeatSinkType(double passiveHeat, double activeHeat) {
			this.passiveHeat = passiveHeat;
			this.activeHeat = activeHeat;
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

	private static HeatSinkType get(ItemStack s) {
		return HeatSinkType.VALUES[s.getMetadata() % HeatSinkType.VALUES.length];
	}

	@Override
	public double getPassiveHeat(ItemStack s) {
		return get(s).passiveHeat;
	}

	@Override
	public double getHactiveHeat(ItemStack s) {
		return get(s).activeHeat;
	}
}
