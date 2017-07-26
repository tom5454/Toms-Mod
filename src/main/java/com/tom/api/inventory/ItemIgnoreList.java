package com.tom.api.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.tom.storage.StorageInit;
import com.tom.storage.handler.ICraftable;

public class ItemIgnoreList {
	private static final List<ItemStack> ignoredItemStacks = new ArrayList<>();
	static {
		add(new ItemStack(StorageInit.drive));
		add(new ItemStack(StorageInit.craftingPattern));
		add(new ItemStack(StorageInit.itemStorageCell));
	}

	private static void add(ItemStack stack) {
		if (stack != null)
			ignoredItemStacks.add(stack);
	}

	public static boolean accepts(ICraftable c) {
		if (c == null)
			return false;
		if (c instanceof StoredItemStack) {
			StoredItemStack stack = (StoredItemStack) c;
			if (stack.getStack() == null)
				return false;
			ItemStack stackS = stack.getStack();
			Item item = stackS.getItem();
			if (Block.getBlockFromItem(item) != Blocks.AIR) {
				Block b = Block.getBlockFromItem(item);
				if (b == StorageInit.drive) {
					return !stackS.hasTagCompound() || !stackS.getTagCompound().getBoolean("stored");
				} else if (b instanceof BlockShulkerBox) { return !stackS.hasTagCompound() ? true : stackS.getTagCompound().getCompoundTag("BlockEntityTag").hasNoTags(); }
			}
			if (item == StorageInit.itemStorageCell) { return !stackS.hasTagCompound() || stackS.getTagCompound().getTagList("data", 10).hasNoTags() || stackS.getTagCompound().getTagList("inventory", 10).hasNoTags(); }
			for (int i = 0;i < ignoredItemStacks.size();i++) {
				ItemStack s = ignoredItemStacks.get(i);
				if (areItemsEqual(s, stackS))
					return false;
			}
		}
		return true;
	}

	public static boolean accepts(ItemStack s) {
		if (s == null)
			return false;
		for (int i = 0;i < ignoredItemStacks.size();i++) {
			ItemStack s2 = ignoredItemStacks.get(i);
			if (areItemsEqual(s, s2))
				return false;
		}
		return true;
	}

	private static boolean areItemsEqual(ItemStack s, ItemStack s2) {
		return s == null ? s2 == null : s.getItem() == s2.getItem();
	}
}
