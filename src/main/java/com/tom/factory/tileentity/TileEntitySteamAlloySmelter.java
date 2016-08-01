package com.tom.factory.tileentity;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.factory.block.SteamAlloySmelter;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntitySteamAlloySmelter extends TileEntityTomsMod implements ITileFluidHandler, ISidedInventory {
	private FluidTank tank = new FluidTank(2000);
	private ItemStack[] stack = new ItemStack[this.getSizeInventory()];
	private int progress = -1;
	private static final int[] SLOTS = new int[]{0,1,2};
	public static final int MAX_PROCESS_TIME = 400;
	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return tank;
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
		NBTTagList list = new NBTTagList();
		for(int i = 0;i<stack.length;i++){
			if(stack[i] != null){
				NBTTagCompound t = new NBTTagCompound();
				stack[i].writeToNBT(t);
				t.setByte("Slot", (byte) i);
				list.appendTag(t);
			}
		}
		tag.setTag("inventory", list);
		tag.setInteger("progress", progress);
		return tag;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag.getCompoundTag("tank"));
		stack = new ItemStack[this.getSizeInventory()];
		NBTTagList list = tag.getTagList("inventory", 10);
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < this.stack.length)
			{
				this.stack[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
		this.progress = tag.getInteger("progress");
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
	public boolean isUseableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUseable(pos, player, worldObj, this);
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return id == 0 ? progress : 0;
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0)progress = value;
		//else if(id == 1)maxProgress = value;
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public void clear() {
		stack = new ItemStack[this.getSizeInventory()];
	}

	@Override
	public String getName() {
		return "steamAlloySmelter";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return SLOTS;
	}
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == 0 || index == 1;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 2;
	}
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			if(tank.getFluidAmount() > 1200){
				if(progress > 0){
					tank.drainInternal(12, true);
					progress--;
				}else if(progress == 0){
					ItemStackChecker s = MachineCraftingHandler.getAlloySmelterOutput(stack[0], stack[1]);
					if(s != null){
						if(stack[2] != null){
							if(TomsModUtils.areItemStacksEqual(stack[2], s.getStack(), true, true, false) && stack[2].stackSize + s.getStack().stackSize <= s.getStack().getMaxStackSize() && stack[0].stackSize >= s.getExtra()){
								stack[2].stackSize += s.getStack().stackSize;
								progress = -1;
								decrStackSize(0, s.getExtra());
								decrStackSize(1, s.getExtra2());
							}
						}else{
							progress = -1;
							stack[2] = s.getStack();
							decrStackSize(0, s.getExtra());
							decrStackSize(1, s.getExtra2());
						}
					}else{
						progress = -1;
					}
				}else{
					ItemStackChecker s = MachineCraftingHandler.getAlloySmelterOutput(stack[0], stack[1]);
					if(s != null){
						if(stack[2] != null){
							if(TomsModUtils.areItemStacksEqual(stack[2], s.getStack(), true, true, false) && stack[2].stackSize + s.getStack().stackSize <= s.getStack().getMaxStackSize() && stack[0].stackSize >= s.getExtra()){
								progress = MAX_PROCESS_TIME;
							}
						}else{
							progress = MAX_PROCESS_TIME;
						}
					}
					TomsModUtils.setBlockStateWithCondition(worldObj, pos, SteamAlloySmelter.ACTIVE, progress > 0);
				}
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, SteamAlloySmelter.ACTIVE, false);
			}
		}
	}
}