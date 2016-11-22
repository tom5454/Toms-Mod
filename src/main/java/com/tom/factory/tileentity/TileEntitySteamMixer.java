package com.tom.factory.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.apis.TomsModUtils;
import com.tom.core.CoreInit;
import com.tom.core.TMResource;
import com.tom.core.TMResource.Type;
import com.tom.factory.block.SteamMixer;

public class TileEntitySteamMixer extends TileEntityTomsMod implements ITileFluidHandler, ISidedInventory {
	private FluidTank tank = new FluidTank(2000);
	private FluidTank tankIn = new FluidTank(10000);
	private FluidTank tankOut = new FluidTank(10000);
	private ItemStack[] stack = new ItemStack[this.getSizeInventory()];
	private int progress = -1;
	public int clientProgress = 0;
	private static final int[] SLOTS = new int[]{0, 1, 2, 3};
	public static final int MAX_PROCESS_TIME = 150;
	protected static final Object[][] RECIPES = new Object[][]{
		{TomsModUtils.createRecipe(new Object[]{
				new ItemStack(Items.GLOWSTONE_DUST, 2), "dyeWhite", Items.REDSTONE, Items.SUGAR}),
			new Object[][]{{new FluidStack(FluidRegistry.WATER, 1000), true, 0}, {new FluidStack(CoreInit.photoactiveLiquid, 200), false, 1}},
			false,
		},
		{TomsModUtils.createRecipe(new Object[]{
				new ItemStack(Items.GUNPOWDER, 2), new ItemStack(Items.ROTTEN_FLESH, 8), new Object[]{"dyeWhite", 2}, TMResource.SULFUR.getStackName(Type.DUST)}),
			new Object[][]{{new FluidStack(FluidRegistry.WATER, 1000), true, 0}, {new FluidStack(CoreInit.sulfuricAcid, 500), false, 1}},
			false,
		}
	};
	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return Helper.getFluidHandlerFromTanksWithPredicate(new FluidTank[]{tank, tankIn, tankOut}, new Object[]{CoreInit.steam, FluidRegistry.WATER, null}, new boolean[]{true, true, false}, new boolean[]{false, false, true});
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
		tag.setTag("tankIn", tankIn.writeToNBT(new NBTTagCompound()));
		tag.setTag("tankOut", tankOut.writeToNBT(new NBTTagCompound()));
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
		tankIn.readFromNBT(tag.getCompoundTag("tankIn"));
		tankOut.readFromNBT(tag.getCompoundTag("tankOut"));
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
		return 4;
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
		return "steamMixer";
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
		return true;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			if(tank.getFluidAmount() > 1200){
				if(progress > 0){
					tank.drainInternal(8, true);
					progress--;
				}else if(progress == 0){
					findRecipe(true);
					progress = -1;
				}else{
					if(findRecipe(false) > -1){
						progress = MAX_PROCESS_TIME;
					}
					TomsModUtils.setBlockStateWithCondition(worldObj, pos, SteamMixer.ACTIVE, progress > 0);
				}
			}else{
				TomsModUtils.setBlockStateWithCondition(worldObj, pos, SteamMixer.ACTIVE, false);
			}
		}
	}
	@SuppressWarnings("unchecked")
	private int findRecipe(boolean apply) {
		Object[] obj = TomsModUtils.checkAndConsumeMatch(RECIPES, this, new Object[]{tankIn, tankOut});
		if((Integer)obj[0] > -1){
			if(apply)TomsModUtils.runAll((List<Runnable>) obj[1]);
			return (Integer) obj[0];
		}
		return -1;
	}
	public FluidTank getTankIn() {
		return tankIn;
	}
	public FluidTank getTankOut() {
		return tankOut;
	}
}
