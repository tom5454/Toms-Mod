package com.tom.storage.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import com.tom.api.inventory.IJEIAutoFillTerminal;
import com.tom.api.multipart.IGuiMultipart;
import com.tom.network.NetworkHandler;
import com.tom.network.messages.MessageNBT;
import com.tom.storage.handler.ICraftingTerminal;
import com.tom.util.TomsModUtils;

public class ContainerCraftingTerminal extends ContainerTerminalBase implements IJEIAutoFillTerminal {
	// private int crafted;
	public ContainerCraftingTerminal(InventoryPlayer playerInv, ICraftingTerminal te) {
		super(te, playerInv.player);
		int x = -4;
		int y = 94;
		this.addSlotToContainer(new SlotTerminalCrafting(playerInv.player, TomsModUtils.wrapCraftingInv(te.getCraftingInv(), player, te.getCraftResult()), te.getCraftResult(), this, 0, x + 124, y + 35));
		for (int i = 0;i < 3;++i) {
			for (int j = 0;j < 3;++j) {
				this.addSlotToContainer(new Slot(te.getCraftingInv(), j + i * 3, x + 30 + j * 18, y + 17 + i * 18));
			}
		}
		this.addPlayerSlots(playerInv, 8, 174);
		addStorageSlots(5, 8, 18);
	}

	@Override
	public void sendMessage(NBTTagCompound tag) {
		if (te instanceof IGuiMultipart) {
			NetworkHandler.sendToServer(new MessageNBT(tag, (IGuiMultipart) te));
		} else
			te.sendUpdate(tag);
	}

	/**
	 * Called to determine if the current slot is valid for the stack merging
	 * (double-click) code. The stack passed in is null for the initial slot
	 * that was double-clicked.
	 */
	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		return slotIn.inventory != ((ICraftingTerminal) te).getCraftResult() && super.canMergeSlot(stack, slotIn);
	}

	@Override
	public ItemStack shiftClickItems(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		// int amount = 0;
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			// amount = itemstack.stackSize;
			// crafted += amount;
			if (index == 0) {
				/*if (!this.mergeItemStack(itemstack1, 10, 46, true))
				{
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
				if(slot instanceof SlotTerminalCrafting){
					((SlotTerminalCrafting)slot).doCraft(playerIn, itemstack1, true);
				}*/
				((ICraftingTerminal) te).craft(playerIn, this, (SlotTerminalCrafting) slot);
				return ItemStack.EMPTY;
			} else if (index > 0 && index < 10) {
				ItemStack stack = te.pushStack(itemstack);
				slot.putStack(stack);
				if (!playerIn.world.isRemote)
					detectAndSendChanges();
			}
			slot.onTake(playerIn, itemstack1);
		}
		/*boolean end = false;
		if(crafted + amount > 64){
			crafted = 0;
			end = true;
		}*/
		return ItemStack.EMPTY;
	}

	public static class SlotTerminalCrafting extends SlotCrafting// implements
	// ISlotClickListener
	{
		private final ContainerTerminalBase terminalContainer;
		// private ItemStack[] stacks = new ItemStack[9];
		private final InventoryCrafting inventoryCrafting;

		public SlotTerminalCrafting(EntityPlayer player, InventoryCrafting craftingInventory, IInventory inventoryIn, ContainerTerminalBase terminal, int slotIndex, int xPosition, int yPosition) {
			super(player, craftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
			this.terminalContainer = terminal;
			this.inventoryCrafting = craftingInventory;
		}

		@Override
		public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
			if (thePlayer.world.isRemote)
				return ItemStack.EMPTY;
			this.onCrafting(stack);
			ItemStack[] stacks = new ItemStack[9];
			for (int i = 0;i < 9;i++) {
				stacks[i] = TomsModUtils.copyItemStack(inventoryCrafting.getStackInSlot(i));
			}
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(thePlayer);
			NonNullList<ItemStack> nonnulllist = CraftingManager.getRemainingItems(this.inventoryCrafting, thePlayer.world);
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

			for (int i = 0;i < nonnulllist.size();++i) {
				ItemStack itemstack = this.inventoryCrafting.getStackInSlot(i);
				ItemStack itemstack1 = nonnulllist.get(i);

				if (!itemstack.isEmpty()) {
					this.inventoryCrafting.decrStackSize(i, 1);
					itemstack = this.inventoryCrafting.getStackInSlot(i);
				}

				if (!itemstack1.isEmpty()) {
					if (itemstack.isEmpty()) {
						this.inventoryCrafting.setInventorySlotContents(i, itemstack1);
					} else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
						itemstack1.grow(itemstack.getCount());
						this.inventoryCrafting.setInventorySlotContents(i, itemstack1);
					} else if (!thePlayer.inventory.addItemStackToInventory(itemstack1)) {
						thePlayer.dropItem(itemstack1, false);
					}
				}
			}
			for (int j = 0;j < inventoryCrafting.getSizeInventory();j++) {
				terminalContainer.inventoryItemStacks.set(j, new ItemStack(Blocks.BARRIER));
				if (inventoryCrafting.getStackInSlot(j).isEmpty()) {
					if (stacks[j] != null && !stacks[j].isEmpty()) {
						ItemStack p = stacks[j].copy();
						p.setCount(1);
						ItemStack pulled = terminalContainer.te.pullStack(p);
						if (!pulled.isEmpty()) {
							inventoryCrafting.setInventorySlotContents(j, pulled);
						}
					}
				} else if (!TomsModUtils.areItemStacksEqual(inventoryCrafting.getStackInSlot(j), stacks[j], true, true, false)) {
					inventoryCrafting.setInventorySlotContents(j, terminalContainer.te.pushStack(inventoryCrafting.getStackInSlot(j)));
					if (inventoryCrafting.getStackInSlot(j).isEmpty()) {
						if (stacks[j] != null && !stacks[j].isEmpty()) {
							ItemStack p = stacks[j].copy();
							p.setCount(1);
							ItemStack pulled = terminalContainer.te.pullStack(p);
							if (!pulled.isEmpty()) {
								inventoryCrafting.setInventorySlotContents(j, pulled);
							}
						}
					}
				}
			}
			terminalContainer.detectAndSendChanges();
			return stack;
		}

		/*@Override
		public void onTake(EntityPlayer playerIn, ItemStack stack) {
			ItemStack[] stacks = new ItemStack[9];
			for(int i = 0;i<9;i++){
				stacks[i] = inventoryCrafting.getStackInSlot(i).copy();
			}
			net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, inventoryCrafting);
			this.onCrafting(stack);
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(playerIn);
			NonNullList<ItemStack> aitemstack = CraftingManager.getInstance().getRemainingItems(this.inventoryCrafting, playerIn.world);
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

			for (int i = 0; i < aitemstack.size(); ++i)
			{
				Item original = null;
				ItemStack itemstack = this.inventoryCrafting.getStackInSlot(i);
				ItemStack itemstack1 = aitemstack.get(i);

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
						itemstack1.grow(itemstack.getCount());
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
		 */
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

	public boolean tryMergeStacks(ItemStack stack) {
		return this.mergeItemStack(stack, 10, 46, true);
	}
}
