package com.tom.core.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.core.CoreInit;
import com.tom.lib.api.tileentity.ITMPeripheral.ITMCompatPeripheral;
import com.tom.util.TomsModUtils;

import com.tom.core.block.HolotapeReader;

public class TileEntityHolotapeReader extends TileEntityTomsMod implements ITMCompatPeripheral, IInventory {
	private List<IComputer> computers = new ArrayList<>();
	public InventoryBasic holotape = new InventoryBasic("", false, 1);
	// public boolean isValidH = false;
	public boolean hasH = false;
	public int direction = 0;

	@Override
	public String getType() {
		return "tm_holotape_reader";
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"isValid", "read"};
	}

	@Override
	public Object[] callMethod(IComputer computer, int method, Object[] arguments) throws LuaException {
		if (method == 0) {
			return new Object[]{this.isValidHolotape()};
		} else if (method == 1) {
			if (!holotape.getStackInSlot(0).isEmpty()) {
				if (holotape.getStackInSlot(0).getTagCompound() == null)
					holotape.getStackInSlot(0).setTagCompound(new NBTTagCompound());
				if (holotape.getStackInSlot(0).getTagCompound().hasKey("w") && holotape.getStackInSlot(0).getTagCompound().getBoolean("w") && holotape.getStackInSlot(0).getTagCompound().hasKey("data") && holotape.getStackInSlot(0).getTagCompound().hasKey("name")) {
					String data = holotape.getStackInSlot(0).getTagCompound().getString("data");
					String name = holotape.getStackInSlot(0).getTagCompound().getString("name");
					return new Object[]{name, data};
				} else {
					throw new LuaException("This holotape hasn't written yet!");
				}
			} else {
				throw new LuaException("No Holotape in the machine!");
			}
		}
		return null;
	}

	@Override
	public void attach(IComputer computer) {
		computers.add(computer);
	}

	@Override
	public void detach(IComputer computer) {
		computers.remove(computer);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.holotape.setInventorySlotContents(0, TomsModUtils.loadItemStackFromNBT(tag.getCompoundTag("holotape")));
		this.direction = tag.getInteger("direction");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (this.holotape != null) {
			tag.setTag("holotape", this.holotape.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
		} else {
			tag.setTag("holotape", new NBTTagCompound());
		}
		tag.setInteger("direction", this.direction);
		return tag;
	}

	/*@Override
	public void writeToPacket(ByteBuf buf){
		//buf.writeBoolean(this.holotape != null);
		//buf.writeBoolean(this.isValidHolotape());
		//buf.writeInt(direction);
	}

	@Override
	public void readFromPacket(ByteBuf buf){
		//this.hasH = buf.readBoolean();
		//this.isValidH = buf.readBoolean();
		//this.direction = buf.readInt();
		int xCoord = pos.getX();
		int yCoord = pos.getY();
		int zCoord = pos.getZ();
		this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}*/
	public boolean isValidHolotape() {
		if (!holotape.getStackInSlot(0).isEmpty()) {
			if (holotape.getStackInSlot(0).getTagCompound() == null)
				holotape.getStackInSlot(0).setTagCompound(new NBTTagCompound());
			return (holotape.getStackInSlot(0).getTagCompound().hasKey("w") && holotape.getStackInSlot(0).getTagCompound().getBoolean("w"));
		} else {
			return false;
		}
	}

	@Override
	public void updateEntity() {
		if (!world.isRemote) {
			this.hasH = !this.holotape.getStackInSlot(0).isEmpty();
			IBlockState state = world.getBlockState(pos);
			if (hasH) {
				if (this.isValidHolotape()) {
					TomsModUtils.setBlockStateWithCondition(world, pos, state, HolotapeReader.STATE, 2);
				} else {
					TomsModUtils.setBlockStateWithCondition(world, pos, state, HolotapeReader.STATE, 1);
				}
			} else {
				TomsModUtils.setBlockStateWithCondition(world, pos, state, HolotapeReader.STATE, 0);
			}
		}
	}

	@Override
	public String getName() {
		return "Holotape Writer";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int s, ItemStack is) {
		return s == 0 && is != null && is.getItem() == CoreInit.holotape;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return holotape.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return holotape.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return holotape.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		holotape.setInventorySlotContents(index, stack);
	}

	@Override
	public boolean isEmpty() {
		return holotape.isEmpty();
	}

	@Override
	public void clear() {
		holotape.clear();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

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

}
