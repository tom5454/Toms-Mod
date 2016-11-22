package com.tom.factory.tileentity;

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
import net.minecraftforge.oredict.OreDictionary;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.core.research.ResearchHandler;
import com.tom.factory.block.SteamSolderingStation;
import com.tom.recipes.handler.AdvancedCraftingHandler;
import com.tom.recipes.handler.AdvancedCraftingHandler.CraftingLevel;
import com.tom.recipes.handler.AdvancedCraftingHandler.ReturnData;

import com.tom.core.tileentity.TileEntityResearchTable;

public class TileEntitySteamSolderingStation extends TileEntityTomsMod implements ITileFluidHandler, ISidedInventory {
	private FluidTank tank = new FluidTank(2000);
	private ItemStack[] stack = new ItemStack[this.getSizeInventory()];
	private int progress = -1;
	private static final int[] SLOTS = new int[]{9, 11};
	public int maxProgress = 0;
	private ItemStack output = null;
	public int craftingError = 0;
	private int craftingErrorShowTimer = 0;
	private int solderingAlloyLevel = 0;
	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return Helper.getFluidHandlerFromTank(tank, true, false, CoreInit.steam);
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
		tag.setInteger("maxProgress", maxProgress);
		NBTTagCompound tagC = new NBTTagCompound();
		tagC.setTag("out", this.output != null ? this.output.writeToNBT(new NBTTagCompound()) : new NBTTagCompound());
		tag.setTag("crafting", tagC);
		tag.setInteger("solderingAlloy", solderingAlloyLevel);
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
		this.maxProgress = tag.getInteger("maxProgress");
		NBTTagCompound tagC = tag.getCompoundTag("crafting");
		this.output = ItemStack.loadItemStackFromNBT(tagC.getCompoundTag("out"));
		this.solderingAlloyLevel = tag.getInteger("solderingAlloy");
	}
	@Override
	public int getSizeInventory() {
		return 12;
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
		return id == 0 ? progress : id == 1 ? maxProgress : id == 2 ? solderingAlloyLevel : 0;
	}

	@Override
	public void setField(int id, int value) {
		if(id == 0)progress = value;
		else if(id == 1)maxProgress = value;
		else if(id == 2)solderingAlloyLevel = value;
		//else if(id == 1)maxProgress = value;
	}

	@Override
	public int getFieldCount() {
		return 2;
	}

	@Override
	public void clear() {
		stack = new ItemStack[this.getSizeInventory()];
	}

	@Override
	public String getName() {
		return "steamSolderingStation";
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
		return index == 11;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 9;
	}
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			if(this.craftingErrorShowTimer > 0)if(this.craftingErrorShowTimer-- == 1 && (craftingError == 2 || craftingError == 3))craftingError = 0;
			if(tank.getFluidAmount() > 1200){
				if(solderingAlloyLevel > 0){
					if(progress > 0){
						tank.drainInternal(20, true);
						progress--;
						solderingAlloyLevel--;
					}else if(progress == 0){
						this.craftingError = 0;
						if(output != null){
							if(stack[9] == null){
								stack[9] = output.copy();
								output = null;
							}else{
								if(stack[9].isItemEqual(this.output) && ItemStack.areItemStackTagsEqual(output, stack[9]) && stack[9].stackSize + this.output.stackSize <= Math.min(getInventoryStackLimit(), this.output.getMaxStackSize())){
									stack[9].stackSize += this.output.stackSize;
									output = null;
								}else{
									this.craftingError = 1;
								}
							}
						}
						if(output == null){
							maxProgress = 0;
							progress = -1;
						}
					}else{
						if(this.progress < 1 && this.hasItemsInCrafting() && stack[10] != null){
							ResearchHandler h = TileEntityResearchTable.getResearchHandler(stack[10]);
							if(h != null){
								ReturnData data = AdvancedCraftingHandler.craft(new ItemStack[]{stack[0],stack[1],
										stack[2],stack[3],stack[4],stack[5],stack[6],stack[7],stack[8]},
										h.getResearchesCompleted(), CraftingLevel.SOLDERING_STATION, worldObj);
								if(data != null){
									if(data.hasAllResearches()){
										if(data.isRightLevel()){
											this.output = data.getReturnStack();
											this.progress = data.getTime();
											this.maxProgress = data.getTime();
											this.craftStart();
										}else{
											craftingError = 3;
											craftingErrorShowTimer = 50;
										}
									}else{
										craftingError = 2;
										craftingErrorShowTimer = 50;
									}
								}
							}
						}
						TomsModUtils.setBlockStateWithCondition(worldObj, pos, SteamSolderingStation.ACTIVE, progress > 0);
					}
				}
				if(solderingAlloyLevel == 0){
					if(stack[11] != null){
						int[] ids = OreDictionary.getOreIDs(stack[11]);
						int id = OreDictionary.getOreID("ingotSolderingAlloy");
						boolean f = false;
						for(int i : ids){
							if(i == id){
								f = true;
								break;
							}
						}
						if(f){
							decrStackSize(11, 1);
							solderingAlloyLevel += 1000;
							tank.drainInternal(50, true);
						}
					}
				}
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, SteamSolderingStation.ACTIVE, false);
			}
		}
	}
	public boolean hasItemsInCrafting(){
		return stack[0] != null || stack[1] != null || stack[2] != null || stack[3] != null ||
				stack[4] != null || stack[5] != null || stack[6] != null || stack[7] != null ||
				stack[8] != null;
	}
	private void craftStart() {
		for(int i = 0;i<9;i++){
			this.decrStackSize(i, 1);
		}
	}
}