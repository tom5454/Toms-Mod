package com.tom.util;

import net.minecraft.item.ItemStack;

public class ItemStackComparator {
	private final ItemStack is;

	public ItemStackComparator(ItemStack is) {
		this.is = is;
	}

	@Override
	public boolean equals(Object other) {
		if (other != null) {
			if (other instanceof ItemStack) {
				ItemStack r = (ItemStack) other;
				return TomsModUtils.areItemStacksEqualOreDict(r, is, true, false, false, true);
			} else if (other instanceof ItemStackComparator) {
				ItemStackComparator r = (ItemStackComparator) other;
				return TomsModUtils.areItemStacksEqualOreDict(r.is, is, true, false, false, true);
			}
		}
		return false;
	}
}