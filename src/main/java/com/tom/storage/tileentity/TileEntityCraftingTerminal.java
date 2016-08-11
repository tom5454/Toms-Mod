package com.tom.storage.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.tom.api.network.INBTPacketReceiver.IANBTPacketReceiver;
import com.tom.api.tileentity.TileEntityGridDeviceBase;
import com.tom.apis.TomsModUtils;
import com.tom.storage.multipart.StorageNetworkGrid;
import com.tom.storage.multipart.StorageNetworkGrid.ITerminal;
import com.tom.storage.tileentity.inventory.ContainerBlockCraftingTerminal;
import com.tom.storage.tileentity.inventory.ContainerBlockCraftingTerminal.SlotTerminalCrafting;

public class TileEntityCraftingTerminal extends TileEntityGridDeviceBase<StorageNetworkGrid> implements ITerminal, IANBTPacketReceiver {
	public boolean poweredClient = false;
	private boolean poweredLast = false;
	public int terminalMode = 0;
	public InventoryCraftResult craftResult = new InventoryCraftResult();
	public InventoryCrafting craftingInv = new InventoryCrafting(new Container(){

		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			return true;
		}
		@Override
		public void onCraftMatrixChanged(IInventory inventoryIn) {
			craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftingInv, worldObj));
		};
	}, 3, 3);
	@Override
	public StorageNetworkGrid constructGrid() {
		return new StorageNetworkGrid();
	}
	@Override
	public void updateEntity(IBlockState currentState) {
		if(!worldObj.isRemote){
			grid.drainEnergy(1);
			poweredClient = grid.isPowered();
			if(poweredLast != poweredClient){
				poweredLast = poweredClient;
				markBlockForUpdate(pos);
			}
		}
	}
	@Override
	public void writeToPacket(NBTTagCompound buf) {
		buf.setBoolean("p", poweredClient);
	}
	@Override
	public void readFromPacket(NBTTagCompound buf) {
		poweredClient = buf.getBoolean("p");
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
	}
	@Override
	public void buttonPressed(EntityPlayer player, int id, int extra) {
		if(id == 0)terminalMode = extra % 3;
		else if(id == 1){
			for(int i = 0;i<9;i++){
				craftingInv.setInventorySlotContents(i, grid.pushStack(craftingInv.getStackInSlot(i)));
			}
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("termMode", terminalMode);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < craftingInv.getSizeInventory(); ++i)
		{
			if (craftingInv.getStackInSlot(i) != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				craftingInv.getStackInSlot(i).writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		compound.setTag("crafting", list);
		return compound;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		terminalMode = compound.getInteger("termMode");
		NBTTagList list = compound.getTagList("crafting", 10);
		craftingInv.clear();
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < craftingInv.getSizeInventory())
			{
				craftingInv.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
	}
	@Override
	public int getTerminalMode() {
		return terminalMode;
	}
	@Override
	public void receiveNBTPacket(NBTTagCompound message, EntityPlayer player) {
		ItemStack[][] stacks = new ItemStack[9][];
		NBTTagList list = message.getTagList("i", 10);
		for(int i = 0;i<list.tagCount();i++){
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			byte slot = nbttagcompound.getByte("s");
			byte l = nbttagcompound.getByte("l");
			stacks[slot] = new ItemStack[l];
			for(int j = 0;j<l;j++){
				NBTTagCompound tag = nbttagcompound.getCompoundTag("i"+j);
				stacks[slot][j] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		handlerItemTransfer(player, stacks);
	}
	private void handlerItemTransfer(EntityPlayer player, ItemStack[][] items){
		for(int i = 0;i<9;i++){
			if(items[i] != null){
				ItemStack stack = null;
				for(int j = 0;j<items[i].length;j++){
					ItemStack pulled = grid.pullStack(items[i][j]);
					if(pulled != null){
						stack = pulled;
						break;
					}
				}
				if(stack == null){
					for(int j = 0;j<items[i].length;j++){
						boolean br = false;
						for(int k = 0;k<player.inventory.getSizeInventory();k++){
							if(TomsModUtils.areItemStacksEqual(player.inventory.getStackInSlot(k), items[i][j], true, false, false)){
								stack = player.inventory.decrStackSize(k, 1);
								br = true;
								break;
							}
						}
						if(br)break;
					}
				}
				if(stack != null){
					craftingInv.setInventorySlotContents(i, stack);
				}
			}
		}
	}
	public void craft(EntityPlayer player, ContainerBlockCraftingTerminal container, SlotTerminalCrafting slot){
		if(worldObj.isRemote)return;
		int crafted = 0;
		int amount = slot.getStack().stackSize;
		ItemStack[] stacks = new ItemStack[9];
		for(int i = 0;i<9;i++){
			stacks[i] = ItemStack.copyItemStack(craftingInv.getStackInSlot(i));
		}
		while(crafted + amount <= slot.getStack().getMaxStackSize() && slot.getStack() != null){
			ItemStack stack1 = slot.getStack().copy();
			net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack1, craftingInv);
			slot.onCrafting(stack1);
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(player);
			ItemStack[] aitemstack = CraftingManager.getInstance().getRemainingItems(this.craftingInv, worldObj);
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

			for (int i = 0; i < aitemstack.length; ++i)
			{
				Item original = null;
				ItemStack itemstack = this.craftingInv.getStackInSlot(i);
				ItemStack itemstack1 = aitemstack[i];

				if (itemstack != null)
				{
					original = itemstack.getItem();
					this.craftingInv.decrStackSize(i, 1);
					itemstack = this.craftingInv.getStackInSlot(i);
				}

				if (itemstack1 != null)
				{
					if (itemstack == null && original != null && original == itemstack1.getItem())
					{
						this.craftingInv.setInventorySlotContents(i, itemstack1);
					}
					else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1))
					{
						itemstack1.stackSize += itemstack.stackSize;
						this.craftingInv.setInventorySlotContents(i, itemstack1);
					}
					else if (!player.inventory.addItemStackToInventory(itemstack1))
					{
						ItemStack retStack = grid.pushStack(itemstack1);
						if(retStack != null)
							player.dropItem(retStack, false);
					}
				}
			}
			crafted += amount;
			for(int i = 0;i<9;i++){
				if(stacks[i] != null && craftingInv.getStackInSlot(i) == null){
					craftingInv.setInventorySlotContents(i, grid.pullStack(stacks[i]));
				}
			}
			container.tryMergeStacks(stack1);
			if(stack1.stackSize > 0){
				if(player.inventory.getItemStack() == null){
					player.inventory.setItemStack(stack1);
				}else if (ItemStack.areItemsEqual(player.inventory.getItemStack(), stack1) && ItemStack.areItemStackTagsEqual(player.inventory.getItemStack(), stack1)){
					player.inventory.getItemStack().stackSize += stack1.stackSize;
				}else{
					ItemStack retStack = grid.pushStack(stack1);
					if(retStack != null)
						player.dropItem(retStack, false);
				}
				break;
			}
		}
		container.detectAndSendChanges();
	}
	@Override
	public void setClientPowered(boolean powered) {
		poweredClient = powered;
	}
	@Override
	public boolean getClientPowered() {
		return poweredClient;
	}
}
