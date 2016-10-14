package com.tom.storage.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.inventory.IJEIAutoFillTerminal;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;
import com.tom.storage.tileentity.TileEntityCraftingTerminal;

public class ContainerBlockCraftingTerminal extends ContainerTerminalBase implements IJEIAutoFillTerminal{
	//private int crafted;
	public ContainerBlockCraftingTerminal(InventoryPlayer playerInv, TileEntityCraftingTerminal te) {
		super(te, playerInv.player);
		int x = -4;
		int y = 94;
		this.addSlotToContainer(new SlotTerminalCrafting(playerInv.player, te.craftingInv, te.craftResult, this, 0, x + 124, y + 35));
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 3; ++j)
			{
				this.addSlotToContainer(new Slot(te.craftingInv, j + i * 3, x + 30 + j * 18, y + 17 + i * 18));
			}
		}
		this.addPlayerSlots(playerInv, 8, 174);
		addStorageSlots(5, 8, 18);
	}

	@Override
	public void sendMessage(NBTTagCompound tag) {
		NetworkHandler.sendToServer(new MessageNBT(tag, te.getPos2()));
	}
	/**
	 * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
	 * is null for the initial slot that was double-clicked.
	 */
	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn)
	{
		return slotIn.inventory != ((TileEntityCraftingTerminal)te).craftResult && super.canMergeSlot(stack, slotIn);
	}
	@Override
	public ItemStack shiftClickItems(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = this.inventorySlots.get(index);
		//int amount = 0;
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			//amount = itemstack.stackSize;
			//crafted += amount;
			if (index == 0)
			{
				/*if (!this.mergeItemStack(itemstack1, 10, 46, true))
				{
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
				if(slot instanceof SlotTerminalCrafting){
					((SlotTerminalCrafting)slot).doCraft(playerIn, itemstack1, true);
				}*/
				((TileEntityCraftingTerminal) te).craft(playerIn, this, (SlotTerminalCrafting) slot);
				return null;
			}else if(index > 0 && index < 10){
				ItemStack stack = te.getGrid().pushStack(itemstack);
				slot.putStack(stack);
				if(!playerIn.worldObj.isRemote)detectAndSendChanges();
			}
			slot.onPickupFromSlot(playerIn, itemstack1);
		}
		/*boolean end = false;
		if(crafted + amount > 64){
			crafted = 0;
			end = true;
		}*/
		return null;
	}
	public static class SlotTerminalCrafting extends SlotCrafting// implements ISlotClickListener
	{
		private final ContainerTerminalBase terminalContainer;
		//private ItemStack[] stacks = new ItemStack[9];
		private final InventoryCrafting inventoryCrafting;
		public SlotTerminalCrafting(EntityPlayer player, InventoryCrafting craftingInventory, IInventory inventoryIn, ContainerTerminalBase terminal,
				int slotIndex, int xPosition, int yPosition) {
			super(player, craftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
			this.terminalContainer = terminal;
			this.inventoryCrafting = craftingInventory;
		}
		@Override
		public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
			ItemStack[] stacks = new ItemStack[9];
			for(int i = 0;i<9;i++){
				stacks[i] = ItemStack.copyItemStack(inventoryCrafting.getStackInSlot(i));
			}
			net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, inventoryCrafting);
			this.onCrafting(stack);
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(playerIn);
			ItemStack[] aitemstack = CraftingManager.getInstance().getRemainingItems(this.inventoryCrafting, playerIn.worldObj);
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

			for (int i = 0; i < aitemstack.length; ++i)
			{
				Item original = null;
				ItemStack itemstack = this.inventoryCrafting.getStackInSlot(i);
				ItemStack itemstack1 = aitemstack[i];

				if (itemstack != null)
				{
					original = itemstack.getItem();
					this.inventoryCrafting.decrStackSize(i, 1);
					itemstack = this.inventoryCrafting.getStackInSlot(i);
				}

				if (itemstack1 != null)
				{
					if (itemstack == null && original != null && original == itemstack1.getItem())
					{
						this.inventoryCrafting.setInventorySlotContents(i, itemstack1);
					}
					else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1))
					{
						itemstack1.stackSize += itemstack.stackSize;
						this.inventoryCrafting.setInventorySlotContents(i, itemstack1);
					}
					else if (!playerIn.inventory.addItemStackToInventory(itemstack1))
					{
						ItemStack retStack = terminalContainer.te.getGrid().pushStack(itemstack1);
						if(retStack != null)
							playerIn.dropItem(retStack, false);
					}
				}
			}
			boolean refilled = false;
			for(int j = 0;j<inventoryCrafting.getSizeInventory();j++){
				terminalContainer.inventoryItemStacks.set(j, null);
				if(inventoryCrafting.getStackInSlot(j) == null){
					ItemStack pulled = terminalContainer.te.getGrid().pullStack(inventoryCrafting.getStackInSlot(j));
					if(pulled != null){
						inventoryCrafting.setInventorySlotContents(j, pulled);
						refilled = true;
					}
				}
			}
			if(refilled){
				terminalContainer.detectAndSendChanges();
			}
		}
		@Override
		public void onCrafting(ItemStack stack) {
			super.onCrafting(stack);
		}
		/*@Override
		public void slotClick(int slotId, int clickedButton, ClickType clickTypeIn, EntityPlayer playerIn) {
			ItemStack[] stacks = new ItemStack[9];
			for(int i = 0;i<9;i++){
				stacks[i] = ItemStack.copyItemStack(inventoryCrafting.getStackInSlot(i));
			}
		}*/
	}
	public boolean tryMergeStacks(ItemStack stack){
		return this.mergeItemStack(stack, 10, 46, true);
	}
}
