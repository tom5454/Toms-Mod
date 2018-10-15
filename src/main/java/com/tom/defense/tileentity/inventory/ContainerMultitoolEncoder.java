package com.tom.defense.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.item.IIdentityCard;
import com.tom.api.tileentity.IGuiTile;
import com.tom.core.CoreInit;
import com.tom.defense.DefenseInit;
import com.tom.defense.tileentity.inventory.ContainerSecurityStation.SlotIndentityCard;
import com.tom.handler.GuiHandler.GuiIDs;
import com.tom.util.TomsModUtils;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerMultitoolEncoder extends ContainerTomsMod implements IGuiTile {
	public ItemStack writerStack;
	public IInventory inventory;
	public EntityPlayer player;

	public ContainerMultitoolEncoder(EntityPlayer player, final ItemStack is) {
		this.writerStack = is;
		this.player = player;
		this.inventory = new InventoryBasic("writer", false, 3) {

			@Override
			public void markDirty() {
				EntityPlayer player = ContainerMultitoolEncoder.this.player;
				if (!player.getHeldItemMainhand().hasTagCompound())
					player.getHeldItemMainhand().setTagCompound(new NBTTagCompound());
				player.getHeldItemMainhand().getTagCompound().setTag("inventory", TomsModUtils.saveAllItems(this));
			}

			@Override
			public void openInventory(EntityPlayer player) {
				if (!player.world.isRemote) {
					NBTTagCompound compound = is.hasTagCompound() ? is.getTagCompound() : new NBTTagCompound();
					TomsModUtils.loadAllItems(compound.getTagList("inventory", 10), this);
				}
			}

			@Override
			public void closeInventory(EntityPlayer player) {
				if (!player.world.isRemote) {
					if (!player.getHeldItemMainhand().hasTagCompound())
						player.getHeldItemMainhand().setTagCompound(new NBTTagCompound());
					player.getHeldItemMainhand().getTagCompound().setTag("inventory", TomsModUtils.saveAllItems(this));
				}
			}
		};
		this.inventory.openInventory(player);
		this.addSlotToContainer(new SlotIndentityCard(inventory, true, 0, 32, 18));
		this.addSlotToContainer(new SlotIndentityCardOutput(inventory, 1, 50, 18));
		this.addSlotToContainer(new SlotProjectorLens(inventory, 2, 50, 50));
		this.addPlayerSlotsExceptHeldItem(player.inventory, 8, 94);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		inventory.closeInventory(playerIn);
	}

	public static class SlotIndentityCardOutput extends Slot {
		public SlotIndentityCardOutput(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}

		@Override
		public int getSlotStackLimit() {
			return 1;
		}
	}

	public static class SlotProjectorLens extends Slot {
		public SlotProjectorLens(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem() == DefenseInit.projectorLens;
		}

		@Override
		public int getSlotStackLimit() {
			return 1;
		}
	}

	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if (id == 1) {
			ItemStack blankCard = inventory.getStackInSlot(0);
			if (!blankCard.isEmpty() && blankCard.getItem() instanceof IIdentityCard && ((IIdentityCard) blankCard.getItem()).isEmpty(blankCard) && inventory.getStackInSlot(1).isEmpty()) {
				ItemStack is = inventory.decrStackSize(0, 1);
				((IIdentityCard) is.getItem()).setUsername(is, player.getName());
				inventory.setInventorySlotContents(1, is);
			}
		} else if (id == 0) {
			ItemStack is = inventory.getStackInSlot(2);
			if (!is.isEmpty() && is.getItem() == DefenseInit.projectorLens) {
				player.openGui(CoreInit.modInstance, GuiIDs.projectorLensConfigMain.ordinal(), player.world, 2, 0, 0);
			}
		}
	}
}
