package com.tom.storage.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

import com.tom.storage.handler.ICraftingTerminal;
import com.tom.storage.tileentity.inventory.ContainerCraftingTerminal;
import com.tom.storage.tileentity.inventory.ContainerCraftingTerminal.SlotTerminalCrafting;
import com.tom.util.TomsModUtils;

public class TileEntityCraftingTerminal extends TileEntityBasicTerminal implements ICraftingTerminal {
	public InventoryCraftResult craftResult = new InventoryCraftResult();
	public InventoryBasic craftingInv = new InventoryBasic("", false, 9);

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if (id == 0)
			terminalMode = extra % 3;
		else if (id == 1) {
			for (int i = 0;i < 9;i++) {
				ItemStack s = craftingInv.getStackInSlot(i);
				craftingInv.setInventorySlotContents(i, grid.pushStack(s));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("termMode", terminalMode);
		TomsModUtils.writeInventory("crafting", compound, craftingInv);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		terminalMode = compound.getInteger("termMode");
		TomsModUtils.loadAllItems(compound, "crafting", craftingInv);
	}

	@Override
	public void receiveNBTPacket(EntityPlayer player, NBTTagCompound message) {
		if (message.hasKey("color")) {
			super.receiveNBTPacket(player, message);
		} else {
			ItemStack[][] stacks = new ItemStack[9][];
			NBTTagList list = message.getTagList("i", 10);
			for (int i = 0;i < list.tagCount();i++) {
				NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
				byte slot = nbttagcompound.getByte("s");
				byte l = nbttagcompound.getByte("l");
				stacks[slot] = new ItemStack[l];
				for (int j = 0;j < l;j++) {
					NBTTagCompound tag = nbttagcompound.getCompoundTag("i" + j);
					stacks[slot][j] = TomsModUtils.loadItemStackFromNBT(tag);
				}
			}
			handlerItemTransfer(player, stacks);
		}
	}

	private void handlerItemTransfer(EntityPlayer player, ItemStack[][] items) {
		for (int i = 0;i < 9;i++) {
			if (items[i] != null) {
				ItemStack stack = ItemStack.EMPTY;
				for (int j = 0;j < items[i].length;j++) {
					ItemStack pulled = grid.pullStack(items[i][j]);
					if (!pulled.isEmpty()) {
						stack = pulled;
						break;
					}
				}
				if (stack.isEmpty()) {
					for (int j = 0;j < items[i].length;j++) {
						boolean br = false;
						for (int k = 0;k < player.inventory.getSizeInventory();k++) {
							if (TomsModUtils.areItemStacksEqual(player.inventory.getStackInSlot(k), items[i][j], true, false, false)) {
								stack = player.inventory.decrStackSize(k, 1);
								br = true;
								break;
							}
						}
						if (br)
							break;
					}
				}
				if (!stack.isEmpty()) {
					craftingInv.setInventorySlotContents(i, stack);
				}
			}
		}
	}

	@Override
	public void craft(EntityPlayer player, ContainerCraftingTerminal container, SlotTerminalCrafting slot) {
		if (world.isRemote)
			return;
		int crafted = 0;
		int amount = slot.getStack().getCount();
		ItemStack[] stacks = new ItemStack[9];
		for (int i = 0;i < 9;i++) {
			stacks[i] = TomsModUtils.copyItemStack(craftingInv.getStackInSlot(i));
		}
		while (crafted + amount <= slot.getStack().getMaxStackSize() && !slot.getStack().isEmpty()) {
			ItemStack stack1 = slot.getStack().copy();
			net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack1, craftingInv);
			slot.onCrafting(stack1);
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(player);
			NonNullList<ItemStack> aitemstack = CraftingManager.getRemainingItems(TomsModUtils.wrapCraftingInv(craftingInv, player, craftResult), world);
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

			for (int i = 0;i < aitemstack.size();++i) {
				Item original = Items.AIR;
				ItemStack itemstack = this.craftingInv.getStackInSlot(i);
				ItemStack itemstack1 = aitemstack.get(i);

				if (!itemstack.isEmpty()) {
					original = itemstack.getItem();
					this.craftingInv.decrStackSize(i, 1);
					itemstack = this.craftingInv.getStackInSlot(i);
				}

				if (!itemstack1.isEmpty()) {
					if (itemstack.isEmpty() && original != Items.AIR && original == itemstack1.getItem()) {
						this.craftingInv.setInventorySlotContents(i, itemstack1);
					} else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
						itemstack1.grow(itemstack.getCount());
						this.craftingInv.setInventorySlotContents(i, itemstack1);
					} else if (!player.inventory.addItemStackToInventory(itemstack1)) {
						ItemStack retStack = grid.pushStack(itemstack1);
						if (!retStack.isEmpty())
							player.dropItem(retStack, false);
					}
				}
			}
			crafted += amount;
			for (int i = 0;i < 9;i++) {
				if (!stacks[i].isEmpty() && craftingInv.getStackInSlot(i).isEmpty()) {
					craftingInv.setInventorySlotContents(i, grid.pullStack(stacks[i]));
				}
			}
			container.tryMergeStacks(stack1);
			if (stack1.getCount() > 0) {
				if (player.inventory.getItemStack().isEmpty()) {
					player.inventory.setItemStack(stack1);
				} else if (ItemStack.areItemsEqual(player.inventory.getItemStack(), stack1) && ItemStack.areItemStackTagsEqual(player.inventory.getItemStack(), stack1)) {
					player.inventory.getItemStack().grow(stack1.getCount());
				} else {
					ItemStack retStack = grid.pushStack(stack1);
					if (!retStack.isEmpty())
						player.dropItem(retStack, false);
				}
				break;
			}
		}
		container.detectAndSendChanges();
	}

	@Override
	public double getPowerDrained() {
		return 1.1;
	}

	@Override
	public IInventory getCraftingInv() {
		return craftingInv;
	}

	@Override
	public InventoryCraftResult getCraftResult() {
		return craftResult;
	}
}
