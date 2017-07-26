package com.tom.api.item;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public interface ICustomCraftingHandlerAdv {
	void onCrafingAdv(String player, ItemStack crafting, ItemStackAccess second, IInventory craftMatrix);

	void onUsingAdv(String player, ItemStack crafting, ItemStackAccess second, IInventory craftMatrix, ItemStack s) throws CraftingErrorException;

	public static class ItemStackAccess {
		private ItemStack stack;

		public ItemStack getStack() {
			return stack;
		}

		public void setStack(ItemStack stack) {
			this.stack = stack;
		}

		public ItemStackAccess(ItemStack stack) {
			this.stack = stack;
		}
	}

	public static class CraftingErrorException extends Exception {
		private static final long serialVersionUID = 4524518439893452334L;
		private ITextComponent comp;

		public CraftingErrorException(ITextComponent comp) {
			this.comp = comp;
		}

		public ITextComponent getTextComponent() {
			return comp;
		}
	}
}
