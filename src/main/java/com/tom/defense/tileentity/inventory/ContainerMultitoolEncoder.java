package com.tom.defense.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.tom.api.item.IIdentityCard;
import com.tom.api.tileentity.IGuiTile;
import com.tom.core.CoreInit;
import com.tom.defense.DefenseInit;
import com.tom.defense.tileentity.inventory.ContainerSecurityStation.SlotIndentityCard;
import com.tom.handler.GuiHandler.GuiIDs;

import com.tom.core.tileentity.inventory.ContainerTomsMod;

public class ContainerMultitoolEncoder extends ContainerTomsMod implements IGuiTile{
	public ItemStack writerStack;
	public IInventory inventory;
	public EntityPlayer player;
	public ContainerMultitoolEncoder(EntityPlayer player, final ItemStack is) {
		this.writerStack = is;
		this.player = player;
		this.inventory = new IInventory(){
			private ItemStack[] stack = new ItemStack[this.getSizeInventory()];
			@Override
			public String getName() {
				return "writer";
			}

			@Override
			public boolean hasCustomName() {
				return false;
			}

			@Override
			public ITextComponent getDisplayName() {
				return new TextComponentString(this.getName());
			}

			@Override
			public int getSizeInventory() {
				return 3;
			}

			@Override
			public ItemStack getStackInSlot(int index) {
				return stack[index];
			}

			@Override
			public ItemStack decrStackSize(int slot, int par2) {
				if (this.stack[slot] != null) {
					ItemStack itemstack;
					if (this.stack[slot].stackSize <= par2) {
						itemstack = this.stack[slot];
						this.stack[slot] = null;
						return itemstack;
					} else {
						itemstack = this.stack[slot].splitStack(par2);

						if (this.stack[slot].stackSize == 0) {
							this.stack[slot] = null;
						}
						return itemstack;
					}
				} else {
					return null;
				}
			}

			@Override
			public ItemStack removeStackFromSlot(int index) {
				ItemStack is = stack[index];
				stack[index] = null;
				return is;
			}

			@Override
			public void setInventorySlotContents(int index, ItemStack stack) {
				this.stack[index] = stack;
			}

			@Override
			public int getInventoryStackLimit() {
				return 64;
			}

			@Override
			public void markDirty() {
				EntityPlayer player = ContainerMultitoolEncoder.this.player;
				NBTTagList list = new NBTTagList();
				for(int i = 0;i<stack.length;i++){
					if(stack[i] != null){
						NBTTagCompound tag = new NBTTagCompound();
						stack[i].writeToNBT(tag);
						tag.setByte("Slot", (byte) i);
						list.appendTag(tag);
					}
				}
				if(!player.getHeldItemMainhand().hasTagCompound())player.getHeldItemMainhand().setTagCompound(new NBTTagCompound());
				player.getHeldItemMainhand().getTagCompound().setTag("inventory", list);
			}

			@Override
			public boolean isUseableByPlayer(EntityPlayer player) {
				return true;
			}

			@Override
			public void openInventory(EntityPlayer player) {
				if(!player.worldObj.isRemote){
					NBTTagCompound compound = is.hasTagCompound() ? is.getTagCompound() : new NBTTagCompound();
					stack = new ItemStack[this.getSizeInventory()];
					NBTTagList list = compound.getTagList("inventory", 10);
					for (int i = 0; i < list.tagCount(); ++i)
					{
						NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
						int j = nbttagcompound.getByte("Slot") & 255;

						if (j >= 0 && j < this.stack.length)
						{
							this.stack[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
						}
					}
					//System.out.println(stack);
					/*for (int i = 0; i < inventorySlots.size(); ++i)
			        {
			            ItemStack itemstack = inventorySlots.get(i).getStack();
			            ItemStack itemstack1 = inventoryItemStacks.get(i);

			            itemstack1 = itemstack == null ? null : itemstack.copy();
			            inventoryItemStacks.set(i, itemstack1);

			            for (int j = 0; j < crafters.size(); ++j)
			            {
			            	crafters.get(j).sendSlotContents(ContainerMultitoolEncoder.this, i, itemstack1);
			            }
			        }*/
				}
			}

			@Override
			public void closeInventory(EntityPlayer player) {
				if(!player.worldObj.isRemote){
					NBTTagList list = new NBTTagList();
					for(int i = 0;i<stack.length;i++){
						if(stack[i] != null){
							NBTTagCompound tag = new NBTTagCompound();
							stack[i].writeToNBT(tag);
							tag.setByte("Slot", (byte) i);
							list.appendTag(tag);
						}
					}
					if(!player.getHeldItemMainhand().hasTagCompound())player.getHeldItemMainhand().setTagCompound(new NBTTagCompound());
					player.getHeldItemMainhand().getTagCompound().setTag("inventory", list);
				}
			}

			@Override
			public boolean isItemValidForSlot(int index, ItemStack stack) {
				return true;
			}

			@Override
			public int getField(int id) {
				return 0;
			}

			@Override
			public void setField(int id, int value) {

			}

			@Override
			public int getFieldCount() {
				return 0;
			}

			@Override
			public void clear() {
				stack = new ItemStack[this.getSizeInventory()];
			}
		};
		this.inventory.openInventory(player);
		this.addSlotToContainer(new SlotIndentityCard(inventory, true, 0, 32, 18));
		this.addSlotToContainer(new SlotIndentityCardOutput(inventory, 1, 50, 18));
		this.addSlotToContainer(new SlotProjectorLens(inventory, 2, 50,50));
		this.addPlayerSlotsExceptHeldItem(player.inventory, 8, 94);
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return null;
	}
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		inventory.closeInventory(playerIn);
	}
	public static class SlotIndentityCardOutput extends Slot{
		public SlotIndentityCardOutput(IInventory inventoryIn, int index,
				int xPosition, int yPosition) {
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
	public static class SlotProjectorLens extends Slot{
		public SlotProjectorLens(IInventory inventoryIn, int index,
				int xPosition, int yPosition) {
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
		if(id == 1){
			ItemStack blankCard = inventory.getStackInSlot(0);
			if(blankCard != null && blankCard.getItem() instanceof IIdentityCard && ((IIdentityCard)blankCard.getItem()).isEmpty(blankCard) && inventory.getStackInSlot(1) == null){
				ItemStack is = inventory.decrStackSize(0, 1);
				((IIdentityCard)is.getItem()).setUsername(is, player.getName());
				inventory.setInventorySlotContents(1, is);
			}
		}else if(id == 0){
			ItemStack is = inventory.getStackInSlot(2);
			if(is != null && is.getItem() == DefenseInit.projectorLens){
				player.openGui(CoreInit.modInstance, GuiIDs.projectorLensConfigMain.ordinal(), player.worldObj, 2,0,0);
			}
		}
	}
}
