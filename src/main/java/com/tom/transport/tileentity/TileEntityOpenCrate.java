package com.tom.transport.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import com.tom.api.tileentity.TileEntityTomsMod;
import com.tom.transport.TransportInit;
import com.tom.transport.block.BlockOpenCrate;

public class TileEntityOpenCrate extends TileEntityTomsMod implements IInventory {
	private List<EntityItem> items = new ArrayList<>();
	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return items.size() + 1;
	}

	@Override
	public boolean isEmpty() {
		return items.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if(items.size() > index){
			EntityItem ent = items.get(index);
			if(ent.isEntityAlive())return ent.getItem();
			else return ItemStack.EMPTY;
		}else return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(items.size() > index){
			EntityItem ent = items.get(index);
			if(ent.isEntityAlive()){
				ItemStack s = ent.getItem().splitStack(count);
				if(ent.getItem().isEmpty())ent.setDead();
				return s;
			}
			else return ItemStack.EMPTY;
		}else return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(items.size() > index){
			EntityItem ent = items.get(index);
			if(ent.isEntityAlive()){
				ItemStack s = ent.getItem();
				ent.setDead();
				return s;
			}
			else return ItemStack.EMPTY;
		}else return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		IBlockState state = world.getBlockState(pos);
		EnumFacing f = EnumFacing.UP;
		if(state.getBlock() == TransportInit.openCrate)f = state.getValue(BlockOpenCrate.FACING);
		BlockPos p = pos.offset(f);
		EntityItem entityitem = new EntityItem(world, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, stack);
		entityitem.setDefaultPickupDelay();
		entityitem.motionX = 0;
		entityitem.motionY = 0;
		entityitem.motionZ = 0;
		world.spawnEntity(entityitem);
		items.add(entityitem);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index == items.size();
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
	}
	@Override
	public void updateEntity(IBlockState currentState) {
		if(world.getTotalWorldTime() % 5 == 0){
			IBlockState state = world.getBlockState(pos);
			EnumFacing f = state.getValue(BlockOpenCrate.FACING);
			BlockPos p = pos.offset(f);
			items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(p));
		}
	}
}
