package com.tom.core.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.tom.api.item.IFuelRod;
import com.tom.core.CoreInit;

public class UraniumRod extends Item implements IFuelRod {
	public UraniumRod() {
		setMaxDamage(20000);
	}

	@Override
	public int getHeat(ItemStack is) {
		return 10;
	}

	@Override
	public ItemStack useSingle(ItemStack is) {
		if (is.getItemDamage() + 1 == is.getMaxDamage()) {
			return new ItemStack(CoreInit.dUraniumRod);
		} else
			is.setItemDamage(is.getItemDamage() + 1);
		return is;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add(I18n.format("item.durability", new Object[]{Integer.valueOf(stack.getMaxDamage() - stack.getItemDamage()), Integer.valueOf(stack.getMaxDamage())}));
	}
}
