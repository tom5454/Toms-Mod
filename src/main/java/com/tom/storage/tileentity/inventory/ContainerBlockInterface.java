package com.tom.storage.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.tom.api.inventory.SlotPhantom;
import com.tom.storage.multipart.StorageNetworkGrid.ICraftingRecipeContainer;
import com.tom.storage.tileentity.TileEntityInterface;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerBlockInterface extends ContainerTomsMod {
	private TileEntityInterface te;
	public ContainerBlockInterface(InventoryPlayer playerInv, TileEntityInterface te) {
		for(int i = 0;i<9;i++)addSlotToContainer(new SlotPattern(te, i, 8 + i * 18, 67, true));
		for(int i = 0;i<9;i++)addSlotToContainer(new SlotPhantom(te, i + 9, 8 + 18 * i, 20, 64));
		for(int i = 0;i<9;i++)addSlotToContainer(new Slot(te, i + 18, 8 + 18 * i, 38));
		addPlayerSlots(playerInv, 8, 94);
		this.te = te;
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUseableByPlayer(playerIn);
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return null;
	}
	public static class SlotPattern extends Slot{
		private final boolean encoded;
		public SlotPattern(IInventory inventoryIn, int index, int xPosition, int yPosition, boolean encoded) {
			super(inventoryIn, index, xPosition, yPosition);
			this.encoded = encoded;
		}
		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() instanceof ICraftingRecipeContainer && (encoded ? ((ICraftingRecipeContainer)stack.getItem()).getRecipe(stack) != null : ((ICraftingRecipeContainer)stack.getItem()).getRecipe(stack) == null);
		}
		@Override
		public int getSlotStackLimit() {
			return encoded ? 1 : 64;
		}
	}
}