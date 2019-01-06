package com.tom.transport.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.items.IItemHandler;

import com.tom.api.tileentity.TileEntityTomsModNoTicking;
import com.tom.handler.TMPlayerHandler;
import com.tom.lib.api.tileentity.IOwnable;
import com.tom.lib.handler.WorldHandler;
import com.tom.lib.utils.Ticker;
import com.tom.transport.block.ConveyorBeltBase;
import com.tom.util.TomsModUtils;

public abstract class TileEntityConveyorBase extends TileEntityTomsModNoTicking implements ISidedInventory, Ticker, IOwnable {
	protected InventoryBasic inv = new InventoryBasic("", false, 2);
	public static final int MAX_POS = 64;
	protected static final int[] SLOT = new int[]{0}, SIDE = new int[]{1}, FRONT = new int[]{};
	protected int itemPos = 0;
	protected TMPlayerHandler playerHandler;
	public String playerName;
	private long lastTick;
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
		if(!stack.isEmpty()){
			long ctick = world.getTotalWorldTime();
			if(lastTick < (ctick-1)){
				queue();
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return false;
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
	public String getName() {
		return "";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		EnumFacing facing = getFacing();
		return facing.rotateY().getAxis() == side.getAxis() || side.getAxis() == Axis.Y ? SIDE : facing == side ? FRONT : SLOT;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return (index == 0 || index == 1) && inv.getStackInSlot(1).isEmpty() && inv.getStackInSlot(0).isEmpty();
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 0;
	}
	public boolean tryPull(IBlockState currentState){
		return false;
	}
	public void updateConveyor(IBlockState currentState) {
		if (!world.isRemote) {
			boolean sync = false;
			if (!inv.getStackInSlot(0).isEmpty()) {
				if (itemPos < MAX_POS) {
					if(playerHandler.checkAndUseGridPower(getPowerUse())){
						itemPos = Math.min(itemPos + getSpeed(), MAX_POS);
						sync = true;
					}
				} else if (itemPos == MAX_POS) {
					sync = pushItemOut(currentState);
				}
			} else if (!inv.getStackInSlot(1).isEmpty()) {
				itemPos = MAX_POS / 2;
				inv.setInventorySlotContents(0, inv.removeStackFromSlot(1));
				sync = true;
			} else {
				if (itemPos != 0)
					sync = true;
				itemPos = 0;
				if(world.getTotalWorldTime() % 5 == 0)
					sync |= tryPull(currentState);
			}
			if (sync)
				markBlockForUpdate();
		} else {
			if (!inv.getStackInSlot(0).isEmpty()) {
				if (itemPos < MAX_POS) {
					itemPos = Math.min(itemPos + getSpeed(), MAX_POS);
				}
			}
		}
	}
	public boolean pushItemOut(IBlockState currentState){
		boolean sync = false;
		EnumFacing f = getFacing();
		IItemHandler h = TomsModUtils.getItemHandler(world, pos.offset(f), f.getOpposite(), true);
		if (h != null) {
			inv.setInventorySlotContents(0, TomsModUtils.putStackInInventoryAllSlots(h, inv.getStackInSlot(0)));
			if (inv.getStackInSlot(0).isEmpty()) {
				sync = true;
				itemPos = 0;
			}
		} else {
			TileEntity t = world.getTileEntity(pos.offset(f).down());
			if (t instanceof IConveyorSlope) {
				IConveyorSlope c = (IConveyorSlope) t;
				if (c.isValid()) {
					inv.setInventorySlotContents(0, c.insert(inv.getStackInSlot(0), f.getOpposite()));
					if (inv.getStackInSlot(0).isEmpty()) {
						sync = true;
						itemPos = 0;
					}
				}
			}
		}
		return sync;
	}
	public abstract int getPowerUse();
	public abstract int getSpeed();

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		TomsModUtils.writeInventory("inv", compound, inv);
		compound.setInteger("itempos", itemPos);
		compound.setString("placer", playerName);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		TomsModUtils.loadAllItems(compound, "inv", inv);
		itemPos = compound.getInteger("itempos");
		playerName = compound.getString("placer");
	}

	@Override
	public void writeToPacket(NBTTagCompound tag) {
		tag.setInteger("p", itemPos);
		tag.setTag("i", inv.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void readFromPacket(NBTTagCompound tag) {
		itemPos = tag.getInteger("p");
		inv.setInventorySlotContents(0, new ItemStack(tag.getCompoundTag("i")));
	}

	public EnumFacing getFacing() {
		return world.getBlockState(pos).getValue(ConveyorBeltBase.FACING);
	}

	public int getItemPos() {
		return itemPos;
	}

	public ItemStack getStack() {
		return inv.getStackInSlot(0);
	}

	public abstract ResourceLocation getTexture();

	public void randomTick() {
		long ctick = world.getTotalWorldTime();
		if(lastTick < (ctick-2) && isTickerValid()){
			queue();
		}
	}
	private void queue() {
		WorldHandler.addTicker(world.provider.getDimension(), this);
	}

	@Override
	public void onLoad() {
		tileOnLoad();
	}

	@Override
	public void updatePlayerHandler() {
		playerHandler = TMPlayerHandler.getPlayerHandlerForName(playerName);
		if(playerHandler != null)WorldHandler.queueTask(world.provider.getDimension(), this::randomTick);
	}
	@Override
	public final void updateTicker() {
		lastTick = world.getTotalWorldTime();
		if(playerHandler != null){
			IBlockState state = world.getBlockState(pos);
			updateConveyor(state);
		}
	}
	@Override
	public boolean isTickerValid() {
		return !isInvalid() && playerHandler != null && (!inv.getStackInSlot(0).isEmpty() || !inv.getStackInSlot(1).isEmpty());
	}
	@Override
	public String getOwnerName() {
		return playerName;
	}
	@Override
	public void setOwner(String owner) {
		this.playerName = owner;
	}
}
