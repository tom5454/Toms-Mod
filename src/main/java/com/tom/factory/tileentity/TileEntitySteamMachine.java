package com.tom.factory.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.tom.api.ITileFluidHandler;
import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.core.CoreInit;
import com.tom.factory.block.SteamAlloySmelter;
import com.tom.recipes.handler.MachineCraftingHandler.ItemStackChecker;
import com.tom.util.TomsModUtils;

public abstract class TileEntitySteamMachine extends TileEntityTomsMod implements ITileFluidHandler, ISidedInventory {
	protected FluidTank tank = new FluidTank(2000);
	protected InventoryBasic inv = new InventoryBasic("", false, getSizeInventory());
	protected ItemStackChecker out;
	protected int progress = -1;
	public boolean clientCanRun;

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return TomsModUtils.isUseable(player, this);
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
		if (id == 0)
			progress = value;
		// else if(id == 1)maxProgress = value;
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public IFluidHandler getTankOnSide(EnumFacing f) {
		return Helper.getFluidHandlerFromTank(tank, true, false, CoreInit.steam);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
		tag.setTag("inventory", TomsModUtils.saveAllItems(inv));
		tag.setInteger("progress", progress);
		if (out != null)
			tag.setTag("out", out.writeToNew());
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag.getCompoundTag("tank"));
		TomsModUtils.loadAllItems(tag.getTagList("inventory", 10), inv);
		this.progress = tag.getInteger("progress");
		out = ItemStackChecker.load(tag.getCompoundTag("out"));
	}

	public abstract int getSteamUsage();

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			if (canRun()) {
				update0();
				if (progress > 0) {
					if (process()) {
						tank.drainInternal(getSteamUsage(), true);
						progress--;
					}
				} else if (progress == 0) {
					finish();
				} else {
					checkItems();
					TomsModUtils.setBlockStateWithCondition(world, pos, SteamAlloySmelter.ACTIVE, progress > 0);
				}
			} else {
				TomsModUtils.setBlockStateWithCondition(world, pos, SteamAlloySmelter.ACTIVE, false);
			}
		}
	}

	protected boolean process() {
		return true;
	}

	protected void update0() {
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public abstract void checkItems();

	public abstract void finish();

	public void setOut(ItemStackChecker out) {
		this.out = out;
	}

	public void addItemsAndSetProgress(int outputSlot) {
		if (out != null) {
			if (!inv.getStackInSlot(outputSlot).isEmpty()) {
				if (TomsModUtils.areItemStacksEqual(inv.getStackInSlot(outputSlot), out.getStack(), true, true, false) && inv.getStackInSlot(outputSlot).getCount() + out.getStack().getCount() <= out.getStack().getMaxStackSize()) {
					inv.getStackInSlot(outputSlot).grow(out.getStack().getCount());
					progress = -1;
				}
			} else {
				progress = -1;
				inv.setInventorySlotContents(outputSlot, out.getStack());
			}
		} else {
			progress = -1;
		}
	}

	public void checkItems(ItemStackChecker s, int outputSlot, int MAX_PROCESS_TIME, int input1, int input2) {
		if (s != null) {
			if (!inv.getStackInSlot(outputSlot).isEmpty()) {
				if (TomsModUtils.areItemStacksEqual(inv.getStackInSlot(outputSlot), s.getStack(), true, true, false) && inv.getStackInSlot(outputSlot).getCount() + s.getStack().getCount() <= s.getStack().getMaxStackSize() && inv.getStackInSlot(0).getCount() >= s.getExtra()) {
					progress = MAX_PROCESS_TIME;
					decrStackSize(input1, s.getExtra());
					if (input2 >= 0)
						decrStackSize(input2, s.getExtra2());
					out = s;
				}
			} else {
				progress = MAX_PROCESS_TIME;
				decrStackSize(input1, s.getExtra());
				if (input2 >= 0)
					decrStackSize(input2, s.getExtra2());
				out = s;
			}
		}
	}
	public boolean canRun(){
		return tank.getFluidAmount() > 1200;
	}
	public void setClCanRun(boolean clientCanRun) {
		this.clientCanRun = clientCanRun;
	}
}
