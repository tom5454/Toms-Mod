package com.tom.factory.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.factory.FactoryInit;
import com.tom.factory.block.BlockBlastFurnace;
import com.tom.recipes.handler.MachineCraftingHandler;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;

public class TileEntityBlastFurnace extends TileEntityTomsMod implements ISidedInventory{
	private ItemStack[] stack = new ItemStack[this.getSizeInventory()];
	private int progress = -1;
	private int burnTime = 0;
	private int totalBurnTime = 0;
	private int maxProgress = 0;
	private static final int[] SLOTS = new int[]{0,1,2};
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
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
		tag.setInteger("burnTime", burnTime);
		tag.setInteger("maxProgress", maxProgress);
		tag.setInteger("totalBurnTime", totalBurnTime);
		return tag;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
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
		this.burnTime = tag.getInteger("burnTime");
		this.maxProgress = tag.getInteger("maxProgress");
		this.totalBurnTime = tag.getInteger("totalBurnTime");
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
		return id == 0 ? progress : id == 1 ? maxProgress : id == 2 ? burnTime : id == 3 ? totalBurnTime : 0;
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0)burnTime = value;
		else if(id == 1)progress = value;
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
		return "blastFurnace";
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
	public boolean canInsertItem(int index, ItemStack is, EnumFacing direction) {
		return index == 0 || (index == 2 && is != null && (is.getItem() == FactoryInit.coalCoke || (is.getItem() == Items.COAL && is.getMetadata() == 1)));
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 1;
	}
	@Override
	public void updateEntity(IBlockState state) {
		if(!worldObj.isRemote){
			if(checkIfMerged(state)){
				if(progress > 0){
					if(burnTime > 0){
						progress--;
						burnTime--;
					}else{
						if(stack[2] != null && (stack[2].getItem() == FactoryInit.coalCoke || (stack[2].getItem() == Items.COAL && stack[2].getMetadata() == 1) || stack[2].getItem() == Item.getItemFromBlock(FactoryInit.blockCoalCoke))){
							totalBurnTime = burnTime = stack[2].getItem() == FactoryInit.coalCoke ? 3200 : (stack[2].getItem() == Item.getItemFromBlock(FactoryInit.blockCoalCoke) ? 28800 : 1600);
							decrStackSize(2, 1);
						}
					}
				}else if(progress == 0){
					ItemStackChecker s = MachineCraftingHandler.getBlastFurnaceOutput(stack[0], null, 0);
					if(s != null){
						if(stack[1] != null){
							if(TomsModUtils.areItemStacksEqual(stack[1], s.getStack(), true, true, false) && stack[1].stackSize + s.getStack().stackSize <= s.getStack().getMaxStackSize() && stack[0].stackSize >= s.getExtra()){
								stack[1].stackSize += s.getStack().stackSize;
								progress = -1;
								maxProgress = 0;
								decrStackSize(0, s.getExtra());
							}
						}else{
							progress = -1;
							maxProgress = 0;
							stack[1] = s.getStack();
							decrStackSize(0, s.getExtra());
						}
					}else{
						progress = -1;
						maxProgress = 0;
					}
				}else{
					ItemStackChecker s = MachineCraftingHandler.getBlastFurnaceOutput(stack[0], null, 0);
					if(s != null){
						if(stack[1] != null){
							if(TomsModUtils.areItemStacksEqual(stack[1], s.getStack(), true, true, false) && stack[1].stackSize + s.getStack().stackSize <= s.getStack().getMaxStackSize() && stack[0].stackSize >= s.getExtra()){
								maxProgress = s.getExtra3();
								progress = maxProgress;
							}
						}else{
							maxProgress = s.getExtra3();
							progress = maxProgress;
						}
					}
					TomsModUtils.setBlockStateWithCondition(worldObj, pos, BlockBlastFurnace.STATE, progress > 0 ? 2 : 1);
				}
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, BlockBlastFurnace.STATE, 0);
			}
		}
	}
	public boolean checkIfMerged(IBlockState state){
		EnumFacing facing = state.getValue(BlockBlastFurnace.FACING);
		BlockPos center = pos.offset(facing, 2);
		boolean isValid = check3x3(center.up(3)) && check3x3(center);
		isValid = isValid && checkMid(center.up()) && checkMid(center.up(2));
		return isValid;
	}
	private boolean check3x3(BlockPos center){
		boolean ret = isWall(center);
		if(ret){
			for(EnumFacing f : EnumFacing.HORIZONTALS){
				ret = ret && isWall(center.offset(f)) && isWall(center.offset(f).offset(f.rotateY()));
				if(!ret)return false;
			}
		}
		return ret;
	}
	private boolean checkMid(BlockPos center){
		boolean ret = worldObj.getBlockState(center).getBlock() == Blocks.LAVA;
		if(ret){
			for(EnumFacing f : EnumFacing.HORIZONTALS){
				ret = ret && isWall(center.offset(f)) && isWall(center.offset(f).offset(f.rotateY()));
				if(!ret)return false;
			}
		}
		return ret;
	}
	private boolean isWall(BlockPos pos){
		return worldObj.getBlockState(pos).getBlock() == FactoryInit.blastFurnaceWall;
	}

}
