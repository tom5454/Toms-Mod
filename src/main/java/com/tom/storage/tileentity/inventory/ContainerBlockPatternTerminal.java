package com.tom.storage.tileentity.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.inventory.IJEIAutoFillTerminal;
import com.tom.api.inventory.SlotPhantom;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;
import com.tom.storage.StorageInit;
import com.tom.storage.tileentity.TileEntityPatternTerminal;
import com.tom.storage.tileentity.inventory.ContainerBlockInterface.SlotPattern;

public class ContainerBlockPatternTerminal extends ContainerTerminalBase implements IJEIAutoFillTerminal{
	private int crafting = -1, useContainerItems = -1;
	public ContainerBlockPatternTerminal(InventoryPlayer playerInv, TileEntityPatternTerminal te) {
		super(te, playerInv.player);
		addStorageSlots(5, 8, 18);
		addSlotToContainer(new SlotPattern(te.patternInv, 0, 150, 113, false));
		addSlotToContainer(new SlotPattern(te.patternInv, 1, 150, 151, true));
		for(int i = 0;i<3;i++)
			for(int j = 0;j<3;j++)
				addSlotToContainer(new SlotPhantom(te.recipeInv, i * 3 + j, 12 + j * 18, 110 + i * 18, 64));
		addSlotToContainer(new SlotPhantom(te.resultInv, 0, 106, 128, 64));
		addSlotToContainer(new SlotPhantom(te.resultInv, 1, 106, 155, 64));
		addSlotToContainer(new SlotPhantom(te.resultInv, 2, 125, 155, 64));
		addSlotToContainer(new SlotCraftingCard(te.upgradeInv, 0, 201, 6));
		this.addPlayerSlots(playerInv, 8, 174);
	}
	public static class SlotCraftingCard extends Slot{

		public SlotCraftingCard(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}
		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() == StorageInit.craftingCard;
		}
		@Override
		public int getSlotStackLimit() {
			return 1;
		}
	}
	@Override
	public void afterSending() {
		crafting = ((TileEntityPatternTerminal)te).getCraftingBehaviour();
		useContainerItems = ((TileEntityPatternTerminal)te).properties.useContainerItems ? 1 : 0;
	}
	@Override
	public void sendToCrafter(IContainerListener crafter) {
		int v = ((TileEntityPatternTerminal)te).getCraftingBehaviour();
		if(crafting != v){
			crafter.sendProgressBarUpdate(this, 10, v);
		}
		v = ((TileEntityPatternTerminal)te).properties.useContainerItems ? 1 : 0;
		if(useContainerItems != v){
			crafter.sendProgressBarUpdate(this, 11, v);
		}
	}
	@Override
	public void onProgressBarUpdate(int id, int data) {
		if(id == 10){
			((TileEntityPatternTerminal)te).setCraftingBehaviour(data);
		}else if(id == 11){
			((TileEntityPatternTerminal)te).properties.useContainerItems = data == 1;
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
		NetworkHandler.sendToServer(new MessageNBT(tag, te.getPos2()));
	}
}
