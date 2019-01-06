package com.tom.storage.tileentity.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.inventory.IJEIAutoFillTerminal;
import com.tom.api.inventory.SlotPhantom;
import com.tom.api.tileentity.IPatternTerminal;
import com.tom.storage.item.ItemCard.CardType;
import com.tom.storage.tileentity.inventory.ContainerBlockInterface.SlotPattern;

public class ContainerPatternTerminal extends ContainerTerminalBase implements IJEIAutoFillTerminal {

	public ContainerPatternTerminal(InventoryPlayer playerInv, IPatternTerminal te) {
		super(te, playerInv.player);
		addStorageSlots(5, 8, 18);
		addSlotToContainer(new SlotPattern(te.getPatternInv(), 0, 150, 113, false));
		addSlotToContainer(new SlotPattern(te.getPatternInv(), 1, 150, 151, true));
		for (int i = 0;i < 3;i++)
			for (int j = 0;j < 3;j++)
				addSlotToContainer(new SlotPhantom(te.getRecipeInv(), i * 3 + j, 12 + j * 18, 110 + i * 18, 64));
		addSlotToContainer(new SlotPhantom(te.getResultInv(), 0, 106, 128, 64));
		addSlotToContainer(new SlotPhantom(te.getResultInv(), 1, 106, 155, 64));
		addSlotToContainer(new SlotPhantom(te.getResultInv(), 2, 125, 155, 64));
		addSlotToContainerS(new SlotCraftingCard(te.getUpgradeInv(), 0, 201, 6));
		this.addPlayerSlots(playerInv, 8, 174);
		syncHandler.registerShort(10, te::getCraftingBehaviour, te::setCraftingBehaviour);
		syncHandler.registerBoolean(11, () -> te.getProperties().useContainerItems, b -> te.getProperties().useContainerItems = b);
	}

	public static class SlotCraftingCard extends Slot {

		public SlotCraftingCard(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && CardType.CRAFTING.equal(stack);
		}

		@Override
		public int getSlotStackLimit() {
			return 1;
		}
	}

	/*@Override
	public void setRecipe(ItemStack[] input, ItemStack[] output) {
		if(input == null || output == null)return;
		TileEntityPatternTerminal term = (TileEntityPatternTerminal)te;
		term.recipeInv.clear();
		term.resultInv.clear();
		for(int i = 0;i<input.length && i < 9;i++){
			term.recipeInv.setInventorySlotContents(i, input[i]);
		}
		for(int i = 0;i<output.length && i < 3;i++){
			term.resultInv.setInventorySlotContents(i, output[i]);
		}
	}
	@Override
	public BlockPos getPos() {
		return te.getPos();
	}*/
	@Override
	public void sendMessage(NBTTagCompound tag) {
		te.sendUpdate(tag);
	}
}
